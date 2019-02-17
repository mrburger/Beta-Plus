package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum Half implements IStringSerializable {
   TOP("top"),
   BOTTOM("bottom");

   private final String field_212249_f;

   private Half(String name) {
      this.field_212249_f = name;
   }

   public String toString() {
      return this.field_212249_f;
   }

   public String getName() {
      return this.field_212249_f;
   }
}