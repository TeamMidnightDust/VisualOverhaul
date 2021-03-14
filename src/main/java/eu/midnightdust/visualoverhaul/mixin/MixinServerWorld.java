package eu.midnightdust.visualoverhaul.mixin;

import eu.midnightdust.visualoverhaul.VisualOverhaul;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.*;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class MixinServerWorld extends World {
    protected MixinServerWorld(MutableWorldProperties properties, RegistryKey<World> registryRef, DimensionType dimensionType, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed) {
        super(properties, registryRef, dimensionType, profiler, isClient, debugWorld, seed);
    }

    @Shadow protected abstract BlockPos getSurface(BlockPos pos);

    @Inject(at = @At("TAIL"),method = "tickChunk")
    public void tickChunk(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci) {
        ChunkPos chunkPos = chunk.getPos();
        boolean bl = this.isRaining();
        int x = chunkPos.getStartX();
        int z = chunkPos.getStartZ();
        Profiler profiler = this.getProfiler();
        BlockPos pos;

        if (this.getGameRules().getInt(VisualOverhaul.PUDDLE_SPAWN_RATE) != 0) {
            profiler.push("puddles");
            if (bl && random.nextInt(10000 / this.getGameRules().getInt(VisualOverhaul.PUDDLE_SPAWN_RATE)) == 0) {
                pos = this.getSurface(getRandomPosInChunk(x, 0, z, 15));
                if (this.hasRain(pos) && getBlockState(pos.down()).isSideSolidFullSquare(this, pos, Direction.UP)) {
                    setBlockState(pos, VisualOverhaul.Puddle.getDefaultState());
                }
            }
            profiler.pop();
        }

        if (this.getGameRules().getInt(VisualOverhaul.SNOW_STACK_CHANCE) != 0) {
            profiler.push("extra_snow");
            if (bl && random.nextInt(10000 / this.getGameRules().getInt(VisualOverhaul.SNOW_STACK_CHANCE)) == 0) {
                pos = this.getSurface(getRandomPosInChunk(x, 0, z, 15));
                if (this.getBlockState(pos).getBlock() == Blocks.SNOW && getBlockState(pos.down()).isSideSolidFullSquare(this, pos, Direction.UP)) {
                    int layer = getBlockState(pos).get(Properties.LAYERS);
                    setBlockState(pos, Blocks.SNOW.getDefaultState().with(Properties.LAYERS, layer + 1));
                }
            }
            profiler.pop();
        }
    }
}