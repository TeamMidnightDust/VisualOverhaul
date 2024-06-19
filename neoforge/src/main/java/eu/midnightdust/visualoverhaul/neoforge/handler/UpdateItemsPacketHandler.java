package eu.midnightdust.visualoverhaul.neoforge.handler;

import eu.midnightdust.visualoverhaul.packet.UpdateItemsPacket;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.client.MinecraftClient;

import static eu.midnightdust.visualoverhaul.VisualOverhaulCommon.*;
import static eu.midnightdust.visualoverhaul.VisualOverhaulCommon.UPDATE_TYPE_FURNACE_ITEMS;

public class UpdateItemsPacketHandler {
    public static void handlePacket(UpdateItemsPacket payload) {
        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> {
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
    }
}
