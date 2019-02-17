package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.TemplateManager;

public abstract class StructurePiece {
   protected static final IBlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
   protected MutableBoundingBox boundingBox;
   /** switches the Coordinate System base off the Bounding Box */
   @Nullable
   private EnumFacing coordBaseMode;
   private Mirror mirror;
   private Rotation rotation;
   /** The type ID of this component. */
   protected int componentType;
   private static final Set<Block> BLOCKS_NEEDING_POSTPROCESSING = ImmutableSet.<Block>builder().add(Blocks.NETHER_BRICK_FENCE).add(Blocks.TORCH).add(Blocks.WALL_TORCH).add(Blocks.OAK_FENCE).add(Blocks.SPRUCE_FENCE).add(Blocks.DARK_OAK_FENCE).add(Blocks.ACACIA_FENCE).add(Blocks.BIRCH_FENCE).add(Blocks.JUNGLE_FENCE).add(Blocks.LADDER).add(Blocks.IRON_BARS).build();

   public StructurePiece() {
   }

   protected StructurePiece(int type) {
      this.componentType = type;
   }

   /**
    * Writes structure base data (id, boundingbox, {@link
    * net.minecraft.world.gen.structure.StructureComponent#coordBaseMode coordBase} and {@link
    * net.minecraft.world.gen.structure.StructureComponent#componentType componentType}) to new NBTTagCompound and
    * returns it.
    */
   public final NBTTagCompound createStructureBaseNBT() {
      if (StructureIO.getStructureComponentName(this) == null) { // FORGE: Friendlier error then the Null String error below.
         throw new RuntimeException("StructureComponent \"" + this.getClass().getName() + "\" missing ID Mapping, Modder see MapGenStructureIO");
      }
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.setString("id", StructureIO.getStructureComponentName(this));
      nbttagcompound.setTag("BB", this.boundingBox.toNBTTagIntArray());
      EnumFacing enumfacing = this.getCoordBaseMode();
      nbttagcompound.setInt("O", enumfacing == null ? -1 : enumfacing.getHorizontalIndex());
      nbttagcompound.setInt("GD", this.componentType);
      this.writeStructureToNBT(nbttagcompound);
      return nbttagcompound;
   }

   /**
    * (abstract) Helper method to write subclass data to NBT
    */
   protected abstract void writeStructureToNBT(NBTTagCompound tagCompound);

   /**
    * Reads and sets structure base data (boundingbox, {@link
    * net.minecraft.world.gen.structure.StructureComponent#coordBaseMode coordBase} and {@link
    * net.minecraft.world.gen.structure.StructureComponent#componentType componentType})
    */
   public void readStructureBaseNBT(IWorld worldIn, NBTTagCompound tagCompound) {
      if (tagCompound.hasKey("BB")) {
         this.boundingBox = new MutableBoundingBox(tagCompound.getIntArray("BB"));
      }

      int i = tagCompound.getInt("O");
      this.setCoordBaseMode(i == -1 ? null : EnumFacing.byHorizontalIndex(i));
      this.componentType = tagCompound.getInt("GD");
      this.readStructureFromNBT(tagCompound, worldIn.getSaveHandler().getStructureTemplateManager());
   }

   /**
    * (abstract) Helper method to read subclass data from NBT
    */
   protected abstract void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager p_143011_2_);

   /**
    * Initiates construction of the Structure Component picked, at the current Location of StructGen
    */
   public void buildComponent(StructurePiece componentIn, List<StructurePiece> listIn, Random rand) {
   }

   /**
    * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at the
    * end, it adds Fences...
    */
   public abstract boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_74875_4_);

   public MutableBoundingBox getBoundingBox() {
      return this.boundingBox;
   }

   /**
    * Returns the component type ID of this component.
    */
   public int getComponentType() {
      return this.componentType;
   }

   /**
    * Discover if bounding box can fit within the current bounding box object.
    */
   public static StructurePiece findIntersecting(List<StructurePiece> listIn, MutableBoundingBox boundingboxIn) {
      for(StructurePiece structurepiece : listIn) {
         if (structurepiece.getBoundingBox() != null && structurepiece.getBoundingBox().intersectsWith(boundingboxIn)) {
            return structurepiece;
         }
      }

      return null;
   }

   /**
    * checks the entire StructureBoundingBox for Liquids
    */
   protected boolean isLiquidInStructureBoundingBox(IBlockReader worldIn, MutableBoundingBox boundingboxIn) {
      int i = Math.max(this.boundingBox.minX - 1, boundingboxIn.minX);
      int j = Math.max(this.boundingBox.minY - 1, boundingboxIn.minY);
      int k = Math.max(this.boundingBox.minZ - 1, boundingboxIn.minZ);
      int l = Math.min(this.boundingBox.maxX + 1, boundingboxIn.maxX);
      int i1 = Math.min(this.boundingBox.maxY + 1, boundingboxIn.maxY);
      int j1 = Math.min(this.boundingBox.maxZ + 1, boundingboxIn.maxZ);
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int k1 = i; k1 <= l; ++k1) {
         for(int l1 = k; l1 <= j1; ++l1) {
            if (worldIn.getBlockState(blockpos$mutableblockpos.setPos(k1, j, l1)).getMaterial().isLiquid()) {
               return true;
            }

            if (worldIn.getBlockState(blockpos$mutableblockpos.setPos(k1, i1, l1)).getMaterial().isLiquid()) {
               return true;
            }
         }
      }

      for(int i2 = i; i2 <= l; ++i2) {
         for(int k2 = j; k2 <= i1; ++k2) {
            if (worldIn.getBlockState(blockpos$mutableblockpos.setPos(i2, k2, k)).getMaterial().isLiquid()) {
               return true;
            }

            if (worldIn.getBlockState(blockpos$mutableblockpos.setPos(i2, k2, j1)).getMaterial().isLiquid()) {
               return true;
            }
         }
      }

      for(int j2 = k; j2 <= j1; ++j2) {
         for(int l2 = j; l2 <= i1; ++l2) {
            if (worldIn.getBlockState(blockpos$mutableblockpos.setPos(i, l2, j2)).getMaterial().isLiquid()) {
               return true;
            }

            if (worldIn.getBlockState(blockpos$mutableblockpos.setPos(l, l2, j2)).getMaterial().isLiquid()) {
               return true;
            }
         }
      }

      return false;
   }

   protected int getXWithOffset(int x, int z) {
      EnumFacing enumfacing = this.getCoordBaseMode();
      if (enumfacing == null) {
         return x;
      } else {
         switch(enumfacing) {
         case NORTH:
         case SOUTH:
            return this.boundingBox.minX + x;
         case WEST:
            return this.boundingBox.maxX - z;
         case EAST:
            return this.boundingBox.minX + z;
         default:
            return x;
         }
      }
   }

   protected int getYWithOffset(int y) {
      return this.getCoordBaseMode() == null ? y : y + this.boundingBox.minY;
   }

   protected int getZWithOffset(int x, int z) {
      EnumFacing enumfacing = this.getCoordBaseMode();
      if (enumfacing == null) {
         return z;
      } else {
         switch(enumfacing) {
         case NORTH:
            return this.boundingBox.maxZ - z;
         case SOUTH:
            return this.boundingBox.minZ + z;
         case WEST:
         case EAST:
            return this.boundingBox.minZ + x;
         default:
            return z;
         }
      }
   }

   protected void setBlockState(IWorld worldIn, IBlockState blockstateIn, int x, int y, int z, MutableBoundingBox boundingboxIn) {
      BlockPos blockpos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));
      if (boundingboxIn.isVecInside(blockpos)) {
         if (this.mirror != Mirror.NONE) {
            blockstateIn = blockstateIn.mirror(this.mirror);
         }

         if (this.rotation != Rotation.NONE) {
            blockstateIn = blockstateIn.rotate(this.rotation);
         }

         worldIn.setBlockState(blockpos, blockstateIn, 2);
         IFluidState ifluidstate = worldIn.getFluidState(blockpos);
         if (!ifluidstate.isEmpty()) {
            worldIn.getPendingFluidTicks().scheduleTick(blockpos, ifluidstate.getFluid(), 0);
         }

         if (BLOCKS_NEEDING_POSTPROCESSING.contains(blockstateIn.getBlock())) {
            worldIn.getChunkDefault(blockpos).markBlockForPostprocessing(blockpos);
         }

      }
   }

   protected IBlockState getBlockStateFromPos(IBlockReader worldIn, int x, int y, int z, MutableBoundingBox boundingboxIn) {
      int i = this.getXWithOffset(x, z);
      int j = this.getYWithOffset(y);
      int k = this.getZWithOffset(x, z);
      BlockPos blockpos = new BlockPos(i, j, k);
      return !boundingboxIn.isVecInside(blockpos) ? Blocks.AIR.getDefaultState() : worldIn.getBlockState(blockpos);
   }

   protected boolean getSkyBrightness(IWorldReaderBase worldIn, int x, int y, int z, MutableBoundingBox boundingboxIn) {
      int i = this.getXWithOffset(x, z);
      int j = this.getYWithOffset(y + 1);
      int k = this.getZWithOffset(x, z);
      BlockPos blockpos = new BlockPos(i, j, k);
      if (!boundingboxIn.isVecInside(blockpos)) {
         return false;
      } else {
         return j < worldIn.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, i, k);
      }
   }

   /**
    * arguments: (World worldObj, StructureBoundingBox structBB, int minX, int minY, int minZ, int maxX, int maxY, int
    * maxZ)
    */
   protected void fillWithAir(IWorld worldIn, MutableBoundingBox structurebb, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      for(int i = minY; i <= maxY; ++i) {
         for(int j = minX; j <= maxX; ++j) {
            for(int k = minZ; k <= maxZ; ++k) {
               this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), j, i, k, structurebb);
            }
         }
      }

   }

   /**
    * Fill the given area with the selected blocks
    */
   protected void fillWithBlocks(IWorld worldIn, MutableBoundingBox boundingboxIn, int xMin, int yMin, int zMin, int xMax, int yMax, int zMax, IBlockState boundaryBlockState, IBlockState insideBlockState, boolean existingOnly) {
      for(int i = yMin; i <= yMax; ++i) {
         for(int j = xMin; j <= xMax; ++j) {
            for(int k = zMin; k <= zMax; ++k) {
               if (!existingOnly || !this.getBlockStateFromPos(worldIn, j, i, k, boundingboxIn).isAir()) {
                  if (i != yMin && i != yMax && j != xMin && j != xMax && k != zMin && k != zMax) {
                     this.setBlockState(worldIn, insideBlockState, j, i, k, boundingboxIn);
                  } else {
                     this.setBlockState(worldIn, boundaryBlockState, j, i, k, boundingboxIn);
                  }
               }
            }
         }
      }

   }

   /**
    * arguments: World worldObj, StructureBoundingBox structBB, int minX, int minY, int minZ, int maxX, int maxY, int
    * maxZ, boolean alwaysreplace, Random rand, StructurePieceBlockSelector blockselector
    */
   protected void fillWithRandomizedBlocks(IWorld worldIn, MutableBoundingBox boundingboxIn, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean alwaysReplace, Random rand, StructurePiece.BlockSelector blockselector) {
      for(int i = minY; i <= maxY; ++i) {
         for(int j = minX; j <= maxX; ++j) {
            for(int k = minZ; k <= maxZ; ++k) {
               if (!alwaysReplace || !this.getBlockStateFromPos(worldIn, j, i, k, boundingboxIn).isAir()) {
                  blockselector.selectBlocks(rand, j, i, k, i == minY || i == maxY || j == minX || j == maxX || k == minZ || k == maxZ);
                  this.setBlockState(worldIn, blockselector.getBlockState(), j, i, k, boundingboxIn);
               }
            }
         }
      }

   }

   protected void generateMaybeBox(IWorld worldIn, MutableBoundingBox sbb, Random rand, float chance, int x1, int y1, int z1, int x2, int y2, int z2, IBlockState edgeState, IBlockState state, boolean requireNonAir, boolean requiredSkylight) {
      for(int i = y1; i <= y2; ++i) {
         for(int j = x1; j <= x2; ++j) {
            for(int k = z1; k <= z2; ++k) {
               if (!(rand.nextFloat() > chance) && (!requireNonAir || !this.getBlockStateFromPos(worldIn, j, i, k, sbb).isAir()) && (!requiredSkylight || this.getSkyBrightness(worldIn, j, i, k, sbb))) {
                  if (i != y1 && i != y2 && j != x1 && j != x2 && k != z1 && k != z2) {
                     this.setBlockState(worldIn, state, j, i, k, sbb);
                  } else {
                     this.setBlockState(worldIn, edgeState, j, i, k, sbb);
                  }
               }
            }
         }
      }

   }

   protected void randomlyPlaceBlock(IWorld worldIn, MutableBoundingBox boundingboxIn, Random rand, float chance, int x, int y, int z, IBlockState blockstateIn) {
      if (rand.nextFloat() < chance) {
         this.setBlockState(worldIn, blockstateIn, x, y, z, boundingboxIn);
      }

   }

   protected void randomlyRareFillWithBlocks(IWorld worldIn, MutableBoundingBox boundingboxIn, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, IBlockState blockstateIn, boolean excludeAir) {
      float f = (float)(maxX - minX + 1);
      float f1 = (float)(maxY - minY + 1);
      float f2 = (float)(maxZ - minZ + 1);
      float f3 = (float)minX + f / 2.0F;
      float f4 = (float)minZ + f2 / 2.0F;

      for(int i = minY; i <= maxY; ++i) {
         float f5 = (float)(i - minY) / f1;

         for(int j = minX; j <= maxX; ++j) {
            float f6 = ((float)j - f3) / (f * 0.5F);

            for(int k = minZ; k <= maxZ; ++k) {
               float f7 = ((float)k - f4) / (f2 * 0.5F);
               if (!excludeAir || !this.getBlockStateFromPos(worldIn, j, i, k, boundingboxIn).isAir()) {
                  float f8 = f6 * f6 + f5 * f5 + f7 * f7;
                  if (f8 <= 1.05F) {
                     this.setBlockState(worldIn, blockstateIn, j, i, k, boundingboxIn);
                  }
               }
            }
         }
      }

   }

   /**
    * Deletes all continuous blocks from selected position upwards. Stops at hitting air.
    */
   protected void clearCurrentPositionBlocksUpwards(IWorld worldIn, int x, int y, int z, MutableBoundingBox structurebb) {
      BlockPos blockpos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));
      if (structurebb.isVecInside(blockpos)) {
         while(!worldIn.isAirBlock(blockpos) && blockpos.getY() < 255) {
            worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 2);
            blockpos = blockpos.up();
         }

      }
   }

   /**
    * Replaces air and liquid from given position downwards. Stops when hitting anything else than air or liquid
    */
   protected void replaceAirAndLiquidDownwards(IWorld worldIn, IBlockState blockstateIn, int x, int y, int z, MutableBoundingBox boundingboxIn) {
      int i = this.getXWithOffset(x, z);
      int j = this.getYWithOffset(y);
      int k = this.getZWithOffset(x, z);
      if (boundingboxIn.isVecInside(new BlockPos(i, j, k))) {
         while((worldIn.isAirBlock(new BlockPos(i, j, k)) || worldIn.getBlockState(new BlockPos(i, j, k)).getMaterial().isLiquid()) && j > 1) {
            worldIn.setBlockState(new BlockPos(i, j, k), blockstateIn, 2);
            --j;
         }

      }
   }

   /**
    * Adds chest to the structure and sets its contents
    */
   protected boolean generateChest(IWorld worldIn, MutableBoundingBox structurebb, Random randomIn, int x, int y, int z, ResourceLocation loot) {
      BlockPos blockpos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));
      return this.generateChest(worldIn, structurebb, randomIn, blockpos, loot, (IBlockState)null);
   }

   public static IBlockState func_197528_a(IBlockReader p_197528_0_, BlockPos p_197528_1_, IBlockState p_197528_2_) {
      EnumFacing enumfacing = null;

      for(EnumFacing enumfacing1 : EnumFacing.Plane.HORIZONTAL) {
         BlockPos blockpos = p_197528_1_.offset(enumfacing1);
         IBlockState iblockstate = p_197528_0_.getBlockState(blockpos);
         if (iblockstate.getBlock() == Blocks.CHEST) {
            return p_197528_2_;
         }

         if (iblockstate.isOpaqueCube(p_197528_0_, blockpos)) {
            if (enumfacing != null) {
               enumfacing = null;
               break;
            }

            enumfacing = enumfacing1;
         }
      }

      if (enumfacing != null) {
         return p_197528_2_.with(BlockHorizontal.HORIZONTAL_FACING, enumfacing.getOpposite());
      } else {
         EnumFacing enumfacing2 = p_197528_2_.get(BlockHorizontal.HORIZONTAL_FACING);
         BlockPos blockpos1 = p_197528_1_.offset(enumfacing2);
         if (p_197528_0_.getBlockState(blockpos1).isOpaqueCube(p_197528_0_, blockpos1)) {
            enumfacing2 = enumfacing2.getOpposite();
            blockpos1 = p_197528_1_.offset(enumfacing2);
         }

         if (p_197528_0_.getBlockState(blockpos1).isOpaqueCube(p_197528_0_, blockpos1)) {
            enumfacing2 = enumfacing2.rotateY();
            blockpos1 = p_197528_1_.offset(enumfacing2);
         }

         if (p_197528_0_.getBlockState(blockpos1).isOpaqueCube(p_197528_0_, blockpos1)) {
            enumfacing2 = enumfacing2.getOpposite();
            p_197528_1_.offset(enumfacing2);
         }

         return p_197528_2_.with(BlockHorizontal.HORIZONTAL_FACING, enumfacing2);
      }
   }

   protected boolean generateChest(IWorld p_191080_1_, MutableBoundingBox p_191080_2_, Random p_191080_3_, BlockPos p_191080_4_, ResourceLocation p_191080_5_, @Nullable IBlockState p_191080_6_) {
      if (p_191080_2_.isVecInside(p_191080_4_) && p_191080_1_.getBlockState(p_191080_4_).getBlock() != Blocks.CHEST) {
         if (p_191080_6_ == null) {
            p_191080_6_ = func_197528_a(p_191080_1_, p_191080_4_, Blocks.CHEST.getDefaultState());
         }

         p_191080_1_.setBlockState(p_191080_4_, p_191080_6_, 2);
         TileEntity tileentity = p_191080_1_.getTileEntity(p_191080_4_);
         if (tileentity instanceof TileEntityChest) {
            ((TileEntityChest)tileentity).setLootTable(p_191080_5_, p_191080_3_.nextLong());
         }

         return true;
      } else {
         return false;
      }
   }

   protected boolean createDispenser(IWorld worldIn, MutableBoundingBox sbb, Random rand, int x, int y, int z, EnumFacing facing, ResourceLocation lootTableIn) {
      BlockPos blockpos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));
      if (sbb.isVecInside(blockpos) && worldIn.getBlockState(blockpos).getBlock() != Blocks.DISPENSER) {
         this.setBlockState(worldIn, Blocks.DISPENSER.getDefaultState().with(BlockDispenser.FACING, facing), x, y, z, sbb);
         TileEntity tileentity = worldIn.getTileEntity(blockpos);
         if (tileentity instanceof TileEntityDispenser) {
            ((TileEntityDispenser)tileentity).setLootTable(lootTableIn, rand.nextLong());
         }

         return true;
      } else {
         return false;
      }
   }

   protected void generateDoor(IWorld worldIn, MutableBoundingBox sbb, Random rand, int x, int y, int z, EnumFacing facing, BlockDoor door) {
      this.setBlockState(worldIn, door.getDefaultState().with(BlockDoor.FACING, facing), x, y, z, sbb);
      this.setBlockState(worldIn, door.getDefaultState().with(BlockDoor.FACING, facing).with(BlockDoor.HALF, DoubleBlockHalf.UPPER), x, y + 1, z, sbb);
   }

   public void offset(int x, int y, int z) {
      this.boundingBox.offset(x, y, z);
   }

   @Nullable
   public EnumFacing getCoordBaseMode() {
      return this.coordBaseMode;
   }

   public void setCoordBaseMode(@Nullable EnumFacing facing) {
      this.coordBaseMode = facing;
      if (facing == null) {
         this.rotation = Rotation.NONE;
         this.mirror = Mirror.NONE;
      } else {
         switch(facing) {
         case SOUTH:
            this.mirror = Mirror.LEFT_RIGHT;
            this.rotation = Rotation.NONE;
            break;
         case WEST:
            this.mirror = Mirror.LEFT_RIGHT;
            this.rotation = Rotation.CLOCKWISE_90;
            break;
         case EAST:
            this.mirror = Mirror.NONE;
            this.rotation = Rotation.CLOCKWISE_90;
            break;
         default:
            this.mirror = Mirror.NONE;
            this.rotation = Rotation.NONE;
         }
      }

   }

   public abstract static class BlockSelector {
      protected IBlockState blockstate = Blocks.AIR.getDefaultState();

      /**
       * picks Block Ids and Metadata (Silverfish)
       */
      public abstract void selectBlocks(Random rand, int x, int y, int z, boolean wall);

      public IBlockState getBlockState() {
         return this.blockstate;
      }
   }
}