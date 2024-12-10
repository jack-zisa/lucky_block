package dev.creoii.luckyblock.outcome;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import dev.creoii.luckyblock.util.function.Function;
import dev.creoii.luckyblock.util.function.target.CountTarget;
import dev.creoii.luckyblock.util.function.target.NbtTarget;
import dev.creoii.luckyblock.util.function.target.Target;
import dev.creoii.luckyblock.util.function.wrapper.EntityWrapper;
import dev.creoii.luckyblock.util.nbt.ContextualNbtCompound;
import dev.creoii.luckyblock.util.vec.VecProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.IntProvider;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class EntityOutcome extends Outcome<EntityOutcome.EntityInfo> implements CountTarget<EntityOutcome>, NbtTarget<EntityOutcome> {
    public static final MapCodec<EntityOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalWeightField(Outcome::getWeightProvider),
                createGlobalDelayField(Outcome::getDelay),
                createGlobalPosField(Outcome::getPos),
                createGlobalReinitField(Outcome::shouldReinit),
                Identifier.CODEC.fieldOf("entity_type").forGetter(outcome -> outcome.entityTypeId),
                IntProvider.POSITIVE_CODEC.fieldOf("count").orElse(LuckyBlockCodecs.ONE).forGetter(outcome -> outcome.count),
                ContextualNbtCompound.CODEC.optionalFieldOf("nbt").forGetter(outcome -> outcome.nbt),
                Function.CODEC.listOf().fieldOf("functions").orElse(List.of()).forGetter(outcome -> outcome.functions)
        ).apply(instance, EntityOutcome::new);
    });
    private final Identifier entityTypeId;
    private final IntProvider count;
    private final Optional<ContextualNbtCompound> nbt;
    private final List<Function<?>> functions;

    public EntityOutcome(int luck, float chance, IntProvider weightProvider, int delay, Optional<VecProvider> pos, boolean reinit, Identifier entityTypeId, IntProvider count, Optional<ContextualNbtCompound> nbt, List<Function<?>> functions) {
        super(OutcomeType.ENTITY, luck, chance, weightProvider, delay, pos, reinit);
        this.entityTypeId = entityTypeId;
        this.count = count;
        this.nbt = nbt;
        this.functions = functions;
    }

    @Override
    public Context<EntityInfo> create(Context<EntityInfo> context) {
        Vec3d pos = getPos(context).getVec(context);
        EntityType<?> entityType = Registries.ENTITY_TYPE.get(entityTypeId);

        List<EntityWrapper> entities = Lists.newArrayList();
        for (int i = 0; i < count.get(context.world().getRandom()); ++i) {
            Entity entity = entityType.create(context.world(), SpawnReason.NATURAL);
            if (entity != null) {
                entities.add(new EntityWrapper(entity, List.of()));
            }
        }

        Context<EntityInfo> newContext = context.withInfo(new EntityInfo(pos, entities));
        functions.forEach(function -> function.apply(this, newContext));
        return newContext;
    }

    @Override
    public void run(Context<EntityInfo> context) {
        context.info().entities.forEach(entity -> {
            spawnEntity(entity.entity(), context, context.info().pos, null);
        });
    }

    private Entity spawnEntity(Entity entity, Context<EntityInfo> context, Vec3d spawnPos, @Nullable ContextualNbtCompound nbtCompound) {
        if (nbtCompound != null) {
            nbtCompound.setContext(context);
            if (nbtCompound.contains("nbt", 10)) {
                ContextualNbtCompound nbt = nbtCompound.getCompound("nbt");
                entity.readNbt(nbt);

                if (nbt.contains(Entity.PASSENGERS_KEY, 9)) {
                    ContextualNbtCompound passengerCompound = nbt.getList(Entity.PASSENGERS_KEY, 10).getCompound(0);
                    EntityType<?> passengerType = Registries.ENTITY_TYPE.get(Identifier.tryParse(passengerCompound.getString("id")));
                    Entity passenger = spawnEntity(passengerType.create(context.world(), SpawnReason.NATURAL), context, spawnPos, passengerCompound);
                    if (passenger != null)
                        passenger.startRiding(entity);
                }
            } else if (nbtCompound.contains(Entity.PASSENGERS_KEY, 9)) {
                entity.readNbt(nbtCompound);

                ContextualNbtCompound passengerCompound = nbtCompound.getList(Entity.PASSENGERS_KEY, 10).getCompound(0);
                EntityType<?> passengerType = Registries.ENTITY_TYPE.get(Identifier.tryParse(passengerCompound.getString("id")));
                Entity passenger = spawnEntity(passengerType.create(context.world(), SpawnReason.NATURAL), context, spawnPos, passengerCompound);
                if (passenger != null)
                    passenger.startRiding(entity);
            } else entity.readNbt(nbtCompound);
        }
        entity.refreshPositionAndAngles(spawnPos.x, spawnPos.y, spawnPos.z, context.world().getRandom().nextFloat() * 360f, 0f);
        context.world().spawnEntity(entity);
        return entity;
    }

    @Override
    public Target<EntityOutcome> update(Function<Target<?>> function, Object newObject) {
        if (newObject instanceof EntityOutcome newEntityOutcome) {
            //functions.remove(function);
            //newItemOutcome.functions.remove(function);
            return newEntityOutcome;
        }
        throw new IllegalArgumentException("Attempted updating entity outcome target with non-entity-outcome value.");
    }

    @Override
    public EntityOutcome setCount(Outcome<? extends ContextInfo> outcome, Context<? extends ContextInfo> context, IntProvider count) {
        return new EntityOutcome(getLuck(), getChance(), getWeightProvider(), getDelay(), getPos(), shouldReinit(), entityTypeId, count, nbt, functions);
    }

    @Override
    public EntityOutcome setNbt(Outcome<? extends ContextInfo> outcome, Context<? extends ContextInfo> context, NbtElement nbt) {
        if (nbt instanceof NbtCompound nbtCompound) {
            ContextualNbtCompound contextualNbtCompound = new ContextualNbtCompound().copyFrom(nbtCompound);
            contextualNbtCompound.setContext(context);
            return new EntityOutcome(getLuck(), getChance(), getWeightProvider(), getDelay(), getPos(), shouldReinit(), entityTypeId, count, Optional.of(contextualNbtCompound), functions);
        }
        return this;
    }

    public static class EntityInfo implements ContextInfo {
        private final Vec3d pos;
        private final List<EntityWrapper> entities;

        public EntityInfo(Vec3d pos, List<EntityWrapper> entities) {
            this.pos = pos;
            this.entities = entities;
        }

        @Override
        public List<Object> getTargets() {
            List<Object> targets = Lists.newArrayList(pos);
            targets.addAll(entities);
            return targets;
        }
    }
}
