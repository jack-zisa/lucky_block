package dev.creoii.luckyblock.block;

import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.LuckyBlockContainer;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LuckyBlock extends BlockWithEntity {
    public static final IntProperty LUCK = IntProperty.of("luck", 0, 200);
    private final String namespace;

    public LuckyBlock(String namespace, Settings settings) {
        super(settings);
        this.namespace = namespace;
        setDefaultState(stateManager.getDefaultState().with(LUCK, 100));
    }

    public String getNamespace() {
        return namespace;
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public @Nullable NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return null;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LuckyBlockEntity(pos, state);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(LUCK, LuckyBlockItem.getLuck(ctx.getStack()) + 100);
    }

    private JsonObject getOutcomeFromState(World world, BlockState state, BlockPos pos, @Nullable PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof LuckyBlockEntity luckyBlockEntity && luckyBlockEntity.getOutcomeId() != null) {
            JsonObject outcome = LuckyBlockMod.OUTCOME_MANAGER.getOutcomeById(luckyBlockEntity.getOutcomeId());
            if (outcome != null) {
                return outcome;
            }
        }

        return LuckyBlockMod.OUTCOME_MANAGER.getRandomOutcome(namespace, world.getRandom(), state.get(LUCK) - 100, player).getRight();
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            Outcome.Context context = new Outcome.Context(world, pos, state, player);
            Outcome outcome = LuckyBlockMod.OUTCOME_MANAGER.parseJsonOutcome(getOutcomeFromState(world, state, pos, player), context);
            if (outcome != null) {
                outcome.runOutcome(context);
            }
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        LuckyBlockContainer container = LuckyBlockMod.luckyBlockManager.getContainer(namespace);
        if (container != null && container.doesRightClickOpen()) {
            if (!world.isClient) {
                Outcome.Context context = new Outcome.Context(world, pos, state, player);
                Outcome outcome = LuckyBlockMod.OUTCOME_MANAGER.parseJsonOutcome(getOutcomeFromState(world, state, pos, player), context);
                if (outcome != null) {
                    world.breakBlock(pos, false);
                    outcome.runOutcome(context);
                }
            }
            return ActionResult.success(world.isClient);
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient && world.isReceivingRedstonePower(pos)) {
            Outcome.Context context = new Outcome.Context(world, pos, state, null);
            Outcome outcome = LuckyBlockMod.OUTCOME_MANAGER.parseJsonOutcome(getOutcomeFromState(world, state, pos, null), context);
            if (outcome != null) {
                world.breakBlock(pos, false);
                outcome.runOutcome(context);
            }
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient && world.isReceivingRedstonePower(pos)) {
            Outcome.Context context = new Outcome.Context(world, pos, state, null);
            Outcome outcome = LuckyBlockMod.OUTCOME_MANAGER.parseJsonOutcome(getOutcomeFromState(world, state, pos, null), context);
            if (outcome != null) {
                world.breakBlock(pos, false);
                outcome.runOutcome(context);
            }
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LUCK);
    }
}
