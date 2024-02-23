package eu.midnightdust.visualoverhaul.neoforge;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.networking.NetworkManager;
import eu.midnightdust.lib.config.MidnightConfig;
import eu.midnightdust.visualoverhaul.VisualOverhaulClient;
import eu.midnightdust.visualoverhaul.block.JukeboxTop;
import eu.midnightdust.visualoverhaul.config.VOConfig;
import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.neoforged.fml.IExtensionPoint;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.ConfigScreenHandler;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Objects;

import static eu.midnightdust.visualoverhaul.VisualOverhaul.*;
import static net.neoforged.fml.IExtensionPoint.DisplayTest.IGNORESERVERONLY;

@SuppressWarnings("all")
public class VisualOverhaulClientForge {
    public static List<ResourcePackProfile> defaultEnabledPacks = Lists.newArrayList();
    public static MinecraftClient client = MinecraftClient.getInstance();
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, MOD_ID);
    public static int waterColor = 4159204;
    public static int foliageColor = -8934609;
    public static int grassColor = -8934609;

    public static void initClient() {
        VisualOverhaulClient.onInitializeClient();
        // Block only registered on client, because it's just used for the renderer //
        BLOCKS.register(Objects.requireNonNull(ModLoadingContext.get().getActiveContainer().getEventBus()));
        BLOCKS.register("jukebox_top", () -> {
            VisualOverhaulClient.JukeBoxTop = new JukeboxTop();
            return VisualOverhaulClient.JukeBoxTop;
        });
        NeoForge.EVENT_BUS.addListener(VisualOverhaulClientForge::doClientTick);

        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> IGNORESERVERONLY, (remote, server) -> true));
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () ->
                new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> MidnightConfig.getScreen(parent, MOD_ID)));
        Registries.ITEM.forEach((item) -> {
            if(item instanceof MusicDiscItem || item.getName().getString().toLowerCase().contains("music_disc") || item.getName().getString().toLowerCase().contains("record") || item.getName().getString().toLowerCase().contains("dynamic_disc")) {
                ModelPredicateProviderRegistry.register(item, new Identifier("round"), (stack, world, entity, seed) -> stack.getCount() == 2 ? 1.0F : 0.0F);
            }
        });

        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(player -> {
            if (player != null) {
                NetworkManager.sendToServer(HELLO_PACKET, new PacketByteBuf(Unpooled.buffer()));
            }
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, UPDATE_POTION_BOTTLES,
                (attachedData, packetSender) -> {
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
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, UPDATE_RECORD,
                (attachedData, packetSender) -> {
                    BlockPos pos = attachedData.readBlockPos();
                    ItemStack record = attachedData.readItemStack();
                    client.execute(() -> {
                        jukeboxItems.put(pos, record);
                    });
                });
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, UPDATE_FURNACE_ITEMS,
                (attachedData, packetSender) -> {
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
        RenderLayers.setRenderLayer(Blocks.JUKEBOX, RenderLayer.getCutout());
        RenderLayers.setRenderLayer(Blocks.FURNACE, RenderLayer.getCutout());
        RenderLayers.setRenderLayer(Blocks.SMOKER, RenderLayer.getCutout());
        RenderLayers.setRenderLayer(Blocks.BLAST_FURNACE, RenderLayer.getCutout());
    }
    public static void doClientTick(TickEvent.ClientTickEvent event) {
        if (VOConfig.coloredItems && event.phase == TickEvent.Phase.START) {
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
}