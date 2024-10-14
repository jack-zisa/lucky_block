package dev.creoii.luckyblock.outcome;

import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.util.FunctionUtils;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record OutcomeContext(World world, BlockPos pos, BlockState state, PlayerEntity player) {
    private static final Pattern PARAM_PATTERN = Pattern.compile("\\{(\\w+)}");
    private static final List<String> COLORS = List.of("brown", "red", "orange", "yellow", "lime", "green", "cyan", "blue", "light_blue", "pink", "magenta", "purple", "black", "gray", "light_gray", "white");
    private static final List<String> WOODS = List.of("oak", "spruce", "birch", "jungle", "dark_oak", "acacia", "mangrove", "cherry");

    public String processString(String string) {
        if (string == null || string.isEmpty()) {
            return "";
        }

        Matcher matcher = PARAM_PATTERN.matcher(string);
        boolean hasParams = false;
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            hasParams = true;
            String param = matcher.group(1);
            String replacement;
            switch (param) {
                case "playerName" -> replacement = player().getGameProfile().getName();
                case "playerPos" -> replacement = player.getBlockX() + " " + player.getBlockY() + " " + player.getBlockZ();
                case "playerPosX" -> replacement = String.valueOf(player.getBlockX());
                case "playerPosY" -> replacement = String.valueOf(player.getBlockY());
                case "playerPosZ" -> replacement = String.valueOf(player.getBlockZ());
                case "playerVec" -> replacement = player.getX() + " " + player.getY() + " " + player.getZ();
                case "playerVecX", "playerX" -> replacement = String.valueOf(player.getX());
                case "playerVecY", "playerY" -> replacement = String.valueOf(player.getY());
                case "playerVecZ", "playerZ" -> replacement = String.valueOf(player.getZ());
                case "blockPos" -> replacement = pos.getX() + " " + pos.getY() + " " + pos.getZ();
                case "blockPosX", "blockX" -> replacement = String.valueOf(pos.getX());
                case "blockPosY", "blockY" -> replacement = String.valueOf(pos.getY());
                case "blockPosZ", "blockZ" -> replacement = String.valueOf(pos.getZ());
                case "blockVec" -> {
                    Vec3d center = pos.toCenterPos();
                    replacement = center.x + " " + center.y + " " + center.z;
                }
                case "blockVecX" -> replacement = String.valueOf(pos.toCenterPos().x);
                case "blockVecY" -> replacement = String.valueOf(pos.toCenterPos().y);
                case "blockVecZ" -> replacement = String.valueOf(pos.toCenterPos().z);
                case "playerDistance" -> replacement = String.valueOf(player.getPos().distanceTo(pos.toCenterPos()));
                case "playerSquaredDistance" -> replacement = String.valueOf(player.getPos().squaredDistanceTo(pos.toCenterPos()));
                case "playerPitch" -> replacement = String.valueOf(player.getPitch());
                case "playerYaw" -> replacement = String.valueOf(player.getYaw());
                case "randomColor" -> replacement = COLORS.get(world.getRandom().nextInt(COLORS.size()));
                case "randomWood" -> replacement = WOODS.get(world.getRandom().nextInt(WOODS.size()));
                case "playerUUID" -> replacement = player.getUuidAsString();
                default -> throw new IllegalArgumentException("Error parsing token '" + param + "'");
            }
            matcher.appendReplacement(result, replacement);
        }

        if (!hasParams)
            return string;

        matcher.appendTail(result);
        return result.toString();
    }

    public Text processText(Text text) {
        if (text.getString() == null) {
            return Text.literal("");
        }

        String processed = processString(text.getString());
        return MutableText.of(PlainTextContent.of(processed)).setStyle(text.getStyle());
    }

    public Vec3d parseVec3d(String param) {
        if (param.startsWith("[")) {
            param = param.substring(1);
        }
        if (param.endsWith("]")) {
            param = param.substring(0, param.length() - 1);
        }
        String[] values = split(param);
        if (values.length == 1) {
            return switch (param) {
                case "{blockPos}" -> pos().toCenterPos();
                case "{playerPos}" -> player().getBlockPos().toCenterPos();
                case "{playerVec}" -> player().getPos();
                default -> {
                    try {
                        yield parseVec3d(FunctionUtils.processFunctions(param, this));
                    } catch (IllegalArgumentException e) {
                        LuckyBlockMod.LOGGER.error("Error parsing special vec3d: '{}'", param);
                        throw e;
                    }
                }
            };
        } else return new Vec3d(LuckyBlockCodecs.SpecialDouble.of(values[0]).getValue(this), LuckyBlockCodecs.SpecialDouble.of(values[1]).getValue(this), LuckyBlockCodecs.SpecialDouble.of(values[2]).getValue(this));
    }

    public BlockPos parseBlockPos(String param) {
        String[] values = split(param);
        if (values.length == 1) {
            return switch (param) {
                case "{blockPos}" -> pos();
                case "{playerPos}", "{playerVec}" -> player().getBlockPos();
                default -> {
                    try {
                        yield parseBlockPos(FunctionUtils.processFunctions(param, this));
                    } catch (IllegalArgumentException e) {
                        LuckyBlockMod.LOGGER.error("Error parsing special block pos: '{}'", param);
                        throw e;
                    }
                }
            };
        } else return new BlockPos(LuckyBlockCodecs.SpecialInteger.of(values[0]).getValue(this), LuckyBlockCodecs.SpecialInteger.of(values[1]).getValue(this), LuckyBlockCodecs.SpecialInteger.of(values[2]).getValue(this));
    }

    public int parseInt(String param) {
        return (int) evaluateExpression(param);
    }

    public double parseDouble(String param) {
        return evaluateExpression(param);
    }

    public double evaluateExpression(String expression) {
        expression = processString(expression);
        expression = FunctionUtils.processFunctions(expression, this);
        char[] tokens = expression.toCharArray();

        Stack<Double> values = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i] == ' ')
                continue;

            // Negative numbers
            if (tokens[i] == '-' && (i == 0 || isOperator(tokens[i - 1]))) {
                StringBuilder sb = new StringBuilder();
                do {
                    sb.append(tokens[i]);
                    i++;
                } while (i < tokens.length && (Character.isDigit(tokens[i]) || tokens[i] == '.'));
                values.push(Double.parseDouble(sb.toString()));
                i--;
            } else if ((tokens[i] >= '0' && tokens[i] <= '9') || tokens[i] == '.') {
                StringBuilder sb = new StringBuilder();
                while (i < tokens.length && (Character.isDigit(tokens[i]) || tokens[i] == '.')) {
                    sb.append(tokens[i]);
                    i++;
                }
                values.push(Double.parseDouble(sb.toString()));
                i--;
            } else if (tokens[i] == '(') {
                operators.push(tokens[i]);
            } else if (tokens[i] == ')') {
                while (operators.peek() != '(') {
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                }
                operators.pop();
            } else if (tokens[i] == '+' || tokens[i] == '-' || tokens[i] == '*' || tokens[i] == '/') {
                while (!operators.isEmpty() && hasPrecedence(tokens[i], operators.peek())) {
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                }
                operators.push(tokens[i]);
            }
        }

        while (!operators.isEmpty()) {
            values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
        }

        return values.pop();
    }

    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private static boolean hasPrecedence(char operator1, char operator2) {
        if (operator2 == '(' || operator2 == ')')
            return false;
        return (operator1 != '*' && operator1 != '/') || (operator2 != '+' && operator2 != '-');
    }

    private static double applyOperator(char operator, double b, double a) {
        return switch (operator) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> {
                if (b == 0)
                    throw new ArithmeticException("Cannot divide by zero");
                yield a / b;
            }
            default -> 0;
        };
    }

    public static String[] split(String param) {
        List<String> result = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        int parentheses = 0;

        for (int i = 0; i < param.length(); i++) {
            char c = param.charAt(i);

            if (c == '(' || c == '[') {
                parentheses++;
            }

            if (c == ')' || c == ']') {
                parentheses--;
            }

            if (c == ',' && parentheses == 0) {
                result.add(builder.toString().trim());
                builder.setLength(0);
            } else builder.append(c);
        }

        if (!builder.isEmpty()) {
            result.add(builder.toString().trim());
        }

        return result.toArray(new String[0]);
    }
}
