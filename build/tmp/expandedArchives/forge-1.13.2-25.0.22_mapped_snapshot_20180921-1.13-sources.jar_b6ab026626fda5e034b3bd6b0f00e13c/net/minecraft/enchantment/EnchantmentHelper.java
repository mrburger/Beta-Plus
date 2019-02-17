package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.IRegistry;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;

public class EnchantmentHelper {
   /**
    * Returns the level of enchantment on the ItemStack passed.
    */
   public static int getEnchantmentLevel(Enchantment enchID, ItemStack stack) {
      if (stack.isEmpty()) {
         return 0;
      } else {
         ResourceLocation resourcelocation = IRegistry.field_212628_q.getKey(enchID);
         NBTTagList nbttaglist = stack.getEnchantmentTagList();

         for(int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);
            ResourceLocation resourcelocation1 = ResourceLocation.makeResourceLocation(nbttagcompound.getString("id"));
            if (resourcelocation1 != null && resourcelocation1.equals(resourcelocation)) {
               return nbttagcompound.getInt("lvl");
            }
         }

         return 0;
      }
   }

   /**
    * Return the enchantments for the specified stack.
    */
   public static Map<Enchantment, Integer> getEnchantments(ItemStack stack) {
      Map<Enchantment, Integer> map = Maps.newLinkedHashMap();
      NBTTagList nbttaglist = stack.getItem() == Items.ENCHANTED_BOOK ? ItemEnchantedBook.getEnchantments(stack) : stack.getEnchantmentTagList();

      for(int i = 0; i < nbttaglist.size(); ++i) {
         NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);
         Enchantment enchantment = IRegistry.field_212628_q.func_212608_b(ResourceLocation.makeResourceLocation(nbttagcompound.getString("id")));
         if (enchantment != null) {
            map.put(enchantment, nbttagcompound.getInt("lvl"));
         }
      }

      return map;
   }

   /**
    * Set the enchantments for the specified stack.
    */
   public static void setEnchantments(Map<Enchantment, Integer> enchMap, ItemStack stack) {
      NBTTagList nbttaglist = new NBTTagList();

      for(Entry<Enchantment, Integer> entry : enchMap.entrySet()) {
         Enchantment enchantment = entry.getKey();
         if (enchantment != null) {
            int i = entry.getValue();
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setString("id", String.valueOf((Object)IRegistry.field_212628_q.getKey(enchantment)));
            nbttagcompound.setShort("lvl", (short)i);
            nbttaglist.add((INBTBase)nbttagcompound);
            if (stack.getItem() == Items.ENCHANTED_BOOK) {
               ItemEnchantedBook.addEnchantment(stack, new EnchantmentData(enchantment, i));
            }
         }
      }

      if (nbttaglist.isEmpty()) {
         stack.removeChildTag("Enchantments");
      } else if (stack.getItem() != Items.ENCHANTED_BOOK) {
         stack.setTagInfo("Enchantments", nbttaglist);
      }

   }

   /**
    * Executes the enchantment modifier on the ItemStack passed.
    */
   private static void applyEnchantmentModifier(EnchantmentHelper.IEnchantmentVisitor modifier, ItemStack stack) {
      if (!stack.isEmpty()) {
         NBTTagList nbttaglist = stack.getEnchantmentTagList();

         for(int i = 0; i < nbttaglist.size(); ++i) {
            String s = nbttaglist.getCompound(i).getString("id");
            int j = nbttaglist.getCompound(i).getInt("lvl");
            Enchantment enchantment = IRegistry.field_212628_q.func_212608_b(ResourceLocation.makeResourceLocation(s));
            if (enchantment != null) {
               modifier.accept(enchantment, j);
            }
         }

      }
   }

   /**
    * Executes the enchantment modifier on the array of ItemStack passed.
    */
   private static void applyEnchantmentModifierArray(EnchantmentHelper.IEnchantmentVisitor modifier, Iterable<ItemStack> stacks) {
      for(ItemStack itemstack : stacks) {
         applyEnchantmentModifier(modifier, itemstack);
      }

   }

   /**
    * Returns the modifier of protection enchantments on armors equipped on player.
    */
   public static int getEnchantmentModifierDamage(Iterable<ItemStack> stacks, DamageSource source) {
      MutableInt mutableint = new MutableInt();
      applyEnchantmentModifierArray((p_212576_2_, p_212576_3_) -> {
         mutableint.add(p_212576_2_.calcModifierDamage(p_212576_3_, source));
      }, stacks);
      return mutableint.intValue();
   }

   public static float getModifierForCreature(ItemStack stack, CreatureAttribute creatureAttribute) {
      MutableFloat mutablefloat = new MutableFloat();
      applyEnchantmentModifier((p_212573_2_, p_212573_3_) -> {
         mutablefloat.add(p_212573_2_.calcDamageByCreature(p_212573_3_, creatureAttribute));
      }, stack);
      return mutablefloat.floatValue();
   }

   public static float getSweepingDamageRatio(EntityLivingBase entityIn) {
      int i = getMaxEnchantmentLevel(Enchantments.SWEEPING, entityIn);
      return i > 0 ? EnchantmentSweepingEdge.getSweepingDamageRatio(i) : 0.0F;
   }

   public static void applyThornEnchantments(EntityLivingBase user, Entity attacker) {
      EnchantmentHelper.IEnchantmentVisitor enchantmenthelper$ienchantmentvisitor = (p_212575_2_, p_212575_3_) -> {
         p_212575_2_.onUserHurt(user, attacker, p_212575_3_);
      };
      if (user != null) {
         applyEnchantmentModifierArray(enchantmenthelper$ienchantmentvisitor, user.getEquipmentAndArmor());
      }

      if (attacker instanceof EntityPlayer) {
         applyEnchantmentModifier(enchantmenthelper$ienchantmentvisitor, user.getHeldItemMainhand());
      }

   }

   public static void applyArthropodEnchantments(EntityLivingBase user, Entity target) {
      EnchantmentHelper.IEnchantmentVisitor enchantmenthelper$ienchantmentvisitor = (p_212574_2_, p_212574_3_) -> {
         p_212574_2_.onEntityDamaged(user, target, p_212574_3_);
      };
      if (user != null) {
         applyEnchantmentModifierArray(enchantmenthelper$ienchantmentvisitor, user.getEquipmentAndArmor());
      }

      if (user instanceof EntityPlayer) {
         applyEnchantmentModifier(enchantmenthelper$ienchantmentvisitor, user.getHeldItemMainhand());
      }

   }

   public static int getMaxEnchantmentLevel(Enchantment enchantmentIn, EntityLivingBase entityIn) {
      Iterable<ItemStack> iterable = enchantmentIn.getEntityEquipment(entityIn);
      if (iterable == null) {
         return 0;
      } else {
         int i = 0;

         for(ItemStack itemstack : iterable) {
            int j = getEnchantmentLevel(enchantmentIn, itemstack);
            if (j > i) {
               i = j;
            }
         }

         return i;
      }
   }

   /**
    * Returns the Knockback modifier of the enchantment on the players held item.
    */
   public static int getKnockbackModifier(EntityLivingBase player) {
      return getMaxEnchantmentLevel(Enchantments.KNOCKBACK, player);
   }

   /**
    * Returns the fire aspect modifier of the players held item.
    */
   public static int getFireAspectModifier(EntityLivingBase player) {
      return getMaxEnchantmentLevel(Enchantments.FIRE_ASPECT, player);
   }

   public static int getRespirationModifier(EntityLivingBase entityIn) {
      return getMaxEnchantmentLevel(Enchantments.RESPIRATION, entityIn);
   }

   public static int getDepthStriderModifier(EntityLivingBase entityIn) {
      return getMaxEnchantmentLevel(Enchantments.DEPTH_STRIDER, entityIn);
   }

   public static int getEfficiencyModifier(EntityLivingBase entityIn) {
      return getMaxEnchantmentLevel(Enchantments.EFFICIENCY, entityIn);
   }

   public static int getFishingLuckBonus(ItemStack stack) {
      return getEnchantmentLevel(Enchantments.LUCK_OF_THE_SEA, stack);
   }

   public static int getFishingSpeedBonus(ItemStack stack) {
      return getEnchantmentLevel(Enchantments.LURE, stack);
   }

   public static int getLootingModifier(EntityLivingBase entityIn) {
      return getMaxEnchantmentLevel(Enchantments.LOOTING, entityIn);
   }

   public static boolean getAquaAffinityModifier(EntityLivingBase entityIn) {
      return getMaxEnchantmentLevel(Enchantments.AQUA_AFFINITY, entityIn) > 0;
   }

   /**
    * Checks if the player has any armor enchanted with the frost walker enchantment. 
    *  @return If player has equipment with frost walker
    */
   public static boolean hasFrostWalker(EntityLivingBase player) {
      return getMaxEnchantmentLevel(Enchantments.FROST_WALKER, player) > 0;
   }

   public static boolean hasBindingCurse(ItemStack stack) {
      return getEnchantmentLevel(Enchantments.BINDING_CURSE, stack) > 0;
   }

   public static boolean hasVanishingCurse(ItemStack stack) {
      return getEnchantmentLevel(Enchantments.VANISHING_CURSE, stack) > 0;
   }

   public static int getLoyaltyModifier(ItemStack stack) {
      return getEnchantmentLevel(Enchantments.LOYALTY, stack);
   }

   public static int getRiptideModifier(ItemStack stack) {
      return getEnchantmentLevel(Enchantments.RIPTIDE, stack);
   }

   public static boolean hasChanneling(ItemStack stack) {
      return getEnchantmentLevel(Enchantments.CHANNELING, stack) > 0;
   }

   public static ItemStack getEnchantedItem(Enchantment enchantmentIn, EntityLivingBase entityIn) {
      List<ItemStack> list = enchantmentIn.getEntityEquipment(entityIn);
      if (list.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         List<ItemStack> list1 = Lists.newArrayList();

         for(ItemStack itemstack : list) {
            if (!itemstack.isEmpty() && getEnchantmentLevel(enchantmentIn, itemstack) > 0) {
               list1.add(itemstack);
            }
         }

         return list1.isEmpty() ? ItemStack.EMPTY : list1.get(entityIn.getRNG().nextInt(list1.size()));
      }
   }

   /**
    * Returns the enchantability of itemstack, using a separate calculation for each enchantNum (0, 1 or 2), cutting to
    * the max enchantability power of the table, which is locked to a max of 15.
    */
   public static int calcItemStackEnchantability(Random rand, int enchantNum, int power, ItemStack stack) {
      Item item = stack.getItem();
      int i = stack.getItemEnchantability();
      if (i <= 0) {
         return 0;
      } else {
         if (power > 15) {
            power = 15;
         }

         int j = rand.nextInt(8) + 1 + (power >> 1) + rand.nextInt(power + 1);
         if (enchantNum == 0) {
            return Math.max(j / 3, 1);
         } else {
            return enchantNum == 1 ? j * 2 / 3 + 1 : Math.max(j, power * 2);
         }
      }
   }

   /**
    * Applys a random enchantment to the specified item.
    */
   public static ItemStack addRandomEnchantment(Random random, ItemStack stack, int level, boolean allowTreasure) {
      List<EnchantmentData> list = buildEnchantmentList(random, stack, level, allowTreasure);
      boolean flag = stack.getItem() == Items.BOOK;
      if (flag) {
         stack = new ItemStack(Items.ENCHANTED_BOOK);
      }

      for(EnchantmentData enchantmentdata : list) {
         if (flag) {
            ItemEnchantedBook.addEnchantment(stack, enchantmentdata);
         } else {
            stack.addEnchantment(enchantmentdata.enchantment, enchantmentdata.enchantmentLevel);
         }
      }

      return stack;
   }

   /**
    * Create a list of random EnchantmentData (enchantments) that can be added together to the ItemStack, the 3rd
    * parameter is the total enchantability level.
    */
   public static List<EnchantmentData> buildEnchantmentList(Random randomIn, ItemStack itemStackIn, int level, boolean allowTreasure) {
      List<EnchantmentData> list = Lists.newArrayList();
      Item item = itemStackIn.getItem();
      int i = itemStackIn.getItemEnchantability();
      if (i <= 0) {
         return list;
      } else {
         level = level + 1 + randomIn.nextInt(i / 4 + 1) + randomIn.nextInt(i / 4 + 1);
         float f = (randomIn.nextFloat() + randomIn.nextFloat() - 1.0F) * 0.15F;
         level = MathHelper.clamp(Math.round((float)level + (float)level * f), 1, Integer.MAX_VALUE);
         List<EnchantmentData> list1 = getEnchantmentDatas(level, itemStackIn, allowTreasure);
         if (!list1.isEmpty()) {
            list.add(WeightedRandom.getRandomItem(randomIn, list1));

            while(randomIn.nextInt(50) <= level) {
               removeIncompatible(list1, Util.getLastElement(list));
               if (list1.isEmpty()) {
                  break;
               }

               list.add(WeightedRandom.getRandomItem(randomIn, list1));
               level /= 2;
            }
         }

         return list;
      }
   }

   public static void removeIncompatible(List<EnchantmentData> dataList, EnchantmentData data) {
      Iterator<EnchantmentData> iterator = dataList.iterator();

      while(iterator.hasNext()) {
         if (!data.enchantment.isCompatibleWith((iterator.next()).enchantment)) {
            iterator.remove();
         }
      }

   }

   public static boolean areAllCompatibleWith(Collection<Enchantment> enchantmentsIn, Enchantment enchantmentIn) {
      for(Enchantment enchantment : enchantmentsIn) {
         if (!enchantment.isCompatibleWith(enchantmentIn)) {
            return false;
         }
      }

      return true;
   }

   public static List<EnchantmentData> getEnchantmentDatas(int p_185291_0_, ItemStack stack, boolean allowTreasure) {
      List<EnchantmentData> list = Lists.newArrayList();
      Item item = stack.getItem();
      boolean flag = stack.getItem() == Items.BOOK;

      for(Enchantment enchantment : IRegistry.field_212628_q) {
         if ((!enchantment.isTreasureEnchantment() || allowTreasure) && (enchantment.canApplyAtEnchantingTable(stack) || (flag && enchantment.isAllowedOnBooks()))) {
            for(int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; --i) {
               if (p_185291_0_ >= enchantment.getMinEnchantability(i) && p_185291_0_ <= enchantment.getMaxEnchantability(i)) {
                  list.add(new EnchantmentData(enchantment, i));
                  break;
               }
            }
         }
      }

      return list;
   }

   @FunctionalInterface
   interface IEnchantmentVisitor {
      void accept(Enchantment p_accept_1_, int p_accept_2_);
   }
}