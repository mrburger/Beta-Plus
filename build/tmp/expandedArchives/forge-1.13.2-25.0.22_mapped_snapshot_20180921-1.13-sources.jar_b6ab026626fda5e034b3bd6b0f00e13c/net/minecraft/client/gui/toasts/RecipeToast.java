package net.minecraft.client.gui.toasts;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RecipeToast implements IToast {
   /** All recipes to display. */
   private final List<IRecipe> recipes = Lists.newArrayList();
   private long firstDrawTime;
   private boolean hasNewOutputs;

   public RecipeToast(IRecipe recipeIn) {
      this.recipes.add(recipeIn);
   }

   public IToast.Visibility draw(GuiToast toastGui, long delta) {
      if (this.hasNewOutputs) {
         this.firstDrawTime = delta;
         this.hasNewOutputs = false;
      }

      if (this.recipes.isEmpty()) {
         return IToast.Visibility.HIDE;
      } else {
         toastGui.getMinecraft().getTextureManager().bindTexture(TEXTURE_TOASTS);
         GlStateManager.color3f(1.0F, 1.0F, 1.0F);
         toastGui.drawTexturedModalRect(0, 0, 0, 32, 160, 32);
         toastGui.getMinecraft().fontRenderer.drawString(I18n.format("recipe.toast.title"), 30.0F, 7.0F, -11534256);
         toastGui.getMinecraft().fontRenderer.drawString(I18n.format("recipe.toast.description"), 30.0F, 18.0F, -16777216);
         RenderHelper.enableGUIStandardItemLighting();
         IRecipe irecipe = this.recipes.get((int)((delta * (long)this.recipes.size() / 5000L) % (long)this.recipes.size()));  //Forge: fix math so that it doesn't divide by 0 when there are more than 5000 recipes
         ItemStack itemstack;
         if (irecipe instanceof FurnaceRecipe) {
            itemstack = new ItemStack(Blocks.FURNACE);
         } else {
            itemstack = new ItemStack(Blocks.CRAFTING_TABLE);
         }

         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.6F, 0.6F, 1.0F);
         toastGui.getMinecraft().getItemRenderer().renderItemAndEffectIntoGUI((EntityLivingBase)null, itemstack, 3, 3);
         GlStateManager.popMatrix();
         toastGui.getMinecraft().getItemRenderer().renderItemAndEffectIntoGUI((EntityLivingBase)null, irecipe.getRecipeOutput(), 8, 8);
         return delta - this.firstDrawTime >= 5000L ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
      }
   }

   public void addRecipe(IRecipe recipeIn) {
      if (this.recipes.add(recipeIn)) {
         this.hasNewOutputs = true;
      }

   }

   public static void addOrUpdate(GuiToast toastGui, IRecipe recipeIn) {
      RecipeToast recipetoast = toastGui.getToast(RecipeToast.class, NO_TOKEN);
      if (recipetoast == null) {
         toastGui.add(new RecipeToast(recipeIn));
      } else {
         recipetoast.addRecipe(recipeIn);
      }

   }
}