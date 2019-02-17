package net.minecraft.world.gen.feature.structure;

import java.util.Random;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class SwampHutPiece extends ScatteredStructurePiece {
   private boolean witch;

   public static void registerPieces() {
      StructureIO.registerStructureComponent(SwampHutPiece.class, "TeSH");
   }

   public SwampHutPiece() {
   }

   public SwampHutPiece(Random random, int x, int z) {
      super(random, x, 64, z, 7, 7, 9);
   }

   /**
    * (abstract) Helper method to write subclass data to NBT
    */
   protected void writeStructureToNBT(NBTTagCompound tagCompound) {
      super.writeStructureToNBT(tagCompound);
      tagCompound.setBoolean("Witch", this.witch);
   }

   /**
    * (abstract) Helper method to read subclass data from NBT
    */
   protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager p_143011_2_) {
      super.readStructureFromNBT(tagCompound, p_143011_2_);
      this.witch = tagCompound.getBoolean("Witch");
   }

   /**
    * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at the
    * end, it adds Fences...
    */
   public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_74875_4_) {
      if (!this.func_202580_a(worldIn, structureBoundingBoxIn, 0)) {
         return false;
      } else {
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 5, 1, 7, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 2, 5, 4, 7, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 0, 4, 1, 0, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 2, 2, 3, 3, 2, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 3, 1, 3, 6, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 2, 3, 5, 3, 6, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 2, 7, 4, 3, 7, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 2, 1, 3, 2, Blocks.OAK_LOG.getDefaultState(), Blocks.OAK_LOG.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 0, 2, 5, 3, 2, Blocks.OAK_LOG.getDefaultState(), Blocks.OAK_LOG.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 7, 1, 3, 7, Blocks.OAK_LOG.getDefaultState(), Blocks.OAK_LOG.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 0, 7, 5, 3, 7, Blocks.OAK_LOG.getDefaultState(), Blocks.OAK_LOG.getDefaultState(), false);
         this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 2, 3, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 3, 3, 7, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 3, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 5, 3, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 5, 3, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.POTTED_RED_MUSHROOM.getDefaultState(), 1, 3, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.CRAFTING_TABLE.getDefaultState(), 3, 2, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.CAULDRON.getDefaultState(), 4, 2, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 1, 2, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 5, 2, 1, structureBoundingBoxIn);
         IBlockState iblockstate = Blocks.SPRUCE_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.NORTH);
         IBlockState iblockstate1 = Blocks.SPRUCE_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.EAST);
         IBlockState iblockstate2 = Blocks.SPRUCE_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.WEST);
         IBlockState iblockstate3 = Blocks.SPRUCE_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.SOUTH);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 1, 6, 4, 1, iblockstate, iblockstate, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 2, 0, 4, 7, iblockstate1, iblockstate1, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 4, 2, 6, 4, 7, iblockstate2, iblockstate2, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 8, 6, 4, 8, iblockstate3, iblockstate3, false);
         this.setBlockState(worldIn, iblockstate.with(BlockStairs.SHAPE, StairsShape.OUTER_RIGHT), 0, 4, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate.with(BlockStairs.SHAPE, StairsShape.OUTER_LEFT), 6, 4, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate3.with(BlockStairs.SHAPE, StairsShape.OUTER_LEFT), 0, 4, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate3.with(BlockStairs.SHAPE, StairsShape.OUTER_RIGHT), 6, 4, 8, structureBoundingBoxIn);

         for(int i = 2; i <= 7; i += 5) {
            for(int j = 1; j <= 5; j += 4) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.OAK_LOG.getDefaultState(), j, -1, i, structureBoundingBoxIn);
            }
         }

         if (!this.witch) {
            int l = this.getXWithOffset(2, 5);
            int i1 = this.getYWithOffset(2);
            int k = this.getZWithOffset(2, 5);
            if (structureBoundingBoxIn.isVecInside(new BlockPos(l, i1, k))) {
               this.witch = true;
               EntityWitch entitywitch = new EntityWitch(worldIn.getWorld());
               entitywitch.enablePersistence();
               entitywitch.setLocationAndAngles((double)l + 0.5D, (double)i1, (double)k + 0.5D, 0.0F, 0.0F);
               entitywitch.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(l, i1, k)), (IEntityLivingData)null, (NBTTagCompound)null);
               worldIn.spawnEntity(entitywitch);
            }
         }

         return true;
      }
   }
}