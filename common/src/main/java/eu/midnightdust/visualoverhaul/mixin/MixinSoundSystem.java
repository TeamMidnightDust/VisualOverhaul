package eu.midnightdust.visualoverhaul.mixin;

import eu.midnightdust.visualoverhaul.util.SoundTest;
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
    public void vo$onPlayRecordSound(SoundInstance soundInstance, CallbackInfo ci) {
        if (soundInstance.getCategory().equals(SoundCategory.RECORDS) && this.started) {
            jukeboxPos = new BlockPos(Math.floor(soundInstance.getX()), Math.floor(soundInstance.getY()), Math.floor(soundInstance.getZ()));
            SoundTest.soundPos.put(jukeboxPos, soundInstance.getId());
        }
    }

    @Inject(at = @At("HEAD"),method = "stop(Lnet/minecraft/client/sound/SoundInstance;)V")
    public void vo$onStopRecordSound(SoundInstance soundInstance, CallbackInfo ci) {
        if (soundInstance != null) {
            if (soundInstance.getCategory().equals(SoundCategory.RECORDS)) {
                jukeboxPos = new BlockPos(Math.floor(soundInstance.getX()), Math.floor(soundInstance.getY()), Math.floor(soundInstance.getZ()));
                if (SoundTest.soundPos.containsKey(jukeboxPos)) {
                    SoundTest.soundPos.remove(jukeboxPos, soundInstance.getId());
                }
            }
        }
    }
}