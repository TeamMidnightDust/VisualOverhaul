package eu.midnightdust.visualoverhaul.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

import static eu.midnightdust.visualoverhaul.VisualOverhaul.MOD_ID;

@Mod(MOD_ID)
public class VisualOverhaulForge {

    public VisualOverhaulForge() {
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> VisualOverhaulClientForge::initClient);
    }
}
