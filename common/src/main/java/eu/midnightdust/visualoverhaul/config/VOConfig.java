package eu.midnightdust.visualoverhaul.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class VOConfig extends MidnightConfig {
    public static final String blocks = "blocks";
    public static final String colors = "colors";
    public static final String gui = "gui";
    @Client @Entry(category = blocks) public static boolean brewingstand = true;
    @Client @Entry(category = blocks) public static boolean jukebox = true;
    @Client @Entry(category = blocks) public static boolean jukebox_fake_block = true;
    @Client @Entry(category = blocks) public static boolean furnace = true;
    @Client @Entry(category = blocks) public static boolean smoker_particles = true;
    @Client @Entry(category = blocks) public static boolean blast_furnace_particles = true;
    @Client @Entry(category = colors) public static boolean coloredItems = true;
    @Client @Entry(category = colors) public static boolean coloredLilypad = true;
    @Client @Entry(category = gui) public static boolean buttonIcons = true;
    @Client @Entry(category = gui) public static IconPosition buttonIconPosition = IconPosition.LOCATION;
    @Client @Entry(category = gui) public static boolean zoomIconOnHover = true;
    @Client @Entry(category = gui, name = "Debug") public static boolean debug = false;
    @Client @Entry @Hidden public static boolean firstLaunch = true;
    public enum IconPosition {LOCATION, LEFT, RIGHT, BOTH}
}
