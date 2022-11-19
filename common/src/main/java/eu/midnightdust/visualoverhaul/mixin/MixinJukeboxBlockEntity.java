package eu.midnightdust.visualoverhaul.mixin;

import eu.midnightdust.visualoverhaul.util.JukeboxPacketUpdate;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(JukeboxBlockEntity.class)
public abstract class MixinJukeboxBlockEntity extends BlockEntity {

    public MixinJukeboxBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(at = @At("RETURN"), method = "getRecord")
    public void getRecord(CallbackInfoReturnable<ItemStack> cir) {
        JukeboxPacketUpdate.invUpdate = true;
    }
}
