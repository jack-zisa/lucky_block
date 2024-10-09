package dev.creoii.luckyblock.util.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.outcome.OutcomeContext;
import dev.creoii.luckyblock.util.LuckyBlockUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class Sphere extends Shape {
    public static final MapCodec<Sphere> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalOffsetField(Shape::getOffset),
                createGlobalSizeField(Shape::getSize),
                Codec.BOOL.fieldOf("hollow").forGetter(sphere -> sphere.hollow)
        ).apply(instance, Sphere::new);
    });
    private final boolean hollow;

    public Sphere(Vec3d center, String size, boolean hollow) {
        super(ShapeType.SPHERE, center, size);
        this.hollow = hollow;
    }

    @Override
    public List<BlockPos> getBlockPositions(Outcome outcome, OutcomeContext context) {
        List<BlockPos> positions = new ArrayList<>();
        int size = context.parseInt(this.size);
        for (int x = -size; x <= size; x++) {
            for (int y = -size; y <= size; y++) {
                for (int z = -size; z <= size; z++) {
                    double distance2 = x * x + y * y + z * z;
                    if (hollow) {
                        if (distance2 <= size * size && distance2 >= (size - 1) * (size - 1)) {
                            positions.add(LuckyBlockUtils.fromVec3d(offset).add(x, y, z));
                        }
                    } else if (distance2 <= size * size) {
                        positions.add(LuckyBlockUtils.fromVec3d(offset).add(x, y, z));
                    }
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
