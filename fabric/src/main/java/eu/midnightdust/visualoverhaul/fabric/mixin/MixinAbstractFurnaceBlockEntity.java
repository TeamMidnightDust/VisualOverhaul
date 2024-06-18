package eu.midnightdust.visualoverhaul.fabric.mixin;

import eu.midnightdust.visualoverhaul.VisualOverhaulCommon;
import eu.midnightdust.visualoverhaul.packet.UpdateItemsPacket;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.Stream;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class MixinAbstractFurnaceBlockEntity extends LockableContainerBlockEntity {

    @Shadow protected DefaultedList<ItemStack> inventory;

    @Shadow protected abstract DefaultedList<ItemStack> getHeldStacks();

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
            if (!world.isClient && (visualoverhaul$invUpdate || world.getPlayers().size() != visualoverhaul$playerUpdate)) {
                Stream<ServerPlayerEntity> watchingPlayers = PlayerLookup.tracking(blockEntity).stream();
                DefaultedList<ItemStack> inv = DefaultedList.ofSize(3, ItemStack.EMPTY);
                for (int i = 0; i <= 2; i++) {
                    inv.set(i, blockEntity.getStack(i));
                }

                watchingPlayers.forEach(player -> {
                    if (VisualOverhaulCommon.playersWithMod.contains(player.getUuid())) {
                        ServerPlayNetworking.send(player, new UpdateItemsPacket(VisualOverhaulCommon.UPDATE_TYPE_FURNACE_ITEMS, pos, inv));
                    }
                });
                visualoverhaul$invUpdate = false;
            }
            visualoverhaul$playerUpdate = world.getPlayers().size();
        }
    }

    @Inject(at = @At("RETURN"), method = "getHeldStacks")
    public void getStack(CallbackInfoReturnable<DefaultedList<ItemStack>> cir) {
        visualoverhaul$invUpdate = true;
    }
}
