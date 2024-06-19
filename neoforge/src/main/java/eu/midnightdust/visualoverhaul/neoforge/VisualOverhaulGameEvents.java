package eu.midnightdust.visualoverhaul.neoforge;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import static eu.midnightdust.visualoverhaul.VisualOverhaulCommon.MOD_ID;
import static eu.midnightdust.visualoverhaul.VisualOverhaulCommon.playersWithMod;

@EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class VisualOverhaulGameEvents {
    @SubscribeEvent
    public static void removeOnLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        playersWithMod.remove(event.getEntity().getUuid());
    }
}
