package eu.midnightdust.visualoverhaul.mixin;

import eu.midnightdust.visualoverhaul.VisualOverhaul;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.fabricmc.fabric.impl.networking.ServerSidePacketRegistryImpl;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Tickable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.Stream;

@Mixin(JukeboxBlockEntity.class)
public abstract class MixinJukeboxBlockEntity extends BlockEntity implements Tickable {

    @Shadow private ItemStack record;
    Boolean invUpdate = true;
    int playerUpdate = -1;

    private MixinJukeboxBlockEntity(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
    }

    @Unique
    public void tick() {
        if (!this.world.isClient && (invUpdate || world.getPlayers().size() == playerUpdate)) {
            Stream<PlayerEntity> watchingPlayers = PlayerStream.watching(world, getPos());
            PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
            passedData.writeBlockPos(pos);
            passedData.writeItemStack(record);

            watchingPlayers.forEach(player -> ServerSidePacketRegistryImpl.INSTANCE.sendToPlayer(player, VisualOverhaul.UPDATE_RECORD, passedData));
            invUpdate = false;
        }
        playerUpdate = world.getPlayers().size();
    }

    @Inject(at = @At("RETURN"), method = "getRecord", cancellable = true)
    public void getRecord(CallbackInfoReturnable cir) {
        this.invUpdate = true;
    }
}
