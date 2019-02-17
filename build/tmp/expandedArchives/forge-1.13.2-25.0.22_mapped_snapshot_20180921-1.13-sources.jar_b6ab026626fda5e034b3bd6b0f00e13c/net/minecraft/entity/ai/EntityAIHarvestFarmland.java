package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;

public class EntityAIHarvestFarmland extends EntityAIMoveToBlock {
   /** Villager that is harvesting */
   private final EntityVillager villager;
   private boolean hasFarmItem;
   private boolean wantsToReapStuff;
   /** 0 => harvest, 1 => replant, -1 => none */
   private int currentTask;

   public EntityAIHarvestFarmland(EntityVillager villagerIn, double speedIn) {
      super(villagerIn, speedIn, 16);
      this.villager = villagerIn;
   }

   /**
    * Returns whether the EntityAIBase should begin execution.
    */
   public boolean shouldExecute() {
      if (this.runDelay <= 0) {
         if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.villager.world, this.villager)) {
            return false;
         }

         this.currentTask = -1;
         this.hasFarmItem = this.villager.isFarmItemInInventory();
         this.wantsToReapStuff = this.villager.wantsMoreFood();
      }

      return super.shouldExecute();
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      return this.currentTask >= 0 && super.shouldContinueExecuting();
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      super.tick();
      this.villager.getLookHelper().setLookPosition((double)this.destinationBlock.getX() + 0.5D, (double)(this.destinationBlock.getY() + 1), (double)this.destinationBlock.getZ() + 0.5D, 10.0F, (float)this.villager.getVerticalFaceSpeed());
      if (this.getIsAboveDestination()) {
         IWorld iworld = this.villager.world;
         BlockPos blockpos = this.destinationBlock.up();
         IBlockState iblockstate = iworld.getBlockState(blockpos);
         Block block = iblockstate.getBlock();
         if (this.currentTask == 0 && block instanceof BlockCrops && ((BlockCrops)block).isMaxAge(iblockstate)) {
            iworld.destroyBlock(blockpos, true);
         } else if (this.currentTask == 1 && iblockstate.isAir()) {
            InventoryBasic inventorybasic = this.villager.getVillagerInventory();

            for(int i = 0; i < inventorybasic.getSizeInventory(); ++i) {
               ItemStack itemstack = inventorybasic.getStackInSlot(i);
               boolean flag = false;
               if (!itemstack.isEmpty()) {
                  if (itemstack.getItem() == Items.WHEAT_SEEDS) {
                     iworld.setBlockState(blockpos, Blocks.WHEAT.getDefaultState(), 3);
                     flag = true;
                  } else if (itemstack.getItem() == Items.POTATO) {
                     iworld.setBlockState(blockpos, Blocks.POTATOES.getDefaultState(), 3);
                     flag = true;
                  } else if (itemstack.getItem() == Items.CARROT) {
                     iworld.setBlockState(blockpos, Blocks.CARROTS.getDefaultState(), 3);
                     flag = true;
                  } else if (itemstack.getItem() == Items.BEETROOT_SEEDS) {
                     iworld.setBlockState(blockpos, Blocks.BEETROOTS.getDefaultState(), 3);
                     flag = true;
                  } else if (itemstack.getItem() instanceof net.minecraftforge.common.IPlantable) {
                     if (((net.minecraftforge.common.IPlantable)itemstack.getItem()).getPlantType(iworld, blockpos) == net.minecraftforge.common.EnumPlantType.Crop) {
                        iworld.setBlockState(blockpos, ((net.minecraftforge.common.IPlantable)itemstack.getItem()).getPlant(iworld, blockpos),3);
                        flag = true;
                     }
                  }
               }

               if (flag) {
                  itemstack.shrink(1);
                  if (itemstack.isEmpty()) {
                     inventorybasic.setInventorySlotContents(i, ItemStack.EMPTY);
                  }
                  break;
               }
            }
         }

         this.currentTask = -1;
         this.runDelay = 10;
      }

   }

   /**
    * Return true to set given position as destination
    */
   protected boolean shouldMoveTo(IWorldReaderBase worldIn, BlockPos pos) {
      Block block = worldIn.getBlockState(pos).getBlock();
      if (block == Blocks.FARMLAND) {
         pos = pos.up();
         IBlockState iblockstate = worldIn.getBlockState(pos);
         block = iblockstate.getBlock();
         if (block instanceof BlockCrops && ((BlockCrops)block).isMaxAge(iblockstate) && this.wantsToReapStuff && (this.currentTask == 0 || this.currentTask < 0)) {
            this.currentTask = 0;
            return true;
         }

         if (iblockstate.isAir() && this.hasFarmItem && (this.currentTask == 1 || this.currentTask < 0)) {
            this.currentTask = 1;
            return true;
         }
      }

      return false;
   }
}