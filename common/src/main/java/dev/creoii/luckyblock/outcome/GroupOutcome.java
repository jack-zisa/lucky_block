package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.intprovider.IntProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GroupOutcome extends Outcome {
    public static final MapCodec<GroupOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalWeightField(Outcome::getWeightProvider),
                createGlobalDelayField(outcome -> outcome.delay),
                Outcome.CODEC.listOf().fieldOf("outcomes").forGetter(outcome -> outcome.outcomes)
        ).apply(instance, GroupOutcome::new);
    });
    private final List<Outcome> outcomes;

    public GroupOutcome(int luck, float chance, IntProvider weightProvider, IntProvider delay, List<Outcome> outcomes) {
        super(OutcomeType.GROUP, luck, chance, weightProvider, delay, Optional.empty(), false);
        this.outcomes = outcomes instanceof ArrayList<Outcome> ? outcomes : new ArrayList<>(outcomes);
    }

    @Override
    public void run(Context context) {
        List<Outcome> runOutcomes = new ArrayList<>(outcomes);
        // sort by chance
        runOutcomes.sort((o1, o2) -> (int) ((o1.getWeightProvider().get(context.world().getRandom()) - o2.getWeightProvider().get(context.world().getRandom())) * 100f));
        for (Outcome outcome : runOutcomes) {
            outcome.runOutcome(context);
        }
    }
}
