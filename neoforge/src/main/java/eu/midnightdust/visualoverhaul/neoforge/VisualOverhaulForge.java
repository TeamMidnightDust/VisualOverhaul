package eu.midnightdust.visualoverhaul.neoforge;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import eu.midnightdust.visualoverhaul.VisualOverhaul;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

import static eu.midnightdust.visualoverhaul.VisualOverhaul.*;

@Mod(MOD_ID)
public class VisualOverhaulForge {

    public VisualOverhaulForge() {
        if (FMLEnvironment.dist == Dist.CLIENT) VisualOverhaulClientForge.initClient();

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, HELLO_PACKET, (attachedData, packetSender) -> VisualOverhaul.playersWithMod.add(packetSender.getPlayer().getUuid()));
        PlayerEvent.PLAYER_QUIT.register(player -> playersWithMod.remove(player.getUuid()));
    }
}
