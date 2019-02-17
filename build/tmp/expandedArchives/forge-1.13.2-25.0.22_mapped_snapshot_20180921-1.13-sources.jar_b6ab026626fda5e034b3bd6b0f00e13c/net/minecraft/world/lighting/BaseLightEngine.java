package net.minecraft.world.lighting;

import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import it.unimi.dsi.fastutil.ints.IntPriorityQueue;
import javax.annotation.Nullable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.IWorldWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseLightEngine implements ILightEngine {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final EnumFacing[] DIRECTIONS = EnumFacing.values();
   private final IntPriorityQueue lightQueue = new IntArrayFIFOQueue(786);

   public int getLightAt(IWorldReaderBase worldIn, BlockPos pos) {
      return worldIn.getLightFor(this.getLightType(), pos);
   }

   public void setLight(IWorldWriter worldIn, BlockPos pos, int lightValue) {
      worldIn.setLightFor(this.getLightType(), pos, lightValue);
   }

   protected int getOpacity(IBlockReader worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos).getOpacity(worldIn, pos);
   }

   protected int getLightAt(IBlockReader worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos).getLightValue();
   }

   private int packLightChange(@Nullable EnumFacing face, int chunkOffsetX, int yPos, int chunkOffsetZ, int lightValue) {
      int i = 7;
      if (face != null) {
         i = face.ordinal();
      }

      return i << 24 | chunkOffsetX << 18 | yPos << 10 | chunkOffsetZ << 4 | lightValue << 0;
   }

   private int unpackXPos(int packedValue) {
      return packedValue >> 18 & 63;
   }

   private int unpackYPos(int packedValue) {
      return packedValue >> 10 & 255;
   }

   private int unpackZPos(int packedValue) {
      return packedValue >> 4 & 63;
   }

   private int unpackLight(int packedValue) {
      return packedValue >> 0 & 15;
   }

   @Nullable
   private EnumFacing func_202661_e(int p_202661_1_) {
      int i = p_202661_1_ >> 24 & 7;
      return i == 7 ? null : EnumFacing.values()[p_202661_1_ >> 24 & 7];
   }

   protected void func_202664_a(IWorld worldIn, ChunkPos cPos) {
      try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
         while(!this.lightQueue.isEmpty()) {
            int i = this.lightQueue.dequeueInt();
            int j = this.unpackLight(i);
            int k = this.unpackXPos(i) - 16;
            int l = this.unpackYPos(i);
            int i1 = this.unpackZPos(i) - 16;
            EnumFacing enumfacing = this.func_202661_e(i);

            for(EnumFacing enumfacing1 : DIRECTIONS) {
               if (enumfacing1 != enumfacing) {
                  int j1 = k + enumfacing1.getXOffset();
                  int k1 = l + enumfacing1.getYOffset();
                  int l1 = i1 + enumfacing1.getZOffset();
                  if (k1 <= 255 && k1 >= 0) {
                     blockpos$pooledmutableblockpos.setPos(j1 + cPos.getXStart(), k1, l1 + cPos.getZStart());
                     int i2 = this.getOpacity(worldIn, blockpos$pooledmutableblockpos);
                     int j2 = j - Math.max(i2, 1);
                     if (j2 > 0 && j2 > this.getLightAt(worldIn, blockpos$pooledmutableblockpos)) {
                        this.setLight(worldIn, blockpos$pooledmutableblockpos, j2);
                        this.enqueueLightChange(cPos, blockpos$pooledmutableblockpos, j2);
                     }
                  }
               }
            }
         }
      }

   }

   protected void enqueueLightChange(ChunkPos cPos, int blockX, int blockY, int blockZ, int lightValue) {
      int i = blockX - cPos.getXStart() + 16;
      int j = blockZ - cPos.getZStart() + 16;
      this.lightQueue.enqueue(this.packLightChange((EnumFacing)null, i, blockY, j, lightValue));
   }

   protected void enqueueLightChange(ChunkPos cPos, BlockPos bPos, int lightValue) {
      this.enqueueLightChange(cPos, bPos.getX(), bPos.getY(), bPos.getZ(), lightValue);
   }
}