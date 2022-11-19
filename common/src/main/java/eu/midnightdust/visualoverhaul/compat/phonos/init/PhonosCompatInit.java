package eu.midnightdust.visualoverhaul.compat.phonos.init;

//import eu.midnightdust.visualoverhaul.compat.phonos.block.RadioJukeboxTop;
//import io.github.foundationgames.phonos.block.PhonosBlocks;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class PhonosCompatInit {
    //public static Block RadioJukeboxTop = new RadioJukeboxTop();

    public static void init() {
        //Registry.register(Registry.BLOCK, new Identifier("visualoverhaul","radio_jukebox_top"), RadioJukeboxTop);

        //BlockRenderLayerMapImpl.INSTANCE.putBlock(PhonosBlocks.RADIO_JUKEBOX, RenderLayer.getCutout());
        //BlockRenderLayerMapImpl.INSTANCE.putBlock(RadioJukeboxTop, RenderLayer.getCutout());
    }
}
