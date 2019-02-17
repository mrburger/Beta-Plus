package net.minecraft.enchantment;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAbstractSkull;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.ItemTrident;

public enum EnumEnchantmentType implements net.minecraftforge.common.IExtensibleEnum {
   ALL {
      /**
       * Return true if the item passed can be enchanted by a enchantment of this type.
       */
      public boolean canEnchantItem(Item itemIn) {
         for(EnumEnchantmentType enumenchantmenttype : EnumEnchantmentType.values()) {
            if (enumenchantmenttype != EnumEnchantmentType.ALL && enumenchantmenttype.canEnchantItem(itemIn)) {
               return true;
            }
         }

         return false;
      }
   },
   ARMOR {
      /**
       * Return true if the item passed can be enchanted by a enchantment of this type.
       */
      public boolean canEnchantItem(Item itemIn) {
         return itemIn instanceof ItemArmor;
      }
   },
   ARMOR_FEET {
      /**
       * Return true if the item passed can be enchanted by a enchantment of this type.
       */
      public boolean canEnchantItem(Item itemIn) {
         return itemIn instanceof ItemArmor && ((ItemArmor)itemIn).getEquipmentSlot() == EntityEquipmentSlot.FEET;
      }
   },
   ARMOR_LEGS {
      /**
       * Return true if the item passed can be enchanted by a enchantment of this type.
       */
      public boolean canEnchantItem(Item itemIn) {
         return itemIn instanceof ItemArmor && ((ItemArmor)itemIn).getEquipmentSlot() == EntityEquipmentSlot.LEGS;
      }
   },
   ARMOR_CHEST {
      /**
       * Return true if the item passed can be enchanted by a enchantment of this type.
       */
      public boolean canEnchantItem(Item itemIn) {
         return itemIn instanceof ItemArmor && ((ItemArmor)itemIn).getEquipmentSlot() == EntityEquipmentSlot.CHEST;
      }
   },
   ARMOR_HEAD {
      /**
       * Return true if the item passed can be enchanted by a enchantment of this type.
       */
      public boolean canEnchantItem(Item itemIn) {
         return itemIn instanceof ItemArmor && ((ItemArmor)itemIn).getEquipmentSlot() == EntityEquipmentSlot.HEAD;
      }
   },
   WEAPON {
      /**
       * Return true if the item passed can be enchanted by a enchantment of this type.
       */
      public boolean canEnchantItem(Item itemIn) {
         return itemIn instanceof ItemSword;
      }
   },
   DIGGER {
      /**
       * Return true if the item passed can be enchanted by a enchantment of this type.
       */
      public boolean canEnchantItem(Item itemIn) {
         return itemIn instanceof ItemTool;
      }
   },
   FISHING_ROD {
      /**
       * Return true if the item passed can be enchanted by a enchantment of this type.
       */
      public boolean canEnchantItem(Item itemIn) {
         return itemIn instanceof ItemFishingRod;
      }
   },
   TRIDENT {
      /**
       * Return true if the item passed can be enchanted by a enchantment of this type.
       */
      public boolean canEnchantItem(Item itemIn) {
         return itemIn instanceof ItemTrident;
      }
   },
   BREAKABLE {
      /**
       * Return true if the item passed can be enchanted by a enchantment of this type.
       */
      public boolean canEnchantItem(Item itemIn) {
         return itemIn.isDamageable();
      }
   },
   BOW {
      /**
       * Return true if the item passed can be enchanted by a enchantment of this type.
       */
      public boolean canEnchantItem(Item itemIn) {
         return itemIn instanceof ItemBow;
      }
   },
   WEARABLE {
      /**
       * Return true if the item passed can be enchanted by a enchantment of this type.
       */
      public boolean canEnchantItem(Item itemIn) {
         Block block = Block.getBlockFromItem(itemIn);
         return itemIn instanceof ItemArmor || itemIn instanceof ItemElytra || block instanceof BlockAbstractSkull || block instanceof BlockPumpkin;
      }
   };

   private EnumEnchantmentType() {
   }

   private java.util.function.Predicate<Item> delegate;
   private EnumEnchantmentType(java.util.function.Predicate<Item> delegate) {
      this.delegate = delegate;
   }

   public static EnumEnchantmentType create(String name, java.util.function.Predicate<Item> delegate) {
      throw new IllegalStateException("Enum not extended");
   }

   /**
    * Return true if the item passed can be enchanted by a enchantment of this type.
    */
   public boolean canEnchantItem(Item itemIn) {
      return this.delegate == null ? false : this.delegate.test(itemIn);
   }
}