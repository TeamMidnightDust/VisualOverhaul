package eu.midnightdust.visualoverhaul.forge;

import dev.architectury.networking.NetworkManager;
import eu.midnightdust.lib.config.MidnightConfig;
import eu.midnightdust.visualoverhaul.VisualOverhaulClient;
import eu.midnightdust.visualoverhaul.block.JukeboxTop;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

import static eu.midnightdust.visualoverhaul.VisualOverhaul.*;

//@SuppressWarnings("all")
public class VisualOverhaulClientForge {
    public static List<ResourcePackProfile> defaultEnabledPacks = Lists.newArrayList();
    public static MinecraftClient client = MinecraftClient.getInstance();
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    public static int waterColor = 4159204;
    public static int foliageColor = -8934609;
    public static int grassColor = -8934609;

    public static void initClient() {
        VisualOverhaulClient.onInitializeClient();
        // Block only registered on client, because it's just used for the renderer //
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BLOCKS.register("jukebox_top", () -> {
            VisualOverhaulClient.JukeBoxTop = new JukeboxTop();
            return VisualOverhaulClient.JukeBoxTop;
        });

        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (remote, server) -> true));
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () ->
                new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> MidnightConfig.getScreen(parent, MOD_ID)));
        MinecraftForge.EVENT_BUS.register(new VisualOverhaulClientEvents());
        ForgeRegistries.ITEMS.forEach((item) -> {
            if(item instanceof MusicDiscItem || item.getName().getString().toLowerCase().contains("music_disc") || item.getName().getString().toLowerCase().contains("record") || item.getName().getString().toLowerCase().contains("dynamic_disc")) {
                ModelPredicateProviderRegistry.register(item, new Identifier("round"), (stack, world, entity, seed) -> stack.getCount() == 2 ? 1.0F : 0.0F);
            }
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, UPDATE_POTION_BOTTLES,
                (attachedData, packetSender) -> {
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
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, UPDATE_RECORD,
                (attachedData, packetSender) -> {
                    BlockPos pos = attachedData.readBlockPos();
                    ItemStack record = attachedData.readItemStack();
                    client.execute(() -> {
                        if (client.world != null && client.world.getBlockEntity(pos) != null && client.world.getBlockEntity(pos) instanceof JukeboxBlockEntity blockEntity) {
                            blockEntity.setRecord(record);
                        }
                    });
                });
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, UPDATE_FURNACE_ITEMS,
                (attachedData, packetSender) -> {
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
        RenderLayers.setRenderLayer(Blocks.JUKEBOX, RenderLayer.getCutout());
        RenderLayers.setRenderLayer(Blocks.FURNACE, RenderLayer.getCutout());
        RenderLayers.setRenderLayer(Blocks.SMOKER, RenderLayer.getCutout());
        RenderLayers.setRenderLayer(Blocks.BLAST_FURNACE, RenderLayer.getCutout());
    }
}