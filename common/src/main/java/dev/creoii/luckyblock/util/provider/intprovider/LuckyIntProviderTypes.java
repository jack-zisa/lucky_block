package dev.creoii.luckyblock.util.provider.intprovider;

import dev.creoii.luckyblock.LuckyBlockMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.IntProviderType;

public class LuckyIntProviderTypes {
    public static final IntProviderType<AddIntProvider> ADD = () -> AddIntProvider.CODEC;
    public static final IntProviderType<SubIntProvider> SUB = () -> SubIntProvider.CODEC;
    public static final IntProviderType<MulIntProvider> MUL = () -> MulIntProvider.CODEC;
    public static final IntProviderType<DivIntProvider> DIV = () -> DivIntProvider.CODEC;
    public static final IntProviderType<ModIntProvider> MOD = () -> ModIntProvider.CODEC;
    public static final IntProviderType<PowIntProvider> POW = () -> PowIntProvider.CODEC;
    public static final IntProviderType<ClampIntProvider> CLAMP = () -> ClampIntProvider.CODEC;
    public static final IntProviderType<AbsIntProvider> ABS = () -> AbsIntProvider.CODEC;
    public static final IntProviderType<WorldIntProvider> WORLD = () -> WorldIntProvider.CODEC;

    public static void register() {
        Registry.register(Registries.INT_PROVIDER_TYPE, Identifier.of(LuckyBlockMod.NAMESPACE, "add"), ADD);
        Registry.register(Registries.INT_PROVIDER_TYPE, Identifier.of(LuckyBlockMod.NAMESPACE, "sub"), SUB);
        Registry.register(Registries.INT_PROVIDER_TYPE, Identifier.of(LuckyBlockMod.NAMESPACE, "mul"), MUL);
        Registry.register(Registries.INT_PROVIDER_TYPE, Identifier.of(LuckyBlockMod.NAMESPACE, "div"), DIV);
        Registry.register(Registries.INT_PROVIDER_TYPE, Identifier.of(LuckyBlockMod.NAMESPACE, "mod"), MOD);
        Registry.register(Registries.INT_PROVIDER_TYPE, Identifier.of(LuckyBlockMod.NAMESPACE, "pow"), POW);
        Registry.register(Registries.INT_PROVIDER_TYPE, Identifier.of(LuckyBlockMod.NAMESPACE, "clamp"), CLAMP);
        Registry.register(Registries.INT_PROVIDER_TYPE, Identifier.of(LuckyBlockMod.NAMESPACE, "abs"), ABS);
        Registry.register(Registries.INT_PROVIDER_TYPE, Identifier.of(LuckyBlockMod.NAMESPACE, "world"), WORLD);
    }
}
