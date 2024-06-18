package eu.midnightdust.visualoverhaul.neoforge;

import eu.midnightdust.visualoverhaul.packet.HelloPacket;
import net.minecraft.client.MinecraftClient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import static eu.midnightdust.visualoverhaul.VisualOverhaulCommon.MOD_ID;
import static eu.midnightdust.visualoverhaul.VisualOverhaulCommon.playersWithMod;

@EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class VisualOverhaulGameEvents {
    @SubscribeEvent()
    public static void sendPacketOnLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getNetworkHandler() != null)
            client.getNetworkHandler().send(new HelloPacket(event.getPlayer().getUuid()));
    }
    @SubscribeEvent
    public static void removeOnLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        playersWithMod.remove(event.getEntity().getUuid());
    }
}
