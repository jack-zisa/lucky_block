package dev.creoii.luckyblock.util.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.vec.ConstantVecProvider;
import dev.creoii.luckyblock.util.vec.VecProvider;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class Shape {
    public static final Codec<Shape> CODEC = LuckyBlockMod.SHAPE_TYPES.getCodec().dispatch(Shape::getType, ShapeType::codec);
    private final ShapeType type;
    protected final VecProvider size;

    public Shape(ShapeType type, VecProvider size) {
        this.type = type;
        this.size = size;
    }

    public static <O> RecordCodecBuilder<O, VecProvider> createGlobalSizeField(Function<O, VecProvider> getter) {
        return VecProvider.VALUE_CODEC.fieldOf("size").orElse(ConstantVecProvider.ZERO).forGetter(getter);
    }

    public ShapeType getType() {
        return type;
    }

    public VecProvider getSize() {
        return size;
    }

    public abstract List<BlockPos> getBlockPositions(Outcome.Context context);

    public abstract List<Vec3d> getVecPositions(Outcome.Context context);

    public abstract List<Entity> getEntitiesWithin(Outcome.Context context, Vec3d center, Predicate<Entity> filter);
}
