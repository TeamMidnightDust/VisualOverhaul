package eu.midnightdust.visualoverhaul;

import eu.midnightdust.visualoverhaul.config.VOConfig;

import static eu.midnightdust.visualoverhaul.VisualOverhaulCommon.MOD_ID;

public class VisualOverhaulClient {
    public static int waterColor = 4159204;
    public static int foliageColor = -8934609;
    public static int grassColor = -8934609;
    public static int potionColor = -13083194;

    public static void onInitializeClient() {
        VOConfig.init(MOD_ID, VOConfig.class);
    }
}
