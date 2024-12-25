package dev.creoii.luckyblock.util.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.function.target.FunctionTarget;
import dev.creoii.luckyblock.util.function.target.HasComponentsFunctionTarget;
import dev.creoii.luckyblock.util.function.target.Target;
import dev.creoii.luckyblock.util.function.wrapper.ItemStackWrapper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.IntProvider;

import java.util.Optional;

public class AddEnchantmentsFunction extends Function<Target<?>> {
    @SuppressWarnings("unchecked")
    public static final MapCodec<AddEnchantmentsFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(FunctionTarget.CODEC.fieldOf("target").orElse(HasComponentsFunctionTarget.INSTANCE).forGetter(Function::getTarget),
                Codec.unboundedMap(Identifier.CODEC, IntProvider.createValidatingCodec(1, 255)).xmap(Object2ObjectOpenHashMap::new, java.util.function.Function.identity()).fieldOf("enchantments").forGetter(function -> function.enchantments)
        ).apply(instance, (functionTarget, map) -> new AddEnchantmentsFunction((FunctionTarget<Target<?>>) functionTarget, map));
    });
    private final Object2ObjectOpenHashMap<Identifier, IntProvider> enchantments;

    protected AddEnchantmentsFunction(FunctionTarget<Target<?>> target, Object2ObjectOpenHashMap<Identifier, IntProvider> enchantments) {
        super(FunctionType.ADD_ENCHANTMENTS, Phase.POST, target);
        this.enchantments = enchantments;
    }

    @Override
    public void apply(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        for (Target<?> target : target.getTargets(outcome, context)) {
            if (target instanceof ItemStackWrapper wrapper) {
                enchantments.forEach((id, level) -> {
                    Optional<RegistryEntry.Reference<Enchantment>> optional = context.world().getRegistryManager().getOptionalEntry(RegistryKey.of(RegistryKeys.ENCHANTMENT, id));
                    optional.ifPresent(enchantmentReference -> wrapper.getStack().addEnchantment(enchantmentReference, level.get(context.random())));
                });
            }
        }
    }
}
