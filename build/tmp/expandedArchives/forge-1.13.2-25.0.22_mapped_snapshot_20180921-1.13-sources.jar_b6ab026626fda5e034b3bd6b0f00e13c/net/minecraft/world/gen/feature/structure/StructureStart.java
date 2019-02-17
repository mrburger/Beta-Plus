package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Biomes;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.biome.Biome;

public abstract class StructureStart {
   /** List of all StructureComponents that are part of this structure */
   protected final List<StructurePiece> components = Lists.newArrayList();
   protected MutableBoundingBox boundingBox;
   protected int chunkPosX;
   protected int chunkPosZ;
   private Biome biome;
   private int field_212688_f;

   public StructureStart() {
   }

   public StructureStart(int chunkPosX, int chunkPosZ, Biome biomeIn, SharedSeedRandom random, long seed) {
      this.chunkPosX = chunkPosX;
      this.chunkPosZ = chunkPosZ;
      this.biome = biomeIn;
      random.setLargeFeatureSeed(seed, this.chunkPosX, this.chunkPosZ);
   }

   public MutableBoundingBox getBoundingBox() {
      return this.boundingBox;
   }

   public List<StructurePiece> getComponents() {
      return this.components;
   }

   /**
    * Keeps iterating Structure Pieces and spawning them until the checks tell it to stop
    */
   public void generateStructure(IWorld worldIn, Random rand, MutableBoundingBox structurebb, ChunkPos p_75068_4_) {
      synchronized(this.components) {
         Iterator<StructurePiece> iterator = this.components.iterator();

         while(iterator.hasNext()) {
            StructurePiece structurepiece = iterator.next();
            if (structurepiece.getBoundingBox().intersectsWith(structurebb) && !structurepiece.addComponentParts(worldIn, rand, structurebb, p_75068_4_)) {
               iterator.remove();
            }
         }

         this.recalculateStructureSize(worldIn);
      }
   }

   protected void recalculateStructureSize(IBlockReader blockReader) {
      this.boundingBox = MutableBoundingBox.getNewBoundingBox();

      for(StructurePiece structurepiece : this.components) {
         this.boundingBox.expandTo(structurepiece.getBoundingBox());
      }

   }

   public NBTTagCompound write(int chunkX, int chunkZ) {
      if (StructureIO.getStructureStartName(this) == null) { // FORGE: This is just a more friendly error instead of the 'Null String' below
         throw new RuntimeException("StructureStart \"" + this.getClass().getName() + "\" missing ID Mapping, Modder see MapGenStructureIO");
      }
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      if (this.isSizeableStructure()) {
         nbttagcompound.setString("id", StructureIO.getStructureStartName(this));
         nbttagcompound.setString("biome", IRegistry.field_212624_m.getKey(this.biome).toString());
         nbttagcompound.setInt("ChunkX", chunkX);
         nbttagcompound.setInt("ChunkZ", chunkZ);
         nbttagcompound.setInt("references", this.field_212688_f);
         nbttagcompound.setTag("BB", this.boundingBox.toNBTTagIntArray());
         NBTTagList lvt_4_1_ = new NBTTagList();
         synchronized(this.components) {
            for(StructurePiece structurepiece : this.components) {
               lvt_4_1_.add((INBTBase)structurepiece.createStructureBaseNBT());
            }
         }

         nbttagcompound.setTag("Children", lvt_4_1_);
         this.writeAdditional(nbttagcompound);
         return nbttagcompound;
      } else {
         nbttagcompound.setString("id", "INVALID");
         return nbttagcompound;
      }
   }

   public void writeAdditional(NBTTagCompound tagCompound) {
   }

   public void read(IWorld worldIn, NBTTagCompound tagCompound) {
      this.chunkPosX = tagCompound.getInt("ChunkX");
      this.chunkPosZ = tagCompound.getInt("ChunkZ");
      this.field_212688_f = tagCompound.getInt("references");
      this.biome = tagCompound.hasKey("biome") ? IRegistry.field_212624_m.func_212608_b(new ResourceLocation(tagCompound.getString("biome"))) : worldIn.getChunkProvider().getChunkGenerator().getBiomeProvider().getBiome(new BlockPos((this.chunkPosX << 4) + 9, 0, (this.chunkPosZ << 4) + 9), Biomes.PLAINS);
      if (tagCompound.hasKey("BB")) {
         this.boundingBox = new MutableBoundingBox(tagCompound.getIntArray("BB"));
      }

      NBTTagList nbttaglist = tagCompound.getList("Children", 10);

      for(int i = 0; i < nbttaglist.size(); ++i) {
         StructurePiece tmp = StructureIO.getStructureComponent(nbttaglist.getCompound(i), worldIn);
         if (tmp != null) this.components.add(tmp); //Forge: Prevent NPEs further down the line when a componenet can't be loaded.
      }

      this.readAdditional(tagCompound);
   }

   public void readAdditional(NBTTagCompound tagCompound) {
   }

   /**
    * offsets the structure Bounding Boxes up to a certain height, typically 63 - 10
    */
   protected void markAvailableHeight(IWorldReaderBase worldIn, Random rand, int p_75067_3_) {
      int i = worldIn.getSeaLevel() - p_75067_3_;
      int j = this.boundingBox.getYSize() + 1;
      if (j < i) {
         j += rand.nextInt(i - j);
      }

      int k = j - this.boundingBox.maxY;
      this.boundingBox.offset(0, k, 0);

      for(StructurePiece structurepiece : this.components) {
         structurepiece.offset(0, k, 0);
      }

   }

   protected void setRandomHeight(IBlockReader worldIn, Random rand, int p_75070_3_, int p_75070_4_) {
      int i = p_75070_4_ - p_75070_3_ + 1 - this.boundingBox.getYSize();
      int j;
      if (i > 1) {
         j = p_75070_3_ + rand.nextInt(i);
      } else {
         j = p_75070_3_;
      }

      int k = j - this.boundingBox.minY;
      this.boundingBox.offset(0, k, 0);

      for(StructurePiece structurepiece : this.components) {
         structurepiece.offset(0, k, 0);
      }

   }

   /**
    * currently only defined for Villages, returns true if Village has more than 2 non-road components
    */
   public boolean isSizeableStructure() {
      return true;
   }

   public void notifyPostProcessAt(ChunkPos pair) {
   }

   public int getChunkPosX() {
      return this.chunkPosX;
   }

   public int getChunkPosZ() {
      return this.chunkPosZ;
   }

   public BlockPos getPos() {
      return new BlockPos(this.chunkPosX << 4, 0, this.chunkPosZ << 4);
   }

   public boolean func_212687_g() {
      return this.field_212688_f < this.func_212686_i();
   }

   public void func_212685_h() {
      ++this.field_212688_f;
   }

   protected int func_212686_i() {
      return 1;
   }
}