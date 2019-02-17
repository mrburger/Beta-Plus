package net.minecraft.village;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MerchantRecipe {
   /** Item the Villager buys. */
   private ItemStack itemToBuy = ItemStack.EMPTY;
   /** Second Item the Villager buys. */
   private ItemStack secondItemToBuy = ItemStack.EMPTY;
   /** Item the Villager sells. */
   private ItemStack itemToSell = ItemStack.EMPTY;
   /** Saves how much has been tool used when put into to slot to be enchanted. */
   private int toolUses;
   /** Maximum times this trade can be used. */
   private int maxTradeUses;
   private boolean rewardsExp;

   public MerchantRecipe(NBTTagCompound tagCompound) {
      this.readFromTags(tagCompound);
   }

   public MerchantRecipe(ItemStack buy1, ItemStack buy2, ItemStack sell) {
      this(buy1, buy2, sell, 0, 7);
   }

   public MerchantRecipe(ItemStack buy1, ItemStack buy2, ItemStack sell, int toolUsesIn, int maxTradeUsesIn) {
      this.itemToBuy = buy1;
      this.secondItemToBuy = buy2;
      this.itemToSell = sell;
      this.toolUses = toolUsesIn;
      this.maxTradeUses = maxTradeUsesIn;
      this.rewardsExp = true;
   }

   public MerchantRecipe(ItemStack buy1, ItemStack sell) {
      this(buy1, ItemStack.EMPTY, sell);
   }

   public MerchantRecipe(ItemStack buy1, Item sellItem) {
      this(buy1, new ItemStack(sellItem));
   }

   /**
    * Gets the itemToBuy.
    */
   public ItemStack getItemToBuy() {
      return this.itemToBuy;
   }

   /**
    * Gets secondItemToBuy.
    */
   public ItemStack getSecondItemToBuy() {
      return this.secondItemToBuy;
   }

   /**
    * Gets if Villager has secondItemToBuy.
    */
   public boolean hasSecondItemToBuy() {
      return !this.secondItemToBuy.isEmpty();
   }

   /**
    * Gets itemToSell.
    */
   public ItemStack getItemToSell() {
      return this.itemToSell;
   }

   public int getToolUses() {
      return this.toolUses;
   }

   public int getMaxTradeUses() {
      return this.maxTradeUses;
   }

   public void incrementToolUses() {
      ++this.toolUses;
   }

   public void increaseMaxTradeUses(int increment) {
      this.maxTradeUses += increment;
   }

   public boolean isRecipeDisabled() {
      return this.toolUses >= this.maxTradeUses;
   }

   /**
    * Compensates {@link net.minecraft.village.MerchantRecipe#toolUses toolUses} with {@link
    * net.minecraft.village.MerchantRecipe#maxTradeUses maxTradeUses}
    */
   @OnlyIn(Dist.CLIENT)
   public void compensateToolUses() {
      this.toolUses = this.maxTradeUses;
   }

   public boolean getRewardsExp() {
      return this.rewardsExp;
   }

   public void readFromTags(NBTTagCompound tagCompound) {
      NBTTagCompound nbttagcompound = tagCompound.getCompound("buy");
      this.itemToBuy = ItemStack.read(nbttagcompound);
      NBTTagCompound nbttagcompound1 = tagCompound.getCompound("sell");
      this.itemToSell = ItemStack.read(nbttagcompound1);
      if (tagCompound.contains("buyB", 10)) {
         this.secondItemToBuy = ItemStack.read(tagCompound.getCompound("buyB"));
      }

      if (tagCompound.contains("uses", 99)) {
         this.toolUses = tagCompound.getInt("uses");
      }

      if (tagCompound.contains("maxUses", 99)) {
         this.maxTradeUses = tagCompound.getInt("maxUses");
      } else {
         this.maxTradeUses = 7;
      }

      if (tagCompound.contains("rewardExp", 1)) {
         this.rewardsExp = tagCompound.getBoolean("rewardExp");
      } else {
         this.rewardsExp = true;
      }

   }

   public NBTTagCompound writeToTags() {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.setTag("buy", this.itemToBuy.write(new NBTTagCompound()));
      nbttagcompound.setTag("sell", this.itemToSell.write(new NBTTagCompound()));
      if (!this.secondItemToBuy.isEmpty()) {
         nbttagcompound.setTag("buyB", this.secondItemToBuy.write(new NBTTagCompound()));
      }

      nbttagcompound.setInt("uses", this.toolUses);
      nbttagcompound.setInt("maxUses", this.maxTradeUses);
      nbttagcompound.setBoolean("rewardExp", this.rewardsExp);
      return nbttagcompound;
   }
}