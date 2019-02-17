package net.minecraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemFishFood extends ItemFood {
   /** Indicates whether this fish is "cooked" or not. */
   private final boolean cooked;
   private final ItemFishFood.FishType type;

   public ItemFishFood(ItemFishFood.FishType fishTypeIn, boolean isCooked, Item.Properties builder) {
      super(0, 0.0F, false, builder);
      this.type = fishTypeIn;
      this.cooked = isCooked;
   }

   public int getHealAmount(ItemStack stack) {
      ItemFishFood.FishType itemfishfood$fishtype = ItemFishFood.FishType.byItemStack(stack);
      return this.cooked && itemfishfood$fishtype.canCook() ? itemfishfood$fishtype.getCookedHealAmount() : itemfishfood$fishtype.getUncookedHealAmount();
   }

   public float getSaturationModifier(ItemStack stack) {
      return this.cooked && this.type.canCook() ? this.type.getCookedSaturationModifier() : this.type.getUncookedSaturationModifier();
   }

   protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
      ItemFishFood.FishType itemfishfood$fishtype = ItemFishFood.FishType.byItemStack(stack);
      if (itemfishfood$fishtype == ItemFishFood.FishType.PUFFERFISH) {
         player.addPotionEffect(new PotionEffect(MobEffects.POISON, 1200, 3));
         player.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 300, 2));
         player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 300, 1));
      }

      super.onFoodEaten(stack, worldIn, player);
   }

   public static enum FishType {
      COD(2, 0.1F, 5, 0.6F),
      SALMON(2, 0.1F, 6, 0.8F),
      TROPICAL_FISH(1, 0.1F),
      PUFFERFISH(1, 0.1F);

      /** The amount that eating the uncooked version of this fish should heal the player. */
      private final int uncookedHealAmount;
      /** The saturation modifier to apply to the heal amount when the player eats the uncooked version of this fish. */
      private final float uncookedSaturationModifier;
      /** The amount that eating the cooked version of this fish should heal the player. */
      private final int cookedHealAmount;
      /** The saturation modifier to apply to the heal amount when the player eats the cooked version of this fish. */
      private final float cookedSaturationModifier;
      /** Indicates whether this type of fish has "raw" and "cooked" variants */
      private final boolean cookable;

      private FishType(int p_i49622_3_, float p_i49622_4_, int p_i49622_5_, float p_i49622_6_) {
         this.uncookedHealAmount = p_i49622_3_;
         this.uncookedSaturationModifier = p_i49622_4_;
         this.cookedHealAmount = p_i49622_5_;
         this.cookedSaturationModifier = p_i49622_6_;
         this.cookable = p_i49622_5_ != 0;
      }

      private FishType(int p_i49623_3_, float p_i49623_4_) {
         this(p_i49623_3_, p_i49623_4_, 0, 0.0F);
      }

      /**
       * Gets the amount that eating the uncooked version of this fish should heal the player.
       */
      public int getUncookedHealAmount() {
         return this.uncookedHealAmount;
      }

      /**
       * Gets the saturation modifier to apply to the heal amount when the player eats the uncooked version of this
       * fish.
       */
      public float getUncookedSaturationModifier() {
         return this.uncookedSaturationModifier;
      }

      /**
       * Gets the amount that eating the cooked version of this fish should heal the player.
       */
      public int getCookedHealAmount() {
         return this.cookedHealAmount;
      }

      /**
       * Gets the saturation modifier to apply to the heal amount when the player eats the cooked version of this fish.
       */
      public float getCookedSaturationModifier() {
         return this.cookedSaturationModifier;
      }

      /**
       * Gets a value indicating whether this type of fish has "raw" and "cooked" variants.
       */
      public boolean canCook() {
         return this.cookable;
      }

      /**
       * Gets the FishType that corresponds to the given ItemStack, defaulting to COD if the given ItemStack does not
       * actually contain a fish.
       */
      public static ItemFishFood.FishType byItemStack(ItemStack stack) {
         Item item = stack.getItem();
         return item instanceof ItemFishFood ? ((ItemFishFood)item).type : COD;
      }
   }
}