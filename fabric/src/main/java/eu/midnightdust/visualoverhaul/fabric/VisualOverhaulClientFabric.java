package eu.midnightdust.visualoverhaul.fabric;

import eu.midnightdust.visualoverhaul.IconicButtons;
import eu.midnightdust.visualoverhaul.VisualOverhaulClient;
import eu.midnightdust.visualoverhaul.block.JukeboxTop;
import eu.midnightdust.visualoverhaul.block.model.FurnaceWoodenPlanksModel;
import eu.midnightdust.visualoverhaul.block.renderer.BrewingStandBlockEntityRenderer;
import eu.midnightdust.visualoverhaul.block.renderer.FurnaceBlockEntityRenderer;
import eu.midnightdust.visualoverhaul.block.renderer.JukeboxBlockEntityRenderer;
import eu.midnightdust.visualoverhaul.config.VOConfig;
import io.netty.buffer.Unpooled;
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
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

import static eu.midnightdust.visualoverhaul.VisualOverhaul.*;
import static eu.midnightdust.visualoverhaul.VisualOverhaulClient.JukeBoxTop;

public class VisualOverhaulClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        VisualOverhaulClient.onInitializeClient();
        JukeBoxTop = new JukeboxTop();
        // Block only registered on client, because it's just used for the renderer //
        Registry.register(Registries.BLOCK, new Identifier(MOD_ID,"jukebox_top"), JukeBoxTop);

        EntityModelLayerRegistry.registerModelLayer(FurnaceWoodenPlanksModel.WOODEN_PLANKS_MODEL_LAYER, FurnaceWoodenPlanksModel::getTexturedModelData);

        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.JUKEBOX, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(JukeBoxTop, RenderLayer.getCutout());
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

        Registries.ITEM.forEach((item) -> {
            if(item instanceof MusicDiscItem || item.getName().getString().toLowerCase().contains("music_disc") || item.getName().getString().toLowerCase().contains("record") || item.getName().getString().toLowerCase().contains("dynamic_disc")) {
                ModelPredicateProviderRegistry.register(item, new Identifier("round"), (stack, world, entity, seed) -> stack.getCount() == 2 ? 1.0F : 0.0F);
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(UPDATE_POTION_BOTTLES,
                (client, handler, attachedData, packetSender) -> {
                    BlockPos pos = attachedData.readBlockPos();
                    DefaultedList<ItemStack> inv = DefaultedList.ofSize(5, ItemStack.EMPTY);
                    for (int i = 0; i <= 4; i++) {
                        inv.set(i, attachedData.readItemStack());
                    }
                    client.execute(() -> {
                        if (client.world != null && client.world.getBlockEntity(pos) != null && client.world.getBlockEntity(pos) instanceof BrewingStandBlockEntity blockEntity) {
                            for (int i = 0; i <= 4; i++) {
                                blockEntity.setStack(i, inv.get(i));
                            }
                        }
                    });
                });
        ClientPlayNetworking.registerGlobalReceiver(UPDATE_RECORD,
                (client, handler, attachedData, packetSender) -> {
                    BlockPos pos = attachedData.readBlockPos();
                    ItemStack record = attachedData.readItemStack();
                    client.execute(() -> {
                        jukeboxItems.put(pos, record);
                    });
                });
        ClientPlayNetworking.registerGlobalReceiver(UPDATE_FURNACE_ITEMS,
                (client, handler, attachedData, packetSender) -> {
                    BlockPos pos = attachedData.readBlockPos();
                    DefaultedList<ItemStack> inv = DefaultedList.ofSize(3, ItemStack.EMPTY);
                    for (int i = 0; i <= 2; i++) {
                        inv.set(i, attachedData.readItemStack());
                    }
                    client.execute(() -> {
                        if (client.world != null && client.world.getBlockEntity(pos) != null && client.world.getBlockEntity(pos) instanceof AbstractFurnaceBlockEntity blockEntity) {
                            for (int i = 0; i <= 2; i++) {
                                blockEntity.setStack(i, inv.get(i));
                            }
                        }
                    });
                });

        // Register builtin resourcepacks
        FabricLoader.getInstance().getModContainer("visualoverhaul").ifPresent(modContainer -> {
            ResourceManagerHelper.registerBuiltinResourcePack(new Identifier(MOD_ID,"nobrewingbottles"), modContainer, ResourcePackActivationType.DEFAULT_ENABLED);
            ResourceManagerHelper.registerBuiltinResourcePack(new Identifier(MOD_ID,"fancyfurnace"), modContainer, ResourcePackActivationType.DEFAULT_ENABLED);
            ResourceManagerHelper.registerBuiltinResourcePack(new Identifier(MOD_ID,"coloredwaterbucket"), modContainer, ResourcePackActivationType.DEFAULT_ENABLED);
            ResourceManagerHelper.registerBuiltinResourcePack(new Identifier(MOD_ID,"rounddiscs"), modContainer, ResourcePackActivationType.ALWAYS_ENABLED);
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (client.player != null) {
                PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
                passedData.writeUuid(client.player.getUuid());
                sender.sendPacket(HELLO_PACKET, passedData);
            }
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
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> grassColor, Items.SHORT_GRASS);
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

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier("iconic", "button_icons");
            }

            @Override
            public void reload(ResourceManager manager) {
                IconicButtons.reload(manager);
            }
        });
    }
}
