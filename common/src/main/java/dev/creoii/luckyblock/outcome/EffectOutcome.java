package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.vec.VecProvider;
import dev.creoii.luckyblock.util.shape.Shape;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.intprovider.IntProvider;

import java.util.Optional;

public class EffectOutcome extends Outcome {
    public static final Codec<EffectOutcome> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalWeightField(Outcome::getWeightProvider),
                createGlobalDelayField(Outcome::getDelay),
                createGlobalPosField(Outcome::getPos),
                NbtCompound.CODEC.fieldOf("status_effect").forGetter(outcome -> outcome.statusEffectInstance),
                Shape.CODEC.optionalFieldOf("shape").forGetter(outcome -> outcome.shape),
                Codec.BOOL.fieldOf("exclude_player").orElse(false).forGetter(outcome -> outcome.excludePlayer)
        ).apply(instance, EffectOutcome::new);
    });
    private final NbtCompound statusEffectInstance;
    private final Optional<Shape> shape;
    private final boolean excludePlayer;

    public EffectOutcome(int luck, float chance, IntProvider weightProvider, int delay, Optional<VecProvider> pos, NbtCompound statusEffectInstance, Optional<Shape> shape, boolean excludePlayer) {
        super(OutcomeType.EFFECT, luck, chance, weightProvider, delay, pos, false);
        this.statusEffectInstance = statusEffectInstance;
        this.shape = shape;
        this.excludePlayer = excludePlayer;
    }

    @Override
    public void run(Context context) {
        StatusEffectInstance instance = StatusEffectInstance.fromNbt(statusEffectInstance);
        if (shape.isPresent()) {
            shape.get().getEntitiesWithin(context, getPos(context).getVec(context), entity -> {
                if (entity instanceof LivingEntity living) {
                    return !excludePlayer || living != context.player();
                }
                return false;
            }).forEach(entity -> ((LivingEntity) entity).addStatusEffect(instance));
        } else if (!excludePlayer)
            context.player().addStatusEffect(instance);
    }
}
