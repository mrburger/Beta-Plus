package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

public class ItemDye extends Item {
   private static final Map<EnumDyeColor, ItemDye> COLOR_DYE_ITEM_MAP = Maps.newEnumMap(EnumDyeColor.class);
   private final EnumDyeColor dyeColor;

   public ItemDye(EnumDyeColor dyeColorIn, Item.Properties builder) {
      super(builder);
      this.dyeColor = dyeColorIn;
      COLOR_DYE_ITEM_MAP.put(dyeColorIn, this);
   }

   /**
    * Returns true if the item can be used on the given entity, e.g. shears on sheep.
    */
   public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
      if (target instanceof EntitySheep) {
         EntitySheep entitysheep = (EntitySheep)target;
         if (!entitysheep.getSheared() && entitysheep.getFleeceColor() != this.dyeColor) {
            entitysheep.setFleeceColor(this.dyeColor);
            stack.shrink(1);
         }

         return true;
      } else {
         return false;
      }
   }

   public EnumDyeColor getDyeColor() {
      return this.dyeColor;
   }

   public static ItemDye getItem(EnumDyeColor color) {
      return COLOR_DYE_ITEM_MAP.get(color);
   }
}