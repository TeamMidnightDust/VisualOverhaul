package eu.midnightdust.visualoverhaul.mixin;

import eu.midnightdust.visualoverhaul.VisualOverhaulClient;
import eu.midnightdust.visualoverhaul.config.VOConfig;
import net.minecraft.client.render.item.tint.PotionTintSource;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PotionTintSource.class)
public class MixinPotionTintSource {
    @Unique private static final List<RegistryEntry<Potion>> WATER_POTIONS = List.of(Potions.WATER, Potions.MUNDANE, Potions.THICK, Potions.AWKWARD);

    @Inject(at = @At("RETURN"), method = "getTint", cancellable = true)
    public void vo$modifyWaterTint(ItemStack stack, ClientWorld world, LivingEntity user, CallbackInfoReturnable<Integer> cir) {
        // Dynamic Potion Item colors
        if (VOConfig.coloredItems) {
            var contents = stack.getComponents().get(DataComponentTypes.POTION_CONTENTS);
            if (contents != null && contents.potion().isPresent()) {
                if (!WATER_POTIONS.contains(contents.potion().get()))
                    return; // Skip all potions with effects
            }
            if (cir.getReturnValue() == -1) cir.setReturnValue(ColorHelper.fullAlpha(VisualOverhaulClient.potionColor));
            else cir.setReturnValue(VisualOverhaulClient.potionColor);
        }
    }
}
