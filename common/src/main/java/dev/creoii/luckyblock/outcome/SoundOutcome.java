package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.ContextualProvider;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import dev.creoii.luckyblock.util.vec.VecProvider;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.FloatProviders;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class SoundOutcome extends Outcome {
    public static final MapCodec<SoundOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalWeightField(Outcome::getWeightProvider),
                createGlobalDelayField(outcome -> outcome.delay),
                createGlobalPosField(Outcome::getPos),
                SoundEvent.CODEC.fieldOf("sound_event").forGetter(outcome -> outcome.soundEvent),
                FloatProviders.codec(0f, Float.MAX_VALUE).fieldOf("volume").orElse(LuckyBlockCodecs.ONE_F).forGetter(outcome -> outcome.volume),
                FloatProviders.codec(0f, Float.MAX_VALUE).fieldOf("pitch").orElse(LuckyBlockCodecs.ONE_F).forGetter(outcome -> outcome.pitch)
        ).apply(instance, SoundOutcome::new);
    });
    private final Holder<SoundEvent> soundEvent;
    private final FloatProvider volume;
    private final FloatProvider pitch;

    public SoundOutcome(int luck, float chance, IntProvider weightProvider, IntProvider delay, Optional<VecProvider> pos, Holder<SoundEvent> soundEvent, FloatProvider volume, FloatProvider pitch) {
        super(OutcomeType.SOUND, luck, chance, weightProvider, delay, pos, false);
        this.soundEvent = soundEvent;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public void run(Context context) {
        Vec3 pos = getPos().isPresent() ? getPos().get().getVec(context) : Vec3.atCenterOf(context.pos());
        float volume = ContextualProvider.applyFloatContext(this.volume, context).sample(context.world().getRandom());
        float pitch = ContextualProvider.applyFloatContext(this.pitch, context).sample(context.world().getRandom());

        double d = Mth.square(soundEvent.value().getRange(volume));

        List<ServerPlayer> players = context.world().getServer().getPlayerList().getPlayers().stream().filter(serverPlayer -> {
            return pos.distanceToSqr(serverPlayer.position()) <= d;
        }).toList();

        for (ServerPlayer serverPlayer : players) {
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

            serverPlayer.playSound(soundEvent.value(), j, pitch);
        }
    }
}
