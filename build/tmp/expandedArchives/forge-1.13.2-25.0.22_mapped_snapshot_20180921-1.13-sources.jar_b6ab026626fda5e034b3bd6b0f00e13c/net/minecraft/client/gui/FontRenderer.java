package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.fonts.Font;
import net.minecraft.client.gui.fonts.IGlyph;
import net.minecraft.client.gui.fonts.TexturedGlyph;
import net.minecraft.client.gui.fonts.providers.IGlyphProvider;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class FontRenderer implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   /** the height in pixels of default text */
   public int FONT_HEIGHT = 9;
   public Random fontRandom = new Random();
   /** The RenderEngine used to load and setup glyph textures. */
   private final TextureManager textureManager;
   private final Font font;
   /** If true, the Unicode Bidirectional Algorithm should be run before rendering any string. */
   private boolean bidiFlag;

   public FontRenderer(TextureManager textureManagerIn, Font fontIn) {
      this.textureManager = textureManagerIn;
      this.font = fontIn;
   }

   public void setGlyphProviders(List<IGlyphProvider> gliphProviders) {
      this.font.setGlyphProviders(gliphProviders);
   }

   public void close() {
      this.font.close();
   }

   /**
    * Draws the specified string with a shadow.
    */
   public int drawStringWithShadow(String text, float x, float y, int color) {
      GlStateManager.enableAlphaTest();
      return this.renderString(text, x, y, color, true);
   }

   public int drawString(String text, float x, float y, int color) {
      GlStateManager.enableAlphaTest();
      return this.renderString(text, x, y, color, false);
   }

   /**
    * Apply Unicode Bidirectional Algorithm to string and return a new possibly reordered string for visual rendering.
    */
   private String bidiReorder(String text) {
      try {
         Bidi bidi = new Bidi((new ArabicShaping(8)).shape(text), 127);
         bidi.setReorderingMode(0);
         return bidi.writeReordered(2);
      } catch (ArabicShapingException var3) {
         return text;
      }
   }

   /**
    * Render single line string by setting GL color, current (posX,posY), and calling renderStringAtPos()
    */
   private int renderString(String text, float x, float y, int color, boolean dropShadow) {
      if (text == null) {
         return 0;
      } else {
         if (this.bidiFlag) {
            text = this.bidiReorder(text);
         }

         if ((color & -67108864) == 0) {
            color |= -16777216;
         }

         if (dropShadow) {
            this.renderStringAtPos(text, x, y, color, true);
         }

         x = this.renderStringAtPos(text, x, y, color, false);
         return (int)x + (dropShadow ? 1 : 0);
      }
   }

   private float renderStringAtPos(String text, float x, float y, int color, boolean isShadow) {
      float f = isShadow ? 0.25F : 1.0F;
      float f1 = (float)(color >> 16 & 255) / 255.0F * f;
      float f2 = (float)(color >> 8 & 255) / 255.0F * f;
      float f3 = (float)(color & 255) / 255.0F * f;
      float f4 = f1;
      float f5 = f2;
      float f6 = f3;
      float f7 = (float)(color >> 24 & 255) / 255.0F;
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      ResourceLocation resourcelocation = null;
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      boolean flag = false;
      boolean flag1 = false;
      boolean flag2 = false;
      boolean flag3 = false;
      boolean flag4 = false;
      List<FontRenderer.Entry> list = Lists.newArrayList();

      for(int i = 0; i < text.length(); ++i) {
         char c0 = text.charAt(i);
         if (c0 == 167 && i + 1 < text.length()) {
            TextFormatting textformatting = TextFormatting.fromFormattingCode(text.charAt(i + 1));
            if (textformatting != null) {
               if (textformatting.isNormalStyle()) {
                  flag = false;
                  flag1 = false;
                  flag4 = false;
                  flag3 = false;
                  flag2 = false;
                  f4 = f1;
                  f5 = f2;
                  f6 = f3;
               }

               if (textformatting.getColor() != null) {
                  int j = textformatting.getColor();
                  f4 = (float)(j >> 16 & 255) / 255.0F * f;
                  f5 = (float)(j >> 8 & 255) / 255.0F * f;
                  f6 = (float)(j & 255) / 255.0F * f;
               } else if (textformatting == TextFormatting.OBFUSCATED) {
                  flag = true;
               } else if (textformatting == TextFormatting.BOLD) {
                  flag1 = true;
               } else if (textformatting == TextFormatting.STRIKETHROUGH) {
                  flag4 = true;
               } else if (textformatting == TextFormatting.UNDERLINE) {
                  flag3 = true;
               } else if (textformatting == TextFormatting.ITALIC) {
                  flag2 = true;
               }
            }

            ++i;
         } else {
            IGlyph iglyph = this.font.findGlyph(c0);
            TexturedGlyph texturedglyph = flag && c0 != ' ' ? this.font.obfuscate(iglyph) : this.font.getGlyph(c0);
            ResourceLocation resourcelocation1 = texturedglyph.getTextureLocation();
            if (resourcelocation1 != null) {
               if (resourcelocation != resourcelocation1) {
                  tessellator.draw();
                  this.textureManager.bindTexture(resourcelocation1);
                  bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                  resourcelocation = resourcelocation1;
               }

               float f8 = flag1 ? iglyph.getBoldOffset() : 0.0F;
               float f9 = isShadow ? iglyph.getShadowOffset() : 0.0F;
               this.func_212452_a(texturedglyph, flag1, flag2, f8, x + f9, y + f9, bufferbuilder, f4, f5, f6, f7);
            }

            float f10 = iglyph.getAdvance(flag1);
            float f11 = isShadow ? 1.0F : 0.0F;
            if (flag4) {
               list.add(new FontRenderer.Entry(x + f11 - 1.0F, y + f11 + (float)this.FONT_HEIGHT / 2.0F, x + f11 + f10, y + f11 + (float)this.FONT_HEIGHT / 2.0F - 1.0F, f4, f5, f6, f7));
            }

            if (flag3) {
               list.add(new FontRenderer.Entry(x + f11 - 1.0F, y + f11 + (float)this.FONT_HEIGHT, x + f11 + f10, y + f11 + (float)this.FONT_HEIGHT - 1.0F, f4, f5, f6, f7));
            }

            x += f10;
         }
      }

      tessellator.draw();
      if (!list.isEmpty()) {
         GlStateManager.disableTexture2D();
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);

         for(FontRenderer.Entry fontrenderer$entry : list) {
            fontrenderer$entry.pipe(bufferbuilder);
         }

         tessellator.draw();
         GlStateManager.enableTexture2D();
      }

      return x;
   }

   private void func_212452_a(TexturedGlyph p_212452_1_, boolean p_212452_2_, boolean p_212452_3_, float p_212452_4_, float p_212452_5_, float p_212452_6_, BufferBuilder p_212452_7_, float p_212452_8_, float p_212452_9_, float p_212452_10_, float p_212452_11_) {
      p_212452_1_.render(this.textureManager, p_212452_3_, p_212452_5_, p_212452_6_, p_212452_7_, p_212452_8_, p_212452_9_, p_212452_10_, p_212452_11_);
      if (p_212452_2_) {
         p_212452_1_.render(this.textureManager, p_212452_3_, p_212452_5_ + p_212452_4_, p_212452_6_, p_212452_7_, p_212452_8_, p_212452_9_, p_212452_10_, p_212452_11_);
      }

   }

   /**
    * Returns the width of this string. Equivalent of FontMetrics.stringWidth(String s).
    */
   public int getStringWidth(String text) {
      if (text == null) {
         return 0;
      } else {
         float f = 0.0F;
         boolean flag = false;

         for(int i = 0; i < text.length(); ++i) {
            char c0 = text.charAt(i);
            if (c0 == 167 && i < text.length() - 1) {
               ++i;
               TextFormatting textformatting = TextFormatting.fromFormattingCode(text.charAt(i));
               if (textformatting == TextFormatting.BOLD) {
                  flag = true;
               } else if (textformatting != null && textformatting.isNormalStyle()) {
                  flag = false;
               }
            } else {
               f += this.font.findGlyph(c0).getAdvance(flag);
            }
         }

         return MathHelper.ceil(f);
      }
   }

   private float getCharWidth(char character) {
      return character == 167 ? 0.0F : (float)MathHelper.ceil(this.font.findGlyph(character).getAdvance(false));
   }

   /**
    * Trims a string to fit a specified Width.
    */
   public String trimStringToWidth(String text, int width) {
      return this.trimStringToWidth(text, width, false);
   }

   /**
    * Trims a string to a specified width, optionally starting from the end and working backwards.
    * <h3>Samples:</h3>
    * (Assuming that {@link #getCharWidth(char)} returns <code>6</code> for all of the characters in
    * <code>0123456789</code> on the current resource pack)
    * <table>
    * <tr><th>Input</th><th>Returns</th></tr>
    * <tr><td><code>trimStringToWidth("0123456789", 1, false)</code></td><td><samp>""</samp></td></tr>
    * <tr><td><code>trimStringToWidth("0123456789", 6, false)</code></td><td><samp>"0"</samp></td></tr>
    * <tr><td><code>trimStringToWidth("0123456789", 29, false)</code></td><td><samp>"0123"</samp></td></tr>
    * <tr><td><code>trimStringToWidth("0123456789", 30, false)</code></td><td><samp>"01234"</samp></td></tr>
    * <tr><td><code>trimStringToWidth("0123456789", 9001, false)</code></td><td><samp>"0123456789"</samp></td></tr>
    * <tr><td><code>trimStringToWidth("0123456789", 1, true)</code></td><td><samp>""</samp></td></tr>
    * <tr><td><code>trimStringToWidth("0123456789", 6, true)</code></td><td><samp>"9"</samp></td></tr>
    * <tr><td><code>trimStringToWidth("0123456789", 29, true)</code></td><td><samp>"6789"</samp></td></tr>
    * <tr><td><code>trimStringToWidth("0123456789", 30, true)</code></td><td><samp>"56789"</samp></td></tr>
    * <tr><td><code>trimStringToWidth("0123456789", 9001, true)</code></td><td><samp>"0123456789"</samp></td></tr>
    * </table>
    */
   public String trimStringToWidth(String text, int width, boolean reverse) {
      StringBuilder stringbuilder = new StringBuilder();
      float f = 0.0F;
      int i = reverse ? text.length() - 1 : 0;
      int j = reverse ? -1 : 1;
      boolean flag = false;
      boolean flag1 = false;

      for(int k = i; k >= 0 && k < text.length() && f < (float)width; k += j) {
         char c0 = text.charAt(k);
         if (flag) {
            flag = false;
            TextFormatting textformatting = TextFormatting.fromFormattingCode(c0);
            if (textformatting == TextFormatting.BOLD) {
               flag1 = true;
            } else if (textformatting != null && textformatting.isNormalStyle()) {
               flag1 = false;
            }
         } else if (c0 == 167) {
            flag = true;
         } else {
            f += this.getCharWidth(c0);
            if (flag1) {
               ++f;
            }
         }

         if (f > (float)width) {
            break;
         }

         if (reverse) {
            stringbuilder.insert(0, c0);
         } else {
            stringbuilder.append(c0);
         }
      }

      return stringbuilder.toString();
   }

   /**
    * Remove all newline characters from the end of the string
    */
   private String trimStringNewline(String text) {
      while(text != null && text.endsWith("\n")) {
         text = text.substring(0, text.length() - 1);
      }

      return text;
   }

   /**
    * Splits and draws a String with wordwrap (maximum length is parameter k)
    */
   public void drawSplitString(String str, int x, int y, int wrapWidth, int textColor) {
      str = this.trimStringNewline(str);
      this.renderSplitString(str, x, y, wrapWidth, textColor);
   }

   private void renderSplitString(String str, int x, int y, int wrapWidth, int textColor) {
      for(String s : this.listFormattedStringToWidth(str, wrapWidth)) {
         float f = (float)x;
         if (this.bidiFlag) {
            int i = this.getStringWidth(this.bidiReorder(s));
            f += (float)(wrapWidth - i);
         }

         this.renderString(s, f, (float)y, textColor, false);
         y += this.FONT_HEIGHT;
      }

   }

   /**
    * Returns the height (in pixels) of the given string if it is wordwrapped to the given max width.
    */
   public int getWordWrappedHeight(String str, int maxLength) {
      return this.FONT_HEIGHT * this.listFormattedStringToWidth(str, maxLength).size();
   }

   /**
    * Set bidiFlag to control if the Unicode Bidirectional Algorithm should be run before rendering any string.
    */
   public void setBidiFlag(boolean bidiFlagIn) {
      this.bidiFlag = bidiFlagIn;
   }

   /**
    * Breaks a string into a list of pieces where the width of each line is always less than or equal to the provided
    * width. Formatting codes will be preserved between lines.
    */
   public List<String> listFormattedStringToWidth(String str, int wrapWidth) {
      return Arrays.asList(this.wrapFormattedStringToWidth(str, wrapWidth).split("\n"));
   }

   /**
    * Inserts newline and formatting into a string to wrap it within the specified width.
    */
   public String wrapFormattedStringToWidth(String str, int wrapWidth) {
      String s;
      String s1;
      for(s = ""; !str.isEmpty(); s = s + s1 + "\n") {
         int i = this.sizeStringToWidth(str, wrapWidth);
         if (str.length() <= i) {
            return s + str;
         }

         s1 = str.substring(0, i);
         char c0 = str.charAt(i);
         boolean flag = c0 == ' ' || c0 == '\n';
         str = TextFormatting.getFormatString(s1) + str.substring(i + (flag ? 1 : 0));
      }

      return s;
   }

   /**
    * Determines how many characters from the string will fit into the specified width.
    */
   private int sizeStringToWidth(String str, int wrapWidth) {
      int i = Math.max(1, wrapWidth);
      int j = str.length();
      float f = 0.0F;
      int k = 0;
      int l = -1;
      boolean flag = false;

      for(boolean flag1 = true; k < j; ++k) {
         char c0 = str.charAt(k);
         switch(c0) {
         case '\n':
            --k;
            break;
         case ' ':
            l = k;
         default:
            if (f != 0.0F) {
               flag1 = false;
            }

            f += this.getCharWidth(c0);
            if (flag) {
               ++f;
            }
            break;
         case '\u00a7':
            if (k < j - 1) {
               ++k;
               TextFormatting textformatting = TextFormatting.fromFormattingCode(str.charAt(k));
               if (textformatting == TextFormatting.BOLD) {
                  flag = true;
               } else if (textformatting != null && textformatting.isNormalStyle()) {
                  flag = false;
               }
            }
         }

         if (c0 == '\n') {
            ++k;
            l = k;
            break;
         }

         if (f > (float)i) {
            if (flag1) {
               ++k;
            }
            break;
         }
      }

      return k != j && l != -1 && l < k ? l : k;
   }

   /**
    * Get bidiFlag that controls if the Unicode Bidirectional Algorithm should be run before rendering any string
    */
   public boolean getBidiFlag() {
      return this.bidiFlag;
   }

   @OnlyIn(Dist.CLIENT)
   static class Entry {
      protected final float x1;
      protected final float y1;
      protected final float x2;
      protected final float y2;
      protected final float red;
      protected final float green;
      protected final float blue;
      protected final float alpha;

      private Entry(float x1, float y1, float x2, float y2, float red, float green, float blue, float alpha) {
         this.x1 = x1;
         this.y1 = y1;
         this.x2 = x2;
         this.y2 = y2;
         this.red = red;
         this.green = green;
         this.blue = blue;
         this.alpha = alpha;
      }

      public void pipe(BufferBuilder buffer) {
         buffer.pos((double)this.x1, (double)this.y1, 0.0D).color(this.red, this.green, this.blue, this.alpha).endVertex();
         buffer.pos((double)this.x2, (double)this.y1, 0.0D).color(this.red, this.green, this.blue, this.alpha).endVertex();
         buffer.pos((double)this.x2, (double)this.y2, 0.0D).color(this.red, this.green, this.blue, this.alpha).endVertex();
         buffer.pos((double)this.x1, (double)this.y2, 0.0D).color(this.red, this.green, this.blue, this.alpha).endVertex();
      }
   }
}