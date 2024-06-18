package eu.midnightdust.visualoverhaul.util;

import com.google.common.collect.Maps;
import eu.midnightdust.visualoverhaul.config.VOConfig;
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
        if (VOConfig.jukebox_clientside && soundPos.containsKey(pos)) {
            return soundPos.get(pos);
        }
        return null;
    }
}
