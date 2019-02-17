package net.minecraft.world;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathType;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class WorldEntitySpawner {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final int MOB_COUNT_DIV = (int)Math.pow(17.0D, 2.0D);
   /** The 17x17 area around the player where mobs can spawn */
   private final Set<ChunkPos> eligibleChunksForSpawning = Sets.newHashSet();

   /**
    * adds all chunks within the spawn radius of the players to eligibleChunksForSpawning. pars: the world,
    * hostileCreatures, passiveCreatures. returns number of eligible chunks.
    */
   public int findChunksForSpawning(WorldServer worldServerIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate) {
      if (!spawnHostileMobs && !spawnPeacefulMobs) {
         return 0;
      } else {
         this.eligibleChunksForSpawning.clear();
         int i = 0;

         for(EntityPlayer entityplayer : worldServerIn.playerEntities) {
            if (!entityplayer.isSpectator()) {
               int j = MathHelper.floor(entityplayer.posX / 16.0D);
               int k = MathHelper.floor(entityplayer.posZ / 16.0D);
               int l = 8;

               for(int i1 = -8; i1 <= 8; ++i1) {
                  for(int j1 = -8; j1 <= 8; ++j1) {
                     boolean flag = i1 == -8 || i1 == 8 || j1 == -8 || j1 == 8;
                     ChunkPos chunkpos = new ChunkPos(i1 + j, j1 + k);
                     if (!this.eligibleChunksForSpawning.contains(chunkpos)) {
                        ++i;
                        if (!flag && worldServerIn.getWorldBorder().contains(chunkpos)) {
                           PlayerChunkMapEntry playerchunkmapentry = worldServerIn.getPlayerChunkMap().getEntry(chunkpos.x, chunkpos.z);
                           if (playerchunkmapentry != null && playerchunkmapentry.isSentToPlayers()) {
                              this.eligibleChunksForSpawning.add(chunkpos);
                           }
                        }
                     }
                  }
               }
            }
         }

         int k4 = 0;
         BlockPos blockpos1 = worldServerIn.getSpawnPoint();

         for(EnumCreatureType enumcreaturetype : EnumCreatureType.values()) {
            if ((!enumcreaturetype.getPeacefulCreature() || spawnPeacefulMobs) && (enumcreaturetype.getPeacefulCreature() || spawnHostileMobs) && (!enumcreaturetype.getAnimal() || spawnOnSetTickRate)) {
               int l4 = enumcreaturetype.getMaxNumberOfCreature() * i / MOB_COUNT_DIV;
               int i5 = worldServerIn.countEntities(enumcreaturetype, l4, true);
               if (i5 <= l4) {
                  BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

                  java.util.ArrayList<ChunkPos> shuffled = new java.util.ArrayList<>(this.eligibleChunksForSpawning);
                  java.util.Collections.shuffle(shuffled);
                  label158:
                  for(ChunkPos chunkpos1 : shuffled) {
                     BlockPos blockpos = getRandomChunkPosition(worldServerIn, chunkpos1.x, chunkpos1.z);
                     int k1 = blockpos.getX();
                     int l1 = blockpos.getY();
                     int i2 = blockpos.getZ();
                     IBlockState iblockstate = worldServerIn.getBlockState(blockpos);
                     if (!iblockstate.isNormalCube()) {
                        int j2 = 0;

                        for(int k2 = 0; k2 < 3; ++k2) {
                           int l2 = k1;
                           int i3 = l1;
                           int j3 = i2;
                           int k3 = 6;
                           Biome.SpawnListEntry biome$spawnlistentry = null;
                           IEntityLivingData ientitylivingdata = null;
                           int l3 = MathHelper.ceil(Math.random() * 4.0D);
                           int i4 = 0;

                           for(int j4 = 0; j4 < l3; ++j4) {
                              l2 += worldServerIn.rand.nextInt(6) - worldServerIn.rand.nextInt(6);
                              i3 += worldServerIn.rand.nextInt(1) - worldServerIn.rand.nextInt(1);
                              j3 += worldServerIn.rand.nextInt(6) - worldServerIn.rand.nextInt(6);
                              blockpos$mutableblockpos.setPos(l2, i3, j3);
                              float f = (float)l2 + 0.5F;
                              float f1 = (float)j3 + 0.5F;
                              EntityPlayer entityplayer1 = worldServerIn.func_212817_a((double)f, (double)f1, -1.0D);
                              if (entityplayer1 != null) {
                                 double d0 = entityplayer1.getDistanceSq((double)f, (double)i3, (double)f1);
                                 if (!(d0 <= 576.0D) && !(blockpos1.distanceSq((double)f, (double)i3, (double)f1) < 576.0D)) {
                                    if (biome$spawnlistentry == null) {
                                       biome$spawnlistentry = worldServerIn.getSpawnListEntryForTypeAt(enumcreaturetype, blockpos$mutableblockpos);
                                       if (biome$spawnlistentry == null) {
                                          break;
                                       }

                                       l3 = biome$spawnlistentry.minGroupCount + worldServerIn.rand.nextInt(1 + biome$spawnlistentry.maxGroupCount - biome$spawnlistentry.minGroupCount);
                                    }

                                    if (worldServerIn.canCreatureTypeSpawnHere(enumcreaturetype, biome$spawnlistentry, blockpos$mutableblockpos)) {
                                       EntitySpawnPlacementRegistry.SpawnPlacementType entityspawnplacementregistry$spawnplacementtype = EntitySpawnPlacementRegistry.getPlacementType(biome$spawnlistentry.entityType);
                                       if (entityspawnplacementregistry$spawnplacementtype != null && canCreatureTypeSpawnAtLocation(entityspawnplacementregistry$spawnplacementtype, worldServerIn, blockpos$mutableblockpos, biome$spawnlistentry.entityType)) {
                                          EntityLiving entityliving;
                                          try {
                                             entityliving = biome$spawnlistentry.entityType.create(worldServerIn);
                                          } catch (Exception exception) {
                                             LOGGER.warn("Failed to create mob", (Throwable)exception);
                                             return k4;
                                          }

                                          entityliving.setLocationAndAngles((double)f, (double)i3, (double)f1, worldServerIn.rand.nextFloat() * 360.0F, 0.0F);
                                          int canSpawn = net.minecraftforge.common.ForgeHooks.canEntitySpawn(entityliving, worldServerIn, f, i3, f1, null);
                                          if ((d0 <= 16384.0D || !entityliving.canDespawn()) && (canSpawn == 1 || (canSpawn == 0 && entityliving.canSpawn(worldServerIn, false) && entityliving.isNotColliding(worldServerIn)))) {
                                             if (!net.minecraftforge.event.ForgeEventFactory.doSpecialSpawn(entityliving, worldServerIn, f, i3, f1, null))
                                             ientitylivingdata = entityliving.onInitialSpawn(worldServerIn.getDifficultyForLocation(new BlockPos(entityliving)), ientitylivingdata, (NBTTagCompound)null);
                                             if (entityliving.isNotColliding(worldServerIn)) {
                                                ++j2;
                                                ++i4;
                                                worldServerIn.spawnEntity(entityliving);
                                             } else {
                                                entityliving.remove();
                                             }

                                             if (j2 >= net.minecraftforge.event.ForgeEventFactory.getMaxSpawnPackSize(entityliving)) {
                                                continue label158;
                                             }

                                             if (entityliving.func_204209_c(i4)) {
                                                break;
                                             }
                                          }

                                          k4 += j2;
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         return k4;
      }
   }

   private static BlockPos getRandomChunkPosition(World worldIn, int x, int z) {
      Chunk chunk = worldIn.getChunk(x, z);
      int i = x * 16 + worldIn.rand.nextInt(16);
      int j = z * 16 + worldIn.rand.nextInt(16);
      int k = chunk.getTopBlockY(Heightmap.Type.LIGHT_BLOCKING, i, j) + 1;
      int l = worldIn.rand.nextInt(k + 1);
      return new BlockPos(i, l, j);
   }

   public static boolean isValidEmptySpawnBlock(IBlockState blockIn, IFluidState fluidIn) {
      if (blockIn.isBlockNormalCube()) {
         return false;
      } else if (blockIn.canProvidePower()) {
         return false;
      } else if (!fluidIn.isEmpty()) {
         return false;
      } else {
         return !blockIn.isIn(BlockTags.RAILS);
      }
   }

   public static boolean canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.SpawnPlacementType placeType, IWorldReaderBase worldIn, BlockPos pos, @Nullable EntityType<? extends EntityLiving> entityTypeIn) {
      if (entityTypeIn != null && worldIn.getWorldBorder().contains(pos)) {
         return placeType.canSpawnAt(worldIn, pos, entityTypeIn);
      }
      return false;
   }

   public static boolean canSpawnAtBody(EntitySpawnPlacementRegistry.SpawnPlacementType placeType, IWorldReaderBase worldIn, BlockPos pos, @Nullable EntityType<? extends EntityLiving> entityTypeIn) {
      {
         IBlockState iblockstate = worldIn.getBlockState(pos);
         IFluidState ifluidstate = worldIn.getFluidState(pos);
         switch(placeType) {
         case IN_WATER:
            return ifluidstate.isTagged(FluidTags.WATER) && worldIn.getFluidState(pos.down()).isTagged(FluidTags.WATER) && !worldIn.getBlockState(pos.up()).isNormalCube();
         case ON_GROUND:
         default:
            IBlockState iblockstate1 = worldIn.getBlockState(pos.down());
            if (iblockstate.canCreatureSpawn(worldIn, pos, placeType, entityTypeIn)) {
               Block block = iblockstate1.getBlock();
               boolean flag = block != Blocks.BEDROCK && block != Blocks.BARRIER;
               return flag && isValidEmptySpawnBlock(iblockstate, ifluidstate) && isValidEmptySpawnBlock(worldIn.getBlockState(pos.up()), worldIn.getFluidState(pos.up()));
            } else {
               return false;
            }
         }
      }
   }

   /**
    * Called during chunk generation to spawn initial creatures.
    */
   public static void performWorldGenSpawning(IWorld worldIn, Biome biomeIn, int centerX, int centerZ, Random diameterX) {
      List<Biome.SpawnListEntry> list = biomeIn.getSpawns(EnumCreatureType.CREATURE);
      if (!list.isEmpty()) {
         int i = centerX << 4;
         int j = centerZ << 4;

         while(diameterX.nextFloat() < biomeIn.getSpawningChance()) {
            Biome.SpawnListEntry biome$spawnlistentry = WeightedRandom.getRandomItem(diameterX, list);
            int k = biome$spawnlistentry.minGroupCount + diameterX.nextInt(1 + biome$spawnlistentry.maxGroupCount - biome$spawnlistentry.minGroupCount);
            IEntityLivingData ientitylivingdata = null;
            int l = i + diameterX.nextInt(16);
            int i1 = j + diameterX.nextInt(16);
            int j1 = l;
            int k1 = i1;

            for(int l1 = 0; l1 < k; ++l1) {
               boolean flag = false;

               for(int i2 = 0; !flag && i2 < 4; ++i2) {
                  BlockPos blockpos = getTopSolidOrLiquidBlock(worldIn, biome$spawnlistentry.entityType, l, i1);
                  if (canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, worldIn, blockpos, biome$spawnlistentry.entityType)) {
                     EntityLiving entityliving;
                     try {
                        entityliving = biome$spawnlistentry.entityType.create(worldIn.getWorld());
                     } catch (Exception exception) {
                        LOGGER.warn("Failed to create mob", (Throwable)exception);
                        continue;
                     }

                     double d0 = MathHelper.clamp((double)l, (double)i + (double)entityliving.width, (double)i + 16.0D - (double)entityliving.width);
                     double d1 = MathHelper.clamp((double)i1, (double)j + (double)entityliving.width, (double)j + 16.0D - (double)entityliving.width);
                     if (net.minecraftforge.common.ForgeHooks.canEntitySpawn(entityliving, worldIn, d0, blockpos.getY(), d1, null) == -1) continue;
                     entityliving.setLocationAndAngles(d0, (double)blockpos.getY(), d1, diameterX.nextFloat() * 360.0F, 0.0F);
                     if (entityliving.canSpawn(worldIn, false) && entityliving.isNotColliding(worldIn)) {
                        ientitylivingdata = entityliving.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(entityliving)), ientitylivingdata, (NBTTagCompound)null);
                        worldIn.spawnEntity(entityliving);
                        flag = true;
                     }
                  }

                  l += diameterX.nextInt(5) - diameterX.nextInt(5);

                  for(i1 += diameterX.nextInt(5) - diameterX.nextInt(5); l < i || l >= i + 16 || i1 < j || i1 >= j + 16; i1 = k1 + diameterX.nextInt(5) - diameterX.nextInt(5)) {
                     l = j1 + diameterX.nextInt(5) - diameterX.nextInt(5);
                  }
               }
            }
         }

      }
   }

   private static BlockPos getTopSolidOrLiquidBlock(IWorld p_208498_0_, @Nullable EntityType<? extends EntityLiving> p_208498_1_, int p_208498_2_, int p_208498_3_) {
      BlockPos blockpos = new BlockPos(p_208498_2_, p_208498_0_.getHeight(EntitySpawnPlacementRegistry.func_209342_b(p_208498_1_), p_208498_2_, p_208498_3_), p_208498_3_);
      BlockPos blockpos1 = blockpos.down();
      return p_208498_0_.getBlockState(blockpos1).allowsMovement(p_208498_0_, blockpos1, PathType.LAND) ? blockpos1 : blockpos;
   }
}