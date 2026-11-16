package dev.creoii.luckyblock.util.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.vec.VecProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Sphere extends Shape {
    public static final MapCodec<Sphere> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalSizeField(Shape::getSize),
                Codec.BOOL.fieldOf("hollow").orElse(false).forGetter(sphere -> sphere.hollow)
        ).apply(instance, Sphere::new);
    });
    private final boolean hollow;

    public Sphere(VecProvider size, boolean hollow) {
        super(ShapeType.SPHERE, size);
        this.hollow = hollow;
    }

    @Override
    public List<BlockPos> getBlockPositions(Outcome.Context context) {
        List<BlockPos> positions = new ArrayList<>();
        Vec3 size = this.size.getVec(context);

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
    public List<Vec3> getVecPositions(Outcome.Context context) {
        return List.of();
    }

    @Override
    public List<Entity> getEntitiesWithin(Outcome.Context context, Vec3 center, Predicate<Entity> filter) {
        List<Entity> entities = new ArrayList<>();
        Vec3 size = this.size.getVec(context);
        double x = size.x / 2d;
        double y = size.y / 2d;
        double z = size.z / 2d;

        if (context.world() instanceof ServerLevel serverWorld) {
            for (Entity entity : serverWorld.getEntitiesOfClass(Entity.class, AABB.ofSize(center, size.x, size.y, size.z), filter)) {
                Vec3 vec3d = entity.position().subtract(center);

                double normalizedX = vec3d.x / x;
                double normalizedY = vec3d.y / y;
                double normalizedZ = vec3d.z / z;

                if (normalizedX * normalizedX + normalizedY * normalizedY + normalizedZ * normalizedZ <= 1d) {
                    entities.add(entity);
                }
            }
        }
        return entities;
    }
}
