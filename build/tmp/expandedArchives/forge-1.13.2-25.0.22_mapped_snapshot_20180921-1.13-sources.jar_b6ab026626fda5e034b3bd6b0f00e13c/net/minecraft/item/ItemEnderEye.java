package net.minecraft.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class ItemEnderEye extends Item {
   public ItemEnderEye(Item.Properties builder) {
      super(builder);
   }

   /**
    * Called when this item is used when targetting a Block
    */
   public EnumActionResult onItemUse(ItemUseContext p_195939_1_) {
      World world = p_195939_1_.getWorld();
      BlockPos blockpos = p_195939_1_.getPos();
      IBlockState iblockstate = world.getBlockState(blockpos);
      if (iblockstate.getBlock() == Blocks.END_PORTAL_FRAME && !iblockstate.get(BlockEndPortalFrame.EYE)) {
         if (world.isRemote) {
            return EnumActionResult.SUCCESS;
         } else {
            IBlockState iblockstate1 = iblockstate.with(BlockEndPortalFrame.EYE, Boolean.valueOf(true));
            Block.nudgeEntitiesWithNewState(iblockstate, iblockstate1, world, blockpos);
            world.setBlockState(blockpos, iblockstate1, 2);
            world.updateComparatorOutputLevel(blockpos, Blocks.END_PORTAL_FRAME);
            p_195939_1_.getItem().shrink(1);

            for(int i = 0; i < 16; ++i) {
               double d0 = (double)((float)blockpos.getX() + (5.0F + random.nextFloat() * 6.0F) / 16.0F);
               double d1 = (double)((float)blockpos.getY() + 0.8125F);
               double d2 = (double)((float)blockpos.getZ() + (5.0F + random.nextFloat() * 6.0F) / 16.0F);
               double d3 = 0.0D;
               double d4 = 0.0D;
               double d5 = 0.0D;
               world.spawnParticle(Particles.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            }

            world.playSound((EntityPlayer)null, blockpos, SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
            BlockPattern.PatternHelper blockpattern$patternhelper = BlockEndPortalFrame.getOrCreatePortalShape().match(world, blockpos);
            if (blockpattern$patternhelper != null) {
               BlockPos blockpos1 = blockpattern$patternhelper.getFrontTopLeft().add(-3, 0, -3);

               for(int j = 0; j < 3; ++j) {
                  for(int k = 0; k < 3; ++k) {
                     world.setBlockState(blockpos1.add(j, 0, k), Blocks.END_PORTAL.getDefaultState(), 2);
                  }
               }

               world.playBroadcastSound(1038, blockpos1.add(1, 0, 1), 0);
            }

            return EnumActionResult.SUCCESS;
         }
      } else {
         return EnumActionResult.PASS;
      }
   }

   /**
    * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
    * {@link #onItemUse}.
    */
   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      RayTraceResult raytraceresult = this.rayTrace(worldIn, playerIn, false);
      if (raytraceresult != null && raytraceresult.type == RayTraceResult.Type.BLOCK && worldIn.getBlockState(raytraceresult.getBlockPos()).getBlock() == Blocks.END_PORTAL_FRAME) {
         return new ActionResult<>(EnumActionResult.PASS, itemstack);
      } else {
         playerIn.setActiveHand(handIn);
         if (!worldIn.isRemote) {
            BlockPos blockpos = ((WorldServer)worldIn).getChunkProvider().findNearestStructure(worldIn, "Stronghold", new BlockPos(playerIn), 100, false);
            if (blockpos != null) {
               EntityEnderEye entityendereye = new EntityEnderEye(worldIn, playerIn.posX, playerIn.posY + (double)(playerIn.height / 2.0F), playerIn.posZ);
               entityendereye.moveTowards(blockpos);
               worldIn.spawnEntity(entityendereye);
               if (playerIn instanceof EntityPlayerMP) {
                  CriteriaTriggers.USED_ENDER_EYE.trigger((EntityPlayerMP)playerIn, blockpos);
               }

               worldIn.playSound((EntityPlayer)null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_ENDER_EYE_LAUNCH, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
               worldIn.playEvent((EntityPlayer)null, 1003, new BlockPos(playerIn), 0);
               if (!playerIn.abilities.isCreativeMode) {
                  itemstack.shrink(1);
               }

               playerIn.addStat(StatList.ITEM_USED.get(this));
               return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
            }
         }

         return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
      }
   }
}