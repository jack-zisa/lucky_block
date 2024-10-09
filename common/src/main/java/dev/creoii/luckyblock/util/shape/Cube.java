package dev.creoii.luckyblock.util.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.outcome.OutcomeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class Cube extends Shape {
    public static final MapCodec<Cube> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalOffsetField(Shape::getOffset),
                createGlobalSizeField(Shape::getSize),
                Codec.BOOL.fieldOf("hollow").forGetter(cube -> cube.hollow)
        ).apply(instance, Cube::new);
    });
    private final boolean hollow;

    public Cube(Vec3d center, String size, boolean hollow) {
        super(ShapeType.CUBE, center, size);
        this.hollow = hollow;
    }

    @Override
    public List<BlockPos> getBlockPositions(Outcome outcome, OutcomeContext context) {
        List<BlockPos> positions = new ArrayList<>();
        int size = context.parseInt(this.size);
        BlockPos from = new BlockPos((int) Math.round(offset.x - size), (int) Math.round(offset.y - size), (int) Math.round(offset.z - size));
        BlockPos to = new BlockPos((int) Math.round(offset.x + size), (int) Math.round(offset.y + size), (int) Math.round(offset.z + size));
        for (int z = from.getZ(); z <= to.getZ(); ++z) {
            for (int y = from.getY(); y <= to.getY(); ++y) {
                for (int x = from.getX(); x <= to.getX(); ++x) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (hollow) {
                        if (pos.getX() == from.getX() || pos.getX() == to.getX() || pos.getY() == from.getY() || pos.getY() == to.getY() || pos.getZ() == from.getZ() || pos.getZ() == to.getZ()) {
                            positions.add(pos);
                        }
                    } else positions.add(pos);
                }
            }
        }
        return positions;
    }

    @Override
    public List<Vec3d> getVecPositions(Outcome outcome, OutcomeContext context) {
        return List.of();
    }
}
