package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;

import java.util.Optional;

public class StructureOutcome extends Outcome {
    public static final MapCodec<StructureOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalDelayField(Outcome::getDelay),
                createGlobalPosField(Outcome::getPos),
                LuckyBlockCodecs.IDENTIFIER.fieldOf("structure").forGetter(outcome -> outcome.structureId)
        ).apply(instance, StructureOutcome::new);
    });
    private final String structureId;

    public StructureOutcome(int luck, float chance, Optional<Integer> delay, Optional<String> pos, String structureId) {
        super(OutcomeType.STRUCTURE, luck, chance, delay, pos);
        this.structureId = structureId;
    }

    @Override
    public void run(OutcomeContext context) {
        if (context.world() instanceof ServerWorld serverWorld && serverWorld.getServer().getRegistryManager() instanceof DynamicRegistryManager dynamicRegistryManager) {
            Identifier structureId = Identifier.tryParse(context.processString(this.structureId));
            Structure structure = dynamicRegistryManager.get(RegistryKeys.STRUCTURE).get(structureId);
            if (structure == null) {
                LuckyBlockMod.LOGGER.error("Structure identifier '{}' is invalid", structureId);
                return;
            }
            ChunkGenerator chunkGenerator = serverWorld.getChunkManager().getChunkGenerator();
            Chunk chunk = serverWorld.getChunk(getPos(context));
            StructureStart start = structure.createStructureStart(dynamicRegistryManager, chunkGenerator, chunkGenerator.getBiomeSource(), serverWorld.getChunkManager().getNoiseConfig(), serverWorld.getStructureTemplateManager(), serverWorld.getSeed(), chunk.getPos(), 0, serverWorld, biome -> true);
            start.place(serverWorld, serverWorld.getStructureAccessor(), chunkGenerator, serverWorld.getRandom(), start.getBoundingBox(), chunk.getPos());
        }
    }
}
