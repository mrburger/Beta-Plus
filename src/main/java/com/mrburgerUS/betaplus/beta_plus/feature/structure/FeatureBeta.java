package com.mrburgerUS.betaplus.beta_plus.feature.structure;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.template.TemplateManager;

import java.util.Random;

abstract class FeatureBeta extends StructureComponent
{
	//BOUNDING BOX IS FINE
	protected int width;
	protected int height;
	protected int depth;
	private int horizontalPos = -1;

	// WORKING
	FeatureBeta(Random rand, int x, int y, int z, int sizeX, int sizeY, int sizeZ)
	{
		super(0);
		this.width = sizeX;
		this.height = sizeY;
		this.depth = sizeZ;
		this.setCoordBaseMode(EnumFacing.Plane.HORIZONTAL.random(rand));

		if (this.getCoordBaseMode().getAxis() == EnumFacing.Axis.Z)
		{
			this.boundingBox = new StructureBoundingBox(x, y, z, x + sizeX - 1, y + sizeY - 1, z + sizeZ - 1);
		}
		else
		{
			this.boundingBox = new StructureBoundingBox(x, y, z, x + sizeZ - 1, y + sizeY - 1, z + sizeX - 1);
		}
	}

	protected void writeStructureToNBT(NBTTagCompound tagCompound)
	{
		tagCompound.setInteger("Width", this.width);
		tagCompound.setInteger("Height", this.height);
		tagCompound.setInteger("Depth", this.depth);
		tagCompound.setInteger("HPos", this.horizontalPos);
	}

	protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager manager)
	{
		this.width = tagCompound.getInteger("Width");
		this.height = tagCompound.getInteger("Height");
		this.depth = tagCompound.getInteger("Depth");
		this.horizontalPos = tagCompound.getInteger("HPos");
	}

	protected boolean offsetToAverageGroundLevel(World worldIn, StructureBoundingBox structurebb, int yOffset)
	{
		if (this.horizontalPos >= 0)
		{
			return true;
		}
		else
		{
			int i = 0;
			int j = 0;
			BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

			for (int k = this.boundingBox.minZ; k <= this.boundingBox.maxZ; ++k)
			{
				for (int l = this.boundingBox.minX; l <= this.boundingBox.maxX; ++l)
				{
					blockpos$mutableblockpos.setPos(l, 64, k);

					if (structurebb.isVecInside(blockpos$mutableblockpos))
					{
						i += Math.max(worldIn.getTopSolidOrLiquidBlock(blockpos$mutableblockpos).getY(), worldIn.provider.getAverageGroundLevel());
						++j;
					}
				}
			}

			if (j == 0)
			{
				return false;
			}
			else
			{
				this.horizontalPos = i / j;
				this.boundingBox.offset(0, this.horizontalPos - this.boundingBox.minY + yOffset, 0);
				return true;
			}
		}
	}
}

