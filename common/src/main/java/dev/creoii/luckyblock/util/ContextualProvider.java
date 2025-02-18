package dev.creoii.luckyblock.util;

import dev.creoii.luckyblock.outcome.Outcome;

public interface ContextualProvider<P> {
    P withContext(Outcome.Context context);
}
