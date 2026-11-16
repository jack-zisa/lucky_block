package dev.creoii.luckyblock.util.provider.intprovider;

import dev.creoii.luckyblock.LuckyBlockMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

public class LuckyIntProviderTypes {
    public static void register() {
        Registry.register(Registries.INT_PROVIDER_TYPE, Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "add"), AddIntProvider.CODEC);
        Registry.register(Registries.INT_PROVIDER_TYPE, Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "sub"), SubIntProvider.CODEC);
        Registry.register(Registries.INT_PROVIDER_TYPE, Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "mul"), MulIntProvider.CODEC);
        Registry.register(Registries.INT_PROVIDER_TYPE, Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "div"), DIV);
        Registry.register(Registries.INT_PROVIDER_TYPE, Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "mod"), MOD);
        Registry.register(Registries.INT_PROVIDER_TYPE, Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "pow"), POW);
        Registry.register(Registries.INT_PROVIDER_TYPE, Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "clamp"), CLAMP);
        Registry.register(Registries.INT_PROVIDER_TYPE, Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "abs"), ABS);
        Registry.register(Registries.INT_PROVIDER_TYPE, Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "world"), WORLD);
    }
}
