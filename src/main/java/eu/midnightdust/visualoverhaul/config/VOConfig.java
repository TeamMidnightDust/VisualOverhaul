package eu.midnightdust.visualoverhaul.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class VOConfig extends MidnightConfig {
    @Client @Entry public static boolean brewingstand = true;
    @Client @Entry public static boolean jukebox = true;
    @Client @Entry public static boolean jukebox_fake_block = true;
    @Client @Entry public static boolean furnace = true;
    @Client @Entry public static boolean smoker_particles = true;
    @Client @Entry public static boolean blast_furnace_particles = true;
    @Client @Entry public static boolean coloredItems = true;
    @Client @Entry public static boolean coloredLilypad = true;
    @Client @Entry public static boolean potionEnchantmentGlint = true;
    @Client @Entry(name = "Debug") public static boolean debug = false;
}
