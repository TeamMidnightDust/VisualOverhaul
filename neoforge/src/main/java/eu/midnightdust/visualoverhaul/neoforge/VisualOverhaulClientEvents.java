package eu.midnightdust.visualoverhaul.neoforge;

import eu.midnightdust.visualoverhaul.IconicButtons;
import eu.midnightdust.visualoverhaul.block.model.FurnaceWoodenPlanksModel;
import eu.midnightdust.visualoverhaul.block.renderer.BrewingStandBlockEntityRenderer;
import eu.midnightdust.visualoverhaul.block.renderer.FurnaceBlockEntityRenderer;
import eu.midnightdust.visualoverhaul.block.renderer.JukeboxBlockEntityRenderer;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.resource.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforgespi.locating.IModFile;

import static eu.midnightdust.visualoverhaul.VisualOverhaul.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class VisualOverhaulClientEvents {
    @SubscribeEvent
    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(FurnaceWoodenPlanksModel.WOODEN_PLANKS_MODEL_LAYER, FurnaceWoodenPlanksModel::getTexturedModelData);
    }

    @SubscribeEvent
    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntityType.BREWING_STAND, BrewingStandBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityType.JUKEBOX, JukeboxBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityType.FURNACE, FurnaceBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityType.SMOKER, FurnaceBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityType.BLAST_FURNACE, FurnaceBlockEntityRenderer::new);
    }
    @SubscribeEvent
    public static void addReloadListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new IconReloadListener());
    }
    public static class IconReloadListener implements SynchronousResourceReloader {
        @Override
        public void reload(ResourceManager manager) {
            IconicButtons.reload(manager);
        }
    }
    @SubscribeEvent
    public static void addPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() == ResourceType.CLIENT_RESOURCES) {
            registerResourcePack(event, new Identifier(MOD_ID,"nobrewingbottles"), false, true);
            registerResourcePack(event, new Identifier(MOD_ID,"fancyfurnace"), false, true);
            registerResourcePack(event, new Identifier(MOD_ID,"coloredwaterbucket"), false, true);
            registerResourcePack(event, new Identifier(MOD_ID,"rounddiscs"), true, false);
        }
    }
    private static void registerResourcePack(AddPackFindersEvent event, Identifier id, boolean alwaysEnabled, boolean defaultEnabled) {
        event.addRepositorySource(((profileAdder) -> {
            IModFile file = ModList.get().getModFileById(id.getNamespace()).getFile();
            try {
                ResourcePackProfile.PackFactory pack = new DirectoryResourcePack.DirectoryBackedFactory(file.findResource("resourcepacks/" + id.getPath()), true);
                ResourcePackProfile packProfile = ResourcePackProfile.create(id.toString(), Text.of(id.getNamespace()+"/"+id.getPath()), alwaysEnabled, pack, ResourceType.CLIENT_RESOURCES, ResourcePackProfile.InsertionPosition.TOP, ResourcePackSource.BUILTIN);
                if (packProfile != null) {
                    profileAdder.accept(packProfile);
                    if (defaultEnabled && !alwaysEnabled) VisualOverhaulClientForge.defaultEnabledPacks.add(packProfile);
                }
            } catch (NullPointerException e) {e.fillInStackTrace();}
        }));
    }
}
