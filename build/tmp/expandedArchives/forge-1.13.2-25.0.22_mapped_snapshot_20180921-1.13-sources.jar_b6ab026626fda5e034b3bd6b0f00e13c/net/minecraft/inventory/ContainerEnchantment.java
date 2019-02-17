package net.minecraft.inventory;

import java.util.List;
import java.util.Random;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ContainerEnchantment extends Container {
   /** A 2-slot InventoryBasic anonymous subclass (see constructor) that holds the item to be enchanted and the lapis. */
   public IInventory tableInventory = new InventoryBasic(new TextComponentString("Enchant"), 2) {
      /**
       * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
       */
      public int getInventoryStackLimit() {
         return 64;
      }

      /**
       * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think
       * it hasn't changed and skip it.
       */
      public void markDirty() {
         super.markDirty();
         ContainerEnchantment.this.onCraftMatrixChanged(this);
      }
   };
   /** current world (for bookshelf counting) */
   private final World world;
   private final BlockPos position;
   private final Random rand = new Random();
   public int xpSeed;
   /** 3-member array storing the enchantment levels of each slot */
   public int[] enchantLevels = new int[3];
   public int[] enchantClue = new int[]{-1, -1, -1};
   public int[] worldClue = new int[]{-1, -1, -1};

   @OnlyIn(Dist.CLIENT)
   public ContainerEnchantment(InventoryPlayer playerInv, World worldIn) {
      this(playerInv, worldIn, BlockPos.ORIGIN);
   }

   public ContainerEnchantment(InventoryPlayer playerInv, World worldIn, BlockPos pos) {
      this.world = worldIn;
      this.position = pos;
      this.xpSeed = playerInv.player.getXPSeed();
      this.addSlot(new Slot(this.tableInventory, 0, 15, 47) {
         /**
          * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
          */
         public boolean isItemValid(ItemStack stack) {
            return true;
         }

         /**
          * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the
          * case of armor slots)
          */
         public int getSlotStackLimit() {
            return 1;
         }
      });
      this.addSlot(new Slot(this.tableInventory, 1, 35, 47) {
         /**
          * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
          */
         public boolean isItemValid(ItemStack stack) {
            return net.minecraftforge.common.Tags.Items.GEMS_LAPIS.contains(stack.getItem());
         }
      });

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for(int k = 0; k < 9; ++k) {
         this.addSlot(new Slot(playerInv, k, 8 + k * 18, 142));
      }

   }

   protected void broadcastData(IContainerListener crafting) {
      crafting.sendWindowProperty(this, 0, this.enchantLevels[0]);
      crafting.sendWindowProperty(this, 1, this.enchantLevels[1]);
      crafting.sendWindowProperty(this, 2, this.enchantLevels[2]);
      crafting.sendWindowProperty(this, 3, this.xpSeed & -16);
      crafting.sendWindowProperty(this, 4, this.enchantClue[0]);
      crafting.sendWindowProperty(this, 5, this.enchantClue[1]);
      crafting.sendWindowProperty(this, 6, this.enchantClue[2]);
      crafting.sendWindowProperty(this, 7, this.worldClue[0]);
      crafting.sendWindowProperty(this, 8, this.worldClue[1]);
      crafting.sendWindowProperty(this, 9, this.worldClue[2]);
   }

   public void addListener(IContainerListener listener) {
      super.addListener(listener);
      this.broadcastData(listener);
   }

   /**
    * Looks for changes made in the container, sends them to every listener.
    */
   public void detectAndSendChanges() {
      super.detectAndSendChanges();

      for(int i = 0; i < this.listeners.size(); ++i) {
         IContainerListener icontainerlistener = this.listeners.get(i);
         this.broadcastData(icontainerlistener);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void updateProgressBar(int id, int data) {
      if (id >= 0 && id <= 2) {
         this.enchantLevels[id] = data;
      } else if (id == 3) {
         this.xpSeed = data;
      } else if (id >= 4 && id <= 6) {
         this.enchantClue[id - 4] = data;
      } else if (id >= 7 && id <= 9) {
         this.worldClue[id - 7] = data;
      } else {
         super.updateProgressBar(id, data);
      }

   }

   private float getPower(BlockPos pos) {
      return world.getBlockState(pos).getEnchantPowerBonus(world, pos);
   }

   /**
    * Callback for when the crafting matrix is changed.
    */
   public void onCraftMatrixChanged(IInventory inventoryIn) {
      if (inventoryIn == this.tableInventory) {
         ItemStack itemstack = inventoryIn.getStackInSlot(0);
         if (!itemstack.isEmpty() && itemstack.isEnchantable()) {
            if (!this.world.isRemote) {
               int l = 0;
               float power = 0;

               for(int j = -1; j <= 1; ++j) {
                  for(int k = -1; k <= 1; ++k) {
                     if ((j != 0 || k != 0) && this.world.isAirBlock(this.position.add(k, 0, j)) && this.world.isAirBlock(this.position.add(k, 1, j))) {
                        power += getPower(position.add(k * 2, 0, j * 2));
                        power += getPower(position.add(k * 2, 1, j * 2));

                        if (k != 0 && j != 0) {
                           power += getPower(position.add(k * 2, 0, j));
                           power += getPower(position.add(k * 2, 1, j));
                           power += getPower(position.add(k, 0, j * 2));
                           power += getPower(position.add(k, 1, j * 2));
                        }
                     }
                  }
               }

               this.rand.setSeed((long)this.xpSeed);

               for(int i1 = 0; i1 < 3; ++i1) {
                  this.enchantLevels[i1] = EnchantmentHelper.calcItemStackEnchantability(this.rand, i1, (int)power, itemstack);
                  this.enchantClue[i1] = -1;
                  this.worldClue[i1] = -1;
                  if (this.enchantLevels[i1] < i1 + 1) {
                     this.enchantLevels[i1] = 0;
                  }
                  this.enchantLevels[i1] = net.minecraftforge.event.ForgeEventFactory.onEnchantmentLevelSet(world, position, i1, (int)power, itemstack, enchantLevels[i1]);
               }

               for(int j1 = 0; j1 < 3; ++j1) {
                  if (this.enchantLevels[j1] > 0) {
                     List<EnchantmentData> list = this.getEnchantmentList(itemstack, j1, this.enchantLevels[j1]);
                     if (list != null && !list.isEmpty()) {
                        EnchantmentData enchantmentdata = list.get(this.rand.nextInt(list.size()));
                        this.enchantClue[j1] = IRegistry.field_212628_q.getId(enchantmentdata.enchantment);
                        this.worldClue[j1] = enchantmentdata.enchantmentLevel;
                     }
                  }
               }

               this.detectAndSendChanges();
            }
         } else {
            for(int i = 0; i < 3; ++i) {
               this.enchantLevels[i] = 0;
               this.enchantClue[i] = -1;
               this.worldClue[i] = -1;
            }
         }
      }

   }

   /**
    * Handles the given Button-click on the server, currently only used by enchanting. Name is for legacy.
    */
   public boolean enchantItem(EntityPlayer playerIn, int id) {
      ItemStack itemstack = this.tableInventory.getStackInSlot(0);
      ItemStack itemstack1 = this.tableInventory.getStackInSlot(1);
      int i = id + 1;
      if ((itemstack1.isEmpty() || itemstack1.getCount() < i) && !playerIn.abilities.isCreativeMode) {
         return false;
      } else if (this.enchantLevels[id] > 0 && !itemstack.isEmpty() && (playerIn.experienceLevel >= i && playerIn.experienceLevel >= this.enchantLevels[id] || playerIn.abilities.isCreativeMode)) {
         if (!this.world.isRemote) {
            List<EnchantmentData> list = this.getEnchantmentList(itemstack, id, this.enchantLevels[id]);
            if (!list.isEmpty()) {
               playerIn.onEnchant(itemstack, i);
               boolean flag = itemstack.getItem() == Items.BOOK;
               if (flag) {
                  itemstack = new ItemStack(Items.ENCHANTED_BOOK);
                  this.tableInventory.setInventorySlotContents(0, itemstack);
               }

               for(int j = 0; j < list.size(); ++j) {
                  EnchantmentData enchantmentdata = list.get(j);
                  if (flag) {
                     ItemEnchantedBook.addEnchantment(itemstack, enchantmentdata);
                  } else {
                     itemstack.addEnchantment(enchantmentdata.enchantment, enchantmentdata.enchantmentLevel);
                  }
               }

               if (!playerIn.abilities.isCreativeMode) {
                  itemstack1.shrink(i);
                  if (itemstack1.isEmpty()) {
                     this.tableInventory.setInventorySlotContents(1, ItemStack.EMPTY);
                  }
               }

               playerIn.addStat(StatList.ENCHANT_ITEM);
               if (playerIn instanceof EntityPlayerMP) {
                  CriteriaTriggers.ENCHANTED_ITEM.trigger((EntityPlayerMP)playerIn, itemstack, i);
               }

               this.tableInventory.markDirty();
               this.xpSeed = playerIn.getXPSeed();
               this.onCraftMatrixChanged(this.tableInventory);
               this.world.playSound((EntityPlayer)null, this.position, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private List<EnchantmentData> getEnchantmentList(ItemStack stack, int enchantSlot, int level) {
      this.rand.setSeed((long)(this.xpSeed + enchantSlot));
      List<EnchantmentData> list = EnchantmentHelper.buildEnchantmentList(this.rand, stack, level, false);
      if (stack.getItem() == Items.BOOK && list.size() > 1) {
         list.remove(this.rand.nextInt(list.size()));
      }

      return list;
   }

   @OnlyIn(Dist.CLIENT)
   public int getLapisAmount() {
      ItemStack itemstack = this.tableInventory.getStackInSlot(1);
      return itemstack.isEmpty() ? 0 : itemstack.getCount();
   }

   /**
    * Called when the container is closed.
    */
   public void onContainerClosed(EntityPlayer playerIn) {
      super.onContainerClosed(playerIn);
      if (!this.world.isRemote) {
         this.clearContainer(playerIn, playerIn.world, this.tableInventory);
      }
   }

   /**
    * Determines whether supplied player can use this container
    */
   public boolean canInteractWith(EntityPlayer playerIn) {
      if (this.world.getBlockState(this.position).getBlock() != Blocks.ENCHANTING_TABLE) {
         return false;
      } else {
         return !(playerIn.getDistanceSq((double)this.position.getX() + 0.5D, (double)this.position.getY() + 0.5D, (double)this.position.getZ() + 0.5D) > 64.0D);
      }
   }

   /**
    * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
    * inventory and the other inventory(s).
    */
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         if (index == 0) {
            if (!this.mergeItemStack(itemstack1, 2, 38, true)) {
               return ItemStack.EMPTY;
            }
         } else if (index == 1) {
            if (!this.mergeItemStack(itemstack1, 2, 38, true)) {
               return ItemStack.EMPTY;
            }
         } else if (itemstack1.getItem() == Items.LAPIS_LAZULI) {
            if (!this.mergeItemStack(itemstack1, 1, 2, true)) {
               return ItemStack.EMPTY;
            }
         } else {
            if (this.inventorySlots.get(0).getHasStack() || !this.inventorySlots.get(0).isItemValid(itemstack1)) {
               return ItemStack.EMPTY;
            }

            if (itemstack1.hasTag()) { // Forge: Fix MC-17431
               ((Slot)this.inventorySlots.get(0)).putStack(itemstack1.split(1));
            } else if (!itemstack1.isEmpty()) {
               this.inventorySlots.get(0).putStack(new ItemStack(itemstack1.getItem()));
               itemstack1.shrink(1);
            }
         }

         if (itemstack1.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
         } else {
            slot.onSlotChanged();
         }

         if (itemstack1.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(playerIn, itemstack1);
      }

      return itemstack;
   }
}