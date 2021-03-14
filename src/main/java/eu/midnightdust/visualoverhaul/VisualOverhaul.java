package eu.midnightdust.visualoverhaul;

import eu.midnightdust.visualoverhaul.block.PuddleBlock;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;

public class VisualOverhaul implements ModInitializer {
    public static final String MOD_ID = "visualoverhaul";
    public static final Block Puddle = new PuddleBlock(Fluids.WATER, FabricBlockSettings.of(Material.WATER));
    public static GameRules.Key<GameRules.IntRule> PUDDLE_SPAWN_RATE;
    public static GameRules.Key<GameRules.IntRule> SNOW_STACK_CHANCE;

    public static final Identifier UPDATE_POTION_BOTTLES = new Identifier("visualoverhaul", "brewingstand");
    public static final Identifier UPDATE_RECORD = new Identifier("visualoverhaul", "record");
    public static final Identifier UPDATE_FURNACE_ITEMS = new Identifier("visualoverhaul", "furnace");

    public void onInitialize() {
        PUDDLE_SPAWN_RATE = GameRuleRegistry.register("puddleSpawnRate", GameRules.Category.SPAWNING, GameRuleFactory.createIntRule(1));
        SNOW_STACK_CHANCE = GameRuleRegistry.register("snowStackChance", GameRules.Category.SPAWNING, GameRuleFactory.createIntRule(1));
        Registry.register(Registry.BLOCK, new Identifier(MOD_ID,"puddle"), Puddle);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID,"puddle"), new BlockItem(Puddle, new Item.Settings()));
    }
}
