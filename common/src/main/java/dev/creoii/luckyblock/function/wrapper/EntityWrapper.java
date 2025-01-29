package dev.creoii.luckyblock.function.wrapper;

import com.mojang.datafixers.util.Either;
import dev.creoii.luckyblock.function.Function;
import dev.creoii.luckyblock.function.FunctionContainer;
import dev.creoii.luckyblock.function.target.*;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.Provider;
import dev.creoii.luckyblock.util.vecprovider.VecProvider;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.floatprovider.FloatProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class EntityWrapper implements Wrapper<EntityType<?>, EntityWrapper>, VelocityTarget<EntityWrapper>, NbtTarget<EntityWrapper>, ColorTarget<EntityWrapper>, EquipmentTarget<EntityWrapper>, RotationTarget<EntityWrapper>, StatusEffectsTarget<EntityWrapper>, NameTarget<EntityWrapper>, VariantTarget<EntityWrapper> {
    private final FunctionContainer functionContainer;
    private final EntityType<?> entityType; // need this field because entity may be null
    @Nullable
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

    public EntityWrapper(Entity entity, FunctionContainer functionContainer) {
        this.functionContainer = functionContainer;
        this.entityType = entity.getType();
        this.entity = entity;
    }

    public FunctionContainer getFunctions() {
        return functionContainer;
    }

    public @Nullable Entity getEntity() {
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
    public EntityWrapper addStatusEffect(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, StatusEffectInstance statusEffectInstance) {
        if (entity instanceof LivingEntity living) {
            living.addStatusEffect(statusEffectInstance);
        }
        return this;
    }

    @Override
    public EntityWrapper setName(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, Text name) {
        if (name != null) {
            entity.setCustomName(name);
            entity.setCustomNameVisible(true);
        }
        return this;
    }

    @Override
    public EntityWrapper setVariant(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, Either<Integer, String> variant) {
        if (entity instanceof AxolotlEntity axolotl) {
            variant.left().ifPresent(integer -> axolotl.setVariant(AxolotlEntity.Variant.byId(integer)));
            variant.right().ifPresent(s -> axolotl.setVariant(AxolotlEntity.Variant.valueOf(s.toUpperCase())));
        } else if (entity instanceof CatEntity cat) {
            Registry<CatVariant> catVariantRegistry = context.world().getRegistryManager().getOrThrow(RegistryKeys.CAT_VARIANT);
            variant.left().ifPresent(integer -> cat.setVariant(catVariantRegistry.getEntry(integer).get()));
            variant.right().ifPresent(s -> cat.setVariant(catVariantRegistry.getEntry(Identifier.tryParse(s)).get()));
        } else if (entity instanceof FoxEntity fox) {
            variant.left().ifPresent(integer -> fox.setVariant(FoxEntity.Type.fromId(integer)));
            variant.right().ifPresent(s -> fox.setVariant(FoxEntity.Type.byName(s.toLowerCase())));
        } else if (entity instanceof GoatEntity goat) {
            variant.left().ifPresent(integer -> goat.setScreaming(integer == 1));
            variant.right().ifPresent(s -> goat.setScreaming(s.equalsIgnoreCase("screaming")));
        } else if (entity instanceof HorseEntity horse) {
            variant.left().ifPresent(integer -> horse.setVariant(HorseColor.byId(integer)));
            variant.right().ifPresent(s -> horse.setVariant(HorseColor.valueOf(s.toUpperCase())));
        } else if (entity instanceof LlamaEntity llama) {
            variant.left().ifPresent(integer -> llama.setVariant(LlamaEntity.Variant.byId(integer)));
            variant.right().ifPresent(s -> llama.setVariant(LlamaEntity.Variant.valueOf(s.toUpperCase())));
        } else if (entity instanceof MooshroomEntity mooshroom) {
            variant.left().ifPresent(integer -> mooshroom.setVariant(integer == 1 ? MooshroomEntity.Type.BROWN : MooshroomEntity.Type.RED));
            variant.right().ifPresent(s -> mooshroom.setVariant(MooshroomEntity.Type.valueOf(s.toUpperCase())));
        } else if (entity instanceof ParrotEntity parrot) {
            variant.left().ifPresent(integer -> parrot.setVariant(ParrotEntity.Variant.byIndex(integer)));
            variant.right().ifPresent(s -> parrot.setVariant(ParrotEntity.Variant.valueOf(s.toUpperCase())));
        } else if (entity instanceof RabbitEntity rabbit) {
            variant.left().ifPresent(integer -> rabbit.setVariant(RabbitEntity.RabbitType.byId(integer)));
            variant.right().ifPresent(s -> rabbit.setVariant(RabbitEntity.RabbitType.valueOf(s.toUpperCase())));
        } else if (entity instanceof TropicalFishEntity tropicalFish) {
            variant.left().ifPresent(integer -> tropicalFish.setVariant(TropicalFishEntity.Variety.fromId(integer)));
            variant.right().ifPresent(s -> tropicalFish.setVariant(TropicalFishEntity.Variety.valueOf(s.toUpperCase())));
        }
        return this;
    }
}
