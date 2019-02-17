package net.minecraft.tileentity;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class TileEntityHopper extends TileEntityLockableLoot implements IHopper, ITickable {
   private NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);
   private int transferCooldown = -1;
   private long tickedGameTime;

   public TileEntityHopper() {
      super(TileEntityType.HOPPER);
   }

   public void read(NBTTagCompound compound) {
      super.read(compound);
      this.inventory = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
      if (!this.checkLootAndRead(compound)) {
         ItemStackHelper.loadAllItems(compound, this.inventory);
      }

      if (compound.contains("CustomName", 8)) {
         this.setCustomName(ITextComponent.Serializer.fromJson(compound.getString("CustomName")));
      }

      this.transferCooldown = compound.getInt("TransferCooldown");
   }

   public NBTTagCompound write(NBTTagCompound compound) {
      super.write(compound);
      if (!this.checkLootAndWrite(compound)) {
         ItemStackHelper.saveAllItems(compound, this.inventory);
      }

      compound.setInt("TransferCooldown", this.transferCooldown);
      ITextComponent itextcomponent = this.getCustomName();
      if (itextcomponent != null) {
         compound.setString("CustomName", ITextComponent.Serializer.toJson(itextcomponent));
      }

      return compound;
   }

   /**
    * Returns the number of slots in the inventory.
    */
   public int getSizeInventory() {
      return this.inventory.size();
   }

   /**
    * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
    */
   public ItemStack decrStackSize(int index, int count) {
      this.fillWithLoot((EntityPlayer)null);
      return ItemStackHelper.getAndSplit(this.getItems(), index, count);
   }

   /**
    * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
    */
   public void setInventorySlotContents(int index, ItemStack stack) {
      this.fillWithLoot((EntityPlayer)null);
      this.getItems().set(index, stack);
      if (stack.getCount() > this.getInventoryStackLimit()) {
         stack.setCount(this.getInventoryStackLimit());
      }

   }

   public ITextComponent getName() {
      return (ITextComponent)(this.customName != null ? this.customName : new TextComponentTranslation("container.hopper"));
   }

   /**
    * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
    */
   public int getInventoryStackLimit() {
      return 64;
   }

   public void tick() {
      if (this.world != null && !this.world.isRemote) {
         --this.transferCooldown;
         this.tickedGameTime = this.world.getGameTime();
         if (!this.isOnTransferCooldown()) {
            this.setTransferCooldown(0);
            this.updateHopper(() -> {
               return pullItems(this);
            });
         }

      }
   }

   private boolean updateHopper(Supplier<Boolean> p_200109_1_) {
      if (this.world != null && !this.world.isRemote) {
         if (!this.isOnTransferCooldown() && this.getBlockState().get(BlockHopper.ENABLED)) {
            boolean flag = false;
            if (!this.isInventoryEmpty()) {
               flag = this.transferItemsOut();
            }

            if (!this.isFull()) {
               flag |= p_200109_1_.get();
            }

            if (flag) {
               this.setTransferCooldown(8);
               this.markDirty();
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean isInventoryEmpty() {
      for(ItemStack itemstack : this.inventory) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public boolean isEmpty() {
      return this.isInventoryEmpty();
   }

   private boolean isFull() {
      for(ItemStack itemstack : this.inventory) {
         if (itemstack.isEmpty() || itemstack.getCount() != itemstack.getMaxStackSize()) {
            return false;
         }
      }

      return true;
   }

   private boolean transferItemsOut() {
      if (net.minecraftforge.items.VanillaInventoryCodeHooks.insertHook(this)) return true;
      IInventory iinventory = this.getInventoryForHopperTransfer();
      if (iinventory == null) {
         return false;
      } else {
         EnumFacing enumfacing = this.getBlockState().get(BlockHopper.FACING).getOpposite();
         if (this.isInventoryFull(iinventory, enumfacing)) {
            return false;
         } else {
            for(int i = 0; i < this.getSizeInventory(); ++i) {
               if (!this.getStackInSlot(i).isEmpty()) {
                  ItemStack itemstack = this.getStackInSlot(i).copy();
                  ItemStack itemstack1 = putStackInInventoryAllSlots(this, iinventory, this.decrStackSize(i, 1), enumfacing);
                  if (itemstack1.isEmpty()) {
                     iinventory.markDirty();
                     return true;
                  }

                  this.setInventorySlotContents(i, itemstack);
               }
            }

            return false;
         }
      }
   }

   /**
    * Returns false if the inventory has any room to place items in
    */
   private boolean isInventoryFull(IInventory inventoryIn, EnumFacing side) {
      if (inventoryIn instanceof ISidedInventory) {
         ISidedInventory isidedinventory = (ISidedInventory)inventoryIn;
         int[] aint = isidedinventory.getSlotsForFace(side);

         for(int k : aint) {
            ItemStack itemstack1 = isidedinventory.getStackInSlot(k);
            if (itemstack1.isEmpty() || itemstack1.getCount() != itemstack1.getMaxStackSize()) {
               return false;
            }
         }
      } else {
         int i = inventoryIn.getSizeInventory();

         for(int j = 0; j < i; ++j) {
            ItemStack itemstack = inventoryIn.getStackInSlot(j);
            if (itemstack.isEmpty() || itemstack.getCount() != itemstack.getMaxStackSize()) {
               return false;
            }
         }
      }

      return true;
   }

   /**
    * Returns false if the specified IInventory contains any items
    */
   private static boolean isInventoryEmpty(IInventory inventoryIn, EnumFacing side) {
      if (inventoryIn instanceof ISidedInventory) {
         ISidedInventory isidedinventory = (ISidedInventory)inventoryIn;
         int[] aint = isidedinventory.getSlotsForFace(side);

         for(int i : aint) {
            if (!isidedinventory.getStackInSlot(i).isEmpty()) {
               return false;
            }
         }
      } else {
         int j = inventoryIn.getSizeInventory();

         for(int k = 0; k < j; ++k) {
            if (!inventoryIn.getStackInSlot(k).isEmpty()) {
               return false;
            }
         }
      }

      return true;
   }

   /**
    * Pull dropped {@link net.minecraft.entity.item.EntityItem EntityItem}s from the world above the hopper and items
    * from any inventory attached to this hopper into the hopper's inventory.
    * 
    * @param hopper the hopper in question
    * @return whether any items were successfully added to the hopper
    */
   public static boolean pullItems(IHopper hopper) {
      Boolean ret = net.minecraftforge.items.VanillaInventoryCodeHooks.extractHook(hopper);
      if (ret != null) return ret;
      IInventory iinventory = getSourceInventory(hopper);
      if (iinventory != null) {
         EnumFacing enumfacing = EnumFacing.DOWN;
         if (isInventoryEmpty(iinventory, enumfacing)) {
            return false;
         }

         if (iinventory instanceof ISidedInventory) {
            ISidedInventory isidedinventory = (ISidedInventory)iinventory;
            int[] aint = isidedinventory.getSlotsForFace(enumfacing);

            for(int i : aint) {
               if (pullItemFromSlot(hopper, iinventory, i, enumfacing)) {
                  return true;
               }
            }
         } else {
            int j = iinventory.getSizeInventory();

            for(int k = 0; k < j; ++k) {
               if (pullItemFromSlot(hopper, iinventory, k, enumfacing)) {
                  return true;
               }
            }
         }
      } else {
         for(EntityItem entityitem : getCaptureItems(hopper)) {
            if (captureItem(hopper, entityitem)) {
               return true;
            }
         }
      }

      return false;
   }

   /**
    * Pulls from the specified slot in the inventory and places in any available slot in the hopper. Returns true if the
    * entire stack was moved
    */
   private static boolean pullItemFromSlot(IHopper hopper, IInventory inventoryIn, int index, EnumFacing direction) {
      ItemStack itemstack = inventoryIn.getStackInSlot(index);
      if (!itemstack.isEmpty() && canExtractItemFromSlot(inventoryIn, itemstack, index, direction)) {
         ItemStack itemstack1 = itemstack.copy();
         ItemStack itemstack2 = putStackInInventoryAllSlots(inventoryIn, hopper, inventoryIn.decrStackSize(index, 1), (EnumFacing)null);
         if (itemstack2.isEmpty()) {
            inventoryIn.markDirty();
            return true;
         }

         inventoryIn.setInventorySlotContents(index, itemstack1);
      }

      return false;
   }

   public static boolean captureItem(IInventory p_200114_0_, EntityItem p_200114_1_) {
      boolean flag = false;
      ItemStack itemstack = p_200114_1_.getItem().copy();
      ItemStack itemstack1 = putStackInInventoryAllSlots((IInventory)null, p_200114_0_, itemstack, (EnumFacing)null);
      if (itemstack1.isEmpty()) {
         flag = true;
         p_200114_1_.remove();
      } else {
         p_200114_1_.setItem(itemstack1);
      }

      return flag;
   }

   /**
    * Attempts to place the passed stack in the inventory, using as many slots as required. Returns leftover items
    */
   public static ItemStack putStackInInventoryAllSlots(@Nullable IInventory source, IInventory destination, ItemStack stack, @Nullable EnumFacing direction) {
      if (destination instanceof ISidedInventory && direction != null) {
         ISidedInventory isidedinventory = (ISidedInventory)destination;
         int[] aint = isidedinventory.getSlotsForFace(direction);

         for(int k = 0; k < aint.length && !stack.isEmpty(); ++k) {
            stack = insertStack(source, destination, stack, aint[k], direction);
         }
      } else {
         int i = destination.getSizeInventory();

         for(int j = 0; j < i && !stack.isEmpty(); ++j) {
            stack = insertStack(source, destination, stack, j, direction);
         }
      }

      return stack;
   }

   /**
    * Can this hopper insert the specified item from the specified slot on the specified side?
    */
   private static boolean canInsertItemInSlot(IInventory inventoryIn, ItemStack stack, int index, @Nullable EnumFacing side) {
      if (!inventoryIn.isItemValidForSlot(index, stack)) {
         return false;
      } else {
         return !(inventoryIn instanceof ISidedInventory) || ((ISidedInventory)inventoryIn).canInsertItem(index, stack, side);
      }
   }

   /**
    * Can this hopper extract the specified item from the specified slot on the specified side?
    */
   private static boolean canExtractItemFromSlot(IInventory inventoryIn, ItemStack stack, int index, EnumFacing side) {
      return !(inventoryIn instanceof ISidedInventory) || ((ISidedInventory)inventoryIn).canExtractItem(index, stack, side);
   }

   /**
    * Insert the specified stack to the specified inventory and return any leftover items
    */
   private static ItemStack insertStack(@Nullable IInventory source, IInventory destination, ItemStack stack, int index, @Nullable EnumFacing direction) {
      ItemStack itemstack = destination.getStackInSlot(index);
      if (canInsertItemInSlot(destination, stack, index, direction)) {
         boolean flag = false;
         boolean flag1 = destination.isEmpty();
         if (itemstack.isEmpty()) {
            destination.setInventorySlotContents(index, stack);
            stack = ItemStack.EMPTY;
            flag = true;
         } else if (canCombine(itemstack, stack)) {
            int i = stack.getMaxStackSize() - itemstack.getCount();
            int j = Math.min(stack.getCount(), i);
            stack.shrink(j);
            itemstack.grow(j);
            flag = j > 0;
         }

         if (flag) {
            if (flag1 && destination instanceof TileEntityHopper) {
               TileEntityHopper tileentityhopper1 = (TileEntityHopper)destination;
               if (!tileentityhopper1.mayTransfer()) {
                  int k = 0;
                  if (source instanceof TileEntityHopper) {
                     TileEntityHopper tileentityhopper = (TileEntityHopper)source;
                     if (tileentityhopper1.tickedGameTime >= tileentityhopper.tickedGameTime) {
                        k = 1;
                     }
                  }

                  tileentityhopper1.setTransferCooldown(8 - k);
               }
            }

            destination.markDirty();
         }
      }

      return stack;
   }

   /**
    * Returns the IInventory that this hopper is pointing into
    */
   @Nullable
   private IInventory getInventoryForHopperTransfer() {
      EnumFacing enumfacing = this.getBlockState().get(BlockHopper.FACING);
      return getInventoryAtPosition(this.getWorld(), this.pos.offset(enumfacing));
   }

   /**
    * Gets the inventory that the provided hopper will transfer items from.
    */
   @Nullable
   public static IInventory getSourceInventory(IHopper hopper) {
      return getInventoryAtPosition(hopper.getWorld(), hopper.getXPos(), hopper.getYPos() + 1.0D, hopper.getZPos());
   }

   public static List<EntityItem> getCaptureItems(IHopper p_200115_0_) {
      return p_200115_0_.getCollectionArea().toBoundingBoxList().stream().flatMap((p_200110_1_) -> {
         return p_200115_0_.getWorld().getEntitiesWithinAABB(EntityItem.class, p_200110_1_.offset(p_200115_0_.getXPos() - 0.5D, p_200115_0_.getYPos() - 0.5D, p_200115_0_.getZPos() - 0.5D), EntitySelectors.IS_ALIVE).stream();
      }).collect(Collectors.toList());
   }

   @Nullable
   public static IInventory getInventoryAtPosition(World p_195484_0_, BlockPos p_195484_1_) {
      return getInventoryAtPosition(p_195484_0_, (double)p_195484_1_.getX() + 0.5D, (double)p_195484_1_.getY() + 0.5D, (double)p_195484_1_.getZ() + 0.5D);
   }

   /**
    * Returns the IInventory (if applicable) of the TileEntity at the specified position
    */
   @Nullable
   public static IInventory getInventoryAtPosition(World worldIn, double x, double y, double z) {
      IInventory iinventory = null;
      BlockPos blockpos = new BlockPos(x, y, z);
      IBlockState iblockstate = worldIn.getBlockState(blockpos);
      Block block = iblockstate.getBlock();
      if (iblockstate.hasTileEntity()) {
         TileEntity tileentity = worldIn.getTileEntity(blockpos);
         if (tileentity instanceof IInventory) {
            iinventory = (IInventory)tileentity;
            if (iinventory instanceof TileEntityChest && block instanceof BlockChest) {
               iinventory = ((BlockChest)block).getContainer(iblockstate, worldIn, blockpos, true);
            }
         }
      }

      if (iinventory == null) {
         List<Entity> list = worldIn.getEntitiesInAABBexcluding((Entity)null, new AxisAlignedBB(x - 0.5D, y - 0.5D, z - 0.5D, x + 0.5D, y + 0.5D, z + 0.5D), EntitySelectors.HAS_INVENTORY);
         if (!list.isEmpty()) {
            iinventory = (IInventory)list.get(worldIn.rand.nextInt(list.size()));
         }
      }

      return iinventory;
   }

   private static boolean canCombine(ItemStack stack1, ItemStack stack2) {
      if (stack1.getItem() != stack2.getItem()) {
         return false;
      } else if (stack1.getDamage() != stack2.getDamage()) {
         return false;
      } else if (stack1.getCount() > stack1.getMaxStackSize()) {
         return false;
      } else {
         return ItemStack.areItemStackTagsEqual(stack1, stack2);
      }
   }

   /**
    * Gets the world X position for this hopper entity.
    */
   public double getXPos() {
      return (double)this.pos.getX() + 0.5D;
   }

   /**
    * Gets the world Y position for this hopper entity.
    */
   public double getYPos() {
      return (double)this.pos.getY() + 0.5D;
   }

   /**
    * Gets the world Z position for this hopper entity.
    */
   public double getZPos() {
      return (double)this.pos.getZ() + 0.5D;
   }

   public void setTransferCooldown(int ticks) {
      this.transferCooldown = ticks;
   }

   private boolean isOnTransferCooldown() {
      return this.transferCooldown > 0;
   }

   public boolean mayTransfer() {
      return this.transferCooldown > 8;
   }

   public String getGuiID() {
      return "minecraft:hopper";
   }

   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      this.fillWithLoot(playerIn);
      return new ContainerHopper(playerInventory, this, playerIn);
   }

   protected NonNullList<ItemStack> getItems() {
      return this.inventory;
   }

   protected void setItems(NonNullList<ItemStack> itemsIn) {
      this.inventory = itemsIn;
   }

   public void onEntityCollision(Entity p_200113_1_) {
      if (p_200113_1_ instanceof EntityItem) {
         BlockPos blockpos = this.getPos();
         if (VoxelShapes.compare(VoxelShapes.create(p_200113_1_.getBoundingBox().offset((double)(-blockpos.getX()), (double)(-blockpos.getY()), (double)(-blockpos.getZ()))), this.getCollectionArea(), IBooleanFunction.AND)) {
            this.updateHopper(() -> {
               return captureItem(this, (EntityItem)p_200113_1_);
            });
         }
      }

   }

   @Override
   protected net.minecraftforge.items.IItemHandler createUnSidedHandler() {
      return new net.minecraftforge.items.VanillaHopperItemHandler(this);
   }

   public long getLastUpdateTime() {
      return this.tickedGameTime;
   }
}