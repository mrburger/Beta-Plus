package net.minecraft.world.gen;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public class ChunkGenSettings implements IChunkGenSettings {
   protected int villageDistance = 32;
   protected int villageSeparation = 8;
   protected int oceanMonumentSpacing = 32;
   protected int oceanMonumentSeparation = 5;
   protected int strongholdDistance = 32;
   protected int strongholdCount = 128;
   protected int strongholdSpread = 3;
   protected int biomeFeatureDistance = 32;
   protected int biomeFeatureSeparation = 8;
   protected int field_204027_h = 16;
   protected int field_211734_k = 8;
   protected int endCityDistance = 20;
   protected int endCitySeparation = 11;
   protected int field_204749_j = 16;
   protected int field_211736_o = 8;
   protected int mansionDistance = 80;
   protected int field_211737_q = 20;
   protected IBlockState defaultBlock = Blocks.STONE.getDefaultState();
   protected IBlockState defaultFluid = Blocks.WATER.getDefaultState();

   public int getVillageDistance() {
      return this.villageDistance;
   }

   public int getVillageSeparation() {
      return this.villageSeparation;
   }

   public int getOceanMonumentSpacing() {
      return this.oceanMonumentSpacing;
   }

   public int getOceanMonumentSeparation() {
      return this.oceanMonumentSeparation;
   }

   public int getStrongholdDistance() {
      return this.strongholdDistance;
   }

   public int getStrongholdCount() {
      return this.strongholdCount;
   }

   public int getStrongholdSpread() {
      return this.strongholdSpread;
   }

   public int getBiomeFeatureDistance() {
      return this.biomeFeatureDistance;
   }

   public int getBiomeFeatureSeparation() {
      return this.biomeFeatureSeparation;
   }

   public int func_204748_h() {
      return this.field_204749_j;
   }

   public int func_211730_k() {
      return this.field_211736_o;
   }

   public int func_204026_h() {
      return this.field_204027_h;
   }

   public int func_211727_m() {
      return this.field_211734_k;
   }

   public int getEndCityDistance() {
      return this.endCityDistance;
   }

   public int getEndCitySeparation() {
      return this.endCitySeparation;
   }

   public int getMansionDistance() {
      return this.mansionDistance;
   }

   public int getMansionSeparation() {
      return this.field_211737_q;
   }

   public IBlockState getDefaultBlock() {
      return this.defaultBlock;
   }

   public IBlockState getDefaultFluid() {
      return this.defaultFluid;
   }

   public void setDefautBlock(IBlockState state) {
      this.defaultBlock = state;
   }

   public void setDefaultFluid(IBlockState state) {
      this.defaultFluid = state;
   }
}