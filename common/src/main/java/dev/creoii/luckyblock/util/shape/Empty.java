package dev.creoii.luckyblock.util.shape;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.position.ConstantPosProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class Empty extends Shape {
    public static final Shape INSTANCE = new Empty();
    public static final MapCodec<Shape> CODEC = MapCodec.unit(INSTANCE);

    public Empty() {
        super(ShapeType.EMPTY, ConstantPosProvider.ZERO);
    }

    @Override
    public List<BlockPos> getBlockPositions(Outcome outcome, Outcome.Context context) {
        return List.of();
    }

    @Override
    public List<Vec3d> getVecPositions(Outcome outcome, Outcome.Context context) {
        return List.of();
    }
}
