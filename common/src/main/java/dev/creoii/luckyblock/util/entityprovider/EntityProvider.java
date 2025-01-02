package dev.creoii.luckyblock.util.entityprovider;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.creoii.luckyblock.LuckyBlockRegistries;
import dev.creoii.luckyblock.function.FunctionContainer;
import dev.creoii.luckyblock.function.wrapper.EntityWrapper;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public abstract class EntityProvider {
    public static final Codec<EntityProvider> TYPE_CODEC = LuckyBlockRegistries.ENTITY_PROVIDER_TYPE.getCodec().dispatch(EntityProvider::getType, EntityProviderType::codec);
    public static final Codec<EntityProvider> CODEC = Codec.either(Identifier.CODEC, TYPE_CODEC).xmap(either -> {
        return either.map(EntityProvider::of, Function.identity());
    }, Either::right);

    public static SimpleEntityProvider of(Identifier id) {
        return new SimpleEntityProvider(new EntityWrapper(Registries.ENTITY_TYPE.get(id), FunctionContainer.EMPTY));
    }

    public static SimpleEntityProvider of(Entity entity) {
        return new SimpleEntityProvider(new EntityWrapper(entity, FunctionContainer.EMPTY));
    }

    public static SimpleEntityProvider of(EntityWrapper wrapper) {
        return new SimpleEntityProvider(wrapper);
    }

    protected abstract EntityProviderType<?> getType();

    @Nullable
    public abstract EntityWrapper getEntity(Outcome.Context<?> context, Random random);
}