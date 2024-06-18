package eu.midnightdust.visualoverhaul.packet;

import eu.midnightdust.visualoverhaul.VisualOverhaulCommon;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public record UpdateItemsPacket(Identifier blockTypeID, BlockPos pos, DefaultedList<ItemStack> inv) implements CustomPayload {
    public static final Id<UpdateItemsPacket> PACKET_ID = new Id<>(VisualOverhaulCommon.UPDATE_ITEMS_PACKET);
    public static final PacketCodec<RegistryByteBuf, UpdateItemsPacket> codec = PacketCodec.of(UpdateItemsPacket::write, UpdateItemsPacket::read);

    public static UpdateItemsPacket read(RegistryByteBuf buf) {
        return new UpdateItemsPacket(buf.readIdentifier(), buf.readBlockPos(), (DefaultedList<ItemStack>) ItemStack.OPTIONAL_LIST_PACKET_CODEC.decode(buf));
    }

    public void write(RegistryByteBuf buf) {
        buf.writeIdentifier(blockTypeID);
        buf.writeBlockPos(pos);
        ItemStack.OPTIONAL_LIST_PACKET_CODEC.encode(buf, this.inv);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}