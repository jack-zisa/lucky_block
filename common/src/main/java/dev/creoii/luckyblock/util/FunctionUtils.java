package dev.creoii.luckyblock.util;

import com.google.common.collect.ImmutableMap;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.outcome.OutcomeContext;

import java.util.Map;
import java.util.function.BiFunction;

public class FunctionUtils {
    private static final Map<String, BiFunction<String[], OutcomeContext, String>> FUNCTIONS = new ImmutableMap.Builder<String, BiFunction<String[], OutcomeContext, String>>()
            .put("random", (args, context) -> {
                if (args.length == 0)
                    return String.valueOf(context.world().getRandom().nextInt());

                double[] numbers = new double[args.length];
                for (int i = 0; i < args.length; i++) {
                    numbers[i] = context.evaluateExpression(args[i].trim());
                }

                return String.valueOf(numbers[context.world().getRandom().nextInt(numbers.length)]);
            })
            .put("randomBetween", (args, context) -> {
                if (args.length != 2)
                    throw new IllegalArgumentException("Function 'randomBetween' requires 2 arguments, found " + args.length);

                double[] numbers = new double[args.length];
                for (int i = 0; i < args.length; i++) {
                    numbers[i] = context.evaluateExpression(args[i].trim());
                }

                return String.valueOf(context.world().getRandom().nextBetween((int) numbers[0], (int) numbers[1]));
            })
            .build();

    public static String processFunctions(String expression, OutcomeContext context) {
        for (int i = 0; i < expression.length(); ++i) {
            if (expression.charAt(i) == '{') {
                int j = i + 1;
                int end = expression.indexOf("}", j);
                String function;
                try {
                    function = expression.substring(j, end);
                } catch (IndexOutOfBoundsException e) {
                    LuckyBlockMod.LOGGER.error("No enclosing parenthesis found in expression '{}'", expression);
                    throw e;
                }

                if (function.contains("(")) {
                    int k = expression.indexOf('(', j);
                    String functionId = expression.substring(j, k);
                    if (FUNCTIONS.containsKey(functionId)) {
                        String arguments = expression.substring(k + 1, expression.indexOf(")", k + 1));
                        return expression.substring(0, Math.max(0, i - 1)) + FUNCTIONS.get(functionId).apply(arguments.split(","), context) + expression.substring(end + 1);
                    } else throw new IllegalArgumentException("Unknown function: " + functionId);
                }
            }
        }
        return expression;
    }
}
