package eu.midnightdust.visualoverhaul.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;

public class JukeboxTop extends Block {
    private static final BooleanProperty HAS_RECORD = Properties.HAS_RECORD;

    public JukeboxTop() {
        super(AbstractBlock.Settings.copy(Blocks.JUKEBOX));
        this.setDefaultState(this.stateManager.getDefaultState().with(HAS_RECORD,false));
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HAS_RECORD);
    }
}
