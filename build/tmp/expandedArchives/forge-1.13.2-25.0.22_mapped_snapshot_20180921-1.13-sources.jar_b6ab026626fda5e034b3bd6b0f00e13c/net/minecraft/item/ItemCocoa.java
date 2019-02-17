package net.minecraft.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class ItemCocoa extends ItemDye {
   public ItemCocoa(EnumDyeColor p_i48516_1_, Item.Properties builder) {
      super(p_i48516_1_, builder);
   }

   /**
    * Called when this item is used when targetting a Block
    */
   public EnumActionResult onItemUse(ItemUseContext p_195939_1_) {
      BlockItemUseContext blockitemusecontext = new BlockItemUseContext(p_195939_1_);
      if (blockitemusecontext.canPlace()) {
         IWorld iworld = p_195939_1_.getWorld();
         IBlockState iblockstate = Blocks.COCOA.getStateForPlacement(blockitemusecontext);
         BlockPos blockpos = blockitemusecontext.getPos();
         if (iblockstate != null && iworld.setBlockState(blockpos, iblockstate, 2)) {
            ItemStack itemstack = p_195939_1_.getItem();
            EntityPlayer entityplayer = blockitemusecontext.getPlayer();
            if (entityplayer instanceof EntityPlayerMP) {
               CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)entityplayer, blockpos, itemstack);
            }

            itemstack.shrink(1);
            return EnumActionResult.SUCCESS;
         }
      }

      return EnumActionResult.FAIL;
   }
}