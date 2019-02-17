package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.state.IBlockState;

public class BlockWithContextConfig implements IFeatureConfig {
   final IBlockState state;
   final List<IBlockState> placeOn;
   final List<IBlockState> placeIn;
   final List<IBlockState> placeUnder;

   public BlockWithContextConfig(IBlockState p_i49003_1_, IBlockState[] p_i49003_2_, IBlockState[] p_i49003_3_, IBlockState[] p_i49003_4_) {
      this.state = p_i49003_1_;
      this.placeOn = Lists.newArrayList(p_i49003_2_);
      this.placeIn = Lists.newArrayList(p_i49003_3_);
      this.placeUnder = Lists.newArrayList(p_i49003_4_);
   }
}