package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.util.position.PosProvider;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;

import java.util.List;
import java.util.Optional;

public class FeatureOutcome extends Outcome {
    public static final MapCodec<FeatureOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalDelayField(Outcome::getDelay),
                createGlobalPosField(Outcome::getPosProvider),
                Identifier.CODEC.fieldOf("feature").forGetter(outcome -> outcome.featureId),
                PlacementModifier.CODEC.listOf().fieldOf("placement").orElse(List.of()).forGetter(outcome -> outcome.placementModifiers)
        ).apply(instance, FeatureOutcome::new);
    });
    private final Identifier featureId;
    private final List<PlacementModifier> placementModifiers;

    public FeatureOutcome(int luck, float chance, Optional<Integer> delay, Optional<PosProvider> pos, Identifier featureId, List<PlacementModifier> placementModifiers) {
        super(OutcomeType.RANDOM, luck, chance, delay, pos, false);
        this.featureId = featureId;
        this.placementModifiers = placementModifiers;
    }

    @Override
    public void run(Context context) {
        if (context.world() instanceof ServerWorld serverWorld && serverWorld.getServer().getRegistryManager() instanceof DynamicRegistryManager dynamicRegistryManager) {
            ConfiguredFeature<?, ?> configuredFeature = dynamicRegistryManager.get(RegistryKeys.CONFIGURED_FEATURE).get(featureId);
            if (configuredFeature == null) {
                LuckyBlockMod.LOGGER.error("Feature identifier '{}' is invalid", featureId);
                return;
            }
            BlockPos place = getPosProvider(context).getPos(context);
            if (!placementModifiers.isEmpty()) {
                PlacedFeature placedFeature = new PlacedFeature(dynamicRegistryManager.get(RegistryKeys.CONFIGURED_FEATURE).getEntry(featureId).get(), placementModifiers);
                if (!placedFeature.generate(serverWorld, serverWorld.getChunkManager().getChunkGenerator(), serverWorld.getRandom(), place)) {
                    LuckyBlockMod.LOGGER.error("Failed to generate feature '{}' at '{}'", featureId, place.toShortString());
                }
            } else if (!configuredFeature.generate(serverWorld, serverWorld.getChunkManager().getChunkGenerator(), serverWorld.getRandom(), place)) {
                LuckyBlockMod.LOGGER.error("Failed to generate feature '{}' at '{}'", featureId, place.toShortString());
            }
        }
    }
}
