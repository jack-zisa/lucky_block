package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.FunctionUtils;
import dev.creoii.luckyblock.util.vec.VecProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.intprovider.IntProvider;

import java.util.Optional;

public class CommandOutcome extends Outcome<NoneOutcome.NoneInfo> {
    public static final MapCodec<CommandOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalWeightField(Outcome::getWeightProvider),
                createGlobalDelayField(Outcome::getDelay),
                createGlobalPosField(Outcome::getPos),
                Codec.STRING.fieldOf("command").forGetter(outcome -> outcome.command)
        ).apply(instance, CommandOutcome::new);
    });
    private final String command;

    public CommandOutcome(int luck, float chance, IntProvider weightProvider, int delay, Optional<VecProvider> pos, String command) {
        super(OutcomeType.COMMAND, luck, chance, weightProvider, delay, pos, false);
        this.command = command;
    }

    @Override
    public void run(Context<NoneOutcome.NoneInfo> context) {
        MinecraftServer server = context.world().getServer();
        if (server != null) {
            ServerCommandSource source = server.getCommandSource();
            if (getPos().isPresent()) {
                source = source.withPosition(getPos().get().getVec(context));
            }
            server.getCommandManager().executeWithPrefix(source, FunctionUtils.parseString(command, context));
        }
    }
}
