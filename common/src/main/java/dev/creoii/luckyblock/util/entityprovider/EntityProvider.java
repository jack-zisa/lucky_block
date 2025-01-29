package dev.creoii.luckyblock.util.entityprovider;

import com.mojang.serialization.Codec;
import dev.creoii.luckyblock.LuckyBlockRegistries;
import dev.creoii.luckyblock.function.FunctionContainer;
import dev.creoii.luckyblock.function.wrapper.EntityWrapper;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.Provider;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class EntityProvider implements Provider<EntityProvider> {
    public static final Codec<EntityProvider> CODEC = LuckyBlockRegistries.ENTITY_PROVIDER_TYPE.getCodec().dispatch(EntityProvider::getType, EntityProviderType::codec);
    private final boolean singular;
    protected EntityProvider parent;

    public static SimpleEntityProvider of(Identifier id) {
        return new SimpleEntityProvider(new EntityWrapper(Registries.ENTITY_TYPE.get(id), FunctionContainer.EMPTY));
    }

    public static SimpleEntityProvider of(Entity entity) {
        return new SimpleEntityProvider(new EntityWrapper(entity, FunctionContainer.EMPTY));
    }

    public static SimpleEntityProvider of(EntityWrapper wrapper) {
        return new SimpleEntityProvider(wrapper);
    }

    public EntityProvider(boolean singular) {
        this.singular = singular;
    }

    @Override
    public void setParent(EntityProvider parent) {
        this.parent = parent;
    }

    @Override
    public EntityProvider getParent() {
        return parent;
    }

    protected abstract EntityProviderType<?> getType();

    public abstract @Nullable List<EntityWrapper> getEntities(Outcome.Context<?> context, Random random);
}