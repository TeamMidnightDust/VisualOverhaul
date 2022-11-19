package eu.midnightdust.visualoverhaul.forge.mixin;

import eu.midnightdust.visualoverhaul.config.VOConfig;
import eu.midnightdust.visualoverhaul.forge.VisualOverhaulClientForge;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import org.apache.commons.compress.utils.Lists;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static eu.midnightdust.visualoverhaul.VisualOverhaul.MOD_ID;

@Mixin(ResourcePackManager.class)
public abstract class MixinResourcePackManager {
    @Shadow private List<ResourcePackProfile> enabled;

    @Inject(method = "Lnet/minecraft/resource/ResourcePackManager;setEnabledProfiles(Ljava/util/Collection;)V", at = @At("TAIL"))
    private void setDefaultEnabledPacks(CallbackInfo info) {
        if (VOConfig.firstLaunch) {
            List<ResourcePackProfile> enabledPacks = Lists.newArrayList();
            enabledPacks.addAll(enabled);
            enabledPacks.addAll(VisualOverhaulClientForge.defaultEnabledPacks);
            this.enabled = enabledPacks;
            VOConfig.firstLaunch = false;
            VOConfig.write(MOD_ID);
        }
    }
}
