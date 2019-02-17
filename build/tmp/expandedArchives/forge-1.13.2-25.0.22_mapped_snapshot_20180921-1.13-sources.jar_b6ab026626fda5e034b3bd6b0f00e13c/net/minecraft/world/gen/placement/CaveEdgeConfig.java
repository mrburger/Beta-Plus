package net.minecraft.world.gen.placement;

import net.minecraft.world.gen.GenerationStage;

public class CaveEdgeConfig implements IPlacementConfig {
   final GenerationStage.Carving carvingStage;
   final float chance;

   public CaveEdgeConfig(GenerationStage.Carving carvingStageIn, float chanceIn) {
      this.carvingStage = carvingStageIn;
      this.chance = chanceIn;
   }
}