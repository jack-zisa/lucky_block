package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.util.position.PosProvider;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;

import java.util.Optional;

public class StructureOutcome extends Outcome {
    public static final MapCodec<StructureOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalDelayField(Outcome::getDelay),
                createGlobalPosField(Outcome::getPosProvider),
                Identifier.CODEC.fieldOf("structure").forGetter(outcome -> outcome.structureId)
        ).apply(instance, StructureOutcome::new);
    });
    private final Identifier structureId;

    public StructureOutcome(int luck, float chance, Optional<Integer> delay, Optional<PosProvider> pos, Identifier structureId) {
        super(OutcomeType.STRUCTURE, luck, chance, delay, pos, false);
        this.structureId = structureId;
    }

    @Override
    public void run(Context context) {
        if (context.world() instanceof ServerWorld serverWorld && serverWorld.getServer().getRegistryManager() instanceof DynamicRegistryManager dynamicRegistryManager) {
            BlockPos pos = getPosProvider(context).getPos(context);
            Optional<StructureTemplate> template = serverWorld.getStructureTemplateManager().getTemplate(structureId);
            if (template.isPresent()) {
                /* TODO: create a codec for structure placement data */
                if (!template.get().place(serverWorld, pos, pos, new StructurePlacementData(), StructureBlockBlockEntity.createRandom(serverWorld.getSeed()), 2)) {
                    LuckyBlockMod.LOGGER.error("Failed to place template '{}'", structureId);
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
