package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;

public class NoneOutcome extends Outcome<NoneOutcome.NoneInfo> {
    public static final Outcome<NoneOutcome.NoneInfo> INSTANCE = new NoneOutcome();
    public static final MapCodec<Outcome<NoneOutcome.NoneInfo>> CODEC = MapCodec.unit(INSTANCE);

    public NoneOutcome() {
        super(OutcomeType.NONE);
    }

    @Override
    public void run(Context<NoneOutcome.NoneInfo> context) {}

    public record NoneInfo() implements ContextInfo {}
}
