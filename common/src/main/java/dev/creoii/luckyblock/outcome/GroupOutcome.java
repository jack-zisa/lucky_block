package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GroupOutcome extends Outcome {
    public static final Codec<GroupOutcome> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalDelayField(Outcome::getDelay),
                createGlobalReinitField(Outcome::shouldReinit),
                Outcome.CODEC.listOf().fieldOf("outcomes").forGetter(outcome -> outcome.outcomes)
        ).apply(instance, GroupOutcome::new);
    });
    private final List<Outcome> outcomes;

    public GroupOutcome(int luck, float chance, Optional<Integer> delay, boolean reinit, List<Outcome> outcomes) {
        super(OutcomeType.GROUP, luck, chance, delay, Optional.empty(), reinit);
        this.outcomes = outcomes instanceof ArrayList<Outcome> ? outcomes : new ArrayList<>(outcomes);
    }

    @Override
    public void run(Context context) {
        for (Outcome outcome : outcomes) {
            outcome.runOutcome(context);
        }
    }
}
