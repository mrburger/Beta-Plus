package net.minecraft.util;

import javax.annotation.Nullable;
import net.minecraft.util.text.ITextComponent;

public interface INameable {
   ITextComponent getName();

   boolean hasCustomName();

   default ITextComponent getDisplayName() {
      return this.getName();
   }

   @Nullable
   ITextComponent getCustomName();
}