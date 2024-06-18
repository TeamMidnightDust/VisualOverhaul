package eu.midnightdust.visualoverhaul.neoforge.mixin;

import eu.midnightdust.visualoverhaul.config.VOConfig;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static eu.midnightdust.visualoverhaul.VisualOverhaulClient.*;
import static eu.midnightdust.visualoverhaul.VisualOverhaulClient.potionColor;

@Mixin(ItemColors.class)
public abstract class MixinItemColors {
    @SuppressWarnings("deprecation")
    @Inject(method = "create", at = @At("RETURN"))
    private static void create(BlockColors blockMap, CallbackInfoReturnable<ItemColors> info) {
        if (VOConfig.coloredItems) {
            ItemColors itemColors = info.getReturnValue();
            itemColors.register((stack, tintIndex) -> tintIndex == 0 ? -1 : waterColor, Items.WATER_BUCKET, Items.AXOLOTL_BUCKET, Items.COD_BUCKET, Items.PUFFERFISH_BUCKET, Items.TROPICAL_FISH_BUCKET, Items.SALMON_BUCKET);
            itemColors.register((stack, tintIndex) -> grassColor, Items.GRASS_BLOCK, Items.SHORT_GRASS, Items.TALL_GRASS, Items.FERN, Items.LARGE_FERN);
            itemColors.register((stack, tintIndex) -> foliageColor, Items.OAK_LEAVES, Items.JUNGLE_LEAVES, Items.DARK_OAK_LEAVES, Items.ACACIA_LEAVES, Items.VINE, Items.SUGAR_CANE);
            if (VOConfig.coloredLilypad) itemColors.register((stack, tintIndex) -> foliageColor, Items.LILY_PAD);
            itemColors.register((stack, tintIndex) -> {
                var contents = stack.getComponents().get(DataComponentTypes.POTION_CONTENTS);
                if (contents == null || contents.potion().isEmpty()) return tintIndex > 0 ? -1 : potionColor;
                var potion = contents.potion().get();
                if ((potion == Potions.WATER || potion == Potions.MUNDANE || potion == Potions.THICK || potion == Potions.AWKWARD) && tintIndex == 0) {
                    return potionColor;
                }
                return tintIndex > 0 ? -1 : contents.getColor();
            }, Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);
        }
    }
}
