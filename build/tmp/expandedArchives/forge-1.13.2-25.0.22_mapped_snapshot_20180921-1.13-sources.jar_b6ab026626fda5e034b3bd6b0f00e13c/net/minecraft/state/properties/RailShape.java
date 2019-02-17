package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum RailShape implements IStringSerializable {
   NORTH_SOUTH(0, "north_south"),
   EAST_WEST(1, "east_west"),
   ASCENDING_EAST(2, "ascending_east"),
   ASCENDING_WEST(3, "ascending_west"),
   ASCENDING_NORTH(4, "ascending_north"),
   ASCENDING_SOUTH(5, "ascending_south"),
   SOUTH_EAST(6, "south_east"),
   SOUTH_WEST(7, "south_west"),
   NORTH_WEST(8, "north_west"),
   NORTH_EAST(9, "north_east");

   private final int meta;
   private final String name;

   private RailShape(int p_i49334_3_, String name) {
      this.meta = p_i49334_3_;
      this.name = name;
   }

   public int getMeta() {
      return this.meta;
   }

   public String toString() {
      return this.name;
   }

   public boolean isAscending() {
      return this == ASCENDING_NORTH || this == ASCENDING_EAST || this == ASCENDING_SOUTH || this == ASCENDING_WEST;
   }

   public String getName() {
      return this.name;
   }
}