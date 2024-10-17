package dev.creoii.luckyblock.util.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.position.VecProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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

    public Cube(VecProvider size, boolean hollow) {
        super(ShapeType.CUBE, size);
        this.hollow = hollow;
    }

    @Override
    public List<BlockPos> getBlockPositions(Outcome outcome, Outcome.Context context) {
        List<BlockPos> positions = new ArrayList<>();
        Vec3d size = this.size.getVec(context);
        BlockPos to = new BlockPos(Math.max(MathHelper.floor(size.x) - 1, 0), Math.max(MathHelper.floor(size.y) - 1, 0), Math.max(MathHelper.floor(size.z) - 1, 0));

        for (int z = 0; z <= to.getZ(); ++z) {
            for (int y = 0; y <= to.getY(); ++y) {
                for (int x = 0; x <= to.getX(); ++x) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (hollow) {
                        if (pos.getX() == 0 || pos.getX() == to.getX() || pos.getY() == 0 || pos.getY() == to.getY() || pos.getZ() == 0 || pos.getZ() == to.getZ()) {
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
