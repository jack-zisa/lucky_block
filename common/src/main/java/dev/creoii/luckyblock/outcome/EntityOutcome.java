package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.ContextualNbtCompound;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.IntProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class EntityOutcome extends Outcome {
    public static final MapCodec<EntityOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
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

    public EntityOutcome(int luck, float chance, Optional<Integer> delay, Optional<String> pos, boolean reinit, Identifier entityTypeId, IntProvider count, Optional<ContextualNbtCompound> nbt) {
        super(OutcomeType.ENTITY, luck, chance, delay, pos, reinit);
        this.entityTypeId = entityTypeId;
        this.count = count;
        this.nbt = nbt;
    }

    @Override
    public void run(OutcomeContext context) {
        Vec3d spawnPos = getVec(context);
        EntityType<?> entityType = Registries.ENTITY_TYPE.get(entityTypeId);
        for (int i = 0; i < count.get(context.world().getRandom()); ++i) {
            spawnEntity(entityType, context, spawnPos, nbt.orElse(null));

            if (shouldReinit()) {
                spawnPos = getVec(context);
            }
        }
    }

    private Entity spawnEntity(EntityType<?> entityType, OutcomeContext context, Vec3d spawnPos, @Nullable NbtCompound nbtCompound) {
        Entity entity = entityType.create(context.world());
        if (entity != null) {
            if (nbtCompound != null) {
                if (nbtCompound instanceof ContextualNbtCompound contextual) {
                    contextual.setContext(context);
                }

                if (nbtCompound.contains("nbt")) {
                    NbtCompound nbt = nbtCompound.getCompound("nbt");
                    entity.readNbt(nbt);
                    if (nbt.contains(Entity.PASSENGERS_KEY)) {
                        NbtCompound passengerCompound = nbt.getList(Entity.PASSENGERS_KEY, 10).getCompound(0);
                        EntityType<?> passengerType = Registries.ENTITY_TYPE.get(Identifier.tryParse(passengerCompound.getString("id")));
                        Entity passenger = spawnEntity(passengerType, context, spawnPos, passengerCompound);
                        if (passenger != null)
                            passenger.startRiding(entity);
                    }
                } else if (nbtCompound.contains(Entity.PASSENGERS_KEY)) {
                    entity.readNbt(nbtCompound);
                    NbtCompound passengerCompound = nbtCompound.getList(Entity.PASSENGERS_KEY, 10).getCompound(0);
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
}
