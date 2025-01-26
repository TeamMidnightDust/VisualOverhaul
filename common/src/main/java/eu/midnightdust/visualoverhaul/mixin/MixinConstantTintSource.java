package eu.midnightdust.visualoverhaul.mixin;

import eu.midnightdust.visualoverhaul.VisualOverhaulClient;
import eu.midnightdust.visualoverhaul.config.VOConfig;
import net.minecraft.client.render.item.tint.ConstantTintSource;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ConstantTintSource.class)
public abstract class MixinConstantTintSource {
    @Inject(at = @At("RETURN"), method = "getTint", cancellable = true)
    public void vo$modifyLeafTint(ItemStack stack, ClientWorld world, LivingEntity user, CallbackInfoReturnable<Integer> cir) {
        // Dynamic Leaf Item colors
        if (VOConfig.coloredItems && List.of(-12012264).contains(cir.getReturnValue())) {
            cir.setReturnValue(VisualOverhaulClient.foliageColor);
        }
    }
}
