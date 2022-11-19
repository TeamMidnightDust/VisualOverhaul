package eu.midnightdust.visualoverhaul.forge;

import eu.midnightdust.visualoverhaul.block.model.FurnaceWoodenPlanksModel;
import eu.midnightdust.visualoverhaul.block.renderer.BrewingStandBlockEntityRenderer;
import eu.midnightdust.visualoverhaul.block.renderer.FurnaceBlockEntityRenderer;
import eu.midnightdust.visualoverhaul.block.renderer.JukeboxBlockEntityRenderer;
import eu.midnightdust.visualoverhaul.config.VOConfig;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.resource.*;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.resource.PathPackResources;

import java.io.IOException;

import static eu.midnightdust.visualoverhaul.VisualOverhaul.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class VisualOverhaulClientEvents {
    @SubscribeEvent
    public void registerClientTick(TickEvent.ClientTickEvent event) {
        if (VOConfig.coloredItems) {
            MinecraftClient client = VisualOverhaulClientForge.client;
            if (client.world != null && client.player != null) {
                VisualOverhaulClientForge.waterColor = BiomeColors.getWaterColor(client.world, client.player.getBlockPos());
                VisualOverhaulClientForge.foliageColor = BiomeColors.getFoliageColor(client.world, client.player.getBlockPos());
                VisualOverhaulClientForge.grassColor = BiomeColors.getGrassColor(client.world, client.player.getBlockPos());
            } else {
                VisualOverhaulClientForge.waterColor = 4159204;
                VisualOverhaulClientForge.foliageColor = -8934609;
                VisualOverhaulClientForge.grassColor = -8934609;
            }
        }
    }
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
    public static void addPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() == ResourceType.CLIENT_RESOURCES) {
            registerResourcePack(event, new Identifier(MOD_ID,"nobrewingbottles"), false, true);
            registerResourcePack(event, new Identifier(MOD_ID,"fancyfurnace"), false, true);
            registerResourcePack(event, new Identifier(MOD_ID,"coloredwaterbucket"), false, true);
            registerResourcePack(event, new Identifier(MOD_ID,"rounddiscs"), true, false);
        }
    }
    private static void registerResourcePack(AddPackFindersEvent event, Identifier id, boolean alwaysEnabled, boolean defaultEnabled) {
        event.addRepositorySource(((profileAdder, factory) -> {
            IModFile file = ModList.get().getModFileById(id.getNamespace()).getFile();
            try (PathPackResources pack = new PathPackResources(id.toString(), file.findResource("resourcepacks/" +id.getPath()))) {
                ResourcePackProfile packProfile = new ResourcePackProfile(id.toString(), alwaysEnabled, () -> pack, Text.of(id.getNamespace()+"/"+id.getPath()), pack.parseMetadata(PackResourceMetadata.READER).getDescription().copy().append(" §7(built-in)"), ResourcePackCompatibility.COMPATIBLE, ResourcePackProfile.InsertionPosition.TOP, false, ResourcePackSource.PACK_SOURCE_BUILTIN, false);
                profileAdder.accept(packProfile);
                if (defaultEnabled && !alwaysEnabled) VisualOverhaulClientForge.defaultEnabledPacks.add(packProfile);
            } catch (IOException | NullPointerException e) {e.printStackTrace();}
        }));
    }
}
