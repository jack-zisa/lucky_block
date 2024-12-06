package dev.creoii.luckyblock.util.function;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.LuckyBlockMod;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public record FunctionType(MapCodec<? extends Function> codec) {
    public static final FunctionType EMPTY = new FunctionType(EmptyFunction.CODEC);
    public static final FunctionType SET_NBT = new FunctionType(SetNbtFunction.CODEC);

    public static void init() {
        registerFunction(Identifier.of(LuckyBlockMod.NAMESPACE, "empty"), EMPTY);
        registerFunction(Identifier.of(LuckyBlockMod.NAMESPACE, "set_nbt"), SET_NBT);
    }

    public static void registerFunction(Identifier id, FunctionType function) {
        Registry.register(LuckyBlockMod.FUNCTION_TYPES, id, function);
    }
}
