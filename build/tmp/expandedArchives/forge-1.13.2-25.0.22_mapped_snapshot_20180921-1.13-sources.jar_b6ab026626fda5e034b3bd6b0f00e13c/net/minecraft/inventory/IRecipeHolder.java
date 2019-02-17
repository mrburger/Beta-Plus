package net.minecraft.inventory;

import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public interface IRecipeHolder {
   void setRecipeUsed(@Nullable IRecipe recipe);

   @Nullable
   IRecipe getRecipeUsed();

   default void onCrafting(EntityPlayer player) {
      IRecipe irecipe = this.getRecipeUsed();
      if (irecipe != null && !irecipe.isDynamic()) {
         player.unlockRecipes(Lists.newArrayList(irecipe));
         this.setRecipeUsed((IRecipe)null);
      }

   }

   default boolean canUseRecipe(World worldIn, EntityPlayerMP player, @Nullable IRecipe recipe) {
      if (recipe == null || !recipe.isDynamic() && worldIn.getGameRules().getBoolean("doLimitedCrafting") && !player.getRecipeBook().isUnlocked(recipe)) {
         return false;
      } else {
         this.setRecipeUsed(recipe);
         return true;
      }
   }
}