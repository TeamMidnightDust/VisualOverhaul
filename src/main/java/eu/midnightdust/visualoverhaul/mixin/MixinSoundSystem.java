package eu.midnightdust.visualoverhaul.mixin;

import eu.midnightdust.visualoverhaul.util.sound.SoundTest;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundSystem.class)
public abstract class MixinSoundSystem {

    @Shadow private boolean started;

    private BlockPos jukeboxPos;

    @Inject(at = @At("TAIL"),method = "play(Lnet/minecraft/client/sound/SoundInstance;)V")
    public void onPlayRecordSound(SoundInstance soundInstance, CallbackInfo ci) {
        if (soundInstance.getCategory().equals(SoundCategory.RECORDS) && this.started) {
            jukeboxPos = new BlockPos(soundInstance.getX(),soundInstance.getY(),soundInstance.getZ());
            SoundTest.soundPos.put(jukeboxPos, soundInstance.getId());
        }
    }

    @Inject(at = @At("TAIL"),method = "stop(Lnet/minecraft/client/sound/SoundInstance;)V")
    public void onStopRecordSound(SoundInstance soundInstance, CallbackInfo ci) {
        if (soundInstance.getCategory().equals(SoundCategory.RECORDS) && SoundTest.soundPos.containsKey(new BlockPos(soundInstance.getX(),soundInstance.getY(),soundInstance.getZ()))) {
            jukeboxPos = new BlockPos(soundInstance.getX(),soundInstance.getY(),soundInstance.getZ());
            SoundTest.soundPos.remove(jukeboxPos,soundInstance.getId());
        }
    }
}