package net.minecraft.client.gui.recipebook;

import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButtonToggle;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.util.RecipeBookCategories;
import net.minecraft.client.util.RecipeBookClient;
import net.minecraft.inventory.ContainerRecipeBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiButtonRecipeTab extends GuiButtonToggle {
   private final RecipeBookCategories category;
   private float animationTime;

   public GuiButtonRecipeTab(int p_i48773_1_, RecipeBookCategories p_i48773_2_) {
      super(p_i48773_1_, 0, 0, 35, 27, false);
      this.category = p_i48773_2_;
      this.initTextureValues(153, 2, 35, 0, GuiRecipeBook.RECIPE_BOOK);
   }

   public void startAnimation(Minecraft p_193918_1_) {
      RecipeBookClient recipebookclient = p_193918_1_.player.getRecipeBook();
      List<RecipeList> list = recipebookclient.getRecipes(this.category);
      if (p_193918_1_.player.openContainer instanceof ContainerRecipeBook) {
         label25:
         for(RecipeList recipelist : list) {
            Iterator iterator = recipelist.getRecipes(recipebookclient.func_203432_a((ContainerRecipeBook)p_193918_1_.player.openContainer)).iterator();

            while(true) {
               if (!iterator.hasNext()) {
                  continue label25;
               }

               IRecipe irecipe = (IRecipe)iterator.next();
               if (recipebookclient.isNew(irecipe)) {
                  break;
               }
            }

            this.animationTime = 15.0F;
            return;
         }

      }
   }

   public void render(int mouseX, int mouseY, float partialTicks) {
      if (this.visible) {
         if (this.animationTime > 0.0F) {
            float f = 1.0F + 0.1F * (float)Math.sin((double)(this.animationTime / 15.0F * (float)Math.PI));
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)(this.x + 8), (float)(this.y + 12), 0.0F);
            GlStateManager.scalef(1.0F, f, 1.0F);
            GlStateManager.translatef((float)(-(this.x + 8)), (float)(-(this.y + 12)), 0.0F);
         }

         this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
         Minecraft minecraft = Minecraft.getInstance();
         minecraft.getTextureManager().bindTexture(this.resourceLocation);
         GlStateManager.disableDepthTest();
         int i = this.xTexStart;
         int j = this.yTexStart;
         if (this.stateTriggered) {
            i += this.xDiffTex;
         }

         if (this.hovered) {
            j += this.yDiffTex;
         }

         int k = this.x;
         if (this.stateTriggered) {
            k -= 2;
         }

         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.drawTexturedModalRect(k, this.y, i, j, this.width, this.height);
         GlStateManager.enableDepthTest();
         RenderHelper.enableGUIStandardItemLighting();
         GlStateManager.disableLighting();
         this.renderIcon(minecraft.getItemRenderer());
         GlStateManager.enableLighting();
         RenderHelper.disableStandardItemLighting();
         if (this.animationTime > 0.0F) {
            GlStateManager.popMatrix();
            this.animationTime -= partialTicks;
         }

      }
   }

   private void renderIcon(ItemRenderer p_193920_1_) {
      List<ItemStack> list = this.category.getIcons();
      int i = this.stateTriggered ? -2 : 0;
      if (list.size() == 1) {
         p_193920_1_.renderItemAndEffectIntoGUI(list.get(0), this.x + 9 + i, this.y + 5);
      } else if (list.size() == 2) {
         p_193920_1_.renderItemAndEffectIntoGUI(list.get(0), this.x + 3 + i, this.y + 5);
         p_193920_1_.renderItemAndEffectIntoGUI(list.get(1), this.x + 14 + i, this.y + 5);
      }

   }

   public RecipeBookCategories func_201503_d() {
      return this.category;
   }

   public boolean func_199500_a(RecipeBookClient p_199500_1_) {
      List<RecipeList> list = p_199500_1_.getRecipes(this.category);
      this.visible = false;
      if (list != null) {
         for(RecipeList recipelist : list) {
            if (recipelist.isNotEmpty() && recipelist.containsValidRecipes()) {
               this.visible = true;
               break;
            }
         }
      }

      return this.visible;
   }
}