package dev.creoii.luckyblock.util.function.wrapper;

import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.function.Function;
import dev.creoii.luckyblock.util.function.Functions;
import dev.creoii.luckyblock.util.function.target.*;
import dev.creoii.luckyblock.util.vec.VecProvider;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.DyeColor;

import java.util.Optional;

public class EntityWrapper implements Wrapper<EntityType<?>, EntityWrapper>, VelocityTarget<EntityWrapper>, NbtTarget<EntityWrapper>, ColorTarget<EntityWrapper>, EquipmentTarget<EntityWrapper>, VariantTarget<EntityWrapper, Object> {
    private final Functions functions;
    private final EntityType<?> entityType;
    private final Entity entity;

    public EntityWrapper(EntityType<?> entityType, Functions functions, Outcome.Context<? extends ContextInfo> context) {
        this.functions = functions;
        this.entityType = entityType;
        this.entity = entityType.create(context.world(), SpawnReason.NATURAL);
    }

    public EntityWrapper(EntityType<?> entityType, Functions functions) {
        this.functions = functions;
        this.entityType = entityType;
        this.entity = null;
    }

    public EntityWrapper(Entity entity, Functions functions) {
        this.functions = functions;
        this.entityType = entity.getType();
        this.entity = entity;
    }

    public static EntityWrapper fromEntityType(EntityType<?> entityType) {
        return new EntityWrapper(entityType, Functions.EMPTY);
    }

    public EntityWrapper init(Outcome.Context<? extends ContextInfo> context) {
        return new EntityWrapper(entityType, functions, context);
    }

    public Functions getFunctions() {
        return functions;
    }

    public Entity getEntity() {
        return entity;
    }

    public EntityType<?> getEntityType() {
        return entityType;
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
        return new EntityWrapper(entity, functions);
    }

    @Override
    public EntityWrapper setHead(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, ItemStack head) {
        if (entity instanceof LivingEntity living) {
            living.equipStack(EquipmentSlot.HEAD, head);
        }
        return this;
    }

    @Override
    public EntityWrapper setChest(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, ItemStack chest) {
        if (entity instanceof LivingEntity living) {
            living.equipStack(EquipmentSlot.CHEST, chest);
        }
        return this;
    }

    @Override
    public EntityWrapper setLegs(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, ItemStack legs) {
        if (entity instanceof LivingEntity living) {
            living.equipStack(EquipmentSlot.LEGS, legs);
        }
        return this;
    }

    @Override
    public EntityWrapper setFeet(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, ItemStack boots) {
        if (entity instanceof LivingEntity living) {
            living.equipStack(EquipmentSlot.FEET, boots);
        }
        return this;
    }

    @Override
    public EntityWrapper setMainhand(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, ItemStack mainhand) {
        if (entity instanceof LivingEntity living) {
            living.equipStack(EquipmentSlot.MAINHAND, mainhand);
        }
        return this;
    }

    @Override
    public EntityWrapper setOffhand(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, ItemStack offhand) {
        if (entity instanceof LivingEntity living) {
            living.equipStack(EquipmentSlot.OFFHAND, offhand);
        }
        return this;
    }

    @Override
    public EntityWrapper setBody(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, ItemStack body) {
        if (entity instanceof LivingEntity living) {
            living.equipStack(EquipmentSlot.BODY, body);
        }
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public EntityWrapper setVariant(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, Object variant) {
        if (entity instanceof VariantHolder variantHolder) {
            variantHolder.setVariant(variant);
        }
        return this;
    }
}
