package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.BlockFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IRecipeHelperPopulator;
import net.minecraft.inventory.IRecipeHolder;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityFurnace extends TileEntityLockable implements ISidedInventory, IRecipeHolder, IRecipeHelperPopulator, ITickable {
   private static final int[] SLOTS_TOP = new int[]{0};
   private static final int[] SLOTS_BOTTOM = new int[]{2, 1};
   private static final int[] SLOTS_SIDES = new int[]{1};
   /** The ItemStacks that hold the items currently being used in the furnace */
   private NonNullList<ItemStack> furnaceItemStacks = NonNullList.withSize(3, ItemStack.EMPTY);
   /** The number of ticks that the furnace will keep burning */
   private int furnaceBurnTime;
   /** The number of ticks that a fresh copy of the currently-burning item would keep the furnace burning for */
   private int currentItemBurnTime;
   private int cookTime;
   private int totalCookTime;
   private ITextComponent furnaceCustomName;
   private final Map<ResourceLocation, Integer> recipeUseCounts = Maps.newHashMap();

   private static void setBurnTime(Map<Item, Integer> map, Tag<Item> tagIn, int time) {
      for(Item item : tagIn.getAllElements()) {
         map.put(item, time);
      }

   }

   private static void setBurnTime(Map<Item, Integer> map, IItemProvider itemProvider, int time) {
      map.put(itemProvider.asItem(), time);
   }

   public static Map<Item, Integer> getBurnTimes() {
      Map<Item, Integer> map = Maps.newLinkedHashMap();
      setBurnTime(map, Items.LAVA_BUCKET, 20000);
      setBurnTime(map, Blocks.COAL_BLOCK, 16000);
      setBurnTime(map, Items.BLAZE_ROD, 2400);
      setBurnTime(map, Items.COAL, 1600);
      setBurnTime(map, Items.CHARCOAL, 1600);
      setBurnTime(map, ItemTags.LOGS, 300);
      setBurnTime(map, ItemTags.PLANKS, 300);
      setBurnTime(map, ItemTags.WOODEN_STAIRS, 300);
      setBurnTime(map, ItemTags.WOODEN_SLABS, 150);
      setBurnTime(map, ItemTags.WOODEN_TRAPDOORS, 300);
      setBurnTime(map, ItemTags.WOODEN_PRESSURE_PLATES, 300);
      setBurnTime(map, Blocks.OAK_FENCE, 300);
      setBurnTime(map, Blocks.BIRCH_FENCE, 300);
      setBurnTime(map, Blocks.SPRUCE_FENCE, 300);
      setBurnTime(map, Blocks.JUNGLE_FENCE, 300);
      setBurnTime(map, Blocks.DARK_OAK_FENCE, 300);
      setBurnTime(map, Blocks.ACACIA_FENCE, 300);
      setBurnTime(map, Blocks.OAK_FENCE_GATE, 300);
      setBurnTime(map, Blocks.BIRCH_FENCE_GATE, 300);
      setBurnTime(map, Blocks.SPRUCE_FENCE_GATE, 300);
      setBurnTime(map, Blocks.JUNGLE_FENCE_GATE, 300);
      setBurnTime(map, Blocks.DARK_OAK_FENCE_GATE, 300);
      setBurnTime(map, Blocks.ACACIA_FENCE_GATE, 300);
      setBurnTime(map, Blocks.NOTE_BLOCK, 300);
      setBurnTime(map, Blocks.BOOKSHELF, 300);
      setBurnTime(map, Blocks.JUKEBOX, 300);
      setBurnTime(map, Blocks.CHEST, 300);
      setBurnTime(map, Blocks.TRAPPED_CHEST, 300);
      setBurnTime(map, Blocks.CRAFTING_TABLE, 300);
      setBurnTime(map, Blocks.DAYLIGHT_DETECTOR, 300);
      setBurnTime(map, ItemTags.BANNERS, 300);
      setBurnTime(map, Items.BOW, 300);
      setBurnTime(map, Items.FISHING_ROD, 300);
      setBurnTime(map, Blocks.LADDER, 300);
      setBurnTime(map, Items.SIGN, 200);
      setBurnTime(map, Items.WOODEN_SHOVEL, 200);
      setBurnTime(map, Items.WOODEN_SWORD, 200);
      setBurnTime(map, Items.WOODEN_HOE, 200);
      setBurnTime(map, Items.WOODEN_AXE, 200);
      setBurnTime(map, Items.WOODEN_PICKAXE, 200);
      setBurnTime(map, ItemTags.WOODEN_DOORS, 200);
      setBurnTime(map, ItemTags.BOATS, 200);
      setBurnTime(map, ItemTags.WOOL, 100);
      setBurnTime(map, ItemTags.WOODEN_BUTTONS, 100);
      setBurnTime(map, Items.STICK, 100);
      setBurnTime(map, ItemTags.SAPLINGS, 100);
      setBurnTime(map, Items.BOWL, 100);
      setBurnTime(map, ItemTags.CARPETS, 67);
      setBurnTime(map, Blocks.DRIED_KELP_BLOCK, 4001);
      return map;
   }

   public TileEntityFurnace() {
      super(TileEntityType.FURNACE);
   }

   /**
    * Returns the number of slots in the inventory.
    */
   public int getSizeInventory() {
      return this.furnaceItemStacks.size();
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.furnaceItemStacks) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   /**
    * Returns the stack in the given slot.
    */
   public ItemStack getStackInSlot(int index) {
      return this.furnaceItemStacks.get(index);
   }

   /**
    * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
    */
   public ItemStack decrStackSize(int index, int count) {
      return ItemStackHelper.getAndSplit(this.furnaceItemStacks, index, count);
   }

   /**
    * Removes a stack from the given slot and returns it.
    */
   public ItemStack removeStackFromSlot(int index) {
      return ItemStackHelper.getAndRemove(this.furnaceItemStacks, index);
   }

   /**
    * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
    */
   public void setInventorySlotContents(int index, ItemStack stack) {
      ItemStack itemstack = this.furnaceItemStacks.get(index);
      boolean flag = !stack.isEmpty() && stack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(stack, itemstack);
      this.furnaceItemStacks.set(index, stack);
      if (stack.getCount() > this.getInventoryStackLimit()) {
         stack.setCount(this.getInventoryStackLimit());
      }

      if (index == 0 && !flag) {
         this.totalCookTime = this.getCookTime();
         this.cookTime = 0;
         this.markDirty();
      }

   }

   public ITextComponent getName() {
      return (ITextComponent)(this.furnaceCustomName != null ? this.furnaceCustomName : new TextComponentTranslation("container.furnace"));
   }

   public boolean hasCustomName() {
      return this.furnaceCustomName != null;
   }

   @Nullable
   public ITextComponent getCustomName() {
      return this.furnaceCustomName;
   }

   public void setCustomName(@Nullable ITextComponent name) {
      this.furnaceCustomName = name;
   }

   public void read(NBTTagCompound compound) {
      super.read(compound);
      this.furnaceItemStacks = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
      ItemStackHelper.loadAllItems(compound, this.furnaceItemStacks);
      this.furnaceBurnTime = compound.getInt("BurnTime");
      this.cookTime = compound.getInt("CookTime");
      this.totalCookTime = compound.getInt("CookTimeTotal");
      this.currentItemBurnTime = getItemBurnTime(this.furnaceItemStacks.get(1));
      int i = compound.getShort("RecipesUsedSize");

      for(int j = 0; j < i; ++j) {
         ResourceLocation resourcelocation = new ResourceLocation(compound.getString("RecipeLocation" + j));
         int k = compound.getInt("RecipeAmount" + j);
         this.recipeUseCounts.put(resourcelocation, k);
      }

      if (compound.contains("CustomName", 8)) {
         this.furnaceCustomName = ITextComponent.Serializer.fromJson(compound.getString("CustomName"));
      }

   }

   public NBTTagCompound write(NBTTagCompound compound) {
      super.write(compound);
      compound.setInt("BurnTime", this.furnaceBurnTime);
      compound.setInt("CookTime", this.cookTime);
      compound.setInt("CookTimeTotal", this.totalCookTime);
      ItemStackHelper.saveAllItems(compound, this.furnaceItemStacks);
      compound.setShort("RecipesUsedSize", (short)this.recipeUseCounts.size());
      int i = 0;

      for(Entry<ResourceLocation, Integer> entry : this.recipeUseCounts.entrySet()) {
         compound.setString("RecipeLocation" + i, entry.getKey().toString());
         compound.setInt("RecipeAmount" + i, entry.getValue());
         ++i;
      }

      if (this.furnaceCustomName != null) {
         compound.setString("CustomName", ITextComponent.Serializer.toJson(this.furnaceCustomName));
      }

      return compound;
   }

   /**
    * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
    */
   public int getInventoryStackLimit() {
      return 64;
   }

   /**
    * Furnace isBurning
    */
   private boolean isBurning() {
      return this.furnaceBurnTime > 0;
   }

   @OnlyIn(Dist.CLIENT)
   public static boolean isBurning(IInventory inventory) {
      return inventory.getField(0) > 0;
   }

   public void tick() {
      boolean flag = this.isBurning();
      boolean flag1 = false;
      if (this.isBurning()) {
         --this.furnaceBurnTime;
      }

      if (!this.world.isRemote) {
         ItemStack itemstack = this.furnaceItemStacks.get(1);
         if (this.isBurning() || !itemstack.isEmpty() && !this.furnaceItemStacks.get(0).isEmpty()) {
            IRecipe irecipe = this.world.getRecipeManager().getRecipe(this, this.world, net.minecraftforge.common.crafting.VanillaRecipeTypes.SMELTING);
            if (!this.isBurning() && this.canSmelt(irecipe)) {
               this.furnaceBurnTime = getItemBurnTime(itemstack);
               this.currentItemBurnTime = this.furnaceBurnTime;
               if (this.isBurning()) {
                  flag1 = true;
                  if (itemstack.hasContainerItem()) {
                     this.furnaceItemStacks.set(1, itemstack.getContainerItem());
                  }
                  else
                  if (!itemstack.isEmpty()) {
                     Item item = itemstack.getItem();
                     itemstack.shrink(1);
                     if (itemstack.isEmpty()) {
                        Item item1 = item.getContainerItem();
                        this.furnaceItemStacks.set(1, item1 == null ? ItemStack.EMPTY : new ItemStack(item1));
                     }
                  }
               }
            }

            if (this.isBurning() && this.canSmelt(irecipe)) {
               ++this.cookTime;
               if (this.cookTime == this.totalCookTime) {
                  this.cookTime = 0;
                  this.totalCookTime = this.getCookTime();
                  this.smeltItem(irecipe);
                  flag1 = true;
               }
            } else {
               this.cookTime = 0;
            }
         } else if (!this.isBurning() && this.cookTime > 0) {
            this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.totalCookTime);
         }

         if (flag != this.isBurning()) {
            flag1 = true;
            this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(BlockFurnace.LIT, Boolean.valueOf(this.isBurning())), 3);
         }
      }

      if (flag1) {
         this.markDirty();
      }

   }

   private int getCookTime() {
      FurnaceRecipe furnacerecipe = this.world.getRecipeManager().getRecipe(this, this.world, net.minecraftforge.common.crafting.VanillaRecipeTypes.SMELTING);
      return furnacerecipe != null ? furnacerecipe.getCookingTime() : 200;
   }

   private boolean canSmelt(@Nullable IRecipe recipe) {
      if (!this.furnaceItemStacks.get(0).isEmpty() && recipe != null) {
         ItemStack itemstack = recipe.getRecipeOutput();
         if (itemstack.isEmpty()) {
            return false;
         } else {
            ItemStack itemstack1 = this.furnaceItemStacks.get(2);
            if (itemstack1.isEmpty()) {
               return true;
            } else if (!itemstack1.isItemEqual(itemstack)) {
               return false;
            } else if (itemstack1.getCount() + itemstack.getCount() <= this.getInventoryStackLimit() && itemstack1.getCount() < itemstack1.getMaxStackSize()) { // Forge fix: make furnace respect stack sizes in furnace recipes
               return true;
            } else {
               return itemstack1.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize(); // Forge fix: make furnace respect stack sizes in furnace recipes
            }
         }
      } else {
         return false;
      }
   }

   private void smeltItem(@Nullable IRecipe recipe) {
      if (recipe != null && this.canSmelt(recipe)) {
         ItemStack itemstack = this.furnaceItemStacks.get(0);
         ItemStack itemstack1 = recipe.getRecipeOutput();
         ItemStack itemstack2 = this.furnaceItemStacks.get(2);
         if (itemstack2.isEmpty()) {
            this.furnaceItemStacks.set(2, itemstack1.copy());
         } else if (itemstack2.getItem() == itemstack1.getItem()) {
            itemstack2.grow(itemstack1.getCount());
         }

         if (!this.world.isRemote) {
            this.canUseRecipe(this.world, (EntityPlayerMP)null, recipe);
         }

         if (itemstack.getItem() == Blocks.WET_SPONGE.asItem() && !this.furnaceItemStacks.get(1).isEmpty() && this.furnaceItemStacks.get(1).getItem() == Items.BUCKET) {
            this.furnaceItemStacks.set(1, new ItemStack(Items.WATER_BUCKET));
         }

         itemstack.shrink(1);
      }
   }

   /**
    * Returns the number of ticks that the supplied fuel item will keep the furnace burning, or 0 if the item isn't fuel
    */
   private static int getItemBurnTime(ItemStack stack) {
      if (stack.isEmpty()) {
         return 0;
      } else {
         Item item = stack.getItem();
         int ret = stack.getBurnTime();
         return net.minecraftforge.event.ForgeEventFactory.getItemBurnTime(stack, ret == -1 ? getBurnTimes().getOrDefault(item, 0) : ret);
      }
   }

   public static boolean isItemFuel(ItemStack stack) {
      return getItemBurnTime(stack) > 0;
   }

   /**
    * Don't rename this method to canInteractWith due to conflicts with Container
    */
   public boolean isUsableByPlayer(EntityPlayer player) {
      if (this.world.getTileEntity(this.pos) != this) {
         return false;
      } else {
         return !(player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) > 64.0D);
      }
   }

   public void openInventory(EntityPlayer player) {
   }

   public void closeInventory(EntityPlayer player) {
   }

   /**
    * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot. For
    * guis use Slot.isItemValid
    */
   public boolean isItemValidForSlot(int index, ItemStack stack) {
      if (index == 2) {
         return false;
      } else if (index != 1) {
         return true;
      } else {
         ItemStack itemstack = this.furnaceItemStacks.get(1);
         return isItemFuel(stack) || SlotFurnaceFuel.isBucket(stack) && itemstack.getItem() != Items.BUCKET;
      }
   }

   public int[] getSlotsForFace(EnumFacing side) {
      if (side == EnumFacing.DOWN) {
         return SLOTS_BOTTOM;
      } else {
         return side == EnumFacing.UP ? SLOTS_TOP : SLOTS_SIDES;
      }
   }

   /**
    * Returns true if automation can insert the given item in the given slot from the given side.
    */
   public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable EnumFacing direction) {
      return this.isItemValidForSlot(index, itemStackIn);
   }

   /**
    * Returns true if automation can extract the given item in the given slot from the given side.
    */
   public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
      if (direction == EnumFacing.DOWN && index == 1) {
         Item item = stack.getItem();
         if (item != Items.WATER_BUCKET && item != Items.BUCKET) {
            return false;
         }
      }

      return true;
   }

   public String getGuiID() {
      return "minecraft:furnace";
   }

   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      return new ContainerFurnace(playerInventory, this);
   }

   public int getField(int id) {
      switch(id) {
      case 0:
         return this.furnaceBurnTime;
      case 1:
         return this.currentItemBurnTime;
      case 2:
         return this.cookTime;
      case 3:
         return this.totalCookTime;
      default:
         return 0;
      }
   }

   public void setField(int id, int value) {
      switch(id) {
      case 0:
         this.furnaceBurnTime = value;
         break;
      case 1:
         this.currentItemBurnTime = value;
         break;
      case 2:
         this.cookTime = value;
         break;
      case 3:
         this.totalCookTime = value;
      }

   }

   public int getFieldCount() {
      return 4;
   }

   public void clear() {
      this.furnaceItemStacks.clear();
   }

   public void fillStackedContents(RecipeItemHelper helper) {
      for(ItemStack itemstack : this.furnaceItemStacks) {
         helper.accountStack(itemstack);
      }

   }

   public void setRecipeUsed(IRecipe recipe) {
      if (this.recipeUseCounts.containsKey(recipe.getId())) {
         this.recipeUseCounts.put(recipe.getId(), this.recipeUseCounts.get(recipe.getId()) + 1);
      } else {
         this.recipeUseCounts.put(recipe.getId(), 1);
      }

   }

   @Nullable
   public IRecipe getRecipeUsed() {
      return null;
   }

   public Map<ResourceLocation, Integer> getRecipeUseCounts() {
      return this.recipeUseCounts;
   }

   public boolean canUseRecipe(World worldIn, EntityPlayerMP player, @Nullable IRecipe recipe) {
      if (recipe != null) {
         this.setRecipeUsed(recipe);
         return true;
      } else {
         return false;
      }
   }

   public void onCrafting(EntityPlayer player) {
      if (!this.world.getGameRules().getBoolean("doLimitedCrafting")) {
         List<IRecipe> list = Lists.newArrayList();

         for(ResourceLocation resourcelocation : this.recipeUseCounts.keySet()) {
            IRecipe irecipe = player.world.getRecipeManager().getRecipe(resourcelocation);
            if (irecipe != null) {
               list.add(irecipe);
            }
         }

         player.unlockRecipes(list);
      }

      this.recipeUseCounts.clear();
   }

   net.minecraftforge.common.util.LazyOptional<? extends net.minecraftforge.items.IItemHandler>[] handlers =
           net.minecraftforge.items.wrapper.SidedInvWrapper.create(this, EnumFacing.UP, EnumFacing.DOWN, EnumFacing.NORTH);

   @Override
   public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable EnumFacing facing) {
      if (!this.removed && facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
         if (facing == EnumFacing.UP)
            return handlers[0].cast();
         else if (facing == EnumFacing.DOWN)
            return handlers[1].cast();
         else
            return handlers[2].cast();
      }
      return super.getCapability(capability, facing);
   }

   /**
    * invalidates a tile entity
    */
   @Override
   public void remove() {
      super.remove();
      for (int x = 0; x < handlers.length; x++)
        handlers[x].invalidate();
   }
}