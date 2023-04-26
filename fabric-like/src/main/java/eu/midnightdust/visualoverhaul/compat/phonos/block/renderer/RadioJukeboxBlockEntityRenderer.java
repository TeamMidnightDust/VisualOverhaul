package eu.midnightdust.visualoverhaul.compat.phonos.block.renderer;

import eu.midnightdust.visualoverhaul.compat.phonos.block.RadioJukeboxTop;
import eu.midnightdust.visualoverhaul.compat.phonos.init.PhonosCompatInit;
import eu.midnightdust.visualoverhaul.config.VOConfig;
import io.github.foundationgames.phonos.block.RadioJukeboxBlock;
import io.github.foundationgames.phonos.block.entity.RadioJukeboxBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class RadioJukeboxBlockEntityRenderer implements BlockEntityRenderer<RadioJukeboxBlockEntity> {
    private ItemStack record;
    private float rotation = 0;
    private BlockState blockState;
    private final MinecraftClient client = MinecraftClient.getInstance();

    public RadioJukeboxBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super();
    }

    @Override
    public void render(RadioJukeboxBlockEntity jukebox, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (VOConfig.jukebox && client.world != null) {
//            matrices.push();
//            Vector3f vecPos = matrices.peek().getNormalMatrix().decomposeLinearTransformation().getMiddle();
//            BlockPos pos = new BlockPos(vecPos.getX(), vecPos.getY(), vecPos.getZ());
//            int lightAbove = WorldRenderer.getLightmapCoordinates(client.world, pos.up());
//
//            // Tries to get the disc using the serverside method
//            if (jukebox.getStack(jukebox.getPlayingSong()) != ItemStack.EMPTY) {
//                record = jukebox.getStack(jukebox.getPlayingSong()).copy();
//                record.setCount(2);
//            }
//            // If the sound is stopped or no sound is playing, the stack is set to an empty stack //
//            else {
//                record = ItemStack.EMPTY;
//            }
//
//            matrices.translate(0.5f, 1.03f, 0.5f);
//            matrices.scale(0.75f, 0.75f, 0.75f);
//
//            if (jukebox.isPlaying()) {
//                rotation = (client.world.getTime() + tickDelta) * 4;
//            }
//            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rotation));
//            client.getItemRenderer().renderItem(record, ModelTransformation.Mode.GROUND, lightAbove, overlay, matrices, vertexConsumers, 0);
//
//            matrices.pop();
//            if (VOConfig.jukebox_fake_block && client.world.getBlockState(pos.up()).getBlock() == Blocks.AIR) {
//                blockState = client.world.getBlockState(pos);
//                matrices.push();
//                matrices.translate(0f, 1f, 0f);
//                if (record == ItemStack.EMPTY) {
//                    client.getBlockRenderManager().renderBlock(PhonosCompatInit.RadioJukeboxTop.getDefaultState().with(RadioJukeboxTop.PLAYING, false).with(RadioJukeboxTop.CHANNEL, blockState.get(RadioJukeboxBlock.CHANNEL)), pos.up(), client.world, matrices, vertexConsumers.getBuffer(RenderLayer.getCutout()), false, Random.create());
//                } else {
//                    client.getBlockRenderManager().renderBlock(PhonosCompatInit.RadioJukeboxTop.getDefaultState().with(RadioJukeboxTop.PLAYING, true).with(RadioJukeboxTop.CHANNEL, blockState.get(RadioJukeboxBlock.CHANNEL)), pos.up(), client.world, matrices, vertexConsumers.getBuffer(RenderLayer.getCutout()), false, Random.create());
//                }
//                matrices.pop();
//            }
        }
    }
}