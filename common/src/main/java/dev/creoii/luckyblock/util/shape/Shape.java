package dev.creoii.luckyblock.util.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.position.ConstantPosProvider;
import dev.creoii.luckyblock.util.position.PosProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.function.Function;

public abstract class Shape {
    public static final Codec<Shape> CODEC = LuckyBlockMod.SHAPE_TYPES.getCodec().dispatch(Shape::getType, ShapeType::codec);
    private final ShapeType type;
    protected final PosProvider size;

    public Shape(ShapeType type, PosProvider size) {
        this.type = type;
        this.size = size;
    }

    public static <O> RecordCodecBuilder<O, PosProvider> createGlobalSizeField(Function<O, PosProvider> getter) {
        return PosProvider.VALUE_CODEC.fieldOf("size").orElse(ConstantPosProvider.ZERO).forGetter(getter);
    }

    public ShapeType getType() {
        return type;
    }

    public PosProvider getSize() {
        return size;
    }

    public abstract List<BlockPos> getBlockPositions(Outcome outcome, Outcome.Context context);

    public abstract List<Vec3d> getVecPositions(Outcome outcome, Outcome.Context context);
}
