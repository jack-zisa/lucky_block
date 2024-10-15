package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;

public class NoneOutcome extends Outcome {
    public static final Outcome INSTANCE = new NoneOutcome();
    public static final MapCodec<Outcome> CODEC = MapCodec.unit(INSTANCE);

    public NoneOutcome() {
        super(OutcomeType.NONE);
    }

    @Override
    public void run(Context context) {}
}
