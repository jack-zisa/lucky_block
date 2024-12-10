package dev.creoii.luckyblock.util.shape;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.LuckyBlockMod;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public record ShapeType(MapCodec<? extends Shape> codec) {
    public static final ShapeType EMPTY = new ShapeType(Empty.CODEC);
    public static final ShapeType CUBE = new ShapeType(Cube.CODEC);
    public static final ShapeType SPHERE = new ShapeType(Sphere.CODEC);
    public static final ShapeType LINE = new ShapeType(Line.CODEC);
    public static final ShapeType TRIANGLE = new ShapeType(Triangle.CODEC);

    public static void init() {
        registerShapeType(Identifier.of(LuckyBlockMod.NAMESPACE, "empty"), EMPTY);
        registerShapeType(Identifier.of(LuckyBlockMod.NAMESPACE, "cube"), CUBE);
        registerShapeType(Identifier.of(LuckyBlockMod.NAMESPACE, "sphere"), SPHERE);
        registerShapeType(Identifier.of(LuckyBlockMod.NAMESPACE, "line"), LINE);
        registerShapeType(Identifier.of(LuckyBlockMod.NAMESPACE, "triangle"), TRIANGLE);
    }

    private static void registerShapeType(Identifier id, ShapeType shapeType) {
        Registry.register(LuckyBlockMod.SHAPE_TYPES, id, shapeType);
    }
}
