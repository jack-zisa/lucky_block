package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.util.ContextualProvider;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import dev.creoii.luckyblock.util.vec.VecProvider;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
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
    public static final Identifier EMPTY_TARGET = new Identifier("minecraft", "empty");
    public static final Codec<StructureOutcome> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalWeightField(Outcome::getWeightProvider),
                createGlobalDelayField(Outcome::getDelay),
                createGlobalPosField(Outcome::getPos),
                Identifier.CODEC.fieldOf("structure").forGetter(outcome -> outcome.structureId),
                IntProvider.createValidatingCodec(0, 7).optionalFieldOf("depth").forGetter(outcome -> outcome.depth), // TODO: Set max to 20 for Minecraft 1.21
                LuckyBlockCodecs.StructurePlacementData.CODEC.fieldOf("placement_data").orElse(LuckyBlockCodecs.StructurePlacementData.DEFAULT).forGetter(outcome -> outcome.structurePlacementData)
        ).apply(instance, StructureOutcome::new);
    });
    private final Identifier structureId;
    private final Optional<IntProvider> depth;
    private final LuckyBlockCodecs.StructurePlacementData structurePlacementData;

    public StructureOutcome(int luck, float chance, IntProvider weightProvider, int delay, Optional<VecProvider> pos, Identifier structureId, Optional<IntProvider> depth, LuckyBlockCodecs.StructurePlacementData structurePlacementData) {
        super(OutcomeType.STRUCTURE, luck, chance, weightProvider, delay, pos, false);
        this.structureId = structureId;
        this.depth = depth;
        this.structurePlacementData = structurePlacementData;
    }

    @Override
    public void run(Context context) {
        if (context.world() instanceof ServerWorld serverWorld && serverWorld.getServer().getRegistryManager() instanceof DynamicRegistryManager dynamicRegistryManager) {
            BlockPos pos = getPos(context).getPos(context);
            Optional<StructureTemplate> template = serverWorld.getStructureTemplateManager().getTemplate(structureId);
            Optional<RegistryEntry.Reference<StructurePool>> pool = dynamicRegistryManager.get(RegistryKeys.TEMPLATE_POOL).getEntry(RegistryKey.of(RegistryKeys.TEMPLATE_POOL, structureId));
            if (template.isPresent()) {
                if (!template.get().place(serverWorld, pos, pos, structurePlacementData.create(), StructureBlockBlockEntity.createRandom(serverWorld.getSeed()), 2)) {
                    LuckyBlockMod.LOGGER.error("Failed to place template '{}'", structureId);
                }
            } else if (pool.isPresent()) {
                if (!StructurePoolBasedGenerator.generate(serverWorld, pool.get(), EMPTY_TARGET, ContextualProvider.applyContext(depth.orElse(LuckyBlockCodecs.ONE), context).get(context.world().getRandom()), pos, false)) {
                    LuckyBlockMod.LOGGER.error("Failed to generate jigsaw '{}'", structureId);
                }
            } else {
                Structure structure = dynamicRegistryManager.get(RegistryKeys.STRUCTURE).get(structureId);
                if (structure == null) {
                    LuckyBlockMod.LOGGER.error("Structure identifier '{}' is invalid", structureId);
                    return;
                }
                ChunkGenerator chunkGenerator = serverWorld.getChunkManager().getChunkGenerator();
                Chunk chunk = serverWorld.getChunk(pos);
                StructureStart start = structure.createStructureStart(dynamicRegistryManager, chunkGenerator, chunkGenerator.getBiomeSource(), serverWorld.getChunkManager().getNoiseConfig(), serverWorld.getStructureTemplateManager(), serverWorld.getSeed(), chunk.getPos(), 0, serverWorld, biome -> true);
                start.place(serverWorld, serverWorld.getStructureAccessor(), chunkGenerator, serverWorld.getRandom(), start.getBoundingBox(), chunk.getPos());
            }
        }
    }
}
