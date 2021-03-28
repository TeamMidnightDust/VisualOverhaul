package eu.midnightdust.visualoverhaul.util.sound;

import com.google.common.collect.Maps;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.Map;

public class SoundTest {

    public static Map<BlockPos, Identifier> soundPos = Maps.newHashMap();

    /**
     * Returns the Sound provided in MixinSoundSystem
     * {@link eu.midnightdust.visualoverhaul.mixin.MixinSoundSystem}
     */
    public static Identifier getSound(BlockPos pos) {
        if (soundPos.containsKey(pos)) {
            return soundPos.get(pos);
        }
        return null;
    }
}
