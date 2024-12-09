package dev.creoii.luckyblock.util.function.target;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.LuckyBlockMod;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * TODO: random, all_of, rename to has_*, in_tag, by_id, tintable items/blocks/entities, growable blocks/entities, breedable entities, tameable entities
 */
public record FunctionTargetType(MapCodec<? extends FunctionTarget<?>> codec) {
    public static final FunctionTargetType NONE = new FunctionTargetType(NoneFunctionTarget.CODEC);
    public static final FunctionTargetType NBT = new FunctionTargetType(NbtFunctionTarget.CODEC);
    public static final FunctionTargetType COMPONENTS = new FunctionTargetType(ComponentsFunctionTarget.CODEC);

    public static void init() {
        registerFunctionTargetType(Identifier.of(LuckyBlockMod.NAMESPACE, "none"), NONE);
        registerFunctionTargetType(Identifier.of(LuckyBlockMod.NAMESPACE, "nbt"), NBT);
        registerFunctionTargetType(Identifier.of(LuckyBlockMod.NAMESPACE, "components"), COMPONENTS);
    }

    public static void registerFunctionTargetType(Identifier id, FunctionTargetType functionTarget) {
        Registry.register(LuckyBlockMod.FUNCTION_TARGET_TYPES, id, functionTarget);
    }
}
