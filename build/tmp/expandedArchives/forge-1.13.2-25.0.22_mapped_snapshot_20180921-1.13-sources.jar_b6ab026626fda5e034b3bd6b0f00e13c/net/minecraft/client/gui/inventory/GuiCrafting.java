package net.minecraft.client.gui.inventory;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerRecipeBook;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiCrafting extends GuiContainer implements IRecipeShownListener {
   private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation("textures/gui/container/crafting_table.png");
   private static final ResourceLocation RECIPE_BUTTON_TEXTURE = new ResourceLocation("textures/gui/recipe_button.png");
   private final GuiRecipeBook recipeBookGui = new GuiRecipeBook();
   private boolean widthTooNarrow;
   private final InventoryPlayer field_212354_A;

   public GuiCrafting(InventoryPlayer playerInv, World worldIn) {
      this(playerInv, worldIn, BlockPos.ORIGIN);
   }

   public GuiCrafting(InventoryPlayer playerInv, World worldIn, BlockPos blockPosition) {
      super(new ContainerWorkbench(playerInv, worldIn, blockPosition));
      this.field_212354_A = playerInv;
   }

   /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
   protected void initGui() {
      super.initGui();
      this.widthTooNarrow = this.width < 379;
      this.recipeBookGui.func_201520_a(this.width, this.height, this.mc, this.widthTooNarrow, (ContainerRecipeBook)this.inventorySlots);
      this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
      this.children.add(this.recipeBookGui);
      this.addButton(new GuiButtonImage(10, this.guiLeft + 5, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiCrafting.this.recipeBookGui.func_201518_a(GuiCrafting.this.widthTooNarrow);
            GuiCrafting.this.recipeBookGui.toggleVisibility();
            GuiCrafting.this.guiLeft = GuiCrafting.this.recipeBookGui.updateScreenPosition(GuiCrafting.this.widthTooNarrow, GuiCrafting.this.width, GuiCrafting.this.xSize);
            this.setPosition(GuiCrafting.this.guiLeft + 5, GuiCrafting.this.height / 2 - 49);
         }
      });
   }

   @Nullable
   public IGuiEventListener getFocused() {
      return this.recipeBookGui;
   }

   /**
    * Called from the main game loop to update the screen.
    */
   public void tick() {
      super.tick();
      this.recipeBookGui.tick();
   }

   /**
    * Draws the screen and all the components in it.
    */
   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      if (this.recipeBookGui.isVisible() && this.widthTooNarrow) {
         this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
         this.recipeBookGui.render(mouseX, mouseY, partialTicks);
      } else {
         this.recipeBookGui.render(mouseX, mouseY, partialTicks);
         super.render(mouseX, mouseY, partialTicks);
         this.recipeBookGui.renderGhostRecipe(this.guiLeft, this.guiTop, true, partialTicks);
      }

      this.renderHoveredToolTip(mouseX, mouseY);
      this.recipeBookGui.renderTooltip(this.guiLeft, this.guiTop, mouseX, mouseY);
   }

   /**
    * Draw the foreground layer for the GuiContainer (everything in front of the items)
    */
   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      this.fontRenderer.drawString(I18n.format("container.crafting"), 28.0F, 6.0F, 4210752);
      this.fontRenderer.drawString(this.field_212354_A.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
   }

   /**
    * Draws the background layer of this container (behind the items).
    */
   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(CRAFTING_TABLE_GUI_TEXTURES);
      int i = this.guiLeft;
      int j = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
   }

   protected boolean isPointInRegion(int p_195359_1_, int p_195359_2_, int p_195359_3_, int p_195359_4_, double p_195359_5_, double p_195359_7_) {
      return (!this.widthTooNarrow || !this.recipeBookGui.isVisible()) && super.isPointInRegion(p_195359_1_, p_195359_2_, p_195359_3_, p_195359_4_, p_195359_5_, p_195359_7_);
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (this.recipeBookGui.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
         return true;
      } else {
         return this.widthTooNarrow && this.recipeBookGui.isVisible() ? true : super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
      }
   }

   protected boolean func_195361_a(double p_195361_1_, double p_195361_3_, int p_195361_5_, int p_195361_6_, int p_195361_7_) {
      boolean flag = p_195361_1_ < (double)p_195361_5_ || p_195361_3_ < (double)p_195361_6_ || p_195361_1_ >= (double)(p_195361_5_ + this.xSize) || p_195361_3_ >= (double)(p_195361_6_ + this.ySize);
      return this.recipeBookGui.func_195604_a(p_195361_1_, p_195361_3_, this.guiLeft, this.guiTop, this.xSize, this.ySize, p_195361_7_) && flag;
   }

   /**
    * Called when the mouse is clicked over a slot or outside the gui.
    */
   protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
      super.handleMouseClick(slotIn, slotId, mouseButton, type);
      this.recipeBookGui.slotClicked(slotIn);
   }

   public void recipesUpdated() {
      this.recipeBookGui.recipesUpdated();
   }

   /**
    * Called when the screen is unloaded. Used to disable keyboard repeat events
    */
   public void onGuiClosed() {
      this.recipeBookGui.removed();
      super.onGuiClosed();
   }

   public GuiRecipeBook func_194310_f() {
      return this.recipeBookGui;
   }
}