package dev.creoii.luckyblock.util.function.wrapper;

import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.function.Function;
import dev.creoii.luckyblock.util.function.Functions;
import dev.creoii.luckyblock.util.function.target.Target;
import net.minecraft.block.Block;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public record BlockStateWrapper(BlockStateProvider state, Functions functions) implements Wrapper<Block, BlockStateWrapper>, Target<BlockStateWrapper> {
    @Override
    public Block getRegistryObject(Outcome.Context<?> context) {
        return state.get(context.world().getRandom(), context.pos()).getBlock();
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
