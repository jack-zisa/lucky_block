package dev.creoii.luckyblock.util.shape;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.vec.ConstantVecProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Predicate;

public class Empty extends Shape {
    public static final Shape INSTANCE = new Empty();
    public static final MapCodec<Shape> CODEC = MapCodec.unit(INSTANCE);

    public Empty() {
        super(ShapeType.EMPTY, ConstantVecProvider.ZERO);
    }

    @Override
    public List<BlockPos> getBlockPositions(Outcome.Context context) {
        return List.of();
    }

    @Override
    public List<Vec3> getVecPositions(Outcome.Context context) {
        return List.of();
    }

    @Override
    public List<Entity> getEntitiesWithin(Outcome.Context context, Vec3 center, Predicate<Entity> filter) {
        return List.of();
    }
}
