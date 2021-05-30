package eu.midnightdust.visualoverhaul;

import eu.midnightdust.visualoverhaul.block.JukeboxTop;
import eu.midnightdust.visualoverhaul.block.model.FurnaceWoodenPlanksModel;
import eu.midnightdust.visualoverhaul.block.renderer.BrewingStandBlockEntityRenderer;
import eu.midnightdust.visualoverhaul.block.renderer.FurnaceBlockEntityRenderer;
import eu.midnightdust.visualoverhaul.block.renderer.JukeboxBlockEntityRenderer;
import eu.midnightdust.visualoverhaul.config.VOConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
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
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.BuiltinBiomes;

import static eu.midnightdust.visualoverhaul.VisualOverhaul.*;

@SuppressWarnings("UnstableApiUsage")
public class VisualOverhaulClient implements ClientModInitializer {

    public static Block JukeBoxTop = new JukeboxTop();
    public static Item RoundDiscDummy = new Item(new FabricItemSettings());
    private final MinecraftClient client = MinecraftClient.getInstance();

    @Override
    public void onInitializeClient() {
        VOConfig.init("visualoverhaul", VOConfig.class);

        // Block only registered on client, because it's just used for the renderer //
        Registry.register(Registry.BLOCK, new Identifier("visualoverhaul","jukebox_top"), JukeBoxTop);
        Registry.register(Registry.ITEM, new Identifier("visualoverhaul","round_disc"), RoundDiscDummy);
        EntityModelLayerRegistry.registerModelLayer(FurnaceWoodenPlanksModel.WOODEN_PLANKS_MODEL_LAYER, FurnaceWoodenPlanksModel::getTexturedModelData);


        BlockRenderLayerMapImpl.INSTANCE.putBlock(Blocks.JUKEBOX, RenderLayer.getCutout());
        BlockRenderLayerMapImpl.INSTANCE.putBlock(JukeBoxTop, RenderLayer.getCutout());
        BlockRenderLayerMapImpl.INSTANCE.putBlock(Blocks.FURNACE, RenderLayer.getCutout());

        BlockEntityRendererRegistry.INSTANCE.register(BlockEntityType.BREWING_STAND, BrewingStandBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(BlockEntityType.JUKEBOX, JukeboxBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(BlockEntityType.FURNACE, FurnaceBlockEntityRenderer::new);

//        // Phonos Compat //
//        if (FabricLoader.getInstance().isModLoaded("phonos")) {
//            PhonosCompatInit.init();
//            BlockEntityRendererRegistry.INSTANCE.register(PhonosBlocks.RADIO_JUKEBOX_ENTITY, RadioJukeboxBlockEntityRenderer::new);
//        }

        Registry.ITEM.forEach((item) -> {
            if(item instanceof MusicDiscItem || item.getName().getString().toLowerCase().contains("music_disc") || item.getName().getString().toLowerCase().contains("dynamic_disc")) {
                FabricModelPredicateProviderRegistry.register(item, new Identifier("round"), (stack, world, entity, seed) -> stack.getCount() == 2 ? 1.0F : 0.0F);
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
                        if (client.world != null && client.world.getBlockEntity(pos) != null && client.world.getBlockEntity(pos) instanceof BrewingStandBlockEntity blockEntity) {
                            blockEntity.setStack(0, inv.get(0));
                            blockEntity.setStack(1, inv.get(1));
                            blockEntity.setStack(2, inv.get(2));
                            blockEntity.setStack(3, inv.get(3));
                            blockEntity.setStack(4, inv.get(4));
                        }
                    });
                });
        ClientSidePacketRegistryImpl.INSTANCE.register(UPDATE_RECORD,
                (packetContext, attachedData) -> {
                    BlockPos pos = attachedData.readBlockPos();
                    ItemStack record = attachedData.readItemStack();
                    packetContext.getTaskQueue().execute(() -> {
                        if (client.world != null && client.world.getBlockEntity(pos) != null && client.world.getBlockEntity(pos) instanceof JukeboxBlockEntity blockEntity) {
                            blockEntity.setRecord(record);
                        }
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
                        if (client.world != null && client.world.getBlockEntity(pos) != null && client.world.getBlockEntity(pos) instanceof FurnaceBlockEntity blockEntity) {
                            blockEntity.setStack(0, inv.get(0));
                            blockEntity.setStack(1, inv.get(1));
                            blockEntity.setStack(2, inv.get(2));
                        }
                    });
                });

        // Register builtin resourcepacks
        FabricLoader.getInstance().getModContainer("visualoverhaul").ifPresent(modContainer -> {
            ResourceManagerHelper.registerBuiltinResourcePack(new Identifier("visualoverhaul","nobrewingbottles"), modContainer, ResourcePackActivationType.DEFAULT_ENABLED);
            ResourceManagerHelper.registerBuiltinResourcePack(new Identifier("visualoverhaul","fancyfurnace"), modContainer, ResourcePackActivationType.DEFAULT_ENABLED);
            ResourceManagerHelper.registerBuiltinResourcePack(new Identifier("visualoverhaul","coloredwaterbucket"), modContainer, ResourcePackActivationType.DEFAULT_ENABLED);
        });

        // Biome-colored Items
        if (VOConfig.coloredItems) {
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                int waterColor;
                int foliageColor;
                int grassColor;
                if (client.world != null) {
                    assert client.player != null;
                    waterColor = client.world.getColor(client.player.getBlockPos(), BiomeColors.WATER_COLOR);
                    foliageColor = client.world.getColor(client.player.getBlockPos(), BiomeColors.FOLIAGE_COLOR);
                    grassColor = client.world.getColor(client.player.getBlockPos(), BiomeColors.GRASS_COLOR);
                } else {
                    waterColor = BuiltinBiomes.PLAINS.getWaterColor();
                    foliageColor = BuiltinBiomes.PLAINS.getFoliageColor();
                    grassColor = BuiltinBiomes.PLAINS.getFoliageColor();
                }
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex == 0 ? -1 : waterColor, Items.WATER_BUCKET);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> grassColor, Items.GRASS_BLOCK);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> grassColor, Items.GRASS);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> grassColor, Items.TALL_GRASS);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> grassColor, Items.FERN);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> grassColor, Items.LARGE_FERN);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> foliageColor, Items.ACACIA_LEAVES);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> foliageColor, Items.DARK_OAK_LEAVES);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> foliageColor, Items.JUNGLE_LEAVES);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> foliageColor, Items.OAK_LEAVES);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
                    if (PotionUtil.getPotion(stack) == Potions.WATER && tintIndex == 0) {
                        return waterColor;
                    }
                    return tintIndex > 0 ? -1 : PotionUtil.getColor(stack);
                }, Items.POTION);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
                    if (PotionUtil.getPotion(stack) == Potions.WATER && tintIndex == 0) {
                        return waterColor;
                    }
                    return tintIndex > 0 ? -1 : PotionUtil.getColor(stack);
                }, Items.SPLASH_POTION);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
                    if (PotionUtil.getPotion(stack) == Potions.WATER && tintIndex == 0) {
                        return waterColor;
                    }
                    return tintIndex > 0 ? -1 : PotionUtil.getColor(stack);
                }, Items.LINGERING_POTION);
            });
        }
    }
}
