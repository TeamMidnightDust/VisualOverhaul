package eu.midnightdust.visualoverhaul.block.renderer;

import eu.midnightdust.visualoverhaul.VisualOverhaulClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class FurnaceBlockEntityRenderer extends BlockEntityRenderer<FurnaceBlockEntity> {
    private static final ModelPart bb_main;
    private static final ModelPart cube_r1;
    private static final ModelPart cube_r2;
    private static final ModelPart cube_r3;

    static  {
        bb_main = new ModelPart(16, 16, 0, 0);
        bb_main.setPivot(0.0F, 24.0F, 0.0F);

        cube_r1 = new ModelPart(16, 16, 0, 0);
        cube_r1.setPivot(6.0F, 1.0F, -2.0F);
        bb_main.addChild(cube_r1);
        setRotationAngle(cube_r1, 0.0F, -0.5672F, 0.0F);
        cube_r1.setTextureOffset(0, 0).addCuboid(-10.0F, -3.0F, 0.0F, 10.0F, 1.0F, 1.0F, 0.0F, false);

        cube_r2 = new ModelPart(16, 16, 0, 0);
        cube_r2.setPivot(5.0F, 0.0F, -5.0F);
        bb_main.addChild(cube_r2);
        setRotationAngle(cube_r2, 0.0F, -0.1309F, 0.0F);
        cube_r2.setTextureOffset(0, 0).addCuboid(-10.0F, -2.5F, 0.0F, 10.0F, 2.0F, 2.0F, 0.0F, false);

        cube_r3 = new ModelPart(16, 16, 0, 0);
        cube_r3.setPivot(5.0F, -1.0F, -7.0F);
        bb_main.addChild(cube_r3);
        setRotationAngle(cube_r3, 0.0F, 0.2618F, 0.0F);
        cube_r3.setTextureOffset(0, 0).addCuboid(-10.0F, -2.0F, 0.0F, 10.0F, 2.0F, 2.0F, 0.0F, false);
    }
    public static void setRotationAngle(ModelPart bone, float x, float y, float z) {
        bone.pitch = x;
        bone.yaw = y;
        bone.roll = z;
    }

    public FurnaceBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    @Override
    public void render(FurnaceBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (VisualOverhaulClient.VO_CONFIG.furnace) {
            BlockState blockState = blockEntity.getCachedState();
            int lightAtBlock = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().offset(blockState.get(AbstractFurnaceBlock.FACING)));
            ItemStack item1 = blockEntity.getStack(0);
            ItemStack item2 = blockEntity.getStack(1);
            float angle = (blockState.get(AbstractFurnaceBlock.FACING)).asRotation();

            matrices.push();

            matrices.translate(0.5f, 0.58f, 0.5f);
            matrices.scale(1f, 1f, 1f);
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(angle * 3 + 180));
            matrices.translate(0.0f, 0.0f, -0.4f);
            matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90));
            MinecraftClient.getInstance().getItemRenderer().renderItem(item1, ModelTransformation.Mode.GROUND, lightAtBlock, overlay, matrices, vertexConsumers);


            matrices.pop();
            if (!item2.getItem().isIn(ItemTags.LOGS_THAT_BURN) && !item2.getItem().isIn(ItemTags.PLANKS)) {
                matrices.push();

                matrices.translate(0.5f, 0.08f, 0.5f);
                matrices.scale(1f, 1f, 1f);
                matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(angle * 3 + 180));
                matrices.translate(0.0f, 0.0f, -0.4f);
                matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90));
                MinecraftClient.getInstance().getItemRenderer().renderItem(item2, ModelTransformation.Mode.GROUND, lightAtBlock, overlay, matrices, vertexConsumers);

                matrices.pop();
            }
            if (item2.getItem().isIn(ItemTags.LOGS_THAT_BURN) || item2.getItem().isIn(ItemTags.PLANKS)) {
                matrices.push();
                BlockState state = Block.getBlockFromItem(item2.getItem()).getDefaultState();
                Sprite texture = MinecraftClient.getInstance().getBlockRenderManager().getModel(state).getSprite();
                VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(spriteToTexture(texture)));

                matrices.translate(0.5f, -1.3f, 0.5f);
                matrices.scale(1f, 1f, 1f);


                matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(angle * 3 + 180));
                bb_main.render(matrices, vertexConsumer, lightAtBlock, overlay);
                matrices.pop();
            }
        }

    }
    public static Identifier spriteToTexture(Sprite sprite) {
        String texture = sprite.getId().getPath();
        return new Identifier(sprite.getId().getNamespace(), "textures/" + texture + ".png");
    }
}