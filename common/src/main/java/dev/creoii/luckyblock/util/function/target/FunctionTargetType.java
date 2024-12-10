package dev.creoii.luckyblock.util.function.target;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.LuckyBlockMod;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * TODO: random, all_of, tintable items/blocks/entities, growable blocks/entities, breedable entities, tameable entities
 */
public record FunctionTargetType(MapCodec<? extends FunctionTarget<?>> codec) {
    public static final FunctionTargetType NONE = new FunctionTargetType(NoneFunctionTarget.CODEC);
    public static final FunctionTargetType HAS_NBT = new FunctionTargetType(HasNbtFunctionTarget.CODEC);
    public static final FunctionTargetType HAS_COMPONENTS = new FunctionTargetType(HasComponentsFunctionTarget.CODEC);
    public static final FunctionTargetType HAS_COUNT = new FunctionTargetType(HasCountFunctionTarget.CODEC);
    public static final FunctionTargetType HAS_VELOCITY = new FunctionTargetType(HasVelocityFunctionTarget.CODEC);
    public static final FunctionTargetType MATCHING = new FunctionTargetType(MatchingFunctionTarget.CODEC);

    public static void init() {
        registerFunctionTargetType(Identifier.of(LuckyBlockMod.NAMESPACE, "none"), NONE);
        registerFunctionTargetType(Identifier.of(LuckyBlockMod.NAMESPACE, "has_nbt"), HAS_NBT);
        registerFunctionTargetType(Identifier.of(LuckyBlockMod.NAMESPACE, "has_components"), HAS_COMPONENTS);
        registerFunctionTargetType(Identifier.of(LuckyBlockMod.NAMESPACE, "has_count"), HAS_COUNT);
        registerFunctionTargetType(Identifier.of(LuckyBlockMod.NAMESPACE, "has_velocity"), HAS_VELOCITY);
        registerFunctionTargetType(Identifier.of(LuckyBlockMod.NAMESPACE, "matching"), MATCHING);
    }

    private static void registerFunctionTargetType(Identifier id, FunctionTargetType functionTarget) {
        Registry.register(LuckyBlockMod.FUNCTION_TARGET_TYPES, id, functionTarget);
    }
}
