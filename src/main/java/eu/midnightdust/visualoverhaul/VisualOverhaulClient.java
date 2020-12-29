package eu.midnightdust.visualoverhaul;

import eu.midnightdust.visualoverhaul.block.JukeboxTop;
import eu.midnightdust.visualoverhaul.block.renderer.BrewingStandBlockEntityRenderer;
import eu.midnightdust.visualoverhaul.block.renderer.JukeboxBlockEntityRenderer;
import eu.midnightdust.visualoverhaul.config.VOConfig;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.impl.networking.ClientSidePacketRegistryImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import static eu.midnightdust.visualoverhaul.VisualOverhaul.UPDATE_POTION_BOTTLES;
import static eu.midnightdust.visualoverhaul.VisualOverhaul.UPDATE_RECORD;

public class VisualOverhaulClient implements ClientModInitializer {
    public static VOConfig VO_CONFIG;
    public static Block JukeBoxTop = new JukeboxTop();


    @Override
    public void onInitializeClient() {
        AutoConfig.register(VOConfig.class, JanksonConfigSerializer::new);
        VO_CONFIG = AutoConfig.getConfigHolder(VOConfig.class).getConfig();

        // Block only registered on client, because it's just used for the renderer //
        Registry.register(Registry.BLOCK, new Identifier("visualoverhaul","jukebox_top"), JukeBoxTop);

        BlockEntityRendererRegistry.INSTANCE.register(BlockEntityType.BREWING_STAND, BrewingStandBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(BlockEntityType.JUKEBOX, JukeboxBlockEntityRenderer::new);

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

        FabricLoader.getInstance().getModContainer("visualoverhaul").ifPresent(modContainer -> {
            ResourceManagerHelper.registerBuiltinResourcePack(new Identifier("visualoverhaul:nobottles"), "resourcepacks/visualoverhaul", modContainer, true);
        });
    }
}
