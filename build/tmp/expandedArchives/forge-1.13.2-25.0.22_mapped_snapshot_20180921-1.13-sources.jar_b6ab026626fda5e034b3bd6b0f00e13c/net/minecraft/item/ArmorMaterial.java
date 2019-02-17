package net.minecraft.item;

import java.util.function.Supplier;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyLoadBase;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum ArmorMaterial implements IArmorMaterial, net.minecraftforge.common.IExtensibleEnum {
   LEATHER("leather", 5, new int[]{1, 2, 3, 1}, 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F, () -> {
      return Ingredient.fromItems(Items.LEATHER);
   }),
   CHAIN("chainmail", 15, new int[]{1, 4, 5, 2}, 12, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 0.0F, () -> {
      return Ingredient.fromItems(Items.IRON_INGOT);
   }),
   IRON("iron", 15, new int[]{2, 5, 6, 2}, 9, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F, () -> {
      return Ingredient.fromItems(Items.IRON_INGOT);
   }),
   GOLD("gold", 7, new int[]{1, 3, 5, 2}, 25, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, 0.0F, () -> {
      return Ingredient.fromItems(Items.GOLD_INGOT);
   }),
   DIAMOND("diamond", 33, new int[]{3, 6, 8, 3}, 10, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 2.0F, () -> {
      return Ingredient.fromItems(Items.DIAMOND);
   }),
   TURTLE("turtle", 25, new int[]{2, 5, 6, 2}, 9, SoundEvents.ITEM_ARMOR_EQUIP_TURTLE, 0.0F, () -> {
      return Ingredient.fromItems(Items.SCUTE);
   });

   /** Holds the 'base' maxDamage that each armorType have. */
   private static final int[] MAX_DAMAGE_ARRAY = new int[]{13, 15, 16, 11};
   private final String name;
   /**
    * Holds the maximum damage factor (each piece multiply this by it's own value) of the material, this is the item
    * damage (how much can absorb before breaks)
    */
   private final int maxDamageFactor;
   /**
    * Holds the damage reduction (each 1 points is half a shield on gui) of each piece of armor (helmet, plate, legs and
    * boots)
    */
   private final int[] damageReductionAmountArray;
   /** Return the enchantability factor of the material */
   private final int enchantability;
   private final SoundEvent soundEvent;
   private final float toughness;
   private final LazyLoadBase<Ingredient> repairMaterial;

   private ArmorMaterial(String nameIn, int p_i48533_4_, int[] p_i48533_5_, int p_i48533_6_, SoundEvent p_i48533_7_, float p_i48533_8_, Supplier<Ingredient> p_i48533_9_) {
      this.name = nameIn;
      this.maxDamageFactor = p_i48533_4_;
      this.damageReductionAmountArray = p_i48533_5_;
      this.enchantability = p_i48533_6_;
      this.soundEvent = p_i48533_7_;
      this.toughness = p_i48533_8_;
      this.repairMaterial = new LazyLoadBase<>(p_i48533_9_);
   }

   public int getDurability(EntityEquipmentSlot slotIn) {
      return MAX_DAMAGE_ARRAY[slotIn.getIndex()] * this.maxDamageFactor;
   }

   public int getDamageReductionAmount(EntityEquipmentSlot slotIn) {
      return this.damageReductionAmountArray[slotIn.getIndex()];
   }

   public int getEnchantability() {
      return this.enchantability;
   }

   public SoundEvent getSoundEvent() {
      return this.soundEvent;
   }

   public Ingredient getRepairMaterial() {
      return this.repairMaterial.getValue();
   }

   @OnlyIn(Dist.CLIENT)
   public String getName() {
      return this.name;
   }

   public float getToughness() {
      return this.toughness;
   }

   public static ArmorMaterial create(String name, String nameIn, int p_i48533_4_, int[] p_i48533_5_, int p_i48533_6_, SoundEvent p_i48533_7_, float p_i48533_8_, Supplier<Ingredient> p_i48533_9_) {
      throw new IllegalStateException("Enum not extended");
   }
}