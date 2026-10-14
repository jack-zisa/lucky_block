package dev.creoii.luckyblock.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class LuckyBlockCodecs {
    public static final ConstantInt ONE = ConstantInt.of(1);
    public static final ConstantFloat ONE_F = ConstantFloat.of(1f);

    public record Explosion(String explosionBehavior, float power, boolean createFire, String explosionSourceType, String destructionType, ParticleOptions particle, ParticleOptions emitterParticle, Holder.Reference<SoundEvent> soundEvent) {
        public static final MapCodec<Explosion> CODEC = RecordCodecBuilder.mapCodec(instance -> {
            return instance.group(Codec.STRING.fieldOf("behavior").orElse("default").forGetter(explosion -> explosion.explosionBehavior),
                    Codec.FLOAT.fieldOf("power").forGetter(explosion -> explosion.power),
                    Codec.BOOL.fieldOf("create_fire").orElse(false).forGetter(explosion -> explosion.createFire),
                    Codec.STRING.fieldOf("explosion_source_type").orElse("block").forGetter(explosion -> explosion.explosionSourceType),
                    Codec.STRING.fieldOf("destruction_type").orElse("destroy").forGetter(explosion -> explosion.destructionType),
                    ParticleTypes.CODEC.fieldOf("particle").orElse(ParticleTypes.EXPLOSION).forGetter(explosion -> explosion.particle),
                    ParticleTypes.CODEC.fieldOf("emitter_particle").orElse(ParticleTypes.EXPLOSION_EMITTER).forGetter(explosion -> explosion.emitterParticle),
                    BuiltInRegistries.SOUND_EVENT.holderByNameCodec().fieldOf("sound_event").orElse(SoundEvents.GENERIC_EXPLODE).forGetter(explosion -> explosion.soundEvent)
            ).apply(instance, Explosion::new);
        });

        public DamageSource getDamageSource(Level world, @Nullable Entity entity) {
            DamageType damageType = null;//world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).get(damageSource);
            if (damageType != null) {
                return new DamageSource(Holder.direct(damageType), entity != null ? entity.position() : null);
            }
            return net.minecraft.world.level.Explosion.getDefaultDamageSource(world, entity);
        }

        public net.minecraft.world.level.ExplosionBehavior getBehavior(Outcome.Context context, @Nullable Entity entity) {
            if (entity != null) {
                return new EntityExplosionBehavior(entity);
            }
            return switch (explosionBehavior.toLowerCase()) {
                case "entity" -> new EntityExplosionBehavior(context.player());
                case "wind_charge" -> WindChargeEntity.EXPLOSION_BEHAVIOR;
                default -> new net.minecraft.world.explosion.ExplosionBehavior();
            };
        }

        public Level.ExplosionInteraction getExplosionSourceType() {
            return Level.ExplosionInteraction.valueOf(explosionSourceType.toUpperCase());
        }
    }

    public record StructurePlacementData(BlockMirror mirror, BlockRotation rotation, boolean ignoreEntities, boolean placeFluids, @Nullable RegistryEntry<StructureProcessorList> processor, boolean updateNeighbors, boolean initializeMobs) {
        public static final StructurePlacementData DEFAULT = new StructurePlacementData(BlockMirror.NONE, BlockRotation.NONE, false, true, null, false, false);
        public static final MapCodec<StructurePlacementData> CODEC = RecordCodecBuilder.mapCodec(instance -> {
            return instance.group(BlockMirror.CODEC.fieldOf("mirror").orElse(BlockMirror.NONE).forGetter(structurePlacementData -> structurePlacementData.mirror),
                    BlockRotation.CODEC.fieldOf("rotation").forGetter(structurePlacementData -> structurePlacementData.rotation),
                    Codec.BOOL.fieldOf("ignore_entities").orElse(false).forGetter(structurePlacementData -> structurePlacementData.ignoreEntities),
                    Codec.BOOL.fieldOf("place_fluids").orElse(true).forGetter(structurePlacementData -> structurePlacementData.placeFluids),
                    StructureProcessorType.REGISTRY_CODEC.fieldOf("processor").orElse(null).forGetter(structurePlacementData -> structurePlacementData.processor),
                    Codec.BOOL.fieldOf("update_neighbors").orElse(false).forGetter(structurePlacementData -> structurePlacementData.updateNeighbors),
                    Codec.BOOL.fieldOf("initialize_mobs").orElse(false).forGetter(structurePlacementData -> structurePlacementData.initializeMobs)
            ).apply(instance, StructurePlacementData::new);
        });

        public net.minecraft.structure.StructurePlacementData create() {
            net.minecraft.structure.StructurePlacementData structurePlacementData = new net.minecraft.structure.StructurePlacementData();
            structurePlacementData.setMirror(mirror);
            structurePlacementData.setRotation(rotation);
            structurePlacementData.setIgnoreEntities(ignoreEntities);
            structurePlacementData.setUpdateNeighbors(updateNeighbors);
            structurePlacementData.setInitializeMobs(initializeMobs);
            if (processor != null)
                processor.value().getList().forEach(structurePlacementData::addProcessor);
            return structurePlacementData;
        }
    }
}
