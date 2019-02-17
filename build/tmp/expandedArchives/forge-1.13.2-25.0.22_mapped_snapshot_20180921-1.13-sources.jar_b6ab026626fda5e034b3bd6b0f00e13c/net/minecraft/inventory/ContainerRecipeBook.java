package net.minecraft.inventory;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class ContainerRecipeBook extends Container {
   public abstract void func_201771_a(RecipeItemHelper p_201771_1_);

   public abstract void clear();

   public abstract boolean matches(IRecipe p_201769_1_);

   public abstract int getOutputSlot();

   public abstract int getWidth();

   public abstract int getHeight();

   @OnlyIn(Dist.CLIENT)
   public abstract int getSize();
}