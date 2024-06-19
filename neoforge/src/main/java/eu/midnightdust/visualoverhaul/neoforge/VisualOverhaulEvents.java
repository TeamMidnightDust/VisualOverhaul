package eu.midnightdust.visualoverhaul.neoforge;

import eu.midnightdust.visualoverhaul.VisualOverhaulCommon;
import eu.midnightdust.visualoverhaul.neoforge.handler.UpdateItemsPacketHandler;
import eu.midnightdust.visualoverhaul.packet.HelloPacket;
import eu.midnightdust.visualoverhaul.packet.UpdateItemsPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static eu.midnightdust.visualoverhaul.VisualOverhaulCommon.*;

@EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class VisualOverhaulEvents {
    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.commonToServer(HelloPacket.PACKET_ID, HelloPacket.codec, (payload, context) -> {
            VisualOverhaulCommon.playersWithMod.add(context.player().getUuid());
        });
        registrar.playToClient(UpdateItemsPacket.PACKET_ID, UpdateItemsPacket.codec, (payload, context) -> {
            UpdateItemsPacketHandler.handlePacket(payload);
        });
    }

}
