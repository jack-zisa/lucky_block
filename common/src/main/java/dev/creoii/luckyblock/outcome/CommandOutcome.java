package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.FunctionUtils;
import dev.creoii.luckyblock.util.provider.string.StringProvider;
import dev.creoii.luckyblock.util.vec.VecProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.intprovider.IntProvider;

import java.util.Optional;

public class CommandOutcome extends Outcome {
    public static final MapCodec<CommandOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalWeightField(Outcome::getWeightProvider),
                createGlobalDelayField(outcome -> outcome.delay),
                createGlobalPosField(Outcome::getPos),
                StringProvider.CODEC.fieldOf("command").forGetter(outcome -> outcome.command)
        ).apply(instance, CommandOutcome::new);
    });
    private final StringProvider command;

    public CommandOutcome(int luck, float chance, IntProvider weightProvider, IntProvider delay, Optional<VecProvider> pos, StringProvider command) {
        super(OutcomeType.COMMAND, luck, chance, weightProvider, delay, pos, false);
        this.command = command;
    }

    @Override
    public void run(Context context) {
        MinecraftServer server = context.world().getServer();
        if (server != null) {
            ServerCommandSource source = server.getCommandSource();
            if (getPos().isPresent()) {
                source = source.withPosition(getPos().get().getVec(context));
            }
            server.getCommandManager().executeWithPrefix(source, FunctionUtils.parseString(command.get(context.world().getRandom()), context));
        }
    }
}
