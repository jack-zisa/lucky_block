package dev.creoii.luckyblock.util.function.target;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.LuckyBlockMod;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * TODO: all_of, tintable items/blocks/entities, growable blocks/entities, breedable entities, tameable entities
 */
public record FunctionTargetType(MapCodec<? extends FunctionTarget<?>> codec) {
    public static final FunctionTargetType NONE = new FunctionTargetType(NoneFunctionTarget.CODEC);
    public static final FunctionTargetType HAS_NBT = new FunctionTargetType(HasNbtFunctionTarget.CODEC);
    public static final FunctionTargetType HAS_COMPONENTS = new FunctionTargetType(HasComponentsFunctionTarget.CODEC);
    public static final FunctionTargetType HAS_STATUS_EFFECTS = new FunctionTargetType(HasStatusEffectsFunctionTarget.CODEC);
    public static final FunctionTargetType HAS_COUNT = new FunctionTargetType(HasCountFunctionTarget.CODEC);
    public static final FunctionTargetType HAS_COLOR = new FunctionTargetType(HasColorFunctionTarget.CODEC);
    public static final FunctionTargetType HAS_NAME = new FunctionTargetType(HasNameFunctionTarget.CODEC);
    public static final FunctionTargetType HAS_EQUIPMENT = new FunctionTargetType(HasEquipmentFunctionTarget.CODEC);
    public static final FunctionTargetType IS_ENTITY = new FunctionTargetType(IsEntityFunctionTarget.CODEC);
    public static final FunctionTargetType IS_ITEM_STACK = new FunctionTargetType(IsItemStackFunctionTarget.CODEC);
    public static final FunctionTargetType HAS_DIRECTION = new FunctionTargetType(HasDirectionFunctionTarget.CODEC);
    public static final FunctionTargetType HAS_ROTATION = new FunctionTargetType(HasRotationFunctionTarget.CODEC);
    public static final FunctionTargetType MATCHING = new FunctionTargetType(MatchingFunctionTarget.CODEC);
    public static final FunctionTargetType RANDOM_COUNT = new FunctionTargetType(RandomCountFunctionTarget.CODEC);

    public static void init() {
        registerFunctionTargetType(Identifier.of(LuckyBlockMod.NAMESPACE, "none"), NONE);
        registerFunctionTargetType(Identifier.of(LuckyBlockMod.NAMESPACE, "has_nbt"), HAS_NBT);
        registerFunctionTargetType(Identifier.of(LuckyBlockMod.NAMESPACE, "has_components"), HAS_COMPONENTS);
        registerFunctionTargetType(Identifier.of(LuckyBlockMod.NAMESPACE, "has_status_effects"), HAS_STATUS_EFFECTS);
        registerFunctionTargetType(Identifier.of(LuckyBlockMod.NAMESPACE, "has_count"), HAS_COUNT);
        registerFunctionTargetType(Identifier.of(LuckyBlockMod.NAMESPACE, "has_color"), HAS_COLOR);
        registerFunctionTargetType(Identifier.of(LuckyBlockMod.NAMESPACE, "has_name"), HAS_NAME);
        registerFunctionTargetType(Identifier.of(LuckyBlockMod.NAMESPACE, "has_equipment"), HAS_EQUIPMENT);
        registerFunctionTargetType(Identifier.of(LuckyBlockMod.NAMESPACE, "has_direction"), HAS_DIRECTION);
        registerFunctionTargetType(Identifier.of(LuckyBlockMod.NAMESPACE, "is_entity"), IS_ENTITY);
        registerFunctionTargetType(Identifier.of(LuckyBlockMod.NAMESPACE, "is_item_stack"), IS_ITEM_STACK);
        registerFunctionTargetType(Identifier.of(LuckyBlockMod.NAMESPACE, "has_rotation"), HAS_ROTATION);
        registerFunctionTargetType(Identifier.of(LuckyBlockMod.NAMESPACE, "matching"), MATCHING);
        registerFunctionTargetType(Identifier.of(LuckyBlockMod.NAMESPACE, "random_count"), RANDOM_COUNT);
    }

    private static void registerFunctionTargetType(Identifier id, FunctionTargetType functionTarget) {
        Registry.register(LuckyBlockMod.FUNCTION_TARGET_TYPES, id, functionTarget);
    }
}
