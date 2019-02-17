package net.minecraft.nbt;

import java.util.AbstractList;

public abstract class NBTTagCollection<T extends INBTBase> extends AbstractList<T> implements INBTBase {
   public abstract int size();

   public T get(int p_get_1_) {
      return (T)this.getTag(p_get_1_);
   }

   public T set(int p_set_1_, T p_set_2_) {
      T t = this.get(p_set_1_);
      this.setTag(p_set_1_, p_set_2_);
      return t;
   }

   public abstract T getTag(int p_197647_1_);

   public abstract void setTag(int p_197648_1_, INBTBase p_197648_2_);

   public abstract void removeTag(int p_197649_1_);
}