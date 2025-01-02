package dev.creoii.luckyblock.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.function.target.FunctionTarget;
import dev.creoii.luckyblock.function.target.HasNameFunctionTarget;
import dev.creoii.luckyblock.function.target.Target;
import dev.creoii.luckyblock.function.wrapper.EntityWrapper;
import dev.creoii.luckyblock.function.wrapper.ItemStackWrapper;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.List;

/**
 * Todo: wrapper for attribute modifier to allow float providers for easy random values
 */
public class SetAttributeModifiersFunction extends Function<Target<?>> {
    @SuppressWarnings("unchecked")
    public static final MapCodec<SetAttributeModifiersFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(FunctionTarget.CODEC.fieldOf("target").orElse(HasNameFunctionTarget.INSTANCE).forGetter(Function::getTarget),
                AttributeModifiersComponent.Entry.CODEC.listOf().fieldOf("modifiers").forGetter(function -> function.modifiers)
        ).apply(instance, (functionTarget, map) -> new SetAttributeModifiersFunction((FunctionTarget<Target<?>>) functionTarget, map));
    });
    private final List<AttributeModifiersComponent.Entry> modifiers;

    protected SetAttributeModifiersFunction(FunctionTarget<Target<?>> target, List<AttributeModifiersComponent.Entry> modifiers) {
        super(FunctionType.SET_ATTRIBUTE_MODIFIERS, Phase.POST, target);
        this.modifiers = modifiers;
    }

    @Override
    public void apply(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        for (Target<?> target : target.getTargets(outcome, context)) {
            if (target instanceof ItemStackWrapper wrapper && wrapper.getStack().get(DataComponentTypes.ATTRIBUTE_MODIFIERS) != null) {
                modifiers.forEach(wrapper.getStack().get(DataComponentTypes.ATTRIBUTE_MODIFIERS).modifiers()::add);
            } else if (target instanceof EntityWrapper wrapper && wrapper.getEntity() instanceof LivingEntity living) {
                modifiers.forEach(modifier -> {
                    EntityAttributeInstance instance = living.getAttributeInstance(modifier.attribute());

                    if (instance != null) {
                        MutableBoolean bl = new MutableBoolean(true);

                        instance.getModifiers().forEach(modifier1 -> {
                            if (modifier.matches(instance.getAttribute(), modifier1.id())) {
                                bl.setFalse();
                            }
                        });

                        if (bl.booleanValue()) {
                            instance.addPersistentModifier(modifier.modifier());
                        }
                    }
                });
            }
        }
    }
}
