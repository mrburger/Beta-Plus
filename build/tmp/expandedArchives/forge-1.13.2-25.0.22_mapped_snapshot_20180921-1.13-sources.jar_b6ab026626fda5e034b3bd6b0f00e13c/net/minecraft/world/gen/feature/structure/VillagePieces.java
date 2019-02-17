package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockGlassPane;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTorchWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.properties.SlabType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTableList;

public class VillagePieces {
   public static void registerVillagePieces() {
      StructureIO.registerStructureComponent(VillagePieces.House1.class, "ViBH");
      StructureIO.registerStructureComponent(VillagePieces.Field1.class, "ViDF");
      StructureIO.registerStructureComponent(VillagePieces.Field2.class, "ViF");
      StructureIO.registerStructureComponent(VillagePieces.Torch.class, "ViL");
      StructureIO.registerStructureComponent(VillagePieces.Hall.class, "ViPH");
      StructureIO.registerStructureComponent(VillagePieces.House4Garden.class, "ViSH");
      StructureIO.registerStructureComponent(VillagePieces.WoodHut.class, "ViSmH");
      StructureIO.registerStructureComponent(VillagePieces.Church.class, "ViST");
      StructureIO.registerStructureComponent(VillagePieces.House2.class, "ViS");
      StructureIO.registerStructureComponent(VillagePieces.Start.class, "ViStart");
      StructureIO.registerStructureComponent(VillagePieces.Path.class, "ViSR");
      StructureIO.registerStructureComponent(VillagePieces.House3.class, "ViTRH");
      StructureIO.registerStructureComponent(VillagePieces.Well.class, "ViW");
   }

   public static List<VillagePieces.PieceWeight> getStructureVillageWeightedPieceList(Random random, int size) {
      List<VillagePieces.PieceWeight> list = Lists.newArrayList();
      list.add(new VillagePieces.PieceWeight(VillagePieces.House4Garden.class, 4, MathHelper.nextInt(random, 2 + size, 4 + size * 2)));
      list.add(new VillagePieces.PieceWeight(VillagePieces.Church.class, 20, MathHelper.nextInt(random, 0 + size, 1 + size)));
      list.add(new VillagePieces.PieceWeight(VillagePieces.House1.class, 20, MathHelper.nextInt(random, 0 + size, 2 + size)));
      list.add(new VillagePieces.PieceWeight(VillagePieces.WoodHut.class, 3, MathHelper.nextInt(random, 2 + size, 5 + size * 3)));
      list.add(new VillagePieces.PieceWeight(VillagePieces.Hall.class, 15, MathHelper.nextInt(random, 0 + size, 2 + size)));
      list.add(new VillagePieces.PieceWeight(VillagePieces.Field1.class, 3, MathHelper.nextInt(random, 1 + size, 4 + size)));
      list.add(new VillagePieces.PieceWeight(VillagePieces.Field2.class, 3, MathHelper.nextInt(random, 2 + size, 4 + size * 2)));
      list.add(new VillagePieces.PieceWeight(VillagePieces.House2.class, 15, MathHelper.nextInt(random, 0, 1 + size)));
      list.add(new VillagePieces.PieceWeight(VillagePieces.House3.class, 8, MathHelper.nextInt(random, 0 + size, 3 + size * 2)));
      net.minecraftforge.fml.common.registry.VillagerRegistry.addExtraVillageComponents(list, random, size);
      Iterator<VillagePieces.PieceWeight> iterator = list.iterator();

      while(iterator.hasNext()) {
         if ((iterator.next()).villagePiecesLimit == 0) {
            iterator.remove();
         }
      }

      return list;
   }

   private static int updatePieceWeight(List<VillagePieces.PieceWeight> p_75079_0_) {
      boolean flag = false;
      int i = 0;

      for(VillagePieces.PieceWeight villagepieces$pieceweight : p_75079_0_) {
         if (villagepieces$pieceweight.villagePiecesLimit > 0 && villagepieces$pieceweight.villagePiecesSpawned < villagepieces$pieceweight.villagePiecesLimit) {
            flag = true;
         }

         i += villagepieces$pieceweight.villagePieceWeight;
      }

      return flag ? i : -1;
   }

   private static VillagePieces.Village findAndCreateComponentFactory(VillagePieces.Start start, VillagePieces.PieceWeight weight, List<StructurePiece> structureComponents, Random rand, int structureMinX, int structureMinY, int structureMinZ, EnumFacing facing, int componentType) {
      Class<? extends VillagePieces.Village> oclass = weight.villagePieceClass;
      VillagePieces.Village villagepieces$village = null;
      if (oclass == VillagePieces.House4Garden.class) {
         villagepieces$village = VillagePieces.House4Garden.createPiece(start, structureComponents, rand, structureMinX, structureMinY, structureMinZ, facing, componentType);
      } else if (oclass == VillagePieces.Church.class) {
         villagepieces$village = VillagePieces.Church.createPiece(start, structureComponents, rand, structureMinX, structureMinY, structureMinZ, facing, componentType);
      } else if (oclass == VillagePieces.House1.class) {
         villagepieces$village = VillagePieces.House1.createPiece(start, structureComponents, rand, structureMinX, structureMinY, structureMinZ, facing, componentType);
      } else if (oclass == VillagePieces.WoodHut.class) {
         villagepieces$village = VillagePieces.WoodHut.createPiece(start, structureComponents, rand, structureMinX, structureMinY, structureMinZ, facing, componentType);
      } else if (oclass == VillagePieces.Hall.class) {
         villagepieces$village = VillagePieces.Hall.createPiece(start, structureComponents, rand, structureMinX, structureMinY, structureMinZ, facing, componentType);
      } else if (oclass == VillagePieces.Field1.class) {
         villagepieces$village = VillagePieces.Field1.createPiece(start, structureComponents, rand, structureMinX, structureMinY, structureMinZ, facing, componentType);
      } else if (oclass == VillagePieces.Field2.class) {
         villagepieces$village = VillagePieces.Field2.createPiece(start, structureComponents, rand, structureMinX, structureMinY, structureMinZ, facing, componentType);
      } else if (oclass == VillagePieces.House2.class) {
         villagepieces$village = VillagePieces.House2.createPiece(start, structureComponents, rand, structureMinX, structureMinY, structureMinZ, facing, componentType);
      } else if (oclass == VillagePieces.House3.class) {
         villagepieces$village = VillagePieces.House3.createPiece(start, structureComponents, rand, structureMinX, structureMinY, structureMinZ, facing, componentType);
      } else {
         villagepieces$village = net.minecraftforge.fml.common.registry.VillagerRegistry.getVillageComponent(weight, start, structureComponents, rand, structureMinX, structureMinY, structureMinZ, facing, componentType);
      }

      return villagepieces$village;
   }

   private static VillagePieces.Village generateComponent(VillagePieces.Start start, List<StructurePiece> structureComponents, Random rand, int structureMinX, int structureMinY, int structureMinZ, EnumFacing facing, int componentType) {
      int i = updatePieceWeight(start.structureVillageWeightedPieceList);
      if (i <= 0) {
         return null;
      } else {
         int j = 0;

         while(j < 5) {
            ++j;
            int k = rand.nextInt(i);

            for(VillagePieces.PieceWeight villagepieces$pieceweight : start.structureVillageWeightedPieceList) {
               k -= villagepieces$pieceweight.villagePieceWeight;
               if (k < 0) {
                  if (!villagepieces$pieceweight.canSpawnMoreVillagePiecesOfType(componentType) || villagepieces$pieceweight == start.lastPlaced && start.structureVillageWeightedPieceList.size() > 1) {
                     break;
                  }

                  VillagePieces.Village villagepieces$village = findAndCreateComponentFactory(start, villagepieces$pieceweight, structureComponents, rand, structureMinX, structureMinY, structureMinZ, facing, componentType);
                  if (villagepieces$village != null) {
                     ++villagepieces$pieceweight.villagePiecesSpawned;
                     start.lastPlaced = villagepieces$pieceweight;
                     if (!villagepieces$pieceweight.canSpawnMoreVillagePieces()) {
                        start.structureVillageWeightedPieceList.remove(villagepieces$pieceweight);
                     }

                     return villagepieces$village;
                  }
               }
            }
         }

         MutableBoundingBox mutableboundingbox = VillagePieces.Torch.findPieceBox(start, structureComponents, rand, structureMinX, structureMinY, structureMinZ, facing);
         if (mutableboundingbox != null) {
            return new VillagePieces.Torch(start, componentType, rand, mutableboundingbox, facing);
         } else {
            return null;
         }
      }
   }

   private static StructurePiece generateAndAddComponent(VillagePieces.Start start, List<StructurePiece> structureComponents, Random rand, int structureMinX, int structureMinY, int structureMinZ, EnumFacing facing, int componentType) {
      if (componentType > 50) {
         return null;
      } else if (Math.abs(structureMinX - start.getBoundingBox().minX) <= 112 && Math.abs(structureMinZ - start.getBoundingBox().minZ) <= 112) {
         StructurePiece structurepiece = generateComponent(start, structureComponents, rand, structureMinX, structureMinY, structureMinZ, facing, componentType + 1);
         if (structurepiece != null) {
            structureComponents.add(structurepiece);
            start.pendingHouses.add(structurepiece);
            return structurepiece;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private static StructurePiece generateAndAddRoadPiece(VillagePieces.Start start, List<StructurePiece> p_176069_1_, Random rand, int p_176069_3_, int p_176069_4_, int p_176069_5_, EnumFacing facing, int p_176069_7_) {
      if (p_176069_7_ > 3 + start.terrainType) {
         return null;
      } else if (Math.abs(p_176069_3_ - start.getBoundingBox().minX) <= 112 && Math.abs(p_176069_5_ - start.getBoundingBox().minZ) <= 112) {
         MutableBoundingBox mutableboundingbox = VillagePieces.Path.findPieceBox(start, p_176069_1_, rand, p_176069_3_, p_176069_4_, p_176069_5_, facing);
         if (mutableboundingbox != null && mutableboundingbox.minY > 10) {
            StructurePiece structurepiece = new VillagePieces.Path(start, p_176069_7_, rand, mutableboundingbox, facing);
            p_176069_1_.add(structurepiece);
            start.pendingRoads.add(structurepiece);
            return structurepiece;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public static class Church extends VillagePieces.Village {
      public Church() {
      }

      public Church(VillagePieces.Start start, int type, Random rand, MutableBoundingBox p_i45564_4_, EnumFacing facing) {
         super(start, type);
         this.setCoordBaseMode(facing);
         this.boundingBox = p_i45564_4_;
      }

      public static VillagePieces.Church createPiece(VillagePieces.Start start, List<StructurePiece> p_175854_1_, Random rand, int p_175854_3_, int p_175854_4_, int p_175854_5_, EnumFacing facing, int p_175854_7_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_175854_3_, p_175854_4_, p_175854_5_, 0, 0, 0, 5, 12, 9, facing);
         return canVillageGoDeeper(mutableboundingbox) && StructurePiece.findIntersecting(p_175854_1_, mutableboundingbox) == null ? new VillagePieces.Church(start, p_175854_7_, rand, mutableboundingbox, facing) : null;
      }

      /**
       * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
       * the end, it adds Fences...
       */
      public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_74875_4_) {
         if (this.averageGroundLvl < 0) {
            this.averageGroundLvl = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);
            if (this.averageGroundLvl < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 12 - 1, 0);
         }

         IBlockState iblockstate = Blocks.COBBLESTONE.getDefaultState();
         IBlockState iblockstate1 = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.NORTH));
         IBlockState iblockstate2 = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.WEST));
         IBlockState iblockstate3 = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.EAST));
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 3, 3, 7, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 5, 1, 3, 9, 3, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 0, 3, 0, 8, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 0, 3, 10, 0, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 1, 0, 10, 3, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 1, 4, 10, 3, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 4, 0, 4, 7, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 0, 4, 4, 4, 7, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 8, 3, 4, 8, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 5, 4, 3, 10, 4, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 5, 5, 3, 5, 7, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 9, 0, 4, 9, 4, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 0, 4, 4, 4, iblockstate, iblockstate, false);
         this.setBlockState(worldIn, iblockstate, 0, 11, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate, 4, 11, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate, 2, 11, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate, 2, 11, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate, 1, 1, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate, 1, 1, 7, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate, 2, 1, 7, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate, 3, 1, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate, 3, 1, 7, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate1, 1, 1, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate1, 2, 1, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate1, 3, 1, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate2, 1, 2, 7, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate3, 3, 2, 7, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 0, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 0, 3, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 4, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 4, 3, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 0, 6, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 0, 7, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 4, 6, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 4, 7, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 2, 6, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 2, 7, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 2, 6, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 2, 7, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 0, 3, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 4, 3, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 2, 3, 8, structureBoundingBoxIn);
         this.placeTorch(worldIn, EnumFacing.SOUTH, 2, 4, 7, structureBoundingBoxIn);
         this.placeTorch(worldIn, EnumFacing.EAST, 1, 4, 6, structureBoundingBoxIn);
         this.placeTorch(worldIn, EnumFacing.WEST, 3, 4, 6, structureBoundingBoxIn);
         this.placeTorch(worldIn, EnumFacing.NORTH, 2, 4, 5, structureBoundingBoxIn);
         IBlockState iblockstate4 = Blocks.LADDER.getDefaultState().with(BlockLadder.FACING, EnumFacing.WEST);

         for(int i = 1; i <= 9; ++i) {
            this.setBlockState(worldIn, iblockstate4, 3, i, 3, structureBoundingBoxIn);
         }

         this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 2, 1, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 2, 2, 0, structureBoundingBoxIn);
         this.createVillageDoor(worldIn, structureBoundingBoxIn, randomIn, 2, 1, 0, EnumFacing.NORTH);
         if (this.getBlockStateFromPos(worldIn, 2, 0, -1, structureBoundingBoxIn).isAir() && !this.getBlockStateFromPos(worldIn, 2, -1, -1, structureBoundingBoxIn).isAir()) {
            this.setBlockState(worldIn, iblockstate1, 2, 0, -1, structureBoundingBoxIn);
            if (this.getBlockStateFromPos(worldIn, 2, -1, -1, structureBoundingBoxIn).getBlock() == Blocks.GRASS_PATH) {
               this.setBlockState(worldIn, Blocks.GRASS_BLOCK.getDefaultState(), 2, -1, -1, structureBoundingBoxIn);
            }
         }

         for(int k = 0; k < 9; ++k) {
            for(int j = 0; j < 5; ++j) {
               this.clearCurrentPositionBlocksUpwards(worldIn, j, 12, k, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, iblockstate, j, -1, k, structureBoundingBoxIn);
            }
         }

         this.spawnVillagers(worldIn, structureBoundingBoxIn, 2, 1, 2, 1);
         return true;
      }

      protected int chooseProfession(int villagersSpawnedIn, int currentVillagerProfession) {
         return 2;
      }
   }

   public static class Field1 extends VillagePieces.Village {
      /** First crop type for this field. */
      private IBlockState cropTypeA;
      /** Second crop type for this field. */
      private IBlockState cropTypeB;
      /** Third crop type for this field. */
      private IBlockState cropTypeC;
      /** Fourth crop type for this field. */
      private IBlockState cropTypeD;

      public Field1() {
      }

      public Field1(VillagePieces.Start start, int type, Random rand, MutableBoundingBox p_i45570_4_, EnumFacing facing) {
         super(start, type);
         this.setCoordBaseMode(facing);
         this.boundingBox = p_i45570_4_;
         this.cropTypeA = VillagePieces.Field2.func_197529_b(rand);
         this.cropTypeB = VillagePieces.Field2.func_197529_b(rand);
         this.cropTypeC = VillagePieces.Field2.func_197529_b(rand);
         this.cropTypeD = VillagePieces.Field2.func_197529_b(rand);
      }

      /**
       * (abstract) Helper method to write subclass data to NBT
       */
      protected void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setTag("CA", NBTUtil.writeBlockState(this.cropTypeA));
         tagCompound.setTag("CB", NBTUtil.writeBlockState(this.cropTypeB));
         tagCompound.setTag("CC", NBTUtil.writeBlockState(this.cropTypeC));
         tagCompound.setTag("CD", NBTUtil.writeBlockState(this.cropTypeD));
      }

      /**
       * (abstract) Helper method to read subclass data from NBT
       */
      protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager p_143011_2_) {
         super.readStructureFromNBT(tagCompound, p_143011_2_);
         this.cropTypeA = NBTUtil.readBlockState(tagCompound.getCompound("CA"));
         this.cropTypeB = NBTUtil.readBlockState(tagCompound.getCompound("CB"));
         this.cropTypeC = NBTUtil.readBlockState(tagCompound.getCompound("CC"));
         this.cropTypeD = NBTUtil.readBlockState(tagCompound.getCompound("CD"));
         if (!(this.cropTypeA.getBlock() instanceof BlockCrops)) {
            this.cropTypeA = Blocks.WHEAT.getDefaultState();
         }

         if (!(this.cropTypeB.getBlock() instanceof BlockCrops)) {
            this.cropTypeB = Blocks.CARROTS.getDefaultState();
         }

         if (!(this.cropTypeC.getBlock() instanceof BlockCrops)) {
            this.cropTypeC = Blocks.POTATOES.getDefaultState();
         }

         if (!(this.cropTypeD.getBlock() instanceof BlockCrops)) {
            this.cropTypeD = Blocks.BEETROOTS.getDefaultState();
         }

      }

      public static VillagePieces.Field1 createPiece(VillagePieces.Start start, List<StructurePiece> p_175851_1_, Random rand, int p_175851_3_, int p_175851_4_, int p_175851_5_, EnumFacing facing, int p_175851_7_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_175851_3_, p_175851_4_, p_175851_5_, 0, 0, 0, 13, 4, 9, facing);
         return canVillageGoDeeper(mutableboundingbox) && StructurePiece.findIntersecting(p_175851_1_, mutableboundingbox) == null ? new VillagePieces.Field1(start, p_175851_7_, rand, mutableboundingbox, facing) : null;
      }

      /**
       * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
       * the end, it adds Fences...
       */
      public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_74875_4_) {
         if (this.averageGroundLvl < 0) {
            this.averageGroundLvl = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);
            if (this.averageGroundLvl < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 4 - 1, 0);
         }

         IBlockState iblockstate = this.getBiomeSpecificBlockState(Blocks.OAK_LOG.getDefaultState());
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 12, 4, 8, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 1, 2, 0, 7, Blocks.FARMLAND.getDefaultState(), Blocks.FARMLAND.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 0, 1, 5, 0, 7, Blocks.FARMLAND.getDefaultState(), Blocks.FARMLAND.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 0, 1, 8, 0, 7, Blocks.FARMLAND.getDefaultState(), Blocks.FARMLAND.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 0, 1, 11, 0, 7, Blocks.FARMLAND.getDefaultState(), Blocks.FARMLAND.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 0, 0, 8, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 0, 0, 6, 0, 8, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 12, 0, 0, 12, 0, 8, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 0, 11, 0, 0, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 8, 11, 0, 8, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 0, 1, 3, 0, 7, Blocks.WATER.getDefaultState(), Blocks.WATER.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 0, 1, 9, 0, 7, Blocks.WATER.getDefaultState(), Blocks.WATER.getDefaultState(), false);

         for(int i = 1; i <= 7; ++i) {
            BlockCrops blockcrops = (BlockCrops)this.cropTypeA.getBlock();
            int j = blockcrops.getMaxAge();
            int k = j / 3;
            this.setBlockState(worldIn, this.cropTypeA.with(blockcrops.getAgeProperty(), Integer.valueOf(MathHelper.nextInt(randomIn, k, j))), 1, 1, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, this.cropTypeA.with(blockcrops.getAgeProperty(), Integer.valueOf(MathHelper.nextInt(randomIn, k, j))), 2, 1, i, structureBoundingBoxIn);
            blockcrops = (BlockCrops)this.cropTypeB.getBlock();
            int l = blockcrops.getMaxAge();
            int i1 = l / 3;
            this.setBlockState(worldIn, this.cropTypeB.with(blockcrops.getAgeProperty(), Integer.valueOf(MathHelper.nextInt(randomIn, i1, l))), 4, 1, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, this.cropTypeB.with(blockcrops.getAgeProperty(), Integer.valueOf(MathHelper.nextInt(randomIn, i1, l))), 5, 1, i, structureBoundingBoxIn);
            blockcrops = (BlockCrops)this.cropTypeC.getBlock();
            int j1 = blockcrops.getMaxAge();
            int k1 = j1 / 3;
            this.setBlockState(worldIn, this.cropTypeC.with(blockcrops.getAgeProperty(), Integer.valueOf(MathHelper.nextInt(randomIn, k1, j1))), 7, 1, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, this.cropTypeC.with(blockcrops.getAgeProperty(), Integer.valueOf(MathHelper.nextInt(randomIn, k1, j1))), 8, 1, i, structureBoundingBoxIn);
            blockcrops = (BlockCrops)this.cropTypeD.getBlock();
            int l1 = blockcrops.getMaxAge();
            int i2 = l1 / 3;
            this.setBlockState(worldIn, this.cropTypeD.with(blockcrops.getAgeProperty(), Integer.valueOf(MathHelper.nextInt(randomIn, i2, l1))), 10, 1, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, this.cropTypeD.with(blockcrops.getAgeProperty(), Integer.valueOf(MathHelper.nextInt(randomIn, i2, l1))), 11, 1, i, structureBoundingBoxIn);
         }

         for(int j2 = 0; j2 < 9; ++j2) {
            for(int k2 = 0; k2 < 13; ++k2) {
               this.clearCurrentPositionBlocksUpwards(worldIn, k2, 4, j2, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.DIRT.getDefaultState(), k2, -1, j2, structureBoundingBoxIn);
            }
         }

         return true;
      }
   }

   public static class Field2 extends VillagePieces.Village {
      /** First crop type for this field. */
      private IBlockState cropTypeA;
      /** Second crop type for this field. */
      private IBlockState cropTypeB;

      public Field2() {
      }

      public Field2(VillagePieces.Start start, int p_i45569_2_, Random rand, MutableBoundingBox p_i45569_4_, EnumFacing facing) {
         super(start, p_i45569_2_);
         this.setCoordBaseMode(facing);
         this.boundingBox = p_i45569_4_;
         this.cropTypeA = func_197529_b(rand);
         this.cropTypeB = func_197529_b(rand);
      }

      /**
       * (abstract) Helper method to write subclass data to NBT
       */
      protected void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setTag("CA", NBTUtil.writeBlockState(this.cropTypeA));
         tagCompound.setTag("CB", NBTUtil.writeBlockState(this.cropTypeB));
      }

      /**
       * (abstract) Helper method to read subclass data from NBT
       */
      protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager p_143011_2_) {
         super.readStructureFromNBT(tagCompound, p_143011_2_);
         this.cropTypeA = NBTUtil.readBlockState(tagCompound.getCompound("CA"));
         this.cropTypeB = NBTUtil.readBlockState(tagCompound.getCompound("CB"));
      }

      private static IBlockState func_197529_b(Random p_197529_0_) {
         switch(p_197529_0_.nextInt(10)) {
         case 0:
         case 1:
            return Blocks.CARROTS.getDefaultState();
         case 2:
         case 3:
            return Blocks.POTATOES.getDefaultState();
         case 4:
            return Blocks.BEETROOTS.getDefaultState();
         default:
            return Blocks.WHEAT.getDefaultState();
         }
      }

      public static VillagePieces.Field2 createPiece(VillagePieces.Start start, List<StructurePiece> p_175852_1_, Random rand, int p_175852_3_, int p_175852_4_, int p_175852_5_, EnumFacing facing, int p_175852_7_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_175852_3_, p_175852_4_, p_175852_5_, 0, 0, 0, 7, 4, 9, facing);
         return canVillageGoDeeper(mutableboundingbox) && StructurePiece.findIntersecting(p_175852_1_, mutableboundingbox) == null ? new VillagePieces.Field2(start, p_175852_7_, rand, mutableboundingbox, facing) : null;
      }

      /**
       * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
       * the end, it adds Fences...
       */
      public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_74875_4_) {
         if (this.averageGroundLvl < 0) {
            this.averageGroundLvl = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);
            if (this.averageGroundLvl < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 4 - 1, 0);
         }

         IBlockState iblockstate = this.getBiomeSpecificBlockState(Blocks.OAK_LOG.getDefaultState());
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 6, 4, 8, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 1, 2, 0, 7, Blocks.FARMLAND.getDefaultState(), Blocks.FARMLAND.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 0, 1, 5, 0, 7, Blocks.FARMLAND.getDefaultState(), Blocks.FARMLAND.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 0, 0, 8, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 0, 0, 6, 0, 8, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 0, 5, 0, 0, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 8, 5, 0, 8, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 0, 1, 3, 0, 7, Blocks.WATER.getDefaultState(), Blocks.WATER.getDefaultState(), false);

         for(int i = 1; i <= 7; ++i) {
            BlockCrops blockcrops = (BlockCrops)this.cropTypeA.getBlock();
            int j = blockcrops.getMaxAge();
            int k = j / 3;
            this.setBlockState(worldIn, this.cropTypeA.with(blockcrops.getAgeProperty(), Integer.valueOf(MathHelper.nextInt(randomIn, k, j))), 1, 1, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, this.cropTypeA.with(blockcrops.getAgeProperty(), Integer.valueOf(MathHelper.nextInt(randomIn, k, j))), 2, 1, i, structureBoundingBoxIn);
            blockcrops = (BlockCrops)this.cropTypeB.getBlock();
            int l = blockcrops.getMaxAge();
            int i1 = l / 3;
            this.setBlockState(worldIn, this.cropTypeB.with(blockcrops.getAgeProperty(), Integer.valueOf(MathHelper.nextInt(randomIn, i1, l))), 4, 1, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, this.cropTypeB.with(blockcrops.getAgeProperty(), Integer.valueOf(MathHelper.nextInt(randomIn, i1, l))), 5, 1, i, structureBoundingBoxIn);
         }

         for(int j1 = 0; j1 < 9; ++j1) {
            for(int k1 = 0; k1 < 7; ++k1) {
               this.clearCurrentPositionBlocksUpwards(worldIn, k1, 4, j1, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.DIRT.getDefaultState(), k1, -1, j1, structureBoundingBoxIn);
            }
         }

         return true;
      }
   }

   public static class Hall extends VillagePieces.Village {
      public Hall() {
      }

      public Hall(VillagePieces.Start start, int type, Random rand, MutableBoundingBox p_i45567_4_, EnumFacing facing) {
         super(start, type);
         this.setCoordBaseMode(facing);
         this.boundingBox = p_i45567_4_;
      }

      public static VillagePieces.Hall createPiece(VillagePieces.Start start, List<StructurePiece> p_175857_1_, Random rand, int p_175857_3_, int p_175857_4_, int p_175857_5_, EnumFacing facing, int p_175857_7_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_175857_3_, p_175857_4_, p_175857_5_, 0, 0, 0, 9, 7, 11, facing);
         return canVillageGoDeeper(mutableboundingbox) && StructurePiece.findIntersecting(p_175857_1_, mutableboundingbox) == null ? new VillagePieces.Hall(start, p_175857_7_, rand, mutableboundingbox, facing) : null;
      }

      /**
       * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
       * the end, it adds Fences...
       */
      public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_74875_4_) {
         if (this.averageGroundLvl < 0) {
            this.averageGroundLvl = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);
            if (this.averageGroundLvl < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 7 - 1, 0);
         }

         IBlockState iblockstate = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE.getDefaultState());
         IBlockState iblockstate1 = this.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.NORTH));
         IBlockState iblockstate2 = this.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.SOUTH));
         IBlockState iblockstate3 = this.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.WEST));
         IBlockState iblockstate4 = this.getBiomeSpecificBlockState(Blocks.OAK_PLANKS.getDefaultState());
         IBlockState iblockstate5 = this.getBiomeSpecificBlockState(Blocks.OAK_LOG.getDefaultState());
         IBlockState iblockstate6 = this.getBiomeSpecificBlockState(Blocks.OAK_FENCE.getDefaultState());
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 7, 4, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 6, 8, 4, 10, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 0, 6, 8, 0, 10, Blocks.DIRT.getDefaultState(), Blocks.DIRT.getDefaultState(), false);
         this.setBlockState(worldIn, iblockstate, 6, 0, 6, structureBoundingBoxIn);
         IBlockState iblockstate7 = iblockstate6.with(BlockFence.NORTH, Boolean.valueOf(true)).with(BlockFence.SOUTH, Boolean.valueOf(true));
         IBlockState iblockstate8 = iblockstate6.with(BlockFence.WEST, Boolean.valueOf(true)).with(BlockFence.EAST, Boolean.valueOf(true));
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 6, 2, 1, 9, iblockstate7, iblockstate7, false);
         this.setBlockState(worldIn, iblockstate6.with(BlockFence.SOUTH, Boolean.valueOf(true)).with(BlockFence.EAST, Boolean.valueOf(true)), 2, 1, 10, structureBoundingBoxIn);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 1, 6, 8, 1, 9, iblockstate7, iblockstate7, false);
         this.setBlockState(worldIn, iblockstate6.with(BlockFence.SOUTH, Boolean.valueOf(true)).with(BlockFence.WEST, Boolean.valueOf(true)), 8, 1, 10, structureBoundingBoxIn);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 10, 7, 1, 10, iblockstate8, iblockstate8, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 1, 7, 0, 4, iblockstate4, iblockstate4, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 0, 3, 5, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 0, 0, 8, 3, 5, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 0, 7, 1, 0, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 5, 7, 1, 5, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 0, 7, 3, 0, iblockstate4, iblockstate4, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 5, 7, 3, 5, iblockstate4, iblockstate4, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 1, 8, 4, 1, iblockstate4, iblockstate4, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 4, 8, 4, 4, iblockstate4, iblockstate4, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 2, 8, 5, 3, iblockstate4, iblockstate4, false);
         this.setBlockState(worldIn, iblockstate4, 0, 4, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate4, 0, 4, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate4, 8, 4, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate4, 8, 4, 3, structureBoundingBoxIn);
         IBlockState iblockstate9 = iblockstate1;
         IBlockState iblockstate10 = iblockstate2;

         for(int i = -1; i <= 2; ++i) {
            for(int j = 0; j <= 8; ++j) {
               this.setBlockState(worldIn, iblockstate9, j, 4 + i, i, structureBoundingBoxIn);
               this.setBlockState(worldIn, iblockstate10, j, 4 + i, 5 - i, structureBoundingBoxIn);
            }
         }

         this.setBlockState(worldIn, iblockstate5, 0, 2, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate5, 0, 2, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate5, 8, 2, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate5, 8, 2, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 0, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 0, 2, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 8, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 8, 2, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 2, 2, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 3, 2, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 5, 2, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate6, 2, 1, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.OAK_PRESSURE_PLATE.getDefaultState(), 2, 2, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate4, 1, 1, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate9, 2, 1, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate3, 1, 1, 3, structureBoundingBoxIn);
         IBlockState iblockstate11 = Blocks.STONE_SLAB.getDefaultState().with(BlockSlab.TYPE, SlabType.DOUBLE);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 0, 1, 7, 0, 3, iblockstate11, iblockstate11, false);
         this.setBlockState(worldIn, iblockstate11, 6, 1, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate11, 6, 1, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 2, 1, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 2, 2, 0, structureBoundingBoxIn);
         this.placeTorch(worldIn, EnumFacing.NORTH, 2, 3, 1, structureBoundingBoxIn);
         this.createVillageDoor(worldIn, structureBoundingBoxIn, randomIn, 2, 1, 0, EnumFacing.NORTH);
         if (this.getBlockStateFromPos(worldIn, 2, 0, -1, structureBoundingBoxIn).isAir() && !this.getBlockStateFromPos(worldIn, 2, -1, -1, structureBoundingBoxIn).isAir()) {
            this.setBlockState(worldIn, iblockstate9, 2, 0, -1, structureBoundingBoxIn);
            if (this.getBlockStateFromPos(worldIn, 2, -1, -1, structureBoundingBoxIn).getBlock() == Blocks.GRASS_PATH) {
               this.setBlockState(worldIn, Blocks.GRASS_BLOCK.getDefaultState(), 2, -1, -1, structureBoundingBoxIn);
            }
         }

         this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 6, 1, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 6, 2, 5, structureBoundingBoxIn);
         this.placeTorch(worldIn, EnumFacing.SOUTH, 6, 3, 4, structureBoundingBoxIn);
         this.createVillageDoor(worldIn, structureBoundingBoxIn, randomIn, 6, 1, 5, EnumFacing.SOUTH);

         for(int l = 0; l < 5; ++l) {
            for(int k = 0; k < 9; ++k) {
               this.clearCurrentPositionBlocksUpwards(worldIn, k, 7, l, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, iblockstate, k, -1, l, structureBoundingBoxIn);
            }
         }

         this.spawnVillagers(worldIn, structureBoundingBoxIn, 4, 1, 2, 2);
         return true;
      }

      protected int chooseProfession(int villagersSpawnedIn, int currentVillagerProfession) {
         return villagersSpawnedIn == 0 ? 4 : super.chooseProfession(villagersSpawnedIn, currentVillagerProfession);
      }
   }

   public static class House1 extends VillagePieces.Village {
      public House1() {
      }

      public House1(VillagePieces.Start start, int type, Random rand, MutableBoundingBox p_i45571_4_, EnumFacing facing) {
         super(start, type);
         this.setCoordBaseMode(facing);
         this.boundingBox = p_i45571_4_;
      }

      public static VillagePieces.House1 createPiece(VillagePieces.Start start, List<StructurePiece> p_175850_1_, Random rand, int p_175850_3_, int p_175850_4_, int p_175850_5_, EnumFacing facing, int p_175850_7_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_175850_3_, p_175850_4_, p_175850_5_, 0, 0, 0, 9, 9, 6, facing);
         return canVillageGoDeeper(mutableboundingbox) && StructurePiece.findIntersecting(p_175850_1_, mutableboundingbox) == null ? new VillagePieces.House1(start, p_175850_7_, rand, mutableboundingbox, facing) : null;
      }

      /**
       * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
       * the end, it adds Fences...
       */
      public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_74875_4_) {
         if (this.averageGroundLvl < 0) {
            this.averageGroundLvl = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);
            if (this.averageGroundLvl < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 9 - 1, 0);
         }

         IBlockState iblockstate = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE.getDefaultState());
         IBlockState iblockstate1 = this.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.NORTH));
         IBlockState iblockstate2 = this.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.SOUTH));
         IBlockState iblockstate3 = this.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.EAST));
         IBlockState iblockstate4 = this.getBiomeSpecificBlockState(Blocks.OAK_PLANKS.getDefaultState());
         IBlockState iblockstate5 = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.NORTH));
         IBlockState iblockstate6 = this.getBiomeSpecificBlockState(Blocks.OAK_FENCE.getDefaultState());
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 7, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 8, 0, 5, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 0, 8, 5, 5, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 6, 1, 8, 6, 4, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 7, 2, 8, 7, 3, iblockstate, iblockstate, false);

         for(int i = -1; i <= 2; ++i) {
            for(int j = 0; j <= 8; ++j) {
               this.setBlockState(worldIn, iblockstate1, j, 6 + i, i, structureBoundingBoxIn);
               this.setBlockState(worldIn, iblockstate2, j, 6 + i, 5 - i, structureBoundingBoxIn);
            }
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 0, 1, 5, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 5, 8, 1, 5, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 1, 0, 8, 1, 4, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 0, 7, 1, 0, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 0, 4, 0, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 5, 0, 4, 5, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 2, 5, 8, 4, 5, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 2, 0, 8, 4, 0, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 1, 0, 4, 4, iblockstate4, iblockstate4, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 5, 7, 4, 5, iblockstate4, iblockstate4, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 2, 1, 8, 4, 4, iblockstate4, iblockstate4, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 0, 7, 4, 0, iblockstate4, iblockstate4, false);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 4, 2, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 5, 2, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 6, 2, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 4, 3, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 5, 3, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 6, 3, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 0, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 0, 2, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 0, 3, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 0, 3, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 8, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 8, 2, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 8, 3, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 8, 3, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 2, 2, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 3, 2, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 5, 2, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 6, 2, 5, structureBoundingBoxIn);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 1, 7, 4, 1, iblockstate4, iblockstate4, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 4, 7, 4, 4, iblockstate4, iblockstate4, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 4, 7, 3, 4, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
         this.setBlockState(worldIn, iblockstate4, 7, 1, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate3, 7, 1, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate1, 6, 1, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate1, 5, 1, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate1, 4, 1, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate1, 3, 1, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate6, 6, 1, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.OAK_PRESSURE_PLATE.getDefaultState(), 6, 2, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate6, 4, 1, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.OAK_PRESSURE_PLATE.getDefaultState(), 4, 2, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.CRAFTING_TABLE.getDefaultState(), 7, 1, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 1, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 2, 0, structureBoundingBoxIn);
         this.createVillageDoor(worldIn, structureBoundingBoxIn, randomIn, 1, 1, 0, EnumFacing.NORTH);
         if (this.getBlockStateFromPos(worldIn, 1, 0, -1, structureBoundingBoxIn).isAir() && !this.getBlockStateFromPos(worldIn, 1, -1, -1, structureBoundingBoxIn).isAir()) {
            this.setBlockState(worldIn, iblockstate5, 1, 0, -1, structureBoundingBoxIn);
            if (this.getBlockStateFromPos(worldIn, 1, -1, -1, structureBoundingBoxIn).getBlock() == Blocks.GRASS_PATH) {
               this.setBlockState(worldIn, Blocks.GRASS_BLOCK.getDefaultState(), 1, -1, -1, structureBoundingBoxIn);
            }
         }

         for(int l = 0; l < 6; ++l) {
            for(int k = 0; k < 9; ++k) {
               this.clearCurrentPositionBlocksUpwards(worldIn, k, 9, l, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, iblockstate, k, -1, l, structureBoundingBoxIn);
            }
         }

         this.spawnVillagers(worldIn, structureBoundingBoxIn, 2, 1, 2, 1);
         return true;
      }

      protected int chooseProfession(int villagersSpawnedIn, int currentVillagerProfession) {
         return 1;
      }
   }

   public static class House2 extends VillagePieces.Village {
      private boolean hasMadeChest;

      public House2() {
      }

      public House2(VillagePieces.Start start, int type, Random rand, MutableBoundingBox p_i45563_4_, EnumFacing facing) {
         super(start, type);
         this.setCoordBaseMode(facing);
         this.boundingBox = p_i45563_4_;
      }

      public static VillagePieces.House2 createPiece(VillagePieces.Start start, List<StructurePiece> p_175855_1_, Random rand, int p_175855_3_, int p_175855_4_, int p_175855_5_, EnumFacing facing, int p_175855_7_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_175855_3_, p_175855_4_, p_175855_5_, 0, 0, 0, 10, 6, 7, facing);
         return canVillageGoDeeper(mutableboundingbox) && StructurePiece.findIntersecting(p_175855_1_, mutableboundingbox) == null ? new VillagePieces.House2(start, p_175855_7_, rand, mutableboundingbox, facing) : null;
      }

      /**
       * (abstract) Helper method to write subclass data to NBT
       */
      protected void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setBoolean("Chest", this.hasMadeChest);
      }

      /**
       * (abstract) Helper method to read subclass data from NBT
       */
      protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager p_143011_2_) {
         super.readStructureFromNBT(tagCompound, p_143011_2_);
         this.hasMadeChest = tagCompound.getBoolean("Chest");
      }

      /**
       * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
       * the end, it adds Fences...
       */
      public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_74875_4_) {
         if (this.averageGroundLvl < 0) {
            this.averageGroundLvl = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);
            if (this.averageGroundLvl < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 6 - 1, 0);
         }

         IBlockState iblockstate = Blocks.COBBLESTONE.getDefaultState();
         IBlockState iblockstate1 = this.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.NORTH));
         IBlockState iblockstate2 = this.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.WEST));
         IBlockState iblockstate3 = this.getBiomeSpecificBlockState(Blocks.OAK_PLANKS.getDefaultState());
         IBlockState iblockstate4 = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.NORTH));
         IBlockState iblockstate5 = this.getBiomeSpecificBlockState(Blocks.OAK_LOG.getDefaultState());
         IBlockState iblockstate6 = this.getBiomeSpecificBlockState(Blocks.OAK_FENCE.getDefaultState());
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 9, 4, 6, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 9, 0, 6, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 0, 9, 4, 6, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 0, 9, 5, 6, Blocks.STONE_SLAB.getDefaultState(), Blocks.STONE_SLAB.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 5, 1, 8, 5, 5, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 0, 2, 3, 0, iblockstate3, iblockstate3, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 0, 4, 0, iblockstate5, iblockstate5, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 0, 3, 4, 0, iblockstate5, iblockstate5, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 6, 0, 4, 6, iblockstate5, iblockstate5, false);
         this.setBlockState(worldIn, iblockstate3, 3, 3, 1, structureBoundingBoxIn);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 2, 3, 3, 2, iblockstate3, iblockstate3, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 3, 5, 3, 3, iblockstate3, iblockstate3, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 1, 0, 3, 5, iblockstate3, iblockstate3, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 6, 5, 3, 6, iblockstate3, iblockstate3, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 0, 5, 3, 0, iblockstate6, iblockstate6, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 1, 0, 9, 3, 0, iblockstate6, iblockstate6, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 4, 9, 4, 6, iblockstate, iblockstate, false);
         this.setBlockState(worldIn, Blocks.LAVA.getDefaultState(), 7, 1, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.LAVA.getDefaultState(), 8, 1, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.IRON_BARS.getDefaultState().with(BlockPane.NORTH, Boolean.valueOf(true)).with(BlockPane.SOUTH, Boolean.valueOf(true)), 9, 2, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.IRON_BARS.getDefaultState().with(BlockPane.NORTH, Boolean.valueOf(true)), 9, 2, 4, structureBoundingBoxIn);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 2, 4, 8, 2, 5, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.setBlockState(worldIn, iblockstate, 6, 1, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.FURNACE.getDefaultState().with(BlockFurnace.FACING, EnumFacing.SOUTH), 6, 2, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.FURNACE.getDefaultState().with(BlockFurnace.FACING, EnumFacing.SOUTH), 6, 3, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.STONE_SLAB.getDefaultState().with(BlockSlab.TYPE, SlabType.DOUBLE), 8, 1, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 0, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 0, 2, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 2, 2, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 4, 2, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate6, 2, 1, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.OAK_PRESSURE_PLATE.getDefaultState(), 2, 2, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate3, 1, 1, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate1, 2, 1, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate2, 1, 1, 4, structureBoundingBoxIn);
         if (!this.hasMadeChest && structureBoundingBoxIn.isVecInside(new BlockPos(this.getXWithOffset(5, 5), this.getYWithOffset(1), this.getZWithOffset(5, 5)))) {
            this.hasMadeChest = true;
            this.generateChest(worldIn, structureBoundingBoxIn, randomIn, 5, 1, 5, LootTableList.CHESTS_VILLAGE_BLACKSMITH);
         }

         for(int i = 6; i <= 8; ++i) {
            if (this.getBlockStateFromPos(worldIn, i, 0, -1, structureBoundingBoxIn).isAir() && !this.getBlockStateFromPos(worldIn, i, -1, -1, structureBoundingBoxIn).isAir()) {
               this.setBlockState(worldIn, iblockstate4, i, 0, -1, structureBoundingBoxIn);
               if (this.getBlockStateFromPos(worldIn, i, -1, -1, structureBoundingBoxIn).getBlock() == Blocks.GRASS_PATH) {
                  this.setBlockState(worldIn, Blocks.GRASS_BLOCK.getDefaultState(), i, -1, -1, structureBoundingBoxIn);
               }
            }
         }

         for(int k = 0; k < 7; ++k) {
            for(int j = 0; j < 10; ++j) {
               this.clearCurrentPositionBlocksUpwards(worldIn, j, 6, k, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, iblockstate, j, -1, k, structureBoundingBoxIn);
            }
         }

         this.spawnVillagers(worldIn, structureBoundingBoxIn, 7, 1, 1, 1);
         return true;
      }

      protected int chooseProfession(int villagersSpawnedIn, int currentVillagerProfession) {
         return 3;
      }
   }

   public static class House3 extends VillagePieces.Village {
      public House3() {
      }

      public House3(VillagePieces.Start start, int type, Random rand, MutableBoundingBox p_i45561_4_, EnumFacing facing) {
         super(start, type);
         this.setCoordBaseMode(facing);
         this.boundingBox = p_i45561_4_;
      }

      public static VillagePieces.House3 createPiece(VillagePieces.Start start, List<StructurePiece> p_175849_1_, Random rand, int p_175849_3_, int p_175849_4_, int p_175849_5_, EnumFacing facing, int p_175849_7_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_175849_3_, p_175849_4_, p_175849_5_, 0, 0, 0, 9, 7, 12, facing);
         return canVillageGoDeeper(mutableboundingbox) && StructurePiece.findIntersecting(p_175849_1_, mutableboundingbox) == null ? new VillagePieces.House3(start, p_175849_7_, rand, mutableboundingbox, facing) : null;
      }

      /**
       * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
       * the end, it adds Fences...
       */
      public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_74875_4_) {
         if (this.averageGroundLvl < 0) {
            this.averageGroundLvl = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);
            if (this.averageGroundLvl < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 7 - 1, 0);
         }

         IBlockState iblockstate = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE.getDefaultState());
         IBlockState iblockstate1 = this.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.NORTH));
         IBlockState iblockstate2 = this.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.SOUTH));
         IBlockState iblockstate3 = this.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.EAST));
         IBlockState iblockstate4 = this.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.WEST));
         IBlockState iblockstate5 = this.getBiomeSpecificBlockState(Blocks.OAK_PLANKS.getDefaultState());
         IBlockState iblockstate6 = this.getBiomeSpecificBlockState(Blocks.OAK_LOG.getDefaultState());
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 7, 4, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 6, 8, 4, 10, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 0, 5, 8, 0, 10, iblockstate5, iblockstate5, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 1, 7, 0, 4, iblockstate5, iblockstate5, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 0, 3, 5, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 0, 0, 8, 3, 10, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 0, 7, 2, 0, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 5, 2, 1, 5, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 0, 6, 2, 3, 10, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 0, 10, 7, 3, 10, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 0, 7, 3, 0, iblockstate5, iblockstate5, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 5, 2, 3, 5, iblockstate5, iblockstate5, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 1, 8, 4, 1, iblockstate5, iblockstate5, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 4, 3, 4, 4, iblockstate5, iblockstate5, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 2, 8, 5, 3, iblockstate5, iblockstate5, false);
         this.setBlockState(worldIn, iblockstate5, 0, 4, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate5, 0, 4, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate5, 8, 4, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate5, 8, 4, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate5, 8, 4, 4, structureBoundingBoxIn);
         IBlockState iblockstate7 = iblockstate1;
         IBlockState iblockstate8 = iblockstate2;
         IBlockState iblockstate9 = iblockstate4;
         IBlockState iblockstate10 = iblockstate3;

         for(int i = -1; i <= 2; ++i) {
            for(int j = 0; j <= 8; ++j) {
               this.setBlockState(worldIn, iblockstate7, j, 4 + i, i, structureBoundingBoxIn);
               if ((i > -1 || j <= 1) && (i > 0 || j <= 3) && (i > 1 || j <= 4 || j >= 6)) {
                  this.setBlockState(worldIn, iblockstate8, j, 4 + i, 5 - i, structureBoundingBoxIn);
               }
            }
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 4, 5, 3, 4, 10, iblockstate5, iblockstate5, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 4, 2, 7, 4, 10, iblockstate5, iblockstate5, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 5, 4, 4, 5, 10, iblockstate5, iblockstate5, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 5, 4, 6, 5, 10, iblockstate5, iblockstate5, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 6, 3, 5, 6, 10, iblockstate5, iblockstate5, false);

         for(int k = 4; k >= 1; --k) {
            this.setBlockState(worldIn, iblockstate5, k, 2 + k, 7 - k, structureBoundingBoxIn);

            for(int k1 = 8 - k; k1 <= 10; ++k1) {
               this.setBlockState(worldIn, iblockstate10, k, 2 + k, k1, structureBoundingBoxIn);
            }
         }

         this.setBlockState(worldIn, iblockstate5, 6, 6, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate5, 7, 5, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate4, 6, 6, 4, structureBoundingBoxIn);

         for(int l = 6; l <= 8; ++l) {
            for(int l1 = 5; l1 <= 10; ++l1) {
               this.setBlockState(worldIn, iblockstate9, l, 12 - l, l1, structureBoundingBoxIn);
            }
         }

         this.setBlockState(worldIn, iblockstate6, 0, 2, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate6, 0, 2, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 0, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 0, 2, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate6, 4, 2, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 5, 2, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate6, 6, 2, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate6, 8, 2, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 8, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 8, 2, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate6, 8, 2, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate5, 8, 2, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate6, 8, 2, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 8, 2, 7, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 8, 2, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate6, 8, 2, 9, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate6, 2, 2, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 2, 2, 7, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 2, 2, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate6, 2, 2, 9, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate6, 4, 4, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 5, 4, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate6, 6, 4, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate5, 5, 5, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 2, 1, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 2, 2, 0, structureBoundingBoxIn);
         this.placeTorch(worldIn, EnumFacing.NORTH, 2, 3, 1, structureBoundingBoxIn);
         this.createVillageDoor(worldIn, structureBoundingBoxIn, randomIn, 2, 1, 0, EnumFacing.NORTH);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, -1, 3, 2, -1, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         if (this.getBlockStateFromPos(worldIn, 2, 0, -1, structureBoundingBoxIn).isAir() && !this.getBlockStateFromPos(worldIn, 2, -1, -1, structureBoundingBoxIn).isAir()) {
            this.setBlockState(worldIn, iblockstate7, 2, 0, -1, structureBoundingBoxIn);
            if (this.getBlockStateFromPos(worldIn, 2, -1, -1, structureBoundingBoxIn).getBlock() == Blocks.GRASS_PATH) {
               this.setBlockState(worldIn, Blocks.GRASS_BLOCK.getDefaultState(), 2, -1, -1, structureBoundingBoxIn);
            }
         }

         for(int i1 = 0; i1 < 5; ++i1) {
            for(int i2 = 0; i2 < 9; ++i2) {
               this.clearCurrentPositionBlocksUpwards(worldIn, i2, 7, i1, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, iblockstate, i2, -1, i1, structureBoundingBoxIn);
            }
         }

         for(int j1 = 5; j1 < 11; ++j1) {
            for(int j2 = 2; j2 < 9; ++j2) {
               this.clearCurrentPositionBlocksUpwards(worldIn, j2, 7, j1, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, iblockstate, j2, -1, j1, structureBoundingBoxIn);
            }
         }

         this.spawnVillagers(worldIn, structureBoundingBoxIn, 4, 1, 2, 2);
         return true;
      }
   }

   public static class House4Garden extends VillagePieces.Village {
      private boolean isRoofAccessible;

      public House4Garden() {
      }

      public House4Garden(VillagePieces.Start start, int p_i45566_2_, Random rand, MutableBoundingBox p_i45566_4_, EnumFacing facing) {
         super(start, p_i45566_2_);
         this.setCoordBaseMode(facing);
         this.boundingBox = p_i45566_4_;
         this.isRoofAccessible = rand.nextBoolean();
      }

      /**
       * (abstract) Helper method to write subclass data to NBT
       */
      protected void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setBoolean("Terrace", this.isRoofAccessible);
      }

      /**
       * (abstract) Helper method to read subclass data from NBT
       */
      protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager p_143011_2_) {
         super.readStructureFromNBT(tagCompound, p_143011_2_);
         this.isRoofAccessible = tagCompound.getBoolean("Terrace");
      }

      public static VillagePieces.House4Garden createPiece(VillagePieces.Start start, List<StructurePiece> p_175858_1_, Random rand, int p_175858_3_, int p_175858_4_, int p_175858_5_, EnumFacing facing, int p_175858_7_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_175858_3_, p_175858_4_, p_175858_5_, 0, 0, 0, 5, 6, 5, facing);
         return StructurePiece.findIntersecting(p_175858_1_, mutableboundingbox) != null ? null : new VillagePieces.House4Garden(start, p_175858_7_, rand, mutableboundingbox, facing);
      }

      /**
       * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
       * the end, it adds Fences...
       */
      public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_74875_4_) {
         if (this.averageGroundLvl < 0) {
            this.averageGroundLvl = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);
            if (this.averageGroundLvl < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 6 - 1, 0);
         }

         IBlockState iblockstate = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE.getDefaultState());
         IBlockState iblockstate1 = this.getBiomeSpecificBlockState(Blocks.OAK_PLANKS.getDefaultState());
         IBlockState iblockstate2 = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.NORTH));
         IBlockState iblockstate3 = this.getBiomeSpecificBlockState(Blocks.OAK_LOG.getDefaultState());
         IBlockState iblockstate4 = this.getBiomeSpecificBlockState(Blocks.OAK_FENCE.getDefaultState());
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 0, 4, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 0, 4, 4, 4, iblockstate3, iblockstate3, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 1, 3, 4, 3, iblockstate1, iblockstate1, false);
         this.setBlockState(worldIn, iblockstate, 0, 1, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate, 0, 2, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate, 0, 3, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate, 4, 1, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate, 4, 2, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate, 4, 3, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate, 0, 1, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate, 0, 2, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate, 0, 3, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate, 4, 1, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate, 4, 2, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate, 4, 3, 4, structureBoundingBoxIn);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 1, 0, 3, 3, iblockstate1, iblockstate1, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 1, 4, 3, 3, iblockstate1, iblockstate1, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 4, 3, 3, 4, iblockstate1, iblockstate1, false);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 0, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.EAST, Boolean.valueOf(true)).with(BlockGlassPane.WEST, Boolean.valueOf(true)), 2, 2, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 4, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate1, 1, 1, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate1, 1, 2, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate1, 1, 3, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate1, 2, 3, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate1, 3, 3, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate1, 3, 2, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate1, 3, 1, 0, structureBoundingBoxIn);
         if (this.getBlockStateFromPos(worldIn, 2, 0, -1, structureBoundingBoxIn).isAir() && !this.getBlockStateFromPos(worldIn, 2, -1, -1, structureBoundingBoxIn).isAir()) {
            this.setBlockState(worldIn, iblockstate2, 2, 0, -1, structureBoundingBoxIn);
            if (this.getBlockStateFromPos(worldIn, 2, -1, -1, structureBoundingBoxIn).getBlock() == Blocks.GRASS_PATH) {
               this.setBlockState(worldIn, Blocks.GRASS_BLOCK.getDefaultState(), 2, -1, -1, structureBoundingBoxIn);
            }
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 3, 3, 3, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         if (this.isRoofAccessible) {
            int i = 0;
            int j = 4;

            for(int k = 0; k <= 4; ++k) {
               for(int l = 0; l <= 4; ++l) {
                  boolean flag = k == 0 || k == 4;
                  boolean flag1 = l == 0 || l == 4;
                  if (flag || flag1) {
                     boolean flag2 = k == 0 || k == 4;
                     boolean flag3 = l == 0 || l == 4;
                     IBlockState iblockstate5 = iblockstate4.with(BlockFence.SOUTH, Boolean.valueOf(flag2 && l != 0)).with(BlockFence.NORTH, Boolean.valueOf(flag2 && l != 4)).with(BlockFence.WEST, Boolean.valueOf(flag3 && k != 0)).with(BlockFence.EAST, Boolean.valueOf(flag3 && k != 4));
                     this.setBlockState(worldIn, iblockstate5, k, 5, l, structureBoundingBoxIn);
                  }
               }
            }
         }

         if (this.isRoofAccessible) {
            IBlockState iblockstate6 = Blocks.LADDER.getDefaultState().with(BlockLadder.FACING, EnumFacing.SOUTH);
            this.setBlockState(worldIn, iblockstate6, 3, 1, 3, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate6, 3, 2, 3, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate6, 3, 3, 3, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate6, 3, 4, 3, structureBoundingBoxIn);
         }

         this.placeTorch(worldIn, EnumFacing.NORTH, 2, 3, 1, structureBoundingBoxIn);

         for(int i1 = 0; i1 < 5; ++i1) {
            for(int j1 = 0; j1 < 5; ++j1) {
               this.clearCurrentPositionBlocksUpwards(worldIn, j1, 6, i1, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, iblockstate, j1, -1, i1, structureBoundingBoxIn);
            }
         }

         this.spawnVillagers(worldIn, structureBoundingBoxIn, 1, 1, 2, 1);
         return true;
      }
   }

   public static class Path extends VillagePieces.Road {
      private int length;

      public Path() {
      }

      public Path(VillagePieces.Start start, int p_i45562_2_, Random rand, MutableBoundingBox p_i45562_4_, EnumFacing facing) {
         super(start, p_i45562_2_);
         this.setCoordBaseMode(facing);
         this.boundingBox = p_i45562_4_;
         this.length = Math.max(p_i45562_4_.getXSize(), p_i45562_4_.getZSize());
      }

      /**
       * (abstract) Helper method to write subclass data to NBT
       */
      protected void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setInt("Length", this.length);
      }

      /**
       * (abstract) Helper method to read subclass data from NBT
       */
      protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager p_143011_2_) {
         super.readStructureFromNBT(tagCompound, p_143011_2_);
         this.length = tagCompound.getInt("Length");
      }

      /**
       * Initiates construction of the Structure Component picked, at the current Location of StructGen
       */
      public void buildComponent(StructurePiece componentIn, List<StructurePiece> listIn, Random rand) {
         boolean flag = false;

         for(int i = rand.nextInt(5); i < this.length - 8; i += 2 + rand.nextInt(5)) {
            StructurePiece structurepiece = this.getNextComponentNN((VillagePieces.Start)componentIn, listIn, rand, 0, i);
            if (structurepiece != null) {
               i += Math.max(structurepiece.boundingBox.getXSize(), structurepiece.boundingBox.getZSize());
               flag = true;
            }
         }

         for(int j = rand.nextInt(5); j < this.length - 8; j += 2 + rand.nextInt(5)) {
            StructurePiece structurepiece1 = this.getNextComponentPP((VillagePieces.Start)componentIn, listIn, rand, 0, j);
            if (structurepiece1 != null) {
               j += Math.max(structurepiece1.boundingBox.getXSize(), structurepiece1.boundingBox.getZSize());
               flag = true;
            }
         }

         EnumFacing enumfacing = this.getCoordBaseMode();
         if (flag && rand.nextInt(3) > 0 && enumfacing != null) {
            switch(enumfacing) {
            case NORTH:
            default:
               VillagePieces.generateAndAddRoadPiece((VillagePieces.Start)componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ, EnumFacing.WEST, this.getComponentType());
               break;
            case SOUTH:
               VillagePieces.generateAndAddRoadPiece((VillagePieces.Start)componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.maxZ - 2, EnumFacing.WEST, this.getComponentType());
               break;
            case WEST:
               VillagePieces.generateAndAddRoadPiece((VillagePieces.Start)componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, this.getComponentType());
               break;
            case EAST:
               VillagePieces.generateAndAddRoadPiece((VillagePieces.Start)componentIn, listIn, rand, this.boundingBox.maxX - 2, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, this.getComponentType());
            }
         }

         if (flag && rand.nextInt(3) > 0 && enumfacing != null) {
            switch(enumfacing) {
            case NORTH:
            default:
               VillagePieces.generateAndAddRoadPiece((VillagePieces.Start)componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ, EnumFacing.EAST, this.getComponentType());
               break;
            case SOUTH:
               VillagePieces.generateAndAddRoadPiece((VillagePieces.Start)componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.maxZ - 2, EnumFacing.EAST, this.getComponentType());
               break;
            case WEST:
               VillagePieces.generateAndAddRoadPiece((VillagePieces.Start)componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, this.getComponentType());
               break;
            case EAST:
               VillagePieces.generateAndAddRoadPiece((VillagePieces.Start)componentIn, listIn, rand, this.boundingBox.maxX - 2, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, this.getComponentType());
            }
         }

      }

      public static MutableBoundingBox findPieceBox(VillagePieces.Start start, List<StructurePiece> p_175848_1_, Random rand, int p_175848_3_, int p_175848_4_, int p_175848_5_, EnumFacing facing) {
         for(int i = 7 * MathHelper.nextInt(rand, 3, 5); i >= 7; i -= 7) {
            MutableBoundingBox mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_175848_3_, p_175848_4_, p_175848_5_, 0, 0, 0, 3, 3, i, facing);
            if (StructurePiece.findIntersecting(p_175848_1_, mutableboundingbox) == null) {
               return mutableboundingbox;
            }
         }

         return null;
      }

      /**
       * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
       * the end, it adds Fences...
       */
      public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_74875_4_) {
         IBlockState iblockstate = this.getBiomeSpecificBlockState(Blocks.GRASS_PATH.getDefaultState());
         IBlockState iblockstate1 = this.getBiomeSpecificBlockState(Blocks.OAK_PLANKS.getDefaultState());
         IBlockState iblockstate2 = this.getBiomeSpecificBlockState(Blocks.GRAVEL.getDefaultState());
         IBlockState iblockstate3 = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE.getDefaultState());
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
         this.boundingBox.minY = 1000;
         this.boundingBox.maxY = 0;

         for(int i = this.boundingBox.minX; i <= this.boundingBox.maxX; ++i) {
            for(int j = this.boundingBox.minZ; j <= this.boundingBox.maxZ; ++j) {
               blockpos$mutableblockpos.setPos(i, 64, j);
               if (structureBoundingBoxIn.isVecInside(blockpos$mutableblockpos)) {
                  int k = worldIn.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockpos$mutableblockpos.getX(), blockpos$mutableblockpos.getZ());
                  blockpos$mutableblockpos.setPos(blockpos$mutableblockpos.getX(), k, blockpos$mutableblockpos.getZ()).move(EnumFacing.DOWN);
                  if (blockpos$mutableblockpos.getY() < worldIn.getSeaLevel()) {
                     blockpos$mutableblockpos.setY(worldIn.getSeaLevel() - 1);
                  }

                  while(blockpos$mutableblockpos.getY() >= worldIn.getSeaLevel() - 1) {
                     IBlockState iblockstate4 = worldIn.getBlockState(blockpos$mutableblockpos);
                     Block block = iblockstate4.getBlock();
                     if (block == Blocks.GRASS_BLOCK && worldIn.isAirBlock(blockpos$mutableblockpos.up())) {
                        worldIn.setBlockState(blockpos$mutableblockpos, iblockstate, 2);
                        break;
                     }

                     if (iblockstate4.getMaterial().isLiquid()) {
                        worldIn.setBlockState(new BlockPos(blockpos$mutableblockpos), iblockstate1, 2);
                        break;
                     }

                     if (block == Blocks.SAND || block == Blocks.RED_SAND || block == Blocks.SANDSTONE || block == Blocks.CHISELED_SANDSTONE || block == Blocks.CUT_SANDSTONE || block == Blocks.RED_SANDSTONE || block == Blocks.CHISELED_SANDSTONE || block == Blocks.CUT_SANDSTONE) {
                        worldIn.setBlockState(blockpos$mutableblockpos, iblockstate2, 2);
                        worldIn.setBlockState(blockpos$mutableblockpos.down(), iblockstate3, 2);
                        break;
                     }

                     blockpos$mutableblockpos.move(EnumFacing.DOWN);
                  }

                  this.boundingBox.minY = Math.min(this.boundingBox.minY, blockpos$mutableblockpos.getY());
                  this.boundingBox.maxY = Math.max(this.boundingBox.maxY, blockpos$mutableblockpos.getY());
               }
            }
         }

         return true;
      }
   }

   public static class PieceWeight {
      /** The Class object for the represantation of this village piece. */
      public Class<? extends VillagePieces.Village> villagePieceClass;
      public final int villagePieceWeight;
      public int villagePiecesSpawned;
      public int villagePiecesLimit;

      public PieceWeight(Class<? extends VillagePieces.Village> p_i2098_1_, int p_i2098_2_, int p_i2098_3_) {
         this.villagePieceClass = p_i2098_1_;
         this.villagePieceWeight = p_i2098_2_;
         this.villagePiecesLimit = p_i2098_3_;
      }

      public boolean canSpawnMoreVillagePiecesOfType(int componentType) {
         return this.villagePiecesLimit == 0 || this.villagePiecesSpawned < this.villagePiecesLimit;
      }

      public boolean canSpawnMoreVillagePieces() {
         return this.villagePiecesLimit == 0 || this.villagePiecesSpawned < this.villagePiecesLimit;
      }
   }

   public abstract static class Road extends VillagePieces.Village {
      public Road() {
      }

      protected Road(VillagePieces.Start start, int type) {
         super(start, type);
      }
   }

   public static class Start extends VillagePieces.Well {
      /** World terrain type, 0 for normal, 1 for flap map */
      public int terrainType;
      public VillagePieces.PieceWeight lastPlaced;
      /**
       * Contains List of all spawnable Structure Piece Weights. If no more Pieces of a type can be spawned, they are
       * removed from this list
       */
      public List<VillagePieces.PieceWeight> structureVillageWeightedPieceList;
      public List<StructurePiece> pendingHouses = Lists.newArrayList();
      public List<StructurePiece> pendingRoads = Lists.newArrayList();
      public @javax.annotation.Nullable net.minecraft.world.biome.Biome biome;

      public Start() {
      }

      public Start(int p_i48769_1_, Random p_i48769_2_, int p_i48769_3_, int p_i48769_4_, List<VillagePieces.PieceWeight> p_i48769_5_, VillageConfig p_i48769_6_) {
         this(p_i48769_1_, p_i48769_2_, p_i48769_3_, p_i48769_4_, p_i48769_5_, p_i48769_6_, null);
      }
      
      public Start(int p_i48769_1_, Random p_i48769_2_, int p_i48769_3_, int p_i48769_4_, List<VillagePieces.PieceWeight> p_i48769_5_, VillageConfig p_i48769_6_, net.minecraft.world.biome.Biome biome) {
         super((VillagePieces.Start)null, 0, p_i48769_2_, p_i48769_3_, p_i48769_4_);
         this.structureVillageWeightedPieceList = p_i48769_5_;
         this.terrainType = p_i48769_6_.field_202461_a;
         this.structureType = p_i48769_6_.type;
         this.func_202579_a(this.structureType);
         this.isZombieInfested = p_i48769_2_.nextInt(50) == 0;
         this.biome = biome;
         this.startPiece = this;
      }
   }

   public static class Torch extends VillagePieces.Village {
      public Torch() {
      }

      public Torch(VillagePieces.Start start, int p_i45568_2_, Random rand, MutableBoundingBox p_i45568_4_, EnumFacing facing) {
         super(start, p_i45568_2_);
         this.setCoordBaseMode(facing);
         this.boundingBox = p_i45568_4_;
      }

      public static MutableBoundingBox findPieceBox(VillagePieces.Start start, List<StructurePiece> p_175856_1_, Random rand, int p_175856_3_, int p_175856_4_, int p_175856_5_, EnumFacing facing) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_175856_3_, p_175856_4_, p_175856_5_, 0, 0, 0, 3, 4, 2, facing);
         return StructurePiece.findIntersecting(p_175856_1_, mutableboundingbox) != null ? null : mutableboundingbox;
      }

      /**
       * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
       * the end, it adds Fences...
       */
      public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_74875_4_) {
         if (this.averageGroundLvl < 0) {
            this.averageGroundLvl = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);
            if (this.averageGroundLvl < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 4 - 1, 0);
         }

         IBlockState iblockstate = this.getBiomeSpecificBlockState(Blocks.OAK_FENCE.getDefaultState());
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 2, 3, 1, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.setBlockState(worldIn, iblockstate, 1, 0, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate, 1, 1, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate, 1, 2, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.BLACK_WOOL.getDefaultState(), 1, 3, 0, structureBoundingBoxIn);
         this.placeTorch(worldIn, EnumFacing.EAST, 2, 3, 0, structureBoundingBoxIn);
         this.placeTorch(worldIn, EnumFacing.NORTH, 1, 3, 1, structureBoundingBoxIn);
         this.placeTorch(worldIn, EnumFacing.WEST, 0, 3, 0, structureBoundingBoxIn);
         this.placeTorch(worldIn, EnumFacing.SOUTH, 1, 3, -1, structureBoundingBoxIn);
         return true;
      }
   }

   public static enum Type {
      OAK(0),
      SANDSTONE(1),
      ACACIA(2),
      SPRUCE(3);

      private final int field_202605_e;

      private Type(int p_i48768_3_) {
         this.field_202605_e = p_i48768_3_;
      }

      public int func_202604_a() {
         return this.field_202605_e;
      }

      public static VillagePieces.Type func_202603_a(int p_202603_0_) {
         VillagePieces.Type[] avillagepieces$type = values();
         return p_202603_0_ >= 0 && p_202603_0_ < avillagepieces$type.length ? avillagepieces$type[p_202603_0_] : OAK;
      }
   }

   public abstract static class Village extends StructurePiece {
      protected int averageGroundLvl = -1;
      /** The number of villagers that have been spawned in this component. */
      private int villagersSpawned;
      protected VillagePieces.Type structureType;
      protected boolean isZombieInfested;
      protected VillagePieces.Start startPiece;

      public Village() {
      }

      protected Village(VillagePieces.Start start, int type) {
         super(type);
         if (start != null) {
            this.structureType = start.structureType;
            this.isZombieInfested = start.isZombieInfested;
            this.startPiece = start;
         }

      }

      /**
       * (abstract) Helper method to write subclass data to NBT
       */
      protected void writeStructureToNBT(NBTTagCompound tagCompound) {
         tagCompound.setInt("HPos", this.averageGroundLvl);
         tagCompound.setInt("VCount", this.villagersSpawned);
         tagCompound.setByte("Type", (byte)this.structureType.func_202604_a());
         tagCompound.setBoolean("Zombie", this.isZombieInfested);
      }

      /**
       * (abstract) Helper method to read subclass data from NBT
       */
      protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager p_143011_2_) {
         this.averageGroundLvl = tagCompound.getInt("HPos");
         this.villagersSpawned = tagCompound.getInt("VCount");
         this.structureType = VillagePieces.Type.func_202603_a(tagCompound.getByte("Type"));
         if (tagCompound.getBoolean("Desert")) {
            this.structureType = VillagePieces.Type.SANDSTONE;
         }

         this.isZombieInfested = tagCompound.getBoolean("Zombie");
      }

      /**
       * Gets the next village component, with the bounding box shifted -1 in the X and Z direction.
       */
      @Nullable
      protected StructurePiece getNextComponentNN(VillagePieces.Start start, List<StructurePiece> structureComponents, Random rand, int p_74891_4_, int p_74891_5_) {
         EnumFacing enumfacing = this.getCoordBaseMode();
         if (enumfacing != null) {
            switch(enumfacing) {
            case NORTH:
            default:
               return VillagePieces.generateAndAddComponent(start, structureComponents, rand, this.boundingBox.minX - 1, this.boundingBox.minY + p_74891_4_, this.boundingBox.minZ + p_74891_5_, EnumFacing.WEST, this.getComponentType());
            case SOUTH:
               return VillagePieces.generateAndAddComponent(start, structureComponents, rand, this.boundingBox.minX - 1, this.boundingBox.minY + p_74891_4_, this.boundingBox.minZ + p_74891_5_, EnumFacing.WEST, this.getComponentType());
            case WEST:
               return VillagePieces.generateAndAddComponent(start, structureComponents, rand, this.boundingBox.minX + p_74891_5_, this.boundingBox.minY + p_74891_4_, this.boundingBox.minZ - 1, EnumFacing.NORTH, this.getComponentType());
            case EAST:
               return VillagePieces.generateAndAddComponent(start, structureComponents, rand, this.boundingBox.minX + p_74891_5_, this.boundingBox.minY + p_74891_4_, this.boundingBox.minZ - 1, EnumFacing.NORTH, this.getComponentType());
            }
         } else {
            return null;
         }
      }

      /**
       * Gets the next village component, with the bounding box shifted +1 in the X and Z direction.
       */
      @Nullable
      protected StructurePiece getNextComponentPP(VillagePieces.Start start, List<StructurePiece> structureComponents, Random rand, int p_74894_4_, int p_74894_5_) {
         EnumFacing enumfacing = this.getCoordBaseMode();
         if (enumfacing != null) {
            switch(enumfacing) {
            case NORTH:
            default:
               return VillagePieces.generateAndAddComponent(start, structureComponents, rand, this.boundingBox.maxX + 1, this.boundingBox.minY + p_74894_4_, this.boundingBox.minZ + p_74894_5_, EnumFacing.EAST, this.getComponentType());
            case SOUTH:
               return VillagePieces.generateAndAddComponent(start, structureComponents, rand, this.boundingBox.maxX + 1, this.boundingBox.minY + p_74894_4_, this.boundingBox.minZ + p_74894_5_, EnumFacing.EAST, this.getComponentType());
            case WEST:
               return VillagePieces.generateAndAddComponent(start, structureComponents, rand, this.boundingBox.minX + p_74894_5_, this.boundingBox.minY + p_74894_4_, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, this.getComponentType());
            case EAST:
               return VillagePieces.generateAndAddComponent(start, structureComponents, rand, this.boundingBox.minX + p_74894_5_, this.boundingBox.minY + p_74894_4_, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, this.getComponentType());
            }
         } else {
            return null;
         }
      }

      /**
       * Discover the y coordinate that will serve as the ground level of the supplied BoundingBox. (A median of all the
       * levels in the BB's horizontal rectangle).
       */
      protected int getAverageGroundLevel(IWorld worldIn, MutableBoundingBox structurebb) {
         int i = 0;
         int j = 0;
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

         for(int k = this.boundingBox.minZ; k <= this.boundingBox.maxZ; ++k) {
            for(int l = this.boundingBox.minX; l <= this.boundingBox.maxX; ++l) {
               blockpos$mutableblockpos.setPos(l, 64, k);
               if (structurebb.isVecInside(blockpos$mutableblockpos)) {
                  i += worldIn.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockpos$mutableblockpos).getY();
                  ++j;
               }
            }
         }

         if (j == 0) {
            return -1;
         } else {
            return i / j;
         }
      }

      protected static boolean canVillageGoDeeper(MutableBoundingBox structurebb) {
         return structurebb != null && structurebb.minY > 10;
      }

      /**
       * Spawns a number of villagers in this component. Parameters: world, component bounding box, x offset, y offset,
       * z offset, number of villagers
       */
      protected void spawnVillagers(IWorld worldIn, MutableBoundingBox structurebb, int x, int y, int z, int count) {
         if (this.villagersSpawned < count) {
            for(int i = this.villagersSpawned; i < count; ++i) {
               int j = this.getXWithOffset(x + i, z);
               int k = this.getYWithOffset(y);
               int l = this.getZWithOffset(x + i, z);
               if (!structurebb.isVecInside(new BlockPos(j, k, l))) {
                  break;
               }

               ++this.villagersSpawned;
               if (this.isZombieInfested) {
                  EntityZombieVillager entityzombievillager = new EntityZombieVillager(worldIn.getWorld());
                  entityzombievillager.setLocationAndAngles((double)j + 0.5D, (double)k, (double)l + 0.5D, 0.0F, 0.0F);
                  entityzombievillager.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(entityzombievillager)), (IEntityLivingData)null, (NBTTagCompound)null);
                  entityzombievillager.enablePersistence();
                  worldIn.spawnEntity(entityzombievillager);
               } else {
                  EntityVillager entityvillager = new EntityVillager(worldIn.getWorld());
                  entityvillager.setLocationAndAngles((double)j + 0.5D, (double)k, (double)l + 0.5D, 0.0F, 0.0F);
                  entityvillager.setProfession(this.chooseForgeProfession(i, entityvillager.getProfessionForge()));
                  entityvillager.finalizeMobSpawn(worldIn.getDifficultyForLocation(new BlockPos(entityvillager)), (IEntityLivingData)null, (NBTTagCompound)null, false);
                  worldIn.spawnEntity(entityvillager);
               }
            }

         }
      }

      @Deprecated // Use Forge version below
      protected int chooseProfession(int villagersSpawnedIn, int currentVillagerProfession) {
         return currentVillagerProfession;
      }
      
      protected net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession chooseForgeProfession(int count, net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession prof) {
         return net.minecraftforge.fml.common.registry.VillagerRegistry.getById(chooseProfession(count, net.minecraftforge.fml.common.registry.VillagerRegistry.getId(prof)));
      }

      protected IBlockState getBiomeSpecificBlockState(IBlockState blockstateIn) {
         net.minecraftforge.event.terraingen.BiomeEvent.GetVillageBlockID event = new net.minecraftforge.event.terraingen.BiomeEvent.GetVillageBlockID(startPiece == null ? null : startPiece.biome, blockstateIn);
         net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
         if (event.getResult() == net.minecraftforge.eventbus.api.Event.Result.DENY) return event.getReplacement();
         Block block = blockstateIn.getBlock();
         if (this.structureType == VillagePieces.Type.SANDSTONE) {
            if (block.isIn(BlockTags.LOGS) || block == Blocks.COBBLESTONE) {
               return Blocks.SANDSTONE.getDefaultState();
            }

            if (block.isIn(BlockTags.PLANKS)) {
               return Blocks.CUT_SANDSTONE.getDefaultState();
            }

            if (block == Blocks.OAK_STAIRS) {
               return Blocks.SANDSTONE_STAIRS.getDefaultState().with(BlockStairs.FACING, blockstateIn.get(BlockStairs.FACING));
            }

            if (block == Blocks.COBBLESTONE_STAIRS) {
               return Blocks.SANDSTONE_STAIRS.getDefaultState().with(BlockStairs.FACING, blockstateIn.get(BlockStairs.FACING));
            }

            if (block == Blocks.GRAVEL) {
               return Blocks.SANDSTONE.getDefaultState();
            }

            if (block == Blocks.OAK_PRESSURE_PLATE) {
               return Blocks.BIRCH_PRESSURE_PLATE.getDefaultState();
            }
         } else if (this.structureType == VillagePieces.Type.SPRUCE) {
            if (block.isIn(BlockTags.LOGS)) {
               return Blocks.SPRUCE_LOG.getDefaultState().with(BlockLog.AXIS, blockstateIn.get(BlockLog.AXIS));
            }

            if (block.isIn(BlockTags.PLANKS)) {
               return Blocks.SPRUCE_PLANKS.getDefaultState();
            }

            if (block == Blocks.OAK_STAIRS) {
               return Blocks.SPRUCE_STAIRS.getDefaultState().with(BlockStairs.FACING, blockstateIn.get(BlockStairs.FACING));
            }

            if (block == Blocks.OAK_FENCE) {
               return Blocks.SPRUCE_FENCE.getDefaultState();
            }

            if (block == Blocks.OAK_PRESSURE_PLATE) {
               return Blocks.SPRUCE_PRESSURE_PLATE.getDefaultState();
            }
         } else if (this.structureType == VillagePieces.Type.ACACIA) {
            if (block.isIn(BlockTags.LOGS)) {
               return Blocks.ACACIA_LOG.getDefaultState().with(BlockLog.AXIS, blockstateIn.get(BlockLog.AXIS));
            }

            if (block.isIn(BlockTags.PLANKS)) {
               return Blocks.ACACIA_PLANKS.getDefaultState();
            }

            if (block == Blocks.OAK_STAIRS) {
               return Blocks.ACACIA_STAIRS.getDefaultState().with(BlockStairs.FACING, blockstateIn.get(BlockStairs.FACING));
            }

            if (block == Blocks.COBBLESTONE) {
               return Blocks.ACACIA_LOG.getDefaultState().with(BlockLog.AXIS, EnumFacing.Axis.Y);
            }

            if (block == Blocks.OAK_FENCE) {
               return Blocks.ACACIA_FENCE.getDefaultState();
            }

            if (block == Blocks.OAK_PRESSURE_PLATE) {
               return Blocks.ACACIA_PRESSURE_PLATE.getDefaultState();
            }
         }

         return blockstateIn;
      }

      protected BlockDoor biomeDoor() {
         if (this.structureType == VillagePieces.Type.ACACIA) {
            return (BlockDoor)Blocks.ACACIA_DOOR;
         } else {
            return this.structureType == VillagePieces.Type.SPRUCE ? (BlockDoor)Blocks.SPRUCE_DOOR : (BlockDoor)Blocks.OAK_DOOR;
         }
      }

      protected void createVillageDoor(IWorld p_189927_1_, MutableBoundingBox p_189927_2_, Random p_189927_3_, int p_189927_4_, int p_189927_5_, int p_189927_6_, EnumFacing p_189927_7_) {
         if (!this.isZombieInfested) {
            this.generateDoor(p_189927_1_, p_189927_2_, p_189927_3_, p_189927_4_, p_189927_5_, p_189927_6_, EnumFacing.NORTH, this.biomeDoor());
         }

      }

      protected void placeTorch(IWorld p_189926_1_, EnumFacing p_189926_2_, int p_189926_3_, int p_189926_4_, int p_189926_5_, MutableBoundingBox p_189926_6_) {
         if (!this.isZombieInfested) {
            this.setBlockState(p_189926_1_, Blocks.WALL_TORCH.getDefaultState().with(BlockTorchWall.HORIZONTAL_FACING, p_189926_2_), p_189926_3_, p_189926_4_, p_189926_5_, p_189926_6_);
         }

      }

      /**
       * Replaces air and liquid from given position downwards. Stops when hitting anything else than air or liquid
       */
      protected void replaceAirAndLiquidDownwards(IWorld worldIn, IBlockState blockstateIn, int x, int y, int z, MutableBoundingBox boundingboxIn) {
         IBlockState iblockstate = this.getBiomeSpecificBlockState(blockstateIn);
         super.replaceAirAndLiquidDownwards(worldIn, iblockstate, x, y, z, boundingboxIn);
      }

      protected void func_202579_a(VillagePieces.Type p_202579_1_) {
         this.structureType = p_202579_1_;
      }
   }

   public static class Well extends VillagePieces.Village {
      public Well() {
      }

      public Well(VillagePieces.Start start, int type, Random rand, int x, int z) {
         super(start, type);
         this.setCoordBaseMode(EnumFacing.Plane.HORIZONTAL.random(rand));
         if (this.getCoordBaseMode().getAxis() == EnumFacing.Axis.Z) {
            this.boundingBox = new MutableBoundingBox(x, 64, z, x + 6 - 1, 78, z + 6 - 1);
         } else {
            this.boundingBox = new MutableBoundingBox(x, 64, z, x + 6 - 1, 78, z + 6 - 1);
         }

      }

      /**
       * Initiates construction of the Structure Component picked, at the current Location of StructGen
       */
      public void buildComponent(StructurePiece componentIn, List<StructurePiece> listIn, Random rand) {
         VillagePieces.generateAndAddRoadPiece((VillagePieces.Start)componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.maxY - 4, this.boundingBox.minZ + 1, EnumFacing.WEST, this.getComponentType());
         VillagePieces.generateAndAddRoadPiece((VillagePieces.Start)componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.maxY - 4, this.boundingBox.minZ + 1, EnumFacing.EAST, this.getComponentType());
         VillagePieces.generateAndAddRoadPiece((VillagePieces.Start)componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.maxY - 4, this.boundingBox.minZ - 1, EnumFacing.NORTH, this.getComponentType());
         VillagePieces.generateAndAddRoadPiece((VillagePieces.Start)componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.maxY - 4, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, this.getComponentType());
      }

      /**
       * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
       * the end, it adds Fences...
       */
      public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_74875_4_) {
         if (this.averageGroundLvl < 0) {
            this.averageGroundLvl = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);
            if (this.averageGroundLvl < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 3, 0);
         }

         IBlockState iblockstate = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE.getDefaultState());
         IBlockState iblockstate1 = this.getBiomeSpecificBlockState(Blocks.OAK_FENCE.getDefaultState());
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 1, 4, 12, 4, iblockstate, Blocks.WATER.getDefaultState(), false);
         this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 2, 12, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 3, 12, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 2, 12, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 3, 12, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate1, 1, 13, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate1, 1, 14, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate1, 4, 13, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate1, 4, 14, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate1, 1, 13, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate1, 1, 14, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate1, 4, 13, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate1, 4, 14, 4, structureBoundingBoxIn);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 15, 1, 4, 15, 4, iblockstate, iblockstate, false);

         for(int i = 0; i <= 5; ++i) {
            for(int j = 0; j <= 5; ++j) {
               if (j == 0 || j == 5 || i == 0 || i == 5) {
                  this.setBlockState(worldIn, iblockstate, j, 11, i, structureBoundingBoxIn);
                  this.clearCurrentPositionBlocksUpwards(worldIn, j, 12, i, structureBoundingBoxIn);
               }
            }
         }

         return true;
      }
   }

   public static class WoodHut extends VillagePieces.Village {
      private boolean isTallHouse;
      private int tablePosition;

      public WoodHut() {
      }

      public WoodHut(VillagePieces.Start start, int type, Random rand, MutableBoundingBox structurebb, EnumFacing facing) {
         super(start, type);
         this.setCoordBaseMode(facing);
         this.boundingBox = structurebb;
         this.isTallHouse = rand.nextBoolean();
         this.tablePosition = rand.nextInt(3);
      }

      /**
       * (abstract) Helper method to write subclass data to NBT
       */
      protected void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setInt("T", this.tablePosition);
         tagCompound.setBoolean("C", this.isTallHouse);
      }

      /**
       * (abstract) Helper method to read subclass data from NBT
       */
      protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager p_143011_2_) {
         super.readStructureFromNBT(tagCompound, p_143011_2_);
         this.tablePosition = tagCompound.getInt("T");
         this.isTallHouse = tagCompound.getBoolean("C");
      }

      public static VillagePieces.WoodHut createPiece(VillagePieces.Start start, List<StructurePiece> p_175853_1_, Random rand, int p_175853_3_, int p_175853_4_, int p_175853_5_, EnumFacing facing, int p_175853_7_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p_175853_3_, p_175853_4_, p_175853_5_, 0, 0, 0, 4, 6, 5, facing);
         return canVillageGoDeeper(mutableboundingbox) && StructurePiece.findIntersecting(p_175853_1_, mutableboundingbox) == null ? new VillagePieces.WoodHut(start, p_175853_7_, rand, mutableboundingbox, facing) : null;
      }

      /**
       * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
       * the end, it adds Fences...
       */
      public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_74875_4_) {
         if (this.averageGroundLvl < 0) {
            this.averageGroundLvl = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);
            if (this.averageGroundLvl < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 6 - 1, 0);
         }

         IBlockState iblockstate = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE.getDefaultState());
         IBlockState iblockstate1 = this.getBiomeSpecificBlockState(Blocks.OAK_PLANKS.getDefaultState());
         IBlockState iblockstate2 = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.NORTH));
         IBlockState iblockstate3 = this.getBiomeSpecificBlockState(Blocks.OAK_LOG.getDefaultState());
         IBlockState iblockstate4 = this.getBiomeSpecificBlockState(Blocks.OAK_FENCE.getDefaultState());
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 3, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 3, 0, 4, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 1, 2, 0, 3, Blocks.DIRT.getDefaultState(), Blocks.DIRT.getDefaultState(), false);
         if (this.isTallHouse) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 1, 2, 4, 3, iblockstate3, iblockstate3, false);
         } else {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 5, 1, 2, 5, 3, iblockstate3, iblockstate3, false);
         }

         this.setBlockState(worldIn, iblockstate3, 1, 4, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate3, 2, 4, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate3, 1, 4, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate3, 2, 4, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate3, 0, 4, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate3, 0, 4, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate3, 0, 4, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate3, 3, 4, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate3, 3, 4, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate3, 3, 4, 3, structureBoundingBoxIn);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 0, 3, 0, iblockstate3, iblockstate3, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 0, 3, 3, 0, iblockstate3, iblockstate3, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 4, 0, 3, 4, iblockstate3, iblockstate3, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 4, 3, 3, 4, iblockstate3, iblockstate3, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 1, 0, 3, 3, iblockstate1, iblockstate1, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 1, 3, 3, 3, iblockstate1, iblockstate1, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 0, 2, 3, 0, iblockstate1, iblockstate1, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 4, 2, 3, 4, iblockstate1, iblockstate1, false);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 0, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState().with(BlockGlassPane.SOUTH, Boolean.valueOf(true)).with(BlockGlassPane.NORTH, Boolean.valueOf(true)), 3, 2, 2, structureBoundingBoxIn);
         if (this.tablePosition > 0) {
            this.setBlockState(worldIn, iblockstate4.with(BlockFence.NORTH, Boolean.valueOf(true)).with(this.tablePosition == 1 ? BlockFence.WEST : BlockFence.EAST, Boolean.valueOf(true)), this.tablePosition, 1, 3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.OAK_PRESSURE_PLATE.getDefaultState(), this.tablePosition, 2, 3, structureBoundingBoxIn);
         }

         this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 1, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 2, 0, structureBoundingBoxIn);
         this.createVillageDoor(worldIn, structureBoundingBoxIn, randomIn, 1, 1, 0, EnumFacing.NORTH);
         if (this.getBlockStateFromPos(worldIn, 1, 0, -1, structureBoundingBoxIn).isAir() && !this.getBlockStateFromPos(worldIn, 1, -1, -1, structureBoundingBoxIn).isAir()) {
            this.setBlockState(worldIn, iblockstate2, 1, 0, -1, structureBoundingBoxIn);
            if (this.getBlockStateFromPos(worldIn, 1, -1, -1, structureBoundingBoxIn).getBlock() == Blocks.GRASS_PATH) {
               this.setBlockState(worldIn, Blocks.GRASS_BLOCK.getDefaultState(), 1, -1, -1, structureBoundingBoxIn);
            }
         }

         for(int i = 0; i < 5; ++i) {
            for(int j = 0; j < 4; ++j) {
               this.clearCurrentPositionBlocksUpwards(worldIn, j, 6, i, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, iblockstate, j, -1, i, structureBoundingBoxIn);
            }
         }

         this.spawnVillagers(worldIn, structureBoundingBoxIn, 1, 1, 2, 1);
         return true;
      }
   }
}