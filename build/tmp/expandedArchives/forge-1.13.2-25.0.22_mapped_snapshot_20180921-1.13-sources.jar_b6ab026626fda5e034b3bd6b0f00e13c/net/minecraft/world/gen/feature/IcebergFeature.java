package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class IcebergFeature extends Feature<IcebergConfig> {
   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, IcebergConfig p_212245_5_) {
      p_212245_4_ = new BlockPos(p_212245_4_.getX(), p_212245_1_.getSeaLevel(), p_212245_4_.getZ());
      boolean flag = p_212245_3_.nextDouble() > 0.7D;
      IBlockState iblockstate = p_212245_5_.state;
      double d0 = p_212245_3_.nextDouble() * 2.0D * Math.PI;
      int i = 11 - p_212245_3_.nextInt(5);
      int j = 3 + p_212245_3_.nextInt(3);
      boolean flag1 = p_212245_3_.nextDouble() > 0.7D;
      int k = 11;
      int l = flag1 ? p_212245_3_.nextInt(6) + 6 : p_212245_3_.nextInt(15) + 3;
      if (!flag1 && p_212245_3_.nextDouble() > 0.9D) {
         l += p_212245_3_.nextInt(19) + 7;
      }

      int i1 = Math.min(l + p_212245_3_.nextInt(11), 18);
      int j1 = Math.min(l + p_212245_3_.nextInt(7) - p_212245_3_.nextInt(5), 11);
      int k1 = flag1 ? i : 11;

      for(int l1 = -k1; l1 < k1; ++l1) {
         for(int i2 = -k1; i2 < k1; ++i2) {
            for(int j2 = 0; j2 < l; ++j2) {
               int k2 = flag1 ? this.func_205178_b(j2, l, j1) : this.func_205183_a(p_212245_3_, j2, l, j1);
               if (flag1 || l1 < k2) {
                  this.func_205181_a(p_212245_1_, p_212245_3_, p_212245_4_, l, l1, j2, i2, k2, k1, flag1, j, d0, flag, iblockstate);
               }
            }
         }
      }

      this.func_205186_a(p_212245_1_, p_212245_4_, j1, l, flag1, i);

      for(int i3 = -k1; i3 < k1; ++i3) {
         for(int j3 = -k1; j3 < k1; ++j3) {
            for(int k3 = -1; k3 > -i1; --k3) {
               int l3 = flag1 ? MathHelper.ceil((float)k1 * (1.0F - (float)Math.pow((double)k3, 2.0D) / ((float)i1 * 8.0F))) : k1;
               int l2 = this.func_205187_b(p_212245_3_, -k3, i1, j1);
               if (i3 < l2) {
                  this.func_205181_a(p_212245_1_, p_212245_3_, p_212245_4_, i1, i3, k3, j3, l2, l3, flag1, j, d0, flag, iblockstate);
               }
            }
         }
      }

      boolean flag2 = flag1 ? p_212245_3_.nextDouble() > 0.1D : p_212245_3_.nextDouble() > 0.7D;
      if (flag2) {
         this.func_205184_a(p_212245_3_, p_212245_1_, j1, l, p_212245_4_, flag1, i, d0, j);
      }

      return true;
   }

   private void func_205184_a(Random p_205184_1_, IWorld p_205184_2_, int p_205184_3_, int p_205184_4_, BlockPos p_205184_5_, boolean p_205184_6_, int p_205184_7_, double p_205184_8_, int p_205184_10_) {
      int i = p_205184_1_.nextBoolean() ? -1 : 1;
      int j = p_205184_1_.nextBoolean() ? -1 : 1;
      int k = p_205184_1_.nextInt(Math.max(p_205184_3_ / 2 - 2, 1));
      if (p_205184_1_.nextBoolean()) {
         k = p_205184_3_ / 2 + 1 - p_205184_1_.nextInt(Math.max(p_205184_3_ - p_205184_3_ / 2 - 1, 1));
      }

      int l = p_205184_1_.nextInt(Math.max(p_205184_3_ / 2 - 2, 1));
      if (p_205184_1_.nextBoolean()) {
         l = p_205184_3_ / 2 + 1 - p_205184_1_.nextInt(Math.max(p_205184_3_ - p_205184_3_ / 2 - 1, 1));
      }

      if (p_205184_6_) {
         k = l = p_205184_1_.nextInt(Math.max(p_205184_7_ - 5, 1));
      }

      BlockPos blockpos = (new BlockPos(0, 0, 0)).add(i * k, 0, j * l);
      double d0 = p_205184_6_ ? p_205184_8_ + (Math.PI / 2D) : p_205184_1_.nextDouble() * 2.0D * Math.PI;

      for(int i1 = 0; i1 < p_205184_4_ - 3; ++i1) {
         int j1 = this.func_205183_a(p_205184_1_, i1, p_205184_4_, p_205184_3_);
         this.func_205174_a(j1, i1, p_205184_5_, p_205184_2_, false, d0, blockpos, p_205184_7_, p_205184_10_);
      }

      for(int k1 = -1; k1 > -p_205184_4_ + p_205184_1_.nextInt(5); --k1) {
         int l1 = this.func_205187_b(p_205184_1_, -k1, p_205184_4_, p_205184_3_);
         this.func_205174_a(l1, k1, p_205184_5_, p_205184_2_, true, d0, blockpos, p_205184_7_, p_205184_10_);
      }

   }

   private void func_205174_a(int p_205174_1_, int p_205174_2_, BlockPos p_205174_3_, IWorld p_205174_4_, boolean p_205174_5_, double p_205174_6_, BlockPos p_205174_8_, int p_205174_9_, int p_205174_10_) {
      int i = p_205174_1_ + 1 + p_205174_9_ / 3;
      int j = Math.min(p_205174_1_ - 3, 3) + p_205174_10_ / 2 - 1;

      for(int k = -i; k < i; ++k) {
         for(int l = -i; l < i; ++l) {
            double d0 = this.func_205180_a(k, l, p_205174_8_, i, j, p_205174_6_);
            if (d0 < 0.0D) {
               BlockPos blockpos = p_205174_3_.add(k, p_205174_2_, l);
               Block block = p_205174_4_.getBlockState(blockpos).getBlock();
               if (this.isIce(block) || block == Blocks.SNOW_BLOCK) {
                  if (p_205174_5_) {
                     this.setBlockState(p_205174_4_, blockpos, Blocks.WATER.getDefaultState());
                  } else {
                     this.setBlockState(p_205174_4_, blockpos, Blocks.AIR.getDefaultState());
                     this.removeSnowLayer(p_205174_4_, blockpos);
                  }
               }
            }
         }
      }

   }

   private void removeSnowLayer(IWorld p_205185_1_, BlockPos p_205185_2_) {
      if (p_205185_1_.getBlockState(p_205185_2_.up()).getBlock() == Blocks.SNOW) {
         this.setBlockState(p_205185_1_, p_205185_2_.up(), Blocks.AIR.getDefaultState());
      }

   }

   private void func_205181_a(IWorld p_205181_1_, Random p_205181_2_, BlockPos p_205181_3_, int p_205181_4_, int p_205181_5_, int p_205181_6_, int p_205181_7_, int p_205181_8_, int p_205181_9_, boolean p_205181_10_, int p_205181_11_, double p_205181_12_, boolean p_205181_14_, IBlockState p_205181_15_) {
      BlockPos blockpos = new BlockPos(0, 0, 0);
      double d0 = p_205181_10_ ? this.func_205180_a(p_205181_5_, p_205181_7_, blockpos, p_205181_9_, this.func_205176_a(p_205181_6_, p_205181_4_, p_205181_11_), p_205181_12_) : this.func_205177_a(p_205181_5_, p_205181_7_, blockpos, p_205181_8_, p_205181_2_);
      if (d0 < 0.0D) {
         BlockPos blockpos1 = p_205181_3_.add(p_205181_5_, p_205181_6_, p_205181_7_);
         double d1 = p_205181_10_ ? -0.5D : (double)(-6 - p_205181_2_.nextInt(3));
         if (d0 > d1 && p_205181_2_.nextDouble() > 0.9D) {
            return;
         }

         this.func_205175_a(blockpos1, p_205181_1_, p_205181_2_, p_205181_4_ - p_205181_6_, p_205181_4_, p_205181_10_, p_205181_14_, p_205181_15_);
      }

   }

   private void func_205175_a(BlockPos p_205175_1_, IWorld p_205175_2_, Random p_205175_3_, int p_205175_4_, int p_205175_5_, boolean p_205175_6_, boolean p_205175_7_, IBlockState p_205175_8_) {
      IBlockState iblockstate = p_205175_2_.getBlockState(p_205175_1_);
      Block block = iblockstate.getBlock();
      if (iblockstate.getMaterial() == Material.AIR || block == Blocks.SNOW_BLOCK || block == Blocks.ICE || block == Blocks.WATER) {
         boolean flag = !p_205175_6_ || p_205175_3_.nextDouble() > 0.05D;
         int i = p_205175_6_ ? 3 : 2;
         if (p_205175_7_ && block != Blocks.WATER && (double)p_205175_4_ <= (double)p_205175_3_.nextInt(Math.max(1, p_205175_5_ / i)) + (double)p_205175_5_ * 0.6D && flag) {
            this.setBlockState(p_205175_2_, p_205175_1_, Blocks.SNOW_BLOCK.getDefaultState());
         } else {
            this.setBlockState(p_205175_2_, p_205175_1_, p_205175_8_);
         }
      }

   }

   private int func_205176_a(int p_205176_1_, int p_205176_2_, int p_205176_3_) {
      int i = p_205176_3_;
      if (p_205176_1_ > 0 && p_205176_2_ - p_205176_1_ <= 3) {
         i = p_205176_3_ - (4 - (p_205176_2_ - p_205176_1_));
      }

      return i;
   }

   private double func_205177_a(int p_205177_1_, int p_205177_2_, BlockPos p_205177_3_, int p_205177_4_, Random p_205177_5_) {
      float f = 10.0F * MathHelper.clamp(p_205177_5_.nextFloat(), 0.2F, 0.8F) / (float)p_205177_4_;
      return (double)f + Math.pow((double)(p_205177_1_ - p_205177_3_.getX()), 2.0D) + Math.pow((double)(p_205177_2_ - p_205177_3_.getZ()), 2.0D) - Math.pow((double)p_205177_4_, 2.0D);
   }

   private double func_205180_a(int p_205180_1_, int p_205180_2_, BlockPos p_205180_3_, int p_205180_4_, int p_205180_5_, double p_205180_6_) {
      return Math.pow(((double)(p_205180_1_ - p_205180_3_.getX()) * Math.cos(p_205180_6_) - (double)(p_205180_2_ - p_205180_3_.getZ()) * Math.sin(p_205180_6_)) / (double)p_205180_4_, 2.0D) + Math.pow(((double)(p_205180_1_ - p_205180_3_.getX()) * Math.sin(p_205180_6_) + (double)(p_205180_2_ - p_205180_3_.getZ()) * Math.cos(p_205180_6_)) / (double)p_205180_5_, 2.0D) - 1.0D;
   }

   private int func_205183_a(Random p_205183_1_, int p_205183_2_, int p_205183_3_, int p_205183_4_) {
      float f = 3.5F - p_205183_1_.nextFloat();
      float f1 = (1.0F - (float)Math.pow((double)p_205183_2_, 2.0D) / ((float)p_205183_3_ * f)) * (float)p_205183_4_;
      if (p_205183_3_ > 15 + p_205183_1_.nextInt(5)) {
         int i = p_205183_2_ < 3 + p_205183_1_.nextInt(6) ? p_205183_2_ / 2 : p_205183_2_;
         f1 = (1.0F - (float)i / ((float)p_205183_3_ * f * 0.4F)) * (float)p_205183_4_;
      }

      return MathHelper.ceil(f1 / 2.0F);
   }

   private int func_205178_b(int p_205178_1_, int p_205178_2_, int p_205178_3_) {
      float f = 1.0F;
      float f1 = (1.0F - (float)Math.pow((double)p_205178_1_, 2.0D) / ((float)p_205178_2_ * 1.0F)) * (float)p_205178_3_;
      return MathHelper.ceil(f1 / 2.0F);
   }

   private int func_205187_b(Random p_205187_1_, int p_205187_2_, int p_205187_3_, int p_205187_4_) {
      float f = 1.0F + p_205187_1_.nextFloat() / 2.0F;
      float f1 = (1.0F - (float)p_205187_2_ / ((float)p_205187_3_ * f)) * (float)p_205187_4_;
      return MathHelper.ceil(f1 / 2.0F);
   }

   private boolean isIce(Block p_205179_1_) {
      return p_205179_1_ == Blocks.PACKED_ICE || p_205179_1_ == Blocks.SNOW_BLOCK || p_205179_1_ == Blocks.BLUE_ICE;
   }

   private boolean func_205182_b(IBlockReader p_205182_1_, BlockPos p_205182_2_) {
      return p_205182_1_.getBlockState(p_205182_2_.down()).getMaterial() == Material.AIR;
   }

   private void func_205186_a(IWorld p_205186_1_, BlockPos p_205186_2_, int p_205186_3_, int p_205186_4_, boolean p_205186_5_, int p_205186_6_) {
      int i = p_205186_5_ ? p_205186_6_ : p_205186_3_ / 2;

      for(int j = -i; j <= i; ++j) {
         for(int k = -i; k <= i; ++k) {
            for(int l = 0; l <= p_205186_4_; ++l) {
               BlockPos blockpos = p_205186_2_.add(j, l, k);
               Block block = p_205186_1_.getBlockState(blockpos).getBlock();
               if (this.isIce(block) || block == Blocks.SNOW) {
                  if (this.func_205182_b(p_205186_1_, blockpos)) {
                     this.setBlockState(p_205186_1_, blockpos, Blocks.AIR.getDefaultState());
                     this.setBlockState(p_205186_1_, blockpos.up(), Blocks.AIR.getDefaultState());
                  } else if (this.isIce(block)) {
                     Block[] ablock = new Block[]{p_205186_1_.getBlockState(blockpos.west()).getBlock(), p_205186_1_.getBlockState(blockpos.east()).getBlock(), p_205186_1_.getBlockState(blockpos.north()).getBlock(), p_205186_1_.getBlockState(blockpos.south()).getBlock()};
                     int i1 = 0;

                     for(Block block1 : ablock) {
                        if (!this.isIce(block1)) {
                           ++i1;
                        }
                     }

                     if (i1 >= 3) {
                        this.setBlockState(p_205186_1_, blockpos, Blocks.AIR.getDefaultState());
                     }
                  }
               }
            }
         }
      }

   }
}