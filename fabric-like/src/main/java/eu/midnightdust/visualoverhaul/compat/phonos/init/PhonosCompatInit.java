package eu.midnightdust.visualoverhaul.compat.phonos.init;

import eu.midnightdust.visualoverhaul.compat.phonos.block.RadioJukeboxTop;
import eu.midnightdust.visualoverhaul.compat.phonos.block.renderer.RadioJukeboxBlockEntityRenderer;
import io.github.foundationgames.phonos.block.PhonosBlocks;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class PhonosCompatInit {
    public static Block RadioJukeboxTop = new RadioJukeboxTop();

    public static void init() {
        Registry.register(Registry.BLOCK, new Identifier("visualoverhaul","radio_jukebox_top"), RadioJukeboxTop);

        BlockRenderLayerMap.INSTANCE.putBlock(PhonosBlocks.RADIO_JUKEBOX, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RadioJukeboxTop, RenderLayer.getCutout());
        BlockEntityRendererRegistry.register(PhonosBlocks.RADIO_JUKEBOX_ENTITY, RadioJukeboxBlockEntityRenderer::new);
    }
}
