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
        return instance.group(createGlobalSizeField(Shape::getSize),
                Codec.BOOL.fieldOf("hollow").forGetter(cube -> cube.hollow)
        ).apply(instance, Cube::new);
    });
    private final boolean hollow;

    public Cube(String size, boolean hollow) {
        super(ShapeType.CUBE, size);
        this.hollow = hollow;
    }

    @Override
    public List<BlockPos> getBlockPositions(Outcome outcome, OutcomeContext context) {
        List<BlockPos> positions = new ArrayList<>();
        Vec3d size = context.parseVec3d(this.size);
        BlockPos from = new BlockPos((int) Math.round(-size.x), (int) Math.round(-size.y), (int) Math.round(-size.z));
        BlockPos to = new BlockPos((int) Math.round(size.x), (int) Math.round(size.y), (int) Math.round(size.z));
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
