package net.minecraft.world.gen;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.AllowsMovementAndSolidMatcher;
import net.minecraft.block.state.pattern.AllowsMovementMatcher;
import net.minecraft.block.state.pattern.BlockMatcherReaderAware;
import net.minecraft.block.state.pattern.BlockTagMatcher;
import net.minecraft.block.state.pattern.IBlockMatcherReaderAware;
import net.minecraft.block.state.pattern.LightEmittingMatcher;
import net.minecraft.block.state.pattern.LiquidBlockMatcher;
import net.minecraft.block.state.pattern.ReaderAwareMatchers;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.BitArray;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;

public class Heightmap {
   private final BitArray data = new BitArray(9, 256);
   private final IBlockMatcherReaderAware<IBlockState> blockMatcher;
   private final IChunk chunk;

   public Heightmap(IChunk chunkIn, Heightmap.Type type) {
      this.blockMatcher = ReaderAwareMatchers.not(ReaderAwareMatchers.or(type.func_202264_a()));
      this.chunk = chunkIn;
   }

   public void generate() {
      int i = this.chunk.getTopFilledSegment() + 16;

      try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
         for(int j = 0; j < 16; ++j) {
            for(int k = 0; k < 16; ++k) {
               this.set(j, k, this.generateColumn(blockpos$pooledmutableblockpos, j, k, this.blockMatcher, i));
            }
         }
      }

   }

   public boolean update(int p_202270_1_, int p_202270_2_, int p_202270_3_, @Nullable IBlockState p_202270_4_) {
      int i = this.getHeight(p_202270_1_, p_202270_3_);
      if (p_202270_2_ <= i - 2) {
         return false;
      } else {
         if (this.blockMatcher.test(p_202270_4_, this.chunk, new BlockPos(p_202270_1_, p_202270_2_, p_202270_3_))) {
            if (p_202270_2_ >= i) {
               this.set(p_202270_1_, p_202270_3_, p_202270_2_ + 1);
               return true;
            }
         } else if (i - 1 == p_202270_2_) {
            this.set(p_202270_1_, p_202270_3_, this.generateColumn((BlockPos.MutableBlockPos)null, p_202270_1_, p_202270_3_, this.blockMatcher, p_202270_2_));
            return true;
         }

         return false;
      }
   }

   private int generateColumn(@Nullable BlockPos.MutableBlockPos mutablePosToUse, int x, int z, IBlockMatcherReaderAware<IBlockState> p_208518_4_, int p_208518_5_) {
      if (mutablePosToUse == null) {
         mutablePosToUse = new BlockPos.MutableBlockPos();
      }

      for(int i = p_208518_5_ - 1; i >= 0; --i) {
         mutablePosToUse.setPos(x, i, z);
         IBlockState iblockstate = this.chunk.getBlockState(mutablePosToUse);
         if (p_208518_4_.test(iblockstate, this.chunk, mutablePosToUse)) {
            return i + 1;
         }
      }

      return 0;
   }

   public int getHeight(int x, int z) {
      return this.getHeight(getDataArrayIndex(x, z));
   }

   private int getHeight(int dataArrayIndex) {
      return this.data.getAt(dataArrayIndex);
   }

   private void set(int x, int z, int value) {
      this.data.setAt(getDataArrayIndex(x, z), value);
   }

   public void setDataArray(long[] dataIn) {
      System.arraycopy(dataIn, 0, this.data.getBackingLongArray(), 0, dataIn.length);
   }

   public long[] getDataArray() {
      return this.data.getBackingLongArray();
   }

   private static int getDataArrayIndex(int x, int z) {
      return x + z * 16;
   }

   public static enum Type {
      WORLD_SURFACE_WG("WORLD_SURFACE_WG", Heightmap.Usage.WORLDGEN, BlockMatcherReaderAware.forBlock(Blocks.AIR)),
      OCEAN_FLOOR_WG("OCEAN_FLOOR_WG", Heightmap.Usage.WORLDGEN, BlockMatcherReaderAware.forBlock(Blocks.AIR), LiquidBlockMatcher.getInstance()),
      LIGHT_BLOCKING("LIGHT_BLOCKING", Heightmap.Usage.LIVE_WORLD, BlockMatcherReaderAware.forBlock(Blocks.AIR), LightEmittingMatcher.getInstance()),
      MOTION_BLOCKING("MOTION_BLOCKING", Heightmap.Usage.LIVE_WORLD, BlockMatcherReaderAware.forBlock(Blocks.AIR), AllowsMovementAndSolidMatcher.getInstance()),
      MOTION_BLOCKING_NO_LEAVES("MOTION_BLOCKING_NO_LEAVES", Heightmap.Usage.LIVE_WORLD, BlockMatcherReaderAware.forBlock(Blocks.AIR), BlockTagMatcher.forTag(BlockTags.LEAVES), AllowsMovementAndSolidMatcher.getInstance()),
      OCEAN_FLOOR("OCEAN_FLOOR", Heightmap.Usage.LIVE_WORLD, BlockMatcherReaderAware.forBlock(Blocks.AIR), AllowsMovementMatcher.getInstance()),
      WORLD_SURFACE("WORLD_SURFACE", Heightmap.Usage.LIVE_WORLD, BlockMatcherReaderAware.forBlock(Blocks.AIR));

      private final IBlockMatcherReaderAware<IBlockState>[] field_202265_e;
      private final String id;
      private final Heightmap.Usage usage;
      private static final Map<String, Heightmap.Type> field_203503_g = Util.make(Maps.newHashMap(), (p_207801_0_) -> {
         for(Heightmap.Type heightmap$type : values()) {
            p_207801_0_.put(heightmap$type.id, heightmap$type);
         }

      });

      private Type(String idIn, Heightmap.Usage usageIn, IBlockMatcherReaderAware<IBlockState>... p_i49318_5_) {
         this.id = idIn;
         this.field_202265_e = p_i49318_5_;
         this.usage = usageIn;
      }

      public IBlockMatcherReaderAware<IBlockState>[] func_202264_a() {
         return this.field_202265_e;
      }

      public String getId() {
         return this.id;
      }

      public Heightmap.Usage getUsage() {
         return this.usage;
      }

      public static Heightmap.Type func_203501_a(String p_203501_0_) {
         return field_203503_g.get(p_203501_0_);
      }
   }

   public static enum Usage {
      WORLDGEN,
      LIVE_WORLD;
   }
}