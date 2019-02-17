package net.minecraft.item;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum EnumDyeColor implements IStringSerializable {
   WHITE(0, "white", 16383998, MaterialColor.SNOW, 15790320),
   ORANGE(1, "orange", 16351261, MaterialColor.ADOBE, 15435844),
   MAGENTA(2, "magenta", 13061821, MaterialColor.MAGENTA, 12801229),
   LIGHT_BLUE(3, "light_blue", 3847130, MaterialColor.LIGHT_BLUE, 6719955),
   YELLOW(4, "yellow", 16701501, MaterialColor.YELLOW, 14602026),
   LIME(5, "lime", 8439583, MaterialColor.LIME, 4312372),
   PINK(6, "pink", 15961002, MaterialColor.PINK, 14188952),
   GRAY(7, "gray", 4673362, MaterialColor.GRAY, 4408131),
   LIGHT_GRAY(8, "light_gray", 10329495, MaterialColor.LIGHT_GRAY, 11250603),
   CYAN(9, "cyan", 1481884, MaterialColor.CYAN, 2651799),
   PURPLE(10, "purple", 8991416, MaterialColor.PURPLE, 8073150),
   BLUE(11, "blue", 3949738, MaterialColor.BLUE, 2437522),
   BROWN(12, "brown", 8606770, MaterialColor.BROWN, 5320730),
   GREEN(13, "green", 6192150, MaterialColor.GREEN, 3887386),
   RED(14, "red", 11546150, MaterialColor.RED, 11743532),
   BLACK(15, "black", 1908001, MaterialColor.BLACK, 1973019);

   private static final EnumDyeColor[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(EnumDyeColor::getId)).toArray((p_199795_0_) -> {
      return new EnumDyeColor[p_199795_0_];
   });
   private static final Int2ObjectOpenHashMap<EnumDyeColor> field_196063_r = new Int2ObjectOpenHashMap<>(Arrays.stream(values()).collect(Collectors.toMap((p_199793_0_) -> {
      return p_199793_0_.field_196067_y;
   }, (p_199794_0_) -> {
      return p_199794_0_;
   })));
   private final int id;
   private final String translationKey;
   private final MaterialColor mapColor;
   /** An int containing the corresponding RGB color for this dye color. */
   private final int colorValue;
   private final int swappedColorValue;
   /**
    * An array containing 3 floats ranging from 0.0 to 1.0: the red, green, and blue components of the corresponding
    * color.
    */
   private final float[] colorComponentValues;
   private final int field_196067_y;
   private final net.minecraft.tags.Tag<Item> tag;

   private EnumDyeColor(int p_i47810_3_, String p_i47810_4_, int p_i47810_5_, MaterialColor p_i47810_6_, int p_i47810_7_) {
      this.id = p_i47810_3_;
      this.translationKey = p_i47810_4_;
      this.colorValue = p_i47810_5_;
      this.mapColor = p_i47810_6_;
      int i = (p_i47810_5_ & 16711680) >> 16;
      int j = (p_i47810_5_ & '\uff00') >> 8;
      int k = (p_i47810_5_ & 255) >> 0;
      this.swappedColorValue = k << 16 | j << 8 | i << 0;
      this.colorComponentValues = new float[]{(float)i / 255.0F, (float)j / 255.0F, (float)k / 255.0F};
      this.field_196067_y = p_i47810_7_;
      this.tag = new net.minecraft.tags.ItemTags.Wrapper(new net.minecraft.util.ResourceLocation("minecraft", "dyes_" + p_i47810_4_));
   }

   public int getId() {
      return this.id;
   }

   public String getTranslationKey() {
      return this.translationKey;
   }

   @OnlyIn(Dist.CLIENT)
   public int func_196057_c() {
      return this.swappedColorValue;
   }

   /**
    * Gets an array containing 3 floats ranging from 0.0 to 1.0: the red, green, and blue components of the
    * corresponding color.
    */
   public float[] getColorComponentValues() {
      return this.colorComponentValues;
   }

   public MaterialColor getMapColor() {
      return this.mapColor;
   }

   public int func_196060_f() {
      return this.field_196067_y;
   }

   public static EnumDyeColor byId(int p_196056_0_) {
      if (p_196056_0_ < 0 || p_196056_0_ >= VALUES.length) {
         p_196056_0_ = 0;
      }

      return VALUES[p_196056_0_];
   }

   public static EnumDyeColor byTranslationKey(String p_204271_0_) {
      for(EnumDyeColor enumdyecolor : values()) {
         if (enumdyecolor.translationKey.equals(p_204271_0_)) {
            return enumdyecolor;
         }
      }

      return WHITE;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static EnumDyeColor func_196058_b(int p_196058_0_) {
      return field_196063_r.get(p_196058_0_);
   }

   public String toString() {
      return this.translationKey;
   }

   public String getName() {
      return this.translationKey;
   }

   public net.minecraft.tags.Tag<Item> getTag() {
      return tag;
   }

   @Nullable
   public static EnumDyeColor getColor(ItemStack stack) {
      if (stack.getItem() instanceof ItemDye)
         return ((ItemDye)stack.getItem()).getDyeColor();

      for (EnumDyeColor color : VALUES) {
         if (stack.getItem().isIn(color.getTag()))
             return color;
      }

      return null;
   }
}