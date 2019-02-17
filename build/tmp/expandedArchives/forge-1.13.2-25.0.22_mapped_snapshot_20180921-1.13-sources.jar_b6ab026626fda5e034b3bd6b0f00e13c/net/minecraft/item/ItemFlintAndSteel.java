package net.minecraft.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class ItemFlintAndSteel extends Item {
   public ItemFlintAndSteel(Item.Properties builder) {
      super(builder);
   }

   /**
    * Called when this item is used when targetting a Block
    */
   public EnumActionResult onItemUse(ItemUseContext p_195939_1_) {
      EntityPlayer entityplayer = p_195939_1_.getPlayer();
      IWorld iworld = p_195939_1_.getWorld();
      BlockPos blockpos = p_195939_1_.getPos().offset(p_195939_1_.getFace());
      if (canIgnite(iworld, blockpos)) {
         iworld.playSound(entityplayer, blockpos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
         IBlockState iblockstate = ((BlockFire)Blocks.FIRE).getStateForPlacement(iworld, blockpos);
         iworld.setBlockState(blockpos, iblockstate, 11);
         ItemStack itemstack = p_195939_1_.getItem();
         if (entityplayer instanceof EntityPlayerMP) {
            CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)entityplayer, blockpos, itemstack);
         }

         if (entityplayer != null) {
            itemstack.damageItem(1, entityplayer);
         }

         return EnumActionResult.SUCCESS;
      } else {
         return EnumActionResult.FAIL;
      }
   }

   public static boolean canIgnite(IWorld p_201825_0_, BlockPos p_201825_1_) {
      IBlockState iblockstate = ((BlockFire)Blocks.FIRE).getStateForPlacement(p_201825_0_, p_201825_1_);
      boolean flag = false;

      for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
         if (p_201825_0_.getBlockState(p_201825_1_.offset(enumfacing)).getBlock() == Blocks.OBSIDIAN && ((BlockPortal)Blocks.NETHER_PORTAL).isPortal(p_201825_0_, p_201825_1_) != null) {
            flag = true;
         }
      }

      return p_201825_0_.isAirBlock(p_201825_1_) && (iblockstate.isValidPosition(p_201825_0_, p_201825_1_) || flag);
   }
}