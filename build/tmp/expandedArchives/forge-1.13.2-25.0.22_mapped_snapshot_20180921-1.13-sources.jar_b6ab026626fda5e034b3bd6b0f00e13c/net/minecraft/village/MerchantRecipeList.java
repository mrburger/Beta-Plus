package net.minecraft.village;

import java.io.IOException;
import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MerchantRecipeList extends ArrayList<MerchantRecipe> {
   public MerchantRecipeList() {
   }

   public MerchantRecipeList(NBTTagCompound compound) {
      this.read(compound);
   }

   /**
    * can par1,par2 be used to in crafting recipe par3
    */
   @Nullable
   public MerchantRecipe canRecipeBeUsed(ItemStack stack0, ItemStack stack1, int index) {
      if (index > 0 && index < this.size()) {
         MerchantRecipe merchantrecipe1 = this.get(index);
         return !this.areItemStacksExactlyEqual(stack0, merchantrecipe1.getItemToBuy()) || (!stack1.isEmpty() || merchantrecipe1.hasSecondItemToBuy()) && (!merchantrecipe1.hasSecondItemToBuy() || !this.areItemStacksExactlyEqual(stack1, merchantrecipe1.getSecondItemToBuy())) || stack0.getCount() < merchantrecipe1.getItemToBuy().getCount() || merchantrecipe1.hasSecondItemToBuy() && stack1.getCount() < merchantrecipe1.getSecondItemToBuy().getCount() ? null : merchantrecipe1;
      } else {
         for(int i = 0; i < this.size(); ++i) {
            MerchantRecipe merchantrecipe = this.get(i);
            if (this.areItemStacksExactlyEqual(stack0, merchantrecipe.getItemToBuy()) && stack0.getCount() >= merchantrecipe.getItemToBuy().getCount() && (!merchantrecipe.hasSecondItemToBuy() && stack1.isEmpty() || merchantrecipe.hasSecondItemToBuy() && this.areItemStacksExactlyEqual(stack1, merchantrecipe.getSecondItemToBuy()) && stack1.getCount() >= merchantrecipe.getSecondItemToBuy().getCount())) {
               return merchantrecipe;
            }
         }

         return null;
      }
   }

   private boolean areItemStacksExactlyEqual(ItemStack stack1, ItemStack stack2) {
      ItemStack itemstack = stack1.copy();
      if (itemstack.getItem().isDamageable()) {
         itemstack.setDamage(itemstack.getDamage());
      }

      return ItemStack.areItemsEqual(itemstack, stack2) && (!stack2.hasTag() || itemstack.hasTag() && NBTUtil.areNBTEquals(stack2.getTag(), itemstack.getTag(), false));
   }

   public void writeToBuf(PacketBuffer buffer) {
      buffer.writeByte((byte)(this.size() & 255));

      for(int i = 0; i < this.size(); ++i) {
         MerchantRecipe merchantrecipe = this.get(i);
         buffer.writeItemStack(merchantrecipe.getItemToBuy());
         buffer.writeItemStack(merchantrecipe.getItemToSell());
         ItemStack itemstack = merchantrecipe.getSecondItemToBuy();
         buffer.writeBoolean(!itemstack.isEmpty());
         if (!itemstack.isEmpty()) {
            buffer.writeItemStack(itemstack);
         }

         buffer.writeBoolean(merchantrecipe.isRecipeDisabled());
         buffer.writeInt(merchantrecipe.getToolUses());
         buffer.writeInt(merchantrecipe.getMaxTradeUses());
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static MerchantRecipeList readFromBuf(PacketBuffer buffer) throws IOException {
      MerchantRecipeList merchantrecipelist = new MerchantRecipeList();
      int i = buffer.readByte() & 255;

      for(int j = 0; j < i; ++j) {
         ItemStack itemstack = buffer.readItemStack();
         ItemStack itemstack1 = buffer.readItemStack();
         ItemStack itemstack2 = ItemStack.EMPTY;
         if (buffer.readBoolean()) {
            itemstack2 = buffer.readItemStack();
         }

         boolean flag = buffer.readBoolean();
         int k = buffer.readInt();
         int l = buffer.readInt();
         MerchantRecipe merchantrecipe = new MerchantRecipe(itemstack, itemstack2, itemstack1, k, l);
         if (flag) {
            merchantrecipe.compensateToolUses();
         }

         merchantrecipelist.add(merchantrecipe);
      }

      return merchantrecipelist;
   }

   public void read(NBTTagCompound compound) {
      NBTTagList nbttaglist = compound.getList("Recipes", 10);

      for(int i = 0; i < nbttaglist.size(); ++i) {
         NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);
         this.add(new MerchantRecipe(nbttagcompound));
      }

   }

   public NBTTagCompound write() {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      NBTTagList nbttaglist = new NBTTagList();

      for(int i = 0; i < this.size(); ++i) {
         MerchantRecipe merchantrecipe = this.get(i);
         nbttaglist.add((INBTBase)merchantrecipe.writeToTags());
      }

      nbttagcompound.setTag("Recipes", nbttaglist);
      return nbttagcompound;
   }
}