package net.minecraft.client.gui;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldSummary;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class GuiListWorldSelection extends GuiListExtended<GuiListWorldSelectionEntry> {
   private static final Logger LOGGER = LogManager.getLogger();
   private final GuiWorldSelection worldSelection;
   /** Index to the currently selected world */
   private int selectedIdx = -1;
   @Nullable
   private List<WorldSummary> field_212331_y = null;

   public GuiListWorldSelection(GuiWorldSelection p_i49846_1_, Minecraft p_i49846_2_, int p_i49846_3_, int p_i49846_4_, int p_i49846_5_, int p_i49846_6_, int p_i49846_7_, Supplier<String> p_i49846_8_, @Nullable GuiListWorldSelection p_i49846_9_) {
      super(p_i49846_2_, p_i49846_3_, p_i49846_4_, p_i49846_5_, p_i49846_6_, p_i49846_7_);
      this.worldSelection = p_i49846_1_;
      if (p_i49846_9_ != null) {
         this.field_212331_y = p_i49846_9_.field_212331_y;
      }

      this.func_212330_a(p_i49846_8_, false);
   }

   public void func_212330_a(Supplier<String> p_212330_1_, boolean p_212330_2_) {
      this.clearEntries();
      ISaveFormat isaveformat = this.mc.getSaveLoader();
      if (this.field_212331_y == null || p_212330_2_) {
         try {
            this.field_212331_y = isaveformat.getSaveList();
         } catch (AnvilConverterException anvilconverterexception) {
            LOGGER.error("Couldn't load level list", (Throwable)anvilconverterexception);
            this.mc.displayGuiScreen(new GuiErrorScreen(I18n.format("selectWorld.unable_to_load"), anvilconverterexception.getMessage()));
            return;
         }

         Collections.sort(this.field_212331_y);
      }

      String s = p_212330_1_.get().toLowerCase(Locale.ROOT);

      for(WorldSummary worldsummary : this.field_212331_y) {
         if (worldsummary.getDisplayName().toLowerCase(Locale.ROOT).contains(s) || worldsummary.getFileName().toLowerCase(Locale.ROOT).contains(s)) {
            this.addEntry(new GuiListWorldSelectionEntry(this, worldsummary, this.mc.getSaveLoader()));
         }
      }

   }

   protected int getScrollBarX() {
      return super.getScrollBarX() + 20;
   }

   /**
    * Gets the width of the list
    */
   public int getListWidth() {
      return super.getListWidth() + 50;
   }

   public void selectWorld(int idx) {
      this.selectedIdx = idx;
      this.worldSelection.selectWorld(this.getSelectedWorld());
   }

   /**
    * Returns true if the element passed in is currently selected
    */
   protected boolean isSelected(int slotIndex) {
      return slotIndex == this.selectedIdx;
   }

   @Nullable
   public GuiListWorldSelectionEntry getSelectedWorld() {
      return this.selectedIdx >= 0 && this.selectedIdx < this.getSize() ? this.getChildren().get(this.selectedIdx) : null;
   }

   public GuiWorldSelection getGuiWorldSelection() {
      return this.worldSelection;
   }
}