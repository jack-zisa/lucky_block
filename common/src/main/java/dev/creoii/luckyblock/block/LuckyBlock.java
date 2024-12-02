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
import net.minecraft.client.item.TooltipType;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
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
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return null;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LuckyBlockEntity(pos, state);
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        if (stack.contains(LuckyBlockMod.LUCK_COMPONENT)) {
            int luck = stack.get(LuckyBlockMod.LUCK_COMPONENT);
            Formatting formatting = luck == 0 ? Formatting.GRAY : luck < 0 ? Formatting.RED : Formatting.GREEN;
            tooltip.add(Text.translatable("lucky.item.luck", luck > 0 ? "+" + luck : luck).formatted(formatting));
        }
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        ItemStack stack = ctx.getStack();
        Integer luck = stack.get(LuckyBlockMod.LUCK_COMPONENT);
        if (luck != null) {
            return getDefaultState().with(LUCK, luck + 100);
        }
        return super.getPlacementState(ctx);
    }

    private JsonObject getOutcomeFromState(World world, BlockState state, BlockPos pos, @Nullable PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof LuckyBlockEntity luckyBlockEntity && luckyBlockEntity.getOutcomeId() != null) {
            JsonObject outcome = LuckyBlockMod.OUTCOME_MANAGER.getOutcomeById(luckyBlockEntity.getOutcomeId());
            if (outcome != null) {
                return outcome;
            }
        }

        Pair<Identifier, JsonObject> pair = LuckyBlockMod.OUTCOME_MANAGER.getRandomOutcome(namespace, world.getRandom(), state.get(LUCK) - 100, player);
        LuckyBlockContainer container = LuckyBlockMod.luckyBlockManager.getContainer(namespace);
        if (container != null && container.isDebug()) {
            LuckyBlockMod.LOGGER.info("Executing outcome: {}", pair.getLeft());
        }
        return pair.getRight();
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        LuckyBlockContainer container = LuckyBlockMod.luckyBlockManager.getContainer(namespace);
        if (container != null && container.hasActivation(LuckyBlockContainer.Activation.BREAK)) {
            if (!world.isClient) {
                Outcome.Context context = new Outcome.Context(world, pos, state, player);
                Outcome outcome = LuckyBlockMod.OUTCOME_MANAGER.parseJsonOutcome(getOutcomeFromState(world, state, pos, player), context);
                if (outcome != null) {
                    outcome.runOutcome(context);
                }
            }
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        LuckyBlockContainer container = LuckyBlockMod.luckyBlockManager.getContainer(namespace);
        if (container != null && container.hasActivation(LuckyBlockContainer.Activation.RIGHT_CLICK)) {
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
        return super.onUse(state, world, pos, player, hit);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        LuckyBlockContainer container = LuckyBlockMod.luckyBlockManager.getContainer(namespace);
        if (container != null && container.hasActivation(LuckyBlockContainer.Activation.RIGHT_CLICK)) {
            if (!world.isClient && world.isReceivingRedstonePower(pos)) {
                Outcome.Context context = new Outcome.Context(world, pos, state, null);
                Outcome outcome = LuckyBlockMod.OUTCOME_MANAGER.parseJsonOutcome(getOutcomeFromState(world, state, pos, null), context);
                if (outcome != null) {
                    world.breakBlock(pos, false);
                    outcome.runOutcome(context);
                }
            }
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        LuckyBlockContainer container = LuckyBlockMod.luckyBlockManager.getContainer(namespace);
        if (container != null && container.hasActivation(LuckyBlockContainer.Activation.RIGHT_CLICK)) {
            if (!world.isClient && world.isReceivingRedstonePower(pos)) {
                Outcome.Context context = new Outcome.Context(world, pos, state, null);
                Outcome outcome = LuckyBlockMod.OUTCOME_MANAGER.parseJsonOutcome(getOutcomeFromState(world, state, pos, null), context);
                if (outcome != null) {
                    world.breakBlock(pos, false);
                    outcome.runOutcome(context);
                }
            }
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LUCK);
    }
}
