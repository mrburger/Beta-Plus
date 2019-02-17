package net.minecraft.world.gen.surfacebuilders;

import net.minecraft.block.state.IBlockState;

public interface ISurfaceBuilderConfig {
   IBlockState getTop();

   IBlockState getMiddle();
}