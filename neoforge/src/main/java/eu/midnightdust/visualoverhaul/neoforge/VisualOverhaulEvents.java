package eu.midnightdust.visualoverhaul.neoforge;

import eu.midnightdust.visualoverhaul.VisualOverhaulCommon;
import eu.midnightdust.visualoverhaul.packet.HelloPacket;
import eu.midnightdust.visualoverhaul.packet.UpdateItemsPacket;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static eu.midnightdust.visualoverhaul.VisualOverhaulCommon.*;
import static eu.midnightdust.visualoverhaul.VisualOverhaulCommon.UPDATE_TYPE_FURNACE_ITEMS;

@EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class VisualOverhaulEvents {
    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.commonToServer(HelloPacket.PACKET_ID, HelloPacket.codec, (payload, context) -> {
            VisualOverhaulCommon.playersWithMod.add(context.player().getUuid());
        });
        registrar.playToClient(UpdateItemsPacket.PACKET_ID, UpdateItemsPacket.codec, (payload, context) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            client.execute(() -> {
                System.out.println(payload.blockTypeID().toString());
                if (payload.blockTypeID().equals(UPDATE_TYPE_RECORD))
                    jukeboxItems.put(payload.pos(), payload.inv().getFirst());
                else if (client.world != null && client.world.getBlockEntity(payload.pos()) != null) {
                    if (payload.blockTypeID().equals(UPDATE_TYPE_POTION_BOTTLES) && client.world.getBlockEntity(payload.pos()) instanceof BrewingStandBlockEntity brewingStand) {
                        for (int i = 0; i <= 4; i++) {
                            brewingStand.setStack(i, payload.inv().get(i));
                        }
                    } else if (payload.blockTypeID().equals(UPDATE_TYPE_FURNACE_ITEMS) && client.world.getBlockEntity(payload.pos()) instanceof AbstractFurnaceBlockEntity furnace) {
                        for (int i = 0; i <= 2; i++) {
                            furnace.setStack(i, payload.inv().get(i));
                        }
                    }
                }
            });
        });
    }

}
