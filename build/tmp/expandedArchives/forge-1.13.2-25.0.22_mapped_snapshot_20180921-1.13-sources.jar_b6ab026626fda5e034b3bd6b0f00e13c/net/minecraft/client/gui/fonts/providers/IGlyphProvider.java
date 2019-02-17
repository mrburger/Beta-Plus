package net.minecraft.client.gui.fonts.providers;

import java.io.Closeable;
import javax.annotation.Nullable;
import net.minecraft.client.gui.fonts.IGlyphInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IGlyphProvider extends Closeable {
   default void close() {
   }

   @Nullable
   default IGlyphInfo func_212248_a(char p_212248_1_) {
      return null;
   }
}