package net.minecraft.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.init.SoundEvents;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ItemLilyPad extends ItemBlock {
   public ItemLilyPad(Block blockIn, Item.Properties builder) {
      super(blockIn, builder);
   }

   /**
    * Called when this item is used when targetting a Block
    */
   public EnumActionResult onItemUse(ItemUseContext p_195939_1_) {
      return EnumActionResult.PASS;
   }

   /**
    * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
    * {@link #onItemUse}.
    */
   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      RayTraceResult raytraceresult = this.rayTrace(worldIn, playerIn, true);
      if (raytraceresult == null) {
         return new ActionResult<>(EnumActionResult.PASS, itemstack);
      } else {
         if (raytraceresult.type == RayTraceResult.Type.BLOCK) {
            BlockPos blockpos = raytraceresult.getBlockPos();
            if (!worldIn.isBlockModifiable(playerIn, blockpos) || !playerIn.canPlayerEdit(blockpos.offset(raytraceresult.sideHit), raytraceresult.sideHit, itemstack)) {
               return new ActionResult<>(EnumActionResult.FAIL, itemstack);
            }

            BlockPos blockpos1 = blockpos.up();
            IBlockState iblockstate = worldIn.getBlockState(blockpos);
            Material material = iblockstate.getMaterial();
            IFluidState ifluidstate = worldIn.getFluidState(blockpos);
            if ((ifluidstate.getFluid() == Fluids.WATER || material == Material.ICE) && worldIn.isAirBlock(blockpos1)) {

               // special case for handling block placement with water lilies
               net.minecraftforge.common.util.BlockSnapshot blocksnapshot = net.minecraftforge.common.util.BlockSnapshot.getBlockSnapshot(worldIn, blockpos1);
               worldIn.setBlockState(blockpos1, Blocks.LILY_PAD.getDefaultState(), 11);
               if (net.minecraftforge.event.ForgeEventFactory.onPlayerBlockPlace(playerIn, blocksnapshot, net.minecraft.util.EnumFacing.UP, handIn).isCanceled()) {
                  blocksnapshot.restore(true, false);
                  return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
               }

               if (playerIn instanceof EntityPlayerMP) {
                  CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)playerIn, blockpos1, itemstack);
               }

               if (!playerIn.abilities.isCreativeMode) {
                  itemstack.shrink(1);
               }

               playerIn.addStat(StatList.ITEM_USED.get(this));
               worldIn.playSound(playerIn, blockpos, SoundEvents.BLOCK_LILY_PAD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
               return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
            }
         }

         return new ActionResult<>(EnumActionResult.FAIL, itemstack);
      }
   }
}