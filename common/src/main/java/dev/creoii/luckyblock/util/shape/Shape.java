package dev.creoii.luckyblock.util.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.outcome.OutcomeContext;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.function.Function;

public abstract class Shape {
    public static final Codec<Shape> CODEC = LuckyBlockMod.SHAPE_TYPES.getCodec().dispatch(Shape::getType, ShapeType::codec);
    private final ShapeType type;
    protected final Vec3d offset;
    protected final String size;

    public Shape(ShapeType type, BlockPos offset, String size) {
        this(type, offset.toCenterPos(), size);
    }

    public Shape(ShapeType type, Vec3d offset, String size) {
        this.type = type;
        this.offset = offset;
        this.size = size;
    }

    public static <O> RecordCodecBuilder<O, Vec3d> createGlobalOffsetField(Function<O, Vec3d> getter) {
        return Vec3d.CODEC.fieldOf("offset").orElse(Vec3d.ZERO).forGetter(getter);
    }

    public static <O> RecordCodecBuilder<O, String> createGlobalSizeField(Function<O, String> getter) {
        return LuckyBlockCodecs.DOUBLE.fieldOf("size").orElse("0").forGetter(getter);
    }

    public ShapeType getType() {
        return type;
    }

    public Vec3d getOffset() {
        return offset;
    }

    public String getSize() {
        return size;
    }

    public abstract List<BlockPos> getBlockPositions(Outcome outcome, OutcomeContext context);

    public abstract List<Vec3d> getVecPositions(Outcome outcome, OutcomeContext context);
}
