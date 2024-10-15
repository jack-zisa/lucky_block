package dev.creoii.luckyblock.util;

import com.google.common.collect.ImmutableMap;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionUtils {
    private static final Pattern PARAM_PATTERN = Pattern.compile("\\{(\\w+)}");
    private static final List<String> COLORS = List.of("brown", "red", "orange", "yellow", "lime", "green", "cyan", "blue", "light_blue", "pink", "magenta", "purple", "black", "gray", "light_gray", "white");
    private static final List<String> WOODS = List.of("oak", "spruce", "birch", "jungle", "dark_oak", "acacia", "mangrove", "cherry");
    public static final Map<String, Function<Outcome.Context, String>> PARAMS = new ImmutableMap.Builder<String, Function<Outcome.Context, String>>()
            .put("playerName", context -> context.player() == null ? "" : context.player().getGameProfile().getName())
            .put("playerPos", context -> context.player() == null ? context.pos().getX() + " " + context.pos().getY() + " " + context.pos().getZ() : context.player().getBlockX() + " " + context.player().getBlockY() + " " + context.player().getBlockZ())
            .put("playerPosX", context -> context.player() == null ? String.valueOf(context.pos().getX()) : String.valueOf(context.player().getBlockX()))
            .put("playerPosY", context -> context.player() == null ? String.valueOf(context.pos().getY()) : String.valueOf(context.player().getBlockY()))
            .put("playerPosZ", context -> context.player() == null ? String.valueOf(context.pos().getZ()) : String.valueOf(context.player().getBlockZ()))
            .put("playerVec", context -> {
                Vec3d fallback = context.pos().toCenterPos();
                return context.player() == null ? fallback.x + " " + fallback.y + " " + fallback.z : context.player().getX() + " " + context.player().getY() + " " + context.player().getZ();
            })
            .put("playerVecX", context -> context.player() == null ? String.valueOf(context.pos().toCenterPos().getX()) : String.valueOf(context.player().getX()))
            .put("playerVecY", context -> context.player() == null ? String.valueOf(context.pos().toCenterPos().getY()) : String.valueOf(context.player().getY()))
            .put("playerVecZ", context -> context.player() == null ? String.valueOf(context.pos().toCenterPos().getZ()) : String.valueOf(context.player().getZ()))
            .put("playerX", context -> context.player() == null ? String.valueOf(context.pos().toCenterPos().getX()) : String.valueOf(context.player().getX()))
            .put("playerY", context -> context.player() == null ? String.valueOf(context.pos().toCenterPos().getY()) : String.valueOf(context.player().getY()))
            .put("playerZ", context -> context.player() == null ? String.valueOf(context.pos().toCenterPos().getZ()) : String.valueOf(context.player().getZ()))
            .put("blockPos", context -> context.pos().getX() + " " + context.pos().getY() + " " + context.pos().getZ())
            .put("blockPosX", context -> String.valueOf(context.pos().getX()))
            .put("blockPosY", context -> String.valueOf(context.pos().getY()))
            .put("blockPosZ", context -> String.valueOf(context.pos().getZ()))
            .put("blockX", context -> String.valueOf(context.pos().getX()))
            .put("blockY", context -> String.valueOf(context.pos().getY()))
            .put("blockZ", context -> String.valueOf(context.pos().getZ()))
            .put("blockVec", context -> {
                Vec3d center = context.pos().toCenterPos();
                return center.x + " " + center.y + " " + center.z;
            })
            .put("blockVecX", context -> String.valueOf(context.pos().toCenterPos().getX()))
            .put("blockVecY", context -> String.valueOf(context.pos().toCenterPos().getY()))
            .put("blockVecZ", context -> String.valueOf(context.pos().toCenterPos().getZ()))
            .put("playerDistance", context -> context.player() == null ? "0" : String.valueOf(context.player().getPos().distanceTo(context.pos().toCenterPos())))
            .put("playerSquaredDistance", context -> context.player() == null ? "0" : String.valueOf(context.player().getPos().squaredDistanceTo(context.pos().toCenterPos())))
            .put("playerPitch", context -> context.player() == null ? "0" : String.valueOf(context.player().getPitch()))
            .put("playerYaw", context -> context.player() == null ? "0" : String.valueOf(context.player().getYaw()))
            .put("playerUUID", context -> context.player() == null ? "" : String.valueOf(context.player().getUuidAsString()))
            .build();
    public static final Map<String, BiFunction<String[], Outcome.Context, String>> FUNCTIONS = new ImmutableMap.Builder<String, BiFunction<String[], Outcome.Context, String>>()
            .put("random", (args, context) -> String.valueOf(args[context.world().getRandom().nextInt(args.length)]))
            .put("randomBetween", FunctionUtils::getRandomBetween)
            .put("randomVelocity", FunctionUtils::getRandomVelocity)
            .build();

    private static String getRandomBetween(String[] args, Outcome.Context context) {
        if (args.length != 2)
            throw new IllegalArgumentException("Function 'randomBetween' requires 2 arguments, found " + args.length);

        int from = Integer.parseInt(parseString(args[0], context));
        int to = Integer.parseInt(parseString(args[1], context));

        return String.valueOf(context.world().getRandom().nextBetween(from, to));
    }

    private static String getRandomVelocity(String[] args, Outcome.Context context) {
        if (args.length != 2 && args.length != 0)
            return "0";

        double power;
        double pitch;
        if (args.length == 0) {
            power = .9d;
            pitch = 15d;
        } else {
            power = Double.parseDouble(parseString(args[0], context));
            pitch = Double.parseDouble(parseString(args[1], context));
        }

        float yawRad = (float) Math.toRadians(context.world().getRandom().nextBetween(-180, 180));
        float pitchRad = (float) Math.toRadians(-90d + context.world().getRandom().nextBetween((int) -pitch, (int) pitch));

        Vec3d motion = new Vec3d(-MathHelper.sin(yawRad) * MathHelper.cos(pitchRad) * power, -MathHelper.sin(pitchRad) * power, MathHelper.cos(yawRad) * MathHelper.cos(pitchRad) * power);
        return motion.x + "," + motion.y + "," + motion.z;
    }

    /**
     * @param string a json object in string format
     * @return the string with all parameters and functions replaced with their values, based on the context
     */
    public static String parseString(String string, Outcome.Context context) {
        StringBuilder result = new StringBuilder();

        Matcher matcher = PARAM_PATTERN.matcher(string);
        while (matcher.find()) {
            String param = matcher.group(1);

            if (FunctionUtils.PARAMS.containsKey(param)) {
                matcher.appendReplacement(result, FunctionUtils.PARAMS.get(param).apply(context));
            } else throw new IllegalArgumentException("Error parsing param '" + param + "'");
        }
        matcher.appendTail(result);

        return result.toString();
    }
}
