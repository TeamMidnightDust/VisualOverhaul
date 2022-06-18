package eu.midnightdust.visualoverhaul;

import eu.midnightdust.visualoverhaul.block.JukeboxTop;
import eu.midnightdust.visualoverhaul.block.model.FurnaceWoodenPlanksModel;
import eu.midnightdust.visualoverhaul.block.renderer.BrewingStandBlockEntityRenderer;
import eu.midnightdust.visualoverhaul.block.renderer.FurnaceBlockEntityRenderer;
import eu.midnightdust.visualoverhaul.block.renderer.JukeboxBlockEntityRenderer;
import eu.midnightdust.visualoverhaul.config.VOConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.impl.blockrenderlayer.BlockRenderLayerMapImpl;
import net.fabricmc.fabric.impl.client.rendering.BlockEntityRendererRegistryImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.*;
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

import static eu.midnightdust.visualoverhaul.VisualOverhaul.*;

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
        BlockRenderLayerMapImpl.INSTANCE.putBlock(Blocks.SMOKER, RenderLayer.getCutout());
        BlockRenderLayerMapImpl.INSTANCE.putBlock(Blocks.BLAST_FURNACE, RenderLayer.getCutout());

        BlockEntityRendererRegistryImpl.register(BlockEntityType.BREWING_STAND, BrewingStandBlockEntityRenderer::new);
        BlockEntityRendererRegistryImpl.register(BlockEntityType.JUKEBOX, JukeboxBlockEntityRenderer::new);
        BlockEntityRendererRegistryImpl.register(BlockEntityType.FURNACE, FurnaceBlockEntityRenderer::new);
        BlockEntityRendererRegistryImpl.register(BlockEntityType.SMOKER, FurnaceBlockEntityRenderer::new);
        BlockEntityRendererRegistryImpl.register(BlockEntityType.BLAST_FURNACE, FurnaceBlockEntityRenderer::new);

//        // Phonos Compat //
//        if (FabricLoader.getInstance().isModLoaded("phonos")) {
//            PhonosCompatInit.init();
//            BlockEntityRendererRegistry.INSTANCE.register(PhonosBlocks.RADIO_JUKEBOX_ENTITY, RadioJukeboxBlockEntityRenderer::new);
//        }

        Registry.ITEM.forEach((item) -> {
            if(item instanceof MusicDiscItem || item.getName().getString().toLowerCase().contains("music_disc") || item.getName().getString().toLowerCase().contains("record") || item.getName().getString().toLowerCase().contains("dynamic_disc")) {
                FabricModelPredicateProviderRegistry.register(item, new Identifier("round"), (stack, world, entity, seed) -> stack.getCount() == 2 ? 1.0F : 0.0F);
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(UPDATE_POTION_BOTTLES,
                (client, handler, attachedData, packetSender) -> {
                    BlockPos pos = attachedData.readBlockPos();
                    DefaultedList<ItemStack> inv = DefaultedList.ofSize(5, ItemStack.EMPTY);
                    for (int i = 0; i < 4; i++) {
                        inv.set(i, attachedData.readItemStack());
                    }
                    client.execute(() -> {
                        if (client.world != null && client.world.getBlockEntity(pos) != null && client.world.getBlockEntity(pos) instanceof BrewingStandBlockEntity blockEntity) {
                            blockEntity.setStack(0, inv.get(0));
                            blockEntity.setStack(1, inv.get(1));
                            blockEntity.setStack(2, inv.get(2));
                            blockEntity.setStack(3, inv.get(3));
                            blockEntity.setStack(4, inv.get(4));
                        }
                    });
                });
        ClientPlayNetworking.registerGlobalReceiver(UPDATE_RECORD,
                (client, handler, attachedData, packetSender) -> {
                    BlockPos pos = attachedData.readBlockPos();
                    ItemStack record = attachedData.readItemStack();
                    client.execute(() -> {
                        if (client.world != null && client.world.getBlockEntity(pos) != null && client.world.getBlockEntity(pos) instanceof JukeboxBlockEntity blockEntity) {
                            blockEntity.setRecord(record);
                        }
                    });
                });
        ClientPlayNetworking.registerGlobalReceiver(UPDATE_FURNACE_ITEMS,
                (client, handler, attachedData, packetSender) -> {
                    BlockPos pos = attachedData.readBlockPos();
                    DefaultedList<ItemStack> inv = DefaultedList.ofSize(3, ItemStack.EMPTY);
                    for (int i = 0; i < 2; i++) {
                        inv.set(i, attachedData.readItemStack());
                    }
                    client.execute(() -> {
                        if (client.world != null && client.world.getBlockEntity(pos) != null && client.world.getBlockEntity(pos) instanceof AbstractFurnaceBlockEntity blockEntity) {
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
            ResourceManagerHelper.registerBuiltinResourcePack(new Identifier("visualoverhaul","rounddiscs"), modContainer, ResourcePackActivationType.ALWAYS_ENABLED);
        });


        // Biome-colored Items
        if (VOConfig.coloredItems) {
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                int waterColor;
                int foliageColor;
                int grassColor;
                if (client.world != null && client.player != null) {
                    waterColor = BiomeColors.getWaterColor(client.world, client.player.getBlockPos());
                    foliageColor = BiomeColors.getFoliageColor(client.world, client.player.getBlockPos());
                    grassColor = BiomeColors.getGrassColor(client.world, client.player.getBlockPos());
                }
                else {
                    waterColor = 4159204;
                    foliageColor = -8934609;
                    grassColor = -8934609;
                }
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex == 0 ? -1 : waterColor, Items.WATER_BUCKET);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex == 0 ? -1 : waterColor, Items.AXOLOTL_BUCKET);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex == 0 ? -1 : waterColor, Items.COD_BUCKET);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex == 0 ? -1 : waterColor, Items.PUFFERFISH_BUCKET);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex == 0 ? -1 : waterColor, Items.TROPICAL_FISH_BUCKET);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex == 0 ? -1 : waterColor, Items.SALMON_BUCKET);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> grassColor, Items.GRASS_BLOCK);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> grassColor, Items.GRASS);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> grassColor, Items.TALL_GRASS);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> grassColor, Items.FERN);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> grassColor, Items.LARGE_FERN);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> foliageColor, Items.ACACIA_LEAVES);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> foliageColor, Items.DARK_OAK_LEAVES);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> foliageColor, Items.JUNGLE_LEAVES);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> foliageColor, Items.OAK_LEAVES);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> foliageColor, Items.VINE);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> foliageColor, Items.SUGAR_CANE);
                if (VOConfig.coloredLilypad) ColorProviderRegistry.ITEM.register((stack, tintIndex) -> foliageColor, Items.LILY_PAD);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
                    if ((PotionUtil.getPotion(stack) == Potions.WATER || PotionUtil.getPotion(stack) == Potions.MUNDANE || PotionUtil.getPotion(stack) == Potions.THICK || PotionUtil.getPotion(stack) == Potions.AWKWARD) && tintIndex == 0) {
                        return waterColor;
                    }
                    return tintIndex > 0 ? -1 : PotionUtil.getColor(stack);
                }, Items.POTION);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
                    if ((PotionUtil.getPotion(stack) == Potions.WATER || PotionUtil.getPotion(stack) == Potions.MUNDANE || PotionUtil.getPotion(stack) == Potions.THICK || PotionUtil.getPotion(stack) == Potions.AWKWARD) && tintIndex == 0) {
                        return waterColor;
                    }
                    return tintIndex > 0 ? -1 : PotionUtil.getColor(stack);
                }, Items.SPLASH_POTION);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
                    if ((PotionUtil.getPotion(stack) == Potions.WATER || PotionUtil.getPotion(stack) == Potions.MUNDANE || PotionUtil.getPotion(stack) == Potions.THICK || PotionUtil.getPotion(stack) == Potions.AWKWARD) && tintIndex == 0) {
                        return waterColor;
                    }
                    return tintIndex > 0 ? -1 : PotionUtil.getColor(stack);
                }, Items.LINGERING_POTION);
            });
        }
        if (VOConfig.coloredLilypad) {
            ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> world != null ? world.getColor(pos, BiomeColors.FOLIAGE_COLOR) : 0, Blocks.LILY_PAD);
        }
    }
}
