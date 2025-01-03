package dev.creoii.luckyblock.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class ReflectionUtils {
    public static List<Method> getPredicates(Class<?> clazz) {
        List<Method> methods = new ArrayList<>();

        for (Method method : clazz.getMethods()) {
            if (method.getReturnType() == boolean.class && method.getParameterCount() == 0) {
                methods.add(method);
            }
        }

        return methods;
    }

    /*public enum Args {
        NONE,
        BLOCK_POS(ArgType.BLOCK_POS);

        private final ArgType[] argumentTypes;

        Args(ArgType... argumentTypes) {
            this.argumentTypes = argumentTypes;
        }

        public ArgType[] getArgumentTypes() {
            return argumentTypes;
        }
    }

    public enum ArgType {
        BLOCK_POS,
        BLOCK_STATE,
        DOUBLE
    }*/
}
