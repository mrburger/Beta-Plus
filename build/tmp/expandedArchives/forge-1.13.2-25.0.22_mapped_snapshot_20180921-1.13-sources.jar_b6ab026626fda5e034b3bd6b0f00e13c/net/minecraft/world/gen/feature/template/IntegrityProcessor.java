package net.minecraft.world.gen.feature.template;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class IntegrityProcessor implements ITemplateProcessor {
   private final float chance;
   private final Random random;

   public IntegrityProcessor(BlockPos pos, PlacementSettings settings) {
      this.chance = settings.getIntegrity();
      this.random = settings.getRandom(pos);
   }

   @Nullable
   public Template.BlockInfo processBlock(IBlockReader worldIn, BlockPos pos, Template.BlockInfo blockInfoIn) {
      return !(this.chance >= 1.0F) && !(this.random.nextFloat() <= this.chance) ? null : blockInfoIn;
   }
}