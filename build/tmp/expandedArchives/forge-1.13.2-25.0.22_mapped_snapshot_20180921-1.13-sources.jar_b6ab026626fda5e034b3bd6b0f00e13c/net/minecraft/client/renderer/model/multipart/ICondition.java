package net.minecraft.client.renderer.model.multipart;

import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.state.StateContainer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ICondition {
   ICondition TRUE = (p_lambda$static$1_0_) -> {
      return (p_lambda$null$0_0_) -> {
         return true;
      };
   };
   ICondition FALSE = (p_lambda$static$3_0_) -> {
      return (p_lambda$null$2_0_) -> {
         return false;
      };
   };

   Predicate<IBlockState> getPredicate(StateContainer<Block, IBlockState> p_getPredicate_1_);
}