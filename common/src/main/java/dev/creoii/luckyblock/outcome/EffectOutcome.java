package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.ContextualProvider;
import dev.creoii.luckyblock.util.provider.booleanprovider.BooleanProvider;
import dev.creoii.luckyblock.util.provider.booleanprovider.FalseBooleanProvider;
import dev.creoii.luckyblock.util.vec.VecProvider;
import dev.creoii.luckyblock.util.shape.Shape;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.math.intprovider.IntProvider;

import java.util.Optional;

public class EffectOutcome extends Outcome {
    public static final MapCodec<EffectOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalWeightField(Outcome::getWeightProvider),
                createGlobalDelayField(outcome -> outcome.delay),
                createGlobalPosField(Outcome::getPos),
                StatusEffectInstance.CODEC.fieldOf("status_effect").forGetter(outcome -> outcome.statusEffectInstance),
                Shape.CODEC.optionalFieldOf("shape").forGetter(outcome -> outcome.shape),
                BooleanProvider.TYPE_CODEC.fieldOf("exclude_player").orElse(FalseBooleanProvider.FALSE).forGetter(outcome -> outcome.excludePlayer)
        ).apply(instance, EffectOutcome::new);
    });
    private final StatusEffectInstance statusEffectInstance;
    private final Optional<Shape> shape;
    private final BooleanProvider excludePlayer;

    public EffectOutcome(int luck, float chance, IntProvider weightProvider, IntProvider delay, Optional<VecProvider> pos, StatusEffectInstance statusEffectInstance, Optional<Shape> shape, BooleanProvider excludePlayer) {
        super(OutcomeType.EFFECT, luck, chance, weightProvider, delay, pos, false);
        this.statusEffectInstance = statusEffectInstance;
        this.shape = shape;
        this.excludePlayer = excludePlayer;
    }

    @Override
    public void run(Context context) {
        boolean excludePlayer = ContextualProvider.applyBooleanContext(this.excludePlayer, context).get(context.world().random);

        if (shape.isPresent()) {
            shape.get().getEntitiesWithin(context, getPos(context).getVec(context), entity -> {
                if (entity instanceof LivingEntity living) {
                    return !excludePlayer || living != context.player();
                }
                return false;
            }).forEach(entity -> ((LivingEntity) entity).addStatusEffect(statusEffectInstance));
        } else if (!excludePlayer) context.player().addStatusEffect(statusEffectInstance);
    }
}
