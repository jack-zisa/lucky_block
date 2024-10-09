package dev.creoii.luckyblock.util.shape;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.outcome.OutcomeContext;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import dev.creoii.luckyblock.util.LuckyBlockUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class Line extends Shape {
    public static final MapCodec<Line> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalOffsetField(Shape::getOffset),
                createGlobalSizeField(Shape::getSize),
                LuckyBlockCodecs.BLOCK_POS.fieldOf("target").forGetter(line -> line.target)
        ).apply(instance, Line::new);
    });
    private final String target;

    public Line(Vec3d offset, String size, String target) {
        super(ShapeType.LINE, offset, size);
        this.target = target;
    }

    @Override
    public List<BlockPos> getBlockPositions(Outcome outcome, OutcomeContext context) {
        List<BlockPos> positions = new ArrayList<>();

        Vec3d direction = context.parseVec3d(target).subtract(outcome.getVec(context).add(offset)).normalize();
        for (int i = 0; i <= context.parseInt(size); ++i) {
            positions.add(LuckyBlockUtils.fromVec3d(offset.add(direction.multiply(i))));
        }

        return positions;
    }

    @Override
    public List<Vec3d> getVecPositions(Outcome outcome, OutcomeContext context) {
        return List.of();
    }
}
