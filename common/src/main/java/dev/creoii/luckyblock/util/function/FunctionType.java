package dev.creoii.luckyblock.util.function;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.LuckyBlockMod;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Tint tintables, set blockstate, grow blocks/animals, breed, tame,
 */
public record FunctionType(MapCodec<? extends Function<?>> codec) {
    public static final FunctionType EMPTY = new FunctionType(EmptyFunction.CODEC);
    public static final FunctionType SET_NBT = new FunctionType(SetNbtFunction.CODEC);
    public static final FunctionType SET_COMPONENTS = new FunctionType(SetComponentsFunction.CODEC);

    public static void init() {
        registerFunctionType(Identifier.of(LuckyBlockMod.NAMESPACE, "empty"), EMPTY);
        registerFunctionType(Identifier.of(LuckyBlockMod.NAMESPACE, "set_nbt"), SET_NBT);
        registerFunctionType(Identifier.of(LuckyBlockMod.NAMESPACE, "set_components"), SET_COMPONENTS);
    }

    public static void registerFunctionType(Identifier id, FunctionType function) {
        Registry.register(LuckyBlockMod.FUNCTION_TYPES, id, function);
    }
}
