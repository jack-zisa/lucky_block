package dev.creoii.luckyblock.function.target;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.util.math.intprovider.IntProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomCountFunctionTarget extends FunctionTarget<Target<?>> {
    public static final MapCodec<RandomCountFunctionTarget> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(FunctionTarget.CODEC.fieldOf("target").forGetter(target -> target.target),
                IntProvider.NON_NEGATIVE_CODEC.fieldOf("count").forGetter(target -> target.count)
        ).apply(instance, RandomCountFunctionTarget::new);
    });
    private final FunctionTarget<?> target;
    private final IntProvider count;

    public RandomCountFunctionTarget(FunctionTarget<?> target, IntProvider count) {
        super(FunctionTargetType.RANDOM_COUNT);
        this.target = target;
        this.count = count;
    }

    @Override
    public List<Target<?>> getTargets(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        List<Target<?>> targets = new ArrayList<>(target.getTargets(outcome, context));
        Collections.shuffle(targets);
        return new ArrayList<>(targets.subList(0, Math.min(count.get(context.random()), targets.size())));
    }
}
