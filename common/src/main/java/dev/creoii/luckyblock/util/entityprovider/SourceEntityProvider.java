package dev.creoii.luckyblock.util.entityprovider;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.function.FunctionContainer;
import dev.creoii.luckyblock.function.wrapper.EntityWrapper;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public class SourceEntityProvider extends EntityProvider {
    private static final SourceEntityProvider DEFAULT = new SourceEntityProvider();
    public static final MapCodec<SourceEntityProvider> CODEC = MapCodec.unit(DEFAULT);

    protected EntityProviderType<?> getType() {
        return EntityProviderType.SOURCE_ENTITY_PROVIDER;
    }

    @Override
    @Nullable
    public EntityWrapper getEntity(Outcome.Context<?> context, Random random) {
        if (context.player() == null)
            return null;

        return new EntityWrapper(context.player(), FunctionContainer.EMPTY);
    }
}
