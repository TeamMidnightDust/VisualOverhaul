package eu.midnightdust.visualoverhaul.util.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.nio.file.Path;

public class ModIconUtilImpl {
    public static Path getPath(String modid) {
        ModContainer mod = FabricLoader.getInstance().getModContainer(modid).orElseThrow(() -> new RuntimeException("Cannot get ModContainer for Fabric mod with id "));
        return mod.findPath(mod.getMetadata().getIconPath(16).orElseThrow()).orElseThrow();
    }
}
