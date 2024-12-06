package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import dev.creoii.luckyblock.util.nbt.ContextualNbtCompound;
import dev.creoii.luckyblock.util.vec.VecProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.IntProvider;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class EntityOutcome extends Outcome<EntityOutcome.EntityInfo> {
    public static final MapCodec<EntityOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalWeightField(Outcome::getWeightProvider),
                createGlobalDelayField(Outcome::getDelay),
                createGlobalPosField(Outcome::getPos),
                createGlobalReinitField(Outcome::shouldReinit),
                Identifier.CODEC.fieldOf("entity_type").forGetter(outcome -> outcome.entityTypeId),
                IntProvider.POSITIVE_CODEC.fieldOf("count").orElse(LuckyBlockCodecs.ONE).forGetter(outcome -> outcome.count),
                ContextualNbtCompound.CODEC.optionalFieldOf("nbt").forGetter(outcome -> outcome.nbt)
        ).apply(instance, EntityOutcome::new);
    });
    private final Identifier entityTypeId;
    private final IntProvider count;
    private final Optional<ContextualNbtCompound> nbt;

    public EntityOutcome(int luck, float chance, IntProvider weightProvider, int delay, Optional<VecProvider> pos, boolean reinit, Identifier entityTypeId, IntProvider count, Optional<ContextualNbtCompound> nbt) {
        super(OutcomeType.ENTITY, luck, chance, weightProvider, delay, pos, reinit);
        this.entityTypeId = entityTypeId;
        this.count = count;
        this.nbt = nbt;
    }

    @Override
    public void run(Context<EntityInfo> context) {
        Vec3d spawnPos = getPos(context).getVec(context);
        context.info().spawnPositions.add(spawnPos);
        EntityType<?> entityType = Registries.ENTITY_TYPE.get(entityTypeId);
        for (int i = 0; i < count.get(context.world().getRandom()); ++i) {
            spawnEntity(entityType, context, spawnPos, nbt.orElse(null));

            if (shouldReinit()) {
                context.info().spawnPositions.add(spawnPos = getPos(context).getVec(context));
            }
        }
    }

    private Entity spawnEntity(EntityType<?> entityType, Context<EntityInfo> context, Vec3d spawnPos, @Nullable ContextualNbtCompound nbtCompound) {
        Entity entity = entityType.create(context.world(), SpawnReason.NATURAL);
        if (entity != null) {
            if (nbtCompound != null) {
                nbtCompound.setContext(context);

                if (nbtCompound.contains("nbt", 10)) {
                    ContextualNbtCompound nbt = nbtCompound.getCompound("nbt");
                    entity.readNbt(nbt);

                    if (nbt.contains(Entity.PASSENGERS_KEY, 9)) {
                        ContextualNbtCompound passengerCompound = nbt.getList(Entity.PASSENGERS_KEY, 10).getCompound(0);
                        EntityType<?> passengerType = Registries.ENTITY_TYPE.get(Identifier.tryParse(passengerCompound.getString("id")));
                        Entity passenger = spawnEntity(passengerType, context, spawnPos, passengerCompound);
                        if (passenger != null)
                            passenger.startRiding(entity);
                    }
                } else if (nbtCompound.contains(Entity.PASSENGERS_KEY, 9)) {
                    entity.readNbt(nbtCompound);

                    ContextualNbtCompound passengerCompound = nbtCompound.getList(Entity.PASSENGERS_KEY, 10).getCompound(0);
                    EntityType<?> passengerType = Registries.ENTITY_TYPE.get(Identifier.tryParse(passengerCompound.getString("id")));
                    Entity passenger = spawnEntity(passengerType, context, spawnPos, passengerCompound);
                    if (passenger != null)
                        passenger.startRiding(entity);
                } else entity.readNbt(nbtCompound);
            }
            entity.refreshPositionAndAngles(spawnPos.x, spawnPos.y, spawnPos.z, context.world().getRandom().nextFloat() * 360f, 0f);
            context.world().spawnEntity(entity);
        }
        return entity;
    }

    public record EntityInfo(List<Entity> entities, List<Vec3d> spawnPositions) implements ContextInfo {}
}
