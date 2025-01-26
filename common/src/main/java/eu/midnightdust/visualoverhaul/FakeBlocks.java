package eu.midnightdust.visualoverhaul;

import eu.midnightdust.visualoverhaul.config.VOConfig;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelNameSupplier;
import net.minecraft.client.model.SpriteGetter;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static eu.midnightdust.visualoverhaul.VisualOverhaulCommon.LOGGER;

public class FakeBlocks {
    private static final Baker BAKER = new FakeBaker();
    private static final Map<Identifier, BakedModel> FAKE_MODELS = new HashMap<>();
    private static final BlockRenderManager renderManager = MinecraftClient.getInstance().getBlockRenderManager();

    public static void reload(ResourceManager manager) {
        manager.findResources("models", path -> path.getPath().startsWith("models/fakeblock") && path.getPath().endsWith(".json")).forEach((id, resource) -> {
            try {
                JsonUnbakedModel unbaked = JsonUnbakedModel.deserialize(resource.getReader());
                BakedModel baked = unbaked.bake(new ModelTextures.Builder().addFirst(unbaked.getTextures()).build(() -> "#fakeblock"), BAKER,
                        new ModelBakeSettings(){}, Boolean.TRUE.equals(unbaked.getAmbientOcclusion()), unbaked.getGuiLight() != null && unbaked.getGuiLight().isSide(), unbaked.getTransformation());
                Identifier fakeId = Identifier.of(id.getNamespace(), id.getPath().replace("models/fakeblock/", "").replace(".json", ""));
                FAKE_MODELS.put(fakeId, baked);
                if (VOConfig.debug) LOGGER.info("Successfully loaded fake block model: {}", fakeId);
            } catch (IOException e) {
                LOGGER.error("Error occurred while loading fake block model {}", id.toString(), e);
            }
        });
    }
    public static void renderFakeBlock(Identifier id, BlockPos pos, BlockRenderView world, MatrixStack matrices, VertexConsumer vertexConsumer) {
        renderManager.getModelRenderer().render(world, FAKE_MODELS.get(id), Blocks.DIRT.getDefaultState(), // State is just needed for a few generic checks
                pos, matrices, vertexConsumer, false, Random.create(), 0, OverlayTexture.DEFAULT_UV);
    }

    public static class FakeBaker implements Baker {
        public BakedModel bake(Identifier id, ModelBakeSettings settings) {
            return null; // Not used in Json models, so we just leave ít like this and cross our fingers.
        }

        @Override
        public SpriteGetter getSpriteGetter() {
            return new SpriteGetter() {
                static final SpriteIdentifier MISSING = new SpriteIdentifier(Identifier.ofVanilla("textures/atlas/blocks.png"), Identifier.ofVanilla("missingno"));

                @Override public Sprite get(SpriteIdentifier spriteId) { return spriteId.getSprite(); }
                @Override public Sprite getMissing(String textureId) { return MISSING.getSprite(); }
            };
        }

        public ModelNameSupplier getModelNameSupplier() { return () -> "#fakeblock"; }
    }
}
