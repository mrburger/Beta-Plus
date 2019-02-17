package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.stats.StatList;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockSeaGrass extends BlockBush implements IGrowable, ILiquidContainer {
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);

   protected BlockSeaGrass(Block.Properties builder) {
      super(builder);
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return SHAPE;
   }

   protected boolean isValidGround(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return Block.doesSideFillSquare(state.getCollisionShape(worldIn, pos), EnumFacing.UP) && state.getBlock() != Blocks.MAGMA_BLOCK;
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
      return ifluidstate.isTagged(FluidTags.WATER) && ifluidstate.getLevel() == 8 ? super.getStateForPlacement(context) : null;
   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    *  
    * @param facingState The state that is currently at the position offset of the provided face to the stateIn at
    * currentPos
    */
   public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      IBlockState iblockstate = super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
      if (!iblockstate.isAir()) {
         worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
      }

      return iblockstate;
   }

   /**
    * Spawns the block's drops in the world. By the time this is called the Block has possibly been set to air via
    * Block.removedByPlayer
    */
   public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
      if (!worldIn.isRemote && stack.getItem() == Items.SHEARS) {
         player.addStat(StatList.BLOCK_MINED.get(this));
         player.addExhaustion(0.005F);
         spawnAsEntity(worldIn, pos, new ItemStack(this));
      } else {
         super.harvestBlock(worldIn, player, pos, state, te, stack);
      }

   }

   public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
      return Items.AIR;
   }

   /**
    * Whether this IGrowable can grow
    */
   public boolean canGrow(IBlockReader worldIn, BlockPos pos, IBlockState state, boolean isClient) {
      return true;
   }

   public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      return true;
   }

   public IFluidState getFluidState(IBlockState state) {
      return Fluids.WATER.getStillFluidState(false);
   }

   public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      IBlockState iblockstate = Blocks.TALL_SEAGRASS.getDefaultState();
      IBlockState iblockstate1 = iblockstate.with(BlockSeaGrassTall.field_208065_c, DoubleBlockHalf.UPPER);
      BlockPos blockpos = pos.up();
      if (worldIn.getBlockState(blockpos).getBlock() == Blocks.WATER) {
         worldIn.setBlockState(pos, iblockstate, 2);
         worldIn.setBlockState(blockpos, iblockstate1, 2);
      }

   }

   public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, IBlockState state, Fluid fluidIn) {
      return false;
   }

   public boolean receiveFluid(IWorld worldIn, BlockPos pos, IBlockState state, IFluidState fluidStateIn) {
      return false;
   }

   public int getOpacity(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return Blocks.WATER.getDefaultState().getOpacity(worldIn, pos);
   }
}