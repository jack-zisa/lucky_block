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

public class Sphere extends Shape {
    public static final MapCodec<Sphere> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalSizeField(Shape::getSize),
                Codec.BOOL.fieldOf("hollow").orElse(false).forGetter(sphere -> sphere.hollow)
        ).apply(instance, Sphere::new);
    });
    private final boolean hollow;

    public Sphere(PosProvider size, boolean hollow) {
        super(ShapeType.SPHERE, size);
        this.hollow = hollow;
    }

    @Override
    public List<BlockPos> getBlockPositions(Outcome outcome, Outcome.Context context) {
        List<BlockPos> positions = new ArrayList<>();
        Vec3d size = this.size.getVec(context);

        for (int x = (int) Math.round(-size.x) + 1; x <= Math.round(size.x) - 1; x++) {
            for (int y = (int) Math.round(-size.y) + 1; y <= Math.round(size.y) - 1; y++) {
                for (int z = (int) Math.round(-size.z) + 1; z <= Math.round(size.z) - 1; z++) {
                    double normalizedX = x / size.x;
                    double normalizedY = y / size.y;
                    double normalizedZ = z / size.z;

                    double distance2 = normalizedX * normalizedX + normalizedY * normalizedY + normalizedZ * normalizedZ;

                    if (hollow) {
                        if (distance2 <= 1d && distance2 >= Math.pow(1 - 1d / Math.max(size.x, Math.max(size.y, size.z)), 2)) {
                            positions.add(new BlockPos(x, y, z));
                        }
                    } else if (distance2 <= 1d) {
                        positions.add(new BlockPos(x, y, z));
                    }
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
