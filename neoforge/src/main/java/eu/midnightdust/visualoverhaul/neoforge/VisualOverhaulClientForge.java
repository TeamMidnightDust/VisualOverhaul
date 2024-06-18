package eu.midnightdust.visualoverhaul.neoforge;

import eu.midnightdust.visualoverhaul.VisualOverhaulClient;
import eu.midnightdust.visualoverhaul.block.JukeboxTop;
import eu.midnightdust.visualoverhaul.config.VOConfig;
import eu.midnightdust.visualoverhaul.util.VOColorUtil;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.util.Identifier;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Objects;

import static eu.midnightdust.visualoverhaul.VisualOverhaulClient.*;
import static eu.midnightdust.visualoverhaul.VisualOverhaulCommon.*;

@SuppressWarnings("all")
public class VisualOverhaulClientForge {
    public static List<ResourcePackProfile> defaultEnabledPacks = Lists.newArrayList();
    public static MinecraftClient client = MinecraftClient.getInstance();
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, MOD_ID);

    public static void initClient() {
        VisualOverhaulClient.onInitializeClient();
        // Block only registered on client, because it's just used for the renderer //
        BLOCKS.register(Objects.requireNonNull(ModLoadingContext.get().getActiveContainer().getEventBus()));
        BLOCKS.register("jukebox_top", () -> {
            VisualOverhaulClient.JukeBoxTop = new JukeboxTop();
            return VisualOverhaulClient.JukeBoxTop;
        });
        NeoForge.EVENT_BUS.addListener(VisualOverhaulClientForge::doClientTick);

        Registries.ITEM.forEach((item) -> {
            ModelPredicateProviderRegistry.register(item, Identifier.ofVanilla("round"), (stack, world, entity, seed) ->
                    stack.getComponents().contains(DataComponentTypes.JUKEBOX_PLAYABLE) && stack.getComponents().contains(DataComponentTypes.CUSTOM_MODEL_DATA) &&
                            stack.getComponents().get(DataComponentTypes.CUSTOM_MODEL_DATA).value() == 710 ? 1.0F : 0.0F);
        });

        RenderLayers.setRenderLayer(Blocks.JUKEBOX, RenderLayer.getCutout());
        RenderLayers.setRenderLayer(Blocks.FURNACE, RenderLayer.getCutout());
        RenderLayers.setRenderLayer(Blocks.SMOKER, RenderLayer.getCutout());
        RenderLayers.setRenderLayer(Blocks.BLAST_FURNACE, RenderLayer.getCutout());
    }
    public static void doClientTick(ClientTickEvent.Pre event) {
        if (VOConfig.coloredItems) {
            MinecraftClient client = VisualOverhaulClientForge.client;
            if (client.world != null && client.player != null) {
                waterColor = BiomeColors.getWaterColor(client.world, client.player.getBlockPos());
                foliageColor = BiomeColors.getFoliageColor(client.world, client.player.getBlockPos());
                grassColor = BiomeColors.getGrassColor(client.world, client.player.getBlockPos());
                potionColor = VOColorUtil.convertRgbToArgb(waterColor);
            } else {
                waterColor = 4159204;
                foliageColor = -8934609;
                grassColor = -8934609;
                potionColor = -13083194;
            }
        }
    }
}