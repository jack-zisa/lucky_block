package dev.creoii.luckyblock.mixin;

import dev.creoii.luckyblock.util.resource.LuckyBlockAddonsResourcePackCreator;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.resource.*;
import net.minecraft.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {
    @Shadow private ResourcePackManager packManager;

    private CreateWorldScreenMixin() {
        super(null);
    }

    @ModifyVariable(method = "create(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/gui/screen/Screen;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/world/CreateWorldScreen;createServerConfig(Lnet/minecraft/resource/ResourcePackManager;Lnet/minecraft/resource/DataConfiguration;)Lnet/minecraft/server/SaveLoading$ServerConfig;"))
    private static ResourcePackManager lucky$onCreateResManagerInit(ResourcePackManager manager) {
        manager.providers.add(new LuckyBlockAddonsResourcePackCreator(ResourceType.SERVER_DATA));
        return manager;
    }

    @Inject(method = "getScannedPack", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourcePackManager;scanPacks()V", shift = At.Shift.BEFORE))
    private void lucky$onScanPacks(CallbackInfoReturnable<Pair<File, ResourcePackManager>> cir) {
        this.packManager.providers.add(new LuckyBlockAddonsResourcePackCreator(ResourceType.SERVER_DATA));
    }
}
