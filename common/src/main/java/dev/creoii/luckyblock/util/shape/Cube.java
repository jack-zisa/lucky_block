package dev.creoii.luckyblock.util.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.position.PosProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class Cube extends Shape {
    public static final MapCodec<Cube> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalSizeField(Shape::getSize),
                Codec.BOOL.fieldOf("hollow").orElse(false).forGetter(cube -> cube.hollow)
        ).apply(instance, Cube::new);
    });
    private final boolean hollow;

    public Cube(PosProvider size, boolean hollow) {
        super(ShapeType.CUBE, size);
        this.hollow = hollow;
    }

    @Override
    public List<BlockPos> getBlockPositions(Outcome outcome, Outcome.Context context) {
        List<BlockPos> positions = new ArrayList<>();
        Vec3d size = this.size.getVec(context);
        BlockPos from = new BlockPos((int) Math.round(-size.x), (int) Math.round(-size.y), (int) Math.round(-size.z));
        BlockPos to = new BlockPos((int) Math.round(size.x), (int) Math.round(size.y), (int) Math.round(size.z));
        for (int z = from.getZ() + 1; z <= to.getZ() - 1; ++z) {
            for (int y = from.getY() + 1; y <= to.getY() - 1; ++y) {
                for (int x = from.getX() + 1; x <= to.getX() - 1; ++x) {
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
    public List<Vec3d> getVecPositions(Outcome outcome, Outcome.Context context) {
        return List.of();
    }
}
