package eu.midnightdust.visualoverhaul.fabric;

import eu.midnightdust.visualoverhaul.FakeBlocks;
import eu.midnightdust.visualoverhaul.IconicButtons;
import eu.midnightdust.visualoverhaul.VisualOverhaulClient;
import eu.midnightdust.visualoverhaul.block.model.FurnaceWoodenPlanksModel;
import eu.midnightdust.visualoverhaul.block.renderer.BrewingStandBlockEntityRenderer;
import eu.midnightdust.visualoverhaul.block.renderer.FurnaceBlockEntityRenderer;
import eu.midnightdust.visualoverhaul.block.renderer.JukeboxBlockEntityRenderer;
import eu.midnightdust.visualoverhaul.config.VOConfig;
import eu.midnightdust.visualoverhaul.packet.HelloPacket;
import eu.midnightdust.visualoverhaul.packet.UpdateItemsPacket;
import eu.midnightdust.visualoverhaul.util.VOColorUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import static eu.midnightdust.visualoverhaul.VisualOverhaulClient.*;
import static eu.midnightdust.visualoverhaul.VisualOverhaulCommon.*;

public class VisualOverhaulClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        VisualOverhaulClient.onInitializeClient();

        EntityModelLayerRegistry.registerModelLayer(FurnaceWoodenPlanksModel.WOODEN_PLANKS_MODEL_LAYER, FurnaceWoodenPlanksModel::getTexturedModelData);

        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.JUKEBOX, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.FURNACE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.SMOKER, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.BLAST_FURNACE, RenderLayer.getCutout());

        BlockEntityRendererFactories.register(BlockEntityType.BREWING_STAND, BrewingStandBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(BlockEntityType.JUKEBOX, JukeboxBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(BlockEntityType.FURNACE, FurnaceBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(BlockEntityType.SMOKER, FurnaceBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(BlockEntityType.BLAST_FURNACE, FurnaceBlockEntityRenderer::new);

        // Phonos Compat //
        //if (FabricLoader.getInstance().isModLoaded("phonos")) {
        //    PhonosCompatInit.init();
        //}

        ClientPlayNetworking.registerGlobalReceiver(UpdateItemsPacket.PACKET_ID,
                (payload, context) -> context.client().execute(() -> {
                    if (payload.blockTypeID().equals(UPDATE_TYPE_RECORD)) jukeboxItems.put(payload.pos(), payload.inv().getFirst());
                    else if (context.client().world != null && context.client().world.getBlockEntity(payload.pos()) != null) {
                        if (payload.blockTypeID().equals(UPDATE_TYPE_POTION_BOTTLES) && context.client().world.getBlockEntity(payload.pos()) instanceof BrewingStandBlockEntity brewingStand) {
                            for (int i = 0; i <= 4; i++) {
                                brewingStand.setStack(i, payload.inv().get(i));
                            }
                        } else if (payload.blockTypeID().equals(UPDATE_TYPE_FURNACE_ITEMS) && context.client().world.getBlockEntity(payload.pos()) instanceof AbstractFurnaceBlockEntity furnace) {
                            for (int i = 0; i <= 2; i++) {
                                furnace.setStack(i, payload.inv().get(i));
                            }
                        }
                    }
                }));

        // Register builtin resourcepacks
        FabricLoader.getInstance().getModContainer("visualoverhaul").ifPresent(modContainer -> {
            ResourceManagerHelper.registerBuiltinResourcePack(id("nobrewingbottles"), modContainer, ResourcePackActivationType.DEFAULT_ENABLED);
            ResourceManagerHelper.registerBuiltinResourcePack(id("fancyfurnace"), modContainer, ResourcePackActivationType.DEFAULT_ENABLED);
            ResourceManagerHelper.registerBuiltinResourcePack(id("coloredwaterbucket"), modContainer, ResourcePackActivationType.DEFAULT_ENABLED);
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (client.player != null) {
                sender.sendPacket(new HelloPacket(client.player.getUuid()));
            }
        });

        // Biome-colored Items
        if (VOConfig.coloredItems) {
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                if (client.world != null && client.player != null) {
                    waterColor = BiomeColors.getWaterColor(client.world, client.player.getBlockPos());
                    foliageColor = BiomeColors.getFoliageColor(client.world, client.player.getBlockPos());
                    grassColor = BiomeColors.getGrassColor(client.world, client.player.getBlockPos());
                    potionColor = VOColorUtil.convertRgbToArgb(waterColor, 200);
                } else {
                    waterColor = 4159204;
                    foliageColor = -8934609;
                    grassColor = -8934609;
                    potionColor = -13083194;
                }
            });
        }
        if (VOConfig.coloredLilypad) {
            ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> world != null ? world.getColor(pos, BiomeColors.FOLIAGE_COLOR) : 0, Blocks.LILY_PAD);
        }

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return Identifier.of("iconic", "button_icons");
            }

            @Override
            public void reload(ResourceManager manager) {
                IconicButtons.reload(manager);
                FakeBlocks.reload(manager);
            }
        });
    }
}
