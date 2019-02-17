package net.minecraft.world;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityPhantom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsManagerServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class PhantomSpawner {
   private int ticksUntilSpawn;

   public int spawnMobs(World worldIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs) {
      if (!spawnHostileMobs) {
         return 0;
      } else {
         Random random = worldIn.rand;
         --this.ticksUntilSpawn;
         if (this.ticksUntilSpawn > 0) {
            return 0;
         } else {
            this.ticksUntilSpawn += (60 + random.nextInt(60)) * 20;
            if (worldIn.getSkylightSubtracted() < 5 && worldIn.dimension.hasSkyLight()) {
               return 0;
            } else {
               int i = 0;

               for(EntityPlayer entityplayer : worldIn.playerEntities) {
                  if (!entityplayer.isSpectator()) {
                     BlockPos blockpos = new BlockPos(entityplayer);
                     if (!worldIn.dimension.hasSkyLight() || blockpos.getY() >= worldIn.getSeaLevel() && worldIn.canSeeSky(blockpos)) {
                        DifficultyInstance difficultyinstance = worldIn.getDifficultyForLocation(blockpos);
                        if (difficultyinstance.isHarderThan(random.nextFloat() * 3.0F)) {
                           StatisticsManagerServer statisticsmanagerserver = ((EntityPlayerMP)entityplayer).getStats();
                           int j = MathHelper.clamp(statisticsmanagerserver.getValue(StatList.CUSTOM.get(StatList.TIME_SINCE_REST)), 1, Integer.MAX_VALUE);
                           int k = 24000;
                           if (random.nextInt(j) >= 72000) {
                              BlockPos blockpos1 = blockpos.up(20 + random.nextInt(15)).east(-10 + random.nextInt(21)).south(-10 + random.nextInt(21));
                              IBlockState iblockstate = worldIn.getBlockState(blockpos1);
                              IFluidState ifluidstate = worldIn.getFluidState(blockpos1);
                              if (WorldEntitySpawner.isValidEmptySpawnBlock(iblockstate, ifluidstate)) {
                                 IEntityLivingData ientitylivingdata = null;
                                 int l = 1 + random.nextInt(difficultyinstance.getDifficulty().getId() + 1);

                                 for(int i1 = 0; i1 < l; ++i1) {
                                    EntityPhantom entityphantom = new EntityPhantom(worldIn);
                                    entityphantom.moveToBlockPosAndAngles(blockpos1, 0.0F, 0.0F);
                                    ientitylivingdata = entityphantom.onInitialSpawn(difficultyinstance, ientitylivingdata, (NBTTagCompound)null);
                                    worldIn.spawnEntity(entityphantom);
                                 }

                                 i += l;
                              }
                           }
                        }
                     }
                  }
               }

               return i;
            }
         }
      }
   }
}