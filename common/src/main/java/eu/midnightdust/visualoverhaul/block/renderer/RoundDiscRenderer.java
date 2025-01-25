package eu.midnightdust.visualoverhaul.block.renderer;

import eu.midnightdust.visualoverhaul.mixin.ItemRenderStateAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelNameSupplier;
import net.minecraft.client.model.SpriteGetter;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/*
* Fabric's Model API would be much more elegant, but doesn't work on NeoForge, so I came up with this very hacky (but kinda cool) solution.
*/
public class RoundDiscRenderer {
    private static final Map<Identifier, BakedModel> DISCS = new HashMap<>();
    private static final DynamicBaker maBaker = new DynamicBaker();

    /*
    * Dynamically generates a baked model for the specified music disc.
    */
    private static BakedModel requestModel(Identifier id) {
        JsonUnbakedModel model = JsonUnbakedModel.deserialize(new StringReader(getJsonModel(id)));
        BakedModel bakedModel = model.bake(new ModelTextures.Builder().addFirst(model.getTextures()).build(()->"0"), maBaker, new ModelBakeSettings(){}, false, false, model.getTransformation());
        DISCS.put(id, bakedModel);
        return bakedModel;
    }

    /*
    * Yes, this is VERY cursed, but hey, it works :)
    */
    private static String getJsonModel(Identifier id) {
        return "{\"textures\":{\"0\":\""+
                id.getNamespace()+":"+"item/"+id.getPath()+
                "\",\"particle\":\"#0\"},\"elements\":[{\"from\":[7.5,0,7.5],\"to\":[8.5,1,8.5],\"faces\":{\"up\":{\"uv\":[7,7,8,8],\"texture\":\"#0\"}}},{\"from\":[9.5,0,9.5],\"to\":[10.5,1,10.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[10,8,10]},\"faces\":{\"up\":{\"uv\":[5,6,6,7],\"texture\":\"#0\"}}},{\"from\":[5.5,0,9.5],\"to\":[6.5,1,10.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[6,8,10]},\"faces\":{\"up\":{\"uv\":[9,6,10,7],\"texture\":\"#0\"}}},{\"from\":[5.5,0,5.5],\"to\":[6.5,1,6.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[6,8,6]},\"faces\":{\"up\":{\"uv\":[9,8,10,9],\"texture\":\"#0\"}}},{\"from\":[9.5,0,5.5],\"to\":[10.5,1,6.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[10,8,6]},\"faces\":{\"up\":{\"uv\":[5,8,6,9],\"texture\":\"#0\"}}},{\"from\":[6.5,0,5.5],\"to\":[9.5,1,6.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[8,8,6]},\"faces\":{\"up\":{\"uv\":[6,8,9,9],\"texture\":\"#0\"}}},{\"from\":[2.5,0,4.5],\"to\":[13.5,1,5.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[8,8,5]},\"faces\":{\"up\":{\"uv\":[2,9,13,10],\"texture\":\"#0\"}}},{\"from\":[4.5,0.001,2.5],\"to\":[5.5,1.001,13.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[5,8,8]},\"faces\":{\"up\":{\"uv\":[2,9,13,10],\"rotation\":270,\"texture\":\"#0\"}}},{\"from\":[10.5,0.001,2.5],\"to\":[11.5,1.001,13.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[11,8,8]},\"faces\":{\"up\":{\"uv\":[2,9,13,10],\"rotation\":270,\"texture\":\"#0\"}}},{\"from\":[2.5,0,10.5],\"to\":[13.5,1,11.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[8,8,11]},\"faces\":{\"up\":{\"uv\":[2,5,13,6],\"texture\":\"#0\"}}},{\"from\":[6.5,0,9.5],\"to\":[9.5,1,10.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[8,8,10]},\"faces\":{\"up\":{\"uv\":[6,6,9,7],\"texture\":\"#0\"}}},{\"from\":[5.5,0,6.5],\"to\":[6.5,1,9.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[6,8,8]},\"faces\":{\"up\":{\"uv\":[6,8,9,9],\"rotation\":270,\"texture\":\"#0\"}}},{\"from\":[9.5,0,6.5],\"to\":[10.5,1,9.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[10,8,8]},\"faces\":{\"up\":{\"uv\":[6,6,9,7],\"rotation\":270,\"texture\":\"#0\"}}},{\"from\":[8.5,0,6.5],\"to\":[9.5,1,9.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[9,8,8]},\"faces\":{\"up\":{\"uv\":[6,7,7,8],\"texture\":\"#0\"}}},{\"from\":[6.5,0,6.5],\"to\":[7.5,1,9.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[7,8,8]},\"faces\":{\"up\":{\"uv\":[8,7,9,8],\"texture\":\"#0\"}}},{\"from\":[7.5,0,8.5],\"to\":[8.5,1,9.5],\"faces\":{\"up\":{\"uv\":[6,7,7,8],\"texture\":\"#0\"}}},{\"from\":[7.5,0,6.5],\"to\":[8.5,1,7.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[8,8,6]},\"faces\":{\"up\":{\"uv\":[8,7,9,8],\"texture\":\"#0\"}}},{\"from\":[7.5,-0.001,11.5],\"to\":[12.5,0.999,12.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[10,8,12]},\"faces\":{\"up\":{\"uv\":[3,5,8,6],\"rotation\":180,\"texture\":\"#0\"}}},{\"from\":[3.5,-0.001,11.5],\"to\":[7.5,0.999,12.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[5,8,12]},\"faces\":{\"up\":{\"uv\":[8,5,12,6],\"rotation\":180,\"texture\":\"#0\"}}},{\"from\":[5.5,0,12.5],\"to\":[10.5,1,13.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[8,8,13]},\"faces\":{\"up\":{\"uv\":[2,5,3,10],\"rotation\":90,\"texture\":\"#0\"}}},{\"from\":[6.5,0,13.5],\"to\":[11.5,1,14.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[9,8,14]},\"faces\":{\"east\":{\"uv\":[10,11,11,12],\"texture\":\"#0\"},\"south\":{\"uv\":[5,12,10,13],\"texture\":\"#0\"},\"up\":{\"uv\":[5,3,10,4],\"texture\":\"#0\"}}},{\"from\":[4.5,0,13.5],\"to\":[6.5,1,14.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[4,8,14]},\"faces\":{\"south\":{\"uv\":[3,11,5,12],\"texture\":\"#0\"},\"west\":{\"uv\":[2,11,3,12],\"texture\":\"#0\"},\"up\":{\"uv\":[8,3,10,4],\"texture\":\"#0\"}}},{\"from\":[3.5,0,12.5],\"to\":[4.5,1,13.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[2,8,13]},\"faces\":{\"south\":{\"uv\":[13,10,14,11],\"texture\":\"#0\"},\"west\":{\"uv\":[13,10,14,11],\"texture\":\"#0\"},\"up\":{\"uv\":[10,4,11,5],\"texture\":\"#0\"}}},{\"from\":[2.5,0,11.5],\"to\":[3.5,1,12.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[1,8,12]},\"faces\":{\"south\":{\"uv\":[12,11,13,12],\"texture\":\"#0\"},\"west\":{\"uv\":[10,11,11,12],\"texture\":\"#0\"},\"up\":{\"uv\":[11,4,12,5],\"texture\":\"#0\"}}},{\"from\":[12.5,0,11.5],\"to\":[13.5,1,12.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[11,8,12]},\"faces\":{\"east\":{\"uv\":[13,10,14,11],\"texture\":\"#0\"},\"south\":{\"uv\":[13,10,14,11],\"texture\":\"#0\"},\"up\":{\"uv\":[2,4,3,5],\"texture\":\"#0\"}}},{\"from\":[11.5,0,12.5],\"to\":[12.5,1,13.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[10,8,13]},\"faces\":{\"east\":{\"uv\":[12,11,13,12],\"texture\":\"#0\"},\"south\":{\"uv\":[11,11,12,12],\"texture\":\"#0\"},\"up\":{\"uv\":[4,4,5,5],\"texture\":\"#0\"}}},{\"from\":[11.5,-0.001,5.5],\"to\":[12.5,0.999,10.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[12,8,8]},\"faces\":{\"up\":{\"uv\":[5,4,10,5],\"rotation\":270,\"texture\":\"#0\"}}},{\"from\":[11.5,-0.001,3.5],\"to\":[12.5,0.999,4.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[12,8,6]},\"faces\":{\"up\":{\"uv\":[2,9,3,10],\"rotation\":270,\"texture\":\"#0\"}}},{\"from\":[12.5,0,5.5],\"to\":[13.5,1,10.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[13,8,8]},\"faces\":{\"up\":{\"uv\":[2,5,3,10],\"texture\":\"#0\"}}},{\"from\":[13.5,0,4.5],\"to\":[14.5,1,9.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[14,8,7]},\"faces\":{\"north\":{\"uv\":[10,11,11,12],\"texture\":\"#0\"},\"east\":{\"uv\":[5,12,10,13],\"texture\":\"#0\"},\"up\":{\"uv\":[5,3,10,4],\"rotation\":270,\"texture\":\"#0\"}}},{\"from\":[13.5,0,9.5],\"to\":[14.5,1,11.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[14,8,12]},\"faces\":{\"east\":{\"uv\":[3,11,5,12],\"texture\":\"#0\"},\"south\":{\"uv\":[2,11,3,12],\"texture\":\"#0\"},\"up\":{\"uv\":[8,3,10,4],\"rotation\":270,\"texture\":\"#0\"}}},{\"from\":[11.5,0,2.5],\"to\":[12.5,1,3.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[12,8,5]},\"faces\":{\"north\":{\"uv\":[11,11,12,12],\"texture\":\"#0\"},\"east\":{\"uv\":[10,11,11,12],\"texture\":\"#0\"},\"up\":{\"uv\":[1,5,2,6],\"rotation\":270,\"texture\":\"#0\"}}},{\"from\":[12.5,0,3.5],\"to\":[13.5,1,4.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[13,8,6]},\"faces\":{\"north\":{\"uv\":[12,11,13,12],\"texture\":\"#0\"},\"east\":{\"uv\":[11,11,12,12],\"texture\":\"#0\"},\"up\":{\"uv\":[2,4,3,5],\"rotation\":270,\"texture\":\"#0\"}}},{\"from\":[5.5,-0.001,3.5],\"to\":[10.5,0.999,4.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[8,8,4.1]},\"faces\":{\"up\":{\"uv\":[5,10,10,11],\"rotation\":180,\"texture\":\"#0\"}}},{\"from\":[3.5,-0.001,3.5],\"to\":[4.5,0.999,4.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[6,8,4]},\"faces\":{\"up\":{\"uv\":[12,9,13,10],\"rotation\":180,\"texture\":\"#0\"}}},{\"from\":[5.5,0,2.5],\"to\":[10.5,1,3.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[8,8,3]},\"faces\":{\"up\":{\"uv\":[2,5,3,10],\"rotation\":270,\"texture\":\"#0\"}}},{\"from\":[6.5,0,1.5],\"to\":[11.5,1,2.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[9,8,2]},\"faces\":{\"north\":{\"uv\":[5,12,10,13],\"texture\":\"#0\"},\"east\":{\"uv\":[12,11,13,12],\"texture\":\"#0\"},\"up\":{\"uv\":[5,3,10,4],\"rotation\":180,\"texture\":\"#0\"}}},{\"from\":[4.5,0,1.5],\"to\":[6.5,1,2.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[7,8,2]},\"faces\":{\"north\":{\"uv\":[10,11,12,12],\"texture\":\"#0\"},\"west\":{\"uv\":[9,12,10,13],\"texture\":\"#0\"},\"up\":{\"uv\":[8,3,10,4],\"rotation\":180,\"texture\":\"#0\"}}},{\"from\":[2.5,0,3.5],\"to\":[3.5,1,4.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[5,8,4]},\"faces\":{\"north\":{\"uv\":[12,11,13,12],\"texture\":\"#0\"},\"west\":{\"uv\":[12,11,13,12],\"texture\":\"#0\"},\"up\":{\"uv\":[1,5,2,6],\"rotation\":180,\"texture\":\"#0\"}}},{\"from\":[3.5,0,2.5],\"to\":[4.5,1,3.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[6,8,3]},\"faces\":{\"north\":{\"uv\":[12,11,13,12],\"texture\":\"#0\"},\"west\":{\"uv\":[11,11,12,12],\"texture\":\"#0\"},\"up\":{\"uv\":[1,5,2,6],\"rotation\":180,\"texture\":\"#0\"}}},{\"from\":[3.5,-0.001,5.5],\"to\":[4.5,0.999,10.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[4,8,8.1]},\"faces\":{\"up\":{\"uv\":[11,5,12,10],\"texture\":\"#0\"}}},{\"from\":[2.5,0,5.5],\"to\":[3.5,1,10.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[3,8,8]},\"faces\":{\"up\":{\"uv\":[12,5,13,10],\"rotation\":180,\"texture\":\"#0\"}}},{\"from\":[1.5,0,6.5],\"to\":[2.5,1,11.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[2,8,9]},\"faces\":{\"south\":{\"uv\":[13,10,14,11],\"texture\":\"#0\"},\"west\":{\"uv\":[5,12,10,13],\"texture\":\"#0\"},\"up\":{\"uv\":[5,3,10,4],\"rotation\":90,\"texture\":\"#0\"}}},{\"from\":[1.5,0,4.5],\"to\":[2.5,1,6.5],\"rotation\":{\"angle\":0,\"axis\":\"y\",\"origin\":[2,8,4]},\"faces\":{\"north\":{\"uv\":[13,10,14,11],\"texture\":\"#0\"},\"west\":{\"uv\":[10,11,12,12],\"texture\":\"#0\"},\"up\":{\"uv\":[8,3,10,4],\"rotation\":90,\"texture\":\"#0\"}}}],\"gui_light\":\"front\",\"display\":{\"thirdperson_righthand\":{\"rotation\":[38,0,0],\"translation\":[0,1.75,0.75],\"scale\":[0.5,0.5,0.5]},\"thirdperson_lefthand\":{\"rotation\":[38,0,0],\"translation\":[0,1.75,0.75],\"scale\":[0.5,0.5,0.5]},\"firstperson_righthand\":{\"rotation\":[87,-19,41],\"translation\":[0,4.25,0]},\"firstperson_lefthand\":{\"rotation\":[87,-19,41],\"translation\":[0,4.25,0]},\"ground\":{\"translation\":[0,7.5,0]},\"gui\":{\"rotation\":[90,0,0]},\"head\":{\"translation\":[0,14.5,0]},\"fixed\":{\"rotation\":[-90,0,0],\"translation\":[0,0,-8]}},\"groups\":[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,{\"name\":\"group\",\"origin\":[10,8,12],\"color\":0,\"children\":[17,18,19,20,21,22,23,24,25]},{\"name\":\"group\",\"origin\":[10,8,12],\"color\":0,\"children\":[26,27,28,29,30,31,32]},{\"name\":\"group\",\"origin\":[10,8,12],\"color\":0,\"children\":[33,34,35,36,37,38,39]},{\"name\":\"group\",\"origin\":[10,8,12],\"color\":0,\"children\":[40,41,42,43]}]}";
    }

    /*
    * Tries to retrieve a cached model and builds a new one otherwise
    */
    public static BakedModel getModel(Identifier id) {
        if (DISCS.containsKey(id)) return DISCS.get(id);
        else return requestModel(id);
    }

    public static void render(Item item, int light, int overlay, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        ItemRenderState itemRenderState = new ItemRenderState();
        ItemRenderState.LayerRenderState renderState = itemRenderState.newLayer();
        ((ItemRenderStateAccessor)itemRenderState).setModelTransformationMode(ModelTransformationMode.GROUND);
        renderState.setModel(getModel(Registries.ITEM.getId(item)), RenderLayer.getCutout());

        itemRenderState.render(matrices, vertexConsumers, light, overlay);
    }

    public static class DynamicBaker implements Baker {
        @Override
        public BakedModel bake(Identifier id, ModelBakeSettings settings) {
            return null; // Not used in Json models, so we just leave ít like this and cross our fingers.
        }

        @Override
        public SpriteGetter getSpriteGetter() {
            return new SpriteGetter() {
                @Override
                public Sprite get(SpriteIdentifier spriteId) { return spriteId.getSprite(); }

                @Override
                public Sprite getMissing(String textureId) {
                    return MinecraftClient.getInstance().getPaintingManager().getBackSprite();
                    // should be the missing sprite, but this also works lol
                }
            };
        }

        @Override // no clue what this does
        public ModelNameSupplier getModelNameSupplier() { return () -> "round_disc"; }
    }
}
