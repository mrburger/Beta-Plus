package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.state.IBlockState;

public class JungleTreeFeature extends TreeFeature {
   public JungleTreeFeature(boolean p_i48679_1_, int p_i48679_2_, IBlockState p_i48679_3_, IBlockState p_i48679_4_, boolean p_i48679_5_) {
      super(p_i48679_1_, p_i48679_2_, p_i48679_3_, p_i48679_4_, p_i48679_5_);
   }

   protected int func_208534_a(Random p_208534_1_) {
      return this.minTreeHeight + p_208534_1_.nextInt(7);
   }
}