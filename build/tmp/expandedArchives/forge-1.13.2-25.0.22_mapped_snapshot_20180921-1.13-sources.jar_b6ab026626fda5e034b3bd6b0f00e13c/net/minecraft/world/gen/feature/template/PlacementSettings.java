package net.minecraft.world.gen.feature.template;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;

public class PlacementSettings {
   private Mirror mirror = Mirror.NONE;
   private Rotation rotation = Rotation.NONE;
   /** Used to properly rotate/mirror structures according to a center that isn't a corner. */
   private BlockPos centerOffset = new BlockPos(0, 0, 0);
   private boolean ignoreEntities;
   /** the type of block in the world that will get replaced by the structure */
   @Nullable
   private Block replacedBlock;
   /** the chunk the structure is within */
   @Nullable
   private ChunkPos chunk;
   /** the bounds the structure is contained within */
   @Nullable
   private MutableBoundingBox boundingBox;
   private boolean ignoreStructureBlock = true;
   private boolean field_204765_h = true;
   private float integrity = 1.0F;
   @Nullable
   private Random random;
   @Nullable
   private Long setSeed;
   @Nullable
   private Integer field_204766_l;
   private int field_204767_m;

   public PlacementSettings copy() {
      PlacementSettings placementsettings = new PlacementSettings();
      placementsettings.mirror = this.mirror;
      placementsettings.rotation = this.rotation;
      placementsettings.centerOffset = this.centerOffset;
      placementsettings.ignoreEntities = this.ignoreEntities;
      placementsettings.replacedBlock = this.replacedBlock;
      placementsettings.chunk = this.chunk;
      placementsettings.boundingBox = this.boundingBox;
      placementsettings.ignoreStructureBlock = this.ignoreStructureBlock;
      placementsettings.field_204765_h = this.field_204765_h;
      placementsettings.integrity = this.integrity;
      placementsettings.random = this.random;
      placementsettings.setSeed = this.setSeed;
      placementsettings.field_204766_l = this.field_204766_l;
      placementsettings.field_204767_m = this.field_204767_m;
      return placementsettings;
   }

   public PlacementSettings setMirror(Mirror mirrorIn) {
      this.mirror = mirrorIn;
      return this;
   }

   public PlacementSettings setRotation(Rotation rotationIn) {
      this.rotation = rotationIn;
      return this;
   }

   public PlacementSettings setCenterOffset(BlockPos center) {
      this.centerOffset = center;
      return this;
   }

   public PlacementSettings setIgnoreEntities(boolean ignoreEntitiesIn) {
      this.ignoreEntities = ignoreEntitiesIn;
      return this;
   }

   public PlacementSettings setReplacedBlock(Block replacedBlockIn) {
      this.replacedBlock = replacedBlockIn;
      return this;
   }

   public PlacementSettings setChunk(ChunkPos chunkPosIn) {
      this.chunk = chunkPosIn;
      return this;
   }

   public PlacementSettings setBoundingBox(MutableBoundingBox boundingBoxIn) {
      this.boundingBox = boundingBoxIn;
      return this;
   }

   public PlacementSettings setSeed(@Nullable Long seedIn) {
      this.setSeed = seedIn;
      return this;
   }

   public PlacementSettings setRandom(@Nullable Random randomIn) {
      this.random = randomIn;
      return this;
   }

   public PlacementSettings setIntegrity(float integrityIn) {
      this.integrity = integrityIn;
      return this;
   }

   public Mirror getMirror() {
      return this.mirror;
   }

   public PlacementSettings setIgnoreStructureBlock(boolean ignoreStructureBlockIn) {
      this.ignoreStructureBlock = ignoreStructureBlockIn;
      return this;
   }

   public Rotation getRotation() {
      return this.rotation;
   }

   public BlockPos func_207664_d() {
      return this.centerOffset;
   }

   public Random getRandom(@Nullable BlockPos seed) {
      if (this.random != null) {
         return this.random;
      } else if (this.setSeed != null) {
         return this.setSeed == 0L ? new Random(Util.milliTime()) : new Random(this.setSeed);
      } else {
         return seed == null ? new Random(Util.milliTime()) : SharedSeedRandom.seedSlimeChunk(seed.getX(), seed.getZ(), 0L, 987234911L);
      }
   }

   public float getIntegrity() {
      return this.integrity;
   }

   public boolean getIgnoreEntities() {
      return this.ignoreEntities;
   }

   @Nullable
   public Block getReplacedBlock() {
      return this.replacedBlock;
   }

   @Nullable
   public MutableBoundingBox getBoundingBox() {
      if (this.boundingBox == null && this.chunk != null) {
         this.setBoundingBoxFromChunk();
      }

      return this.boundingBox;
   }

   public boolean getIgnoreStructureBlock() {
      return this.ignoreStructureBlock;
   }

   void setBoundingBoxFromChunk() {
      if (this.chunk != null) {
         this.boundingBox = this.getBoundingBoxFromChunk(this.chunk);
      }

   }

   public boolean func_204763_l() {
      return this.field_204765_h;
   }

   public List<Template.BlockInfo> func_204764_a(List<List<Template.BlockInfo>> p_204764_1_, @Nullable BlockPos p_204764_2_) {
      this.field_204766_l = 8;
      if (this.field_204766_l != null && this.field_204766_l >= 0 && this.field_204766_l < p_204764_1_.size()) {
         return p_204764_1_.get(this.field_204766_l);
      } else {
         this.field_204766_l = this.getRandom(p_204764_2_).nextInt(p_204764_1_.size());
         return p_204764_1_.get(this.field_204766_l);
      }
   }

   @Nullable
   private MutableBoundingBox getBoundingBoxFromChunk(@Nullable ChunkPos pos) {
      if (pos == null) {
         return this.boundingBox;
      } else {
         int i = pos.x * 16;
         int j = pos.z * 16;
         return new MutableBoundingBox(i, 0, j, i + 16 - 1, 255, j + 16 - 1);
      }
   }
}