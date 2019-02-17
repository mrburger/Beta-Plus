package net.minecraft.client.renderer;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ViewFrustum {
   protected final WorldRenderer renderGlobal;
   protected final World world;
   protected int countChunksY;
   protected int countChunksX;
   protected int countChunksZ;
   public RenderChunk[] renderChunks;

   public ViewFrustum(World worldIn, int renderDistanceChunks, WorldRenderer renderGlobalIn, IRenderChunkFactory renderChunkFactory) {
      this.renderGlobal = renderGlobalIn;
      this.world = worldIn;
      this.setCountChunksXYZ(renderDistanceChunks);
      this.createRenderChunks(renderChunkFactory);
   }

   protected void createRenderChunks(IRenderChunkFactory renderChunkFactory) {
      int i = this.countChunksX * this.countChunksY * this.countChunksZ;
      this.renderChunks = new RenderChunk[i];

      for(int j = 0; j < this.countChunksX; ++j) {
         for(int k = 0; k < this.countChunksY; ++k) {
            for(int l = 0; l < this.countChunksZ; ++l) {
               int i1 = this.func_212478_a(j, k, l);
               this.renderChunks[i1] = renderChunkFactory.create(this.world, this.renderGlobal);
               this.renderChunks[i1].setPosition(j * 16, k * 16, l * 16);
            }
         }
      }

   }

   public void deleteGlResources() {
      for(RenderChunk renderchunk : this.renderChunks) {
         renderchunk.deleteGlResources();
      }

   }

   private int func_212478_a(int p_212478_1_, int p_212478_2_, int p_212478_3_) {
      return (p_212478_3_ * this.countChunksY + p_212478_2_) * this.countChunksX + p_212478_1_;
   }

   protected void setCountChunksXYZ(int renderDistanceChunks) {
      int i = renderDistanceChunks * 2 + 1;
      this.countChunksX = i;
      this.countChunksY = 16;
      this.countChunksZ = i;
   }

   public void updateChunkPositions(double viewEntityX, double viewEntityZ) {
      int i = MathHelper.floor(viewEntityX) - 8;
      int j = MathHelper.floor(viewEntityZ) - 8;
      int k = this.countChunksX * 16;

      for(int l = 0; l < this.countChunksX; ++l) {
         int i1 = this.getBaseCoordinate(i, k, l);

         for(int j1 = 0; j1 < this.countChunksZ; ++j1) {
            int k1 = this.getBaseCoordinate(j, k, j1);

            for(int l1 = 0; l1 < this.countChunksY; ++l1) {
               int i2 = l1 * 16;
               RenderChunk renderchunk = this.renderChunks[this.func_212478_a(l, l1, j1)];
               renderchunk.setPosition(i1, i2, k1);
            }
         }
      }

   }

   private int getBaseCoordinate(int midBlocksIn, int countBlocksIn, int chunksIn) {
      int i = chunksIn * 16;
      int j = i - midBlocksIn + countBlocksIn / 2;
      if (j < 0) {
         j -= countBlocksIn - 1;
      }

      return i - j / countBlocksIn * countBlocksIn;
   }

   public void markBlocksForUpdate(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean updateImmediately) {
      int i = MathHelper.intFloorDiv(minX, 16);
      int j = MathHelper.intFloorDiv(minY, 16);
      int k = MathHelper.intFloorDiv(minZ, 16);
      int l = MathHelper.intFloorDiv(maxX, 16);
      int i1 = MathHelper.intFloorDiv(maxY, 16);
      int j1 = MathHelper.intFloorDiv(maxZ, 16);

      for(int k1 = i; k1 <= l; ++k1) {
         int l1 = MathHelper.normalizeAngle(k1, this.countChunksX);

         for(int i2 = j; i2 <= i1; ++i2) {
            int j2 = MathHelper.normalizeAngle(i2, this.countChunksY);

            for(int k2 = k; k2 <= j1; ++k2) {
               int l2 = MathHelper.normalizeAngle(k2, this.countChunksZ);
               RenderChunk renderchunk = this.renderChunks[this.func_212478_a(l1, j2, l2)];
               renderchunk.setNeedsUpdate(updateImmediately);
            }
         }
      }

   }

   @Nullable
   protected RenderChunk getRenderChunk(BlockPos pos) {
      int i = MathHelper.intFloorDiv(pos.getX(), 16);
      int j = MathHelper.intFloorDiv(pos.getY(), 16);
      int k = MathHelper.intFloorDiv(pos.getZ(), 16);
      if (j >= 0 && j < this.countChunksY) {
         i = MathHelper.normalizeAngle(i, this.countChunksX);
         k = MathHelper.normalizeAngle(k, this.countChunksZ);
         return this.renderChunks[this.func_212478_a(i, j, k)];
      } else {
         return null;
      }
   }
}