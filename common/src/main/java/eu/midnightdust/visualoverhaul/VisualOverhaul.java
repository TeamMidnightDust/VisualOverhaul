package eu.midnightdust.visualoverhaul;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.compress.utils.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class VisualOverhaul {
    public static final String MOD_ID = "visualoverhaul";
    public static final List<UUID> playersWithMod = Lists.newArrayList();
    public static final Map<BlockPos, ItemStack> jukeboxItems = new HashMap<>();

    public static final Identifier HELLO_PACKET = new Identifier(MOD_ID, "hello");
    public static final Identifier UPDATE_POTION_BOTTLES = new Identifier(MOD_ID, "brewingstand");
    public static final Identifier UPDATE_RECORD = new Identifier(MOD_ID, "record");
    public static final Identifier UPDATE_FURNACE_ITEMS = new Identifier(MOD_ID, "furnace");
}
