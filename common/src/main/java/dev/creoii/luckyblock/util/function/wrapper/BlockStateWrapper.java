package dev.creoii.luckyblock.util.function.wrapper;

import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.function.Function;
import dev.creoii.luckyblock.util.function.target.Target;
import net.minecraft.block.BlockState;

import java.util.List;

public record BlockStateWrapper(BlockState state, List<Function<?>> functions) implements Target<BlockStateWrapper> {
    public static BlockStateWrapper fromState(BlockState state) {
        return new BlockStateWrapper(state, List.of());
    }

    public BlockState toState(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        functions.forEach(function -> function.apply(outcome, context));
        return state;
    }

    @Override
    public Target<BlockStateWrapper> update(Function<Target<?>> function, Object newObject) {
        if (newObject instanceof BlockStateWrapper newState) {
            //functions.remove(function);
            //newState.functions.remove(function);
            return newState;
        }
        throw new IllegalArgumentException("Attempted updating blockstate target with non-blockstate value.");
    }
}
