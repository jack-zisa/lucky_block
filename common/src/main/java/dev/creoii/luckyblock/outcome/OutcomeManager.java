package dev.creoii.luckyblock.outcome;

import com.google.common.collect.ImmutableMap;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OutcomeManager extends JsonDataLoader {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().setLenient().create();
    private Map<Identifier, Outcome> outcomes = ImmutableMap.of();
    private final Map<Pair<Outcome, OutcomeContext>, MutableInt> delays = Maps.newHashMap();

    public OutcomeManager() {
        super(GSON, "outcomes");
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        ImmutableMap.Builder<Identifier, Outcome> builder = ImmutableMap.builder();
        for (Map.Entry<Identifier, JsonElement> entry : prepared.entrySet()) {
            DataResult<Outcome> dataResult = Outcome.CODEC.parse(JsonOps.INSTANCE, entry.getValue());
            dataResult.resultOrPartial(string -> LuckyBlockMod.LOGGER.error("Error parsing outcome '{}': {}", entry.getKey(), string)).ifPresent(outcome -> {
                LuckyBlockMod.LOGGER.info("Loading outcome '{}'", entry.getKey());
                builder.put(entry.getKey(), outcome);
            });
        }
        outcomes = builder.build();
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

    public Outcome getRandomOutcome(Random random) {
        int r = random.nextInt(outcomes.size());
        int i = 0;

        for (Outcome outcome : outcomes.values()) {
            if (i == r)
                return outcome;
            ++i;
        }

        return NoneOutcome.INSTANCE;
    }
}
