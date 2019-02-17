package net.minecraft.client.renderer.chunk;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderChunkCache implements IWorldReader {
   protected final int field_212400_a;
   protected final int field_212401_b;
   protected final BlockPos field_212402_c;
   protected final int field_212403_d;
   protected final int field_212404_e;
   protected final int field_212405_f;
   protected final Chunk[][] field_212406_g;
   protected final RenderChunkCache.Entry[] field_212407_h;
   protected final World field_212408_i;

   @Nullable
   public static RenderChunkCache func_212397_a(World p_212397_0_, BlockPos p_212397_1_, BlockPos p_212397_2_, int p_212397_3_) {
      int i = p_212397_1_.getX() - p_212397_3_ >> 4;
      int j = p_212397_1_.getZ() - p_212397_3_ >> 4;
      int k = p_212397_2_.getX() + p_212397_3_ >> 4;
      int l = p_212397_2_.getZ() + p_212397_3_ >> 4;
      Chunk[][] achunk = new Chunk[k - i + 1][l - j + 1];

      for(int i1 = i; i1 <= k; ++i1) {
         for(int j1 = j; j1 <= l; ++j1) {
            achunk[i1 - i][j1 - j] = p_212397_0_.getChunk(i1, j1);
         }
      }

      boolean flag = true;

      for(int l1 = p_212397_1_.getX() >> 4; l1 <= p_212397_2_.getX() >> 4; ++l1) {
         for(int k1 = p_212397_1_.getZ() >> 4; k1 <= p_212397_2_.getZ() >> 4; ++k1) {
            Chunk chunk = achunk[l1 - i][k1 - j];
            if (!chunk.isEmptyBetween(p_212397_1_.getY(), p_212397_2_.getY())) {
               flag = false;
            }
         }
      }

      if (flag) {
         return null;
      } else {
         int i2 = 1;
         BlockPos blockpos = p_212397_1_.add(-1, -1, -1);
         BlockPos blockpos1 = p_212397_2_.add(1, 1, 1);
         return new RenderChunkCache(p_212397_0_, i, j, achunk, blockpos, blockpos1);
      }
   }

   public RenderChunkCache(World p_i49840_1_, int p_i49840_2_, int p_i49840_3_, Chunk[][] p_i49840_4_, BlockPos p_i49840_5_, BlockPos p_i49840_6_) {
      this.field_212408_i = p_i49840_1_;
      this.field_212400_a = p_i49840_2_;
      this.field_212401_b = p_i49840_3_;
      this.field_212406_g = p_i49840_4_;
      this.field_212402_c = p_i49840_5_;
      this.field_212403_d = p_i49840_6_.getX() - p_i49840_5_.getX() + 1;
      this.field_212404_e = p_i49840_6_.getY() - p_i49840_5_.getY() + 1;
      this.field_212405_f = p_i49840_6_.getZ() - p_i49840_5_.getZ() + 1;
      this.field_212407_h = new RenderChunkCache.Entry[this.field_212403_d * this.field_212404_e * this.field_212405_f];

      for(BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(p_i49840_5_, p_i49840_6_)) {
         this.field_212407_h[this.func_212398_a(blockpos$mutableblockpos)] = new RenderChunkCache.Entry(p_i49840_1_, blockpos$mutableblockpos);
      }

   }

   protected int func_212398_a(BlockPos p_212398_1_) {
      int i = p_212398_1_.getX() - this.field_212402_c.getX();
      int j = p_212398_1_.getY() - this.field_212402_c.getY();
      int k = p_212398_1_.getZ() - this.field_212402_c.getZ();
      return k * this.field_212403_d * this.field_212404_e + j * this.field_212403_d + i;
   }

   public IBlockState getBlockState(BlockPos pos) {
      return this.field_212407_h[this.func_212398_a(pos)].field_212495_a;
   }

   public IFluidState getFluidState(BlockPos pos) {
      return this.field_212407_h[this.func_212398_a(pos)].field_212496_b;
   }

   public Biome getBiome(BlockPos pos) {
      int i = (pos.getX() >> 4) - this.field_212400_a;
      int j = (pos.getZ() >> 4) - this.field_212401_b;
      return this.field_212406_g[i][j].getBiome(pos);
   }

   private int func_212396_b(EnumLightType p_212396_1_, BlockPos p_212396_2_) {
      return this.field_212407_h[this.func_212398_a(p_212396_2_)].func_212493_a(p_212396_1_, p_212396_2_);
   }

   public int getCombinedLight(BlockPos pos, int lightValue) {
      int i = this.func_212396_b(EnumLightType.SKY, pos);
      int j = this.func_212396_b(EnumLightType.BLOCK, pos);
      if (j < lightValue) {
         j = lightValue;
      }

      return i << 20 | j << 4;
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos pos) {
      return this.func_212399_a(pos, Chunk.EnumCreateEntityType.IMMEDIATE);
   }

   @Nullable
   public TileEntity func_212399_a(BlockPos p_212399_1_, Chunk.EnumCreateEntityType p_212399_2_) {
      int i = (p_212399_1_.getX() >> 4) - this.field_212400_a;
      int j = (p_212399_1_.getZ() >> 4) - this.field_212401_b;
      return this.field_212406_g[i][j].getTileEntity(p_212399_1_, p_212399_2_);
   }

   public float getBrightness(BlockPos pos) {
      return this.field_212408_i.dimension.getLightBrightnessTable()[this.getLight(pos)];
   }

   public int getNeighborAwareLightSubtracted(BlockPos pos, int amount) {
      if (this.getBlockState(pos).useNeighborBrightness(this, pos)) {
         int i = 0;

         for(EnumFacing enumfacing : EnumFacing.values()) {
            int j = this.getLightSubtracted(pos.offset(enumfacing), amount);
            if (j > i) {
               i = j;
            }

            if (i >= 15) {
               return i;
            }
         }

         return i;
      } else {
         return this.getLightSubtracted(pos, amount);
      }
   }

   public Dimension getDimension() {
      return this.field_212408_i.getDimension();
   }

   public int getLightSubtracted(BlockPos pos, int amount) {
      if (pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() <= 30000000) {
         if (pos.getY() < 0) {
            return 0;
         } else if (pos.getY() >= 256) {
            int k = 15 - amount;
            if (k < 0) {
               k = 0;
            }

            return k;
         } else {
            int i = (pos.getX() >> 4) - this.field_212400_a;
            int j = (pos.getZ() >> 4) - this.field_212401_b;
            return this.field_212406_g[i][j].getLightSubtracted(pos, amount);
         }
      } else {
         return 15;
      }
   }

   public boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
      return this.func_212395_a(x, z);
   }

   public boolean canSeeSky(BlockPos pos) {
      return false;
   }

   public boolean func_212395_a(int p_212395_1_, int p_212395_2_) {
      int i = p_212395_1_ - this.field_212400_a;
      int j = p_212395_2_ - this.field_212401_b;
      return i >= 0 && i < this.field_212406_g.length && j >= 0 && j < this.field_212406_g[i].length;
   }

   public int getHeight(Heightmap.Type heightmapType, int x, int z) {
      throw new RuntimeException("NOT IMPLEMENTED!");
   }

   public WorldBorder getWorldBorder() {
      return this.field_212408_i.getWorldBorder();
   }

   public boolean checkNoEntityCollision(@Nullable Entity entityIn, VoxelShape shape) {
      throw new RuntimeException("This method should never be called here. No entity logic inside Region");
   }

   @Nullable
   public EntityPlayer getClosestPlayer(double x, double y, double z, double distance, Predicate<Entity> predicate) {
      throw new RuntimeException("This method should never be called here. No entity logic inside Region");
   }

   public int getSkylightSubtracted() {
      return 0;
   }

   /**
    * Checks to see if an air block exists at the provided location. Note that this only checks to see if the blocks
    * material is set to air, meaning it is possible for non-vanilla blocks to still pass this check.
    */
   public boolean isAirBlock(BlockPos pos) {
      return this.getBlockState(pos).isAir();
   }

   public int getLightFor(EnumLightType type, BlockPos pos) {
      if (pos.getY() >= 0 && pos.getY() < 256) {
         int i = (pos.getX() >> 4) - this.field_212400_a;
         int j = (pos.getZ() >> 4) - this.field_212401_b;
         return this.field_212406_g[i][j].getLightFor(type, pos);
      } else {
         return type.defaultLightValue;
      }
   }

   public int getStrongPower(BlockPos pos, EnumFacing direction) {
      return this.getBlockState(pos).getStrongPower(this, pos, direction);
   }

   public boolean isRemote() {
      throw new RuntimeException("Not yet implemented");
   }

   public int getSeaLevel() {
      throw new RuntimeException("Not yet implemented");
   }

   @OnlyIn(Dist.CLIENT)
   public class Entry {
      protected final IBlockState field_212495_a;
      protected final IFluidState field_212496_b;
      private int[] field_212498_d;

      protected Entry(World p_i49805_2_, BlockPos p_i49805_3_) {
         this.field_212495_a = p_i49805_2_.getBlockState(p_i49805_3_);
         this.field_212496_b = p_i49805_2_.getFluidState(p_i49805_3_);
      }

      protected int func_212493_a(EnumLightType p_212493_1_, BlockPos p_212493_2_) {
         if (this.field_212498_d == null) {
            this.func_212492_a(p_212493_2_);
         }

         return this.field_212498_d[p_212493_1_.ordinal()];
      }

      private void func_212492_a(BlockPos p_212492_1_) {
         this.field_212498_d = new int[EnumLightType.values().length];

         for(EnumLightType enumlighttype : EnumLightType.values()) {
            this.field_212498_d[enumlighttype.ordinal()] = this.func_212494_b(enumlighttype, p_212492_1_);
         }

      }

      private int func_212494_b(EnumLightType p_212494_1_, BlockPos p_212494_2_) {
         if (p_212494_1_ == EnumLightType.SKY && !RenderChunkCache.this.field_212408_i.getDimension().hasSkyLight()) {
            return 0;
         } else if (p_212494_2_.getY() >= 0 && p_212494_2_.getY() < 256) {
            if (this.field_212495_a.useNeighborBrightness(RenderChunkCache.this, p_212494_2_)) {
               int l = 0;

               for(EnumFacing enumfacing : EnumFacing.values()) {
                  int k = RenderChunkCache.this.getLightFor(p_212494_1_, p_212494_2_.offset(enumfacing));
                  if (k > l) {
                     l = k;
                  }

                  if (l >= 15) {
                     return l;
                  }
               }

               return l;
            } else {
               int i = (p_212494_2_.getX() >> 4) - RenderChunkCache.this.field_212400_a;
               int j = (p_212494_2_.getZ() >> 4) - RenderChunkCache.this.field_212401_b;
               return RenderChunkCache.this.field_212406_g[i][j].getLightFor(p_212494_1_, p_212494_2_);
            }
         } else {
            return p_212494_1_.defaultLightValue;
         }
      }
   }
}