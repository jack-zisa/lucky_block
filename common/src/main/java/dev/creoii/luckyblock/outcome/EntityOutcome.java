package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.ContextualProvider;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import dev.creoii.luckyblock.util.nbt.ContextualNbtCompound;
import dev.creoii.luckyblock.util.provider.booleanprovider.BooleanProvider;
import dev.creoii.luckyblock.util.provider.booleanprovider.FalseBooleanProvider;
import dev.creoii.luckyblock.util.vec.VecProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.IntProvider;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class EntityOutcome extends Outcome {
    public static final MapCodec<EntityOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalWeightField(Outcome::getWeightProvider),
                createGlobalDelayField(outcome -> outcome.delay),
                createGlobalPosField(Outcome::getPos),
                createGlobalReinitField(Outcome::shouldReinit),
                EntityType.CODEC.fieldOf("entity_type").forGetter(outcome -> outcome.entityType),
                ContextualNbtCompound.CODEC.optionalFieldOf("nbt").forGetter(outcome -> outcome.nbt),
                EntityType.CODEC.listOf().fieldOf("passengers").orElse(List.of()).forGetter(outcome -> outcome.passengers),
                IntProvider.POSITIVE_CODEC.fieldOf("count").orElse(LuckyBlockCodecs.ONE).forGetter(outcome -> outcome.count),
                BooleanProvider.TYPE_CODEC.fieldOf("initialize_mobs").orElse(FalseBooleanProvider.FALSE).forGetter(outcome -> outcome.initializeMobs)
        ).apply(instance, EntityOutcome::new);
    });
    private final EntityType<?> entityType;
    private final Optional<ContextualNbtCompound> nbt;
    private final List<EntityType<?>> passengers;
    private final IntProvider count;
    private final BooleanProvider initializeMobs;

    public EntityOutcome(int luck, float chance, IntProvider weightProvider, IntProvider delay, Optional<VecProvider> pos, boolean reinit, EntityType<?> entityType, Optional<ContextualNbtCompound> nbt, List<EntityType<?>> passengers, IntProvider count, BooleanProvider initializeMobs) {
        super(OutcomeType.ENTITY, luck, chance, weightProvider, delay, pos, reinit);
        this.entityType = entityType;
        this.nbt = nbt;
        this.passengers = passengers;
        this.count = count;
        this.initializeMobs = initializeMobs;
    }

    @Override
    public void run(Context context) {
        Vec3d spawnPos = Vec3d.ofBottomCenter(getPos(context).getPos(context));
        IntProvider count = ContextualProvider.applyIntContext(this.count, context);
        for (int i = 0; i < count.get(context.world().getRandom()); ++i) {
            spawnEntity(entityType, context, spawnPos, nbt.orElse(null));

            if (shouldReinit()) {
                spawnPos = getPos(context).getVec(context);
            }
        }
    }

    private Entity spawnEntity(EntityType<?> entityType, Context context, Vec3d spawnPos, @Nullable ContextualNbtCompound nbtCompound) {
        Entity entity = entityType.create(context.world(), SpawnReason.NATURAL);
        if (entity != null) {
            if (entity instanceof MobEntity mob && context.world() instanceof ServerWorld serverWorld && ContextualProvider.applyBooleanContext(initializeMobs, context).get(context.world().random)) {
                mob.initialize(serverWorld, context.world().getLocalDifficulty(BlockPos.ofFloored(spawnPos.x, spawnPos.y, spawnPos.z)), SpawnReason.NATURAL, null);
            }
            entity.refreshPositionAndAngles(spawnPos.x, spawnPos.y, spawnPos.z, context.world().getRandom().nextFloat() * 360f, 0f);

            Entity toRide = entity;
            for (EntityType<?> passengerType : passengers) {
                Entity passenger = spawnEntity(passengerType, context, spawnPos, nbtCompound);
                passenger.startRiding(toRide);
                toRide = passenger;
            }

            context.world().spawnEntity(entity);
        }
        return entity;
    }
}
