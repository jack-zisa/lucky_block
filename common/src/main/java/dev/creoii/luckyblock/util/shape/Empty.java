package dev.creoii.luckyblock.util.shape;

import com.mojang.serialization.Codec;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.vec.ConstantVecProvider;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.function.Predicate;

public class Empty extends Shape {
    public static final Shape INSTANCE = new Empty();
    public static final Codec<Shape> CODEC = Codec.unit(INSTANCE);

    public Empty() {
        super(ShapeType.EMPTY, ConstantVecProvider.ZERO);
    }

    @Override
    public List<BlockPos> getBlockPositions(Outcome.Context context) {
        return List.of();
    }

    @Override
    public List<Vec3d> getVecPositions(Outcome.Context context) {
        return List.of();
    }

    @Override
    public List<Entity> getEntitiesWithin(Outcome.Context context, Vec3d center, Predicate<Entity> filter) {
        return List.of();
    }
}
