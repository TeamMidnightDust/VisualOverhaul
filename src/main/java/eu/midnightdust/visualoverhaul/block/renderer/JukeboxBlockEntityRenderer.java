package eu.midnightdust.visualoverhaul.block.renderer;

import eu.midnightdust.visualoverhaul.VisualOverhaulClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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

import java.util.Random;

@Environment(EnvType.CLIENT)
public class JukeboxBlockEntityRenderer extends BlockEntityRenderer<JukeboxBlockEntity> {

    public JukeboxBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    @Override
    public void render(JukeboxBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        if (VisualOverhaulClient.VO_CONFIG.jukebox) {
            int lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().up());
            ItemStack record = blockEntity.getRecord();
            record.setCount(2);

            matrices.push();

            matrices.translate(0.5f, 1.03f, 0.5f);
            matrices.scale(0.75f, 0.75f, 0.75f);
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((blockEntity.getWorld().getTime() + tickDelta) * 4));
            MinecraftClient.getInstance().getItemRenderer().renderItem(record, ModelTransformation.Mode.GROUND, lightAbove, overlay, matrices, vertexConsumers);

            matrices.pop();
            if (VisualOverhaulClient.VO_CONFIG.jukebox_fake_block) {
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