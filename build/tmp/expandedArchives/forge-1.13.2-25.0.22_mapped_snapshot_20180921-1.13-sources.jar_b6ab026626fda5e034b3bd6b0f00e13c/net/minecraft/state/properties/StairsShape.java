package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum StairsShape implements IStringSerializable {
   STRAIGHT("straight"),
   INNER_LEFT("inner_left"),
   INNER_RIGHT("inner_right"),
   OUTER_LEFT("outer_left"),
   OUTER_RIGHT("outer_right");

   private final String field_212251_f;

   private StairsShape(String name) {
      this.field_212251_f = name;
   }

   public String toString() {
      return this.field_212251_f;
   }

   public String getName() {
      return this.field_212251_f;
   }
}