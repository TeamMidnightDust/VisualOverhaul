package eu.midnightdust.visualoverhaul.config;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;

import java.util.ArrayList;
import java.util.List;

@Config(name = "visualoverhaul")
public class VOConfig implements ConfigData {

    public boolean brewingstand = true;
    public boolean jukebox = true;
    public boolean jukebox_fake_block = true;
}
