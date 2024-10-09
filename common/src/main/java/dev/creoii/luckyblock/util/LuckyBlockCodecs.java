package dev.creoii.luckyblock.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.creoii.luckyblock.outcome.OutcomeContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.intprovider.ConstantIntProvider;

import java.util.List;
import java.util.function.Function;

public class LuckyBlockCodecs {
    public static final ConstantIntProvider ONE = ConstantIntProvider.create(1);

    public static Codec<String> DOUBLE = Codec.either(Codec.DOUBLE, Codec.STRING).xmap(either -> {
        return either.map(String::valueOf, Function.identity());
    }, Either::right);

    public static Codec<String> INT = Codec.either(Codec.INT, Codec.STRING).xmap(either -> {
        return either.map(String::valueOf, Function.identity());
    }, Either::right);

    public static Codec<List<Identifier>> IDENTIFIER = Codec.either(Identifier.CODEC, Identifier.CODEC.listOf()).xmap(either -> {
        return either.map(List::of, Function.identity());
    }, Either::right);

    public static Codec<ItemStack> ITEMSTACK = Codec.either(Identifier.CODEC, ItemStack.CODEC).xmap(either -> {
        return either.map(identifier -> Registries.ITEM.get(identifier).getDefaultStack(), Function.identity());
    }, Either::right);

    /**
     * Returns a codec that parses one of the following values:
     * <ul>
     *     <li>"{param}"</li>
     *     <li>[0, 0, 0]</li>
     *     <li>[0, "{param}", 0]</li>
     *     <li>[0, "{param} + 10", 0]</li>
     * </ul>
     * And converts it into a string of format:
     * <p>"x y z"</p>
     */
    public static Codec<String> BLOCK_POS = Codec.either(SpecialInteger.CODEC.listOf().comapFlatMap(list -> {
        return Util.decodeFixedLengthList(list, 3).map(values -> values);
    }, list -> list), Codec.STRING).xmap(either -> {
        return either.map(list -> {
            if (list.size() == 3) {
                return list.getFirst().value + "," + list.get(1).value + "," + list.get(2).value;
            } else if (list.size() == 2) {
                return list.getFirst().value + "," + list.get(1).value;
            } else return list.getFirst().value();
        }, Function.identity());
    }, Either::right);

    public static Codec<String> VEC_3D = Codec.either(SpecialDouble.CODEC.listOf().comapFlatMap(list -> {
        return Util.decodeFixedLengthList(list, 3).map(values -> values);
    }, list -> list), Codec.STRING).xmap(either -> {
        return either.map(list -> {
            if (list.size() == 3) {
                return list.getFirst().value + "," + list.get(1).value + "," + list.get(2).value;
            } else if (list.size() == 2) {
                return list.getFirst().value + "," + list.get(1).value;
            } else return list.getFirst().value();
        }, Function.identity());
    }, Either::right);

    public static Codec<String> POSITION = Codec.either(BLOCK_POS, VEC_3D).xmap(either -> {
        return either.map(Function.identity(), Function.identity());
    }, Either::right);

    public record SpecialInteger(String value) {
        public static final Codec<SpecialInteger> CODEC = Codec.either(Codec.INT, Codec.STRING).xmap(either -> {
            return either.map(SpecialInteger::new, SpecialInteger::new);
        }, specialInteger -> Either.right(specialInteger.value));

        private SpecialInteger(int value) {
            this(String.valueOf(value));
        }

        public static SpecialInteger of(String value) {
            return new SpecialInteger(value);
        }

        public static SpecialInteger of(int value) {
            return new SpecialInteger(value);
        }

        public int getValue(OutcomeContext context) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return context.parseInt(value);
            }
        }
    }

    public record SpecialDouble(String value) {
        public static final Codec<SpecialDouble> CODEC = Codec.either(Codec.DOUBLE, Codec.STRING).xmap(either -> {
            return either.map(SpecialDouble::new, SpecialDouble::new);
        }, specialDouble -> Either.right(specialDouble.value));

        private SpecialDouble(double value) {
            this(String.valueOf(value));
        }

        public static SpecialDouble of(String value) {
            return new SpecialDouble(value);
        }

        public static SpecialDouble of(double value) {
            return new SpecialDouble(value);
        }

        public double getValue(OutcomeContext context) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                return context.parseDouble(value);
            }
        }
    }
}
