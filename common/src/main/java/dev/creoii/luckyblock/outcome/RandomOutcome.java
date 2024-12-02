package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import net.minecraft.util.math.intprovider.IntProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RandomOutcome extends Outcome {
    public static final Codec<RandomOutcome> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalWeightField(Outcome::getWeightProvider),
                createGlobalDelayField(Outcome::getDelay),
                Outcome.CODEC.listOf().fieldOf("outcomes").forGetter(outcome -> outcome.outcomes),
                IntProvider.POSITIVE_CODEC.fieldOf("count").orElse(LuckyBlockCodecs.ONE).forGetter(outcome -> outcome.count),
                Codec.BOOL.fieldOf("duplicates").orElse(false).forGetter(outcome -> outcome.duplicates)
        ).apply(instance, RandomOutcome::new);
    });
    private final List<Outcome> outcomes;
    private final IntProvider count;
    private final boolean duplicates;

    public RandomOutcome(int luck, float chance, IntProvider weightProvider, int delay, List<Outcome> outcomes, IntProvider count, boolean duplicates) {
        super(OutcomeType.RANDOM, luck, chance, weightProvider, delay, Optional.empty(), false);
        this.outcomes = outcomes;
        this.count = count;
        this.duplicates = duplicates;
    }

    @Override
    public void run(Context context) {
        List<Outcome> runOutcomes = new ArrayList<>(outcomes);
        int count = this.count.get(context.world().getRandom());
        if (!duplicates) {
            count = Math.clamp(count, 0, runOutcomes.size());
        }

        for (int i = 0; i < count; ++i) {
            double totalWeight = 0d;
            List<Double> weights = new ArrayList<>();
            weights.add(0d);

            for (Outcome outcome : runOutcomes) {
                double outcomeWeight;
                try {
                    outcomeWeight = outcome.getWeightProvider().get(context.world().getRandom());
                } catch (Exception e) {
                    outcomeWeight = 1d;
                }

                weights.add(totalWeight += (outcomeWeight > 0d ? outcomeWeight : 1d) * 100);
            }

            double rIndex = context.world().getRandom().nextDouble() * totalWeight;
            int index = -1;
            for (int j = 0; j < weights.size() - 1; ++j) {
                if (rIndex >= weights.get(j) && rIndex < weights.get(j + 1)) {
                    index = j;
                }
            }

            if (index == -1) {
                index = weights.size() - 2;
            }

            Outcome selected = runOutcomes.get(index);
            selected.runOutcome(context);

            if (!duplicates) {
                runOutcomes.remove(selected);
            }
        }
    }
}
