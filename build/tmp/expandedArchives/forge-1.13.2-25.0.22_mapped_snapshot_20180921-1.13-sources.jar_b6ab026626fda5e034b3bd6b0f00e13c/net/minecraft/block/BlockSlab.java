package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.SlabType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public class BlockSlab extends Block implements IBucketPickupHandler, ILiquidContainer {
   public static final EnumProperty<SlabType> TYPE = BlockStateProperties.SLAB_TYPE;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape BOTTOM_SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
   protected static final VoxelShape TOP_SHAPE = Block.makeCuboidShape(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);

   public BlockSlab(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.getDefaultState().with(TYPE, SlabType.BOTTOM).with(WATERLOGGED, Boolean.valueOf(false)));
   }

   public int getOpacity(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return worldIn.getMaxLightLevel();
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(TYPE, WATERLOGGED);
   }

   protected boolean canSilkHarvest() {
      return false;
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      SlabType slabtype = state.get(TYPE);
      switch(slabtype) {
      case DOUBLE:
         return VoxelShapes.fullCube();
      case TOP:
         return TOP_SHAPE;
      default:
         return BOTTOM_SHAPE;
      }
   }

   /**
    * Determines if the block is solid enough on the top side to support other blocks, like redstone components.
    * @deprecated prefer calling {@link IBlockState#isTopSolid()} wherever possible
    */
   public boolean isTopSolid(IBlockState state) {
      return state.get(TYPE) == SlabType.DOUBLE || state.get(TYPE) == SlabType.TOP;
   }

   /**
    * Get the geometry of the queried face at the given position and state. This is used to decide whether things like
    * buttons are allowed to be placed on the face, or how glass panes connect to the face, among other things.
    * <p>
    * Common values are {@code SOLID}, which is the default, and {@code UNDEFINED}, which represents something that does
    * not fit the other descriptions and will generally cause other things not to connect to the face.
    * 
    * @return an approximation of the form of the given face
    * @deprecated call via {@link IBlockState#getBlockFaceShape(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
    * Implementing/overriding is fine.
    */
   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      SlabType slabtype = state.get(TYPE);
      if (slabtype == SlabType.DOUBLE) {
         return BlockFaceShape.SOLID;
      } else if (face == EnumFacing.UP && slabtype == SlabType.TOP) {
         return BlockFaceShape.SOLID;
      } else {
         return face == EnumFacing.DOWN && slabtype == SlabType.BOTTOM ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
      }
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      IBlockState iblockstate = context.getWorld().getBlockState(context.getPos());
      if (iblockstate.getBlock() == this) {
         return iblockstate.with(TYPE, SlabType.DOUBLE).with(WATERLOGGED, Boolean.valueOf(false));
      } else {
         IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
         IBlockState iblockstate1 = this.getDefaultState().with(TYPE, SlabType.BOTTOM).with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
         EnumFacing enumfacing = context.getFace();
         return enumfacing != EnumFacing.DOWN && (enumfacing == EnumFacing.UP || !((double)context.getHitY() > 0.5D)) ? iblockstate1 : iblockstate1.with(TYPE, SlabType.TOP);
      }
   }

   public int quantityDropped(IBlockState state, Random random) {
      return state.get(TYPE) == SlabType.DOUBLE ? 2 : 1;
   }

   /**
    * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
    */
   public boolean isFullCube(IBlockState state) {
      return state.get(TYPE) == SlabType.DOUBLE;
   }

   public boolean isReplaceable(IBlockState state, BlockItemUseContext useContext) {
      ItemStack itemstack = useContext.getItem();
      SlabType slabtype = state.get(TYPE);
      if (slabtype != SlabType.DOUBLE && itemstack.getItem() == this.asItem()) {
         if (useContext.replacingClickedOnBlock()) {
            boolean flag = (double)useContext.getHitY() > 0.5D;
            EnumFacing enumfacing = useContext.getFace();
            if (slabtype == SlabType.BOTTOM) {
               return enumfacing == EnumFacing.UP || flag && enumfacing.getAxis().isHorizontal();
            } else {
               return enumfacing == EnumFacing.DOWN || !flag && enumfacing.getAxis().isHorizontal();
            }
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public Fluid pickupFluid(IWorld worldIn, BlockPos pos, IBlockState state) {
      if (state.get(WATERLOGGED)) {
         worldIn.setBlockState(pos, state.with(WATERLOGGED, Boolean.valueOf(false)), 3);
         return Fluids.WATER;
      } else {
         return Fluids.EMPTY;
      }
   }

   public IFluidState getFluidState(IBlockState state) {
      return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
   }

   public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, IBlockState state, Fluid fluidIn) {
      return state.get(TYPE) != SlabType.DOUBLE && !state.get(WATERLOGGED) && fluidIn == Fluids.WATER;
   }

   public boolean receiveFluid(IWorld worldIn, BlockPos pos, IBlockState state, IFluidState fluidStateIn) {
      if (state.get(TYPE) != SlabType.DOUBLE && !state.get(WATERLOGGED) && fluidStateIn.getFluid() == Fluids.WATER) {
         if (!worldIn.isRemote()) {
            worldIn.setBlockState(pos, state.with(WATERLOGGED, Boolean.valueOf(true)), 3);
            worldIn.getPendingFluidTicks().scheduleTick(pos, fluidStateIn.getFluid(), fluidStateIn.getFluid().getTickRate(worldIn));
         }

         return true;
      } else {
         return false;
      }
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
      if (stateIn.get(WATERLOGGED)) {
         worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
      }

      return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public boolean allowsMovement(IBlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      switch(type) {
      case LAND:
         return state.get(TYPE) == SlabType.BOTTOM;
      case WATER:
         return worldIn.getFluidState(pos).isTagged(FluidTags.WATER);
      case AIR:
         return false;
      default:
         return false;
      }
   }
}