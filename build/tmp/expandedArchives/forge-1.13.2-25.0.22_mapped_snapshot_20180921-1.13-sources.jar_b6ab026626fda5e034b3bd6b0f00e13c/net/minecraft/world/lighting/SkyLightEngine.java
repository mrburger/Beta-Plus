package net.minecraft.world.lighting;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.WorldGenRegion;

public class SkyLightEngine extends BaseLightEngine {
   public static final EnumFacing[] HORIZONTAL_DIRECTIONS = new EnumFacing[]{EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH};

   public EnumLightType getLightType() {
      return EnumLightType.SKY;
   }

   public void calculateLight(WorldGenRegion region, IChunk chunkIn) {
      int i = chunkIn.getPos().getXStart();
      int j = chunkIn.getPos().getZStart();

      try (
         BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();
         BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos1 = BlockPos.PooledMutableBlockPos.retain();
      ) {
         for(int k = 0; k < 16; ++k) {
            for(int l = 0; l < 16; ++l) {
               int i1 = chunkIn.getTopBlockY(Heightmap.Type.LIGHT_BLOCKING, k, l) + 1;
               int j1 = k + i;
               int k1 = l + j;

               for(int l1 = i1; l1 < chunkIn.getSections().length * 16 - 1; ++l1) {
                  blockpos$pooledmutableblockpos.setPos(j1, l1, k1);
                  this.setLight(region, blockpos$pooledmutableblockpos, 15);
               }

               this.enqueueLightChange(chunkIn.getPos(), j1, i1, k1, 15);

               for(EnumFacing enumfacing : HORIZONTAL_DIRECTIONS) {
                  int i2 = region.getHeight(Heightmap.Type.LIGHT_BLOCKING, j1 + enumfacing.getXOffset(), k1 + enumfacing.getZOffset());
                  if (i2 - i1 >= 2) {
                     for(int j2 = i1; j2 <= i2; ++j2) {
                        blockpos$pooledmutableblockpos1.setPos(j1 + enumfacing.getXOffset(), j2, k1 + enumfacing.getZOffset());
                        int k2 = region.getBlockState(blockpos$pooledmutableblockpos1).getOpacity(region, blockpos$pooledmutableblockpos1);
                        if (k2 != region.getMaxLightLevel()) {
                           this.setLight(region, blockpos$pooledmutableblockpos1, 15 - k2 - 1);
                           this.enqueueLightChange(chunkIn.getPos(), blockpos$pooledmutableblockpos1, 15 - k2 - 1);
                        }
                     }
                  }
               }
            }
         }

         this.func_202664_a(region, chunkIn.getPos());
      }

   }
}