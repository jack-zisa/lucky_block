package dev.creoii.luckyblock.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.OutcomeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.World;
import net.minecraft.world.explosion.EntityExplosionBehavior;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class LuckyBlockCodecs {
    public static final ConstantIntProvider ONE = ConstantIntProvider.create(1);

    public static Codec<String> DOUBLE = Codec.either(Codec.DOUBLE, Codec.STRING).xmap(either -> {
        return either.map(String::valueOf, Function.identity());
    }, Either::right);

    public static Codec<String> INT = Codec.either(Codec.INT, Codec.STRING).xmap(either -> {
        return either.map(String::valueOf, Function.identity());
    }, Either::right);

    public static Codec<ItemStack> ITEMSTACK = Codec.either(Identifier.CODEC, ItemStack.CODEC).xmap(either -> {
        return either.map(identifier -> Registries.ITEM.get(identifier).getDefaultStack(), Function.identity());
    }, Either::right);

    public static Codec<String> IDENTIFIER = Codec.either(Identifier.CODEC, Codec.STRING).xmap(either -> {
        return either.map(Identifier::toString, Function.identity());
    }, Either::right);

    public static Codec<String> NBT = Codec.either(NbtCompound.CODEC, Codec.STRING).xmap(either -> {
        return either.map(NbtCompound::toString, Function.identity());
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

    public static final MapCodec<Explosion> EXPLOSION = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(Codec.STRING.fieldOf("behavior").orElse("default").forGetter(explosion -> explosion.explosionBehavior),
                Codec.FLOAT.fieldOf("power").forGetter(explosion -> explosion.power),
                Codec.BOOL.fieldOf("create_fire").orElse(false).forGetter(explosion -> explosion.createFire),
                Codec.STRING.fieldOf("explosion_source_type").orElse("none").forGetter(explosion -> explosion.explosionSourceType),
                Codec.STRING.fieldOf("destruction_type").orElse("destroy").forGetter(explosion -> explosion.destructionType),
                ParticleTypes.TYPE_CODEC.fieldOf("particle").orElse(ParticleTypes.EXPLOSION).forGetter(explosion -> explosion.particle),
                ParticleTypes.TYPE_CODEC.fieldOf("emitter_particle").orElse(ParticleTypes.EXPLOSION_EMITTER).forGetter(explosion -> explosion.emitterParticle),
                Registries.SOUND_EVENT.getEntryCodec().fieldOf("sound_event").orElse(SoundEvents.ENTITY_GENERIC_EXPLODE).forGetter(explosion -> explosion.soundEvent)
        ).apply(instance, Explosion::new);
    });

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

    public record Explosion(String explosionBehavior, float power, boolean createFire, String explosionSourceType, String destructionType, ParticleEffect particle, ParticleEffect emitterParticle, RegistryEntry<SoundEvent> soundEvent) {
        public DamageSource getDamageSource(World world, @Nullable Entity entity) {
            DamageType damageType = null;//world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).get(damageSource);
            if (damageType != null) {
                return new DamageSource(RegistryEntry.of(damageType), entity != null ? entity.getPos() : null);
            }
            return net.minecraft.world.explosion.Explosion.createDamageSource(world, entity);
        }

        public net.minecraft.world.explosion.ExplosionBehavior getBehavior(OutcomeContext context, @Nullable Entity entity) {
            if (entity != null) {
                return new EntityExplosionBehavior(entity);
            }
            return switch (explosionBehavior.toLowerCase()) {
                case "entity" -> new EntityExplosionBehavior(context.player());
                case "wind_charge" -> new WindChargeEntity.WindChargeExplosionBehavior();
                default -> new net.minecraft.world.explosion.ExplosionBehavior();
            };
        }

        public World.ExplosionSourceType getExplosionSourceType() {
            return World.ExplosionSourceType.valueOf(explosionSourceType.toUpperCase());
        }
    }
}
