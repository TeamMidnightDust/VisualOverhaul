package eu.midnightdust.visualoverhaul.block.renderer;

import eu.midnightdust.visualoverhaul.block.model.FurnaceWoodenPlanksModel;
import eu.midnightdust.visualoverhaul.config.VOConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class FurnaceBlockEntityRenderer<E extends AbstractFurnaceBlockEntity> implements BlockEntityRenderer<E> {
    private final FurnaceWoodenPlanksModel planks;

    public FurnaceBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.planks = new FurnaceWoodenPlanksModel(ctx.getLayerModelPart(FurnaceWoodenPlanksModel.WOODEN_PLANKS_MODEL_LAYER));
    }

    public void render(E blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (VOConfig.furnace && blockEntity != null) {
            BlockState blockState = blockEntity.getCachedState();
            int lightAtBlock = WorldRenderer.getLightmapCoordinates(Objects.requireNonNull(blockEntity.getWorld()), blockEntity.getPos().offset(blockState.get(AbstractFurnaceBlock.FACING)));
            ItemStack item1 = blockEntity.getStack(0);
            ItemStack item2 = blockEntity.getStack(1);
            float angle = (blockState.get(AbstractFurnaceBlock.FACING)).asRotation();

            if(!item1.isEmpty()) {
                matrices.push();

                matrices.translate(0.5f, 0.58f, 0.5f);
                if (blockEntity.getCachedState().getBlock().equals(Blocks.SMOKER)) matrices.translate(0f, -0.06f, 0f);
                if (blockEntity.getCachedState().getBlock().equals(Blocks.BLAST_FURNACE)) matrices.translate(0f, -0.25f, 0f);
                matrices.scale(1f, 1f, 1f);
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(angle * 3 + 180));
                matrices.translate(0.0f, 0.0f, -0.4f);
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90));
                MinecraftClient.getInstance().getItemRenderer().renderItem(item1, ModelTransformation.Mode.GROUND, lightAtBlock, overlay, matrices, vertexConsumers, 0);


                matrices.pop();
            }
            if (!item2.isEmpty() && !ItemTags.LOGS_THAT_BURN.contains(item2.getItem()) && !ItemTags.PLANKS.contains(item2.getItem())) {
                matrices.push();

                matrices.translate(0.5f, 0.08f, 0.5f);
                if (blockEntity.getCachedState().getBlock().equals(Blocks.SMOKER)) matrices.translate(0f, 0.06f, 0f);
                if (blockEntity.getCachedState().getBlock().equals(Blocks.BLAST_FURNACE)) matrices.translate(0f, 0.24f, 0f);
                matrices.scale(1f, 1f, 1f);
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(angle * 3 + 180));
                matrices.translate(0.0f, 0.0f, -0.4f);
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90));
                MinecraftClient.getInstance().getItemRenderer().renderItem(item2, ModelTransformation.Mode.GROUND, lightAtBlock, overlay, matrices, vertexConsumers,0);

                matrices.pop();
            }
            else if (!item2.isEmpty()) {
                matrices.push();
                BlockState state = Block.getBlockFromItem(item2.getItem()).getDefaultState();
                Sprite texture = MinecraftClient.getInstance().getBlockRenderManager().getModel(state).getParticleSprite();
                VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(spriteToTexture(texture)));

                matrices.translate(0.5f, -1.3f, 0.5f);
                if (blockEntity.getCachedState().getBlock().equals(Blocks.SMOKER)) matrices.translate(0f, 0.06f, 0f);
                if (blockEntity.getCachedState().getBlock().equals(Blocks.BLAST_FURNACE)) matrices.translate(0f, 0.2f, 0f);
                matrices.scale(1f, 1f, 1f);


                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(angle * 3 + 180));
                planks.getPart().render(matrices, vertexConsumer, lightAtBlock, overlay);
                matrices.pop();
            }
        }

    }
    public static Identifier spriteToTexture(Sprite sprite) {
        String texture = sprite.getId().getPath();
        return new Identifier(sprite.getId().getNamespace(), "textures/" + texture + ".png");
    }
}