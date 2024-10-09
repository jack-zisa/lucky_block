package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Optional;

public class RandomOutcome extends Outcome {
    public static final MapCodec<RandomOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalDelayField(Outcome::getDelay),
                Outcome.CODEC.listOf().fieldOf("outcomes").forGetter(outcome -> outcome.outcomes)
        ).apply(instance, RandomOutcome::new);
    });
    private final List<Outcome> outcomes;

    public RandomOutcome(Optional<Integer> delay, List<Outcome> outcomes) {
        super(OutcomeType.RANDOM, delay, Optional.empty());
        this.outcomes = outcomes;
    }

    @Override
    public void run(OutcomeContext context) {
        outcomes.get(context.world().getRandom().nextInt(outcomes.size())).run(context);
    }
}
