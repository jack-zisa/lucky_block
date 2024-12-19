package dev.creoii.luckyblock.mixin;

import dev.creoii.luckyblock.util.resource.LuckyBlockAddonsResourcePackCreator;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.resource.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin {
    @ModifyVariable(method = "create(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/gui/screen/Screen;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/world/CreateWorldScreen;createServerConfig(Lnet/minecraft/resource/ResourcePackManager;Lnet/minecraft/resource/DataConfiguration;)Lnet/minecraft/server/SaveLoading$ServerConfig;"))
    private static ResourcePackManager lucky$addBuiltInAddonDataPack(ResourcePackManager manager) {
        manager.providers.add(new LuckyBlockAddonsResourcePackCreator(ResourceType.SERVER_DATA));
        return manager;
    }
}
