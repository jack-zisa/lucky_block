package dev.creoii.luckyblock.luckyblock;

import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.outcome.OutcomeContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LuckyBlock extends Block {
    public static final IntProperty LUCK = IntProperty.of("luck", 0, 200);

    public LuckyBlock(Settings settings) {
        super(settings);
        setDefaultState(stateManager.getDefaultState().with(LUCK, 100));
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        ItemStack stack = ctx.getStack();
        if (stack.contains(LuckyBlockMod.LUCK)) {
            return getDefaultState().with(LUCK, stack.get(LuckyBlockMod.LUCK) + 100);
        }
        return super.getPlacementState(ctx);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            LuckyBlockMod.OUTCOME_MANAGER.getRandomOutcome(world.getRandom(), state.get(LUCK) - 100).runOutcome(new OutcomeContext(world, pos, state, player));
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            if (!LuckyBlockMod.OUTCOME_MANAGER.isEmpty()) {
                world.breakBlock(pos, false, player);
                LuckyBlockMod.OUTCOME_MANAGER.getRandomOutcome(world.getRandom(), state.get(LUCK) - 100).runOutcome(new OutcomeContext(world, pos, state, player));
            }
        }
        return ActionResult.success(world.isClient);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LUCK);
    }
}
