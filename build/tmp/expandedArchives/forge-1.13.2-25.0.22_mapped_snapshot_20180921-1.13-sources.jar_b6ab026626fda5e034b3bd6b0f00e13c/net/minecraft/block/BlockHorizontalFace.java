package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;

public class BlockHorizontalFace extends BlockHorizontal {
   public static final EnumProperty<AttachFace> FACE = BlockStateProperties.FACE;

   protected BlockHorizontalFace(Block.Properties builder) {
      super(builder);
   }

   public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
      EnumFacing enumfacing = getFacing(state).getOpposite();
      BlockPos blockpos = pos.offset(enumfacing);
      IBlockState iblockstate = worldIn.getBlockState(blockpos);
      Block block = iblockstate.getBlock();
      if (isExceptionBlockForAttaching(block)) {
         return false;
      } else {
         boolean flag = iblockstate.getBlockFaceShape(worldIn, blockpos, enumfacing.getOpposite()) == BlockFaceShape.SOLID;
         if (enumfacing == EnumFacing.UP) {
            return block == Blocks.HOPPER || flag;
         } else {
            return !isExceptBlockForAttachWithPiston(block) && flag;
         }
      }
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      for(EnumFacing enumfacing : context.getNearestLookingDirections()) {
         IBlockState iblockstate;
         if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            iblockstate = this.getDefaultState().with(FACE, enumfacing == EnumFacing.UP ? AttachFace.CEILING : AttachFace.FLOOR).with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing());
         } else {
            iblockstate = this.getDefaultState().with(FACE, AttachFace.WALL).with(HORIZONTAL_FACING, enumfacing.getOpposite());
         }

         if (iblockstate.isValidPosition(context.getWorld(), context.getPos())) {
            return iblockstate;
         }
      }

      return null;
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
      return getFacing(stateIn).getOpposite() == facing && !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   protected static EnumFacing getFacing(IBlockState p_196365_0_) {
      switch((AttachFace)p_196365_0_.get(FACE)) {
      case CEILING:
         return EnumFacing.DOWN;
      case FLOOR:
         return EnumFacing.UP;
      default:
         return p_196365_0_.get(HORIZONTAL_FACING);
      }
   }
}