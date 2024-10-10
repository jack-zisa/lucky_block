package dev.creoii.luckyblock.util;

import com.google.common.collect.ImmutableMap;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.outcome.OutcomeContext;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

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
            .put("randomVelocity", (args, context) -> {
                if (args.length != 2 && args.length != 0)
                    return "0";

                double power;
                double pitch;
                if (args.length == 0) {
                    power = .9d;
                    pitch = 15d;
                } else {
                    double[] numbers = new double[args.length];
                    for (int i = 0; i < args.length; i++) {
                        numbers[i] = context.evaluateExpression(args[i].trim());
                    }
                    power = numbers[0];
                    pitch = numbers[1];
                }

                float yawRad = (float) Math.toRadians(context.world().getRandom().nextBetween(-180, 180));
                float pitchRad = (float) Math.toRadians(-90d + context.world().getRandom().nextBetween((int) -pitch, (int) pitch));

                Vec3d motion = new Vec3d(-MathHelper.sin(yawRad) * MathHelper.cos(pitchRad) * power, -MathHelper.sin(pitchRad) * power, MathHelper.cos(yawRad) * MathHelper.cos(pitchRad) * power);
                return motion.x + "," + motion.y + "," + motion.z;
            })
            .build();

    public static String processFunctions(String expression, OutcomeContext context) {
        for (int i = 1; i < expression.length(); ++i) {
            if (expression.charAt(i - 1) == '{') {
                int end = expression.indexOf("}", i);
                String function;
                try {
                    function = expression.substring(i, end);
                } catch (IndexOutOfBoundsException e) {
                    LuckyBlockMod.LOGGER.error("No enclosing parenthesis found in expression '{}'", expression);
                    throw e;
                }

                if (function.contains("(")) {
                    int k = expression.indexOf('(', i);
                    String functionId = expression.substring(i, k);
                    if (FUNCTIONS.containsKey(functionId)) {
                        String arguments = expression.substring(k + 1, expression.indexOf(")", k + 1));
                        return expression.substring(0, Math.max(0, i - 2)) + FUNCTIONS.get(functionId).apply(arguments.split(","), context) + expression.substring(end + 1);
                    } else throw new IllegalArgumentException("Unknown function: '" + functionId + "'");
                }
            }
        }
        return expression;
    }
}
