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
                createGlobalDelayField(Outcome::getDelay),
                Outcome.EITHER_CODEC.listOf().fieldOf("outcomes").forGetter(outcome -> outcome.outcomes)
        ).apply(instance, GroupOutcome::new);
    });
    private final List<Outcome> outcomes;

    public GroupOutcome(int luck, float chance, IntProvider weightProvider, int delay, List<Outcome> outcomes) {
        super(OutcomeType.GROUP, luck, chance, weightProvider, delay, Optional.empty(), false);
        this.outcomes = outcomes instanceof ArrayList<Outcome> ? outcomes : new ArrayList<>(outcomes);
    }

    @Override
    public void run(Context context) {
        List<Outcome> runOutcomes = new ArrayList<>(outcomes);
        // sort by chance
        runOutcomes.sort((o1, o2) -> (int) ((o1.getWeightProvider().get(context.world().getRandom()) - o2.getWeightProvider().get(context.world().getRandom())) * 100f));
        for (Outcome outcome : runOutcomes) {
            if (outcome instanceof Reference reference) {
                reference.setContext(context);
                Outcome ref = reference.getOutcome();
                System.out.println("found reference: null? " + (ref == null));
                if (ref != null) {
                    System.out.println("running reference outcome: " + reference.getId());
                    ref.runOutcome(context);
                }
            } else outcome.runOutcome(context);
        }
    }
}
