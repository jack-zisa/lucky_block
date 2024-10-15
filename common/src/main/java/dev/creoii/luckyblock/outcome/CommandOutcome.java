package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.FunctionUtils;
import dev.creoii.luckyblock.util.position.PosProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Optional;

public class CommandOutcome extends Outcome {
    public static final MapCodec<CommandOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalDelayField(Outcome::getDelay),
                createGlobalPosField(Outcome::getPosProvider),
                Codec.STRING.fieldOf("command").forGetter(outcome -> outcome.command)
        ).apply(instance, CommandOutcome::new);
    });
    private final String command;

    public CommandOutcome(int luck, float chance, Optional<Integer> delay, Optional<PosProvider> pos, String command) {
        super(OutcomeType.COMMAND, luck, chance, delay, pos, false);
        this.command = command;
    }

    @Override
    public void run(Context context) {
        MinecraftServer server = context.world().getServer();
        if (server != null) {
            ServerCommandSource source = server.getCommandSource();
            if (getPosProvider().isPresent()) {
                source = source.withPosition(getPosProvider().get().getVec(context));
            }
            server.getCommandManager().executeWithPrefix(source, FunctionUtils.parseString(command, context));
        }
    }
}
