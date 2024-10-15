package dev.creoii.luckyblock.outcome;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.*;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.util.FunctionUtils;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Replacement for convoluted function parsing system:
 *  - Store JsonObjects rather than Outcomes in list <code>outcomes</code> DONE
 *  - When activating a lucky block, evaluate any {} functions in <code>entry.getValue()</code> WIP
 *  - Parse the JsonObject into an Outcome using: DONE
 *  <p>
 *      <code>
 *          DataResult<Outcome> dataResult = Outcome.CODEC.parse(JsonOps.INSTANCE, entry.getValue());
 *          dataResult.resultOrPartial()
 *      </code>
 *  </p>
 *  - Run the outcome as normal DONE
 */
public class OutcomeManager extends JsonDataLoader {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().setLenient().create();
    public static final Identifier EMPTY_OUTCOME = new Identifier("lucky:empty");
    private Map<Identifier, JsonObject> outcomes;
    private final Map<Pair<Outcome, Outcome.Context>, MutableInt> delays = Maps.newHashMap();

    public OutcomeManager() {
        super(GSON, "outcomes");
    }

    public Set<Identifier> getIds() {
        return outcomes.keySet();
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        ImmutableMap.Builder<Identifier, JsonObject> builder = ImmutableMap.builder();

        JsonObject empty = new JsonObject();
        empty.add("type", new JsonPrimitive("lucky:none"));
        builder.put(EMPTY_OUTCOME, empty);

        for (Map.Entry<Identifier, JsonObject> entry : prepared.entrySet().stream().map(entry -> new AbstractMap.SimpleImmutableEntry<>(entry.getKey(), entry.getValue().getAsJsonObject())).collect(Collectors.toSet())) {
            LuckyBlockMod.LOGGER.info("Loading outcome '{}'", entry.getKey());
            if (!entry.getKey().getPath().startsWith("wells/")) {
                builder.put(entry.getKey(), entry.getValue());
            }
        }
        outcomes = builder.build();
    }

    public void tickDelays(MinecraftServer server) {
        if (server.getTickManager().shouldTick()) {
            if (delays.isEmpty())
                return;

            List<Pair<Outcome, Outcome.Context>> toRemove = new ArrayList<>();

            delays.forEach((pair, integer) -> {
                if (integer.decrementAndGet() <= 0) {
                    pair.getLeft().run(pair.getRight());
                    toRemove.add(pair);
                }
            });

            toRemove.forEach(delays::remove);
        }
    }

    public void addDelay(Outcome outcome, Outcome.Context context, int delay) {
        delays.put(new Pair<>(outcome, context), new MutableInt(delay));
    }

    public boolean isEmpty() {
        return outcomes.isEmpty();
    }

    @Nullable
    public JsonObject getOutcome(Identifier id) {
        if (outcomes.isEmpty()) {
            throw new IllegalArgumentException("No outcomes found");
        }

        for (Map.Entry<Identifier, JsonObject> outcome : outcomes.entrySet()) {
            if (outcome.getKey().equals(id)) {
                return outcome.getValue();
            }
        }

        return null;
    }

    public Pair<Identifier, JsonObject> getRandomOutcome(Random random, int luck) {
        if (outcomes.isEmpty()) {
            throw new IllegalArgumentException("No outcomes found");
        }

        int lowest = 0, highest = 0;

        for (Map.Entry<Identifier, JsonObject> outcome : outcomes.entrySet()) {
            int outcomeLuck;
            try {
                outcomeLuck = outcome.getValue().getAsJsonPrimitive("luck").getAsInt();
            } catch (Exception e) {
                outcomeLuck = 0;
            }

            if (outcomeLuck < lowest)
                lowest = outcomeLuck;
            if (outcomeLuck > highest)
                highest = outcomeLuck;
        }

        highest += -1 * lowest + 1;

        double totalWeight = 0d;
        List<Double> weights = new ArrayList<>();
        weights.add(0d);

        for (Map.Entry<Identifier, JsonObject> outcome : outcomes.entrySet()) {
            int outcomeLuck;
            try {
                outcomeLuck = outcome.getValue().getAsJsonPrimitive("luck").getAsInt() + (-1 * lowest) + 1;
            } catch (Exception e) {
                outcomeLuck = (-1 * lowest) + 1;
            }

            double outcomeChance;
            try {
                outcomeChance = outcome.getValue().getAsJsonPrimitive("chance").getAsDouble();
            } catch (Exception e) {
                outcomeChance = 1d;
            }

            double adjusted = Math.pow(1d / (1d - Math.abs(luck) * .77d / 100d), luck >= 0 ? outcomeLuck : highest + 1 - outcomeLuck);
            weights.add(totalWeight += (outcomeChance > 0d ? outcomeChance : 1d) * adjusted * 100);
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

        @SuppressWarnings("unchecked") Map.Entry<Identifier, JsonObject> entry = outcomes.entrySet().toArray(new Map.Entry[]{})[index];
        return new Pair<>(entry.getKey(), entry.getValue());
    }

    @Nullable
    public Outcome parseJsonOutcome(JsonObject object, Outcome.Context context) {
        System.out.println("BEFORE: " + object.toString());

        String result = FunctionUtils.parseString(object.toString(), context);

        System.out.println("AFTER: " + result);
        JsonObject parsedObject = GSON.fromJson(result, JsonObject.class);
        DataResult<Outcome> dataResult = Outcome.CODEC.parse(JsonOps.INSTANCE, parsedObject);
        Optional<Outcome> outcome = dataResult.resultOrPartial(string -> LuckyBlockMod.LOGGER.error("Error parsing outcome: {}", string));
        return outcome.orElse(null);
    }
}
