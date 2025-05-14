package dev.creoii.luckyblock.util.provider.integer;

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
    public static final IntProviderType<WorldIntProvider> WORLD = () -> WorldIntProvider.CODEC;

    public static void register() {
    }
}
