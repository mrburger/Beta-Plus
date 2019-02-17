package net.minecraft.client.gui.inventory;

import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.recipebook.GuiFurnaceRecipeBook;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.ContainerRecipeBook;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiFurnace extends GuiContainer implements IRecipeShownListener {
   private static final ResourceLocation FURNACE_GUI_TEXTURES = new ResourceLocation("textures/gui/container/furnace.png");
   private static final ResourceLocation RECIPE_BUTTON_TEXTURE = new ResourceLocation("textures/gui/recipe_button.png");
   /** The player inventory bound to this GUI. */
   private final InventoryPlayer playerInventory;
   private final IInventory tileFurnace;
   public final GuiFurnaceRecipeBook recipeBook = new GuiFurnaceRecipeBook();
   private boolean canRenderRecipeBook;

   public GuiFurnace(InventoryPlayer playerInv, IInventory furnaceInv) {
      super(new ContainerFurnace(playerInv, furnaceInv));
      this.playerInventory = playerInv;
      this.tileFurnace = furnaceInv;
   }

   /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
   public void initGui() {
      super.initGui();
      this.canRenderRecipeBook = this.width < 379;
      this.recipeBook.func_201520_a(this.width, this.height, this.mc, this.canRenderRecipeBook, (ContainerRecipeBook)this.inventorySlots);
      this.guiLeft = this.recipeBook.updateScreenPosition(this.canRenderRecipeBook, this.width, this.xSize);
      this.addButton(new GuiButtonImage(10, this.guiLeft + 20, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiFurnace.this.recipeBook.func_201518_a(GuiFurnace.this.canRenderRecipeBook);
            GuiFurnace.this.recipeBook.toggleVisibility();
            GuiFurnace.this.guiLeft = GuiFurnace.this.recipeBook.updateScreenPosition(GuiFurnace.this.canRenderRecipeBook, GuiFurnace.this.width, GuiFurnace.this.xSize);
            this.setPosition(GuiFurnace.this.guiLeft + 20, GuiFurnace.this.height / 2 - 49);
         }
      });
   }

   /**
    * Called from the main game loop to update the screen.
    */
   public void tick() {
      super.tick();
      this.recipeBook.tick();
   }

   /**
    * Draws the screen and all the components in it.
    */
   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      if (this.recipeBook.isVisible() && this.canRenderRecipeBook) {
         this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
         this.recipeBook.render(mouseX, mouseY, partialTicks);
      } else {
         this.recipeBook.render(mouseX, mouseY, partialTicks);
         super.render(mouseX, mouseY, partialTicks);
         this.recipeBook.renderGhostRecipe(this.guiLeft, this.guiTop, true, partialTicks);
      }

      this.renderHoveredToolTip(mouseX, mouseY);
      this.recipeBook.renderTooltip(this.guiLeft, this.guiTop, mouseX, mouseY);
   }

   /**
    * Draw the foreground layer for the GuiContainer (everything in front of the items)
    */
   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      String s = this.tileFurnace.getDisplayName().getFormattedText();
      this.fontRenderer.drawString(s, (float)(this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2), 6.0F, 4210752);
      this.fontRenderer.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
   }

   /**
    * Draws the background layer of this container (behind the items).
    */
   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(FURNACE_GUI_TEXTURES);
      int i = this.guiLeft;
      int j = this.guiTop;
      this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
      if (TileEntityFurnace.isBurning(this.tileFurnace)) {
         int k = this.getBurnLeftScaled(13);
         this.drawTexturedModalRect(i + 56, j + 36 + 12 - k, 176, 12 - k, 14, k + 1);
      }

      int l = this.getCookProgressScaled(24);
      this.drawTexturedModalRect(i + 79, j + 34, 176, 14, l + 1, 16);
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (this.recipeBook.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
         return true;
      } else {
         return this.canRenderRecipeBook && this.recipeBook.isVisible() ? true : super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
      }
   }

   /**
    * Called when the mouse is clicked over a slot or outside the gui.
    */
   protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
      super.handleMouseClick(slotIn, slotId, mouseButton, type);
      this.recipeBook.slotClicked(slotIn);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      return this.recipeBook.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) ? false : super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
   }

   protected boolean func_195361_a(double p_195361_1_, double p_195361_3_, int p_195361_5_, int p_195361_6_, int p_195361_7_) {
      boolean flag = p_195361_1_ < (double)p_195361_5_ || p_195361_3_ < (double)p_195361_6_ || p_195361_1_ >= (double)(p_195361_5_ + this.xSize) || p_195361_3_ >= (double)(p_195361_6_ + this.ySize);
      return this.recipeBook.func_195604_a(p_195361_1_, p_195361_3_, this.guiLeft, this.guiTop, this.xSize, this.ySize, p_195361_7_) && flag;
   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      return this.recipeBook.charTyped(p_charTyped_1_, p_charTyped_2_) ? true : super.charTyped(p_charTyped_1_, p_charTyped_2_);
   }

   public void recipesUpdated() {
      this.recipeBook.recipesUpdated();
   }

   public GuiRecipeBook func_194310_f() {
      return this.recipeBook;
   }

   /**
    * Called when the screen is unloaded. Used to disable keyboard repeat events
    */
   public void onGuiClosed() {
      this.recipeBook.removed();
      super.onGuiClosed();
   }

   private int getCookProgressScaled(int pixels) {
      int i = this.tileFurnace.getField(2);
      int j = this.tileFurnace.getField(3);
      return j != 0 && i != 0 ? i * pixels / j : 0;
   }

   private int getBurnLeftScaled(int pixels) {
      int i = this.tileFurnace.getField(1);
      if (i == 0) {
         i = 200;
      }

      return this.tileFurnace.getField(0) * pixels / i;
   }
}