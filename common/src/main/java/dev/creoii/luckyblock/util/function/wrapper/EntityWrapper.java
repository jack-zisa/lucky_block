package dev.creoii.luckyblock.util.function.wrapper;

import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.function.*;
import dev.creoii.luckyblock.util.function.target.*;
import dev.creoii.luckyblock.util.vecprovider.VecProvider;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.floatprovider.FloatProvider;

import java.util.Optional;

public class EntityWrapper implements Wrapper<EntityType<?>, EntityWrapper>, VelocityTarget<EntityWrapper>, NbtTarget<EntityWrapper>, ColorTarget<EntityWrapper>, EquipmentTarget<EntityWrapper>, RotationTarget<EntityWrapper>, PassengersTarget<EntityWrapper>, StatusEffectsTarget<EntityWrapper> {
    private final FunctionContainer functionContainer;
    private final EntityType<?> entityType; // need this field because entity may be null
    private final Entity entity;

    public EntityWrapper(EntityType<?> entityType, FunctionContainer functionContainer, Outcome.Context<? extends ContextInfo> context) {
        this.functionContainer = functionContainer;
        this.entityType = entityType;
        this.entity = entityType.create(context.world(), SpawnReason.NATURAL);
    }

    public EntityWrapper(EntityType<?> entityType, FunctionContainer functionContainer) {
        this.functionContainer = functionContainer;
        this.entityType = entityType;
        this.entity = null;
    }

    public FunctionContainer getFunctions() {
        return functionContainer;
    }

    public Entity getEntity() {
        return entity;
    }

    public EntityType<?> getEntityType() {
        return entityType;
    }

    @Override
    public EntityWrapper init(Outcome.Context<?> context) {
        return new EntityWrapper(entityType, functionContainer, context);
    }

    @Override
    public EntityType<?> getRegistryObject(Outcome.Context<?> context) {
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
        return this;
    }

    @Override
    public EntityWrapper setNbt(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, NbtElement nbt) {
        if (nbt instanceof NbtCompound nbtCompound) {
            entity.readNbt(nbtCompound);
        }
        return this;
    }

    @Override
    public EntityWrapper setColor(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, int color) {
        return this;
    }

    @Override
    public EntityWrapper setRgb(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, int[] rgb) {
        return this;
    }

    @Override
    public EntityWrapper setDyeColor(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, DyeColor dyeColor) {
        if (entity instanceof ShulkerEntity shulkerEntity) {
            shulkerEntity.setVariant(Optional.of(dyeColor));
        } else if (entity instanceof SheepEntity sheepEntity) {
            sheepEntity.setColor(dyeColor);
        } else if (entity instanceof WolfEntity wolfEntity) {
            wolfEntity.setCollarColor(dyeColor);
        } else if (entity instanceof CatEntity catEntity) {
            catEntity.setCollarColor(dyeColor);
        }
        return this;
    }

    @Override
    public EntityWrapper setStack(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, EquipmentSlot slot, ItemStack stack) {
        if (entity instanceof LivingEntity living) {
            living.equipStack(slot, stack);
        }
        return this;
    }

    @Override
    public EntityWrapper setRotation(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, FloatProvider pitch, FloatProvider yaw) {
        entity.setAngles(yaw.get(context.random()) % 360f, pitch.get(context.random()) % 360f);
        return this;
    }

    @Override
    public EntityWrapper addPassenger(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, EntityWrapper entity) {
        if (entity.entity == null) {
            entity = entity.init(context);
        }

        Function.applyAll(entity.getFunctions(), outcome, context);
        entity.entity.refreshPositionAndAngles(context.pos(), entity.entity.getYaw(), entity.entity.getPitch());

        if (this.entity.hasPassengers()) {
            entity.entity.startRiding(this.entity.getPassengerList().getLast(), true);
        } else entity.entity.startRiding(this.entity, true);

        context.world().spawnEntity(entity.entity);
        return this;
    }

    @Override
    public EntityWrapper addStatusEffect(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, StatusEffectInstance statusEffectInstance) {
        if (entity instanceof LivingEntity living) {
            living.addStatusEffect(statusEffectInstance);
        }
        return this;
    }
}
