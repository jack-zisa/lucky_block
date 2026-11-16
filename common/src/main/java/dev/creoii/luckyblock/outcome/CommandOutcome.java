package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.ContextualProvider;
import dev.creoii.luckyblock.util.FunctionUtils;
import dev.creoii.luckyblock.util.provider.stringprovider.StringProvider;
import dev.creoii.luckyblock.util.vec.VecProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.valueproviders.IntProvider;

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
            Commands commands = server.getCommands();
            CommandSourceStack commandSourceStack = server.createCommandSourceStack();
            if (getPos().isPresent()) {
                commandSourceStack = commandSourceStack.withPosition(getPos().get().getVec(context));
            }
            commands.performPrefixedCommand(commandSourceStack, FunctionUtils.parseString(ContextualProvider.applyStringContext(command, context).get(context.world().getRandom()), context));
        }
    }
}
