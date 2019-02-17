package net.minecraft.world.gen.feature.structure;

import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.TemplateManager;

public abstract class ScatteredStructurePiece extends StructurePiece {
   protected int width;
   protected int height;
   protected int depth;
   protected int hPos = -1;

   public ScatteredStructurePiece() {
   }

   protected ScatteredStructurePiece(Random random, int x, int y, int z, int widthIn, int heightIn, int p_i48655_7_) {
      super(0);
      this.width = widthIn;
      this.height = heightIn;
      this.depth = p_i48655_7_;
      this.setCoordBaseMode(EnumFacing.Plane.HORIZONTAL.random(random));
      if (this.getCoordBaseMode().getAxis() == EnumFacing.Axis.Z) {
         this.boundingBox = new MutableBoundingBox(x, y, z, x + widthIn - 1, y + heightIn - 1, z + p_i48655_7_ - 1);
      } else {
         this.boundingBox = new MutableBoundingBox(x, y, z, x + p_i48655_7_ - 1, y + heightIn - 1, z + widthIn - 1);
      }

   }

   /**
    * (abstract) Helper method to write subclass data to NBT
    */
   protected void writeStructureToNBT(NBTTagCompound tagCompound) {
      tagCompound.setInt("Width", this.width);
      tagCompound.setInt("Height", this.height);
      tagCompound.setInt("Depth", this.depth);
      tagCompound.setInt("HPos", this.hPos);
   }

   /**
    * (abstract) Helper method to read subclass data from NBT
    */
   protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager p_143011_2_) {
      this.width = tagCompound.getInt("Width");
      this.height = tagCompound.getInt("Height");
      this.depth = tagCompound.getInt("Depth");
      this.hPos = tagCompound.getInt("HPos");
   }

   protected boolean func_202580_a(IWorld p_202580_1_, MutableBoundingBox p_202580_2_, int p_202580_3_) {
      if (this.hPos >= 0) {
         return true;
      } else {
         int i = 0;
         int j = 0;
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

         for(int k = this.boundingBox.minZ; k <= this.boundingBox.maxZ; ++k) {
            for(int l = this.boundingBox.minX; l <= this.boundingBox.maxX; ++l) {
               blockpos$mutableblockpos.setPos(l, 64, k);
               if (p_202580_2_.isVecInside(blockpos$mutableblockpos)) {
                  i += p_202580_1_.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockpos$mutableblockpos).getY();
                  ++j;
               }
            }
         }

         if (j == 0) {
            return false;
         } else {
            this.hPos = i / j;
            this.boundingBox.offset(0, this.hPos - this.boundingBox.minY + p_202580_3_, 0);
            return true;
         }
      }
   }
}