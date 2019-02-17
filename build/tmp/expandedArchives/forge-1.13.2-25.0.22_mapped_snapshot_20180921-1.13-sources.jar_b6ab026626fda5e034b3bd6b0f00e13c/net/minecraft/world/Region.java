package net.minecraft.world;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Region implements IWorldReader {
   protected int chunkX;
   protected int chunkZ;
   protected Chunk[][] chunkArray;
   /** set by !chunk.getAreLevelsEmpty */
   protected boolean empty;
   /** Reference to the World object. */
   protected World world;

   public Region(World worldIn, BlockPos posFromIn, BlockPos posToIn, int subIn) {
      this.world = worldIn;
      this.chunkX = posFromIn.getX() - subIn >> 4;
      this.chunkZ = posFromIn.getZ() - subIn >> 4;
      int i = posToIn.getX() + subIn >> 4;
      int j = posToIn.getZ() + subIn >> 4;
      this.chunkArray = new Chunk[i - this.chunkX + 1][j - this.chunkZ + 1];
      this.empty = true;

      for(int k = this.chunkX; k <= i; ++k) {
         for(int l = this.chunkZ; l <= j; ++l) {
            this.chunkArray[k - this.chunkX][l - this.chunkZ] = worldIn.getChunk(k, l);
         }
      }

      for(int i1 = posFromIn.getX() >> 4; i1 <= posToIn.getX() >> 4; ++i1) {
         for(int j1 = posFromIn.getZ() >> 4; j1 <= posToIn.getZ() >> 4; ++j1) {
            Chunk chunk = this.chunkArray[i1 - this.chunkX][j1 - this.chunkZ];
            if (chunk != null && !chunk.isEmptyBetween(posFromIn.getY(), posToIn.getY())) {
               this.empty = false;
            }
         }
      }

   }

   @Nullable
   public TileEntity getTileEntity(BlockPos pos) {
      return this.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK); // Forge: don't modify world from other threads
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos pos, Chunk.EnumCreateEntityType createType) {
      int i = (pos.getX() >> 4) - this.chunkX;
      int j = (pos.getZ() >> 4) - this.chunkZ;
      if (!withinBounds(i, j)) return null;
      return this.chunkArray[i][j].getTileEntity(pos, createType);
   }

   @OnlyIn(Dist.CLIENT)
   public int getCombinedLight(BlockPos pos, int lightValue) {
      int i = this.getLightForExt(EnumLightType.SKY, pos);
      int j = this.getLightForExt(EnumLightType.BLOCK, pos);
      if (j < lightValue) {
         j = lightValue;
      }

      return i << 20 | j << 4;
   }

   public float getBrightness(BlockPos pos) {
      return this.world.dimension.getLightBrightnessTable()[this.getLight(pos)];
   }

   public int getNeighborAwareLightSubtracted(BlockPos pos, int amount) {
      if (this.getBlockState(pos).useNeighborBrightness(this, pos)) {
         int i = 0;

         for(EnumFacing enumfacing : EnumFacing.values()) {
            int j = this.getLightSubtracted(pos.offset(enumfacing), amount);
            if (j > i) {
               i = j;
            }

            if (i >= 15) {
               return i;
            }
         }

         return i;
      } else {
         return this.getLightSubtracted(pos, amount);
      }
   }

   public Dimension getDimension() {
      return this.world.getDimension();
   }

   public int getLightSubtracted(BlockPos pos, int amount) {
      if (pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() <= 30000000) {
         if (pos.getY() < 0) {
            return 0;
         } else if (pos.getY() >= 256) {
            int k = 15 - amount;
            if (k < 0) {
               k = 0;
            }

            return k;
         } else {
            int i = (pos.getX() >> 4) - this.chunkX;
            int j = (pos.getZ() >> 4) - this.chunkZ;
            return this.chunkArray[i][j].getLightSubtracted(pos, amount);
         }
      } else {
         return 15;
      }
   }

   public boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
      return this.containsChunk(x, z);
   }

   public boolean canSeeSky(BlockPos pos) {
      return false;
   }

   public boolean containsChunk(int p_205054_1_, int p_205054_2_) {
      int i = p_205054_1_ - this.chunkX;
      int j = p_205054_2_ - this.chunkZ;
      return i >= 0 && i < this.chunkArray.length && j >= 0 && j < this.chunkArray[i].length;
   }

   public int getHeight(Heightmap.Type heightmapType, int x, int z) {
      throw new RuntimeException("NOT IMPLEMENTED!");
   }

   public WorldBorder getWorldBorder() {
      return this.world.getWorldBorder();
   }

   public boolean checkNoEntityCollision(@Nullable Entity entityIn, VoxelShape shape) {
      throw new RuntimeException("This method should never be called here. No entity logic inside Region");
   }

   @Nullable
   public EntityPlayer getClosestPlayer(double x, double y, double z, double distance, Predicate<Entity> predicate) {
      throw new RuntimeException("This method should never be called here. No entity logic inside Region");
   }

   public IBlockState getBlockState(BlockPos pos) {
      if (pos.getY() >= 0 && pos.getY() < 256) {
         int i = (pos.getX() >> 4) - this.chunkX;
         int j = (pos.getZ() >> 4) - this.chunkZ;
         if (i >= 0 && i < this.chunkArray.length && j >= 0 && j < this.chunkArray[i].length) {
            Chunk chunk = this.chunkArray[i][j];
            if (chunk != null) {
               return chunk.getBlockState(pos);
            }
         }
      }

      return Blocks.AIR.getDefaultState();
   }

   public IFluidState getFluidState(BlockPos pos) {
      if (pos.getY() >= 0 && pos.getY() < 256) {
         int i = (pos.getX() >> 4) - this.chunkX;
         int j = (pos.getZ() >> 4) - this.chunkZ;
         if (i >= 0 && i < this.chunkArray.length && j >= 0 && j < this.chunkArray[i].length) {
            Chunk chunk = this.chunkArray[i][j];
            if (chunk != null) {
               return chunk.getFluidState(pos);
            }
         }
      }

      return Fluids.EMPTY.getDefaultState();
   }

   public int getSkylightSubtracted() {
      return 0;
   }

   public Biome getBiome(BlockPos pos) {
      int i = (pos.getX() >> 4) - this.chunkX;
      int j = (pos.getZ() >> 4) - this.chunkZ;
      if (!withinBounds(i, j)) return net.minecraft.init.Biomes.PLAINS;
      return this.chunkArray[i][j].getBiome(pos);
   }

   @OnlyIn(Dist.CLIENT)
   private int getLightForExt(EnumLightType type, BlockPos pos) {
      if (type == EnumLightType.SKY && !this.world.getDimension().hasSkyLight()) {
         return 0;
      } else if (pos.getY() >= 0 && pos.getY() < 256) {
         if (this.getBlockState(pos).useNeighborBrightness(this, pos)) {
            int l = 0;

            for(EnumFacing enumfacing : EnumFacing.values()) {
               int k = this.getLightFor(type, pos.offset(enumfacing));
               if (k > l) {
                  l = k;
               }

               if (l >= 15) {
                  return l;
               }
            }

            return l;
         } else {
            int i = (pos.getX() >> 4) - this.chunkX;
            int j = (pos.getZ() >> 4) - this.chunkZ;
            if (!withinBounds(i, j)) return type.defaultLightValue;
            return this.chunkArray[i][j].getLightFor(type, pos);
         }
      } else {
         return type.defaultLightValue;
      }
   }

   /**
    * Checks to see if an air block exists at the provided location. Note that this only checks to see if the blocks
    * material is set to air, meaning it is possible for non-vanilla blocks to still pass this check.
    */
   public boolean isAirBlock(BlockPos pos) {
      return this.getBlockState(pos).isAir(this, pos);
   }

   public int getLightFor(EnumLightType type, BlockPos pos) {
      if (pos.getY() >= 0 && pos.getY() < 256) {
         int i = (pos.getX() >> 4) - this.chunkX;
         int j = (pos.getZ() >> 4) - this.chunkZ;
         if (!withinBounds(i, j)) return type.defaultLightValue;
         return this.chunkArray[i][j].getLightFor(type, pos);
      } else {
         return type.defaultLightValue;
      }
   }

   public int getStrongPower(BlockPos pos, EnumFacing direction) {
      return this.getBlockState(pos).getStrongPower(this, pos, direction);
   }

   public boolean isRemote() {
      throw new RuntimeException("Not yet implemented");
   }

   public int getSeaLevel() {
      throw new RuntimeException("Not yet implemented");
   }

   private boolean withinBounds(int x, int z) {
      return x >= 0 && x < chunkArray.length && z >= 0 && z < chunkArray[x].length && chunkArray[x][z] != null;
   }
}