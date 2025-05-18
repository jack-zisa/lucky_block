package dev.creoii.luckyblock.util;

import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.provider.booleanprovider.BooleanProvider;
import dev.creoii.luckyblock.util.provider.floatprovider.*;
import dev.creoii.luckyblock.util.provider.intprovider.*;
import dev.creoii.luckyblock.util.provider.stringprovider.RandomStringProvider;
import dev.creoii.luckyblock.util.provider.stringprovider.StringProvider;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.intprovider.IntProvider;

public interface ContextualProvider<P> {
    P withContext(Outcome.Context context);

    Type getValueType();

    static IntProvider applyIntContext(IntProvider provider, Outcome.Context context) {
        if (provider instanceof ContextualProvider<?> contextualProvider && contextualProvider.getValueType() == Type.INT) {
            provider = (IntProvider) contextualProvider.withContext(context);
        }

        if (provider instanceof AddIntProvider addIntProvider) {
            applyIntContext(addIntProvider.a, context);
            applyIntContext(addIntProvider.b, context);
        }

        else if (provider instanceof SubIntProvider subIntProvider) {
            applyIntContext(subIntProvider.a, context);
            applyIntContext(subIntProvider.b, context);
        }

        else if (provider instanceof MulIntProvider mulIntProvider) {
            applyIntContext(mulIntProvider.a, context);
            applyIntContext(mulIntProvider.b, context);
        }

        else if (provider instanceof DivIntProvider divIntProvider) {
            applyIntContext(divIntProvider.a, context);
            applyIntContext(divIntProvider.b, context);
        }

        else if (provider instanceof ModIntProvider modIntProvider) {
            applyIntContext(modIntProvider.a, context);
            applyIntContext(modIntProvider.b, context);
        }

        else if (provider instanceof PowIntProvider powIntProvider) {
            applyIntContext(powIntProvider.a, context);
            applyIntContext(powIntProvider.b, context);
        }

        else if (provider instanceof AbsIntProvider absIntProvider) {
            applyIntContext(absIntProvider.value, context);
        }

        else if (provider instanceof ClampIntProvider clampIntProvider) {
            applyIntContext(clampIntProvider.value, context);
            applyIntContext(clampIntProvider.min, context);
            applyIntContext(clampIntProvider.max, context);
        }

        return provider;
    }

    static FloatProvider applyFloatContext(FloatProvider provider, Outcome.Context context) {
        if (provider instanceof ContextualProvider<?> contextualProvider && contextualProvider.getValueType() == Type.FLOAT) {
            provider = (FloatProvider) contextualProvider.withContext(context);
        }

        if (provider instanceof CbrtFloatProvider cbrtFloatProvider) {
            applyFloatContext(cbrtFloatProvider.value, context);
        }

        else if (provider instanceof CosFloatProvider cosFloatProvider) {
            applyFloatContext(cosFloatProvider.value, context);
        }

        else if (provider instanceof SinFloatProvider sinFloatProvider) {
            applyFloatContext(sinFloatProvider.value, context);
        }

        else if (provider instanceof SqrtFloatProvider sqrtFloatProvider) {
            applyFloatContext(sqrtFloatProvider.value, context);
        }

        else if (provider instanceof TanFloatProvider tanFloatProvider) {
            applyFloatContext(tanFloatProvider.value, context);
        }

        return provider;
    }

    static StringProvider applyStringContext(StringProvider provider, Outcome.Context context) {
        if (provider instanceof ContextualProvider<?> contextualProvider && contextualProvider.getValueType() == Type.FLOAT) {
            provider = (StringProvider) contextualProvider.withContext(context);
        }

        if (provider instanceof RandomStringProvider randomStringProvider) {
            randomStringProvider.values.forEach(stringProvider -> {
                applyStringContext(stringProvider, context);
            });
        }

        return provider;
    }

    static BooleanProvider applyBooleanContext(BooleanProvider provider, Outcome.Context context) {
        if (provider instanceof ContextualProvider<?> contextualProvider && contextualProvider.getValueType() == Type.FLOAT) {
            provider = (BooleanProvider) contextualProvider.withContext(context);
        }

        return provider;
    }

    enum Type {
        INT,
        FLOAT,
        STRING,
        BOOLEAN
    }
}
