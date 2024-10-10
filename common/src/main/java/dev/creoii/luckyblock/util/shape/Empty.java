package dev.creoii.luckyblock.util.shape;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.outcome.OutcomeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class Empty extends Shape {
    public static final Shape INSTANCE = new Empty();
    public static final MapCodec<Shape> CODEC = MapCodec.unit(INSTANCE);

    public Empty() {
        super(ShapeType.EMPTY, "0");
    }

    @Override
    public List<BlockPos> getBlockPositions(Outcome outcome, OutcomeContext context) {
        return List.of();
    }

    @Override
    public List<Vec3d> getVecPositions(Outcome outcome, OutcomeContext context) {
        return List.of();
    }
}
