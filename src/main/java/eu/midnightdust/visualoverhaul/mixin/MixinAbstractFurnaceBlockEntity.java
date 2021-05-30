package eu.midnightdust.visualoverhaul.mixin;

import eu.midnightdust.visualoverhaul.VisualOverhaul;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.fabricmc.fabric.impl.networking.ServerSidePacketRegistryImpl;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.Stream;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class MixinAbstractFurnaceBlockEntity extends LockableContainerBlockEntity {

    @Shadow protected DefaultedList<ItemStack> inventory;
    Boolean invUpdate = true;
    int playerUpdate = -1;

    private MixinAbstractFurnaceBlockEntity(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
    }

    @Inject(at = @At("TAIL"), method = "tick")
    public void tick(CallbackInfo ci) {
        if (this.world.getBlockState(this.pos).getBlock().is(Blocks.FURNACE)) {
            if (!this.world.isClient && (invUpdate || world.getPlayers().size() == playerUpdate)) {
                Stream<PlayerEntity> watchingPlayers = PlayerStream.watching(world, getPos());
                PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
                passedData.writeBlockPos(pos);
                passedData.writeItemStack(inventory.get(0));
                passedData.writeItemStack(inventory.get(1));
                passedData.writeItemStack(inventory.get(2));

                passedData.writeString(String.valueOf(inventory));
                watchingPlayers.forEach(player -> ServerSidePacketRegistryImpl.INSTANCE.sendToPlayer(player, VisualOverhaul.UPDATE_FURNACE_ITEMS, passedData));
                invUpdate = false;
            }
            playerUpdate = world.getPlayers().size();
        }
    }

    @Inject(at = @At("RETURN"), method = "getStack", cancellable = true)
    public void getStack(int slot, CallbackInfoReturnable cir) {
        this.invUpdate = true;
    }
}
