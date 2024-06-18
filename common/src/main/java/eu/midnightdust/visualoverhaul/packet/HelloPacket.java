package eu.midnightdust.visualoverhaul.packet;

import eu.midnightdust.visualoverhaul.VisualOverhaulCommon;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.UUID;

public record HelloPacket(UUID uuid) implements CustomPayload {
    public static final CustomPayload.Id<HelloPacket> PACKET_ID = new CustomPayload.Id<>(VisualOverhaulCommon.HELLO_PACKET);
    public static final PacketCodec<PacketByteBuf, HelloPacket> codec = PacketCodec.of(HelloPacket::write, HelloPacket::read);

    public static HelloPacket read(PacketByteBuf buf) {
        return new HelloPacket(buf.readUuid());
    }

    public void write(PacketByteBuf buf) {
        buf.writeUuid(uuid);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}