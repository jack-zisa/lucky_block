package dev.creoii.luckyblock.util.function.wrapper;

import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.function.Function;
import dev.creoii.luckyblock.util.function.target.NbtTarget;
import dev.creoii.luckyblock.util.function.target.Target;
import dev.creoii.luckyblock.util.function.target.VelocityTarget;
import dev.creoii.luckyblock.util.vec.VecProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.List;

public record EntityWrapper(Entity entity, List<Function<?>> functions) implements Wrapper<EntityType<?>, EntityWrapper>, VelocityTarget<EntityWrapper>, NbtTarget<EntityWrapper> {
    @Override
    public EntityType<?> getObject(Outcome.Context<?> context) {
        return entity.getType();
    }

    @Override
    public Target<EntityWrapper> update(Function<Target<?>> function, Object newObject) {
        if (newObject instanceof EntityWrapper newEntity) {
            //functions.remove(function);
            //newStack.functions.remove(function);
            return newEntity;
        }
        throw new IllegalArgumentException("Attempted updating entity target with non-entity value.");
    }

    @Override
    public EntityWrapper setVelocity(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, VecProvider velocity) {
        entity.setVelocity(entity.getVelocity().add(velocity.getVec(context)));
        return new EntityWrapper(entity, functions);
    }

    @Override
    public EntityWrapper setNbt(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, NbtElement nbt) {
        if (nbt instanceof NbtCompound nbtCompound) {
            entity.readNbt(nbtCompound);
        }
        return new EntityWrapper(entity, functions);
    }
}
