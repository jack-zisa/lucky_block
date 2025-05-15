package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.ContextualProvider;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import dev.creoii.luckyblock.util.nbt.ContextualNbtCompound;
import dev.creoii.luckyblock.util.vec.VecProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.IntProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class EntityOutcome extends Outcome {
    public static final MapCodec<EntityOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalWeightField(Outcome::getWeightProvider),
                createGlobalDelayField(outcome -> outcome.delay),
                createGlobalPosField(Outcome::getPos),
                createGlobalReinitField(Outcome::shouldReinit),
                Identifier.CODEC.fieldOf("entity_type").forGetter(outcome -> outcome.entityTypeId),
                IntProvider.POSITIVE_CODEC.fieldOf("count").orElse(LuckyBlockCodecs.ONE).forGetter(outcome -> outcome.count),
                ContextualNbtCompound.CODEC.optionalFieldOf("nbt").forGetter(outcome -> outcome.nbt),
                Codec.BOOL.fieldOf("initialize_mobs").orElse(false).forGetter(outcome -> outcome.initializeMobs)
        ).apply(instance, EntityOutcome::new);
    });
    private final Identifier entityTypeId;
    private final IntProvider count;
    private final Optional<ContextualNbtCompound> nbt;
    private final boolean initializeMobs;

    public EntityOutcome(int luck, float chance, IntProvider weightProvider, IntProvider delay, Optional<VecProvider> pos, boolean reinit, Identifier entityTypeId, IntProvider count, Optional<ContextualNbtCompound> nbt, boolean initializeMobs) {
        super(OutcomeType.ENTITY, luck, chance, weightProvider, delay, pos, reinit);
        this.entityTypeId = entityTypeId;
        this.count = count;
        this.nbt = nbt;
        this.initializeMobs = initializeMobs;
    }

    @Override
    public void run(Context context) {
        Vec3d spawnPos = Vec3d.ofBottomCenter(getPos(context).getPos(context));
        EntityType<?> entityType = Registries.ENTITY_TYPE.get(entityTypeId);
        for (int i = 0; i < ContextualProvider.applyContext(count, context).get(context.world().getRandom()); ++i) {
            spawnEntity(entityType, context, spawnPos, nbt.orElse(null));

            if (shouldReinit()) {
                spawnPos = getPos(context).getVec(context);
            }
        }
    }

    private Entity spawnEntity(EntityType<?> entityType, Context context, Vec3d spawnPos, @Nullable ContextualNbtCompound nbtCompound) {
        Entity entity = entityType.create(context.world());
        if (entity != null) {
            if (nbtCompound != null) {
                nbtCompound.setContext(context);

                if (nbtCompound.contains("nbt", 10)) {
                    ContextualNbtCompound nbt = nbtCompound.getCompound("nbt");
                    readNbt(entity, nbtCompound, context);

                    if (nbt.contains(Entity.PASSENGERS_KEY, 9)) {
                        ContextualNbtCompound passengerCompound = nbt.getList(Entity.PASSENGERS_KEY, 10).getCompound(0);
                        EntityType<?> passengerType = Registries.ENTITY_TYPE.get(Identifier.tryParse(passengerCompound.getString("id")));
                        Entity passenger = spawnEntity(passengerType, context, spawnPos, passengerCompound.contains("nbt") ? passengerCompound.getCompound("nbt") : null);
                        if (passenger != null)
                            passenger.startRiding(entity);
                    }
                } else if (nbtCompound.contains(Entity.PASSENGERS_KEY, 9)) {
                    readNbt(entity, nbtCompound, context);

                    ContextualNbtCompound passengerCompound = nbtCompound.getList(Entity.PASSENGERS_KEY, 10).getCompound(0);
                    EntityType<?> passengerType = Registries.ENTITY_TYPE.get(Identifier.tryParse(passengerCompound.getString("id")));
                    Entity passenger = spawnEntity(passengerType, context, spawnPos, passengerCompound.contains("nbt") ? passengerCompound.getCompound("nbt") : null);
                    if (passenger != null)
                        passenger.startRiding(entity);
                } else readNbt(entity, nbtCompound, context);
            }
            if (entity instanceof MobEntity mob && context.world() instanceof ServerWorld serverWorld && initializeMobs) {
                mob.initialize(serverWorld, context.world().getLocalDifficulty(BlockPos.ofFloored(spawnPos.x, spawnPos.y, spawnPos.z)), SpawnReason.NATURAL, null);
            }
            entity.refreshPositionAndAngles(spawnPos.x, spawnPos.y, spawnPos.z, context.world().getRandom().nextFloat() * 360f, 0f);
            context.world().spawnEntity(entity);
        }
        return entity;
    }

    private void readNbt(Entity entity, ContextualNbtCompound nbtCompound, Context context) {
        entity.readNbt(nbtCompound);

        if (entity instanceof TameableEntity tameable) {
            boolean sitting = nbtCompound.contains("Sitting") && nbtCompound.getBoolean("Sitting");
            tameable.setSitting(sitting);
            tameable.setInSittingPose(sitting);
            tameable.setJumping(false);
            tameable.getNavigation().stop();
            tameable.setTarget(null);
        }

        if (nbtCompound.contains("Owner") && context.player() != null) {
            if (entity instanceof ProjectileEntity projectile) {
                projectile.setOwner(context.player());
            } else if (entity instanceof TameableEntity tameable) {
                tameable.setOwner(context.player());
            }
        }
    }
}
