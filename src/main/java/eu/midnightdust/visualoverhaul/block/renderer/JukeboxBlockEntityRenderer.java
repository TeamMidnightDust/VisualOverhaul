package eu.midnightdust.visualoverhaul.block.renderer;

import eu.midnightdust.visualoverhaul.VisualOverhaulClient;
import eu.midnightdust.visualoverhaul.config.VOConfig;
import eu.midnightdust.visualoverhaul.util.sound.SoundTest;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Random;

@Environment(EnvType.CLIENT)
public class JukeboxBlockEntityRenderer extends BlockEntityRenderer<JukeboxBlockEntity> {
    private ItemStack record;
    private Identifier discItem;

    public JukeboxBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    @Override
    public void render(JukeboxBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        if (VOConfig.jukebox) {
            int lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().up());

            // Gets the record sound played at the position of the jukebox //
            if (SoundTest.getSound(blockEntity.getPos()) != null) {
                // Converts the Sound Id to the item id of the approprieate disc (minecraft:music_disc.cat -> minecraft:music_disc_cat) //
                discItem = new Identifier(String.valueOf(SoundTest.getSound(blockEntity.getPos())).replace(".", "_"));
            }
            // If the sound is stopped or no sound is playing, the stack is set to an empty stack //
            if (SoundTest.getSound(blockEntity.getPos()) == null) {
                discItem = null;
                record = ItemStack.EMPTY;
            }
            // Tries to get the disc item from the registry //
            else if (Registry.ITEM.getOrEmpty(discItem).isPresent()) {
                record = new ItemStack(Registry.ITEM.get(discItem));
            }
            // Fallback to serverside implementation if the id doesn't match an item //
            else {
                record = blockEntity.getRecord();
            }

            record.setCount(2);

            matrices.push();

            matrices.translate(0.5f, 1.03f, 0.5f);
            matrices.scale(0.75f, 0.75f, 0.75f);
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((blockEntity.getWorld().getTime() + tickDelta) * 4));
            MinecraftClient.getInstance().getItemRenderer().renderItem(record, ModelTransformation.Mode.GROUND, lightAbove, overlay, matrices, vertexConsumers);

            matrices.pop();
            if (VOConfig.jukebox_fake_block && blockEntity.getWorld().getBlockState(blockEntity.getPos().up()).getBlock() == Blocks.AIR) {
                matrices.push();
                matrices.translate(0f, 1f, 0f);
                if (record == ItemStack.EMPTY) {
                    MinecraftClient.getInstance().getBlockRenderManager().renderBlock(VisualOverhaulClient.JukeBoxTop.getDefaultState().with(Properties.HAS_RECORD, false), blockEntity.getPos().up(), blockEntity.getWorld(), matrices, vertexConsumers.getBuffer(RenderLayer.getCutout()), false, new Random());
                } else {
                    MinecraftClient.getInstance().getBlockRenderManager().renderBlock(VisualOverhaulClient.JukeBoxTop.getDefaultState().with(Properties.HAS_RECORD, true), blockEntity.getPos().up(), blockEntity.getWorld(), matrices, vertexConsumers.getBuffer(RenderLayer.getCutout()), false, new Random());
                }
                matrices.pop();
            }
        }
    }
}