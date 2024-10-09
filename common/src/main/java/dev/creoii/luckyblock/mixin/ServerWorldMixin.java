package dev.creoii.luckyblock.mixin;

import dev.creoii.luckyblock.LuckyBlockMod;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
    @Shadow @NotNull public abstract MinecraftServer getServer();

    @Inject(method = "tick", at = @At("TAIL"))
    private void gbw$tickOutcomeDelays(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        LuckyBlockMod.OUTCOME_MANAGER.tickDelays(getServer());
    }
}
