package eu.midnightdust.visualoverhaul.mixin;

import eu.midnightdust.visualoverhaul.VisualOverhaulClient;
import eu.midnightdust.visualoverhaul.config.VOConfig;
import net.minecraft.client.render.item.tint.GrassTintSource;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GrassTintSource.class)
public abstract class MixinGrassTintSource {
    @Inject(at = @At("RETURN"), method = "getTint", cancellable = true)
    public void vo$modifyGrassTint(ItemStack stack, ClientWorld world, LivingEntity user, CallbackInfoReturnable<Integer> cir) {
        // Dynamic Grass Item colors
        if (VOConfig.coloredItems) {
            cir.setReturnValue(VisualOverhaulClient.grassColor);
        }
    }
}
