package eu.midnightdust.visualoverhaul.fabric.mixin;

import eu.midnightdust.visualoverhaul.VisualOverhaul;
import eu.midnightdust.visualoverhaul.util.JukeboxPacketUpdate;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.stream.Stream;

@Mixin(JukeboxBlock.class)
public abstract class MixinJukeboxBlock extends BlockWithEntity {

    protected MixinJukeboxBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient() ? null : validateTicker(type, BlockEntityType.JUKEBOX, MixinJukeboxBlock::visualoverhaul$tick);
    }
    @Unique
    private static void visualoverhaul$tick(World world, BlockPos pos, BlockState state, JukeboxBlockEntity blockEntity) {
        if (!world.isClient && (JukeboxPacketUpdate.invUpdate || world.getPlayers().size() == JukeboxPacketUpdate.playerUpdate)) {
            Stream<ServerPlayerEntity> watchingPlayers = PlayerLookup.tracking(blockEntity).stream();
            PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
            passedData.writeBlockPos(pos);
            passedData.writeItemStack(blockEntity.getStack());

            watchingPlayers.forEach(player -> {
                if (VisualOverhaul.playersWithMod.contains(player.getUuid())) {
                    ServerPlayNetworking.send(player, VisualOverhaul.UPDATE_RECORD, passedData);
                }
            });
            JukeboxPacketUpdate.invUpdate = false;
        }
        JukeboxPacketUpdate.playerUpdate = world.getPlayers().size();
    }
}

