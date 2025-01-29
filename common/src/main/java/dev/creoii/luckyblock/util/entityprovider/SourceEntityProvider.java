package dev.creoii.luckyblock.util.entityprovider;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.function.FunctionContainer;
import dev.creoii.luckyblock.function.wrapper.EntityWrapper;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SourceEntityProvider extends EntityProvider {
    private static final SourceEntityProvider DEFAULT = new SourceEntityProvider();
    public static final MapCodec<SourceEntityProvider> CODEC = MapCodec.unit(DEFAULT);

    public SourceEntityProvider() {
        super(false);
    }

    protected EntityProviderType<?> getType() {
        return EntityProviderType.SOURCE_ENTITY_PROVIDER;
    }

    @Override
    public @Nullable List<EntityWrapper> getEntities(Outcome.Context<?> context, Random random) {
        if (context.source() == null)
            return null;

        return List.of(new EntityWrapper(context.source(), FunctionContainer.EMPTY));
    }
}
