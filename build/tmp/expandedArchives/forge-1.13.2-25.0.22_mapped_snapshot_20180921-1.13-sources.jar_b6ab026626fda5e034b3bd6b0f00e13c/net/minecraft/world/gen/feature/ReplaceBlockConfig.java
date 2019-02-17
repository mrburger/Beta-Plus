package net.minecraft.world.gen.feature;

import java.util.function.Predicate;
import net.minecraft.block.state.IBlockState;

public class ReplaceBlockConfig implements IFeatureConfig {
   public final Predicate<IBlockState> field_202457_a;
   public final IBlockState state;

   public ReplaceBlockConfig(Predicate<IBlockState> p_i48669_1_, IBlockState p_i48669_2_) {
      this.field_202457_a = p_i48669_1_;
      this.state = p_i48669_2_;
   }
}