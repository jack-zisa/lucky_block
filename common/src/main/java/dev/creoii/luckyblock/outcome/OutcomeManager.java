package dev.creoii.luckyblock.outcome;

import com.google.common.collect.Maps;
import com.google.gson.*;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.creoii.luckyblock.LuckyBlockContainer;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.util.FunctionUtils;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Pair;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.util.*;

public class OutcomeManager extends JsonDataLoader<JsonElement> {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().setLenient().create();
    private final Map<Pair<Outcome<? extends ContextInfo>, Outcome.Context<? extends ContextInfo>>, MutableInt> delays = Maps.newHashMap();

    public OutcomeManager() {
        super(null, "outcomes");
    }

    @Override
    protected Map<Identifier, JsonElement> prepare(ResourceManager resourceManager, Profiler profiler) {
        Map<Identifier, JsonElement> map = new HashMap<>();
        load(resourceManager, dataType, GSON, map);
        return map;
    }

    public static void load(ResourceManager manager, String dataType, Gson gson, Map<Identifier, JsonElement> results) {
        ResourceFinder resourceFinder = ResourceFinder.json(dataType);
        for (Map.Entry<Identifier, Resource> identifierResourceEntry : resourceFinder.findResources(manager).entrySet()) {
            Identifier identifier = identifierResourceEntry.getKey();
            Identifier identifier2 = resourceFinder.toResourceId(identifier);

            try {
                Reader reader = identifierResourceEntry.getValue().getReader();
                try {
                    JsonElement jsonElement = JsonHelper.deserialize(gson, reader, JsonElement.class);
                    JsonElement jsonElement2 = results.put(identifier2, jsonElement);
                    if (jsonElement2 != null) {
                        throw new IllegalStateException("Duplicate data file ignored with ID " + identifier2);
                    }
                } catch (Throwable var13) {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (Throwable var12) {
                            var13.addSuppressed(var12);
                        }
                    }

                    throw var13;
                }

                reader.close();
            } catch (IllegalArgumentException | IOException | JsonParseException var14) {
                LuckyBlockMod.LOGGER.error("Couldn't parse data file {} from {}", identifier2, identifier, (Exception) var14);
            }
        }
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        for (Map.Entry<Identifier, JsonElement> entry : prepared.entrySet()) {
            if (!entry.getValue().isJsonObject())
                continue;

            LuckyBlockContainer container = LuckyBlockMod.luckyBlockManager.getContainer(entry.getKey().getNamespace());
            if (container == null)
                continue;

            if (container.isDebug())
                LuckyBlockMod.LOGGER.info("Loading outcome '{}'", entry.getKey());
            /**
             * FOR LATER:
             * - Define a custom prefix in lucky_block.json, which will then be used later for this
             */
            if (entry.getKey().getPath().startsWith("nonrandom/")) {
                container.addNonRandomOutcome(entry.getKey(), (JsonObject) entry.getValue());
            } else container.addRandomOutcome(entry.getKey(), (JsonObject) entry.getValue());
        }
    }

    public void tickDelays(MinecraftServer server) {
        if (server.getTickManager().shouldTick()) {
            if (delays.isEmpty())
                return;

            List<Pair<Outcome<? extends ContextInfo>, Outcome.Context<? extends ContextInfo>>> toRemove = new ArrayList<>();

            delays.forEach((pair, integer) -> {
                if (integer.decrementAndGet() <= 0) {
                    pair.getLeft().runUnchecked(pair.getRight());
                    toRemove.add(pair);
                }
            });

            toRemove.forEach(delays::remove);
        }
    }

    public void addDelay(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, int delay) {
        delays.put(new Pair<>(outcome, context), new MutableInt(delay));
    }

    @Nullable
    public JsonObject getOutcomeById(Identifier id) {
        LuckyBlockContainer container = LuckyBlockMod.luckyBlockManager.getContainer(id.getNamespace());
        if (container == null) {
            throw new IllegalArgumentException("Lucky Block container '" + id.getNamespace() + "' not found");
        }
        Map<Identifier, JsonObject> nonrandomOutcomes = container.getNonrandomOutcomes();
        if (nonrandomOutcomes.isEmpty()) {
            throw new IllegalArgumentException("No nonrandom outcomes found");
        }

        for (Map.Entry<Identifier, JsonObject> outcome : nonrandomOutcomes.entrySet()) {
            if (outcome.getKey().equals(id)) {
                return outcome.getValue();
            }
        }

        Map<Identifier, JsonObject> randomOutcomes = container.getRandomOutcomes();
        if (randomOutcomes.isEmpty()) {
            throw new IllegalArgumentException("No random outcomes found");
        }

        for (Map.Entry<Identifier, JsonObject> outcome : randomOutcomes.entrySet()) {
            if (outcome.getKey().equals(id)) {
                return outcome.getValue();
            }
        }

        throw new IllegalArgumentException("Outcome '" + id + "' does not exist");
    }

    public Pair<Identifier, JsonObject> getRandomOutcome(String namespace, Random random, int luck, @Nullable PlayerEntity player) {
        LuckyBlockContainer container = LuckyBlockMod.luckyBlockManager.getContainer(namespace);
        if (container == null) {
            throw new IllegalArgumentException("Lucky Block container '" + namespace + "' not found");
        }
        Map<Identifier, JsonObject> randomOutcomes = container.getRandomOutcomes();
        if (randomOutcomes.isEmpty()) {
            throw new IllegalArgumentException("No outcomes found");
        }

        if (player != null) {
            StatusEffectInstance goodLuckEffect = player.getStatusEffect(StatusEffects.LUCK);
            StatusEffectInstance badLuckEffect = player.getStatusEffect(StatusEffects.UNLUCK);

            if (goodLuckEffect != null) {
                luck += goodLuckEffect.getAmplifier() + 1;
            }

            if (badLuckEffect != null) {
                luck -= badLuckEffect.getAmplifier() + 1;
            }
        }

        int lowest = 0, highest = 0;

        for (Map.Entry<Identifier, JsonObject> outcome : randomOutcomes.entrySet()) {
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

        for (Map.Entry<Identifier, JsonObject> outcome : randomOutcomes.entrySet()) {
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

        @SuppressWarnings("unchecked") Map.Entry<Identifier, JsonObject> entry = randomOutcomes.entrySet().toArray(new Map.Entry[]{})[index];
        return new Pair<>(entry.getKey(), entry.getValue());
    }

    @Nullable
    public Outcome<? extends ContextInfo> parseJsonOutcome(JsonObject object, Outcome.Context<? extends ContextInfo> context) {
        JsonObject parsedObject = GSON.fromJson(FunctionUtils.parseString(object.toString(), context), JsonObject.class);
        DataResult<Outcome<? extends ContextInfo>> dataResult = Outcome.CODEC.parse(JsonOps.INSTANCE, parsedObject);
        Optional<Outcome<? extends ContextInfo>> outcome = dataResult.resultOrPartial(string -> LuckyBlockMod.LOGGER.error("Error parsing outcome: {}", string));
        return outcome.orElse(null);
    }
}
