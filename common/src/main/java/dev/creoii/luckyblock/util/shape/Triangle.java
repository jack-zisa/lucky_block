package dev.creoii.luckyblock.util.shape;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.vec.VecProvider;
import net.minecraft.entity.Entity;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.IntProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Triangle extends Shape {
    public static final MapCodec<Triangle> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalSizeField(Shape::getSize),
                Type.CODEC.fieldOf("triangle_type").forGetter(triangle -> triangle.type),
                IntProvider.POSITIVE_CODEC.fieldOf("base").forGetter(triangle -> triangle.base),
                IntProvider.POSITIVE_CODEC.optionalFieldOf("depth").forGetter(triangle -> triangle.depth)
        ).apply(instance, Triangle::new);
    });
    private final Type type;
    private final IntProvider base;
    private final Optional<IntProvider> depth;

    public Triangle(VecProvider size, Type type, IntProvider base, Optional<IntProvider> depth) {
        super(ShapeType.TRIANGLE, size);
        this.type = type;
        this.base = base;
        this.depth = depth;
    }

    @Override
    public List<BlockPos> getBlockPositions(Outcome.Context context) {
        List<BlockPos> positions = new ArrayList<>();
        Vec3d size = this.size.getVec(context);
        int base = this.base.get(context.world().getRandom());

        if (depth.isPresent() && type == Type.POINT) {
            int depth = this.depth.get().get(context.world().getRandom());
            for (int y = 0; y < size.y; y++) {
                int halfWidth = (base / 2) - y;
                int halfDepth = (depth / 2) - y;

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
                    int width = base - y;
                    for (int x = 0; x < width; x++) {
                        positions.add(new BlockPos(x, y, z));
                    }
                }
            }
        } else if (type == Type.MIDDLE) {
            for (int z = 0; z < size.z; z++) {
                for (int y = 0; y < size.y; y++) {
                    int halfWidth = base / 2 - y;
                    for (int x = -halfWidth; x <= halfWidth; x++) {
                        positions.add(new BlockPos(x, y, z));
                    }
                }
            }
        }
        return positions;
    }

    @Override
    public List<Vec3d> getVecPositions(Outcome.Context context) {
        return List.of();
    }

    @Override
    public List<Entity> getEntitiesWithin(Outcome.Context context, Vec3d center, Predicate<Entity> filter) {
        return List.of();
    }

    public enum Type implements StringIdentifiable {
        SIDE("side"),
        MIDDLE("middle"),
        POINT("point");

        public static final StringIdentifiable.EnumCodec<Type> CODEC = StringIdentifiable.createCodec(Type::values);
        private final String name;

        Type(String name) {
            this.name = name;
        }


        @Override
        public String asString() {
            return name;
        }
    }
}
