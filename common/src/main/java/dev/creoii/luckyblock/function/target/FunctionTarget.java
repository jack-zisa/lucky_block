package dev.creoii.luckyblock.function.target;

import com.mojang.serialization.Codec;
import dev.creoii.luckyblock.LuckyBlockRegistries;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.function.Function;
import dev.creoii.luckyblock.function.wrapper.BlockStateWrapper;
import dev.creoii.luckyblock.function.wrapper.EntityWrapper;
import dev.creoii.luckyblock.function.wrapper.ItemStackWrapper;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtElement;

import java.util.List;
import java.util.stream.Collectors;

public abstract class FunctionTarget<T extends Target<?>> {
    public static final Codec<FunctionTarget<?>> CODEC = LuckyBlockRegistries.FUNCTION_TARGET_TYPES.getCodec().dispatch(FunctionTarget::getType, FunctionTargetType::codec);
    private final FunctionTargetType type;

    public FunctionTarget(FunctionTargetType type) {
        this.type = type;
    }

    public FunctionTargetType getType() {
        return type;
    }

    public static List<Target<?>> getBlockStateTargets(ContextInfo info) {
        return info.getTargets().stream().filter(o -> o instanceof BlockStateWrapper).map(o -> (BlockStateWrapper) o).collect(Collectors.toList());
    }

    public static List<Target<?>> getBlockEntityTargets(ContextInfo info) {
        return info.getTargets().stream().filter(o -> o instanceof BlockEntity).map(o -> new BlockEntityTarget((BlockEntity) o)).collect(Collectors.toList());
    }

    public static List<Target<?>> getItemStackTargets(ContextInfo info) {
        return info.getTargets().stream().filter(o -> o instanceof ItemStackWrapper).map(o -> (ItemStackWrapper) o).collect(Collectors.toList());
    }

    public static List<Target<?>> getEntityTargets(ContextInfo info) {
        return info.getTargets().stream().filter(o -> o instanceof EntityWrapper).map(o -> (EntityWrapper) o).collect(Collectors.toList());
    }

    /**
     * @return a list of targets converted from {@link ContextInfo#getTargets()}. Valid types include:
     * <ul>
     *     <li>{@link net.minecraft.block.BlockState}</li>
     *     <li>{@link net.minecraft.item.ItemStack}</li>
     *     <li>{@link net.minecraft.entity.Entity}</li>
     *     <li>{@link net.minecraft.block.entity.BlockEntity}</li>
     * </ul>
     */
    public abstract List<T> getTargets(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context);

    public record NoneTarget() implements Target<Void> {
        @Override
        public Target<Void> update(Function<Target<?>> function, Object newObject) {
            return new NoneTarget();
        }
    }

    public record BlockEntityTarget(BlockEntity blockEntity) implements NbtTarget<BlockEntity> {
        @Override
        public Target<BlockEntity> update(Function<Target<?>> function, Object newObject) {
            if (newObject instanceof BlockEntity newBlockEntity) {
                return new BlockEntityTarget(newBlockEntity);
            }
            throw new IllegalArgumentException("Attempted updating blockentity target with non-blockentity value.");
        }

        @Override
        public BlockEntity setNbt(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, NbtElement nbt) {
            return blockEntity;
        }
    }
}
