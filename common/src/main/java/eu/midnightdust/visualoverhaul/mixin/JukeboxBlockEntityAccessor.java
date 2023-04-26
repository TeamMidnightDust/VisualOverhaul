package eu.midnightdust.visualoverhaul.mixin;

import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(JukeboxBlockEntity.class)
public interface JukeboxBlockEntityAccessor {
    @Accessor @Final
    DefaultedList<ItemStack> getInventory();
}
