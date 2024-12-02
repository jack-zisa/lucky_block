package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import dev.creoii.luckyblock.util.vec.VecProvider;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.intprovider.IntProvider;

import java.util.List;
import java.util.Optional;

public class SoundOutcome extends Outcome {
    public static final MapCodec<SoundOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalWeightField(Outcome::getWeightProvider),
                createGlobalDelayField(Outcome::getDelay),
                createGlobalPosField(Outcome::getPos),
                SoundEvent.CODEC.fieldOf("sound_event").forGetter(outcome -> outcome.soundEvent),
                FloatProvider.createValidatedCodec(0f, Float.MAX_VALUE).fieldOf("volume").orElse(LuckyBlockCodecs.ONE_F).forGetter(outcome -> outcome.volume),
                FloatProvider.createValidatedCodec(0f, Float.MAX_VALUE).fieldOf("pitch").orElse(LuckyBlockCodecs.ONE_F).forGetter(outcome -> outcome.pitch)
        ).apply(instance, SoundOutcome::new);
    });
    private final SoundEvent soundEvent;
    private final FloatProvider volume;
    private final FloatProvider pitch;

    public SoundOutcome(int luck, float chance, IntProvider weightProvider, int delay, Optional<VecProvider> pos, SoundEvent soundEvent, FloatProvider volume, FloatProvider pitch) {
        super(OutcomeType.SOUND, luck, chance, weightProvider, delay, pos, false);
        this.soundEvent = soundEvent;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public void run(Context context) {
        Vec3d pos = getPos().isPresent() ? getPos().get().getVec(context) : context.pos().toCenterPos();
        float volume = this.volume.get(context.world().getRandom());
        float pitch = this.pitch.get(context.world().getRandom());

        double d = MathHelper.square(soundEvent.getDistanceToTravel(volume));

        List<ServerPlayerEntity> players = context.world().getServer().getPlayerManager().getPlayerList().stream().filter(serverPlayer -> {
            return pos.squaredDistanceTo(serverPlayer.getPos()) <= d;
        }).toList();

        for (ServerPlayerEntity serverPlayer : players) {
            float j;
            while (true) {
                double e = pos.x - serverPlayer.getX();
                double f = pos.y - serverPlayer.getY();
                double g = pos.z - serverPlayer.getZ();
                double h = e * e + f * f + g * g;
                j = volume;
                if (!(h > d)) {
                    break;
                }
            }

            serverPlayer.playSoundToPlayer(soundEvent, SoundCategory.NEUTRAL, j, pitch);
        }
    }
}
