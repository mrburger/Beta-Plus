package net.minecraft.client.gui.achievement;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IGuiEventListenerDeferred;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatType;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiStats extends GuiScreen implements IProgressMeter {
   protected GuiScreen parentScreen;
   protected String screenTitle = "Select world";
   private GuiStats.StatsGeneral generalStats;
   private GuiStats.StatsItem itemStats;
   private GuiStats.StatsMobsList mobStats;
   private final StatisticsManager stats;
   private GuiSlot displaySlot;
   /** When true, the game will be paused when the gui is shown */
   private boolean doesGuiPauseGame = true;

   public GuiStats(GuiScreen parent, StatisticsManager manager) {
      this.parentScreen = parent;
      this.stats = manager;
   }

   public IGuiEventListener getFocused() {
      return this.displaySlot;
   }

   /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
   protected void initGui() {
      this.screenTitle = I18n.format("gui.stats");
      this.doesGuiPauseGame = true;
      this.mc.getConnection().sendPacket(new CPacketClientStatus(CPacketClientStatus.State.REQUEST_STATS));
   }

   public void initLists() {
      this.generalStats = new GuiStats.StatsGeneral(this.mc);
      this.itemStats = new GuiStats.StatsItem(this.mc);
      this.mobStats = new GuiStats.StatsMobsList(this.mc);
   }

   public void initButtons() {
      this.addButton(new GuiButton(0, this.width / 2 - 100, this.height - 28, I18n.format("gui.done")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiStats.this.mc.displayGuiScreen(GuiStats.this.parentScreen);
         }
      });
      this.addButton(new GuiButton(1, this.width / 2 - 120, this.height - 52, 80, 20, I18n.format("stat.generalButton")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiStats.this.displaySlot = GuiStats.this.generalStats;
         }
      });
      GuiButton guibutton = this.addButton(new GuiButton(3, this.width / 2 - 40, this.height - 52, 80, 20, I18n.format("stat.itemsButton")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiStats.this.displaySlot = GuiStats.this.itemStats;
         }
      });
      GuiButton guibutton1 = this.addButton(new GuiButton(4, this.width / 2 + 40, this.height - 52, 80, 20, I18n.format("stat.mobsButton")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiStats.this.displaySlot = GuiStats.this.mobStats;
         }
      });
      if (this.itemStats.getSize() == 0) {
         guibutton.enabled = false;
      }

      if (this.mobStats.getSize() == 0) {
         guibutton1.enabled = false;
      }

      this.children.add((IGuiEventListenerDeferred)() -> {
         return this.displaySlot;
      });
   }

   /**
    * Draws the screen and all the components in it.
    */
   public void render(int mouseX, int mouseY, float partialTicks) {
      if (this.doesGuiPauseGame) {
         this.drawDefaultBackground();
         this.drawCenteredString(this.fontRenderer, I18n.format("multiplayer.downloadingStats"), this.width / 2, this.height / 2, 16777215);
         this.drawCenteredString(this.fontRenderer, LOADING_STRINGS[(int)(Util.milliTime() / 150L % (long)LOADING_STRINGS.length)], this.width / 2, this.height / 2 + this.fontRenderer.FONT_HEIGHT * 2, 16777215);
      } else {
         this.displaySlot.drawScreen(mouseX, mouseY, partialTicks);
         this.drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 20, 16777215);
         super.render(mouseX, mouseY, partialTicks);
      }

   }

   public void onStatsUpdated() {
      if (this.doesGuiPauseGame) {
         this.initLists();
         this.displaySlot = this.generalStats;
         this.initButtons(); //Forge: Bugfix, initButtons adds displaySlot to children, so set it first to prevent NPE
         this.doesGuiPauseGame = false;
      }

   }

   /**
    * Returns true if this GUI should pause the game when it is displayed in single-player
    */
   public boolean doesGuiPauseGame() {
      return !this.doesGuiPauseGame;
   }

   private int func_195224_b(int p_195224_1_) {
      return 115 + 40 * p_195224_1_;
   }

   private void drawStatsScreen(int x, int y, Item itemIn) {
      this.drawButtonBackground(x + 1, y + 1);
      GlStateManager.enableRescaleNormal();
      RenderHelper.enableGUIStandardItemLighting();
      this.itemRender.renderItemIntoGUI(itemIn.getDefaultInstance(), x + 2, y + 2);
      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableRescaleNormal();
   }

   /**
    * Draws a gray box that serves as a button background.
    */
   private void drawButtonBackground(int x, int y) {
      this.drawSprite(x, y, 0, 0);
   }

   /**
    * Draws a sprite from assets/textures/gui/container/stats_icons.png
    */
   private void drawSprite(int x, int y, int u, int v) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(STAT_ICONS);
      float f = 0.0078125F;
      float f1 = 0.0078125F;
      int i = 18;
      int j = 18;
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
      bufferbuilder.pos((double)(x + 0), (double)(y + 18), (double)this.zLevel).tex((double)((float)(u + 0) * 0.0078125F), (double)((float)(v + 18) * 0.0078125F)).endVertex();
      bufferbuilder.pos((double)(x + 18), (double)(y + 18), (double)this.zLevel).tex((double)((float)(u + 18) * 0.0078125F), (double)((float)(v + 18) * 0.0078125F)).endVertex();
      bufferbuilder.pos((double)(x + 18), (double)(y + 0), (double)this.zLevel).tex((double)((float)(u + 18) * 0.0078125F), (double)((float)(v + 0) * 0.0078125F)).endVertex();
      bufferbuilder.pos((double)(x + 0), (double)(y + 0), (double)this.zLevel).tex((double)((float)(u + 0) * 0.0078125F), (double)((float)(v + 0) * 0.0078125F)).endVertex();
      tessellator.draw();
   }

   @OnlyIn(Dist.CLIENT)
   class StatsGeneral extends GuiSlot {
      private Iterator<Stat<ResourceLocation>> field_195102_w;

      public StatsGeneral(Minecraft mcIn) {
         super(mcIn, GuiStats.this.width, GuiStats.this.height, 32, GuiStats.this.height - 64, 10);
         this.setShowSelectionBox(false);
      }

      protected int getSize() {
         return StatList.CUSTOM.size();
      }

      /**
       * Returns true if the element passed in is currently selected
       */
      protected boolean isSelected(int slotIndex) {
         return false;
      }

      /**
       * Return the height of the content being scrolled
       */
      protected int getContentHeight() {
         return this.getSize() * 10;
      }

      protected void drawBackground() {
         GuiStats.this.drawDefaultBackground();
      }

      protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks) {
         if (slotIndex == 0) {
            this.field_195102_w = StatList.CUSTOM.iterator();
         }

         Stat<ResourceLocation> stat = this.field_195102_w.next();
         ITextComponent itextcomponent = (new TextComponentTranslation("stat." + stat.getValue().toString().replace(':', '.'))).applyTextStyle(TextFormatting.GRAY);
         this.drawString(GuiStats.this.fontRenderer, itextcomponent.getString(), xPos + 2, yPos + 1, slotIndex % 2 == 0 ? 16777215 : 9474192);
         String s = stat.format(GuiStats.this.stats.getValue(stat));
         this.drawString(GuiStats.this.fontRenderer, s, xPos + 2 + 213 - GuiStats.this.fontRenderer.getStringWidth(s), yPos + 1, slotIndex % 2 == 0 ? 16777215 : 9474192);
      }
   }

   @OnlyIn(Dist.CLIENT)
   class StatsItem extends GuiSlot {
      protected final List<StatType<Block>> field_195113_v;
      protected final List<StatType<Item>> field_195114_w;
      private final int[] field_195112_D = new int[]{3, 4, 1, 2, 5, 6};
      protected int field_195115_x = -1;
      protected final List<Item> field_195116_y;
      protected final java.util.Comparator<Item> field_195117_z = new GuiStats.StatsItem.Comparator();
      @Nullable
      protected StatType<?> field_195110_A;
      protected int field_195111_B;

      public StatsItem(Minecraft mcIn) {
         super(mcIn, GuiStats.this.width, GuiStats.this.height, 32, GuiStats.this.height - 64, 20);
         this.field_195113_v = Lists.newArrayList();
         this.field_195113_v.add(StatList.BLOCK_MINED);
         this.field_195114_w = Lists.newArrayList(StatList.ITEM_BROKEN, StatList.ITEM_CRAFTED, StatList.ITEM_USED, StatList.ITEM_PICKED_UP, StatList.ITEM_DROPPED);
         this.setShowSelectionBox(false);
         this.setHasListHeader(true, 20);
         Set<Item> set = Sets.newIdentityHashSet();

         for(Item item : IRegistry.field_212630_s) {
            boolean flag = false;

            for(StatType<Item> stattype : this.field_195114_w) {
               if (stattype.contains(item) && GuiStats.this.stats.getValue(stattype.get(item)) > 0) {
                  flag = true;
               }
            }

            if (flag) {
               set.add(item);
            }
         }

         for(Block block : IRegistry.field_212618_g) {
            boolean flag1 = false;

            for(StatType<Block> stattype1 : this.field_195113_v) {
               if (stattype1.contains(block) && GuiStats.this.stats.getValue(stattype1.get(block)) > 0) {
                  flag1 = true;
               }
            }

            if (flag1) {
               set.add(block.asItem());
            }
         }

         set.remove(Items.AIR);
         this.field_195116_y = Lists.newArrayList(set);
      }

      /**
       * Handles drawing a list's header row.
       */
      protected void drawListHeader(int insideLeft, int insideTop, Tessellator tessellatorIn) {
         if (!this.mc.mouseHelper.isLeftDown()) {
            this.field_195115_x = -1;
         }

         for(int i = 0; i < this.field_195112_D.length; ++i) {
            GuiStats.this.drawSprite(insideLeft + GuiStats.this.func_195224_b(i) - 18, insideTop + 1, 0, this.field_195115_x == i ? 0 : 18);
         }

         if (this.field_195110_A != null) {
            int k = GuiStats.this.func_195224_b(this.func_195105_b(this.field_195110_A)) - 36;
            int j = this.field_195111_B == 1 ? 2 : 1;
            GuiStats.this.drawSprite(insideLeft + k, insideTop + 1, 18 * j, 0);
         }

         for(int l = 0; l < this.field_195112_D.length; ++l) {
            int i1 = this.field_195115_x != l ? 0 : 1;
            GuiStats.this.drawSprite(insideLeft + GuiStats.this.func_195224_b(l) - 18 + i1, insideTop + 1 + i1, 18 * this.field_195112_D[l], 18);
         }

      }

      protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks) {
         Item item = this.func_195106_c(slotIndex);
         GuiStats.this.drawStatsScreen(xPos + 40, yPos, item);

         for(int i = 0; i < this.field_195113_v.size(); ++i) {
            Stat<Block> stat;
            if (item instanceof ItemBlock) {
               stat = this.field_195113_v.get(i).get(((ItemBlock)item).getBlock());
            } else {
               stat = null;
            }

            this.func_195103_a(stat, xPos + GuiStats.this.func_195224_b(i), yPos, slotIndex % 2 == 0);
         }

         for(int j = 0; j < this.field_195114_w.size(); ++j) {
            this.func_195103_a(this.field_195114_w.get(j).get(item), xPos + GuiStats.this.func_195224_b(j + this.field_195113_v.size()), yPos, slotIndex % 2 == 0);
         }

      }

      /**
       * Returns true if the element passed in is currently selected
       */
      protected boolean isSelected(int slotIndex) {
         return false;
      }

      /**
       * Gets the width of the list
       */
      public int getListWidth() {
         return 375;
      }

      protected int getScrollBarX() {
         return this.width / 2 + 140;
      }

      protected void drawBackground() {
         GuiStats.this.drawDefaultBackground();
      }

      /**
       * Called when the mouse left-clicks the header or anywhere else that isn't on an entry.
       */
      protected void clickedHeader(int p_148132_1_, int p_148132_2_) {
         this.field_195115_x = -1;

         for(int i = 0; i < this.field_195112_D.length; ++i) {
            int j = p_148132_1_ - GuiStats.this.func_195224_b(i);
            if (j >= -36 && j <= 0) {
               this.field_195115_x = i;
               break;
            }
         }

         if (this.field_195115_x >= 0) {
            this.func_195107_a(this.func_195108_d(this.field_195115_x));
            this.mc.getSoundHandler().play(SimpleSound.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         }

      }

      private StatType<?> func_195108_d(int p_195108_1_) {
         return p_195108_1_ < this.field_195113_v.size() ? this.field_195113_v.get(p_195108_1_) : this.field_195114_w.get(p_195108_1_ - this.field_195113_v.size());
      }

      private int func_195105_b(StatType<?> p_195105_1_) {
         int i = this.field_195113_v.indexOf(p_195105_1_);
         if (i >= 0) {
            return i;
         } else {
            int j = this.field_195114_w.indexOf(p_195105_1_);
            return j >= 0 ? j + this.field_195113_v.size() : -1;
         }
      }

      protected final int getSize() {
         return this.field_195116_y.size();
      }

      protected final Item func_195106_c(int p_195106_1_) {
         return this.field_195116_y.get(p_195106_1_);
      }

      protected void func_195103_a(@Nullable Stat<?> p_195103_1_, int p_195103_2_, int p_195103_3_, boolean p_195103_4_) {
         String s = p_195103_1_ == null ? "-" : p_195103_1_.format(GuiStats.this.stats.getValue(p_195103_1_));
         this.drawString(GuiStats.this.fontRenderer, s, p_195103_2_ - GuiStats.this.fontRenderer.getStringWidth(s), p_195103_3_ + 5, p_195103_4_ ? 16777215 : 9474192);
      }

      protected void renderDecorations(int mouseXIn, int mouseYIn) {
         if (mouseYIn >= this.top && mouseYIn <= this.bottom) {
            int i = this.getEntryAt((double)mouseXIn, (double)mouseYIn);
            int j = (this.width - this.getListWidth()) / 2;
            if (i >= 0) {
               if (mouseXIn < j + 40 || mouseXIn > j + 40 + 20) {
                  return;
               }

               Item item = this.func_195106_c(i);
               this.func_200207_a(this.func_200208_a(item), mouseXIn, mouseYIn);
            } else {
               ITextComponent itextcomponent = null;
               int k = mouseXIn - j;

               for(int l = 0; l < this.field_195112_D.length; ++l) {
                  int i1 = GuiStats.this.func_195224_b(l);
                  if (k >= i1 - 18 && k <= i1) {
                     itextcomponent = new TextComponentTranslation(this.func_195108_d(l).getTranslationKey());
                     break;
                  }
               }

               this.func_200207_a(itextcomponent, mouseXIn, mouseYIn);
            }

         }
      }

      protected void func_200207_a(@Nullable ITextComponent p_200207_1_, int p_200207_2_, int p_200207_3_) {
         if (p_200207_1_ != null) {
            String s = p_200207_1_.getFormattedText();
            int i = p_200207_2_ + 12;
            int j = p_200207_3_ - 12;
            int k = GuiStats.this.fontRenderer.getStringWidth(s);
            this.drawGradientRect(i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
            GuiStats.this.fontRenderer.drawStringWithShadow(s, (float)i, (float)j, -1);
         }
      }

      protected ITextComponent func_200208_a(Item p_200208_1_) {
         return p_200208_1_.getName();
      }

      protected void func_195107_a(StatType<?> p_195107_1_) {
         if (p_195107_1_ != this.field_195110_A) {
            this.field_195110_A = p_195107_1_;
            this.field_195111_B = -1;
         } else if (this.field_195111_B == -1) {
            this.field_195111_B = 1;
         } else {
            this.field_195110_A = null;
            this.field_195111_B = 0;
         }

         this.field_195116_y.sort(this.field_195117_z);
      }

      @OnlyIn(Dist.CLIENT)
      class Comparator implements java.util.Comparator<Item> {
         private Comparator() {
         }

         public int compare(Item p_compare_1_, Item p_compare_2_) {
            int i;
            int j;
            if (StatsItem.this.field_195110_A == null) {
               i = 0;
               j = 0;
            } else if (StatsItem.this.field_195113_v.contains(StatsItem.this.field_195110_A)) {
               StatType<Block> stattype = (StatType<Block>)StatsItem.this.field_195110_A;
               i = p_compare_1_ instanceof ItemBlock ? GuiStats.this.stats.getValue(stattype, ((ItemBlock)p_compare_1_).getBlock()) : -1;
               j = p_compare_2_ instanceof ItemBlock ? GuiStats.this.stats.getValue(stattype, ((ItemBlock)p_compare_2_).getBlock()) : -1;
            } else {
               StatType<Item> stattype1 = (StatType<Item>)StatsItem.this.field_195110_A;
               i = GuiStats.this.stats.getValue(stattype1, p_compare_1_);
               j = GuiStats.this.stats.getValue(stattype1, p_compare_2_);
            }

            return i == j ? StatsItem.this.field_195111_B * Integer.compare(Item.getIdFromItem(p_compare_1_), Item.getIdFromItem(p_compare_2_)) : StatsItem.this.field_195111_B * Integer.compare(i, j);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   class StatsMobsList extends GuiSlot {
      private final List<EntityType<?>> mobs = Lists.newArrayList();

      public StatsMobsList(Minecraft mcIn) {
         super(mcIn, GuiStats.this.width, GuiStats.this.height, 32, GuiStats.this.height - 64, GuiStats.this.fontRenderer.FONT_HEIGHT * 4);
         this.setShowSelectionBox(false);

         for(EntityType<?> entitytype : IRegistry.field_212629_r) {
            if (GuiStats.this.stats.getValue(StatList.ENTITY_KILLED.get(entitytype)) > 0 || GuiStats.this.stats.getValue(StatList.ENTITY_KILLED_BY.get(entitytype)) > 0) {
               this.mobs.add(entitytype);
            }
         }

      }

      protected int getSize() {
         return this.mobs.size();
      }

      /**
       * Returns true if the element passed in is currently selected
       */
      protected boolean isSelected(int slotIndex) {
         return false;
      }

      /**
       * Return the height of the content being scrolled
       */
      protected int getContentHeight() {
         return this.getSize() * GuiStats.this.fontRenderer.FONT_HEIGHT * 4;
      }

      protected void drawBackground() {
         GuiStats.this.drawDefaultBackground();
      }

      protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks) {
         EntityType<?> entitytype = this.mobs.get(slotIndex);
         String s = I18n.format(Util.makeTranslationKey("entity", EntityType.getId(entitytype)));
         int i = GuiStats.this.stats.getValue(StatList.ENTITY_KILLED.get(entitytype));
         int j = GuiStats.this.stats.getValue(StatList.ENTITY_KILLED_BY.get(entitytype));
         this.drawString(GuiStats.this.fontRenderer, s, xPos + 2 - 10, yPos + 1, 16777215);
         this.drawString(GuiStats.this.fontRenderer, this.func_199707_a(s, i), xPos + 2, yPos + 1 + GuiStats.this.fontRenderer.FONT_HEIGHT, i == 0 ? 6316128 : 9474192);
         this.drawString(GuiStats.this.fontRenderer, this.func_199706_b(s, j), xPos + 2, yPos + 1 + GuiStats.this.fontRenderer.FONT_HEIGHT * 2, j == 0 ? 6316128 : 9474192);
      }

      private String func_199707_a(String p_199707_1_, int p_199707_2_) {
         String s = StatList.ENTITY_KILLED.getTranslationKey();
         return p_199707_2_ == 0 ? I18n.format(s + ".none", p_199707_1_) : I18n.format(s, p_199707_2_, p_199707_1_);
      }

      private String func_199706_b(String p_199706_1_, int p_199706_2_) {
         String s = StatList.ENTITY_KILLED_BY.getTranslationKey();
         return p_199706_2_ == 0 ? I18n.format(s + ".none", p_199706_1_) : I18n.format(s, p_199706_1_, p_199706_2_);
      }
   }
}