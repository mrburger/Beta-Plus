package net.minecraft.entity.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityMinecartMobSpawner extends EntityMinecart {
   /** Mob spawner logic for this spawner minecart. */
   private final MobSpawnerBaseLogic mobSpawnerLogic = new MobSpawnerBaseLogic() {
      public void broadcastEvent(int id) {
         EntityMinecartMobSpawner.this.world.setEntityState(EntityMinecartMobSpawner.this, (byte)id);
      }

      public World getWorld() {
         return EntityMinecartMobSpawner.this.world;
      }

      public BlockPos getSpawnerPosition() {
         return new BlockPos(EntityMinecartMobSpawner.this);
      }

      @Override
      @javax.annotation.Nullable
      public net.minecraft.entity.Entity getSpawnerEntity() {
         return EntityMinecartMobSpawner.this;
      }
   };

   public EntityMinecartMobSpawner(World worldIn) {
      super(EntityType.SPAWNER_MINECART, worldIn);
   }

   public EntityMinecartMobSpawner(World worldIn, double x, double y, double z) {
      super(EntityType.SPAWNER_MINECART, worldIn, x, y, z);
   }

   public EntityMinecart.Type getMinecartType() {
      return EntityMinecart.Type.SPAWNER;
   }

   public IBlockState getDefaultDisplayTile() {
      return Blocks.SPAWNER.getDefaultState();
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   protected void readAdditional(NBTTagCompound compound) {
      super.readAdditional(compound);
      this.mobSpawnerLogic.readFromNBT(compound);
   }

   /**
    * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
    * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
    */
   protected void writeAdditional(NBTTagCompound compound) {
      super.writeAdditional(compound);
      this.mobSpawnerLogic.writeToNBT(compound);
   }

   /**
    * Handler for {@link World#setEntityState}
    */
   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte id) {
      this.mobSpawnerLogic.setDelayToMin(id);
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
      this.mobSpawnerLogic.tick();
   }

   public boolean ignoreItemEntityData() {
      return true;
   }
}