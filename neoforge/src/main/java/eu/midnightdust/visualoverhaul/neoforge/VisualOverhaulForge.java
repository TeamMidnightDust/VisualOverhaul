package eu.midnightdust.visualoverhaul.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

import static eu.midnightdust.visualoverhaul.VisualOverhaulCommon.*;

@Mod(MOD_ID)
public class VisualOverhaulForge {

    public VisualOverhaulForge() {
        if (FMLEnvironment.dist == Dist.CLIENT) VisualOverhaulClientForge.initClient();
    }
}
