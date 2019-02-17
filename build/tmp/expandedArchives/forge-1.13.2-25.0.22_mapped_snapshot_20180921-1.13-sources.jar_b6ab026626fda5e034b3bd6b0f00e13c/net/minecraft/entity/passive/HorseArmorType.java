package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum HorseArmorType implements net.minecraftforge.common.IExtensibleEnum {
   NONE(0),
   IRON(5, "iron", "meo"),
   GOLD(7, "gold", "goo"),
   DIAMOND(11, "diamond", "dio");

   private final String textureName;
   private final String hash;
   private final int protection;
   private Item item;

   private HorseArmorType(int armorStrengthIn) {
      this.protection = armorStrengthIn;
      this.textureName = null;
      this.hash = "";
   }

   private HorseArmorType(int armorStrengthIn, String p_i46800_4_, String p_i46800_5_) {
      this.protection = armorStrengthIn;
      this.textureName = "textures/entity/horse/armor/horse_armor_" + p_i46800_4_ + ".png";
      this.hash = p_i46800_5_;
   }

   private HorseArmorType(int strength, String texture, String hash, Item item) {
       this(strength, texture, hash);
       this.item = item;
   }

   public static HorseArmorType create(String name, int strength, String texture, String hash, Item item) {
      throw new IllegalStateException("Enum not extended");
   }

   public int getOrdinal() {
      return this.ordinal();
   }

   @OnlyIn(Dist.CLIENT)
   public String getHash() {
      return this.hash;
   }

   public int getProtection() {
      return this.protection;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public String getTextureName() {
      return this.textureName;
   }

   @Deprecated //Forge: Use by name, or ItemStack, ordinals for modded ones are not guaranteed to be in the same order each run.
   public static HorseArmorType getByOrdinal(int ordinal) {
      return values()[ordinal];
   }

   public static HorseArmorType getByItemStack(ItemStack stack) {
      return stack.isEmpty() ? NONE : stack.getHorseArmorType();
   }

   @Deprecated //Forge: Use ItemStack.getHorseArmorType
   public static HorseArmorType getByItem(Item itemIn) {
      if (itemIn == Items.IRON_HORSE_ARMOR) {
         return IRON;
      } else if (itemIn == Items.GOLDEN_HORSE_ARMOR) {
         return GOLD;
      } else if (itemIn == Items.DIAMOND_HORSE_ARMOR) {
          return DIAMOND;
      } else {
         for (HorseArmorType type : values()) {
            if (type.item != null && type.item.delegate.get() == itemIn.delegate.get())
               return type;
         }
         return NONE;
      }
   }

   @Deprecated //Forge: Use ItemStack version
   public static boolean isHorseArmor(Item itemIn) {
      return getByItem(itemIn) != NONE;
   }

   public static boolean isHorseArmor(ItemStack stack) {
      return stack.getHorseArmorType() != NONE;
   }
}