package net.minecraft.world.lighting;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.WorldGenRegion;

public class BlockLightEngine extends BaseLightEngine {
   public EnumLightType getLightType() {
      return EnumLightType.BLOCK;
   }

   public void calculateLight(WorldGenRegion region, IChunk chunkIn) {
      for(BlockPos blockpos : chunkIn.getLightBlockPositions()) {
         this.setLight(region, blockpos, this.getLightAt((net.minecraft.world.IBlockReader)region, blockpos)); //Forge add typecast to fix obf issue.
         this.enqueueLightChange(chunkIn.getPos(), blockpos, this.getLightAt(region, blockpos));
      }

      this.func_202664_a(region, chunkIn.getPos());
   }
}