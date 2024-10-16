package dev.creoii.luckyblock.block;

import com.google.gson.JsonObject;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.outcome.OutcomeManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LuckyBlock extends Block {
    public static final IntProperty LUCK = IntProperty.of("luck", 0, 200);
    public static final IntProperty SET_OUTCOME = IntProperty.of("set_outcome", 0, 3);
    public static final List<String> WELL_OUTCOME_IDS = List.of(OutcomeManager.EMPTY_OUTCOME.toString(), "lucky:indexed/wish_came_true", "lucky:indexed/potato_wish", "lucky:indexed/death_wish");

    public LuckyBlock(Settings settings) {
        super(settings);
        setDefaultState(stateManager.getDefaultState().with(LUCK, 100).with(SET_OUTCOME, 0));
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        if (stack.contains(LuckyBlockMod.LUCK)) {
            int luck = stack.get(LuckyBlockMod.LUCK);
            Formatting formatting = luck == 0 ? Formatting.GRAY : luck < 0 ? Formatting.RED : Formatting.GREEN;
            tooltip.add(Text.translatable("lucky.item.luck", luck > 0 ? "+" + luck : luck).formatted(formatting));
        }
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        ItemStack stack = ctx.getStack();
        Integer luck = stack.get(LuckyBlockMod.LUCK);
        if (luck != null) {
            return getDefaultState().with(LUCK, luck + 100);
        }
        return super.getPlacementState(ctx);
    }

    private JsonObject getOutcomeFromState(World world, BlockState state) {
        int outcomeId = state.get(SET_OUTCOME);
        if (outcomeId != 0) {
            JsonObject outcome = LuckyBlockMod.OUTCOME_MANAGER.getIndexedOutcome(Identifier.tryParse(WELL_OUTCOME_IDS.get(outcomeId)));
            if (outcome != null) {
                return outcome;
            }
        }
        return LuckyBlockMod.OUTCOME_MANAGER.getRandomOutcome(world.getRandom(), state.get(LUCK) - 100).getRight();
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            Outcome.Context context = new Outcome.Context(world, pos, state, player);
            Outcome outcome = LuckyBlockMod.OUTCOME_MANAGER.parseJsonOutcome(getOutcomeFromState(world, state), context);
            if (outcome != null) {
                outcome.runOutcome(context);
            }
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            Outcome.Context context = new Outcome.Context(world, pos, state, player);
            Outcome outcome = LuckyBlockMod.OUTCOME_MANAGER.parseJsonOutcome(getOutcomeFromState(world, state), context);
            if (outcome != null) {
                world.breakBlock(pos, false);
                outcome.runOutcome(context);
            }
        }
        return ActionResult.success(world.isClient);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient && world.isReceivingRedstonePower(pos)) {
            Outcome.Context context = new Outcome.Context(world, pos, state, null);
            Outcome outcome = LuckyBlockMod.OUTCOME_MANAGER.parseJsonOutcome(getOutcomeFromState(world, state), context);
            if (outcome != null) {
                world.breakBlock(pos, false);
                outcome.runOutcome(context);
            }
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LUCK, SET_OUTCOME);
    }
}
