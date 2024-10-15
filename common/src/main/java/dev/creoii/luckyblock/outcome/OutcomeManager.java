package dev.creoii.luckyblock.outcome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.gson.*;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.creoii.luckyblock.LuckyBlockMod;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OutcomeManager extends JsonDataLoader {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().setLenient().create();
    public static final Identifier EMPTY_OUTCOME = new Identifier("lucky:empty");
    private List<Pair<Identifier, Outcome>> outcomes;
    private List<Identifier> ids;
    private final Map<Pair<Outcome, OutcomeContext>, MutableInt> delays = Maps.newHashMap();

    public OutcomeManager() {
        super(GSON, "outcomes");
    }

    public List<Identifier> getIds() {
        return ids;
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        ImmutableList.Builder<Pair<Identifier, Outcome>> builder = ImmutableList.builder();
        ImmutableList.Builder<Identifier> builder1 = ImmutableList.builder();

        builder.add(new Pair<>(EMPTY_OUTCOME, NoneOutcome.INSTANCE));
        builder1.add(EMPTY_OUTCOME);

        for (Map.Entry<Identifier, JsonElement> entry : prepared.entrySet()) {
            DataResult<Outcome> dataResult = Outcome.CODEC.parse(JsonOps.INSTANCE, entry.getValue());
            dataResult.resultOrPartial(string -> LuckyBlockMod.LOGGER.error("Error parsing outcome '{}': {}", entry.getKey(), string)).ifPresent(outcome -> {
                LuckyBlockMod.LOGGER.info("Loading outcome '{}' with chance {}", entry.getKey(), outcome.getChance());
                builder.add(new Pair<>(entry.getKey(), outcome));
                builder1.add(entry.getKey());
            });
        }
        outcomes = builder.build();
        ids = builder1.build();
    }

    public void tickDelays(MinecraftServer server) {
        if (server.getTickManager().shouldTick()) {
            if (delays.isEmpty())
                return;

            List<Pair<Outcome, OutcomeContext>> toRemove = new ArrayList<>();

            delays.forEach((pair, integer) -> {
                if (integer.decrementAndGet() <= 0) {
                    pair.getLeft().run(pair.getRight());
                    toRemove.add(pair);
                }
            });

            toRemove.forEach(delays::remove);
        }
    }

    public void addDelay(Outcome outcome, OutcomeContext context, int delay) {
        delays.put(new Pair<>(outcome, context), new MutableInt(delay));
    }

    public boolean isEmpty() {
        return outcomes.isEmpty();
    }

    @Nullable
    public Outcome getOutcome(Identifier id) {
        if (outcomes.isEmpty()) {
            throw new IllegalArgumentException("No outcomes found");
        }

        for (Pair<Identifier, Outcome> outcome : outcomes) {
            if (outcome.getLeft().equals(id)) {
                return outcome.getRight();
            }
        }

        return null;
    }

    public Outcome getRandomOutcome(Random random, int luck) {
        if (outcomes.isEmpty()) {
            throw new IllegalArgumentException("No outcomes found");
        }

        int lowest = 0, highest = 0;

        for (Pair<Identifier, Outcome> outcome : outcomes) {
            if (outcome.getRight().getLuck() < lowest)
                lowest = outcome.getRight().getLuck();
            if (outcome.getRight().getLuck() > highest)
                highest = outcome.getRight().getLuck();
        }

        highest += -1 * lowest + 1;

        double totalWeight = 0d;
        List<Double> weights = new ArrayList<>();
        weights.add(0d);

        for (Pair<Identifier, Outcome> drop : outcomes) {
            int outcomeLuck = drop.getRight().getLuck() + (-1 * lowest) + 1;
            double adjusted = Math.pow(1d / (1d - Math.abs(luck) * .77d / 100d), luck >= 0 ? outcomeLuck : highest + 1 - outcomeLuck);
            weights.add(totalWeight += (drop.getRight().getChance() > 0f ? drop.getRight().getChance() : 1d) * adjusted * 100);
        }

        double rIndex = random.nextDouble() * totalWeight;
        int index = -1;
        for (int i = 0; i < weights.size() - 1; ++i) {
            if (rIndex >= weights.get(i) && rIndex < weights.get(i + 1)) {
                index = i;
            }
        }

        if (index == -1)
            index = weights.size() - 2;

        return outcomes.get(index).getRight();
    }
}
