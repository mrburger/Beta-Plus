package net.minecraft.potion;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.registry.IRegistry;

public class PotionBrewing {
   private static final List<PotionBrewing.MixPredicate<PotionType>> POTION_TYPE_CONVERSIONS = Lists.newArrayList();
   private static final List<PotionBrewing.MixPredicate<Item>> POTION_ITEM_CONVERSIONS = Lists.newArrayList();
   private static final List<Ingredient> POTION_ITEMS = Lists.newArrayList();
   private static final Predicate<ItemStack> IS_POTION_ITEM = (p_210319_0_) -> {
      for(Ingredient ingredient : POTION_ITEMS) {
         if (ingredient.test(p_210319_0_)) {
            return true;
         }
      }

      return false;
   };

   public static boolean isReagent(ItemStack stack) {
      return isItemConversionReagent(stack) || isTypeConversionReagent(stack);
   }

   protected static boolean isItemConversionReagent(ItemStack stack) {
      int i = 0;

      for(int j = POTION_ITEM_CONVERSIONS.size(); i < j; ++i) {
         if ((POTION_ITEM_CONVERSIONS.get(i)).reagent.test(stack)) {
            return true;
         }
      }

      return false;
   }

   protected static boolean isTypeConversionReagent(ItemStack stack) {
      int i = 0;

      for(int j = POTION_TYPE_CONVERSIONS.size(); i < j; ++i) {
         if ((POTION_TYPE_CONVERSIONS.get(i)).reagent.test(stack)) {
            return true;
         }
      }

      return false;
   }

   public static boolean hasConversions(ItemStack input, ItemStack reagent) {
      if (!IS_POTION_ITEM.test(input)) {
         return false;
      } else {
         return hasItemConversions(input, reagent) || hasTypeConversions(input, reagent);
      }
   }

   protected static boolean hasItemConversions(ItemStack input, ItemStack reagent) {
      Item item = input.getItem();
      int i = 0;

      for(int j = POTION_ITEM_CONVERSIONS.size(); i < j; ++i) {
         PotionBrewing.MixPredicate<Item> mixpredicate = POTION_ITEM_CONVERSIONS.get(i);
         if (mixpredicate.input.get() == item && mixpredicate.reagent.test(reagent)) {
            return true;
         }
      }

      return false;
   }

   protected static boolean hasTypeConversions(ItemStack input, ItemStack reagent) {
      PotionType potiontype = PotionUtils.getPotionFromItem(input);
      int i = 0;

      for(int j = POTION_TYPE_CONVERSIONS.size(); i < j; ++i) {
         PotionBrewing.MixPredicate<PotionType> mixpredicate = POTION_TYPE_CONVERSIONS.get(i);
         if (mixpredicate.input.get() == potiontype && mixpredicate.reagent.test(reagent)) {
            return true;
         }
      }

      return false;
   }

   public static ItemStack doReaction(ItemStack reagent, ItemStack potionIn) {
      if (!potionIn.isEmpty()) {
         PotionType potiontype = PotionUtils.getPotionFromItem(potionIn);
         Item item = potionIn.getItem();
         int i = 0;

         for(int j = POTION_ITEM_CONVERSIONS.size(); i < j; ++i) {
            PotionBrewing.MixPredicate<Item> mixpredicate = POTION_ITEM_CONVERSIONS.get(i);
            if (mixpredicate.input.get() == item && mixpredicate.reagent.test(reagent)) {
               return PotionUtils.addPotionToItemStack(new ItemStack((IItemProvider)mixpredicate.output.get()), potiontype);
            }
         }

         i = 0;

         for(int k = POTION_TYPE_CONVERSIONS.size(); i < k; ++i) {
            PotionBrewing.MixPredicate<PotionType> mixpredicate1 = POTION_TYPE_CONVERSIONS.get(i);
            if (mixpredicate1.input.get() == potiontype && mixpredicate1.reagent.test(reagent)) {
               return PotionUtils.addPotionToItemStack(new ItemStack(item), (PotionType)mixpredicate1.output.get());
            }
         }
      }

      return potionIn;
   }

   public static void init() {
      func_196208_a(Items.POTION);
      func_196208_a(Items.SPLASH_POTION);
      func_196208_a(Items.LINGERING_POTION);
      func_196207_a(Items.POTION, Items.GUNPOWDER, Items.SPLASH_POTION);
      func_196207_a(Items.SPLASH_POTION, Items.DRAGON_BREATH, Items.LINGERING_POTION);
      addMix(PotionTypes.WATER, Items.GLISTERING_MELON_SLICE, PotionTypes.MUNDANE);
      addMix(PotionTypes.WATER, Items.GHAST_TEAR, PotionTypes.MUNDANE);
      addMix(PotionTypes.WATER, Items.RABBIT_FOOT, PotionTypes.MUNDANE);
      addMix(PotionTypes.WATER, Items.BLAZE_POWDER, PotionTypes.MUNDANE);
      addMix(PotionTypes.WATER, Items.SPIDER_EYE, PotionTypes.MUNDANE);
      addMix(PotionTypes.WATER, Items.SUGAR, PotionTypes.MUNDANE);
      addMix(PotionTypes.WATER, Items.MAGMA_CREAM, PotionTypes.MUNDANE);
      addMix(PotionTypes.WATER, Items.GLOWSTONE_DUST, PotionTypes.THICK);
      addMix(PotionTypes.WATER, Items.REDSTONE, PotionTypes.MUNDANE);
      addMix(PotionTypes.WATER, Items.NETHER_WART, PotionTypes.AWKWARD);
      addMix(PotionTypes.AWKWARD, Items.GOLDEN_CARROT, PotionTypes.NIGHT_VISION);
      addMix(PotionTypes.NIGHT_VISION, Items.REDSTONE, PotionTypes.LONG_NIGHT_VISION);
      addMix(PotionTypes.NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, PotionTypes.INVISIBILITY);
      addMix(PotionTypes.LONG_NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, PotionTypes.LONG_INVISIBILITY);
      addMix(PotionTypes.INVISIBILITY, Items.REDSTONE, PotionTypes.LONG_INVISIBILITY);
      addMix(PotionTypes.AWKWARD, Items.MAGMA_CREAM, PotionTypes.FIRE_RESISTANCE);
      addMix(PotionTypes.FIRE_RESISTANCE, Items.REDSTONE, PotionTypes.LONG_FIRE_RESISTANCE);
      addMix(PotionTypes.AWKWARD, Items.RABBIT_FOOT, PotionTypes.LEAPING);
      addMix(PotionTypes.LEAPING, Items.REDSTONE, PotionTypes.LONG_LEAPING);
      addMix(PotionTypes.LEAPING, Items.GLOWSTONE_DUST, PotionTypes.STRONG_LEAPING);
      addMix(PotionTypes.LEAPING, Items.FERMENTED_SPIDER_EYE, PotionTypes.SLOWNESS);
      addMix(PotionTypes.LONG_LEAPING, Items.FERMENTED_SPIDER_EYE, PotionTypes.LONG_SLOWNESS);
      addMix(PotionTypes.SLOWNESS, Items.REDSTONE, PotionTypes.LONG_SLOWNESS);
      addMix(PotionTypes.SLOWNESS, Items.GLOWSTONE_DUST, PotionTypes.STRONG_SLOWNESS);
      addMix(PotionTypes.AWKWARD, Items.TURTLE_HELMET, PotionTypes.TURTLE_MASTER);
      addMix(PotionTypes.TURTLE_MASTER, Items.REDSTONE, PotionTypes.LONG_TURTLE_MASTER);
      addMix(PotionTypes.TURTLE_MASTER, Items.GLOWSTONE_DUST, PotionTypes.STRONG_TURTLE_MASTER);
      addMix(PotionTypes.SWIFTNESS, Items.FERMENTED_SPIDER_EYE, PotionTypes.SLOWNESS);
      addMix(PotionTypes.LONG_SWIFTNESS, Items.FERMENTED_SPIDER_EYE, PotionTypes.LONG_SLOWNESS);
      addMix(PotionTypes.AWKWARD, Items.SUGAR, PotionTypes.SWIFTNESS);
      addMix(PotionTypes.SWIFTNESS, Items.REDSTONE, PotionTypes.LONG_SWIFTNESS);
      addMix(PotionTypes.SWIFTNESS, Items.GLOWSTONE_DUST, PotionTypes.STRONG_SWIFTNESS);
      addMix(PotionTypes.AWKWARD, Items.PUFFERFISH, PotionTypes.WATER_BREATHING);
      addMix(PotionTypes.WATER_BREATHING, Items.REDSTONE, PotionTypes.LONG_WATER_BREATHING);
      addMix(PotionTypes.AWKWARD, Items.GLISTERING_MELON_SLICE, PotionTypes.HEALING);
      addMix(PotionTypes.HEALING, Items.GLOWSTONE_DUST, PotionTypes.STRONG_HEALING);
      addMix(PotionTypes.HEALING, Items.FERMENTED_SPIDER_EYE, PotionTypes.HARMING);
      addMix(PotionTypes.STRONG_HEALING, Items.FERMENTED_SPIDER_EYE, PotionTypes.STRONG_HARMING);
      addMix(PotionTypes.HARMING, Items.GLOWSTONE_DUST, PotionTypes.STRONG_HARMING);
      addMix(PotionTypes.POISON, Items.FERMENTED_SPIDER_EYE, PotionTypes.HARMING);
      addMix(PotionTypes.LONG_POISON, Items.FERMENTED_SPIDER_EYE, PotionTypes.HARMING);
      addMix(PotionTypes.STRONG_POISON, Items.FERMENTED_SPIDER_EYE, PotionTypes.STRONG_HARMING);
      addMix(PotionTypes.AWKWARD, Items.SPIDER_EYE, PotionTypes.POISON);
      addMix(PotionTypes.POISON, Items.REDSTONE, PotionTypes.LONG_POISON);
      addMix(PotionTypes.POISON, Items.GLOWSTONE_DUST, PotionTypes.STRONG_POISON);
      addMix(PotionTypes.AWKWARD, Items.GHAST_TEAR, PotionTypes.REGENERATION);
      addMix(PotionTypes.REGENERATION, Items.REDSTONE, PotionTypes.LONG_REGENERATION);
      addMix(PotionTypes.REGENERATION, Items.GLOWSTONE_DUST, PotionTypes.STRONG_REGENERATION);
      addMix(PotionTypes.AWKWARD, Items.BLAZE_POWDER, PotionTypes.STRENGTH);
      addMix(PotionTypes.STRENGTH, Items.REDSTONE, PotionTypes.LONG_STRENGTH);
      addMix(PotionTypes.STRENGTH, Items.GLOWSTONE_DUST, PotionTypes.STRONG_STRENGTH);
      addMix(PotionTypes.WATER, Items.FERMENTED_SPIDER_EYE, PotionTypes.WEAKNESS);
      addMix(PotionTypes.WEAKNESS, Items.REDSTONE, PotionTypes.LONG_WEAKNESS);
      addMix(PotionTypes.AWKWARD, Items.PHANTOM_MEMBRANE, PotionTypes.SLOW_FALLING);
      addMix(PotionTypes.SLOW_FALLING, Items.REDSTONE, PotionTypes.LONG_SLOW_FALLING);
   }

   private static void func_196207_a(Item p_196207_0_, Item p_196207_1_, Item p_196207_2_) {
      if (!(p_196207_0_ instanceof ItemPotion)) {
         throw new IllegalArgumentException("Expected a potion, got: " + IRegistry.field_212630_s.getKey(p_196207_0_));
      } else if (!(p_196207_2_ instanceof ItemPotion)) {
         throw new IllegalArgumentException("Expected a potion, got: " + IRegistry.field_212630_s.getKey(p_196207_2_));
      } else {
         POTION_ITEM_CONVERSIONS.add(new PotionBrewing.MixPredicate<>(p_196207_0_, Ingredient.fromItems(p_196207_1_), p_196207_2_));
      }
   }

   private static void func_196208_a(Item p_196208_0_) {
      if (!(p_196208_0_ instanceof ItemPotion)) {
         throw new IllegalArgumentException("Expected a potion, got: " + IRegistry.field_212630_s.getKey(p_196208_0_));
      } else {
         POTION_ITEMS.add(Ingredient.fromItems(p_196208_0_));
      }
   }

   private static void addMix(PotionType p_193357_0_, Item p_193357_1_, PotionType p_193357_2_) {
      POTION_TYPE_CONVERSIONS.add(new PotionBrewing.MixPredicate<>(p_193357_0_, Ingredient.fromItems(p_193357_1_), p_193357_2_));
   }

   static class MixPredicate<T extends net.minecraftforge.registries.ForgeRegistryEntry<T>> {
      private final net.minecraftforge.registries.IRegistryDelegate<T> input;
      private final Ingredient reagent;
      private final net.minecraftforge.registries.IRegistryDelegate<T> output;

      public MixPredicate(T inputIn, Ingredient reagentIn, T outputIn) {
         this.input = inputIn.delegate;
         this.reagent = reagentIn;
         this.output = outputIn.delegate;
      }
   }
}