package net.minecraft.potion;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PotionUtils {
   /**
    * Creates a List of PotionEffect from data on the passed ItemStack's NBTTagCompound.
    */
   public static List<PotionEffect> getEffectsFromStack(ItemStack stack) {
      return getEffectsFromTag(stack.getTag());
   }

   public static List<PotionEffect> mergeEffects(PotionType potionIn, Collection<PotionEffect> effects) {
      List<PotionEffect> list = Lists.newArrayList();
      list.addAll(potionIn.getEffects());
      list.addAll(effects);
      return list;
   }

   /**
    * Creates a list of PotionEffect from data on a NBTTagCompound.
    */
   public static List<PotionEffect> getEffectsFromTag(@Nullable NBTTagCompound tag) {
      List<PotionEffect> list = Lists.newArrayList();
      list.addAll(getPotionTypeFromNBT(tag).getEffects());
      addCustomPotionEffectToList(tag, list);
      return list;
   }

   public static List<PotionEffect> getFullEffectsFromItem(ItemStack itemIn) {
      return getFullEffectsFromTag(itemIn.getTag());
   }

   public static List<PotionEffect> getFullEffectsFromTag(@Nullable NBTTagCompound tag) {
      List<PotionEffect> list = Lists.newArrayList();
      addCustomPotionEffectToList(tag, list);
      return list;
   }

   public static void addCustomPotionEffectToList(@Nullable NBTTagCompound tag, List<PotionEffect> effectList) {
      if (tag != null && tag.contains("CustomPotionEffects", 9)) {
         NBTTagList nbttaglist = tag.getList("CustomPotionEffects", 10);

         for(int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);
            PotionEffect potioneffect = PotionEffect.read(nbttagcompound);
            if (potioneffect != null) {
               effectList.add(potioneffect);
            }
         }
      }

   }

   public static int getColor(ItemStack p_190932_0_) {
      NBTTagCompound nbttagcompound = p_190932_0_.getTag();
      if (nbttagcompound != null && nbttagcompound.contains("CustomPotionColor", 99)) {
         return nbttagcompound.getInt("CustomPotionColor");
      } else {
         return getPotionFromItem(p_190932_0_) == PotionTypes.EMPTY ? 16253176 : getPotionColorFromEffectList(getEffectsFromStack(p_190932_0_));
      }
   }

   public static int getPotionColor(PotionType potionIn) {
      return potionIn == PotionTypes.EMPTY ? 16253176 : getPotionColorFromEffectList(potionIn.getEffects());
   }

   public static int getPotionColorFromEffectList(Collection<PotionEffect> effects) {
      int i = 3694022;
      if (effects.isEmpty()) {
         return 3694022;
      } else {
         float f = 0.0F;
         float f1 = 0.0F;
         float f2 = 0.0F;
         int j = 0;

         for(PotionEffect potioneffect : effects) {
            if (potioneffect.doesShowParticles()) {
               int k = potioneffect.getPotion().getLiquidColor();
               int l = potioneffect.getAmplifier() + 1;
               f += (float)(l * (k >> 16 & 255)) / 255.0F;
               f1 += (float)(l * (k >> 8 & 255)) / 255.0F;
               f2 += (float)(l * (k >> 0 & 255)) / 255.0F;
               j += l;
            }
         }

         if (j == 0) {
            return 0;
         } else {
            f = f / (float)j * 255.0F;
            f1 = f1 / (float)j * 255.0F;
            f2 = f2 / (float)j * 255.0F;
            return (int)f << 16 | (int)f1 << 8 | (int)f2;
         }
      }
   }

   public static PotionType getPotionFromItem(ItemStack itemIn) {
      return getPotionTypeFromNBT(itemIn.getTag());
   }

   /**
    * If no correct potion is found, returns the default one : PotionTypes.water
    */
   public static PotionType getPotionTypeFromNBT(@Nullable NBTTagCompound tag) {
      return tag == null ? PotionTypes.EMPTY : PotionType.getPotionTypeForName(tag.getString("Potion"));
   }

   public static ItemStack addPotionToItemStack(ItemStack itemIn, PotionType potionIn) {
      ResourceLocation resourcelocation = IRegistry.field_212621_j.getKey(potionIn);
      if (potionIn == PotionTypes.EMPTY) {
         itemIn.removeChildTag("Potion");
      } else {
         itemIn.getOrCreateTag().setString("Potion", resourcelocation.toString());
      }

      return itemIn;
   }

   public static ItemStack appendEffects(ItemStack itemIn, Collection<PotionEffect> effects) {
      if (effects.isEmpty()) {
         return itemIn;
      } else {
         NBTTagCompound nbttagcompound = itemIn.getOrCreateTag();
         NBTTagList nbttaglist = nbttagcompound.getList("CustomPotionEffects", 9);

         for(PotionEffect potioneffect : effects) {
            nbttaglist.add((INBTBase)potioneffect.write(new NBTTagCompound()));
         }

         nbttagcompound.setTag("CustomPotionEffects", nbttaglist);
         return itemIn;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void addPotionTooltip(ItemStack itemIn, List<ITextComponent> lores, float durationFactor) {
      List<PotionEffect> list = getEffectsFromStack(itemIn);
      List<Tuple<String, AttributeModifier>> list1 = Lists.newArrayList();
      if (list.isEmpty()) {
         lores.add((new TextComponentTranslation("effect.none")).applyTextStyle(TextFormatting.GRAY));
      } else {
         for(PotionEffect potioneffect : list) {
            ITextComponent itextcomponent = new TextComponentTranslation(potioneffect.getEffectName());
            Potion potion = potioneffect.getPotion();
            Map<IAttribute, AttributeModifier> map = potion.getAttributeModifierMap();
            if (!map.isEmpty()) {
               for(Entry<IAttribute, AttributeModifier> entry : map.entrySet()) {
                  AttributeModifier attributemodifier = entry.getValue();
                  AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), potion.getAttributeModifierAmount(potioneffect.getAmplifier(), attributemodifier), attributemodifier.getOperation());
                  list1.add(new Tuple<>(entry.getKey().getName(), attributemodifier1));
               }
            }

            if (potioneffect.getAmplifier() > 0) {
               itextcomponent.appendText(" ").appendSibling(new TextComponentTranslation("potion.potency." + potioneffect.getAmplifier()));
            }

            if (potioneffect.getDuration() > 20) {
               itextcomponent.appendText(" (").appendText(PotionUtil.getPotionDurationString(potioneffect, durationFactor)).appendText(")");
            }

            lores.add(itextcomponent.applyTextStyle(potion.isBadEffect() ? TextFormatting.RED : TextFormatting.BLUE));
         }
      }

      if (!list1.isEmpty()) {
         lores.add(new TextComponentString(""));
         lores.add((new TextComponentTranslation("potion.whenDrank")).applyTextStyle(TextFormatting.DARK_PURPLE));

         for(Tuple<String, AttributeModifier> tuple : list1) {
            AttributeModifier attributemodifier2 = tuple.getB();
            double d0 = attributemodifier2.getAmount();
            double d1;
            if (attributemodifier2.getOperation() != 1 && attributemodifier2.getOperation() != 2) {
               d1 = attributemodifier2.getAmount();
            } else {
               d1 = attributemodifier2.getAmount() * 100.0D;
            }

            if (d0 > 0.0D) {
               lores.add((new TextComponentTranslation("attribute.modifier.plus." + attributemodifier2.getOperation(), ItemStack.DECIMALFORMAT.format(d1), new TextComponentTranslation("attribute.name." + (String)tuple.getA()))).applyTextStyle(TextFormatting.BLUE));
            } else if (d0 < 0.0D) {
               d1 = d1 * -1.0D;
               lores.add((new TextComponentTranslation("attribute.modifier.take." + attributemodifier2.getOperation(), ItemStack.DECIMALFORMAT.format(d1), new TextComponentTranslation("attribute.name." + (String)tuple.getA()))).applyTextStyle(TextFormatting.RED));
            }
         }
      }

   }
}