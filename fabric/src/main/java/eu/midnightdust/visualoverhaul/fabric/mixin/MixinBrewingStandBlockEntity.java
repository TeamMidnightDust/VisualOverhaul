package eu.midnightdust.visualoverhaul.fabric.mixin;

import eu.midnightdust.visualoverhaul.VisualOverhaul;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.Stream;

@Mixin(BrewingStandBlockEntity.class)
public abstract class MixinBrewingStandBlockEntity extends LockableContainerBlockEntity {

    @Unique
    private static boolean visualoverhaul$invUpdate = true;
    @Unique
    private static int visualoverhaul$playerUpdate = -1;

    protected MixinBrewingStandBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private static void tick(World world, BlockPos pos, BlockState state, BrewingStandBlockEntity blockEntity, CallbackInfo ci) {
        if (!world.isClient && (visualoverhaul$invUpdate || world.getPlayers().size() == visualoverhaul$playerUpdate)) {
            Stream<ServerPlayerEntity> watchingPlayers = PlayerLookup.tracking(blockEntity).stream();
            PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
            passedData.writeBlockPos(pos);
            passedData.writeItemStack(blockEntity.getStack(0));
            passedData.writeItemStack(blockEntity.getStack(1));
            passedData.writeItemStack(blockEntity.getStack(2));
            passedData.writeItemStack(blockEntity.getStack(3));
            passedData.writeItemStack(blockEntity.getStack(4));

            watchingPlayers.forEach(player -> {
                if (VisualOverhaul.playersWithMod.contains(player.getUuid())) {
                    ServerPlayNetworking.send(player, VisualOverhaul.UPDATE_POTION_BOTTLES, passedData);
                }
            });
            visualoverhaul$invUpdate = false;
        }
        visualoverhaul$playerUpdate = world.getPlayers().size();
    }

    @Inject(at = @At("RETURN"), method = "getStack")
    public void getStack(int slot, CallbackInfoReturnable<ItemStack> cir) {
        visualoverhaul$invUpdate = true;
    }
}
