package net.minecraft.world.gen.surfacebuilders;

import net.minecraft.block.state.IBlockState;

public class SurfaceBuilderConfig implements ISurfaceBuilderConfig {
   private final IBlockState top;
   private final IBlockState middle;
   private final IBlockState bottom;

   public SurfaceBuilderConfig(IBlockState topIn, IBlockState p_i48954_2_, IBlockState p_i48954_3_) {
      this.top = topIn;
      this.middle = p_i48954_2_;
      this.bottom = p_i48954_3_;
   }

   public IBlockState getTop() {
      return this.top;
   }

   public IBlockState getMiddle() {
      return this.middle;
   }

   public IBlockState getBottom() {
      return this.bottom;
   }
}