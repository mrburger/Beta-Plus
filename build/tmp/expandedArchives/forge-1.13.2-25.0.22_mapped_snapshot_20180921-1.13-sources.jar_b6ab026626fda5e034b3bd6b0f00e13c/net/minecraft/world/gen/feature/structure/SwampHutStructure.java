package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Biomes;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;

public class SwampHutStructure extends ScatteredStructure<SwampHutConfig> {
   private static final List<Biome.SpawnListEntry> field_202384_d = Lists.newArrayList(new Biome.SpawnListEntry(EntityType.WITCH, 1, 1, 1));

   protected String getStructureName() {
      return "Swamp_Hut";
   }

   public int getSize() {
      return 3;
   }

   protected StructureStart makeStart(IWorld worldIn, IChunkGenerator<?> generator, SharedSeedRandom random, int x, int z) {
      Biome biome = generator.getBiomeProvider().getBiome(new BlockPos((x << 4) + 9, 0, (z << 4) + 9), Biomes.PLAINS);
      return new SwampHutStructure.Start(worldIn, random, x, z, biome);
   }

   protected int getSeedModifier() {
      return 14357620;
   }

   public List<Biome.SpawnListEntry> getSpawnList() {
      return field_202384_d;
   }

   public boolean func_202383_b(IWorld p_202383_1_, BlockPos p_202383_2_) {
      StructureStart structurestart = this.getStart(p_202383_1_, p_202383_2_);
      if (structurestart != NO_STRUCTURE && structurestart instanceof SwampHutStructure.Start && !structurestart.getComponents().isEmpty()) {
         StructurePiece structurepiece = structurestart.getComponents().get(0);
         return structurepiece instanceof SwampHutPiece;
      } else {
         return false;
      }
   }

   public static class Start extends StructureStart {
      public Start() {
      }

      public Start(IWorld worldIn, SharedSeedRandom random, int x, int z, Biome biomeIn) {
         super(x, z, biomeIn, random, worldIn.getSeed());
         SwampHutPiece swamphutpiece = new SwampHutPiece(random, x * 16, z * 16);
         this.components.add(swamphutpiece);
         this.recalculateStructureSize(worldIn);
      }
   }
}