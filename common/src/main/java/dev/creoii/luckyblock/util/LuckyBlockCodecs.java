package dev.creoii.luckyblock.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.World;
import net.minecraft.world.explosion.EntityExplosionBehavior;
import org.jetbrains.annotations.Nullable;

public class LuckyBlockCodecs {
    public static final ConstantIntProvider ONE = ConstantIntProvider.create(1);

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

    public record Explosion(String explosionBehavior, float power, boolean createFire, String explosionSourceType, String destructionType, ParticleEffect particle, ParticleEffect emitterParticle, RegistryEntry<SoundEvent> soundEvent) {
        public DamageSource getDamageSource(World world, @Nullable Entity entity) {
            DamageType damageType = null;//world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).get(damageSource);
            if (damageType != null) {
                return new DamageSource(RegistryEntry.of(damageType), entity != null ? entity.getPos() : null);
            }
            return net.minecraft.world.explosion.Explosion.createDamageSource(world, entity);
        }

        public net.minecraft.world.explosion.ExplosionBehavior getBehavior(Outcome.Context context, @Nullable Entity entity) {
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
