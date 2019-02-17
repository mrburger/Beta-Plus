package net.minecraft.world.gen.surfacebuilders;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class DefaultSurfaceBuilder implements ISurfaceBuilder<SurfaceBuilderConfig> {
   public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, IBlockState defaultBlock, IBlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config) {
      this.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, config.getTop(), config.getMiddle(), config.getBottom(), seaLevel);
   }

   protected void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, IBlockState defaultBlock, IBlockState defaultFluid, IBlockState top, IBlockState middle, IBlockState bottom, int sealevel) {
      IBlockState iblockstate = top;
      IBlockState iblockstate1 = middle;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      int i = -1;
      int j = (int)(noise / 3.0D + 3.0D + random.nextDouble() * 0.25D);
      int k = x & 15;
      int l = z & 15;

      for(int i1 = startHeight; i1 >= 0; --i1) {
         blockpos$mutableblockpos.setPos(k, i1, l);
         IBlockState iblockstate2 = chunkIn.getBlockState(blockpos$mutableblockpos);
         if (iblockstate2.isAir()) {
            i = -1;
         } else if (iblockstate2.getBlock() == defaultBlock.getBlock()) {
            if (i == -1) {
               if (j <= 0) {
                  iblockstate = Blocks.AIR.getDefaultState();
                  iblockstate1 = defaultBlock;
               } else if (i1 >= sealevel - 4 && i1 <= sealevel + 1) {
                  iblockstate = top;
                  iblockstate1 = middle;
               }

               if (i1 < sealevel && (iblockstate == null || iblockstate.isAir())) {
                  if (biomeIn.getTemperature(blockpos$mutableblockpos.setPos(x, i1, z)) < 0.15F) {
                     iblockstate = Blocks.ICE.getDefaultState();
                  } else {
                     iblockstate = defaultFluid;
                  }

                  blockpos$mutableblockpos.setPos(k, i1, l);
               }

               i = j;
               if (i1 >= sealevel - 1) {
                  chunkIn.setBlockState(blockpos$mutableblockpos, iblockstate, false);
               } else if (i1 < sealevel - 7 - j) {
                  iblockstate = Blocks.AIR.getDefaultState();
                  iblockstate1 = defaultBlock;
                  chunkIn.setBlockState(blockpos$mutableblockpos, bottom, false);
               } else {
                  chunkIn.setBlockState(blockpos$mutableblockpos, iblockstate1, false);
               }
            } else if (i > 0) {
               --i;
               chunkIn.setBlockState(blockpos$mutableblockpos, iblockstate1, false);
               if (i == 0 && iblockstate1.getBlock() == Blocks.SAND && j > 1) {
                  i = random.nextInt(4) + Math.max(0, i1 - 63);
                  iblockstate1 = iblockstate1.getBlock() == Blocks.RED_SAND ? Blocks.RED_SANDSTONE.getDefaultState() : Blocks.SANDSTONE.getDefaultState();
               }
            }
         }
      }

   }
}