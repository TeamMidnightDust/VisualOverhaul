package eu.midnightdust.visualoverhaul.util.forge;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;

import java.nio.file.Path;

public class ModIconUtilImpl {
    public static Path getPath(String modid) {
        IModInfo mod = ModList.get().getMods().stream().filter(modInfo -> modInfo.getModId().equals(modid)).findFirst().orElseThrow(() -> new RuntimeException("Cannot get ModContainer for Forge mod with id "));
        return mod.getOwningFile().getFile().findResource(mod.getLogoFile().orElseThrow());
    }
}
