package dev.creoii.luckyblock.block;

import dev.creoii.luckyblock.LuckyBlockMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class LuckyBlockEntity extends BlockEntity {
    @Nullable private Identifier outcomeId;

    public LuckyBlockEntity(BlockPos pos, BlockState state) {
        super(LuckyBlockMod.LUCKY_BLOCK_ENTITY, pos, state);
        outcomeId = null;
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public @Nullable Identifier getOutcomeId() {
        return outcomeId;
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        outcomeId = view.read("outcome", Identifier.CODEC).orElse(Identifier.of("lucky:none"));
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        if (outcomeId != null) {
            view.putString("outcome", outcomeId.toString());
        }
    }
}
