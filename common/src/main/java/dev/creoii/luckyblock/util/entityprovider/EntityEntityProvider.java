package dev.creoii.luckyblock.util.entityprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.function.FunctionContainer;
import dev.creoii.luckyblock.function.wrapper.EntityWrapper;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EntityEntityProvider extends EntityProvider {
    public static final MapCodec<EntityEntityProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(EntityProvider.CODEC.fieldOf("entity").forGetter(provider -> provider.entity),
                Codec.STRING.fieldOf("value").forGetter(provider -> provider.value),
                FunctionContainer.CODEC.optionalFieldOf("functions", FunctionContainer.EMPTY).forGetter(provider -> provider.functions)
        ).apply(instance, EntityEntityProvider::new);
    });
    private final EntityProvider entity;
    private final String value;
    private final FunctionContainer functions;

    protected EntityEntityProvider(EntityProvider entity, String value, FunctionContainer functions) {
        super(false);
        this.entity = entity;
        this.value = value;
        this.functions = functions;
    }

    public static EntityEntityProvider of(EntityProvider provider, String value, FunctionContainer functions) {
        return new EntityEntityProvider(provider, value, functions);
    }

    protected EntityProviderType<?> getType() {
        return EntityProviderType.ENTITY_ENTITY_PROVIDER;
    }

    @Override
    public @Nullable List<EntityWrapper> getEntities(Outcome.Context<?> context, Random random) {
        Entity base = entity.getEntities(context, random).get(0).getEntity();

        if (value.equals("all_passengers")) {
            return base.getPassengerList().stream().map(entity1 -> new EntityWrapper(entity1, FunctionContainer.EMPTY)).toList();
        } else {
            Entity entity1 = switch (value) {
                case "owner" -> {
                    if (base instanceof ProjectileEntity projectile) {
                        yield projectile.getOwner();
                    } else if (base instanceof Tameable tameable) {
                        yield tameable.getOwner();
                    }
                    yield null;
                }
                case "attacker" -> {
                    if (base instanceof LivingEntity living) {
                        yield living.getAttacker();
                    }
                    yield null;
                }
                case "attacking" -> {
                    if (base instanceof LivingEntity living) {
                        yield living.getAttacking();
                    }
                    yield null;
                }
                case "vehicle" -> base.getVehicle();
                case "passenger" -> base.getFirstPassenger();
                case "random_passenger" -> base.getPassengerList().get(random.nextInt(base.getPassengerList().size()));
                default -> throw new IllegalStateException("Unexpected value: " + value);
            };

            if (entity1 != null) {
                System.out.println(entity1.getType());
                return List.of(new EntityWrapper(entity1, functions));
            }

            System.out.println("null entity");
        }

        return null;
    }
}
