package dev.creoii.luckyblock.util.provider.floatprovider;

import dev.creoii.luckyblock.LuckyBlockMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.floatprovider.FloatProviderType;

public class LuckyFloatProviderTypes {
    public static final FloatProviderType<SinFloatProvider> SIN = () -> SinFloatProvider.CODEC;
    public static final FloatProviderType<CosFloatProvider> COS = () -> CosFloatProvider.CODEC;
    public static final FloatProviderType<TanFloatProvider> TAN = () -> TanFloatProvider.CODEC;
    public static final FloatProviderType<SqrtFloatProvider> SQRT = () -> SqrtFloatProvider.CODEC;
    public static final FloatProviderType<CbrtFloatProvider> CBRT = () -> CbrtFloatProvider.CODEC;
    public static final FloatProviderType<WorldFloatProvider> WORLD = () -> WorldFloatProvider.CODEC;

    public static void register() {
        Registry.register(Registries.FLOAT_PROVIDER_TYPE, Identifier.of(LuckyBlockMod.NAMESPACE, "sin"), SIN);
        Registry.register(Registries.FLOAT_PROVIDER_TYPE, Identifier.of(LuckyBlockMod.NAMESPACE, "cos"), COS);
        Registry.register(Registries.FLOAT_PROVIDER_TYPE, Identifier.of(LuckyBlockMod.NAMESPACE, "tan"), TAN);
        Registry.register(Registries.FLOAT_PROVIDER_TYPE, Identifier.of(LuckyBlockMod.NAMESPACE, "sqrt"), SQRT);
        Registry.register(Registries.FLOAT_PROVIDER_TYPE, Identifier.of(LuckyBlockMod.NAMESPACE, "cbrt"), CBRT);
        Registry.register(Registries.FLOAT_PROVIDER_TYPE, Identifier.of(LuckyBlockMod.NAMESPACE, "world"), WORLD);
    }
}
