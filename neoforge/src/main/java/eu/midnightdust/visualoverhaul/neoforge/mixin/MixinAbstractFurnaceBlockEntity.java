package eu.midnightdust.visualoverhaul.neoforge.mixin;

import dev.architectury.networking.NetworkManager;
import eu.midnightdust.visualoverhaul.VisualOverhaul;
import io.netty.buffer.Unpooled;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.Stream;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class MixinAbstractFurnaceBlockEntity extends LockableContainerBlockEntity {

    @Unique
    private static boolean visualoverhaul$invUpdate = true;
    @Unique
    private static int visualoverhaul$playerUpdate = -1;


    protected MixinAbstractFurnaceBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private static void tick(World world, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity, CallbackInfo ci) {
        if (world.getBlockState(pos).hasBlockEntity()) {
            if (!world.isClient && (visualoverhaul$invUpdate || world.getPlayers().size() == visualoverhaul$playerUpdate)) {
                Stream<ServerPlayerEntity> watchingPlayers = ((ServerChunkManager)world.getChunkManager()).threadedAnvilChunkStorage.getPlayersWatchingChunk(new ChunkPos(pos), false).stream();
                PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
                passedData.writeBlockPos(pos);
                passedData.writeItemStack(blockEntity.getStack(0));
                passedData.writeItemStack(blockEntity.getStack(1));
                passedData.writeItemStack(blockEntity.getStack(2));

                watchingPlayers.forEach(player -> {
                    if (VisualOverhaul.playersWithMod.contains(player.getUuid())) {
                        NetworkManager.sendToPlayer(player, VisualOverhaul.UPDATE_FURNACE_ITEMS, passedData);
                    }
                });
                visualoverhaul$invUpdate = false;
            }
            visualoverhaul$playerUpdate = world.getPlayers().size();
        }
    }

    @Inject(at = @At("RETURN"), method = "getStack")
    public void getStack(int slot, CallbackInfoReturnable<ItemStack> cir) {
        visualoverhaul$invUpdate = true;
    }
}
