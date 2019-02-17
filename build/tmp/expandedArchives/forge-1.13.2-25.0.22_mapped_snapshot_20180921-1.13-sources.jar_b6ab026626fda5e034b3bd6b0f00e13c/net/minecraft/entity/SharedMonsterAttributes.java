package net.minecraft.entity;

import java.util.Collection;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SharedMonsterAttributes {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final IAttribute MAX_HEALTH = (new RangedAttribute((IAttribute)null, "generic.maxHealth", 20.0D, Float.MIN_VALUE, 1024.0D)).setDescription("Max Health").setShouldWatch(true);  // Forge: set smallest max-health value to fix MC-119183. This gets rounded to float so we use the smallest positive float value.
   public static final IAttribute FOLLOW_RANGE = (new RangedAttribute((IAttribute)null, "generic.followRange", 32.0D, 0.0D, 2048.0D)).setDescription("Follow Range");
   public static final IAttribute KNOCKBACK_RESISTANCE = (new RangedAttribute((IAttribute)null, "generic.knockbackResistance", 0.0D, 0.0D, 1.0D)).setDescription("Knockback Resistance");
   public static final IAttribute MOVEMENT_SPEED = (new RangedAttribute((IAttribute)null, "generic.movementSpeed", (double)0.7F, 0.0D, 1024.0D)).setDescription("Movement Speed").setShouldWatch(true);
   public static final IAttribute FLYING_SPEED = (new RangedAttribute((IAttribute)null, "generic.flyingSpeed", (double)0.4F, 0.0D, 1024.0D)).setDescription("Flying Speed").setShouldWatch(true);
   public static final IAttribute ATTACK_DAMAGE = new RangedAttribute((IAttribute)null, "generic.attackDamage", 2.0D, 0.0D, 2048.0D);
   public static final IAttribute ATTACK_SPEED = (new RangedAttribute((IAttribute)null, "generic.attackSpeed", 4.0D, 0.0D, 1024.0D)).setShouldWatch(true);
   public static final IAttribute ARMOR = (new RangedAttribute((IAttribute)null, "generic.armor", 0.0D, 0.0D, 30.0D)).setShouldWatch(true);
   public static final IAttribute ARMOR_TOUGHNESS = (new RangedAttribute((IAttribute)null, "generic.armorToughness", 0.0D, 0.0D, 20.0D)).setShouldWatch(true);
   public static final IAttribute LUCK = (new RangedAttribute((IAttribute)null, "generic.luck", 0.0D, -1024.0D, 1024.0D)).setShouldWatch(true);

   public static NBTTagList writeAttributes(AbstractAttributeMap map) {
      NBTTagList nbttaglist = new NBTTagList();

      for(IAttributeInstance iattributeinstance : map.getAllAttributes()) {
         nbttaglist.add((INBTBase)writeAttribute(iattributeinstance));
      }

      return nbttaglist;
   }

   private static NBTTagCompound writeAttribute(IAttributeInstance instance) {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      IAttribute iattribute = instance.getAttribute();
      nbttagcompound.setString("Name", iattribute.getName());
      nbttagcompound.setDouble("Base", instance.getBaseValue());
      Collection<AttributeModifier> collection = instance.getModifiers();
      if (collection != null && !collection.isEmpty()) {
         NBTTagList nbttaglist = new NBTTagList();

         for(AttributeModifier attributemodifier : collection) {
            if (attributemodifier.isSaved()) {
               nbttaglist.add((INBTBase)writeAttributeModifier(attributemodifier));
            }
         }

         nbttagcompound.setTag("Modifiers", nbttaglist);
      }

      return nbttagcompound;
   }

   public static NBTTagCompound writeAttributeModifier(AttributeModifier modifier) {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.setString("Name", modifier.getName());
      nbttagcompound.setDouble("Amount", modifier.getAmount());
      nbttagcompound.setInt("Operation", modifier.getOperation());
      nbttagcompound.setUniqueId("UUID", modifier.getID());
      return nbttagcompound;
   }

   public static void readAttributes(AbstractAttributeMap map, NBTTagList list) {
      for(int i = 0; i < list.size(); ++i) {
         NBTTagCompound nbttagcompound = list.getCompound(i);
         IAttributeInstance iattributeinstance = map.getAttributeInstanceByName(nbttagcompound.getString("Name"));
         if (iattributeinstance == null) {
            LOGGER.warn("Ignoring unknown attribute '{}'", (Object)nbttagcompound.getString("Name"));
         } else {
            readAttribute(iattributeinstance, nbttagcompound);
         }
      }

   }

   private static void readAttribute(IAttributeInstance instance, NBTTagCompound compound) {
      instance.setBaseValue(compound.getDouble("Base"));
      if (compound.contains("Modifiers", 9)) {
         NBTTagList nbttaglist = compound.getList("Modifiers", 10);

         for(int i = 0; i < nbttaglist.size(); ++i) {
            AttributeModifier attributemodifier = readAttributeModifier(nbttaglist.getCompound(i));
            if (attributemodifier != null) {
               AttributeModifier attributemodifier1 = instance.getModifier(attributemodifier.getID());
               if (attributemodifier1 != null) {
                  instance.removeModifier(attributemodifier1);
               }

               instance.applyModifier(attributemodifier);
            }
         }
      }

   }

   @Nullable
   public static AttributeModifier readAttributeModifier(NBTTagCompound compound) {
      UUID uuid = compound.getUniqueId("UUID");

      try {
         return new AttributeModifier(uuid, compound.getString("Name"), compound.getDouble("Amount"), compound.getInt("Operation"));
      } catch (Exception exception) {
         LOGGER.warn("Unable to create attribute: {}", (Object)exception.getMessage());
         return null;
      }
   }
}