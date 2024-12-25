package dev.creoii.luckyblock.util.function;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.LuckyBlockMod;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Tint tintables, entity variants, set blockstate/blockentity, grow blocks/animals, breed, tame
 */
public record FunctionType(MapCodec<? extends Function<?>> codec) {
    public static final FunctionType EMPTY = new FunctionType(EmptyFunction.CODEC);
    public static final FunctionType SET_NBT = new FunctionType(SetNbtFunction.CODEC);
    public static final FunctionType SET_COMPONENTS = new FunctionType(SetComponentsFunction.CODEC);
    public static final FunctionType SET_COUNT = new FunctionType(SetCountFunction.CODEC);
    public static final FunctionType SET_COLOR = new FunctionType(SetColorFunction.CODEC);
    public static final FunctionType SET_VELOCITY = new FunctionType(SetVelocityFunction.CODEC);
    public static final FunctionType SET_EQUIPMENT = new FunctionType(SetEquipmentFunction.CODEC);
    public static final FunctionType SET_DIRECTION = new FunctionType(SetDirectionFunction.CODEC);
    public static final FunctionType SET_ROTATION = new FunctionType(SetRotationFunction.CODEC);
    public static final FunctionType ADD_PASSENGER = new FunctionType(AddPassengerFunction.CODEC);
    public static final FunctionType ADD_STATUS_EFFECTS = new FunctionType(AddStatusEffectsFunction.CODEC);
    public static final FunctionType ADD_ENCHANTMENTS = new FunctionType(AddEnchantmentsFunction.CODEC);

    public static void init() {
        registerFunctionType(Identifier.of(LuckyBlockMod.NAMESPACE, "empty"), EMPTY);
        registerFunctionType(Identifier.of(LuckyBlockMod.NAMESPACE, "set_nbt"), SET_NBT);
        registerFunctionType(Identifier.of(LuckyBlockMod.NAMESPACE, "set_components"), SET_COMPONENTS);
        registerFunctionType(Identifier.of(LuckyBlockMod.NAMESPACE, "set_count"), SET_COUNT);
        registerFunctionType(Identifier.of(LuckyBlockMod.NAMESPACE, "set_color"), SET_COLOR);
        registerFunctionType(Identifier.of(LuckyBlockMod.NAMESPACE, "set_velocity"), SET_VELOCITY);
        registerFunctionType(Identifier.of(LuckyBlockMod.NAMESPACE, "set_equipment"), SET_EQUIPMENT);
        registerFunctionType(Identifier.of(LuckyBlockMod.NAMESPACE, "set_direction"), SET_DIRECTION);
        registerFunctionType(Identifier.of(LuckyBlockMod.NAMESPACE, "set_rotation"), SET_ROTATION);
        registerFunctionType(Identifier.of(LuckyBlockMod.NAMESPACE, "add_passenger"), ADD_PASSENGER);
        registerFunctionType(Identifier.of(LuckyBlockMod.NAMESPACE, "add_status_effects"), ADD_STATUS_EFFECTS);
        registerFunctionType(Identifier.of(LuckyBlockMod.NAMESPACE, "add_enchantments"), ADD_ENCHANTMENTS);
    }

    private static void registerFunctionType(Identifier id, FunctionType function) {
        Registry.register(LuckyBlockMod.FUNCTION_TYPES, id, function);
    }
}
