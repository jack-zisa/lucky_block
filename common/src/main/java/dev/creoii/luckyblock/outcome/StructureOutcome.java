package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.util.ContextualProvider;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import dev.creoii.luckyblock.util.provider.stringprovider.StringProvider;
import dev.creoii.luckyblock.util.vec.VecProvider;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;

import java.util.Optional;

public class StructureOutcome extends Outcome {
    public static final Identifier EMPTY_TARGET = Identifier.of("minecraft", "empty");
    public static final MapCodec<StructureOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalWeightField(Outcome::getWeightProvider),
                createGlobalDelayField(outcome -> outcome.delay),
                createGlobalPosField(Outcome::getPos),
                StringProvider.CODEC.fieldOf("structure").forGetter(outcome -> outcome.structureId),
                IntProvider.createValidatingCodec(0, 20).optionalFieldOf("depth").forGetter(outcome -> outcome.depth),
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
        if (context.world() instanceof ServerWorld serverWorld && serverWorld.getServer().getRegistryManager() instanceof DynamicRegistryManager dynamicRegistryManager) {
            Identifier structureId = Identifier.tryParse(ContextualProvider.applyStringContext(this.structureId, context).get(context.world().getRandom()));
            BlockPos pos = getPos(context).getPos(context);
            Optional<StructureTemplate> template = serverWorld.getStructureTemplateManager().getTemplate(structureId);
            Optional<RegistryEntry.Reference<StructurePool>> pool = dynamicRegistryManager.getOptional(RegistryKeys.TEMPLATE_POOL).get().getEntry(structureId);
            if (template.isPresent()) {
                if (!template.get().place(serverWorld, pos, pos, structurePlacementData.create(), StructureBlockBlockEntity.createRandom(serverWorld.getSeed()), 2)) {
                    LuckyBlockMod.LOGGER.error("Failed to place template '{}'", structureId);
                }
            } else if (pool.isPresent()) {
                IntProvider depth = ContextualProvider.applyIntContext(this.depth.orElse(LuckyBlockCodecs.ONE), context);
                if (!StructurePoolBasedGenerator.generate(serverWorld, pool.get(), EMPTY_TARGET, depth.get(context.world().getRandom()), pos, false)) {
                    LuckyBlockMod.LOGGER.error("Failed to generate jigsaw '{}'", structureId);
                }
            } else {
                Registry<Structure> registry = dynamicRegistryManager.getOrThrow(RegistryKeys.STRUCTURE);
                Structure structure = registry.get(structureId);
                if (structure == null) {
                    LuckyBlockMod.LOGGER.error("Structure identifier '{}' is invalid", structureId);
                    return;
                }
                ChunkGenerator chunkGenerator = serverWorld.getChunkManager().getChunkGenerator();
                Chunk chunk = serverWorld.getChunk(pos);
                StructureStart start = structure.createStructureStart(registry.getEntry(structureId).get(), serverWorld.getRegistryKey(), dynamicRegistryManager, serverWorld.getChunkManager().getChunkGenerator(), chunkGenerator.getBiomeSource(), serverWorld.getChunkManager().getNoiseConfig(), serverWorld.getStructureTemplateManager(), serverWorld.getSeed(), chunk.getPos(), 0, serverWorld, biome -> true);
                start.place(serverWorld, serverWorld.getStructureAccessor(), chunkGenerator, serverWorld.getRandom(), start.getBoundingBox(), chunk.getPos());
            }
        }
    }
}
