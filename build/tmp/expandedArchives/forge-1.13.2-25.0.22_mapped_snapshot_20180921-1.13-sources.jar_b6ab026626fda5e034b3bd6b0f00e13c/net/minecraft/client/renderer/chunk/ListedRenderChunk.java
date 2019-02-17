package net.minecraft.client.renderer.chunk;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ListedRenderChunk extends RenderChunk {
   private final int baseDisplayList = GLAllocation.generateDisplayLists(BlockRenderLayer.values().length);

   public ListedRenderChunk(World p_i49842_1_, WorldRenderer p_i49842_2_) {
      super(p_i49842_1_, p_i49842_2_);
   }

   public int getDisplayList(BlockRenderLayer layer, CompiledChunk p_178600_2_) {
      return !p_178600_2_.isLayerEmpty(layer) ? this.baseDisplayList + layer.ordinal() : -1;
   }

   public void deleteGlResources() {
      super.deleteGlResources();
      GLAllocation.deleteDisplayLists(this.baseDisplayList, BlockRenderLayer.values().length);
   }
}