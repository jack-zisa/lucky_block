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
                createGlobalReinitField(Outcome::shouldReinit),
                Outcome.CODEC.listOf().fieldOf("outcomes").forGetter(outcome -> outcome.outcomes),
                IntProvider.POSITIVE_CODEC.optionalFieldOf("count").forGetter(outcome -> outcome.count)
        ).apply(instance, GroupOutcome::new);
    });
    private final List<Outcome> outcomes;
    private final Optional<IntProvider> count;

    public GroupOutcome(int luck, float chance, Optional<Integer> delay, boolean reinit, List<Outcome> outcomes, Optional<IntProvider> count) {
        super(OutcomeType.GROUP, luck, chance, delay, Optional.empty(), reinit);
        this.outcomes = outcomes instanceof ArrayList<Outcome> ? outcomes : new ArrayList<>(outcomes);
        this.count = count;
    }

    @Override
    public void run(Context context) {
        int count = this.count.map(intProvider -> intProvider.get(context.world().getRandom())).orElseGet(outcomes::size);
        if (shouldReinit()) {
            for (int i = 0; i < count; ++i) {
                outcomes.get(context.world().getRandom().nextInt(outcomes.size())).runOutcome(context);
            }
        } else {
            for (int i = 0; i < Math.clamp(count, 0, outcomes.size()); ++i) {
                outcomes.get(i).runOutcome(context);
            }
        }
    }
}
