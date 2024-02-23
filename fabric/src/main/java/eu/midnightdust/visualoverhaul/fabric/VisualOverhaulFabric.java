package eu.midnightdust.visualoverhaul.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.*;

import static eu.midnightdust.visualoverhaul.VisualOverhaul.HELLO_PACKET;
import static eu.midnightdust.visualoverhaul.VisualOverhaul.playersWithMod;

public class VisualOverhaulFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ServerPlayNetworking.registerGlobalReceiver(HELLO_PACKET, (server, player, handler, buf, responseSender) -> playersWithMod.add(player.getUuid()));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> playersWithMod.remove(handler.getPlayer().getUuid()));
    };

}
