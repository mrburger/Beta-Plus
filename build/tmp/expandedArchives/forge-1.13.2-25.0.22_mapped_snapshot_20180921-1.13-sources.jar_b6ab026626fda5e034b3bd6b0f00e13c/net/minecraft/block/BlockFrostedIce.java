package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockFrostedIce extends BlockIce {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_0_3;

   public BlockFrostedIce(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(AGE, Integer.valueOf(0)));
   }

   public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      if ((random.nextInt(3) == 0 || this.func_196456_a(worldIn, pos, 4)) && worldIn.getLight(pos) > 11 - state.get(AGE) - state.getOpacity(worldIn, pos) && this.func_196455_e(state, worldIn, pos)) {
         try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
            for(EnumFacing enumfacing : EnumFacing.values()) {
               blockpos$pooledmutableblockpos.setPos(pos).move(enumfacing);
               IBlockState iblockstate = worldIn.getBlockState(blockpos$pooledmutableblockpos);
               if (iblockstate.getBlock() == this && !this.func_196455_e(iblockstate, worldIn, blockpos$pooledmutableblockpos)) {
                  worldIn.getPendingBlockTicks().scheduleTick(blockpos$pooledmutableblockpos, this, MathHelper.nextInt(random, 20, 40));
               }
            }
         }

      } else {
         worldIn.getPendingBlockTicks().scheduleTick(pos, this, MathHelper.nextInt(random, 20, 40));
      }
   }

   private boolean func_196455_e(IBlockState p_196455_1_, World p_196455_2_, BlockPos p_196455_3_) {
      int i = p_196455_1_.get(AGE);
      if (i < 3) {
         p_196455_2_.setBlockState(p_196455_3_, p_196455_1_.with(AGE, Integer.valueOf(i + 1)), 2);
         return false;
      } else {
         this.turnIntoWater(p_196455_1_, p_196455_2_, p_196455_3_);
         return true;
      }
   }

   /**
    * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
    * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
    * block, etc.
    */
   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (blockIn == this && this.func_196456_a(worldIn, pos, 2)) {
         this.turnIntoWater(state, worldIn, pos);
      }

      super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
   }

   private boolean func_196456_a(IBlockReader p_196456_1_, BlockPos p_196456_2_, int p_196456_3_) {
      int i = 0;

      try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
         for(EnumFacing enumfacing : EnumFacing.values()) {
            blockpos$pooledmutableblockpos.setPos(p_196456_2_).move(enumfacing);
            if (p_196456_1_.getBlockState(blockpos$pooledmutableblockpos).getBlock() == this) {
               ++i;
               if (i >= p_196456_3_) {
                  boolean flag = false;
                  return flag;
               }
            }
         }

         return true;
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(AGE);
   }

   public ItemStack getItem(IBlockReader worldIn, BlockPos pos, IBlockState state) {
      return ItemStack.EMPTY;
   }
}