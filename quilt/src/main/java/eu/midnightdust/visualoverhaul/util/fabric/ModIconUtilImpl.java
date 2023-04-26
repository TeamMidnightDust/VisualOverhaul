package eu.midnightdust.visualoverhaul.util.fabric;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;

import java.nio.file.Path;

public class ModIconUtilImpl {
    public static Path getPath(String modid) {
        ModContainer mod = QuiltLoader.getModContainer(modid).orElseThrow(() -> new RuntimeException("Cannot get ModContainer for Fabric mod with id "));
        return mod.getPath(mod.metadata().icon(16));
    }
}
