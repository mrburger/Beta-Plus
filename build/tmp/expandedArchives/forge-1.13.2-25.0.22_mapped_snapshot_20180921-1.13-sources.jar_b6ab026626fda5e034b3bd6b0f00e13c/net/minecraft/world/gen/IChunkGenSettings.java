package net.minecraft.world.gen;

import net.minecraft.block.state.IBlockState;

public interface IChunkGenSettings {
   int getVillageDistance();

   int getVillageSeparation();

   int getOceanMonumentSpacing();

   int getOceanMonumentSeparation();

   int getStrongholdDistance();

   int getStrongholdCount();

   int getStrongholdSpread();

   int getBiomeFeatureDistance();

   int getBiomeFeatureSeparation();

   int func_204748_h();

   int func_211730_k();

   int func_204026_h();

   int func_211727_m();

   int getEndCityDistance();

   int getEndCitySeparation();

   int getMansionDistance();

   int getMansionSeparation();

   IBlockState getDefaultBlock();

   IBlockState getDefaultFluid();
}