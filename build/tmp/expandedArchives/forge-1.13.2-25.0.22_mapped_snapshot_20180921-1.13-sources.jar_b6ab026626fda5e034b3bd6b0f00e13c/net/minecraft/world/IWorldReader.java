package net.minecraft.world;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IWorldReader extends IWorldReaderBase {
   @OnlyIn(Dist.CLIENT)
   int getCombinedLight(BlockPos pos, int lightValue);
}