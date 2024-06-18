package eu.midnightdust.visualoverhaul.neoforge.mixin;

import eu.midnightdust.visualoverhaul.VisualOverhaulCommon;
import eu.midnightdust.visualoverhaul.packet.UpdateItemsPacket;
import eu.midnightdust.visualoverhaul.util.JukeboxPacketUpdate;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
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
            Stream<ServerPlayerEntity> watchingPlayers = ((ServerChunkManager)world.getChunkManager()).chunkLoadingManager.getPlayersWatchingChunk(new ChunkPos(pos), false).stream();
            watchingPlayers.forEach(player -> {
                if (VisualOverhaulCommon.playersWithMod.contains(player.getUuid())) {
                    player.networkHandler.send(new UpdateItemsPacket(VisualOverhaulCommon.UPDATE_TYPE_RECORD, pos, DefaultedList.ofSize(1, blockEntity.getStack())));
                }
            });
            //JukeboxPacketUpdate.invUpdate = false;
        }
        JukeboxPacketUpdate.playerUpdate = world.getPlayers().size();
    }
}

