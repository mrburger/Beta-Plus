package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockSeaGrassTall extends BlockShearableDoublePlant implements ILiquidContainer {
   public static final EnumProperty<DoubleBlockHalf> field_208065_c = BlockShearableDoublePlant.field_208063_b;
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

   public BlockSeaGrassTall(Block p_i48779_1_, Block.Properties p_i48779_2_) {
      super(p_i48779_1_, p_i48779_2_);
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return SHAPE;
   }

   protected boolean isValidGround(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return Block.doesSideFillSquare(state.getCollisionShape(worldIn, pos), EnumFacing.UP) && state.getBlock() != Blocks.MAGMA_BLOCK;
   }

   public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
      return Items.AIR;
   }

   public ItemStack getItem(IBlockReader worldIn, BlockPos pos, IBlockState state) {
      return new ItemStack(Blocks.SEAGRASS);
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      IBlockState iblockstate = super.getStateForPlacement(context);
      if (iblockstate != null) {
         IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos().up());
         if (ifluidstate.isTagged(FluidTags.WATER) && ifluidstate.getLevel() == 8) {
            return iblockstate;
         }
      }

      return null;
   }

   public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
      if (state.get(field_208065_c) == DoubleBlockHalf.UPPER) {
         IBlockState iblockstate = worldIn.getBlockState(pos.down());
         return iblockstate.getBlock() == this && iblockstate.get(field_208065_c) == DoubleBlockHalf.LOWER;
      } else {
         IFluidState ifluidstate = worldIn.getFluidState(pos);
         return super.isValidPosition(state, worldIn, pos) && ifluidstate.isTagged(FluidTags.WATER) && ifluidstate.getLevel() == 8;
      }
   }

   public IFluidState getFluidState(IBlockState state) {
      return Fluids.WATER.getStillFluidState(false);
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