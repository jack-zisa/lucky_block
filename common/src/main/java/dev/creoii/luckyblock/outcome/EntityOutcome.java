package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.IntProvider;

import java.util.Optional;

public class EntityOutcome extends Outcome {
    public static final MapCodec<EntityOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalDelayField(Outcome::getDelay),
                createGlobalPosField(Outcome::getPos),
                Identifier.CODEC.fieldOf("entity_type").forGetter(outcome -> outcome.entityTypeId),
                IntProvider.POSITIVE_CODEC.fieldOf("count").orElse(LuckyBlockCodecs.ONE).forGetter(outcome -> outcome.count),
                StringNbtReader.NBT_COMPOUND_CODEC.optionalFieldOf("nbt").forGetter(outcome -> outcome.nbt)
        ).apply(instance, EntityOutcome::new);
    });
    private final Identifier entityTypeId;
    private final IntProvider count;
    private final Optional<NbtCompound> nbt;

    public EntityOutcome(int luck, float chance, Optional<Integer> delay, Optional<String> pos, Identifier entityTypeId, IntProvider count, Optional<NbtCompound> nbt) {
        super(OutcomeType.ENTITY, luck, chance, delay, pos);
        this.entityTypeId = entityTypeId;
        this.count = count;
        this.nbt = nbt;
    }

    @Override
    public void run(OutcomeContext context) {
        Vec3d spawnPos = getVec(context);
        EntityType<?> entityType = Registries.ENTITY_TYPE.get(entityTypeId);
        for (int i = 0; i < count.get(context.world().getRandom()); ++i) {
            Entity entity = entityType.create(context.world());
            if (entity != null) {
                nbt.ifPresent(entity::readNbt);
                entity.refreshPositionAndAngles(spawnPos.x, spawnPos.y, spawnPos.z, context.world().getRandom().nextFloat() * 360f, 0f);
                context.world().spawnEntity(entity);
            }
        }
    }
}
