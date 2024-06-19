package eu.midnightdust.visualoverhaul.neoforge;

import eu.midnightdust.visualoverhaul.packet.HelloPacket;
import net.minecraft.client.MinecraftClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

import static eu.midnightdust.visualoverhaul.VisualOverhaulCommon.MOD_ID;

@EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class VisualOverhaulClientGameEvents {
    @SubscribeEvent()
    public static void sendPacketOnLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getNetworkHandler() != null)
            client.getNetworkHandler().send(new HelloPacket(event.getPlayer().getUuid()));
    }
}
