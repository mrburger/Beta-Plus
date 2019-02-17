package net.minecraft.world.biome;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReaderBase;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BiomeColors {
   private static final BiomeColors.ColorResolver GRASS_COLOR = Biome::getGrassColor;
   private static final BiomeColors.ColorResolver FOLIAGE_COLOR = Biome::getFoliageColor;
   private static final BiomeColors.ColorResolver WATER_COLOR = (p_210280_0_, p_210280_1_) -> {
      return p_210280_0_.getWaterColor();
   };
   private static final BiomeColors.ColorResolver WATER_FOG_COLOR = (p_210279_0_, p_210279_1_) -> {
      return p_210279_0_.getWaterFogColor();
   };

   private static int getColor(IWorldReaderBase worldIn, BlockPos pos, BiomeColors.ColorResolver resolver) {
      int i = 0;
      int j = 0;
      int k = 0;
      int l = Minecraft.getInstance().gameSettings.biomeBlendRadius;
      int i1 = (l * 2 + 1) * (l * 2 + 1);

      for(BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(pos.getX() - l, pos.getY(), pos.getZ() - l, pos.getX() + l, pos.getY(), pos.getZ() + l)) {
         int j1 = resolver.getColor(worldIn.getBiome(blockpos$mutableblockpos), blockpos$mutableblockpos);
         i += (j1 & 16711680) >> 16;
         j += (j1 & '\uff00') >> 8;
         k += j1 & 255;
      }

      return (i / i1 & 255) << 16 | (j / i1 & 255) << 8 | k / i1 & 255;
   }

   public static int getGrassColor(IWorldReaderBase worldIn, BlockPos pos) {
      return getColor(worldIn, pos, GRASS_COLOR);
   }

   public static int getFoliageColor(IWorldReaderBase worldIn, BlockPos pos) {
      return getColor(worldIn, pos, FOLIAGE_COLOR);
   }

   public static int getWaterColor(IWorldReaderBase worldIn, BlockPos pos) {
      return getColor(worldIn, pos, WATER_COLOR);
   }

   @OnlyIn(Dist.CLIENT)
   interface ColorResolver {
      int getColor(Biome p_getColor_1_, BlockPos p_getColor_2_);
   }
}