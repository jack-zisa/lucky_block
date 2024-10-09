package dev.creoii.luckyblock.luckyblock;

import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.outcome.OutcomeContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LuckyBlock extends Block {
    public LuckyBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            LuckyBlockMod.OUTCOME_MANAGER.getRandomOutcome(world.getRandom()).runOutcome(new OutcomeContext(world, pos, state, player));
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            if (!LuckyBlockMod.OUTCOME_MANAGER.isEmpty()) {
                world.breakBlock(pos, false, player);
                LuckyBlockMod.OUTCOME_MANAGER.getRandomOutcome(world.getRandom()).runOutcome(new OutcomeContext(world, pos, state, player));
            }
        }
        return ActionResult.success(world.isClient);
    }
}
