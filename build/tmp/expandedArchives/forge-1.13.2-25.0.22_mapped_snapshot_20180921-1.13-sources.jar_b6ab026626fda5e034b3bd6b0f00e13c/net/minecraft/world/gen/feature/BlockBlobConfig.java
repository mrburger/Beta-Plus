package net.minecraft.world.gen.feature;

import net.minecraft.block.Block;

public class BlockBlobConfig implements IFeatureConfig {
   public final Block block;
   public final int field_202464_b;

   public BlockBlobConfig(Block p_i48690_1_, int p_i48690_2_) {
      this.block = p_i48690_1_;
      this.field_202464_b = p_i48690_2_;
   }
}