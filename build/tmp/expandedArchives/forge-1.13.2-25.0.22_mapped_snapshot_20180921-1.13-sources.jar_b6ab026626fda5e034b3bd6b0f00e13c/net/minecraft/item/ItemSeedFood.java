package net.minecraft.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class ItemSeedFood extends ItemFood implements net.minecraftforge.common.IPlantable {
   private final IBlockState field_195972_b;

   public ItemSeedFood(int healAmountIn, float saturation, Block crop, Item.Properties builder) {
      super(healAmountIn, saturation, false, builder);
      this.field_195972_b = crop.getDefaultState();
   }

   /**
    * Called when this item is used when targetting a Block
    */
   public EnumActionResult onItemUse(ItemUseContext p_195939_1_) {
      IWorld iworld = p_195939_1_.getWorld();
      BlockPos blockpos = p_195939_1_.getPos().up();
      if (p_195939_1_.getFace() == EnumFacing.UP && iworld.isAirBlock(blockpos) && this.field_195972_b.isValidPosition(iworld, blockpos) && iworld.getBlockState(p_195939_1_.getPos()).canSustainPlant(iworld, p_195939_1_.getPos(), EnumFacing.UP, this)) {
         iworld.setBlockState(blockpos, this.field_195972_b, 11);
         EntityPlayer entityplayer = p_195939_1_.getPlayer();
         ItemStack itemstack = p_195939_1_.getItem();
         if (entityplayer instanceof EntityPlayerMP) {
            CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)entityplayer, blockpos, itemstack);
         }

         itemstack.shrink(1);
         return EnumActionResult.SUCCESS;
      } else {
         return EnumActionResult.PASS;
      }
   }

   @Override
   public net.minecraftforge.common.EnumPlantType getPlantType(net.minecraft.world.IBlockReader world, BlockPos pos) {
      return net.minecraftforge.common.EnumPlantType.Crop;
   }

   @Override
   public IBlockState getPlant(net.minecraft.world.IBlockReader world, BlockPos pos) {
      return this.field_195972_b;
   }
}