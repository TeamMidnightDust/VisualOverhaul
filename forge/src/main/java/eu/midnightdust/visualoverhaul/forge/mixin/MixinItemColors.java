package eu.midnightdust.visualoverhaul.forge.mixin;

import eu.midnightdust.visualoverhaul.config.VOConfig;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static eu.midnightdust.visualoverhaul.forge.VisualOverhaulClientForge.grassColor;
import static eu.midnightdust.visualoverhaul.forge.VisualOverhaulClientForge.foliageColor;
import static eu.midnightdust.visualoverhaul.forge.VisualOverhaulClientForge.waterColor;

@Mixin(ItemColors.class)
public abstract class MixinItemColors {
    @Inject(method = "create", at = @At("RETURN"))
    private static void create(BlockColors blockMap, CallbackInfoReturnable<ItemColors> info) {
        if (VOConfig.coloredItems) {
            ItemColors itemColors = info.getReturnValue();
            itemColors.register((stack, tintIndex) -> tintIndex == 0 ? -1 : waterColor, Items.WATER_BUCKET);
            itemColors.register((stack, tintIndex) -> tintIndex == 0 ? -1 : waterColor, Items.AXOLOTL_BUCKET);
            itemColors.register((stack, tintIndex) -> tintIndex == 0 ? -1 : waterColor, Items.COD_BUCKET);
            itemColors.register((stack, tintIndex) -> tintIndex == 0 ? -1 : waterColor, Items.PUFFERFISH_BUCKET);
            itemColors.register((stack, tintIndex) -> tintIndex == 0 ? -1 : waterColor, Items.TROPICAL_FISH_BUCKET);
            itemColors.register((stack, tintIndex) -> tintIndex == 0 ? -1 : waterColor, Items.SALMON_BUCKET);
            itemColors.register((stack, tintIndex) -> grassColor, Items.GRASS_BLOCK);
            itemColors.register((stack, tintIndex) -> grassColor, Items.GRASS);
            itemColors.register((stack, tintIndex) -> grassColor, Items.TALL_GRASS);
            itemColors.register((stack, tintIndex) -> grassColor, Items.FERN);
            itemColors.register((stack, tintIndex) -> grassColor, Items.LARGE_FERN);
            itemColors.register((stack, tintIndex) -> foliageColor, Items.ACACIA_LEAVES);
            itemColors.register((stack, tintIndex) -> foliageColor, Items.DARK_OAK_LEAVES);
            itemColors.register((stack, tintIndex) -> foliageColor, Items.JUNGLE_LEAVES);
            itemColors.register((stack, tintIndex) -> foliageColor, Items.OAK_LEAVES);
            itemColors.register((stack, tintIndex) -> foliageColor, Items.VINE);
            itemColors.register((stack, tintIndex) -> foliageColor, Items.SUGAR_CANE);
            if (VOConfig.coloredLilypad) itemColors.register((stack, tintIndex) -> foliageColor, Items.LILY_PAD);
            itemColors.register((stack, tintIndex) -> {
                if ((PotionUtil.getPotion(stack) == Potions.WATER || PotionUtil.getPotion(stack) == Potions.MUNDANE || PotionUtil.getPotion(stack) == Potions.THICK || PotionUtil.getPotion(stack) == Potions.AWKWARD) && tintIndex == 0) {
                    return waterColor;
                }
                return tintIndex > 0 ? -1 : PotionUtil.getColor(stack);
            }, Items.POTION);
            itemColors.register((stack, tintIndex) -> {
                if ((PotionUtil.getPotion(stack) == Potions.WATER || PotionUtil.getPotion(stack) == Potions.MUNDANE || PotionUtil.getPotion(stack) == Potions.THICK || PotionUtil.getPotion(stack) == Potions.AWKWARD) && tintIndex == 0) {
                    return waterColor;
                }
                return tintIndex > 0 ? -1 : PotionUtil.getColor(stack);
            }, Items.SPLASH_POTION);
            itemColors.register((stack, tintIndex) -> {
                if ((PotionUtil.getPotion(stack) == Potions.WATER || PotionUtil.getPotion(stack) == Potions.MUNDANE || PotionUtil.getPotion(stack) == Potions.THICK || PotionUtil.getPotion(stack) == Potions.AWKWARD) && tintIndex == 0) {
                    return waterColor;
                }
                return tintIndex > 0 ? -1 : PotionUtil.getColor(stack);
            }, Items.LINGERING_POTION);
        }
    }
}
