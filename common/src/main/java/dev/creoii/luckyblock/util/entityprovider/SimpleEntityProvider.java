package dev.creoii.luckyblock.util.entityprovider;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.function.FunctionObjectCodecs;
import dev.creoii.luckyblock.function.wrapper.EntityWrapper;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public class SimpleEntityProvider extends EntityProvider {
    public static final MapCodec<SimpleEntityProvider> CODEC = FunctionObjectCodecs.ENTITY_WRAPPER.fieldOf("entity").xmap(SimpleEntityProvider::new, provider -> provider.entity);
    private final EntityWrapper entity;

    protected SimpleEntityProvider(EntityWrapper entity) {
        this.entity = entity;
    }

    protected EntityProviderType<?> getType() {
        return EntityProviderType.SIMPLE_ENTITY_PROVIDER;
    }

    @Override
    @Nullable
    public EntityWrapper getEntity(Outcome.Context<?> context, Random random) {
        return entity;
    }
}
