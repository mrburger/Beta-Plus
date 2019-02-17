package net.minecraft.world;

public enum EnumLightType {
   SKY(15),
   BLOCK(0);

   public final int defaultLightValue;

   private EnumLightType(int defaultLightValueIn) {
      this.defaultLightValue = defaultLightValueIn;
   }
}