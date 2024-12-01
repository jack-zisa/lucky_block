package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Optional;

public class RandomOutcome extends Outcome {
    public static final MapCodec<RandomOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalDelayField(Outcome::getDelay),
                Outcome.CODEC.listOf().fieldOf("outcomes").forGetter(outcome -> outcome.outcomes)
        ).apply(instance, RandomOutcome::new);
    });
    private final List<Outcome> outcomes;

    public RandomOutcome(int luck, float chance, int delay, List<Outcome> outcomes) {
        super(OutcomeType.RANDOM, luck, chance, delay, Optional.empty(), false);
        this.outcomes = outcomes;
    }

    @Override
    public void run(Context context) {
        outcomes.get(context.world().getRandom().nextInt(outcomes.size())).runOutcome(context);
    }
}
