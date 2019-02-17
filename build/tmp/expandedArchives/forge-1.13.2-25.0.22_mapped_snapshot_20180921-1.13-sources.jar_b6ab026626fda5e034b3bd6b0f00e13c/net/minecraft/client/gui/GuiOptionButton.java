package net.minecraft.client.gui;

import javax.annotation.Nullable;
import net.minecraft.client.GameSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class GuiOptionButton extends GuiButton {
   @Nullable
   private final GameSettings.Options enumOptions;

   public GuiOptionButton(int p_i45011_1_, int p_i45011_2_, int p_i45011_3_, String p_i45011_4_) {
      this(p_i45011_1_, p_i45011_2_, p_i45011_3_, (GameSettings.Options)null, p_i45011_4_);
   }

   public GuiOptionButton(int p_i45013_1_, int p_i45013_2_, int p_i45013_3_, @Nullable GameSettings.Options p_i45013_4_, String p_i45013_5_) {
      this(p_i45013_1_, p_i45013_2_, p_i45013_3_, 150, 20, p_i45013_4_, p_i45013_5_);
   }

   public GuiOptionButton(int p_i47664_1_, int p_i47664_2_, int p_i47664_3_, int p_i47664_4_, int p_i47664_5_, @Nullable GameSettings.Options p_i47664_6_, String p_i47664_7_) {
      super(p_i47664_1_, p_i47664_2_, p_i47664_3_, p_i47664_4_, p_i47664_5_, p_i47664_7_);
      this.enumOptions = p_i47664_6_;
   }

   @Nullable
   public GameSettings.Options getOption() {
      return this.enumOptions;
   }
}