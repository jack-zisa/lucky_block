package dev.creoii.luckyblock.util.nbt;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.position.VecProvider;
import net.minecraft.nbt.*;
import net.minecraft.nbt.visitor.StringNbtWriter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ContextualNbtList extends NbtList {
    @Nullable
    private Outcome.Context context;

    public ContextualNbtList(List<NbtElement> list, byte type, @Nullable Outcome.Context context) {
        super(list, type);
        this.context = context;
    }

    public ContextualNbtList() {
        this(Lists.newArrayList(), (byte) 0, null);
    }

    public void setContext(@Nullable Outcome.Context context) {
        this.context = context;
    }

    public @Nullable Outcome.Context getContext() {
        return context;
    }

    public ContextualNbtCompound getCompound(int index) {
        NbtCompound compound = super.getCompound(index);
        return new ContextualNbtCompound().copyFrom(compound);
    }

    public ContextualNbtList getList(int index) {
        if (index >= 0 && index < value.size()) {
            NbtElement nbtElement = value.get(index);
            if (nbtElement.getType() == 9) {
                ((ContextualNbtList) nbtElement).setContext(context);
                return (ContextualNbtList) nbtElement;
            } else if (nbtElement.getType() == 10 && context != null) {
                StringNbtWriter writer = new StringNbtWriter();
                DataResult<VecProvider> dataResult = VecProvider.VALUE_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(writer.apply(getCompound(index))));
                Optional<VecProvider> vecProvider = dataResult.resultOrPartial(string -> LuckyBlockMod.LOGGER.error("Error parsing vec provider: {}", string));
                if (vecProvider.isPresent()) {
                    ContextualNbtList nbtList = new ContextualNbtList();
                    nbtList.setContext(context);
                    Vec3d vec3d = vecProvider.get().getVec(context);
                    nbtList.add(NbtDouble.of(vec3d.x));
                    nbtList.add(NbtDouble.of(vec3d.y));
                    nbtList.add(NbtDouble.of(vec3d.z));
                    return nbtList;
                }
            }
        }

        ContextualNbtList list = new ContextualNbtList();
        list.setContext(context);
        return list;
    }

    public short getShort(int index) {
        if (index >= 0 && index < value.size()) {
            NbtElement nbtElement = value.get(index);
            if (nbtElement.getType() == 3) {
                return ((NbtShort) nbtElement).shortValue();
            } else if (nbtElement.getType() == 10 && context != null) {
                StringNbtWriter writer = new StringNbtWriter();
                DataResult<IntProvider> dataResult = IntProvider.createValidatingCodec(-32768, 32767).parse(JsonOps.INSTANCE, JsonParser.parseString(writer.apply(getCompound(index))));
                Optional<IntProvider> intProvider = dataResult.resultOrPartial(string -> LuckyBlockMod.LOGGER.error("Error parsing int provider: {}", string));
                if (intProvider.isPresent()) {
                    return (short) intProvider.get().get(context.world().getRandom());
                }
            }
        }
        return 0;
    }

    public int getInt(int index) {
        if (index >= 0 && index < value.size()) {
            NbtElement nbtElement = value.get(index);
            if (nbtElement.getType() == 3) {
                return ((NbtInt) nbtElement).intValue();
            } else if (nbtElement.getType() == 10 && context != null) {
                StringNbtWriter writer = new StringNbtWriter();
                DataResult<IntProvider> dataResult = IntProvider.VALUE_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(writer.apply(getCompound(index))));
                Optional<IntProvider> intProvider = dataResult.resultOrPartial(string -> LuckyBlockMod.LOGGER.error("Error parsing int provider: {}", string));
                if (intProvider.isPresent()) {
                    return intProvider.get().get(context.world().getRandom());
                }
            }
        }
        return 0;
    }

    public int[] getIntArray(int index) {
        if (index >= 0 && index < value.size()) {
            NbtElement nbtElement = value.get(index);
            if (nbtElement.getType() == 11) {
                return ((NbtIntArray)nbtElement).getIntArray();
            } else if (nbtElement.getType() == 10 && context != null) {
                StringNbtWriter writer = new StringNbtWriter();
                DataResult<VecProvider> dataResult = VecProvider.VALUE_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(writer.apply(getCompound(index))));
                Optional<VecProvider> vecProvider = dataResult.resultOrPartial(string -> LuckyBlockMod.LOGGER.error("Error parsing vec provider: {}", string));
                if (vecProvider.isPresent()) {
                    BlockPos pos = vecProvider.get().getPos(context);
                    return new int[]{pos.getX(), pos.getY(), pos.getZ()};
                }
            }
        }

        return new int[0];
    }

    public long[] getLongArray(int index) {
        if (index >= 0 && index < value.size()) {
            NbtElement nbtElement = value.get(index);
            if (nbtElement.getType() == 12) {
                return ((NbtLongArray) nbtElement).getLongArray();
            } else if (nbtElement.getType() == 10 && context != null) {
                StringNbtWriter writer = new StringNbtWriter();
                DataResult<VecProvider> dataResult = VecProvider.VALUE_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(writer.apply(getCompound(index))));
                Optional<VecProvider> vecProvider = dataResult.resultOrPartial(string -> LuckyBlockMod.LOGGER.error("Error parsing vec provider: {}", string));
                if (vecProvider.isPresent()) {
                    BlockPos pos = vecProvider.get().getPos(context);
                    return new long[]{pos.getX(), pos.getY(), pos.getZ()};
                }
            }
        }

        return new long[0];
    }

    public double getDouble(int index) {
        if (index >= 0 && index < value.size()) {
            NbtElement nbtElement = value.get(index);
            if (nbtElement.getType() == 6) {
                return ((NbtDouble)nbtElement).doubleValue();
            } else if (nbtElement.getType() == 10 && context != null) {
                StringNbtWriter writer = new StringNbtWriter();
                DataResult<FloatProvider> dataResult = FloatProvider.VALUE_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(writer.apply(getCompound(index))));
                Optional<FloatProvider> floatProvider = dataResult.resultOrPartial(string -> LuckyBlockMod.LOGGER.error("Error parsing float provider: {}", string));
                if (floatProvider.isPresent()) {
                    return floatProvider.get().get(context.world().getRandom());
                }
            }
        }

        return 0d;
    }

    public float getFloat(int index) {
        if (index >= 0 && index < value.size()) {
            NbtElement nbtElement = value.get(index);
            if (nbtElement.getType() == 5) {
                return ((NbtFloat)nbtElement).floatValue();
            } else if (nbtElement.getType() == 10 && context != null) {
                StringNbtWriter writer = new StringNbtWriter();
                DataResult<FloatProvider> dataResult = FloatProvider.VALUE_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(writer.apply(getCompound(index))));
                Optional<FloatProvider> floatProvider = dataResult.resultOrPartial(string -> LuckyBlockMod.LOGGER.error("Error parsing float provider: {}", string));
                if (floatProvider.isPresent()) {
                    return floatProvider.get().get(context.world().getRandom());
                }
            }
        }

        return 0f;
    }

    public ContextualNbtList copy() {
        Iterable<NbtElement> iterable = NbtTypes.byId(type).isImmutable() ? value : Iterables.transform(value, NbtElement::copy);
        return new ContextualNbtList(Lists.newArrayList(iterable), type, context);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ContextualNbtList list)) return false;
        if (!super.equals(object)) return false;
        return Objects.equals(context, list.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), context);
    }

    public ContextualNbtList copyFrom(NbtList source) {
        if (source instanceof ContextualNbtList contextual) {
            setContext(contextual.getContext());
        }
        addAll(source.value);
        return this;
    }
}
