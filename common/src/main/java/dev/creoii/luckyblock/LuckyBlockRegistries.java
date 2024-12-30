package dev.creoii.luckyblock;

import com.mojang.serialization.Lifecycle;
import dev.creoii.luckyblock.function.FunctionType;
import dev.creoii.luckyblock.function.target.FunctionTargetType;
import dev.creoii.luckyblock.outcome.OutcomeType;
import dev.creoii.luckyblock.util.colorprovider.ColorProviderType;
import dev.creoii.luckyblock.util.shape.ShapeType;
import dev.creoii.luckyblock.util.stackprovider.ItemStackProviderType;
import dev.creoii.luckyblock.util.textprovider.TextProviderType;
import dev.creoii.luckyblock.util.vecprovider.VecProviderType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleDefaultedRegistry;
import net.minecraft.util.Identifier;

public class LuckyBlockRegistries {
    public static final RegistryKey<Registry<OutcomeType>> OUTCOME_TYPES_KEY = RegistryKey.ofRegistry(Identifier.of(LuckyBlockMod.NAMESPACE, "outcome_types"));
    public static final Registry<OutcomeType> OUTCOME_TYPES = new SimpleDefaultedRegistry<>("lucky:none", OUTCOME_TYPES_KEY, Lifecycle.stable(), false);

    public static final RegistryKey<Registry<ItemStackProviderType<?>>> ITEM_STACK_PROVIDER_TYPE_KEY = RegistryKey.ofRegistry(Identifier.of(LuckyBlockMod.NAMESPACE, "item_stack_provider_type"));
    public static final Registry<ItemStackProviderType<?>> ITEM_STACK_PROVIDER_TYPE = new SimpleDefaultedRegistry<>("lucky:simple_stack_provider", ITEM_STACK_PROVIDER_TYPE_KEY, Lifecycle.stable(), false);

    public static final RegistryKey<Registry<TextProviderType<?>>> TEXT_PROVIDER_TYPE_KEY = RegistryKey.ofRegistry(Identifier.of(LuckyBlockMod.NAMESPACE, "text_provider_type"));
    public static final Registry<TextProviderType<?>> TEXT_PROVIDER_TYPE = new SimpleDefaultedRegistry<>("lucky:simple_text_provider", TEXT_PROVIDER_TYPE_KEY, Lifecycle.stable(), false);

    public static final RegistryKey<Registry<ColorProviderType<?>>> COLOR_PROVIDER_TYPE_KEY = RegistryKey.ofRegistry(Identifier.of(LuckyBlockMod.NAMESPACE, "color_provider_type"));
    public static final Registry<ColorProviderType<?>> COLOR_PROVIDER_TYPE = new SimpleDefaultedRegistry<>("lucky:simple_color_provider", COLOR_PROVIDER_TYPE_KEY, Lifecycle.stable(), false);

    public static final RegistryKey<Registry<FunctionType>> FUNCTION_TYPES_KEY = RegistryKey.ofRegistry(Identifier.of(LuckyBlockMod.NAMESPACE, "function_types"));
    public static final Registry<FunctionType> FUNCTION_TYPES = new SimpleDefaultedRegistry<>("lucky:empty", FUNCTION_TYPES_KEY, Lifecycle.stable(), false);

    public static final RegistryKey<Registry<FunctionTargetType>> FUNCTION_TARGET_TYPES_KEY = RegistryKey.ofRegistry(Identifier.of(LuckyBlockMod.NAMESPACE, "function_target_types"));
    public static final Registry<FunctionTargetType> FUNCTION_TARGET_TYPES = new SimpleDefaultedRegistry<>("lucky:default", FUNCTION_TARGET_TYPES_KEY, Lifecycle.stable(), false);

    public static final RegistryKey<Registry<ShapeType>> SHAPE_TYPES_KEY = RegistryKey.ofRegistry(Identifier.of(LuckyBlockMod.NAMESPACE, "shape_types"));
    public static final Registry<ShapeType> SHAPE_TYPES = new SimpleDefaultedRegistry<>("lucky:empty", SHAPE_TYPES_KEY, Lifecycle.stable(), false);

    public static final RegistryKey<Registry<VecProviderType<?>>> POS_PROVIDER_TYPES_KEY = RegistryKey.ofRegistry(Identifier.of(LuckyBlockMod.NAMESPACE, "pos_provider_types"));
    public static final Registry<VecProviderType<?>> POS_PROVIDER_TYPES = new SimpleDefaultedRegistry<>("lucky:zero", POS_PROVIDER_TYPES_KEY, Lifecycle.stable(), false);
}
