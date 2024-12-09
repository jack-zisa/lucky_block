package dev.creoii.luckyblock.outcome;

import java.util.List;

public interface ContextInfo {
    /**
     * @return a list of objects that may be targeted by a function, such as block positions, entities, item stacks, etc.
     */
    List<Object> getTargets();
}
