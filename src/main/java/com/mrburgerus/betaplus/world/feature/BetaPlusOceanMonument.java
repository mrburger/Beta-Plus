package com.mrburgerus.betaplus.world.feature;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Biomes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.*;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;


/* Hopefully I can avoid this... */
public class BetaPlusOceanMonument extends Structure<OceanMonumentConfig>
{
	private static final List<Biome.SpawnListEntry> MONUMENT_ENEMIES;

	public BetaPlusOceanMonument() {
	}

	@Override
	protected ChunkPos getStartPositionForPosition(IChunkGenerator<?> p_211744_1_, Random p_211744_2_, int p_211744_3_, int p_211744_4_, int p_211744_5_, int p_211744_6_) {
		int lvt_7_1_ = p_211744_1_.getSettings().getOceanMonumentSpacing();
		int lvt_8_1_ = p_211744_1_.getSettings().getOceanMonumentSeparation();
		int lvt_9_1_ = p_211744_3_ + lvt_7_1_ * p_211744_5_;
		int lvt_10_1_ = p_211744_4_ + lvt_7_1_ * p_211744_6_;
		int lvt_11_1_ = lvt_9_1_ < 0 ? lvt_9_1_ - lvt_7_1_ + 1 : lvt_9_1_;
		int lvt_12_1_ = lvt_10_1_ < 0 ? lvt_10_1_ - lvt_7_1_ + 1 : lvt_10_1_;
		int lvt_13_1_ = lvt_11_1_ / lvt_7_1_;
		int lvt_14_1_ = lvt_12_1_ / lvt_7_1_;
		((SharedSeedRandom)p_211744_2_).setLargeFeatureSeedWithSalt(p_211744_1_.getSeed(), lvt_13_1_, lvt_14_1_, 10387313);
		lvt_13_1_ *= lvt_7_1_;
		lvt_14_1_ *= lvt_7_1_;
		lvt_13_1_ += (p_211744_2_.nextInt(lvt_7_1_ - lvt_8_1_) + p_211744_2_.nextInt(lvt_7_1_ - lvt_8_1_)) / 2;
		lvt_14_1_ += (p_211744_2_.nextInt(lvt_7_1_ - lvt_8_1_) + p_211744_2_.nextInt(lvt_7_1_ - lvt_8_1_)) / 2;
		return new ChunkPos(lvt_13_1_, lvt_14_1_);
	}

	protected boolean hasStartAt(IChunkGenerator<?> p_202372_1_, Random p_202372_2_, int p_202372_3_, int p_202372_4_) {
		ChunkPos lvt_5_1_ = this.getStartPositionForPosition(p_202372_1_, p_202372_2_, p_202372_3_, p_202372_4_, 0, 0);
		if (p_202372_3_ == lvt_5_1_.x && p_202372_4_ == lvt_5_1_.z) {
			Set<Biome> lvt_6_1_ = p_202372_1_.getBiomeProvider().getBiomesInSquare(p_202372_3_ * 16 + 9, p_202372_4_ * 16 + 9, 16);
			Iterator var7 = lvt_6_1_.iterator();

			Biome lvt_8_1_;
			do {
				if (!var7.hasNext()) {
					Set<Biome> lvt_7_1_ = p_202372_1_.getBiomeProvider().getBiomesInSquare(p_202372_3_ * 16 + 9, p_202372_4_ * 16 + 9, 29);
					Iterator var11 = lvt_7_1_.iterator();

					Biome lvt_9_1_;
					do {
						if (!var11.hasNext()) {
							return true;
						}

						lvt_9_1_ = (Biome)var11.next();
					} while(lvt_9_1_.getCategory() == Biome.Category.OCEAN || lvt_9_1_.getCategory() == Biome.Category.RIVER);

					return false;
				}

				lvt_8_1_ = (Biome)var7.next();
			} while(p_202372_1_.hasStructure(lvt_8_1_, Feature.OCEAN_MONUMENT));

			return false;
		} else {
			return false;
		}
	}

	protected boolean isEnabledIn(IWorld p_202365_1_) {
		return p_202365_1_.getWorldInfo().isMapFeaturesEnabled();
	}

	protected StructureStart makeStart(IWorld p_202369_1_, IChunkGenerator<?> p_202369_2_, SharedSeedRandom p_202369_3_, int p_202369_4_, int p_202369_5_) {
		Biome lvt_6_1_ = p_202369_2_.getBiomeProvider().getBiome(new BlockPos((p_202369_4_ << 4) + 9, 0, (p_202369_5_ << 4) + 9), Biomes.DEFAULT);
		return new OceanMonumentStructure.Start(p_202369_1_, p_202369_3_, p_202369_4_, p_202369_5_, lvt_6_1_);
	}

	protected String getStructureName() {
		return "Monument";
	}

	public int getSize() {
		return 8;
	}

	public List<Biome.SpawnListEntry> getSpawnList() {
		return MONUMENT_ENEMIES;
	}

	static {
		MONUMENT_ENEMIES = Lists.newArrayList(new Biome.SpawnListEntry[]{new Biome.SpawnListEntry(EntityType.GUARDIAN, 1, 2, 4)});
	}

	public static class Start extends StructureStart {
		private final Set<ChunkPos> processed = Sets.newHashSet();
		private boolean wasCreated;

		public Start() {
		}

		public Start(IWorld p_i48754_1_, SharedSeedRandom p_i48754_2_, int p_i48754_3_, int p_i48754_4_, Biome p_i48754_5_) {
			super(p_i48754_3_, p_i48754_4_, p_i48754_5_, p_i48754_2_, p_i48754_1_.getSeed());
			this.create(p_i48754_1_, p_i48754_2_, p_i48754_3_, p_i48754_4_);
		}

		private void create(IBlockReader p_175789_1_, Random p_175789_2_, int p_175789_3_, int p_175789_4_) {
			int lvt_5_1_ = p_175789_3_ * 16 - 29;
			int lvt_6_1_ = p_175789_4_ * 16 - 29;
			EnumFacing lvt_7_1_ = EnumFacing.Plane.HORIZONTAL.random(p_175789_2_);
			this.components.add(new OceanMonumentPieces.MonumentBuilding(p_175789_2_, lvt_5_1_, lvt_6_1_, lvt_7_1_));
			this.recalculateStructureSize(p_175789_1_);
			this.wasCreated = true;
		}

		public void generateStructure(IWorld p_75068_1_, Random p_75068_2_, MutableBoundingBox p_75068_3_, ChunkPos p_75068_4_) {
			if (!this.wasCreated) {
				this.components.clear();
				this.create(p_75068_1_, p_75068_2_, this.getChunkPosX(), this.getChunkPosZ());
			}

			super.generateStructure(p_75068_1_, p_75068_2_, p_75068_3_, p_75068_4_);
		}

		public void notifyPostProcessAt(ChunkPos p_175787_1_) {
			super.notifyPostProcessAt(p_175787_1_);
			this.processed.add(p_175787_1_);
		}

		public void writeAdditional(NBTTagCompound p_143022_1_) {
			super.writeAdditional(p_143022_1_);
			NBTTagList lvt_2_1_ = new NBTTagList();
			Iterator var3 = this.processed.iterator();

			while(var3.hasNext()) {
				ChunkPos lvt_4_1_ = (ChunkPos)var3.next();
				NBTTagCompound lvt_5_1_ = new NBTTagCompound();
				lvt_5_1_.setInt("X", lvt_4_1_.x);
				lvt_5_1_.setInt("Z", lvt_4_1_.z);
				lvt_2_1_.add(lvt_5_1_);
			}

			p_143022_1_.setTag("Processed", lvt_2_1_);
		}

		public void readAdditional(NBTTagCompound p_143017_1_) {
			super.readAdditional(p_143017_1_);
			if (p_143017_1_.contains("Processed", 9)) {
				NBTTagList lvt_2_1_ = p_143017_1_.getList("Processed", 10);

				for(int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.size(); ++lvt_3_1_) {
					NBTTagCompound lvt_4_1_ = lvt_2_1_.getCompound(lvt_3_1_);
					this.processed.add(new ChunkPos(lvt_4_1_.getInt("X"), lvt_4_1_.getInt("Z")));
				}
			}

		}
	}
}
