package net.minecraft.client.renderer;

import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RegionRenderCacheBuilder {
   private final BufferBuilder[] builders = new BufferBuilder[BlockRenderLayer.values().length];

   public RegionRenderCacheBuilder() {
      this.builders[BlockRenderLayer.SOLID.ordinal()] = new BufferBuilder(2097152);
      this.builders[BlockRenderLayer.CUTOUT.ordinal()] = new BufferBuilder(131072);
      this.builders[BlockRenderLayer.CUTOUT_MIPPED.ordinal()] = new BufferBuilder(131072);
      this.builders[BlockRenderLayer.TRANSLUCENT.ordinal()] = new BufferBuilder(262144);
   }

   public BufferBuilder getBuilder(BlockRenderLayer layer) {
      return this.builders[layer.ordinal()];
   }

   public BufferBuilder getBuilder(int id) {
      return this.builders[id];
   }
}