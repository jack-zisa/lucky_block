package dev.creoii.luckyblock.function;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.LuckyBlockRegistries;
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
    public static final FunctionType SET_NAME = new FunctionType(SetNameFunction.CODEC);
    public static final FunctionType SET_ATTRIBUTE_MODIFIERS = new FunctionType(SetAttributeModifiersFunction.CODEC);
    public static final FunctionType ADD_PASSENGER = new FunctionType(AddPassengerFunction.CODEC);
    public static final FunctionType ADD_STATUS_EFFECTS = new FunctionType(AddStatusEffectsFunction.CODEC);
    public static final FunctionType ADD_ENCHANTMENTS = new FunctionType(AddEnchantmentsFunction.CODEC);
    public static final FunctionType INITIALIZE_MOBS = new FunctionType(InitializeMobsFunction.CODEC);
    public static final FunctionType SET_BABY = new FunctionType(SetBabyFunction.CODEC);
    public static final FunctionType SET_VARIANT = new FunctionType(SetVariantFunction.CODEC);
    public static final FunctionType SET_TARGET = new FunctionType(SetTargetFunction.CODEC);
    public static final FunctionType SET_OWNER = new FunctionType(SetOwnerFunction.CODEC);

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
        registerFunctionType(Identifier.of(LuckyBlockMod.NAMESPACE, "set_name"), SET_NAME);
        registerFunctionType(Identifier.of(LuckyBlockMod.NAMESPACE, "set_attribute_modifiers"), SET_ATTRIBUTE_MODIFIERS);
        registerFunctionType(Identifier.of(LuckyBlockMod.NAMESPACE, "add_passenger"), ADD_PASSENGER);
        registerFunctionType(Identifier.of(LuckyBlockMod.NAMESPACE, "add_status_effects"), ADD_STATUS_EFFECTS);
        registerFunctionType(Identifier.of(LuckyBlockMod.NAMESPACE, "add_enchantments"), ADD_ENCHANTMENTS);
        registerFunctionType(Identifier.of(LuckyBlockMod.NAMESPACE, "initialize_mobs"), INITIALIZE_MOBS);
        registerFunctionType(Identifier.of(LuckyBlockMod.NAMESPACE, "set_baby"), SET_BABY);
        registerFunctionType(Identifier.of(LuckyBlockMod.NAMESPACE, "set_variant"), SET_VARIANT);
        registerFunctionType(Identifier.of(LuckyBlockMod.NAMESPACE, "set_target"), SET_TARGET);
        registerFunctionType(Identifier.of(LuckyBlockMod.NAMESPACE, "set_owner"), SET_OWNER);
    }

    private static void registerFunctionType(Identifier id, FunctionType function) {
        Registry.register(LuckyBlockRegistries.FUNCTION_TYPES, id, function);
    }
}
