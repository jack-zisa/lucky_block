package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import net.minecraft.util.math.intprovider.IntProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RandomOutcome extends Outcome {
    public static final MapCodec<RandomOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
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
            count = Math.clamp(count, 0, runOutcomes.size() - 1);
        }
        for (int i = 0; i < count; ++i) {
            Outcome outcome = runOutcomes.get(context.world().getRandom().nextInt(runOutcomes.size()));
            outcome.runOutcome(context);
            if (!duplicates) {
                runOutcomes.remove(outcome);
            }
        }
    }
}
