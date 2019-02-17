package net.minecraft.state;

import com.google.common.base.MoreObjects;

public abstract class AbstractProperty<T extends Comparable<T>> implements IProperty<T> {
   private final Class<T> valueClass;
   private final String name;
   private Integer hashCode;

   protected AbstractProperty(String name, Class<T> valueClass) {
      this.valueClass = valueClass;
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   /**
    * The class of the values of this property
    */
   public Class<T> getValueClass() {
      return this.valueClass;
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("name", this.name).add("clazz", this.valueClass).add("values", this.getAllowedValues()).toString();
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof AbstractProperty)) {
         return false;
      } else {
         AbstractProperty<?> abstractproperty = (AbstractProperty)p_equals_1_;
         return this.valueClass.equals(abstractproperty.valueClass) && this.name.equals(abstractproperty.name);
      }
   }

   public final int hashCode() {
      if (this.hashCode == null) {
         this.hashCode = this.computeHashCode();
      }

      return this.hashCode;
   }

   public int computeHashCode() {
      return 31 * this.valueClass.hashCode() + this.name.hashCode();
   }
}