package dev.creoii.luckyblock.block;

import dev.creoii.luckyblock.LuckyBlockMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
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
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("outcome", 8)) {
            outcomeId = Identifier.tryParse(nbt.getString("outcome"));
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (outcomeId != null) {
            nbt.putString("outcome", outcomeId.toString());
        }
    }
}
