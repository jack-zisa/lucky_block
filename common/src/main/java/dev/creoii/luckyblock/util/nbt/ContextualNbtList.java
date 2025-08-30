package dev.creoii.luckyblock.util.nbt;

import com.google.common.collect.Lists;
import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.vec.VecProvider;
import net.minecraft.nbt.*;
import net.minecraft.nbt.visitor.StringNbtWriter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ContextualNbtList extends NbtList {
    @Nullable
    private Outcome.Context context;

    public ContextualNbtList(List<NbtElement> list, @Nullable Outcome.Context context) {
        super(list);
        this.context = context;
    }

    public ContextualNbtList() {
        this(Lists.newArrayList(), null);
    }

    public void setContext(@Nullable Outcome.Context context) {
        this.context = context;
    }

    public @Nullable Outcome.Context getContext() {
        return context;
    }

    @Override
    public Optional<NbtCompound> getCompound(int index) {
        Optional<NbtCompound> compound = super.getCompound(index);
        return compound.map(nbtCompound -> new ContextualNbtCompound().copyFrom(nbtCompound));
    }

    public Optional<NbtList> getList(int index) {
        if (index >= 0 && index < value.size()) {
            NbtElement nbtElement = value.get(index);
            if (nbtElement.getType() == 9) {
                ((ContextualNbtList) nbtElement).setContext(context);
                return Optional.of((ContextualNbtList) nbtElement);
            } else if (nbtElement.getType() == 10 && context != null) {
                StringNbtWriter writer = new StringNbtWriter();
                writer.visitCompound(getCompound(index).get());
                DataResult<VecProvider> dataResult = VecProvider.VALUE_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(writer.getString()));
                Optional<VecProvider> vecProvider = dataResult.resultOrPartial(string -> LuckyBlockMod.LOGGER.error("Error parsing vec provider: {}", string));
                if (vecProvider.isPresent()) {
                    ContextualNbtList nbtList = new ContextualNbtList();
                    nbtList.setContext(context);
                    Vec3d vec3d = vecProvider.get().getVec(context);
                    nbtList.add(NbtDouble.of(vec3d.x));
                    nbtList.add(NbtDouble.of(vec3d.y));
                    nbtList.add(NbtDouble.of(vec3d.z));
                    return Optional.of(nbtList);
                }
            }
        }

        return Optional.empty();
    }

    public Optional<Short> getShort(int index) {
        if (index >= 0 && index < value.size()) {
            NbtElement nbtElement = value.get(index);
            if (nbtElement.getType() == 3) {
                return Optional.of(((NbtShort) nbtElement).shortValue());
            } else if (nbtElement.getType() == 10 && context != null) {
                StringNbtWriter writer = new StringNbtWriter();
                writer.visitCompound(getCompound(index).get());
                DataResult<IntProvider> dataResult = IntProvider.createValidatingCodec(-32768, 32767).parse(JsonOps.INSTANCE, JsonParser.parseString(writer.getString()));
                Optional<IntProvider> intProvider = dataResult.resultOrPartial(string -> LuckyBlockMod.LOGGER.error("Error parsing int provider: {}", string));
                if (intProvider.isPresent()) {
                    return Optional.of((short) intProvider.get().get(context.world().getRandom()));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Integer> getInt(int index) {
        if (index >= 0 && index < value.size()) {
            NbtElement nbtElement = value.get(index);
            if (nbtElement.getType() == 3) {
                return Optional.of(((NbtInt) nbtElement).intValue());
            } else if (nbtElement.getType() == 10 && context != null) {
                StringNbtWriter writer = new StringNbtWriter();
                writer.visitCompound(getCompound(index).get());
                DataResult<IntProvider> dataResult = IntProvider.VALUE_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(writer.getString()));
                Optional<IntProvider> intProvider = dataResult.resultOrPartial(string -> LuckyBlockMod.LOGGER.error("Error parsing int provider: {}", string));
                if (intProvider.isPresent()) {
                    return Optional.of(intProvider.get().get(context.world().getRandom()));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<int[]> getIntArray(int index) {
        if (index >= 0 && index < value.size()) {
            NbtElement nbtElement = value.get(index);
            if (nbtElement.getType() == 11) {
                return Optional.of(((NbtIntArray)nbtElement).getIntArray());
            } else if (nbtElement.getType() == 10 && context != null) {
                StringNbtWriter writer = new StringNbtWriter();
                writer.visitCompound(getCompound(index).get());
                DataResult<VecProvider> dataResult = VecProvider.VALUE_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(writer.getString()));
                Optional<VecProvider> vecProvider = dataResult.resultOrPartial(string -> LuckyBlockMod.LOGGER.error("Error parsing vec provider: {}", string));
                if (vecProvider.isPresent()) {
                    BlockPos pos = vecProvider.get().getPos(context);
                    return Optional.of(new int[]{pos.getX(), pos.getY(), pos.getZ()});
                }
            }
        }

        return Optional.empty();
    }

    public Optional<long[]> getLongArray(int index) {
        if (index >= 0 && index < value.size()) {
            NbtElement nbtElement = value.get(index);
            if (nbtElement.getType() == 12) {
                return Optional.of(((NbtLongArray) nbtElement).getLongArray());
            } else if (nbtElement.getType() == 10 && context != null) {
                StringNbtWriter writer = new StringNbtWriter();
                writer.visitCompound(getCompound(index).get());
                DataResult<VecProvider> dataResult = VecProvider.VALUE_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(writer.getString()));
                Optional<VecProvider> vecProvider = dataResult.resultOrPartial(string -> LuckyBlockMod.LOGGER.error("Error parsing vec provider: {}", string));
                if (vecProvider.isPresent()) {
                    BlockPos pos = vecProvider.get().getPos(context);
                    return Optional.of(new long[]{pos.getX(), pos.getY(), pos.getZ()});
                }
            }
        }

        return Optional.empty();
    }

    public Optional<Double> getDouble(int index) {
        if (index >= 0 && index < value.size()) {
            NbtElement nbtElement = value.get(index);
            if (nbtElement.getType() == 6) {
                return Optional.of(((NbtDouble)nbtElement).doubleValue());
            } else if (nbtElement.getType() == 10 && context != null) {
                StringNbtWriter writer = new StringNbtWriter();
                writer.visitCompound(getCompound(index).get());
                DataResult<FloatProvider> dataResult = FloatProvider.VALUE_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(writer.getString()));
                Optional<FloatProvider> floatProvider = dataResult.resultOrPartial(string -> LuckyBlockMod.LOGGER.error("Error parsing float provider: {}", string));
                if (floatProvider.isPresent()) {
                    return Optional.of((double) floatProvider.get().get(context.world().getRandom()));
                }
            }
        }

        return Optional.empty();
    }

    public Optional<Float> getFloat(int index) {
        if (index >= 0 && index < value.size()) {
            NbtElement nbtElement = value.get(index);
            if (nbtElement.getType() == 5) {
                return Optional.of(((NbtFloat)nbtElement).floatValue());
            } else if (nbtElement.getType() == 10 && context != null) {
                StringNbtWriter writer = new StringNbtWriter();
                writer.visitCompound(getCompound(index).get());
                DataResult<FloatProvider> dataResult = FloatProvider.VALUE_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(writer.getString()));
                Optional<FloatProvider> floatProvider = dataResult.resultOrPartial(string -> LuckyBlockMod.LOGGER.error("Error parsing float provider: {}", string));
                if (floatProvider.isPresent()) {
                    return Optional.of(floatProvider.get().get(context.world().getRandom()));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public ContextualNbtList copy() {
        List<NbtElement> list = new ArrayList<>(value.size());

        for(NbtElement nbtElement : value) {
            list.add(nbtElement.copy());
        }

        return new ContextualNbtList(list, context);
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
