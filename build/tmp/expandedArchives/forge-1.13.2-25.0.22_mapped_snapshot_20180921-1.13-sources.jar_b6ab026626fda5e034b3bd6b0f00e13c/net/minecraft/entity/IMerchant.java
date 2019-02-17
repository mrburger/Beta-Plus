package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IMerchant {
   void setCustomer(@Nullable EntityPlayer player);

   @Nullable
   EntityPlayer getCustomer();

   @Nullable
   MerchantRecipeList getRecipes(EntityPlayer player);

   @OnlyIn(Dist.CLIENT)
   void setRecipes(@Nullable MerchantRecipeList recipeList);

   void useRecipe(MerchantRecipe recipe);

   /**
    * Notifies the merchant of a possible merchantrecipe being fulfilled or not. Usually, this is just a sound byte
    * being played depending if the suggested itemstack is not null.
    */
   void verifySellingItem(ItemStack stack);

   ITextComponent getDisplayName();

   World getWorld();

   BlockPos getPos();
}