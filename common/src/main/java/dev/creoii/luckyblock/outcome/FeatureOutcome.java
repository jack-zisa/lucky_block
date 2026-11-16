package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.util.ContextualProvider;
import dev.creoii.luckyblock.util.provider.stringprovider.StringProvider;
import dev.creoii.luckyblock.util.vec.VecProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.List;
import java.util.Optional;

public class FeatureOutcome extends Outcome {
    public static final MapCodec<FeatureOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalWeightField(Outcome::getWeightProvider),
                createGlobalDelayField(outcome -> outcome.delay),
                createGlobalPosField(Outcome::getPos),
                StringProvider.CODEC.fieldOf("feature").forGetter(outcome -> outcome.featureId),
                PlacementModifier.CODEC.listOf().fieldOf("placement").orElse(List.of()).forGetter(outcome -> outcome.placementModifiers)
        ).apply(instance, FeatureOutcome::new);
    });
    private final StringProvider featureId;
    private final List<PlacementModifier> placementModifiers;

    public FeatureOutcome(int luck, float chance, IntProvider weightProvider, IntProvider delay, Optional<VecProvider> pos, StringProvider featureId, List<PlacementModifier> placementModifiers) {
        super(OutcomeType.FEATURE, luck, chance, weightProvider, delay, pos, false);
        this.featureId = featureId;
        this.placementModifiers = placementModifiers;
    }

    @Override
    public void run(Context context) {
        Level level = context.world();
        if (!level.isClientSide()) {
            Identifier featureId = Identifier.tryParse(ContextualProvider.applyStringContext(this.featureId, context).get(level.getRandom()));
            Optional<Holder.Reference<Feature>> configuredFeature = level.registryAccess().lookup(Registries.FEATURE).get().get(featureId);
            if (configuredFeature.isEmpty()) {
                LuckyBlockMod.LOGGER.error("Feature identifier '{}' is invalid", featureId);
                return;
            }
            BlockPos place = getPos(context).getPos(context);
            if (!placementModifiers.isEmpty()) {
                PlacedFeature placedFeature = new PlacedFeature(level.registryAccess().lookup(Registries.FEATURE).get().get(featureId).get(), placementModifiers);
                if (!placedFeature.place((WorldGenLevel) level, ((ServerLevel) level).getChunkSource().getGenerator(), level.getRandom(), place)) {
                    LuckyBlockMod.LOGGER.error("Failed to generate feature '{}' at '{}'", featureId, place.toShortString());
                }
            } else if (!configuredFeature.get().value().place((WorldGenLevel) level, ((ServerLevel) level).getChunkSource().getGenerator(), level.getRandom(), place)) {
                LuckyBlockMod.LOGGER.error("Failed to generate feature '{}' at '{}'", featureId, place.toShortString());
            }
        }
    }
}
