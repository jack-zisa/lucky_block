package dev.creoii.luckyblock.util;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import dev.creoii.luckyblock.outcome.OutcomeContext;
import net.minecraft.nbt.*;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ContextualNbtCompound extends NbtCompound {
    public static final Codec<ContextualNbtCompound> CODEC = Codec.PASSTHROUGH.comapFlatMap(dynamic -> {
        NbtElement nbtElement = dynamic.convert(NbtOps.INSTANCE).getValue();
        if (nbtElement instanceof NbtCompound nbtCompound) {
            DataResult<NbtCompound> result = DataResult.success(nbtCompound == dynamic.getValue() ? nbtCompound.copy() : nbtCompound);
            return result.map(nbtCompound1 -> new ContextualNbtCompound(nbtCompound1.entries, null));
        } else {
            return DataResult.error(() -> "Not a compound tag: " + nbtElement);
        }
    }, nbt -> new Dynamic<>(NbtOps.INSTANCE, nbt.copy()));
    @Nullable
    private OutcomeContext context;

    protected ContextualNbtCompound(Map<String, NbtElement> entries, @Nullable OutcomeContext context) {
        super(entries);
        this.context = context;
    }

    public ContextualNbtCompound() {
        this(Maps.newHashMap(), null);
    }

    public void setContext(@Nullable OutcomeContext context) {
        this.context = context;
    }

    public @Nullable OutcomeContext getContext() {
        return context;
    }

    public int getInt(String key) {
        try {
            if (contains(key, 99)) {
                return ((AbstractNbtNumber) entries.get(key)).intValue();
            } else if (contains(key, 8) && context != null) {
                return context.parseInt(entries.get(key).asString());
            }
        } catch (ClassCastException ignored) {}

        return 0;
    }

    public byte getByte(String key) {
        try {
            if (contains(key, 99)) {
                return ((AbstractNbtNumber) entries.get(key)).byteValue();
            } else if (contains(key, 8) && context != null) {
                return (byte) context.parseInt(entries.get(key).asString());
            }
        } catch (ClassCastException ignored) {}

        return 0;
    }

    public long getLong(String key) {
        try {
            if (contains(key, 99)) {
                return ((AbstractNbtNumber) entries.get(key)).longValue();
            } else if (contains(key, 8) && context != null) {
                return context.parseInt(entries.get(key).asString());
            }
        } catch (ClassCastException ignored) {}

        return 0L;
    }

    public float getFloat(String key) {
        try {
            if (contains(key, 99)) {
                return ((AbstractNbtNumber) entries.get(key)).floatValue();
            } else if (contains(key, 8) && context != null) {
                return (float) context.parseDouble(entries.get(key).asString());
            }
        } catch (ClassCastException ignored) {}

        return 0f;
    }

    public double getDouble(String key) {
        try {
            if (contains(key, 99)) {
                return ((AbstractNbtNumber) entries.get(key)).doubleValue();
            } else if (contains(key, 8) && context != null) {
                return context.parseDouble(entries.get(key).asString());
            }
        } catch (ClassCastException ignored) {}

        return 0d;
    }

    public String getString(String key) {
        try {
            if (contains(key, 8)) {
                String value = entries.get(key).asString();
                if (value.startsWith("{") && value.endsWith("}") && context != null) {
                    return context.processString(value);
                }
                return value;
            }
        } catch (ClassCastException ignored) {}

        return "";
    }

    public int[] getIntArray(String key) {
        try {
            if (contains(key, 11)) {
                return ((NbtIntArray) entries.get(key)).getIntArray();
            } else if (contains(key, 8) && context != null) {
                BlockPos value = context.parseBlockPos(entries.get(key).asString());
                return new int[]{value.getX(), value.getY(), value.getZ()};
            }
        } catch (ClassCastException ignored) {}

        return new int[0];
    }

    public long[] getLongArray(String key) {
        try {
            if (contains(key, 12)) {
                return ((NbtLongArray) entries.get(key)).getLongArray();
            } else if (contains(key, 8) && context != null) {
                BlockPos value = context.parseBlockPos(entries.get(key).asString());
                return new long[]{value.getX(), value.getY(), value.getZ()};
            }
        } catch (ClassCastException ignored) {}

        return new long[0];
    }

    public NbtList getList(String key, int type) {
        try {
            if (getType(key) == 9) {
                NbtList nbtList = (NbtList) entries.get(key);
                if (!nbtList.isEmpty() && nbtList.getHeldType() != type) {
                    return new NbtList();
                }

                return nbtList;
            } else if (contains(key, 8) && context != null) {
                Vec3d value = context.parseVec3d(entries.get(key).asString());
                NbtList nbtList = new NbtList();
                nbtList.add(NbtDouble.of(value.getX()));
                nbtList.add(NbtDouble.of(value.getY()));
                nbtList.add(NbtDouble.of(value.getZ()));
                return nbtList;
            }
        } catch (ClassCastException ignored) {}

        return new NbtList();
    }

    protected ContextualNbtCompound shallowCopy() {
        return new ContextualNbtCompound(new HashMap<>(this.entries), context);
    }

    public ContextualNbtCompound copy() {
        Map<String, NbtElement> map = Maps.newHashMap(Maps.transformValues(entries, NbtElement::copy));
        return new ContextualNbtCompound(map, context);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContextualNbtCompound that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(context, that.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), context);
    }

    @Override
    public ContextualNbtCompound copyFrom(NbtCompound source) {
        if (source instanceof ContextualNbtCompound contextual) {
            setContext(contextual.getContext());
        }
        return (ContextualNbtCompound) super.copyFrom(source);
    }
}
