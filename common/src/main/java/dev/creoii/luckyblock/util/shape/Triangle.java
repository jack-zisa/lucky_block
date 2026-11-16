package dev.creoii.luckyblock.util.shape;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.vec.VecProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Triangle extends Shape {
    public static final MapCodec<Triangle> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalSizeField(Shape::getSize),
                Type.CODEC.fieldOf("triangle_type").forGetter(triangle -> triangle.type)
        ).apply(instance, Triangle::new);
    });
    private final Type type;

    public Triangle(VecProvider size, Type type) {
        super(ShapeType.TRIANGLE, size);
        this.type = type;
    }

    @Override
    public List<BlockPos> getBlockPositions(Outcome.Context context) {
        List<BlockPos> positions = new ArrayList<>();
        Vec3 size = this.size.getVec(context);

        if (type == Type.POINT) {
            for (int y = 0; y < size.y; y++) {
                int halfWidth = (int) (size.y / 2) - y;
                int halfDepth = ((int) size.z / 2) - y;

                if (halfWidth < 0 || halfDepth < 0)
                    break;

                for (int z = -halfDepth; z <= halfDepth; z++) {
                    for (int x = -halfWidth; x <= halfWidth; x++) {
                        positions.add(new BlockPos(x, y, z));
                    }
                }
            }
        } else if (type == Type.SIDE) {
            for (int z = 0; z < size.z; z++) {
                for (int y = 0; y < size.y; y++) {
                    int width = ((int) size.y) - y;
                    for (int x = 0; x < width; x++) {
                        positions.add(new BlockPos(x, y, z));
                    }
                }
            }
        } else if (type == Type.MIDDLE) {
            for (int z = 0; z < size.z; z++) {
                for (int y = 0; y < size.y; y++) {
                    int halfWidth = (int) (size.y / 2) - y;
                    for (int x = -halfWidth; x <= halfWidth; x++) {
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
        return List.of();
    }

    public enum Type implements StringRepresentable {
        SIDE("side"),
        MIDDLE("middle"),
        POINT("point");

        public static final StringRepresentable.EnumCodec<Type> CODEC = StringRepresentable.fromEnum(Type::values);
        private final String name;

        Type(String name) {
            this.name = name;
        }


        @Override
        public @NonNull String getSerializedName() {
            return name;
        }
    }
}
