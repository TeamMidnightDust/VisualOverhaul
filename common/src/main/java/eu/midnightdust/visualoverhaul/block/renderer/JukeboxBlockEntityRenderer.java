package eu.midnightdust.visualoverhaul.block.renderer;

import eu.midnightdust.visualoverhaul.FakeBlocks;
import eu.midnightdust.visualoverhaul.VisualOverhaulClient;
import eu.midnightdust.visualoverhaul.config.VOConfig;
import eu.midnightdust.visualoverhaul.util.SoundTest;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.SideShapeType;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.joml.AxisAngle4f;
import org.joml.Math;
import org.joml.Quaternionf;

import java.util.Objects;

import static eu.midnightdust.visualoverhaul.VisualOverhaulCommon.id;
import static eu.midnightdust.visualoverhaul.VisualOverhaulCommon.jukeboxItems;

@Environment(EnvType.CLIENT)
public class JukeboxBlockEntityRenderer implements BlockEntityRenderer<JukeboxBlockEntity> {

    public JukeboxBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(JukeboxBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (VOConfig.jukebox) {
            int lightAbove = WorldRenderer.getLightmapCoordinates(Objects.requireNonNull(blockEntity.getWorld()), blockEntity.getPos().up());

            Identifier discModel = null; // If the sound is stopped or no sound is playing, no model is set //

            // Tries to get the disc using the serverside method
            if (jukeboxItems.containsKey(blockEntity.getPos()) && !jukeboxItems.get(blockEntity.getPos()).isEmpty()) {
                discModel = RoundDiscRenderer.getModelId(jukeboxItems.get(blockEntity.getPos()));
            }
            // Else gets the record sound played at the position of the jukebox //
            else if (SoundTest.getSound(blockEntity.getPos()) != null) {
                // Converts the Sound ID to the item ID of the appropriate disc (minecraft:music_disc.cat -> minecraft:music_disc_cat) //
                discModel = Identifier.of(String.valueOf(SoundTest.getSound(blockEntity.getPos())).replace(".", "_"));
            }

            if (discModel != null) {
                matrices.push();

                matrices.translate(0.5f, 1.03f, 0.5f);
                matrices.scale(0.75f, 0.75f, 0.75f);
                matrices.multiply(new Quaternionf(new AxisAngle4f(Math.toRadians(Util.getMeasuringTimeMs() / 9.0f), 0, 1, 0)));

                RoundDiscRenderer.render(discModel, lightAbove, overlay, matrices, vertexConsumers);
                matrices.pop();
            }
            if (VOConfig.jukebox_fake_block && !blockEntity.getWorld().getBlockState(blockEntity.getPos().up()).isSideSolid(blockEntity.getWorld(),blockEntity.getPos().up(), Direction.DOWN, SideShapeType.FULL)) {
                matrices.push();
                matrices.translate(0f, 1f, 0f);
                Identifier blockId = discModel != null ? id("jukebox_top_playing") : id("jukebox_top_stopped");
                FakeBlocks.renderFakeBlock(blockId, blockEntity.getPos().up(), blockEntity.getWorld(), matrices, vertexConsumers.getBuffer(RenderLayer.getCutout()));
                matrices.pop();
            }
        }
    }
}