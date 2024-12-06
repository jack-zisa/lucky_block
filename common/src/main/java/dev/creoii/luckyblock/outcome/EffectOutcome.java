package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.vec.VecProvider;
import dev.creoii.luckyblock.util.shape.Shape;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.math.intprovider.IntProvider;

import java.util.List;
import java.util.Optional;

public class EffectOutcome extends Outcome<EffectOutcome.EffectInfo> {
    public static final MapCodec<EffectOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalWeightField(Outcome::getWeightProvider),
                createGlobalDelayField(Outcome::getDelay),
                createGlobalPosField(Outcome::getPos),
                StatusEffectInstance.CODEC.fieldOf("status_effect").forGetter(outcome -> outcome.statusEffectInstance),
                Shape.CODEC.optionalFieldOf("shape").forGetter(outcome -> outcome.shape),
                Codec.BOOL.fieldOf("exclude_player").orElse(false).forGetter(outcome -> outcome.excludePlayer)
        ).apply(instance, EffectOutcome::new);
    });
    private final StatusEffectInstance statusEffectInstance;
    private final Optional<Shape> shape;
    private final boolean excludePlayer;

    public EffectOutcome(int luck, float chance, IntProvider weightProvider, int delay, Optional<VecProvider> pos, StatusEffectInstance statusEffectInstance, Optional<Shape> shape, boolean excludePlayer) {
        super(OutcomeType.EFFECT, luck, chance, weightProvider, delay, pos, false);
        this.statusEffectInstance = statusEffectInstance;
        this.shape = shape;
        this.excludePlayer = excludePlayer;
    }

    @Override
    public void run(Context<EffectInfo> context) {
        if (shape.isPresent()) {
            shape.get().getEntitiesWithin(context, getPos(context).getVec(context), entity -> {
                if (entity instanceof LivingEntity living) {
                    return !excludePlayer || living != context.player();
                }
                return false;
            }).forEach(entity -> {
                context.info().entities.add((LivingEntity) entity);
                ((LivingEntity) entity).addStatusEffect(statusEffectInstance);
            });
        } else if (!excludePlayer) {
            context.info().entities.add(context.player());
            context.player().addStatusEffect(statusEffectInstance);
        }
    }

    public record EffectInfo(List<LivingEntity> entities) implements ContextInfo {}
}
