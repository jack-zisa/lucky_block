package dev.creoii.luckyblock.util.booleanprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.function.wrapper.EntityWrapper;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.ReflectionUtils;
import dev.creoii.luckyblock.util.entityprovider.EntityProvider;
import net.minecraft.util.math.random.Random;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EntityBooleanProvider extends BooleanProvider {
    public static final MapCodec<EntityBooleanProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(EntityProvider.CODEC.fieldOf("entity").forGetter(provider -> provider.entity),
                Codec.STRING.fieldOf("value").forGetter(provider -> provider.value)
        ).apply(instance, EntityBooleanProvider::new);
    });
    private final EntityProvider entity;
    private final String value;

    protected EntityBooleanProvider(EntityProvider entity, String value) {
        this.entity = entity;
        this.value = value;
    }

    protected BooleanProviderType<?> getType() {
        return BooleanProviderType.ENTITY_BOOLEAN_PROVIDER;
    }

    @Override
    public boolean getBoolean(Outcome.Context<?> context, Random random) {
        EntityWrapper wrapper = entity.getEntity(context, random);
        if (wrapper == null) {
            return false;
        }

        if (wrapper.getEntity() == null) {
            wrapper = wrapper.init(context);
        }

        for (Method method : ReflectionUtils.getPredicates(wrapper.getEntity().getClass())) {
            if (value.equals(method.getName())) {
                try {
                    return (boolean) method.invoke(wrapper.getEntity());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new IllegalArgumentException("No method " + value + " found in class " + wrapper.getEntity().getClass().getSimpleName());
                }
            }
        }
        return false;
    }
}
