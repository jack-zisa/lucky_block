package dev.creoii.luckyblock.util.stackprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.function.wrapper.EntityWrapper;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.entityprovider.EntityProvider;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.random.Random;

public class EntityItemStackProvider extends ItemStackProvider {
    public static final MapCodec<EntityItemStackProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(EntityProvider.CODEC.fieldOf("entity").forGetter(provider -> provider.entity),
                Codec.STRING.fieldOf("value").forGetter(provider -> provider.value)
        ).apply(instance, EntityItemStackProvider::new);
    });
    private final EntityProvider entity;
    private final String value;

    public EntityItemStackProvider(EntityProvider entity, String value) {
        this.entity = entity;
        this.value = value;
    }

    protected ItemStackProviderType<?> getType() {
        return ItemStackProviderType.ENTITY_STACK_PROVIDER;
    }

    public ItemStack get(Outcome.Context<?> context, Random random) {
        EntityWrapper wrapper = entity.getEntities(context, random);
        if (wrapper == null) {
            return ItemStack.EMPTY;
        }

        if (wrapper.getEntity() == null) {
            wrapper = wrapper.init(context);
        }

        if (wrapper.getEntity() instanceof LivingEntity living) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (value.equals(slot.asString() + "_slot")) {
                    return living.getEquippedStack(slot);
                }
            }
        }

        return switch (value) {
            case "pick_block_stack" -> wrapper.getEntity().getPickBlockStack();
            case "weapon_stack" -> wrapper.getEntity().getWeaponStack();
            case "left_arm_stack" -> {
                if (wrapper.getEntity() instanceof LivingEntity living) {
                    yield living.getStackInArm(Arm.LEFT);
                }
                yield  ItemStack.EMPTY;
            }
            case "right_arm_stack" -> {
                if (wrapper.getEntity() instanceof LivingEntity living) {
                    yield living.getStackInArm(Arm.RIGHT);
                }
                yield  ItemStack.EMPTY;
            }
            case "active_stack" -> {
                if (wrapper.getEntity() instanceof LivingEntity living) {
                    yield living.getActiveItem();
                }
                yield  ItemStack.EMPTY;
            }
            case "blocking_stack" -> {
                if (wrapper.getEntity() instanceof LivingEntity living) {
                    yield living.getBlockingItem();
                }
                yield  ItemStack.EMPTY;
            }
            default -> throw new IllegalStateException("Unexpected value: " + value);
        };
    }
}
