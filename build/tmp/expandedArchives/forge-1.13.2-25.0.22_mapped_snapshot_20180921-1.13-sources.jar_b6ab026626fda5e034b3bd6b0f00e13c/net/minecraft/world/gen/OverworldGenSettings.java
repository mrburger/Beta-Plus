package net.minecraft.world.gen;

public class OverworldGenSettings extends ChunkGenSettings {
   private final int field_202212_j = 4;
   private final int field_202213_k = 4;
   private final int field_202214_l = -1;
   private final int field_202215_m = 63;
   private final double field_202216_n = 200.0D;
   private final double field_202217_o = 200.0D;
   private final double field_202218_p = 0.5D;
   private final float field_202219_q = 684.412F;
   private final float field_202220_r = 684.412F;
   private final float field_202221_s = 80.0F;
   private final float field_202222_t = 160.0F;
   private final float field_202223_u = 80.0F;
   private final float field_202224_v = 0.0F;
   private final float field_202225_w = 1.0F;
   private final float field_202226_x = 0.0F;
   private final float field_202227_y = 1.0F;
   private final double field_202228_z = 8.5D;
   private final double field_202209_A = 12.0D;
   private final double field_202210_B = 512.0D;
   private final double field_202211_C = 512.0D;

   public int getBiomeSize() {
      return 4;
   }

   public int getRiverSize() {
      return 4;
   }

   public int func_202199_l() {
      return -1;
   }

   public int getSeaLevel() {
      return 63;
   }

   public double getDepthNoiseScaleX() {
      return 200.0D;
   }

   public double getDepthNoiseScaleZ() {
      return 200.0D;
   }

   public double getDepthNoiseScaleExponent() {
      return 0.5D;
   }

   public float getCoordinateScale() {
      return 684.412F;
   }

   public float getHeightScale() {
      return 684.412F;
   }

   public float getMainNoiseScaleX() {
      return 80.0F;
   }

   public float getMainNoiseScaleY() {
      return 160.0F;
   }

   public float getMainNoiseScaleZ() {
      return 80.0F;
   }

   public float func_202203_v() {
      return 0.0F;
   }

   public float func_202202_w() {
      return 1.0F;
   }

   public float func_202204_x() {
      return 0.0F;
   }

   public float func_202205_y() {
      return 1.0F;
   }

   public double func_202201_z() {
      return 8.5D;
   }

   public double func_202206_A() {
      return 12.0D;
   }

   public double getLowerLimitScale() {
      return 512.0D;
   }

   public double getUpperLimitScale() {
      return 512.0D;
   }
}