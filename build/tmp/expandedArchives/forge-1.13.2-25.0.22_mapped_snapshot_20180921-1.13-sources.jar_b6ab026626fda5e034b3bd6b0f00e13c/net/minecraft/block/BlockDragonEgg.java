package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Particles;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockDragonEgg extends BlockFalling {
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

   public BlockDragonEgg(Block.Properties builder) {
      super(builder);
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return SHAPE;
   }

   public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      this.teleport(state, worldIn, pos);
      return true;
   }

   public void onBlockClicked(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player) {
      this.teleport(state, worldIn, pos);
   }

   private void teleport(IBlockState p_196443_1_, World p_196443_2_, BlockPos p_196443_3_) {
      for(int i = 0; i < 1000; ++i) {
         BlockPos blockpos = p_196443_3_.add(p_196443_2_.rand.nextInt(16) - p_196443_2_.rand.nextInt(16), p_196443_2_.rand.nextInt(8) - p_196443_2_.rand.nextInt(8), p_196443_2_.rand.nextInt(16) - p_196443_2_.rand.nextInt(16));
         if (p_196443_2_.getBlockState(blockpos).isAir()) {
            if (p_196443_2_.isRemote) {
               for(int j = 0; j < 128; ++j) {
                  double d0 = p_196443_2_.rand.nextDouble();
                  float f = (p_196443_2_.rand.nextFloat() - 0.5F) * 0.2F;
                  float f1 = (p_196443_2_.rand.nextFloat() - 0.5F) * 0.2F;
                  float f2 = (p_196443_2_.rand.nextFloat() - 0.5F) * 0.2F;
                  double d1 = (double)blockpos.getX() + (double)(p_196443_3_.getX() - blockpos.getX()) * d0 + (p_196443_2_.rand.nextDouble() - 0.5D) + 0.5D;
                  double d2 = (double)blockpos.getY() + (double)(p_196443_3_.getY() - blockpos.getY()) * d0 + p_196443_2_.rand.nextDouble() - 0.5D;
                  double d3 = (double)blockpos.getZ() + (double)(p_196443_3_.getZ() - blockpos.getZ()) * d0 + (p_196443_2_.rand.nextDouble() - 0.5D) + 0.5D;
                  p_196443_2_.spawnParticle(Particles.PORTAL, d1, d2, d3, (double)f, (double)f1, (double)f2);
               }
            } else {
               p_196443_2_.setBlockState(blockpos, p_196443_1_, 2);
               p_196443_2_.removeBlock(p_196443_3_);
            }

            return;
         }
      }

   }

   /**
    * How many world ticks before ticking
    */
   public int tickRate(IWorldReaderBase worldIn) {
      return 5;
   }

   /**
    * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
    */
   public boolean isFullCube(IBlockState state) {
      return false;
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
      return BlockFaceShape.UNDEFINED;
   }

   public boolean allowsMovement(IBlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }
}