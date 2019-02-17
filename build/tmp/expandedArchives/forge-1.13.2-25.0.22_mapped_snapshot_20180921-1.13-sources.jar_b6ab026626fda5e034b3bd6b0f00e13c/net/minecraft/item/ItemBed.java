package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public class ItemBed extends ItemBlock {
   public ItemBed(Block blockIn, Item.Properties builder) {
      super(blockIn, builder);
   }

   protected boolean placeBlock(BlockItemUseContext p_195941_1_, IBlockState p_195941_2_) {
      return p_195941_1_.getWorld().setBlockState(p_195941_1_.getPos(), p_195941_2_, 26);
   }
}