package dev.creoii.luckyblock.luckyblock;

import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.outcome.OutcomeContext;
import dev.creoii.luckyblock.outcome.OutcomeManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LuckyBlock extends Block {
    public static final IntProperty LUCK = IntProperty.of("luck", 0, 200);
    public static final IntProperty SET_OUTCOME = IntProperty.of("set_outcome", 0, 3);
    public static final List<String> WELL_OUTCOME_IDS = List.of(OutcomeManager.EMPTY_OUTCOME.toString(), "lucky:wells/wish_came_true", "lucky:wells/potato_wish", "lucky:wells/death_wish");

    public LuckyBlock(Settings settings) {
        super(settings);
        setDefaultState(stateManager.getDefaultState().with(LUCK, 100).with(SET_OUTCOME, 0));
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
            int outcomeId = state.get(SET_OUTCOME);
            if (outcomeId != 0) {
                Outcome outcome = LuckyBlockMod.OUTCOME_MANAGER.getOutcome(Identifier.tryParse(WELL_OUTCOME_IDS.get(outcomeId)));
                if (outcome != null) {
                    outcome.runOutcome(new OutcomeContext(world, pos, state, player));
                    return super.onBreak(world, pos, state, player);
                }
            }
            LuckyBlockMod.OUTCOME_MANAGER.getRandomOutcome(world.getRandom(), state.get(LUCK) - 100).runOutcome(new OutcomeContext(world, pos, state, player));
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            if (!LuckyBlockMod.OUTCOME_MANAGER.isEmpty()) {
                int outcomeId = state.get(SET_OUTCOME);
                if (outcomeId != 0) {
                    Outcome outcome = LuckyBlockMod.OUTCOME_MANAGER.getOutcome(Identifier.tryParse(WELL_OUTCOME_IDS.get(outcomeId)));
                    if (outcome != null) {
                        world.breakBlock(pos, false, player);
                        outcome.runOutcome(new OutcomeContext(world, pos, state, player));
                        return ActionResult.success(world.isClient);
                    }
                }
                world.breakBlock(pos, false, player);
                LuckyBlockMod.OUTCOME_MANAGER.getRandomOutcome(world.getRandom(), state.get(LUCK) - 100).runOutcome(new OutcomeContext(world, pos, state, player));
            }
        }
        return ActionResult.success(world.isClient);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient && world.isReceivingRedstonePower(pos)) {
            if (!LuckyBlockMod.OUTCOME_MANAGER.isEmpty()) {
                int outcomeId = state.get(SET_OUTCOME);
                if (outcomeId != 0) {
                    Outcome outcome = LuckyBlockMod.OUTCOME_MANAGER.getOutcome(Identifier.tryParse(WELL_OUTCOME_IDS.get(outcomeId)));
                    if (outcome != null) {
                        world.breakBlock(pos, false);
                        outcome.runOutcome(new OutcomeContext(world, pos, state, null));
                        return;
                    }
                }
                world.breakBlock(pos, false);
                LuckyBlockMod.OUTCOME_MANAGER.getRandomOutcome(world.getRandom(), state.get(LUCK) - 100).runOutcome(new OutcomeContext(world, pos, state, null));
            }
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LUCK, SET_OUTCOME);
    }
}
