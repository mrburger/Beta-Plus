package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public class ItemBlockTall extends ItemBlock {
   public ItemBlockTall(Block blockIn, Item.Properties builder) {
      super(blockIn, builder);
   }

   protected boolean placeBlock(BlockItemUseContext p_195941_1_, IBlockState p_195941_2_) {
      p_195941_1_.getWorld().setBlockState(p_195941_1_.getPos().up(), Blocks.AIR.getDefaultState(), 27);
      return super.placeBlock(p_195941_1_, p_195941_2_);
   }
}