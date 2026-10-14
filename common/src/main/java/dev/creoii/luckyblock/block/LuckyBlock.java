package dev.creoii.luckyblock.block;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import dev.creoii.luckyblock.LuckyBlockContainer;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class LuckyBlock extends BaseEntityBlock {
    public static final IntegerProperty LUCK = IntegerProperty.create("luck", 0, 200);
    private final String namespace;

    public LuckyBlock(String namespace, BlockBehaviour.Properties settings) {
        super(settings);
        this.namespace = namespace;
        registerDefaultState(defaultBlockState().setValue(LUCK, 100));
    }

    public String getNamespace() {
        return namespace;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @org.jspecify.annotations.Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LuckyBlockEntity(pos, state);
    }

    @Override
    public @org.jspecify.annotations.Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        ItemStack stack = ctx.getItemInHand();
        Integer luck = stack.get(LuckyBlockMod.LUCK_COMPONENT);
        if (luck != null) {
            return defaultBlockState().setValue(LUCK, luck + 100);
        }
        return super.getStateForPlacement(ctx);
    }

    private JsonObject getOutcomeFromState(Level world, BlockState state, BlockPos pos, @Nullable Player player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof LuckyBlockEntity luckyBlockEntity && luckyBlockEntity.getOutcomeId() != null) {
            JsonObject outcome = LuckyBlockMod.OUTCOME_MANAGER.getOutcomeById(luckyBlockEntity.getOutcomeId());
            if (outcome != null) {
                return outcome;
            }
        }

        Pair<Identifier, JsonObject> pair = LuckyBlockMod.OUTCOME_MANAGER.getRandomOutcome(namespace, world.getRandom(), state.get(LUCK) - 100, player);
        if (pair == null)
            return null;

        LuckyBlockContainer container = LuckyBlockMod.luckyBlockManager.getContainer(namespace);
        if (container != null && container.isDebug()) {
            LuckyBlockMod.LOGGER.info("Executing outcome: {}", pair.getFirst());
        }
        return pair.getSecond();
    }

    @Override
    public void playerDestroy(ServerLevel level, ServerPlayer player, BlockPos pos, BlockState state, @org.jspecify.annotations.Nullable BlockEntity blockEntity, ItemStack destroyedWith) {
        LuckyBlockContainer container = LuckyBlockMod.luckyBlockManager.getContainer(namespace);
        if (container != null && ((container.hasActivation(LuckyBlockContainer.Activation.BREAK_SURVIVAL) && !player.isCreative()) || (container.hasActivation(LuckyBlockContainer.Activation.BREAK_CREATIVE) && player.isCreative()))) {
            if (!level.isClientSide()) {
                Outcome.Context context = new Outcome.Context(level, pos, state, player);
                JsonObject outcomeElement = getOutcomeFromState(level, state, pos, player);
                if (outcomeElement == null)
                    super.playerDestroy(level, player, pos, state, blockEntity, destroyedWith);

                Outcome outcome = LuckyBlockMod.OUTCOME_MANAGER.parseJsonOutcome(outcomeElement, context);
                if (outcome != null) {
                    outcome.runOutcome(context);
                }
            }
        }
        super.playerDestroy(level, player, pos, state, blockEntity, destroyedWith);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        LuckyBlockContainer container = LuckyBlockMod.luckyBlockManager.getContainer(namespace);
        if (container != null && container.hasActivation(LuckyBlockContainer.Activation.RIGHT_CLICK)) {
            if (!level.isClientSide()) {
                Outcome.Context context = new Outcome.Context(level, pos, state, player);
                JsonObject outcomeElement = getOutcomeFromState(level, state, pos, player);
                if (outcomeElement == null)
                    return super.useWithoutItem(state, level, pos, player, hitResult);

                Outcome outcome = LuckyBlockMod.OUTCOME_MANAGER.parseJsonOutcome(outcomeElement, context);
                if (outcome != null) {
                    level.destroyBlock(pos, false);
                    outcome.runOutcome(context);
                }
                return InteractionResult.SUCCESS_SERVER;
            }
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @org.jspecify.annotations.Nullable Orientation orientation, boolean movedByPiston) {
        LuckyBlockContainer container = LuckyBlockMod.luckyBlockManager.getContainer(namespace);
        if (container != null && container.hasActivation(LuckyBlockContainer.Activation.POWER)) {
            if (!level.isClientSide() && level.isReceivingRedstonePower(pos)) {
                Outcome.Context context = new Outcome.Context(level, pos, state, null);
                JsonObject outcomeElement = getOutcomeFromState(level, state, pos, null);
                if (outcomeElement == null)
                    return;

                Outcome outcome = LuckyBlockMod.OUTCOME_MANAGER.parseJsonOutcome(outcomeElement, context);
                if (outcome != null) {
                    level.destroyBlock(pos, false);
                    outcome.runOutcome(context);
                }
            }
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @org.jspecify.annotations.Nullable LivingEntity by, ItemStack itemStack) {
        LuckyBlockContainer container = LuckyBlockMod.luckyBlockManager.getContainer(namespace);
        if (container != null && container.hasActivation(LuckyBlockContainer.Activation.POWER)) {
            if (!level.isClientSide() && level.isReceivingRedstonePower(pos)) {
                Outcome.Context context = new Outcome.Context(level, pos, state, by instanceof Player player ? player : null);
                JsonObject outcomeElement = getOutcomeFromState(level, state, pos, by instanceof Player player ? player : null);
                if (outcomeElement == null)
                    return;

                Outcome outcome = LuckyBlockMod.OUTCOME_MANAGER.parseJsonOutcome(outcomeElement, context);
                if (outcome != null) {
                    level.destroyBlock(pos, false);
                    outcome.runOutcome(context);
                }
            }
        }
        super.setPlacedBy(level, pos, state, by, itemStack);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LUCK);
    }
}
