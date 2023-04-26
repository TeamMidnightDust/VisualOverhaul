package eu.midnightdust.visualoverhaul.util;

import eu.midnightdust.visualoverhaul.config.VOConfig;
import net.fabricmc.loader.api.FabricLoader;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import org.apache.commons.lang3.Validate;
import org.spongepowered.asm.mixin.Unique;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ModIconUtil {
    private static final Map<Path, NativeImageBackedTexture> modIconCache = new HashMap<>();
    private final String modid;

    public ModIconUtil(String modid) {
        this.modid = modid;
    }
    @ExpectPlatform
    public static Path getPath(String modid) {
        throw new AssertionError();
    }

    @Unique
    public NativeImageBackedTexture createModIcon() {
        try {
            Path path = getPath(modid);
            if (VOConfig.debug) System.out.println(path);
            NativeImageBackedTexture cachedIcon = getCachedModIcon(path);
            if (cachedIcon != null) {
                return cachedIcon;
            }
            cachedIcon = getCachedModIcon(path);
            if (cachedIcon != null) {
                return cachedIcon;
            }
            try (InputStream inputStream = Files.newInputStream(path)) {
                NativeImage image = NativeImage.read(Objects.requireNonNull(inputStream));
                Validate.validState(image.getHeight() == image.getWidth(), "Must be square icon");
                NativeImageBackedTexture tex = new NativeImageBackedTexture(image);
                cacheModIcon(path, tex);
                return tex;
            }

        } catch (Throwable t) {
            if (VOConfig.debug) System.out.println(t.getMessage());
            return null;
        }
    }
    static NativeImageBackedTexture getCachedModIcon(Path path) {
        return modIconCache.get(path);
    }

    static void cacheModIcon(Path path, NativeImageBackedTexture tex) {
        modIconCache.put(path, tex);
    }
}
