package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.placement.BasePlacement;
import net.minecraft.world.gen.placement.IPlacementConfig;

public class CompositeFlowerFeature<D extends IPlacementConfig> extends CompositeFeature<NoFeatureConfig, D> {
   public CompositeFlowerFeature(AbstractFlowersFeature p_i48681_1_, BasePlacement<D> p_i48681_2_, D p_i48681_3_) {
      super(p_i48681_1_, IFeatureConfig.NO_FEATURE_CONFIG, p_i48681_2_, p_i48681_3_);
   }

   public IBlockState getRandomFlower(Random p_202354_1_, BlockPos p_202354_2_) {
      return ((AbstractFlowersFeature)this.feature).getRandomFlower(p_202354_1_, p_202354_2_);
   }
}