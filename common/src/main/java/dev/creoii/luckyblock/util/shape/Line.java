package dev.creoii.luckyblock.util.shape;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.position.PosProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class Line extends Shape {
    public static final MapCodec<Line> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(PosProvider.CONSTANT_POS.fieldOf("size").forGetter(line -> line.size),
                PosProvider.VALUE_CODEC.fieldOf("target").forGetter(line -> line.target)
        ).apply(instance, Line::new);
    });
    private final PosProvider target;

    public Line(PosProvider size, PosProvider target) {
        super(ShapeType.LINE, size);
        this.target = target;
    }

    @Override
    public List<BlockPos> getBlockPositions(Outcome outcome, Outcome.Context context) {
        List<BlockPos> positions = new ArrayList<>();

        Vec3d direction = target.getVec(context).subtract(outcome.getPosProvider(context).getVec(context)).normalize();
        for (int i = 0; i <= size.getPos(context).getX(); ++i) {
            positions.add(PosProvider.fromVec(direction.multiply(i)));
        }

        return positions;
    }

    @Override
    public List<Vec3d> getVecPositions(Outcome outcome, Outcome.Context context) {
        return List.of();
    }
}
