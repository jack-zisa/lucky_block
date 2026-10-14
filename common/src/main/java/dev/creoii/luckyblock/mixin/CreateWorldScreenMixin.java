package dev.creoii.luckyblock.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.creoii.luckyblock.util.resource.LuckyBlockAddonsResourcePackCreator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin {
    @ModifyVariable(method = "openCreateWorldScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/worldselection/CreateWorldScreen;createDefaultLoadConfig(Lnet/minecraft/server/packs/repository/PackRepository;Lnet/minecraft/world/level/WorldDataConfiguration;)Lnet/minecraft/server/WorldLoader$InitConfig;"))
    private static Minecraft lucky$addBuiltInAddonDataPack(Minecraft minecraft, @Local(name = "vanillaOnlyPackRepository") PackRepository vanillaOnlyPackRepository) {
        vanillaOnlyPackRepository.getSelectedPacks().add(new LuckyBlockAddonsResourcePackCreator(ResourceType.SERVER_DATA));
        return manager;
    }
}