package dev.creoii.luckyblock.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class ReflectionUtils {
    public static List<Method> getPredicates(Class<?> clazz) {
        List<Method> methods = new ArrayList<>();

        for (Method method : clazz.getMethods()) {
            if (method.getReturnType() == boolean.class && method.getParameterCount() == 0) {
                method.setAccessible(true);
                methods.add(method);
            }
        }

        return methods;
    }
}
