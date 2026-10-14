package dev.creoii.luckyblock.block;

import dev.creoii.luckyblock.LuckyBlockMod;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class LuckyBlockEntity extends BlockEntity {
    @Nullable private Identifier outcomeId;

    public LuckyBlockEntity(BlockPos pos, BlockState state) {
        super(LuckyBlockMod.LUCKY_BLOCK_ENTITY, pos, state);
        outcomeId = null;
    }

    public @Nullable Identifier getOutcomeId() {
        return outcomeId;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        outcomeId = input.read("outcome", Identifier.CODEC).orElse(Identifier.parse("lucky:none"));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        if (outcomeId != null) {
            output.putString("outcome", outcomeId.toString());
        }
    }
}
