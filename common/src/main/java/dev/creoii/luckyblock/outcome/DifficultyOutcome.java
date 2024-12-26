package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.Difficulty;

import java.util.Optional;

public class DifficultyOutcome extends Outcome {
    public static final MapCodec<DifficultyOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalWeightField(Outcome::getWeightProvider),
                createGlobalDelayField(outcome -> outcome.delay),
                Difficulty.CODEC.fieldOf("difficulty").forGetter(outcome -> outcome.difficulty)
        ).apply(instance, DifficultyOutcome::new);
    });
    private final Difficulty difficulty;

    public DifficultyOutcome(int luck, float chance, IntProvider weightProvider, IntProvider delay, Difficulty difficulty) {
        super(OutcomeType.DIFFICULTY, luck, chance, weightProvider, delay, Optional.empty(), false);
        this.difficulty = difficulty;
    }

    @Override
    public void run(Context context) {
        MinecraftServer server = context.world().getServer();
        if (server != null) {
            server.setDifficulty(difficulty, true);
        }
    }
}
