package dev.creoii.luckyblock.util.function.target;

import com.mojang.serialization.Codec;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.function.FunctionObjectCodecs;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentChanges;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.List;
import java.util.stream.Collectors;

public abstract class FunctionTarget<T extends Target<?>> {
    public static final Codec<FunctionTarget<?>> CODEC = LuckyBlockMod.FUNCTION_TARGET_TYPES.getCodec().dispatch(FunctionTarget::getType, FunctionTargetType::codec);
    private final FunctionTargetType type;

    public FunctionTarget(FunctionTargetType type) {
        this.type = type;
    }

    public FunctionTargetType getType() {
        return type;
    }

    public static List<Target<?>> getBlockStateTargets(ContextInfo info) {
        return info.getTargets().stream().filter(o -> o instanceof BlockState).map(o -> new BlockStateTarget((BlockState) o)).collect(Collectors.toList());
    }

    public static List<Target<?>> getBlockEntityTargets(ContextInfo info) {
        return info.getTargets().stream().filter(o -> o instanceof BlockEntity).map(o -> new BlockEntityTarget((BlockEntity) o)).collect(Collectors.toList());
    }

    public static List<Target<?>> getItemStackTargets(ContextInfo info) {
        return info.getTargets().stream().filter(o -> o instanceof ItemStackTarget).map(o -> (ItemStackTarget) o).collect(Collectors.toList());
    }

    public static List<Target<?>> getEntityTargets(ContextInfo info) {
        return info.getTargets().stream().filter(o -> o instanceof Entity).map(o -> new EntityTarget((Entity) o)).collect(Collectors.toList());
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
        public Target<Void> update(Object newObject) {
            return new NoneTarget();
        }
    }

    public record BlockStateTarget(BlockState blockState) implements Target<BlockState> {
        @Override
        public Target<BlockState> update(Object newObject) {
            if (newObject instanceof BlockState newBlockState) {
                return new BlockStateTarget(newBlockState);
            }
            throw new IllegalArgumentException("Attempted updating blockstate target with non-blockstate value.");
        }
    }

    public record BlockEntityTarget(BlockEntity blockEntity) implements NbtTarget<BlockEntity> {
        @Override
        public Target<BlockEntity> update(Object newObject) {
            if (newObject instanceof BlockEntity newBlockEntity) {
                return new BlockEntityTarget(newBlockEntity);
            }
            throw new IllegalArgumentException("Attempted updating blockentity target with non-blockentity value.");
        }

        @Override
        public BlockEntity setNbt(NbtElement nbt) {
            return blockEntity;
        }
    }

    public record EntityTarget(Entity entity) implements NbtTarget<Entity> {
        @Override
        public Target<Entity> update(Object newObject) {
            if (newObject instanceof Entity newEntity) {
                return new EntityTarget(newEntity);
            }
            throw new IllegalArgumentException("Attempted updating entity target with non-entity value.");
        }

        @Override
        public Entity setNbt(NbtElement nbt) {
            if (nbt instanceof NbtCompound nbtCompound)
                entity.readNbt(nbtCompound);
            return entity;
        }
    }

    public record ItemStackTarget(FunctionObjectCodecs.ItemStackWrapper stack) implements ComponentsTarget<FunctionObjectCodecs.ItemStackWrapper> {
        @Override
        public Target<FunctionObjectCodecs.ItemStackWrapper> update(Object newObject) {
            if (newObject instanceof FunctionObjectCodecs.ItemStackWrapper newStack) {
                return new ItemStackTarget(newStack);
            }
            throw new IllegalArgumentException("Attempted updating itemstack target with non-itemstack value.");
        }

        @Override
        public FunctionObjectCodecs.ItemStackWrapper setComponents(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, ComponentChanges componentChanges) {
            stack.stack().applyChanges(componentChanges);
            return new FunctionObjectCodecs.ItemStackWrapper(stack.stack(), List.of());
        }
    }
}
