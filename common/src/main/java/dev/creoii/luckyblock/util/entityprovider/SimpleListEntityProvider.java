package dev.creoii.luckyblock.util.entityprovider;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.function.FunctionObjectCodecs;
import dev.creoii.luckyblock.function.wrapper.EntityWrapper;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SimpleListEntityProvider extends EntityProvider {
    public static final MapCodec<SimpleListEntityProvider> CODEC = FunctionObjectCodecs.ENTITY_WRAPPER.listOf().fieldOf("entities").xmap(SimpleListEntityProvider::new, provider -> provider.entities);
    private final List<EntityWrapper> entities;

    protected SimpleListEntityProvider(List<EntityWrapper> entities) {
        super(false);
        this.entities = entities;
    }

    protected EntityProviderType<?> getType() {
        return EntityProviderType.SIMPLE_LIST_ENTITY_PROVIDER;
    }

    @Override
    public @Nullable List<EntityWrapper> getEntities(Outcome.Context<?> context, Random random) {
        return entities;
    }
}
