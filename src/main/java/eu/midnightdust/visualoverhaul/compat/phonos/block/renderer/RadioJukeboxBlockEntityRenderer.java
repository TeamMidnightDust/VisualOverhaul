package eu.midnightdust.visualoverhaul.compat.phonos.block.renderer;
//
//import eu.midnightdust.visualoverhaul.compat.phonos.block.RadioJukeboxTop;
//import eu.midnightdust.visualoverhaul.compat.phonos.init.PhonosCompatInit;
//import eu.midnightdust.visualoverhaul.config.VOConfig;
//import io.github.foundationgames.phonos.block.RadioJukeboxBlock;
//import io.github.foundationgames.phonos.block.entity.RadioJukeboxBlockEntity;
//import net.fabricmc.api.EnvType;
//import net.fabricmc.api.Environment;
//import net.minecraft.block.BlockState;
//import net.minecraft.block.Blocks;
//import net.minecraft.client.MinecraftClient;
//import net.minecraft.client.render.RenderLayer;
//import net.minecraft.client.render.VertexConsumerProvider;
//import net.minecraft.client.render.WorldRenderer;
//import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
//import net.minecraft.client.render.block.entity.BlockEntityRenderer;
//import net.minecraft.client.render.model.json.ModelTransformation;
//import net.minecraft.client.util.math.MatrixStack;
//import net.minecraft.item.ItemStack;
//
//import java.util.Random;
//
//@Environment(EnvType.CLIENT)
//public class RadioJukeboxBlockEntityRenderer implements BlockEntityRenderer<RadioJukeboxBlockEntity> {
//    private ItemStack record;
//    private float rotation = 0;
//    private BlockState blockState;
//
//    public RadioJukeboxBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
//        super();
//    }
//
//    @Override
//    public void render(RadioJukeboxBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
//        if (VOConfig.jukebox) {
//            int lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().up());
//
//            // Tries to get the disc using the serverside method
//            if (blockEntity.getStack(blockEntity.getPlayingSong()) != ItemStack.EMPTY) {
//                record = blockEntity.getStack(blockEntity.getPlayingSong()).copy();
//                record.setCount(2);
//            }
//            // If the sound is stopped or no sound is playing, the stack is set to an empty stack //
//            else {
//                record = ItemStack.EMPTY;
//            }
//
//            matrices.push();
//
//            matrices.translate(0.5f, 1.03f, 0.5f);
//            matrices.scale(0.75f, 0.75f, 0.75f);
//
//            if (blockEntity.isPlaying()) {
//                rotation = (blockEntity.getWorld().getTime() + tickDelta) * 4;
//            }
//            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(rotation));
//            MinecraftClient.getInstance().getItemRenderer().renderItem(record, ModelTransformation.Mode.GROUND, lightAbove, overlay, matrices, vertexConsumers);
//
//            matrices.pop();
//            if (VOConfig.jukebox_fake_block && blockEntity.getWorld().getBlockState(blockEntity.getPos().up()).getBlock() == Blocks.AIR) {
//                blockState = blockEntity.getWorld().getBlockState(blockEntity.getPos());
//                matrices.push();
//                matrices.translate(0f, 1f, 0f);
//                if (record == ItemStack.EMPTY) {
//                    MinecraftClient.getInstance().getBlockRenderManager().renderBlock(PhonosCompatInit.RadioJukeboxTop.getDefaultState().with(RadioJukeboxTop.PLAYING, false).with(RadioJukeboxTop.CHANNEL, blockState.get(RadioJukeboxBlock.CHANNEL)), blockEntity.getPos().up(), blockEntity.getWorld(), matrices, vertexConsumers.getBuffer(RenderLayer.getCutout()), false, new Random());
//                } else {
//                    MinecraftClient.getInstance().getBlockRenderManager().renderBlock(PhonosCompatInit.RadioJukeboxTop.getDefaultState().with(RadioJukeboxTop.PLAYING, true).with(RadioJukeboxTop.CHANNEL, blockState.get(RadioJukeboxBlock.CHANNEL)), blockEntity.getPos().up(), blockEntity.getWorld(), matrices, vertexConsumers.getBuffer(RenderLayer.getCutout()), false, new Random());
//                }
//                matrices.pop();
//            }
//        }
//    }
//}