package net.minecraft.world.gen.area;

public final class AreaDimension {
   private final int startX;
   private final int startZ;
   private final int xSize;
   private final int zSize;

   public AreaDimension(int startXIn, int startZIn, int xSizeIn, int zSizeIn) {
      this.startX = startXIn;
      this.startZ = startZIn;
      this.xSize = xSizeIn;
      this.zSize = zSizeIn;
   }

   public int getStartX() {
      return this.startX;
   }

   public int getStartZ() {
      return this.startZ;
   }

   public int getXSize() {
      return this.xSize;
   }

   public int getZSize() {
      return this.zSize;
   }
}