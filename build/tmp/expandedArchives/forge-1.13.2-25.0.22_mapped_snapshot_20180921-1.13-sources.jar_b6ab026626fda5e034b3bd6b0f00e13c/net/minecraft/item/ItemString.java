package net.minecraft.item;

import net.minecraft.init.Blocks;

public class ItemString extends ItemBlock {
   public ItemString(Item.Properties builder) {
      super(Blocks.TRIPWIRE, builder);
   }

   /**
    * Returns the unlocalized name of this item.
    */
   public String getTranslationKey() {
      return this.getDefaultTranslationKey();
   }
}