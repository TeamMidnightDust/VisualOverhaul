package eu.midnightdust.visualoverhaul.block.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class FurnaceWoodenPlanksModel extends Model {
    private static ModelPart bb_main;
    public static final EntityModelLayer WOODEN_PLANKS_MODEL_LAYER = new EntityModelLayer(new Identifier("visualoverhaul", "wooden_planks"), "main");

    public FurnaceWoodenPlanksModel(ModelPart root) {
        super(RenderLayer::getEntitySolid);
        bb_main = root;
        bb_main.setPivot(0.0F, 24.0F, 0.0F);
    }
    public ModelPart getPart() {
        return bb_main;
    }

    public static TexturedModelData getTexturedModelData() {
        return TexturedModelData.of(getModelData(), 16, 16);
    }

    public static ModelData getModelData(){
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("cube_r1", ModelPartBuilder.create().uv(0, 0).cuboid(-10.0F, -3.0F, 0.0F, 10.0F, 1.0F, 1.0F), ModelTransform.of(6.0F, 1.0F, -2.0F,0.0F, -0.5672F, 0.0F));
        modelPartData.addChild("cube_r2", ModelPartBuilder.create().uv(0, 0).cuboid(-10.0F, -2.5F, 0.0F, 10.0F, 2.0F, 2.0F), ModelTransform.of(5.0F, 0.0F, -5.0F,0.0F, -0.1309F, 0.0F));
        modelPartData.addChild("cube_r3", ModelPartBuilder.create().uv(0, 0).cuboid(-10.0F, -2.0F, 0.0F, 10.0F, 2.0F, 2.0F), ModelTransform.of(5.0F, -1.0F, -7.0F,0.0F, 0.2618F, 0.0F));
        return modelData;
    }

    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        bb_main.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
