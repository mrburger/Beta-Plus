package net.minecraft.village;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.pathfinding.PathType;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class Village extends net.minecraftforge.common.capabilities.CapabilityProvider<Village> implements net.minecraftforge.common.extensions.IForgeVillage {
   private World world;
   /** list of VillageDoorInfo objects */
   private final List<VillageDoorInfo> villageDoorInfoList = Lists.newArrayList();
   /**
    * This is the sum of all door coordinates and used to calculate the actual village center by dividing by the number
    * of doors.
    */
   private BlockPos centerHelper = BlockPos.ORIGIN;
   /** This is the actual village center. */
   private BlockPos center = BlockPos.ORIGIN;
   private int villageRadius;
   private int lastAddDoorTimestamp;
   private int tickCounter;
   private int villagerCount;
   /** Timestamp of tick count when villager last bred */
   private int noBreedTicks;
   /** List of player reputations with this village */
   private final Map<UUID, Integer> playerReputation = Maps.newHashMap();
   private final List<Village.VillageAggressor> villageAgressors = Lists.newArrayList();
   private int golemCount;

   public Village() {
      this(null);
   }

   public Village(World worldIn) {
      super(Village.class);
      this.world = worldIn;
      this.gatherCapabilities();
   }

   public void setWorld(World worldIn) {
      this.world = worldIn;
   }

   /**
    * Called periodically by VillageCollection
    */
   public void tick(int tickCounterIn) {
      this.tickCounter = tickCounterIn;
      this.removeDeadAndOutOfRangeDoors();
      this.removeDeadAndOldAgressors();
      if (tickCounterIn % 20 == 0) {
         this.updateVillagerCount();
      }

      if (tickCounterIn % 30 == 0) {
         this.updateGolemCount();
      }

      int i = this.villagerCount / 10;
      if (this.golemCount < i && this.villageDoorInfoList.size() > 20 && this.world.rand.nextInt(7000) == 0) {
         Entity entity = this.func_208059_f(this.center);
         if (entity != null) {
            ++this.golemCount;
         }
      }

   }

   @Nullable
   private Entity func_208059_f(BlockPos p_208059_1_) {
      for(int i = 0; i < 10; ++i) {
         BlockPos blockpos = p_208059_1_.add(this.world.rand.nextInt(16) - 8, this.world.rand.nextInt(6) - 3, this.world.rand.nextInt(16) - 8);
         if (this.isBlockPosWithinSqVillageRadius(blockpos)) {
            EntityIronGolem entityirongolem = EntityType.IRON_GOLEM.makeEntity(this.world, (NBTTagCompound)null, (ITextComponent)null, (EntityPlayer)null, blockpos, false, false);
            if (entityirongolem != null) {
               if (entityirongolem.canSpawn(this.world, false) && entityirongolem.isNotColliding(this.world)) {
                  this.world.spawnEntity(entityirongolem);
                  return entityirongolem;
               }

               entityirongolem.remove();
            }
         }
      }

      return null;
   }

   private void updateGolemCount() {
      List<EntityIronGolem> list = this.world.getEntitiesWithinAABB(EntityIronGolem.class, new AxisAlignedBB((double)(this.center.getX() - this.villageRadius), (double)(this.center.getY() - 4), (double)(this.center.getZ() - this.villageRadius), (double)(this.center.getX() + this.villageRadius), (double)(this.center.getY() + 4), (double)(this.center.getZ() + this.villageRadius)));
      this.golemCount = list.size();
   }

   private void updateVillagerCount() {
      List<EntityVillager> list = this.world.getEntitiesWithinAABB(EntityVillager.class, new AxisAlignedBB((double)(this.center.getX() - this.villageRadius), (double)(this.center.getY() - 4), (double)(this.center.getZ() - this.villageRadius), (double)(this.center.getX() + this.villageRadius), (double)(this.center.getY() + 4), (double)(this.center.getZ() + this.villageRadius)));
      this.villagerCount = list.size();
      if (this.villagerCount == 0) {
         this.playerReputation.clear();
      }

   }

   public BlockPos getCenter() {
      return this.center;
   }

   public int getVillageRadius() {
      return this.villageRadius;
   }

   /**
    * Actually get num village door info entries, but that boils down to number of doors. Called by EntityAIVillagerMate
    * and VillageSiege
    */
   public int getNumVillageDoors() {
      return this.villageDoorInfoList.size();
   }

   public int getTicksSinceLastDoorAdding() {
      return this.tickCounter - this.lastAddDoorTimestamp;
   }

   public int getNumVillagers() {
      return this.villagerCount;
   }

   /**
    * Checks to see if the distance squared between this BlockPos and the center of this Village is less than this
    * Village's villageRadius squared
    */
   public boolean isBlockPosWithinSqVillageRadius(BlockPos pos) {
      return this.center.distanceSq(pos) < (double)(this.villageRadius * this.villageRadius);
   }

   /**
    * called only by class EntityAIMoveThroughVillage
    */
   public List<VillageDoorInfo> getVillageDoorInfoList() {
      return this.villageDoorInfoList;
   }

   public VillageDoorInfo getNearestDoor(BlockPos pos) {
      VillageDoorInfo villagedoorinfo = null;
      int i = Integer.MAX_VALUE;

      for(VillageDoorInfo villagedoorinfo1 : this.villageDoorInfoList) {
         int j = villagedoorinfo1.getDistanceToDoorBlockSq(pos);
         if (j < i) {
            villagedoorinfo = villagedoorinfo1;
            i = j;
         }
      }

      return villagedoorinfo;
   }

   /**
    * Returns {@link net.minecraft.village.VillageDoorInfo VillageDoorInfo} from given block position
    */
   public VillageDoorInfo getDoorInfo(BlockPos pos) {
      VillageDoorInfo villagedoorinfo = null;
      int i = Integer.MAX_VALUE;

      for(VillageDoorInfo villagedoorinfo1 : this.villageDoorInfoList) {
         int j = villagedoorinfo1.getDistanceToDoorBlockSq(pos);
         if (j > 256) {
            j = j * 1000;
         } else {
            j = villagedoorinfo1.getDoorOpeningRestrictionCounter();
         }

         if (j < i) {
            BlockPos blockpos = villagedoorinfo1.getDoorBlockPos();
            EnumFacing enumfacing = villagedoorinfo1.getInsideDirection();
            if (this.world.getBlockState(blockpos.offset(enumfacing, 1)).allowsMovement(this.world, blockpos.offset(enumfacing, 1), PathType.LAND) && this.world.getBlockState(blockpos.offset(enumfacing, -1)).allowsMovement(this.world, blockpos.offset(enumfacing, -1), PathType.LAND) && this.world.getBlockState(blockpos.up().offset(enumfacing, 1)).allowsMovement(this.world, blockpos.up().offset(enumfacing, 1), PathType.LAND) && this.world.getBlockState(blockpos.up().offset(enumfacing, -1)).allowsMovement(this.world, blockpos.up().offset(enumfacing, -1), PathType.LAND)) {
               villagedoorinfo = villagedoorinfo1;
               i = j;
            }
         }
      }

      return villagedoorinfo;
   }

   /**
    * if door not existed in this village, null will be returned
    */
   @Nullable
   public VillageDoorInfo getExistedDoor(BlockPos doorBlock) {
      if (this.center.distanceSq(doorBlock) > (double)(this.villageRadius * this.villageRadius)) {
         return null;
      } else {
         for(VillageDoorInfo villagedoorinfo : this.villageDoorInfoList) {
            if (villagedoorinfo.getDoorBlockPos().getX() == doorBlock.getX() && villagedoorinfo.getDoorBlockPos().getZ() == doorBlock.getZ() && Math.abs(villagedoorinfo.getDoorBlockPos().getY() - doorBlock.getY()) <= 1) {
               return villagedoorinfo;
            }
         }

         return null;
      }
   }

   public void addVillageDoorInfo(VillageDoorInfo doorInfo) {
      this.villageDoorInfoList.add(doorInfo);
      this.centerHelper = this.centerHelper.add(doorInfo.getDoorBlockPos());
      this.updateVillageRadiusAndCenter();
      this.lastAddDoorTimestamp = doorInfo.getLastActivityTimestamp();
   }

   /**
    * Returns true, if there is not a single village door left. Called by VillageCollection
    */
   public boolean isAnnihilated() {
      return this.villageDoorInfoList.isEmpty();
   }

   public void addOrRenewAgressor(EntityLivingBase entitylivingbaseIn) {
      for(Village.VillageAggressor village$villageaggressor : this.villageAgressors) {
         if (village$villageaggressor.agressor == entitylivingbaseIn) {
            village$villageaggressor.agressionTime = this.tickCounter;
            return;
         }
      }

      this.villageAgressors.add(new Village.VillageAggressor(entitylivingbaseIn, this.tickCounter));
   }

   @Nullable
   public EntityLivingBase findNearestVillageAggressor(EntityLivingBase entitylivingbaseIn) {
      double d0 = Double.MAX_VALUE;
      Village.VillageAggressor village$villageaggressor = null;

      for(int i = 0; i < this.villageAgressors.size(); ++i) {
         Village.VillageAggressor village$villageaggressor1 = this.villageAgressors.get(i);
         double d1 = village$villageaggressor1.agressor.getDistanceSq(entitylivingbaseIn);
         if (!(d1 > d0)) {
            village$villageaggressor = village$villageaggressor1;
            d0 = d1;
         }
      }

      return village$villageaggressor == null ? null : village$villageaggressor.agressor;
   }

   public EntityPlayer getNearestTargetPlayer(EntityLivingBase villageDefender) {
      double d0 = Double.MAX_VALUE;
      EntityPlayer entityplayer = null;

      for(UUID s : this.playerReputation.keySet()) {
         if (this.isPlayerReputationTooLow(s)) {
            EntityPlayer entityplayer1 = this.world.getPlayerEntityByUUID(s);
            if (entityplayer1 != null) {
               double d1 = entityplayer1.getDistanceSq(villageDefender);
               if (!(d1 > d0)) {
                  entityplayer = entityplayer1;
                  d0 = d1;
               }
            }
         }
      }

      return entityplayer;
   }

   private void removeDeadAndOldAgressors() {
      Iterator<Village.VillageAggressor> iterator = this.villageAgressors.iterator();

      while(iterator.hasNext()) {
         Village.VillageAggressor village$villageaggressor = iterator.next();
         if (!village$villageaggressor.agressor.isAlive() || Math.abs(this.tickCounter - village$villageaggressor.agressionTime) > 300) {
            iterator.remove();
         }
      }

   }

   private void removeDeadAndOutOfRangeDoors() {
      boolean flag = false;
      boolean flag1 = this.world.rand.nextInt(50) == 0;
      Iterator<VillageDoorInfo> iterator = this.villageDoorInfoList.iterator();

      while(iterator.hasNext()) {
         VillageDoorInfo villagedoorinfo = iterator.next();
         if (flag1) {
            villagedoorinfo.resetDoorOpeningRestrictionCounter();
         }

         if (world.isBlockLoaded(villagedoorinfo.getDoorBlockPos())) // Forge: check that the door block is loaded to avoid loading chunks
         if (!this.isWoodDoor(villagedoorinfo.getDoorBlockPos()) || Math.abs(this.tickCounter - villagedoorinfo.getLastActivityTimestamp()) > 1200) {
            this.centerHelper = this.centerHelper.subtract(villagedoorinfo.getDoorBlockPos());
            flag = true;
            villagedoorinfo.setIsDetachedFromVillageFlag(true);
            iterator.remove();
         }
      }

      if (flag) {
         this.updateVillageRadiusAndCenter();
      }

   }

   private boolean isWoodDoor(BlockPos pos) {
      IBlockState iblockstate = this.world.getBlockState(pos);
      Block block = iblockstate.getBlock();
      if (block instanceof BlockDoor) {
         return iblockstate.getMaterial() == Material.WOOD;
      } else {
         return false;
      }
   }

   private void updateVillageRadiusAndCenter() {
      int i = this.villageDoorInfoList.size();
      if (i == 0) {
         this.center = BlockPos.ORIGIN;
         this.villageRadius = 0;
      } else {
         this.center = new BlockPos(this.centerHelper.getX() / i, this.centerHelper.getY() / i, this.centerHelper.getZ() / i);
         int j = 0;

         for(VillageDoorInfo villagedoorinfo : this.villageDoorInfoList) {
            j = Math.max(villagedoorinfo.getDistanceToDoorBlockSq(this.center), j);
         }

         this.villageRadius = Math.max(32, (int)Math.sqrt((double)j) + 1);
      }
   }

   /**
    * Return the village reputation for a player
    */
   @Deprecated //Hasn't worked since 1.9, use UUID version below.
   public int getPlayerReputation(String playerName) {
      return this.getPlayerReputation(findUUID(playerName));
   }

   public int getPlayerReputation(UUID playerName) {
      Integer integer = this.playerReputation.get(playerName);
      return integer == null ? 0 : integer;
   }

   private UUID findUUID(String name) {
      if (this.world == null || this.world.getServer() == null)
         return EntityPlayer.getOfflineUUID(name);
      GameProfile profile = this.world.getServer().getPlayerProfileCache().getGameProfileForUsername(name);
      return profile == null ? EntityPlayer.getOfflineUUID(name) : profile.getId();
   }

   /**
    * Modify a players reputation in the village. Use positive values to increase reputation and negative values to
    * decrease. <br>Note that a players reputation is clamped between -30 and 10
    */
   @Deprecated //Hasn't worked since 1.9, use UUID version below.
   public int modifyPlayerReputation(String playerName, int reputation) {
      return this.modifyPlayerReputation(findUUID(playerName), reputation);
   }

   public int modifyPlayerReputation(UUID playerName, int reputation) {
      int i = this.getPlayerReputation(playerName);
      int j = MathHelper.clamp(i + reputation, -30, 10);
      this.playerReputation.put(playerName, j);
      return j;
   }

   /**
    * Return whether this player has a too low reputation with this village.
    */
   public boolean isPlayerReputationTooLow(String playerName) {
      return this.isPlayerReputationTooLow(findUUID(playerName));
   }

   public boolean isPlayerReputationTooLow(UUID uuid) {
      return this.getPlayerReputation(uuid) <= -15;
   }

   /**
    * Read this village's data from NBT.
    */
   public void readVillageDataFromNBT(NBTTagCompound compound) {
      this.villagerCount = compound.getInt("PopSize");
      this.villageRadius = compound.getInt("Radius");
      this.golemCount = compound.getInt("Golems");
      this.lastAddDoorTimestamp = compound.getInt("Stable");
      this.tickCounter = compound.getInt("Tick");
      this.noBreedTicks = compound.getInt("MTick");
      this.center = new BlockPos(compound.getInt("CX"), compound.getInt("CY"), compound.getInt("CZ"));
      this.centerHelper = new BlockPos(compound.getInt("ACX"), compound.getInt("ACY"), compound.getInt("ACZ"));
      NBTTagList nbttaglist = compound.getList("Doors", 10);

      for(int i = 0; i < nbttaglist.size(); ++i) {
         NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);
         VillageDoorInfo villagedoorinfo = new VillageDoorInfo(new BlockPos(nbttagcompound.getInt("X"), nbttagcompound.getInt("Y"), nbttagcompound.getInt("Z")), nbttagcompound.getInt("IDX"), nbttagcompound.getInt("IDZ"), nbttagcompound.getInt("TS"));
         this.villageDoorInfoList.add(villagedoorinfo);
      }

      NBTTagList nbttaglist1 = compound.getList("Players", 10);

      for(int j = 0; j < nbttaglist1.size(); ++j) {
         NBTTagCompound nbttagcompound1 = nbttaglist1.getCompound(j);
         if (nbttagcompound1.hasKey("UUID")) {
             this.playerReputation.put(UUID.fromString(nbttagcompound1.getString("UUID")), Integer.valueOf(nbttagcompound1.getInt("S")));
         } else {
            //World is never set here, so this will always be offline UUIDs, sadly there is no way to convert this.
            this.playerReputation.put(findUUID(nbttagcompound1.getString("Name")), Integer.valueOf(nbttagcompound1.getInt("S")));
         }
      }
      if (compound.hasKey("ForgeCaps")) this.deserializeCaps(compound.getCompound("ForgeCaps"));
   }

   /**
    * Write this village's data to NBT.
    */
   public void writeVillageDataToNBT(NBTTagCompound compound) {
      compound.setInt("PopSize", this.villagerCount);
      compound.setInt("Radius", this.villageRadius);
      compound.setInt("Golems", this.golemCount);
      compound.setInt("Stable", this.lastAddDoorTimestamp);
      compound.setInt("Tick", this.tickCounter);
      compound.setInt("MTick", this.noBreedTicks);
      compound.setInt("CX", this.center.getX());
      compound.setInt("CY", this.center.getY());
      compound.setInt("CZ", this.center.getZ());
      compound.setInt("ACX", this.centerHelper.getX());
      compound.setInt("ACY", this.centerHelper.getY());
      compound.setInt("ACZ", this.centerHelper.getZ());
      NBTTagList nbttaglist = new NBTTagList();

      for(VillageDoorInfo villagedoorinfo : this.villageDoorInfoList) {
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         nbttagcompound.setInt("X", villagedoorinfo.getDoorBlockPos().getX());
         nbttagcompound.setInt("Y", villagedoorinfo.getDoorBlockPos().getY());
         nbttagcompound.setInt("Z", villagedoorinfo.getDoorBlockPos().getZ());
         nbttagcompound.setInt("IDX", villagedoorinfo.getInsideOffsetX());
         nbttagcompound.setInt("IDZ", villagedoorinfo.getInsideOffsetZ());
         nbttagcompound.setInt("TS", villagedoorinfo.getLastActivityTimestamp());
         nbttaglist.add((INBTBase)nbttagcompound);
      }

      compound.setTag("Doors", nbttaglist);
      NBTTagList nbttaglist1 = new NBTTagList();

      for(UUID s : this.playerReputation.keySet()) {
         NBTTagCompound nbttagcompound1 = new NBTTagCompound();

         try {
            {
               nbttagcompound1.setString("UUID", s.toString());
               nbttagcompound1.setInt("S", this.playerReputation.get(s));
               nbttaglist1.add((INBTBase)nbttagcompound1);
            }
         } catch (RuntimeException var9) {
            ;
         }
      }

      compound.setTag("Players", nbttaglist1);
      NBTTagCompound capTag = this.serializeCaps();
      if (capTag != null) compound.setTag("ForgeCaps", capTag);
   }

   /**
    * Prevent villager breeding for a fixed interval of time
    */
   public void endMatingSeason() {
      this.noBreedTicks = this.tickCounter;
   }

   /**
    * Return whether villagers mating refractory period has passed
    */
   public boolean isMatingSeason() {
      return this.noBreedTicks == 0 || this.tickCounter - this.noBreedTicks >= 3600;
   }

   public void setDefaultPlayerReputation(int defaultReputation) {
      for(UUID s : this.playerReputation.keySet()) {
         this.modifyPlayerReputation(s, defaultReputation);
      }

   }

   class VillageAggressor {
      public EntityLivingBase agressor;
      public int agressionTime;

      VillageAggressor(EntityLivingBase agressorIn, int agressionTimeIn) {
         this.agressor = agressorIn;
         this.agressionTime = agressionTimeIn;
      }
   }
}