package net.minecraft.fluid;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockFlowingFluid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.particles.IParticleData;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class WaterFluid extends FlowingFluid {
   public Fluid getFlowingFluid() {
      return Fluids.FLOWING_WATER;
   }

   public Fluid getStillFluid() {
      return Fluids.WATER;
   }

   /**
    * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
    * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
    */
   @OnlyIn(Dist.CLIENT)
   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.TRANSLUCENT;
   }

   public Item getFilledBucket() {
      return Items.WATER_BUCKET;
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(World worldIn, BlockPos pos, IFluidState state, Random random) {
      if (!state.isSource() && !state.get(FALLING)) {
         if (random.nextInt(64) == 0) {
            worldIn.playSound((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false);
         }
      } else if (random.nextInt(10) == 0) {
         worldIn.spawnParticle(Particles.UNDERWATER, (double)((float)pos.getX() + random.nextFloat()), (double)((float)pos.getY() + random.nextFloat()), (double)((float)pos.getZ() + random.nextFloat()), 0.0D, 0.0D, 0.0D);
      }

   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public IParticleData getDripParticleData() {
      return Particles.DRIPPING_WATER;
   }

   protected boolean canSourcesMultiply() {
      return true;
   }

   protected void beforeReplacingBlock(IWorld worldIn, BlockPos pos, IBlockState state) {
      state.dropBlockAsItem(worldIn.getWorld(), pos, 0);
   }

   public int getSlopeFindDistance(IWorldReaderBase worldIn) {
      return 4;
   }

   public IBlockState getBlockState(IFluidState state) {
      return Blocks.WATER.getDefaultState().with(BlockFlowingFluid.LEVEL, Integer.valueOf(getLevelFromState(state)));
   }

   public boolean isEquivalentTo(Fluid fluidIn) {
      return fluidIn == Fluids.WATER || fluidIn == Fluids.FLOWING_WATER;
   }

   public int getLevelDecreasePerBlock(IWorldReaderBase worldIn) {
      return 1;
   }

   public int getTickRate(IWorldReaderBase p_205569_1_) {
      return 5;
   }

   public boolean canOtherFlowInto(IFluidState state, Fluid fluidIn, EnumFacing direction) {
      return direction == EnumFacing.DOWN && !fluidIn.isIn(FluidTags.WATER);
   }

   protected float getExplosionResistance() {
      return 100.0F;
   }

   public static class Flowing extends WaterFluid {
      protected void fillStateContainer(StateContainer.Builder<Fluid, IFluidState> builder) {
         super.fillStateContainer(builder);
         builder.add(LEVEL_1_TO_8);
      }

      public int getLevel(IFluidState p_207192_1_) {
         return p_207192_1_.get(LEVEL_1_TO_8);
      }

      public boolean isSource(IFluidState state) {
         return false;
      }
   }

   public static class Source extends WaterFluid {
      public int getLevel(IFluidState p_207192_1_) {
         return 8;
      }

      public boolean isSource(IFluidState state) {
         return true;
      }
   }
}