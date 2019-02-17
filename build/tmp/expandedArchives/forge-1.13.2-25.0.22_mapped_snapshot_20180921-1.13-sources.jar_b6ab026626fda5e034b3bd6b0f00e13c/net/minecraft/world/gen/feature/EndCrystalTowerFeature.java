package net.minecraft.world.gen.feature;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockPane;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class EndCrystalTowerFeature extends Feature<NoFeatureConfig> {
   private boolean crystalInvulnerable;
   private EndCrystalTowerFeature.EndSpike spike;
   private BlockPos beamTarget;

   public void setSpike(EndCrystalTowerFeature.EndSpike p_186143_1_) {
      this.spike = p_186143_1_;
   }

   public void setCrystalInvulnerable(boolean p_186144_1_) {
      this.crystalInvulnerable = p_186144_1_;
   }

   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      if (this.spike == null) {
         throw new IllegalStateException("Decoration requires priming with a spike");
      } else {
         int i = this.spike.getRadius();

         for(BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(new BlockPos(p_212245_4_.getX() - i, 0, p_212245_4_.getZ() - i), new BlockPos(p_212245_4_.getX() + i, this.spike.getHeight() + 10, p_212245_4_.getZ() + i))) {
            if (blockpos$mutableblockpos.distanceSq((double)p_212245_4_.getX(), (double)blockpos$mutableblockpos.getY(), (double)p_212245_4_.getZ()) <= (double)(i * i + 1) && blockpos$mutableblockpos.getY() < this.spike.getHeight()) {
               this.setBlockState(p_212245_1_, blockpos$mutableblockpos, Blocks.OBSIDIAN.getDefaultState());
            } else if (blockpos$mutableblockpos.getY() > 65) {
               this.setBlockState(p_212245_1_, blockpos$mutableblockpos, Blocks.AIR.getDefaultState());
            }
         }

         if (this.spike.isGuarded()) {
            int j1 = -2;
            int k1 = 2;
            int j = 3;
            BlockPos.MutableBlockPos blockpos$mutableblockpos1 = new BlockPos.MutableBlockPos();

            for(int k = -2; k <= 2; ++k) {
               for(int l = -2; l <= 2; ++l) {
                  for(int i1 = 0; i1 <= 3; ++i1) {
                     boolean flag = MathHelper.abs(k) == 2;
                     boolean flag1 = MathHelper.abs(l) == 2;
                     boolean flag2 = i1 == 3;
                     if (flag || flag1 || flag2) {
                        boolean flag3 = k == -2 || k == 2 || flag2;
                        boolean flag4 = l == -2 || l == 2 || flag2;
                        IBlockState iblockstate = Blocks.IRON_BARS.getDefaultState().with(BlockPane.NORTH, Boolean.valueOf(flag3 && l != -2)).with(BlockPane.SOUTH, Boolean.valueOf(flag3 && l != 2)).with(BlockPane.WEST, Boolean.valueOf(flag4 && k != -2)).with(BlockPane.EAST, Boolean.valueOf(flag4 && k != 2));
                        this.setBlockState(p_212245_1_, blockpos$mutableblockpos1.setPos(p_212245_4_.getX() + k, this.spike.getHeight() + i1, p_212245_4_.getZ() + l), iblockstate);
                     }
                  }
               }
            }
         }

         EntityEnderCrystal entityendercrystal = new EntityEnderCrystal(p_212245_1_.getWorld());
         entityendercrystal.setBeamTarget(this.beamTarget);
         entityendercrystal.setInvulnerable(this.crystalInvulnerable);
         entityendercrystal.setLocationAndAngles((double)((float)p_212245_4_.getX() + 0.5F), (double)(this.spike.getHeight() + 1), (double)((float)p_212245_4_.getZ() + 0.5F), p_212245_3_.nextFloat() * 360.0F, 0.0F);
         p_212245_1_.spawnEntity(entityendercrystal);
         this.setBlockState(p_212245_1_, new BlockPos(p_212245_4_.getX(), this.spike.getHeight(), p_212245_4_.getZ()), Blocks.BEDROCK.getDefaultState());
         return true;
      }
   }

   /**
    * Sets the value that will be used in a call to entitycrystal.setBeamTarget.
    * At the moment, WorldGenSpikes.setBeamTarget is only ever called with a value of (0, 128, 0)
    */
   public void setBeamTarget(@Nullable BlockPos pos) {
      this.beamTarget = pos;
   }

   public static class EndSpike {
      private final int centerX;
      private final int centerZ;
      private final int radius;
      private final int height;
      private final boolean guarded;
      private final AxisAlignedBB topBoundingBox;

      public EndSpike(int p_i47020_1_, int p_i47020_2_, int p_i47020_3_, int p_i47020_4_, boolean p_i47020_5_) {
         this.centerX = p_i47020_1_;
         this.centerZ = p_i47020_2_;
         this.radius = p_i47020_3_;
         this.height = p_i47020_4_;
         this.guarded = p_i47020_5_;
         this.topBoundingBox = new AxisAlignedBB((double)(p_i47020_1_ - p_i47020_3_), 0.0D, (double)(p_i47020_2_ - p_i47020_3_), (double)(p_i47020_1_ + p_i47020_3_), 256.0D, (double)(p_i47020_2_ + p_i47020_3_));
      }

      public boolean doesStartInChunk(BlockPos p_186154_1_) {
         int i = this.centerX - this.radius;
         int j = this.centerZ - this.radius;
         return p_186154_1_.getX() == (i & -16) && p_186154_1_.getZ() == (j & -16);
      }

      public int getCenterX() {
         return this.centerX;
      }

      public int getCenterZ() {
         return this.centerZ;
      }

      public int getRadius() {
         return this.radius;
      }

      public int getHeight() {
         return this.height;
      }

      public boolean isGuarded() {
         return this.guarded;
      }

      public AxisAlignedBB getTopBoundingBox() {
         return this.topBoundingBox;
      }
   }
}