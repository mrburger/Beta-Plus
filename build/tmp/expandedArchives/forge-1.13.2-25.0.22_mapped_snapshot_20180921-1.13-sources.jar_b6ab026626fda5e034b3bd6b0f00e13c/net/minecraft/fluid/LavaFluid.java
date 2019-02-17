package net.minecraft.fluid;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockFlowingFluid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
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

public abstract class LavaFluid extends FlowingFluid {
   public Fluid getFlowingFluid() {
      return Fluids.FLOWING_LAVA;
   }

   public Fluid getStillFluid() {
      return Fluids.LAVA;
   }

   /**
    * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
    * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
    */
   @OnlyIn(Dist.CLIENT)
   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.SOLID;
   }

   public Item getFilledBucket() {
      return Items.LAVA_BUCKET;
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(World worldIn, BlockPos pos, IFluidState state, Random random) {
      BlockPos blockpos = pos.up();
      if (worldIn.getBlockState(blockpos).isAir() && !worldIn.getBlockState(blockpos).isOpaqueCube(worldIn, blockpos)) {
         if (random.nextInt(100) == 0) {
            double d0 = (double)((float)pos.getX() + random.nextFloat());
            double d1 = (double)(pos.getY() + 1);
            double d2 = (double)((float)pos.getZ() + random.nextFloat());
            worldIn.spawnParticle(Particles.LAVA, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            worldIn.playSound(d0, d1, d2, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
         }

         if (random.nextInt(200) == 0) {
            worldIn.playSound((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), SoundEvents.BLOCK_LAVA_AMBIENT, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
         }
      }

   }

   public void randomTick(World p_207186_1_, BlockPos pos, IFluidState state, Random random) {
      if (p_207186_1_.getGameRules().getBoolean("doFireTick")) {
         int i = random.nextInt(3);
         if (i > 0) {
            BlockPos blockpos = pos;

            for(int j = 0; j < i; ++j) {
               blockpos = blockpos.add(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);
               if (!p_207186_1_.isBlockPresent(blockpos)) {
                  return;
               }

               IBlockState iblockstate = p_207186_1_.getBlockState(blockpos);
               if (iblockstate.isAir()) {
                  if (this.isSurroundingBlockFlammable(p_207186_1_, blockpos)) {
                     p_207186_1_.setBlockState(blockpos, Blocks.FIRE.getDefaultState());
                     return;
                  }
               } else if (iblockstate.getMaterial().blocksMovement()) {
                  return;
               }
            }
         } else {
            for(int k = 0; k < 3; ++k) {
               BlockPos blockpos1 = pos.add(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
               if (!p_207186_1_.isBlockPresent(blockpos1)) {
                  return;
               }

               if (p_207186_1_.isAirBlock(blockpos1.up()) && this.getCanBlockBurn(p_207186_1_, blockpos1)) {
                  p_207186_1_.setBlockState(blockpos1.up(), Blocks.FIRE.getDefaultState());
               }
            }
         }

      }
   }

   private boolean isSurroundingBlockFlammable(IWorldReaderBase worldIn, BlockPos pos) {
      for(EnumFacing enumfacing : EnumFacing.values()) {
         if (this.getCanBlockBurn(worldIn, pos.offset(enumfacing))) {
            return true;
         }
      }

      return false;
   }

   private boolean getCanBlockBurn(IWorldReaderBase worldIn, BlockPos pos) {
      return pos.getY() >= 0 && pos.getY() < 256 && !worldIn.isBlockLoaded(pos) ? false : worldIn.getBlockState(pos).getMaterial().isFlammable();
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public IParticleData getDripParticleData() {
      return Particles.DRIPPING_LAVA;
   }

   protected void beforeReplacingBlock(IWorld worldIn, BlockPos pos, IBlockState state) {
      this.triggerEffects(worldIn, pos);
   }

   public int getSlopeFindDistance(IWorldReaderBase worldIn) {
      return worldIn.getDimension().doesWaterVaporize() ? 4 : 2;
   }

   public IBlockState getBlockState(IFluidState state) {
      return Blocks.LAVA.getDefaultState().with(BlockFlowingFluid.LEVEL, Integer.valueOf(getLevelFromState(state)));
   }

   public boolean isEquivalentTo(Fluid fluidIn) {
      return fluidIn == Fluids.LAVA || fluidIn == Fluids.FLOWING_LAVA;
   }

   public int getLevelDecreasePerBlock(IWorldReaderBase worldIn) {
      return worldIn.getDimension().doesWaterVaporize() ? 1 : 2;
   }

   public boolean canOtherFlowInto(IFluidState state, Fluid fluidIn, EnumFacing direction) {
      return state.getHeight() >= 0.44444445F && fluidIn.isIn(FluidTags.WATER);
   }

   public int getTickRate(IWorldReaderBase p_205569_1_) {
      return p_205569_1_.getDimension().isNether() ? 10 : 30;
   }

   public int getTickRate(World worldIn, IFluidState p_205578_2_, IFluidState p_205578_3_) {
      int i = this.getTickRate(worldIn);
      if (!p_205578_2_.isEmpty() && !p_205578_3_.isEmpty() && !p_205578_2_.get(FALLING) && !p_205578_3_.get(FALLING) && p_205578_3_.getHeight() > p_205578_2_.getHeight() && worldIn.getRandom().nextInt(4) != 0) {
         i *= 4;
      }

      return i;
   }

   protected void triggerEffects(IWorld p_205581_1_, BlockPos p_205581_2_) {
      double d0 = (double)p_205581_2_.getX();
      double d1 = (double)p_205581_2_.getY();
      double d2 = (double)p_205581_2_.getZ();
      p_205581_1_.playSound((EntityPlayer)null, p_205581_2_, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (p_205581_1_.getRandom().nextFloat() - p_205581_1_.getRandom().nextFloat()) * 0.8F);

      for(int i = 0; i < 8; ++i) {
         p_205581_1_.spawnParticle(Particles.LARGE_SMOKE, d0 + Math.random(), d1 + 1.2D, d2 + Math.random(), 0.0D, 0.0D, 0.0D);
      }

   }

   protected boolean canSourcesMultiply() {
      return false;
   }

   protected void flowInto(IWorld worldIn, BlockPos pos, IBlockState blockStateIn, EnumFacing direction, IFluidState fluidStateIn) {
      if (direction == EnumFacing.DOWN) {
         IFluidState ifluidstate = worldIn.getFluidState(pos);
         if (this.isIn(FluidTags.LAVA) && ifluidstate.isTagged(FluidTags.WATER)) {
            if (blockStateIn.getBlock() instanceof BlockFlowingFluid) {
               worldIn.setBlockState(pos, Blocks.STONE.getDefaultState(), 3);
            }

            this.triggerEffects(worldIn, pos);
            return;
         }
      }

      super.flowInto(worldIn, pos, blockStateIn, direction, fluidStateIn);
   }

   protected boolean getTickRandomly() {
      return true;
   }

   protected float getExplosionResistance() {
      return 100.0F;
   }

   public static class Flowing extends LavaFluid {
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

   public static class Source extends LavaFluid {
      public int getLevel(IFluidState p_207192_1_) {
         return 8;
      }

      public boolean isSource(IFluidState state) {
         return true;
      }
   }
}