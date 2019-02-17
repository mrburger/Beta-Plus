package net.minecraft.world.gen.feature.structure;

import java.util.Random;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTripWire;
import net.minecraft.block.BlockTripWireHook;
import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.RedstoneSide;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTableList;

public class JunglePyramidPiece extends ScatteredStructurePiece {
   private boolean field_202586_e;
   private boolean field_202587_f;
   private boolean field_202588_g;
   private boolean field_202589_h;
   private static final JunglePyramidPiece.MossStoneSelector MOSS_STONE_SELECTOR = new JunglePyramidPiece.MossStoneSelector();

   public static void registerJunglePyramidPieces() {
      StructureIO.registerStructureComponent(JunglePyramidPiece.class, "TeJP");
   }

   public JunglePyramidPiece() {
   }

   public JunglePyramidPiece(Random random, int x, int z) {
      super(random, x, 64, z, 12, 10, 15);
   }

   /**
    * (abstract) Helper method to write subclass data to NBT
    */
   protected void writeStructureToNBT(NBTTagCompound tagCompound) {
      super.writeStructureToNBT(tagCompound);
      tagCompound.setBoolean("placedMainChest", this.field_202586_e);
      tagCompound.setBoolean("placedHiddenChest", this.field_202587_f);
      tagCompound.setBoolean("placedTrap1", this.field_202588_g);
      tagCompound.setBoolean("placedTrap2", this.field_202589_h);
   }

   /**
    * (abstract) Helper method to read subclass data from NBT
    */
   protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager p_143011_2_) {
      super.readStructureFromNBT(tagCompound, p_143011_2_);
      this.field_202586_e = tagCompound.getBoolean("placedMainChest");
      this.field_202587_f = tagCompound.getBoolean("placedHiddenChest");
      this.field_202588_g = tagCompound.getBoolean("placedTrap1");
      this.field_202589_h = tagCompound.getBoolean("placedTrap2");
   }

   /**
    * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at the
    * end, it adds Fences...
    */
   public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_74875_4_) {
      if (!this.func_202580_a(worldIn, structureBoundingBoxIn, 0)) {
         return false;
      } else {
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, -4, 0, this.width - 1, 0, this.depth - 1, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, 1, 2, 9, 2, 2, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, 1, 12, 9, 2, 12, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, 1, 3, 2, 2, 11, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 9, 1, 3, 9, 2, 11, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, 3, 1, 10, 6, 1, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, 3, 13, 10, 6, 13, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, 3, 2, 1, 6, 12, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 10, 3, 2, 10, 6, 12, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, 3, 2, 9, 3, 12, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, 6, 2, 9, 6, 12, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 3, 7, 3, 8, 7, 11, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 8, 4, 7, 8, 10, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithAir(worldIn, structureBoundingBoxIn, 3, 1, 3, 8, 2, 11);
         this.fillWithAir(worldIn, structureBoundingBoxIn, 4, 3, 6, 7, 3, 9);
         this.fillWithAir(worldIn, structureBoundingBoxIn, 2, 4, 2, 9, 5, 12);
         this.fillWithAir(worldIn, structureBoundingBoxIn, 4, 6, 5, 7, 6, 9);
         this.fillWithAir(worldIn, structureBoundingBoxIn, 5, 7, 6, 6, 7, 8);
         this.fillWithAir(worldIn, structureBoundingBoxIn, 5, 1, 2, 6, 2, 2);
         this.fillWithAir(worldIn, structureBoundingBoxIn, 5, 2, 12, 6, 2, 12);
         this.fillWithAir(worldIn, structureBoundingBoxIn, 5, 5, 1, 6, 5, 1);
         this.fillWithAir(worldIn, structureBoundingBoxIn, 5, 5, 13, 6, 5, 13);
         this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 5, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 10, 5, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 5, 9, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 10, 5, 9, structureBoundingBoxIn);

         for(int i = 0; i <= 14; i += 14) {
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, 4, i, 2, 5, i, false, randomIn, MOSS_STONE_SELECTOR);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 4, i, 4, 5, i, false, randomIn, MOSS_STONE_SELECTOR);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 7, 4, i, 7, 5, i, false, randomIn, MOSS_STONE_SELECTOR);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 9, 4, i, 9, 5, i, false, randomIn, MOSS_STONE_SELECTOR);
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 5, 6, 0, 6, 6, 0, false, randomIn, MOSS_STONE_SELECTOR);

         for(int l = 0; l <= 11; l += 11) {
            for(int j = 2; j <= 12; j += 2) {
               this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, l, 4, j, l, 5, j, false, randomIn, MOSS_STONE_SELECTOR);
            }

            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, l, 6, 5, l, 6, 5, false, randomIn, MOSS_STONE_SELECTOR);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, l, 6, 9, l, 6, 9, false, randomIn, MOSS_STONE_SELECTOR);
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, 7, 2, 2, 9, 2, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 9, 7, 2, 9, 9, 2, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, 7, 12, 2, 9, 12, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 9, 7, 12, 9, 9, 12, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 9, 4, 4, 9, 4, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 7, 9, 4, 7, 9, 4, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 9, 10, 4, 9, 10, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 7, 9, 10, 7, 9, 10, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 5, 9, 7, 6, 9, 7, false, randomIn, MOSS_STONE_SELECTOR);
         IBlockState iblockstate3 = Blocks.COBBLESTONE_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.EAST);
         IBlockState iblockstate4 = Blocks.COBBLESTONE_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.WEST);
         IBlockState iblockstate = Blocks.COBBLESTONE_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.SOUTH);
         IBlockState iblockstate1 = Blocks.COBBLESTONE_STAIRS.getDefaultState().with(BlockStairs.FACING, EnumFacing.NORTH);
         this.setBlockState(worldIn, iblockstate1, 5, 9, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate1, 6, 9, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate, 5, 9, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate, 6, 9, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate1, 4, 0, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate1, 5, 0, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate1, 6, 0, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate1, 7, 0, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate1, 4, 1, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate1, 4, 2, 9, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate1, 4, 3, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate1, 7, 1, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate1, 7, 2, 9, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate1, 7, 3, 10, structureBoundingBoxIn);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 1, 9, 4, 1, 9, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 7, 1, 9, 7, 1, 9, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 1, 10, 7, 2, 10, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 5, 4, 5, 6, 4, 5, false, randomIn, MOSS_STONE_SELECTOR);
         this.setBlockState(worldIn, iblockstate3, 4, 4, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate4, 7, 4, 5, structureBoundingBoxIn);

         for(int k = 0; k < 4; ++k) {
            this.setBlockState(worldIn, iblockstate, 5, 0 - k, 6 + k, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate, 6, 0 - k, 6 + k, structureBoundingBoxIn);
            this.fillWithAir(worldIn, structureBoundingBoxIn, 5, 0 - k, 7 + k, 6, 0 - k, 9 + k);
         }

         this.fillWithAir(worldIn, structureBoundingBoxIn, 1, -3, 12, 10, -1, 13);
         this.fillWithAir(worldIn, structureBoundingBoxIn, 1, -3, 1, 3, -1, 13);
         this.fillWithAir(worldIn, structureBoundingBoxIn, 1, -3, 1, 9, -1, 5);

         for(int i1 = 1; i1 <= 13; i1 += 2) {
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, -3, i1, 1, -2, i1, false, randomIn, MOSS_STONE_SELECTOR);
         }

         for(int j1 = 2; j1 <= 12; j1 += 2) {
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, -1, j1, 3, -1, j1, false, randomIn, MOSS_STONE_SELECTOR);
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, -2, 1, 5, -2, 1, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 7, -2, 1, 9, -2, 1, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 6, -3, 1, 6, -3, 1, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 6, -1, 1, 6, -1, 1, false, randomIn, MOSS_STONE_SELECTOR);
         this.setBlockState(worldIn, Blocks.TRIPWIRE_HOOK.getDefaultState().with(BlockTripWireHook.FACING, EnumFacing.EAST).with(BlockTripWireHook.ATTACHED, Boolean.valueOf(true)), 1, -3, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.TRIPWIRE_HOOK.getDefaultState().with(BlockTripWireHook.FACING, EnumFacing.WEST).with(BlockTripWireHook.ATTACHED, Boolean.valueOf(true)), 4, -3, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.TRIPWIRE.getDefaultState().with(BlockTripWire.EAST, Boolean.valueOf(true)).with(BlockTripWire.WEST, Boolean.valueOf(true)).with(BlockTripWire.ATTACHED, Boolean.valueOf(true)), 2, -3, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.TRIPWIRE.getDefaultState().with(BlockTripWire.EAST, Boolean.valueOf(true)).with(BlockTripWire.WEST, Boolean.valueOf(true)).with(BlockTripWire.ATTACHED, Boolean.valueOf(true)), 3, -3, 8, structureBoundingBoxIn);
         IBlockState iblockstate5 = Blocks.REDSTONE_WIRE.getDefaultState().with(BlockRedstoneWire.NORTH, RedstoneSide.SIDE).with(BlockRedstoneWire.SOUTH, RedstoneSide.SIDE);
         this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState().with(BlockRedstoneWire.SOUTH, RedstoneSide.SIDE), 5, -3, 7, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate5, 5, -3, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate5, 5, -3, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate5, 5, -3, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate5, 5, -3, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate5, 5, -3, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState().with(BlockRedstoneWire.NORTH, RedstoneSide.SIDE).with(BlockRedstoneWire.WEST, RedstoneSide.SIDE), 5, -3, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState().with(BlockRedstoneWire.EAST, RedstoneSide.SIDE), 4, -3, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 3, -3, 1, structureBoundingBoxIn);
         if (!this.field_202588_g) {
            this.field_202588_g = this.createDispenser(worldIn, structureBoundingBoxIn, randomIn, 3, -2, 1, EnumFacing.NORTH, LootTableList.CHESTS_JUNGLE_TEMPLE_DISPENSER);
         }

         this.setBlockState(worldIn, Blocks.VINE.getDefaultState().with(BlockVine.SOUTH, Boolean.valueOf(true)), 3, -2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.TRIPWIRE_HOOK.getDefaultState().with(BlockTripWireHook.FACING, EnumFacing.NORTH).with(BlockTripWireHook.ATTACHED, Boolean.valueOf(true)), 7, -3, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.TRIPWIRE_HOOK.getDefaultState().with(BlockTripWireHook.FACING, EnumFacing.SOUTH).with(BlockTripWireHook.ATTACHED, Boolean.valueOf(true)), 7, -3, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.TRIPWIRE.getDefaultState().with(BlockTripWire.NORTH, Boolean.valueOf(true)).with(BlockTripWire.SOUTH, Boolean.valueOf(true)).with(BlockTripWire.ATTACHED, Boolean.valueOf(true)), 7, -3, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.TRIPWIRE.getDefaultState().with(BlockTripWire.NORTH, Boolean.valueOf(true)).with(BlockTripWire.SOUTH, Boolean.valueOf(true)).with(BlockTripWire.ATTACHED, Boolean.valueOf(true)), 7, -3, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.TRIPWIRE.getDefaultState().with(BlockTripWire.NORTH, Boolean.valueOf(true)).with(BlockTripWire.SOUTH, Boolean.valueOf(true)).with(BlockTripWire.ATTACHED, Boolean.valueOf(true)), 7, -3, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState().with(BlockRedstoneWire.EAST, RedstoneSide.SIDE), 8, -3, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState().with(BlockRedstoneWire.WEST, RedstoneSide.SIDE).with(BlockRedstoneWire.SOUTH, RedstoneSide.SIDE), 9, -3, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState().with(BlockRedstoneWire.NORTH, RedstoneSide.SIDE).with(BlockRedstoneWire.SOUTH, RedstoneSide.UP), 9, -3, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 9, -3, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState().with(BlockRedstoneWire.NORTH, RedstoneSide.SIDE), 9, -2, 4, structureBoundingBoxIn);
         if (!this.field_202589_h) {
            this.field_202589_h = this.createDispenser(worldIn, structureBoundingBoxIn, randomIn, 9, -2, 3, EnumFacing.WEST, LootTableList.CHESTS_JUNGLE_TEMPLE_DISPENSER);
         }

         this.setBlockState(worldIn, Blocks.VINE.getDefaultState().with(BlockVine.EAST, Boolean.valueOf(true)), 8, -1, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.VINE.getDefaultState().with(BlockVine.EAST, Boolean.valueOf(true)), 8, -2, 3, structureBoundingBoxIn);
         if (!this.field_202586_e) {
            this.field_202586_e = this.generateChest(worldIn, structureBoundingBoxIn, randomIn, 8, -3, 3, LootTableList.CHESTS_JUNGLE_TEMPLE);
         }

         this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 9, -3, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 8, -3, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 4, -3, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 5, -2, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 5, -1, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 6, -3, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 7, -2, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 7, -1, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 8, -3, 5, structureBoundingBoxIn);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 9, -1, 1, 9, -1, 5, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithAir(worldIn, structureBoundingBoxIn, 8, -3, 8, 10, -1, 10);
         this.setBlockState(worldIn, Blocks.CHISELED_STONE_BRICKS.getDefaultState(), 8, -2, 11, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.CHISELED_STONE_BRICKS.getDefaultState(), 9, -2, 11, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.CHISELED_STONE_BRICKS.getDefaultState(), 10, -2, 11, structureBoundingBoxIn);
         IBlockState iblockstate2 = Blocks.LEVER.getDefaultState().with(BlockLever.HORIZONTAL_FACING, EnumFacing.NORTH).with(BlockLever.FACE, AttachFace.WALL);
         this.setBlockState(worldIn, iblockstate2, 8, -2, 12, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate2, 9, -2, 12, structureBoundingBoxIn);
         this.setBlockState(worldIn, iblockstate2, 10, -2, 12, structureBoundingBoxIn);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 8, -3, 8, 8, -3, 10, false, randomIn, MOSS_STONE_SELECTOR);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 10, -3, 8, 10, -3, 10, false, randomIn, MOSS_STONE_SELECTOR);
         this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 10, -2, 9, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState().with(BlockRedstoneWire.NORTH, RedstoneSide.SIDE), 8, -2, 9, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState().with(BlockRedstoneWire.SOUTH, RedstoneSide.SIDE), 8, -2, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState(), 10, -1, 9, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.STICKY_PISTON.getDefaultState().with(BlockPistonBase.FACING, EnumFacing.UP), 9, -2, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.STICKY_PISTON.getDefaultState().with(BlockPistonBase.FACING, EnumFacing.WEST), 10, -2, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.STICKY_PISTON.getDefaultState().with(BlockPistonBase.FACING, EnumFacing.WEST), 10, -1, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.REPEATER.getDefaultState().with(BlockRedstoneRepeater.HORIZONTAL_FACING, EnumFacing.NORTH), 10, -2, 10, structureBoundingBoxIn);
         if (!this.field_202587_f) {
            this.field_202587_f = this.generateChest(worldIn, structureBoundingBoxIn, randomIn, 9, -3, 10, LootTableList.CHESTS_JUNGLE_TEMPLE);
         }

         return true;
      }
   }

   static class MossStoneSelector extends StructurePiece.BlockSelector {
      private MossStoneSelector() {
      }

      /**
       * picks Block Ids and Metadata (Silverfish)
       */
      public void selectBlocks(Random rand, int x, int y, int z, boolean wall) {
         if (rand.nextFloat() < 0.4F) {
            this.blockstate = Blocks.COBBLESTONE.getDefaultState();
         } else {
            this.blockstate = Blocks.MOSSY_COBBLESTONE.getDefaultState();
         }

      }
   }
}