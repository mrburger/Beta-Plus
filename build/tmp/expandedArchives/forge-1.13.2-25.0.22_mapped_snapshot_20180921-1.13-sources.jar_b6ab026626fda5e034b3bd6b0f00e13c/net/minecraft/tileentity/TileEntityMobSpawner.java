package net.minecraft.tileentity;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityMobSpawner extends TileEntity implements ITickable {
   private final MobSpawnerBaseLogic spawnerLogic = new MobSpawnerBaseLogic() {
      public void broadcastEvent(int id) {
         TileEntityMobSpawner.this.world.addBlockEvent(TileEntityMobSpawner.this.pos, Blocks.SPAWNER, id, 0);
      }

      public World getWorld() {
         return TileEntityMobSpawner.this.world;
      }

      public BlockPos getSpawnerPosition() {
         return TileEntityMobSpawner.this.pos;
      }

      public void setNextSpawnData(WeightedSpawnerEntity nextSpawnData) {
         super.setNextSpawnData(nextSpawnData);
         if (this.getWorld() != null) {
            IBlockState iblockstate = this.getWorld().getBlockState(this.getSpawnerPosition());
            this.getWorld().notifyBlockUpdate(TileEntityMobSpawner.this.pos, iblockstate, iblockstate, 4);
         }

      }
   };

   public TileEntityMobSpawner() {
      super(TileEntityType.MOB_SPAWNER);
   }

   public void read(NBTTagCompound compound) {
      super.read(compound);
      this.spawnerLogic.readFromNBT(compound);
   }

   public NBTTagCompound write(NBTTagCompound compound) {
      super.write(compound);
      this.spawnerLogic.writeToNBT(compound);
      return compound;
   }

   public void tick() {
      this.spawnerLogic.tick();
   }

   /**
    * Retrieves packet to send to the client whenever this Tile Entity is resynced via World.notifyBlockUpdate. For
    * modded TE's, this packet comes back to you clientside in {@link #onDataPacket}
    */
   @Nullable
   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.pos, 1, this.getUpdateTag());
   }

   /**
    * Get an NBT compound to sync to the client with SPacketChunkData, used for initial loading of the chunk or when
    * many blocks change at once. This compound comes back to you clientside in {@link handleUpdateTag}
    */
   public NBTTagCompound getUpdateTag() {
      NBTTagCompound nbttagcompound = this.write(new NBTTagCompound());
      nbttagcompound.removeTag("SpawnPotentials");
      return nbttagcompound;
   }

   /**
    * See {@link Block#eventReceived} for more information. This must return true serverside before it is called
    * clientside.
    */
   public boolean receiveClientEvent(int id, int type) {
      return this.spawnerLogic.setDelayToMin(id) ? true : super.receiveClientEvent(id, type);
   }

   public boolean onlyOpsCanSetNbt() {
      return true;
   }

   public MobSpawnerBaseLogic getSpawnerBaseLogic() {
      return this.spawnerLogic;
   }
}