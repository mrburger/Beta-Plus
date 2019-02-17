package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketRenameItem;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiRepair extends GuiContainer implements IContainerListener {
   private static final ResourceLocation ANVIL_RESOURCE = new ResourceLocation("textures/gui/container/anvil.png");
   /** The same reference as {@link GuiContainer#field_147002_h}, downcasted to {@link ContainerRepair}. */
   private final ContainerRepair anvil;
   private GuiTextField nameField;
   private final InventoryPlayer playerInventory;

   public GuiRepair(InventoryPlayer inventoryIn, World worldIn) {
      super(new ContainerRepair(inventoryIn, worldIn, Minecraft.getInstance().player));
      this.playerInventory = inventoryIn;
      this.anvil = (ContainerRepair)this.inventorySlots;
   }

   public IGuiEventListener getFocused() {
      return this.nameField.isFocused() ? this.nameField : null;
   }

   /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
   protected void initGui() {
      super.initGui();
      this.mc.keyboardListener.enableRepeatEvents(true);
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      this.nameField = new GuiTextField(0, this.fontRenderer, i + 62, j + 24, 103, 12);
      this.nameField.setTextColor(-1);
      this.nameField.setDisabledTextColour(-1);
      this.nameField.setEnableBackgroundDrawing(false);
      this.nameField.setMaxStringLength(35);
      this.nameField.setTextAcceptHandler(this::func_195393_a);
      this.children.add(this.nameField);
      this.inventorySlots.removeListener(this);
      this.inventorySlots.addListener(this);
   }

   /**
    * Called when the GUI is resized in order to update the world and the resolution
    */
   public void onResize(Minecraft mcIn, int w, int h) {
      String s = this.nameField.getText();
      this.setWorldAndResolution(mcIn, w, h);
      this.nameField.setText(s);
   }

   /**
    * Called when the screen is unloaded. Used to disable keyboard repeat events
    */
   public void onGuiClosed() {
      super.onGuiClosed();
      this.mc.keyboardListener.enableRepeatEvents(false);
      this.inventorySlots.removeListener(this);
   }

   /**
    * Draw the foreground layer for the GuiContainer (everything in front of the items)
    */
   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      GlStateManager.disableLighting();
      GlStateManager.disableBlend();
      this.fontRenderer.drawString(I18n.format("container.repair"), 60.0F, 6.0F, 4210752);
      if (this.anvil.maximumCost > 0) {
         int i = 8453920;
         boolean flag = true;
         String s = I18n.format("container.repair.cost", this.anvil.maximumCost);
         if (this.anvil.maximumCost >= 40 && !this.mc.player.abilities.isCreativeMode) {
            s = I18n.format("container.repair.expensive");
            i = 16736352;
         } else if (!this.anvil.getSlot(2).getHasStack()) {
            flag = false;
         } else if (!this.anvil.getSlot(2).canTakeStack(this.playerInventory.player)) {
            i = 16736352;
         }

         if (flag) {
            int j = this.xSize - 8 - this.fontRenderer.getStringWidth(s) - 2;
            int k = 69;
            drawRect(j - 2, 67, this.xSize - 8, 79, 1325400064);
            this.fontRenderer.drawStringWithShadow(s, (float)j, 69.0F, i);
         }
      }

      GlStateManager.enableLighting();
   }

   private void func_195393_a(int p_195393_1_, String p_195393_2_) {
      if (!p_195393_2_.isEmpty()) {
         String s = p_195393_2_;
         Slot slot = this.anvil.getSlot(0);
         if (slot != null && slot.getHasStack() && !slot.getStack().hasDisplayName() && p_195393_2_.equals(slot.getStack().getDisplayName().getString())) {
            s = "";
         }

         this.anvil.updateItemName(s);
         this.mc.player.connection.sendPacket(new CPacketRenameItem(s));
      }
   }

   /**
    * Draws the screen and all the components in it.
    */
   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      super.render(mouseX, mouseY, partialTicks);
      this.renderHoveredToolTip(mouseX, mouseY);
      GlStateManager.disableLighting();
      GlStateManager.disableBlend();
      this.nameField.drawTextField(mouseX, mouseY, partialTicks);
   }

   /**
    * Draws the background layer of this container (behind the items).
    */
   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(ANVIL_RESOURCE);
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
      this.drawTexturedModalRect(i + 59, j + 20, 0, this.ySize + (this.anvil.getSlot(0).getHasStack() ? 0 : 16), 110, 16);
      if ((this.anvil.getSlot(0).getHasStack() || this.anvil.getSlot(1).getHasStack()) && !this.anvil.getSlot(2).getHasStack()) {
         this.drawTexturedModalRect(i + 99, j + 45, this.xSize, 0, 28, 21);
      }

   }

   /**
    * update the crafting window inventory with the items in the list
    */
   public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList) {
      this.sendSlotContents(containerToSend, 0, containerToSend.getSlot(0).getStack());
   }

   /**
    * Sends the contents of an inventory slot to the client-side Container. This doesn't have to match the actual
    * contents of that slot.
    */
   public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) {
      if (slotInd == 0) {
         this.nameField.setText(stack.isEmpty() ? "" : stack.getDisplayName().getString());
         this.nameField.setEnabled(!stack.isEmpty());
      }

   }

   /**
    * Sends two ints to the client-side Container. Used for furnace burning time, smelting progress, brewing progress,
    * and enchanting level. Normally the first int identifies which variable to update, and the second contains the new
    * value. Both are truncated to shorts in non-local SMP.
    */
   public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue) {
   }

   public void sendAllWindowProperties(Container containerIn, IInventory inventory) {
   }
}