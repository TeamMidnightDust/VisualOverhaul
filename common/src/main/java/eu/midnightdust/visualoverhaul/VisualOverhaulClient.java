package eu.midnightdust.visualoverhaul;

import eu.midnightdust.visualoverhaul.config.VOConfig;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.util.Identifier;

import static eu.midnightdust.visualoverhaul.VisualOverhaul.MOD_ID;

public class VisualOverhaulClient {

    public static Block JukeBoxTop;

    public static void onInitializeClient() {
        VOConfig.init(MOD_ID, VOConfig.class);
    }
}
