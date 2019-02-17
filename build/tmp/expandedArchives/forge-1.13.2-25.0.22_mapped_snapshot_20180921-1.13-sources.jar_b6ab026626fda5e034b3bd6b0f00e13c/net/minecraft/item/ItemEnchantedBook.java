package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemEnchantedBook extends Item {
   public ItemEnchantedBook(Item.Properties builder) {
      super(builder);
   }

   /**
    * Returns true if this item has an enchantment glint. By default, this returns <code>stack.isItemEnchanted()</code>,
    * but other items can override it (for instance, written books always return true).
    *  
    * Note that if you override this method, you generally want to also call the super version (on {@link Item}) to get
    * the glint for enchanted items. Of course, that is unnecessary if the overwritten version always returns true.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean hasEffect(ItemStack stack) {
      return true;
   }

   /**
    * Checks isDamagable and if it cannot be stacked
    */
   public boolean isEnchantable(ItemStack stack) {
      return false;
   }

   public static NBTTagList getEnchantments(ItemStack p_92110_0_) {
      NBTTagCompound nbttagcompound = p_92110_0_.getTag();
      return nbttagcompound != null ? nbttagcompound.getList("StoredEnchantments", 10) : new NBTTagList();
   }

   /**
    * allows items to add custom lines of information to the mouseover description
    */
   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      super.addInformation(stack, worldIn, tooltip, flagIn);
      NBTTagList nbttaglist = getEnchantments(stack);

      for(int i = 0; i < nbttaglist.size(); ++i) {
         NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);
         Enchantment enchantment = IRegistry.field_212628_q.func_212608_b(ResourceLocation.makeResourceLocation(nbttagcompound.getString("id")));
         if (enchantment != null) {
            tooltip.add(enchantment.func_200305_d(nbttagcompound.getInt("lvl")));
         }
      }

   }

   /**
    * Adds an stored enchantment to an enchanted book ItemStack
    */
   public static void addEnchantment(ItemStack p_92115_0_, EnchantmentData stack) {
      NBTTagList nbttaglist = getEnchantments(p_92115_0_);
      boolean flag = true;
      ResourceLocation resourcelocation = IRegistry.field_212628_q.getKey(stack.enchantment);

      for(int i = 0; i < nbttaglist.size(); ++i) {
         NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);
         ResourceLocation resourcelocation1 = ResourceLocation.makeResourceLocation(nbttagcompound.getString("id"));
         if (resourcelocation1 != null && resourcelocation1.equals(resourcelocation)) {
            if (nbttagcompound.getInt("lvl") < stack.enchantmentLevel) {
               nbttagcompound.setShort("lvl", (short)stack.enchantmentLevel);
            }

            flag = false;
            break;
         }
      }

      if (flag) {
         NBTTagCompound nbttagcompound1 = new NBTTagCompound();
         nbttagcompound1.setString("id", String.valueOf((Object)resourcelocation));
         nbttagcompound1.setShort("lvl", (short)stack.enchantmentLevel);
         nbttaglist.add((INBTBase)nbttagcompound1);
      }

      p_92115_0_.getOrCreateTag().setTag("StoredEnchantments", nbttaglist);
   }

   /**
    * Returns the ItemStack of an enchanted version of this item.
    */
   public static ItemStack getEnchantedItemStack(EnchantmentData p_92111_0_) {
      ItemStack itemstack = new ItemStack(Items.ENCHANTED_BOOK);
      addEnchantment(itemstack, p_92111_0_);
      return itemstack;
   }

   /**
    * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
    */
   public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
      if (group == ItemGroup.SEARCH) {
         for(Enchantment enchantment : IRegistry.field_212628_q) {
            if (enchantment.type != null) {
               for(int i = enchantment.getMinLevel(); i <= enchantment.getMaxLevel(); ++i) {
                  items.add(getEnchantedItemStack(new EnchantmentData(enchantment, i)));
               }
            }
         }
      } else if (group.getRelevantEnchantmentTypes().length != 0) {
         for(Enchantment enchantment1 : IRegistry.field_212628_q) {
            if (group.hasRelevantEnchantmentType(enchantment1.type)) {
               items.add(getEnchantedItemStack(new EnchantmentData(enchantment1, enchantment1.getMaxLevel())));
            }
         }
      }

   }
}