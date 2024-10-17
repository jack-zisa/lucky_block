package dev.creoii.luckyblock.util.shape;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.position.VecProvider;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Line extends Shape {
    public static final MapCodec<Line> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(VecProvider.CONSTANT_POS.fieldOf("size").forGetter(line -> line.size),
                VecProvider.VALUE_CODEC.fieldOf("from").forGetter(line -> line.from),
                VecProvider.VALUE_CODEC.fieldOf("to").forGetter(line -> line.to)
        ).apply(instance, Line::new);
    });
    private final VecProvider from;
    private final VecProvider to;

    public Line(VecProvider size, VecProvider from, VecProvider to) {
        super(ShapeType.LINE, size);
        this.from = from;
        this.to = to;
    }

    @Override
    public List<BlockPos> getBlockPositions(Outcome.Context context) {
        List<BlockPos> positions = new ArrayList<>();

        Vec3d direction = to.getVec(context).subtract(from.getVec(context)).normalize();
        for (int i = 0; i <= size.getPos(context).getX(); ++i) {
            positions.add(VecProvider.fromVec(direction.multiply(i)));
        }

        return positions;
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
