package eu.midnightdust.visualoverhaul;

import eu.midnightdust.visualoverhaul.block.JukeboxTop;
import eu.midnightdust.visualoverhaul.block.renderer.BrewingStandBlockEntityRenderer;
import eu.midnightdust.visualoverhaul.block.renderer.FurnaceBlockEntityRenderer;
import eu.midnightdust.visualoverhaul.block.renderer.JukeboxBlockEntityRenderer;
import eu.midnightdust.visualoverhaul.config.VOConfig;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.impl.blockrenderlayer.BlockRenderLayerMapImpl;
import net.fabricmc.fabric.impl.networking.ClientSidePacketRegistryImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import static eu.midnightdust.visualoverhaul.VisualOverhaul.*;

public class VisualOverhaulClient implements ClientModInitializer {
    public static VOConfig VO_CONFIG;
    public static Block JukeBoxTop = new JukeboxTop();


    @Override
    public void onInitializeClient() {
        AutoConfig.register(VOConfig.class, JanksonConfigSerializer::new);
        VO_CONFIG = AutoConfig.getConfigHolder(VOConfig.class).getConfig();

        // Block only registered on client, because it's just used for the renderer //
        Registry.register(Registry.BLOCK, new Identifier("visualoverhaul","jukebox_top"), JukeBoxTop);

        BlockRenderLayerMapImpl.INSTANCE.putBlock(Blocks.JUKEBOX, RenderLayer.getCutout());
        BlockRenderLayerMapImpl.INSTANCE.putBlock(JukeBoxTop, RenderLayer.getCutout());
        BlockRenderLayerMapImpl.INSTANCE.putBlock(Blocks.FURNACE, RenderLayer.getCutout());

        BlockEntityRendererRegistry.INSTANCE.register(BlockEntityType.BREWING_STAND, BrewingStandBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(BlockEntityType.JUKEBOX, JukeboxBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(BlockEntityType.FURNACE, FurnaceBlockEntityRenderer::new);

        Registry.ITEM.forEach((item) -> {
            if(item instanceof MusicDiscItem) {
                FabricModelPredicateProviderRegistry.register(item, new Identifier("round"), (stack, world, entity) -> stack.getCount() == 2 ? 1.0F : 0.0F);
            }
        });

        ClientSidePacketRegistryImpl.INSTANCE.register(UPDATE_POTION_BOTTLES,
                (packetContext, attachedData) -> {
                    BlockPos pos = attachedData.readBlockPos();
                    DefaultedList<ItemStack> inv = DefaultedList.ofSize(5, ItemStack.EMPTY);
                    for (int i = 0; i < 4; i++) {
                        inv.set(i, attachedData.readItemStack());
                    }
                    packetContext.getTaskQueue().execute(() -> {
                        BrewingStandBlockEntity blockEntity = (BrewingStandBlockEntity) MinecraftClient.getInstance().world.getBlockEntity(pos);
                        blockEntity.setStack(0,inv.get(0));
                        blockEntity.setStack(1,inv.get(1));
                        blockEntity.setStack(2,inv.get(2));
                        blockEntity.setStack(3,inv.get(3));
                        blockEntity.setStack(4,inv.get(4));
                    });
                });
        ClientSidePacketRegistryImpl.INSTANCE.register(UPDATE_RECORD,
                (packetContext, attachedData) -> {
                    BlockPos pos = attachedData.readBlockPos();
                    ItemStack record = attachedData.readItemStack();
                    packetContext.getTaskQueue().execute(() -> {
                        JukeboxBlockEntity blockEntity = (JukeboxBlockEntity)MinecraftClient.getInstance().world.getBlockEntity(pos);
                        blockEntity.setRecord(record);
                    });
                });
        ClientSidePacketRegistryImpl.INSTANCE.register(UPDATE_FURNACE_ITEMS,
                (packetContext, attachedData) -> {
                    BlockPos pos = attachedData.readBlockPos();
                    DefaultedList<ItemStack> inv = DefaultedList.ofSize(3, ItemStack.EMPTY);
                    for (int i = 0; i < 2; i++) {
                        inv.set(i, attachedData.readItemStack());
                    }
                    packetContext.getTaskQueue().execute(() -> {
                        FurnaceBlockEntity blockEntity = (FurnaceBlockEntity)MinecraftClient.getInstance().world.getBlockEntity(pos);
                        blockEntity.setStack(0,inv.get(0));
                        blockEntity.setStack(1,inv.get(1));
                        blockEntity.setStack(2,inv.get(2));
                    });
                });

        FabricLoader.getInstance().getModContainer("visualoverhaul").ifPresent(modContainer -> {
            ResourceManagerHelper.registerBuiltinResourcePack(new Identifier("visualoverhaul:nobottles"), "resourcepacks/nobrewingbottles", modContainer, true);
            ResourceManagerHelper.registerBuiltinResourcePack(new Identifier("visualoverhaul:fancyfurnace"), "resourcepacks/fancyfurnace", modContainer, true);
        });
    }
}
