package eu.midnightdust.visualoverhaul.mixin;

import eu.midnightdust.visualoverhaul.config.VOConfig;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionItem.class)
public abstract class MixinPotionItem extends Item {
    public MixinPotionItem(Settings settings) {
        super(settings);
    }

    @Inject(at = @At("HEAD"), method = "hasGlint", cancellable = true)
    public void vo$hasGlint(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!VOConfig.potionEnchantmentGlint) cir.setReturnValue(super.hasGlint(stack));
    }
}
