package eu.midnightdust.visualoverhaul.forge.mixin;

import eu.midnightdust.visualoverhaul.config.VOConfig;
import net.minecraft.block.Blocks;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.world.BiomeColors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockColors.class)
public abstract class MixinBlockColors {
    @Inject(method = "create", at = @At("RETURN"))
    private static void create(CallbackInfoReturnable<BlockColors> info) {
        if (VOConfig.coloredItems) info.getReturnValue().registerColorProvider((state, world, pos, tintIndex) -> world != null ? world.getColor(pos, BiomeColors.FOLIAGE_COLOR) : 0, Blocks.LILY_PAD);
    }
}
