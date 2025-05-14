package dev.creoii.luckyblock.util;

import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.provider.integer.AddIntProvider;
import dev.creoii.luckyblock.util.provider.integer.DivIntProvider;
import dev.creoii.luckyblock.util.provider.integer.MulIntProvider;
import dev.creoii.luckyblock.util.provider.integer.SubIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;

public interface ContextualProvider<P> {
    P withContext(Outcome.Context context);

    Type getValueType();

    static IntProvider applyContext(IntProvider provider, Outcome.Context context) {
        if (provider instanceof ContextualProvider<?> contextualProvider && contextualProvider.getValueType() == ContextualProvider.Type.INT) {
            provider = (IntProvider) contextualProvider.withContext(context);
        }

        if (provider instanceof AddIntProvider addIntProvider) {
            applyContext(addIntProvider.a, context);
            applyContext(addIntProvider.b, context);
        }

        else if (provider instanceof SubIntProvider subIntProvider) {
            applyContext(subIntProvider.a, context);
            applyContext(subIntProvider.b, context);
        }

        else if (provider instanceof MulIntProvider mulIntProvider) {
            applyContext(mulIntProvider.a, context);
            applyContext(mulIntProvider.b, context);
        }

        else if (provider instanceof DivIntProvider divIntProvider) {
            applyContext(divIntProvider.a, context);
            applyContext(divIntProvider.b, context);
        }

        return provider;
    }

    enum Type {
        INT
    }
}
