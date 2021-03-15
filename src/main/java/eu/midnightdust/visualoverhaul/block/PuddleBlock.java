package eu.midnightdust.visualoverhaul.block;

import eu.midnightdust.visualoverhaul.VisualOverhaul;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.*;
import net.minecraft.item.*;
import net.minecraft.loot.context.LootContext;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
public class PuddleBlock extends Block {

    protected final FlowableFluid fluid;
    public static final VoxelShape COLLISION_SHAPE;

    public PuddleBlock(FlowableFluid fluid, AbstractBlock.Settings settings) {
        super(settings);
        this.fluid = fluid;
    }
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isEmpty()) {
            return ActionResult.PASS;
        } else {
            Item item = itemStack.getItem();
            ItemStack waterBottleStack;
            if (item == Items.GLASS_BOTTLE) {
                if (!world.isClient) {
                    if (!player.abilities.creativeMode) {
                        waterBottleStack = PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER);
                        player.incrementStat(Stats.USE_CAULDRON);
                        itemStack.decrement(1);
                        if (itemStack.isEmpty()) {
                            player.setStackInHand(hand, waterBottleStack);
                        } else if (!player.inventory.insertStack(waterBottleStack)) {
                            player.dropItem(waterBottleStack, false);
                        } else if (player instanceof ServerPlayerEntity) {
                            ((ServerPlayerEntity)player).refreshScreenHandler(player.playerScreenHandler);
                        }
                    }

                    world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    world.setBlockState(pos, Blocks.AIR.getDefaultState());
                }
                return ActionResult.success(world.isClient);
            }
            else return ActionResult.FAIL;
        }

    }
    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }
    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!world.isRaining() && random.nextInt(2000) == 0) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }

        this.scheduledTick(state, world, pos, random);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return context.isAbove(COLLISION_SHAPE, pos, true) ? COLLISION_SHAPE : VoxelShapes.empty();
    }
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return  COLLISION_SHAPE;
    }
    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return true;
    }

    public FluidState getFluidState(BlockState state) {
        return fluid.getFlowing(1,false);
    }

    @Environment(EnvType.CLIENT)
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        return stateFrom.getFluidState().getFluid().matchesType(this.fluid);
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        return Collections.emptyList();
    }

    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if (world.getBlockState(pos) == Blocks.AIR.getDefaultState() || world.getBlockState(pos) == VisualOverhaul.Puddle.getDefaultState()) {
            int i;
            // Check if there are fluids on the sides or corners of the block above
            for (i = 2; i < 6; ++i) {
                BlockPos pos1 = pos.up();
                BlockPos pos2 = pos1.offset(Direction.byId(i));
                if (!world.getFluidState(pos1.offset(Direction.byId(i))).isEmpty()) {
                    // When sides of the block above have water don't place the puddle
                    return false;
                }
                if (!world.getFluidState(pos2.offset(Direction.byId(i).rotateYClockwise())).isEmpty()) {
                    // When corners of the block above have water don't place the puddle
                    return false;
                }
            }
            // Check if there are fluids on the sides or corners of the block below
            for (i = 2; i < 6; ++i) {
                BlockPos pos1 = pos.down();
                BlockPos pos2 = pos1.offset(Direction.byId(i));
                if (!world.getFluidState(pos1.offset(Direction.byId(i))).isEmpty()) {
                    // When sides of the block below have water don't place the puddle
                    return false;
                }
                if (!world.getFluidState(pos2.offset(Direction.byId(i).rotateYClockwise())).isEmpty()) {
                    // When corners of the block below have water don't place the puddle
                    return false;
                }
            }
            return world.getBlockState(pos.down()).isSideSolidFullSquare(world, pos, Direction.UP);
        }
        // When there's already another block at the position don't place the puddle
        else return false;
    }
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        return !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }

    static {
        COLLISION_SHAPE = net.minecraft.block.Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 0.5D, 16.0D);
    }
}

