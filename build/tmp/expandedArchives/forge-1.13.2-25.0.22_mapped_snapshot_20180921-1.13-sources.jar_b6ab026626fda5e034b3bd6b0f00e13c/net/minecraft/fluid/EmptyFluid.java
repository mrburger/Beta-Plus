package net.minecraft.fluid;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorldReaderBase;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EmptyFluid extends Fluid {
   /**
    * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
    * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
    */
   @OnlyIn(Dist.CLIENT)
   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.SOLID;
   }

   public Item getFilledBucket() {
      return Items.AIR;
   }

   public boolean canOtherFlowInto(IFluidState state, Fluid fluidIn, EnumFacing direction) {
      return true;
   }

   public Vec3d getFlow(IWorldReaderBase worldIn, BlockPos pos, IFluidState state) {
      return Vec3d.ZERO;
   }

   public int getTickRate(IWorldReaderBase p_205569_1_) {
      return 0;
   }

   protected boolean isEmpty() {
      return true;
   }

   protected float getExplosionResistance() {
      return 0.0F;
   }

   public float getHeight(IFluidState state) {
      return 0.0F;
   }

   protected IBlockState getBlockState(IFluidState state) {
      return Blocks.AIR.getDefaultState();
   }

   public boolean isSource(IFluidState state) {
      return false;
   }

   public int getLevel(IFluidState p_207192_1_) {
      return 0;
   }
}