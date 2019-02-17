package net.minecraft.client.gui.fonts.providers;

import javax.annotation.Nullable;
import net.minecraft.client.gui.fonts.DefaultGlyph;
import net.minecraft.client.gui.fonts.IGlyphInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DefaultGlyphProvider implements IGlyphProvider {
   @Nullable
   public IGlyphInfo func_212248_a(char p_212248_1_) {
      return DefaultGlyph.INSTANCE;
   }
}