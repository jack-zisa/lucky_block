package dev.creoii.luckyblock.outcome;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import dev.creoii.luckyblock.util.function.Function;
import dev.creoii.luckyblock.util.function.FunctionObjectCodecs;
import dev.creoii.luckyblock.util.function.Functions;
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
                FunctionObjectCodecs.ENTITY_WRAPPER.fieldOf("entity").forGetter(outcome -> outcome.entity),
                Functions.CODEC.fieldOf("functions").orElse(Functions.EMPTY).forGetter(outcome -> outcome.functions)
        ).apply(instance, EntityOutcome::new);
    });
    private final EntityWrapper entity;
    private final Functions functions;
    private IntProvider count;

    public EntityOutcome(int luck, float chance, IntProvider weightProvider, int delay, Optional<VecProvider> pos, boolean reinit, EntityWrapper entity, Functions functions) {
        super(OutcomeType.ENTITY, luck, chance, weightProvider, delay, pos, reinit);
        this.entity = entity;
        this.functions = functions;
        count = LuckyBlockCodecs.ONE;
    }

    @Override
    public Context<EntityInfo> create(Context<EntityInfo> context) {
        EntityInfo info = new EntityInfo();
        Function.applyPre(functions, this, context.withInfo(info));

        Vec3d pos = getPos(context).getVec(context);

        List<EntityWrapper> entities = Lists.newArrayList();
        int count = this.count.get(context.world().getRandom());
        for (int i = 0; i < count; ++i) {
            EntityWrapper wrapper = entity.init(context);
            Function.applyPre(wrapper.getFunctions(), this, context);
            entities.add(wrapper);
        }

        info.pos = pos;
        info.entities = entities;

        Function.applyPost(functions, this, context.withInfo(info));
        return context.withInfo(info);
    }

    @Override
    public void run(Context<EntityInfo> context) {
        int count = context.info().entities.size();
        for (int i = 0; i < count; ++i) {
            EntityWrapper entity = context.info().entities.get(i);
            Function.applyPost(entity.getFunctions(), this, context);
            spawnEntity(entity.getEntity(), context, context.info().pos, null);
        }
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
        this.count = count;
        return this;
    }

    @Override
    public EntityOutcome setNbt(Outcome<? extends ContextInfo> outcome, Context<? extends ContextInfo> context, NbtElement nbt) {
        if (nbt instanceof NbtCompound nbtCompound) {
            ContextualNbtCompound contextualNbtCompound = new ContextualNbtCompound().copyFrom(nbtCompound);
            contextualNbtCompound.setContext(context);
            // set nbt
            return new EntityOutcome(getLuck(), getChance(), getWeightProvider(), getDelay(), getPos(), shouldReinit(), entity, functions);
        }
        return this;
    }

    public class EntityInfo implements ContextInfo {
        private Vec3d pos;
        private List<EntityWrapper> entities;

        public EntityInfo(Vec3d pos, List<EntityWrapper> entities) {
            this.pos = pos;
            this.entities = entities;
        }

        public EntityInfo() {
            this(null, List.of());
        }

        @Override
        public List<Object> getTargets() {
            List<Object> targets = Lists.newArrayList(EntityOutcome.this, pos);
            targets.addAll(entities);
            return targets;
        }
    }
}
