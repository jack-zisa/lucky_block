package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.util.ContextualProvider;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import dev.creoii.luckyblock.util.provider.stringprovider.StringProvider;
import dev.creoii.luckyblock.util.vec.VecProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Optional;

public class StructureOutcome extends Outcome {
    public static final Identifier EMPTY_TARGET = Identifier.fromNamespaceAndPath("minecraft", "empty");
    public static final MapCodec<StructureOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalWeightField(Outcome::getWeightProvider),
                createGlobalDelayField(outcome -> outcome.delay),
                createGlobalPosField(Outcome::getPos),
                StringProvider.CODEC.fieldOf("structure").forGetter(outcome -> outcome.structureId),
                IntProviders.codec(0, 20).optionalFieldOf("depth").forGetter(outcome -> outcome.depth),
                LuckyBlockCodecs.StructurePlacementData.CODEC.fieldOf("placement_data").orElse(LuckyBlockCodecs.StructurePlacementData.DEFAULT).forGetter(outcome -> outcome.structurePlacementData)
        ).apply(instance, StructureOutcome::new);
    });
    private final StringProvider structureId;
    private final Optional<IntProvider> depth;
    private final LuckyBlockCodecs.StructurePlacementData structurePlacementData;

    public StructureOutcome(int luck, float chance, IntProvider weightProvider, IntProvider delay, Optional<VecProvider> pos, StringProvider structureId, Optional<IntProvider> depth, LuckyBlockCodecs.StructurePlacementData structurePlacementData) {
        super(OutcomeType.STRUCTURE, luck, chance, weightProvider, delay, pos, false);
        this.structureId = structureId;
        this.depth = depth;
        this.structurePlacementData = structurePlacementData;
    }

    @Override
    public void run(Context context) {
        Level level = context.world();
        if (!level.isClientSide()) {
            RegistryAccess registryAccess = level.registryAccess();

            Identifier structureId = Identifier.tryParse(ContextualProvider.applyStringContext(this.structureId, context).get(context.world().getRandom()));
            BlockPos pos = getPos(context).getPos(context);
            Optional<StructureTemplate> template = ((ServerLevel) level).getStructureTemplateManager().get(structureId);
            Optional<Holder.Reference<StructureTemplatePool>> pool = registryAccess.get(Registries.TEMPLATE_POOL).get().value().get(structureId);
            if (template.isPresent()) {
                if (!template.get().placeInWorld((ServerLevelAccessor) level, pos, pos, structurePlacementData.create(), RandomSource.create(((ServerLevel) level).getSeed()), 2)) {
                    LuckyBlockMod.LOGGER.error("Failed to place template '{}'", structureId);
                }
            } else if (pool.isPresent()) {
                IntProvider depth = ContextualProvider.applyIntContext(this.depth.orElse(LuckyBlockCodecs.ONE), context);
                if (!StructurePoolBasedGenerator.generate(serverWorld, pool.get(), EMPTY_TARGET, depth.sample(context.world().getRandom()), pos, false)) {
                    LuckyBlockMod.LOGGER.error("Failed to generate jigsaw '{}'", structureId);
                }
            } else {
                Registry<Structure> registry = registryAccess.getOrThrow(Registries.STRUCTURE).value();
                Structure structure = registry.getValue(structureId);
                if (structure == null) {
                    LuckyBlockMod.LOGGER.error("Structure identifier '{}' is invalid", structureId);
                    return;
                }
                ChunkGenerator chunkGenerator = ((ServerLevel) level).getChunkSource().getGenerator();
                ChunkAccess chunk = level.getChunk(pos);
                StructureStart start = structure.generate(registry.get(structureId).get(), level.dimension(), registryAccess, chunkGenerator, chunkGenerator.getBiomeSource(), ((ServerLevel) level).getChunkSource().randomState(), ((ServerLevel) level).getChunkSource().randomState(), ((ServerLevel) level).getSeed(), chunk.getPos(), 0, ((ServerLevel) level), biome -> true);
                start.placeInChunk((WorldGenLevel) level, ((ServerLevel) level).structureManager(), chunkGenerator, level.getRandom(), start.getBoundingBox(), chunk.getPos());
            }
        }
    }
}
