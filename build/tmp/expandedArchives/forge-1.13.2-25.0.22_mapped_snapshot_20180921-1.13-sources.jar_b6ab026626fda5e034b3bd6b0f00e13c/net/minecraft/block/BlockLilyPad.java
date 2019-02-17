package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockLilyPad extends BlockBush {
   protected static final VoxelShape LILY_PAD_AABB = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 1.5D, 15.0D);

   protected BlockLilyPad(Block.Properties builder) {
      super(builder);
   }

   public void onEntityCollision(IBlockState state, World worldIn, BlockPos pos, Entity entityIn) {
      super.onEntityCollision(state, worldIn, pos, entityIn);
      if (entityIn instanceof EntityBoat) {
         worldIn.destroyBlock(new BlockPos(pos), true);
      }

   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return LILY_PAD_AABB;
   }

   protected boolean isValidGround(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      IFluidState ifluidstate = worldIn.getFluidState(pos);
      return ifluidstate.getFluid() == Fluids.WATER || state.getMaterial() == Material.ICE;
   }
}