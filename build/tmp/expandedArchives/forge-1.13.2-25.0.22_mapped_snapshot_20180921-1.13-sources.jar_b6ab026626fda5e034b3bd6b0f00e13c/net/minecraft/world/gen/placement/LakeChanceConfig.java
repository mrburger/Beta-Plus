package net.minecraft.world.gen.placement;

public class LakeChanceConfig implements IPlacementConfig {
   public final int rarity;

   public LakeChanceConfig(int rarityIn) {
      this.rarity = rarityIn;
   }
}