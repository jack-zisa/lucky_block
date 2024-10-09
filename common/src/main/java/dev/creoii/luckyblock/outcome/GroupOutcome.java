package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.intprovider.IntProvider;

import java.util.*;

public class GroupOutcome extends Outcome {
    public static final MapCodec<GroupOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalDelayField(Outcome::getDelay),
                Outcome.CODEC.listOf().fieldOf("outcomes").forGetter(outcome -> outcome.outcomes),
                IntProvider.POSITIVE_CODEC.optionalFieldOf("count").forGetter(outcome -> outcome.count)
        ).apply(instance, GroupOutcome::new);
    });
    private final List<Outcome> outcomes;
    private final Optional<IntProvider> count;

    public GroupOutcome(int luck, float chance, Optional<Integer> delay, List<Outcome> outcomes, Optional<IntProvider> count) {
        super(OutcomeType.GROUP, luck, chance, delay, Optional.empty());
        this.outcomes = outcomes instanceof ArrayList<Outcome> ? outcomes : new ArrayList<>(outcomes);
        this.count = count;
    }

    @Override
    public void run(OutcomeContext context) {
        Collections.shuffle(outcomes);
        for (int i = 0; i < (count.isPresent() ? Math.clamp(this.count.get().get(context.world().getRandom()), 0, outcomes.size()) : outcomes.size()); ++i) {
            outcomes.get(i).run(context);
        }
    }
}
