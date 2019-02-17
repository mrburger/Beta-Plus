package net.minecraft.world.gen.feature.structure;

import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public abstract class TemplateStructurePiece extends StructurePiece {
   private static final PlacementSettings DEFAULT_PLACE_SETTINGS = new PlacementSettings();
   protected Template template;
   protected PlacementSettings placeSettings = DEFAULT_PLACE_SETTINGS.setIgnoreEntities(true).setReplacedBlock(Blocks.AIR);
   protected BlockPos templatePosition;

   public TemplateStructurePiece() {
   }

   public TemplateStructurePiece(int type) {
      super(type);
   }

   protected void setup(Template templateIn, BlockPos pos, PlacementSettings settings) {
      this.template = templateIn;
      this.setCoordBaseMode(EnumFacing.NORTH);
      this.templatePosition = pos;
      this.placeSettings = settings;
      this.setBoundingBoxFromTemplate();
   }

   /**
    * (abstract) Helper method to write subclass data to NBT
    */
   protected void writeStructureToNBT(NBTTagCompound tagCompound) {
      tagCompound.setInt("TPX", this.templatePosition.getX());
      tagCompound.setInt("TPY", this.templatePosition.getY());
      tagCompound.setInt("TPZ", this.templatePosition.getZ());
   }

   /**
    * (abstract) Helper method to read subclass data from NBT
    */
   protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager p_143011_2_) {
      this.templatePosition = new BlockPos(tagCompound.getInt("TPX"), tagCompound.getInt("TPY"), tagCompound.getInt("TPZ"));
   }

   /**
    * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at the
    * end, it adds Fences...
    */
   public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_74875_4_) {
      this.placeSettings.setBoundingBox(structureBoundingBoxIn);
      if (this.template.addBlocksToWorld(worldIn, this.templatePosition, this.placeSettings, 2)) {
         Map<BlockPos, String> map = this.template.getDataBlocks(this.templatePosition, this.placeSettings);

         for(Entry<BlockPos, String> entry : map.entrySet()) {
            String s = entry.getValue();
            this.handleDataMarker(s, entry.getKey(), worldIn, randomIn, structureBoundingBoxIn);
         }
      }

      return true;
   }

   protected abstract void handleDataMarker(String function, BlockPos pos, IWorld worldIn, Random rand, MutableBoundingBox sbb);

   private void setBoundingBoxFromTemplate() {
      Rotation rotation = this.placeSettings.getRotation();
      BlockPos blockpos = this.placeSettings.func_207664_d();
      BlockPos blockpos1 = this.template.transformedSize(rotation);
      Mirror mirror = this.placeSettings.getMirror();
      int i = blockpos.getX();
      int j = blockpos.getZ();
      int k = blockpos1.getX() - 1;
      int l = blockpos1.getY() - 1;
      int i1 = blockpos1.getZ() - 1;
      switch(rotation) {
      case NONE:
         this.boundingBox = new MutableBoundingBox(0, 0, 0, k, l, i1);
         break;
      case CLOCKWISE_180:
         this.boundingBox = new MutableBoundingBox(i + i - k, 0, j + j - i1, i + i, l, j + j);
         break;
      case COUNTERCLOCKWISE_90:
         this.boundingBox = new MutableBoundingBox(i - j, 0, i + j - i1, i - j + k, l, i + j);
         break;
      case CLOCKWISE_90:
         this.boundingBox = new MutableBoundingBox(i + j - k, 0, j - i, i + j, l, j - i + i1);
      }

      switch(mirror) {
      case NONE:
      default:
         break;
      case FRONT_BACK:
         BlockPos blockpos3 = BlockPos.ORIGIN;
         if (rotation != Rotation.CLOCKWISE_90 && rotation != Rotation.COUNTERCLOCKWISE_90) {
            if (rotation == Rotation.CLOCKWISE_180) {
               blockpos3 = blockpos3.offset(EnumFacing.EAST, k);
            } else {
               blockpos3 = blockpos3.offset(EnumFacing.WEST, k);
            }
         } else {
            blockpos3 = blockpos3.offset(rotation.rotate(EnumFacing.WEST), i1);
         }

         this.boundingBox.offset(blockpos3.getX(), 0, blockpos3.getZ());
         break;
      case LEFT_RIGHT:
         BlockPos blockpos2 = BlockPos.ORIGIN;
         if (rotation != Rotation.CLOCKWISE_90 && rotation != Rotation.COUNTERCLOCKWISE_90) {
            if (rotation == Rotation.CLOCKWISE_180) {
               blockpos2 = blockpos2.offset(EnumFacing.SOUTH, i1);
            } else {
               blockpos2 = blockpos2.offset(EnumFacing.NORTH, i1);
            }
         } else {
            blockpos2 = blockpos2.offset(rotation.rotate(EnumFacing.NORTH), k);
         }

         this.boundingBox.offset(blockpos2.getX(), 0, blockpos2.getZ());
      }

      this.boundingBox.offset(this.templatePosition.getX(), this.templatePosition.getY(), this.templatePosition.getZ());
   }

   public void offset(int x, int y, int z) {
      super.offset(x, y, z);
      this.templatePosition = this.templatePosition.add(x, y, z);
   }
}