package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.model.ModelBook;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnchantmentNameParts;
import net.minecraft.util.INameable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiEnchantment extends GuiContainer {
   /** The ResourceLocation containing the Enchantment GUI texture location */
   private static final ResourceLocation ENCHANTMENT_TABLE_GUI_TEXTURE = new ResourceLocation("textures/gui/container/enchanting_table.png");
   /** The ResourceLocation containing the texture for the Book rendered above the enchantment table */
   private static final ResourceLocation ENCHANTMENT_TABLE_BOOK_TEXTURE = new ResourceLocation("textures/entity/enchanting_table_book.png");
   /** The ModelBook instance used for rendering the book on the Enchantment table */
   private static final ModelBook MODEL_BOOK = new ModelBook();
   /** The player inventory currently bound to this GuiEnchantment instance. */
   private final InventoryPlayer playerInventory;
   /** A Random instance for use with the enchantment gui */
   private final Random random = new Random();
   /** The same reference as {@link GuiContainer#field_147002_h}, downcasted to {@link ContainerEnchantment}. */
   private final ContainerEnchantment container;
   public int ticks;
   public float flip;
   public float oFlip;
   public float flipT;
   public float flipA;
   public float open;
   public float oOpen;
   private ItemStack last = ItemStack.EMPTY;
   private final INameable nameable;

   public GuiEnchantment(InventoryPlayer inventory, World worldIn, INameable nameable) {
      super(new ContainerEnchantment(inventory, worldIn));
      this.playerInventory = inventory;
      this.container = (ContainerEnchantment)this.inventorySlots;
      this.nameable = nameable;
   }

   /**
    * Draw the foreground layer for the GuiContainer (everything in front of the items)
    */
   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      this.fontRenderer.drawString(this.nameable.getDisplayName().getFormattedText(), 12.0F, 5.0F, 4210752);
      this.fontRenderer.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
   }

   /**
    * Called from the main game loop to update the screen.
    */
   public void tick() {
      super.tick();
      this.tickBook();
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;

      for(int k = 0; k < 3; ++k) {
         double d0 = p_mouseClicked_1_ - (double)(i + 60);
         double d1 = p_mouseClicked_3_ - (double)(j + 14 + 19 * k);
         if (d0 >= 0.0D && d1 >= 0.0D && d0 < 108.0D && d1 < 19.0D && this.container.enchantItem(this.mc.player, k)) {
            this.mc.playerController.sendEnchantPacket(this.container.windowId, k);
            return true;
         }
      }

      return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   /**
    * Draws the background layer of this container (behind the items).
    */
   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(ENCHANTMENT_TABLE_GUI_TEXTURE);
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
      GlStateManager.pushMatrix();
      GlStateManager.matrixMode(5889);
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      int k = (int)this.mc.mainWindow.getGuiScaleFactor();
      GlStateManager.viewport((this.width - 320) / 2 * k, (this.height - 240) / 2 * k, 320 * k, 240 * k);
      GlStateManager.translatef(-0.34F, 0.23F, 0.0F);
      GlStateManager.multMatrixf(Matrix4f.perspective(90.0D, 1.3333334F, 9.0F, 80.0F));
      float f = 1.0F;
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      RenderHelper.enableStandardItemLighting();
      GlStateManager.translatef(0.0F, 3.3F, -16.0F);
      GlStateManager.scalef(1.0F, 1.0F, 1.0F);
      float f1 = 5.0F;
      GlStateManager.scalef(5.0F, 5.0F, 5.0F);
      GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(ENCHANTMENT_TABLE_BOOK_TEXTURE);
      GlStateManager.rotatef(20.0F, 1.0F, 0.0F, 0.0F);
      float f2 = this.oOpen + (this.open - this.oOpen) * partialTicks;
      GlStateManager.translatef((1.0F - f2) * 0.2F, (1.0F - f2) * 0.1F, (1.0F - f2) * 0.25F);
      GlStateManager.rotatef(-(1.0F - f2) * 90.0F - 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
      float f3 = this.oFlip + (this.flip - this.oFlip) * partialTicks + 0.25F;
      float f4 = this.oFlip + (this.flip - this.oFlip) * partialTicks + 0.75F;
      f3 = (f3 - (float)MathHelper.fastFloor((double)f3)) * 1.6F - 0.3F;
      f4 = (f4 - (float)MathHelper.fastFloor((double)f4)) * 1.6F - 0.3F;
      if (f3 < 0.0F) {
         f3 = 0.0F;
      }

      if (f4 < 0.0F) {
         f4 = 0.0F;
      }

      if (f3 > 1.0F) {
         f3 = 1.0F;
      }

      if (f4 > 1.0F) {
         f4 = 1.0F;
      }

      GlStateManager.enableRescaleNormal();
      MODEL_BOOK.render((Entity)null, 0.0F, f3, f4, f2, 0.0F, 0.0625F);
      GlStateManager.disableRescaleNormal();
      RenderHelper.disableStandardItemLighting();
      GlStateManager.matrixMode(5889);
      GlStateManager.viewport(0, 0, this.mc.mainWindow.getFramebufferWidth(), this.mc.mainWindow.getFramebufferHeight());
      GlStateManager.popMatrix();
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      RenderHelper.disableStandardItemLighting();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      EnchantmentNameParts.getInstance().reseedRandomGenerator((long)this.container.xpSeed);
      int l = this.container.getLapisAmount();

      for(int i1 = 0; i1 < 3; ++i1) {
         int j1 = i + 60;
         int k1 = j1 + 20;
         this.zLevel = 0.0F;
         this.mc.getTextureManager().bindTexture(ENCHANTMENT_TABLE_GUI_TEXTURE);
         int l1 = this.container.enchantLevels[i1];
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         if (l1 == 0) {
            this.drawTexturedModalRect(j1, j + 14 + 19 * i1, 0, 185, 108, 19);
         } else {
            String s = "" + l1;
            int i2 = 86 - this.fontRenderer.getStringWidth(s);
            String s1 = EnchantmentNameParts.getInstance().generateNewRandomName(this.fontRenderer, i2);
            FontRenderer fontrenderer = this.mc.getFontResourceManager().getFontRenderer(Minecraft.standardGalacticFontRenderer);
            int j2 = 6839882;
            if (((l < i1 + 1 || this.mc.player.experienceLevel < l1) && !this.mc.player.abilities.isCreativeMode) || this.container.enchantClue[l] == -1) { // Forge: render buttons as disabled when enchantable but enchantability not met on lower levels
               this.drawTexturedModalRect(j1, j + 14 + 19 * i1, 0, 185, 108, 19);
               this.drawTexturedModalRect(j1 + 1, j + 15 + 19 * i1, 16 * i1, 239, 16, 16);
               fontrenderer.drawSplitString(s1, k1, j + 16 + 19 * i1, i2, (j2 & 16711422) >> 1);
               j2 = 4226832;
            } else {
               int k2 = mouseX - (i + 60);
               int l2 = mouseY - (j + 14 + 19 * i1);
               if (k2 >= 0 && l2 >= 0 && k2 < 108 && l2 < 19) {
                  this.drawTexturedModalRect(j1, j + 14 + 19 * i1, 0, 204, 108, 19);
                  j2 = 16777088;
               } else {
                  this.drawTexturedModalRect(j1, j + 14 + 19 * i1, 0, 166, 108, 19);
               }

               this.drawTexturedModalRect(j1 + 1, j + 15 + 19 * i1, 16 * i1, 223, 16, 16);
               fontrenderer.drawSplitString(s1, k1, j + 16 + 19 * i1, i2, j2);
               j2 = 8453920;
            }

            fontrenderer = this.mc.fontRenderer;
            fontrenderer.drawStringWithShadow(s, (float)(k1 + 86 - fontrenderer.getStringWidth(s)), (float)(j + 16 + 19 * i1 + 7), j2);
         }
      }

   }

   /**
    * Draws the screen and all the components in it.
    */
   public void render(int mouseX, int mouseY, float partialTicks) {
      partialTicks = this.mc.getRenderPartialTicks();
      this.drawDefaultBackground();
      super.render(mouseX, mouseY, partialTicks);
      this.renderHoveredToolTip(mouseX, mouseY);
      boolean flag = this.mc.player.abilities.isCreativeMode;
      int i = this.container.getLapisAmount();

      for(int j = 0; j < 3; ++j) {
         int k = this.container.enchantLevels[j];
         Enchantment enchantment = Enchantment.getEnchantmentByID(this.container.enchantClue[j]);
         int l = this.container.worldClue[j];
         int i1 = j + 1;
         if (this.isPointInRegion(60, 14 + 19 * j, 108, 17, (double)mouseX, (double)mouseY) && k > 0) {
            List<String> list = Lists.newArrayList();
            list.add("" + TextFormatting.WHITE + TextFormatting.ITALIC + I18n.format("container.enchant.clue", enchantment == null ? "" : enchantment.func_200305_d(l).getFormattedText()));
            if (enchantment == null) {
               java.util.Collections.addAll(list, "", TextFormatting.RED + I18n.format("forge.container.enchant.limitedEnchantability"));
            } else if (!flag) {
               list.add("");
               if (this.mc.player.experienceLevel < k) {
                  list.add(TextFormatting.RED + I18n.format("container.enchant.level.requirement", this.container.enchantLevels[j]));
               } else {
                  String s;
                  if (i1 == 1) {
                     s = I18n.format("container.enchant.lapis.one");
                  } else {
                     s = I18n.format("container.enchant.lapis.many", i1);
                  }

                  TextFormatting textformatting = i >= i1 ? TextFormatting.GRAY : TextFormatting.RED;
                  list.add(textformatting + "" + s);
                  if (i1 == 1) {
                     s = I18n.format("container.enchant.level.one");
                  } else {
                     s = I18n.format("container.enchant.level.many", i1);
                  }

                  list.add(TextFormatting.GRAY + "" + s);
               }
            }

            this.drawHoveringText(list, mouseX, mouseY);
            break;
         }
      }

   }

   public void tickBook() {
      ItemStack itemstack = this.inventorySlots.getSlot(0).getStack();
      if (!ItemStack.areItemStacksEqual(itemstack, this.last)) {
         this.last = itemstack;

         while(true) {
            this.flipT += (float)(this.random.nextInt(4) - this.random.nextInt(4));
            if (!(this.flip <= this.flipT + 1.0F) || !(this.flip >= this.flipT - 1.0F)) {
               break;
            }
         }
      }

      ++this.ticks;
      this.oFlip = this.flip;
      this.oOpen = this.open;
      boolean flag = false;

      for(int i = 0; i < 3; ++i) {
         if (this.container.enchantLevels[i] != 0) {
            flag = true;
         }
      }

      if (flag) {
         this.open += 0.2F;
      } else {
         this.open -= 0.2F;
      }

      this.open = MathHelper.clamp(this.open, 0.0F, 1.0F);
      float f1 = (this.flipT - this.flip) * 0.4F;
      float f = 0.2F;
      f1 = MathHelper.clamp(f1, -0.2F, 0.2F);
      this.flipA += (f1 - this.flipA) * 0.9F;
      this.flip += this.flipA;
   }
}