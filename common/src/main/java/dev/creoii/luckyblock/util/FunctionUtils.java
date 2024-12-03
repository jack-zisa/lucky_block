package dev.creoii.luckyblock.util;

import com.ezylang.evalex.Expression;
import com.google.common.collect.ImmutableMap;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.Direction;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionUtils {
    private static final Pattern PARAM_PATTERN = Pattern.compile("\\{(\\w+)}");
    private static final Pattern MATH_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?([*/+-]\\d+(\\.\\d+)?)+");
    private static final List<String> COLORS = List.of("brown", "red", "orange", "yellow", "lime", "green", "cyan", "blue", "light_blue", "pink", "magenta", "purple", "black", "gray", "light_gray", "white");
    private static final List<String> WOODS = List.of("oak", "spruce", "birch", "jungle", "dark_oak", "acacia", "mangrove", "cherry");
    public static final Map<String, Function<Outcome.Context, String>> STRING_PARAMS = new ImmutableMap.Builder<String, Function<Outcome.Context, String>>()
            .put("playerName", context -> context.player() == null ? "" : context.player().getGameProfile().getName())
            .put("playerUUID", context -> context.player() == null ? "" : String.valueOf(context.player().getUuidAsString()))
            .put("playerDirection", context -> context.player() == null ? "" : String.valueOf(context.player().getFacing().asString()))
            .put("playerHorizontalDirection", context -> context.player() == null ? "" : String.valueOf(context.player().getHorizontalFacing().asString()))
            .put("randomDirection", context -> String.valueOf(Direction.random(context.world().getRandom())))
            .put("randomHorizontalDirection", context -> String.valueOf(Direction.Type.HORIZONTAL.random(context.world().getRandom())))
            .put("randomDyeColor", context -> String.valueOf(COLORS.get(context.world().getRandom().nextInt(COLORS.size()))))
            .put("randomDye", context -> String.valueOf(COLORS.get(context.world().getRandom().nextInt(COLORS.size()))))
            .put("randomWoodType", context -> String.valueOf(WOODS.get(context.world().getRandom().nextInt(WOODS.size()))))
            .put("randomWood", context -> String.valueOf(WOODS.get(context.world().getRandom().nextInt(WOODS.size()))))
            .put("randomAxis", context -> String.valueOf(Direction.Axis.pickRandomAxis(context.world().getRandom())))
            .put("randomBlock", context -> String.valueOf(Registries.BLOCK.getRandom(context.world().getRandom()).get().getIdAsString()))
            .put("randomItem", context -> String.valueOf(Registries.ITEM.getRandom(context.world().getRandom()).get().getIdAsString()))
            .put("randomEntityType", context -> String.valueOf(Registries.ENTITY_TYPE.getRandom(context.world().getRandom()).get().getIdAsString()))
            .build();
    public static final Map<String, Function<Outcome.Context, Integer>> INT_PARAMS = new ImmutableMap.Builder<String, Function<Outcome.Context, Integer>>()
            .put("playerPosX", context -> context.player() == null ? context.pos().getX() : context.player().getBlockX())
            .put("playerPosY", context -> context.player() == null ? context.pos().getY() : context.player().getBlockY())
            .put("playerPosZ", context -> context.player() == null ? context.pos().getZ() : context.player().getBlockZ())
            .put("blockPosX", context -> context.pos().getX())
            .put("blockPosY", context -> context.pos().getY())
            .put("blockPosZ", context -> context.pos().getZ())
            .put("randomRGBColor", context -> context.world().getRandom().nextInt(16777215))
            .build();
    public static final Map<String, Function<Outcome.Context, Double>> DOUBLE_PARAMS = new ImmutableMap.Builder<String, Function<Outcome.Context, Double>>()
            .put("playerVecX", context -> context.player() == null ? context.pos().toCenterPos().getX() : context.player().getX())
            .put("playerVecY", context -> context.player() == null ? context.pos().toCenterPos().getY() : context.player().getY())
            .put("playerVecZ", context -> context.player() == null ? context.pos().toCenterPos().getZ() : context.player().getZ())
            .put("playerX", context -> context.player() == null ? context.pos().toCenterPos().getX() : context.player().getX())
            .put("playerY", context -> context.player() == null ? context.pos().toCenterPos().getY() : context.player().getY())
            .put("playerZ", context -> context.player() == null ? context.pos().toCenterPos().getZ() : context.player().getZ())
            .put("blockVecX", context -> context.pos().toCenterPos().getX())
            .put("blockVecY", context -> context.pos().toCenterPos().getY())
            .put("blockVecZ", context -> context.pos().toCenterPos().getZ())
            .put("blockX", context -> context.pos().toCenterPos().getX())
            .put("blockY", context -> context.pos().toCenterPos().getY())
            .put("blockZ", context -> context.pos().toCenterPos().getZ())
            .put("playerDistance", context -> context.player() == null ? 0d : context.player().getPos().distanceTo(context.pos().toCenterPos()))
            .put("playerSquaredDistance", context -> context.player() == null ? 0d : context.player().getPos().squaredDistanceTo(context.pos().toCenterPos()))
            .put("playerDistance2", context -> context.player() == null ? 0d : context.player().getPos().squaredDistanceTo(context.pos().toCenterPos()))
            .put("playerPitch", context -> context.player() == null ? 0d : context.player().getPitch())
            .put("playerYaw", context -> context.player() == null ? 0d : context.player().getYaw())
            .build();

    /**
     * @param string a json object in string format
     * @return the string with all parameters and functions replaced with their values, based on the context
     */
    public static String parseString(String string, Outcome.Context context) {
        StringBuilder result = new StringBuilder();
        Matcher matcher = PARAM_PATTERN.matcher(string);

        while (matcher.find()) {
            String param = matcher.group(1);

            if (FunctionUtils.STRING_PARAMS.containsKey(param)) {
                String replacement = FunctionUtils.STRING_PARAMS.get(param).apply(context);
                matcher.appendReplacement(result, replacement);
            } else if (FunctionUtils.DOUBLE_PARAMS.containsKey(param)) {
                Number numberValue = FunctionUtils.DOUBLE_PARAMS.get(param).apply(context);
                matcher.appendReplacement(result, String.valueOf(numberValue));
            } else if (FunctionUtils.INT_PARAMS.containsKey(param)) {
                Number numberValue = FunctionUtils.INT_PARAMS.get(param).apply(context);
                matcher.appendReplacement(result, String.valueOf(numberValue));
            } else throw new IllegalArgumentException("Error parsing param '" + param + "'");
        }

        String parsed = matcher.appendTail(result).toString();
        return replaceIgnoreProperties(parsed);
    }

    private static String replaceIgnoreProperties(String string) {
        String parsed = string.replaceAll("(\"Properties\"\\s*:\\s*\\{[^}]*?)\"([\\-\\d.]+)\"", "$1@@$2@@");
        parsed = evaluateExpressions(parsed.replaceAll("\"([\\-\\d.]+)\"", "$1"));
        return parsed.replaceAll("@@([\\-\\d.]+)@@", "\"$1\"");
    }

    private static String evaluateExpressions(String input) {
        Matcher matcher = MATH_PATTERN.matcher(input);

        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            try {
                matcher.appendReplacement(result, new Expression(matcher.group()).evaluate().getStringValue());
            } catch (Exception e) {
                throw new IllegalArgumentException("Error evaluating math expression: " + matcher.group(), e);
            }
        }
        return matcher.appendTail(result).toString().replaceAll("\"([\\-\\d\\.]+)\"", "$1");
    }
}
