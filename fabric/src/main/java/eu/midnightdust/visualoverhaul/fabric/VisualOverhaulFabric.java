package eu.midnightdust.visualoverhaul.fabric;

import eu.midnightdust.visualoverhaul.packet.HelloPacket;
import eu.midnightdust.visualoverhaul.packet.UpdateItemsPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.*;

import static eu.midnightdust.visualoverhaul.VisualOverhaulCommon.playersWithMod;

public class VisualOverhaulFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(HelloPacket.PACKET_ID, HelloPacket.codec);
        PayloadTypeRegistry.playS2C().register(UpdateItemsPacket.PACKET_ID, UpdateItemsPacket.codec);

        ServerPlayNetworking.registerGlobalReceiver(HelloPacket.PACKET_ID, (payload, context) -> playersWithMod.add(context.player().getUuid()));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> playersWithMod.remove(handler.getPlayer().getUuid()));
    };

}
