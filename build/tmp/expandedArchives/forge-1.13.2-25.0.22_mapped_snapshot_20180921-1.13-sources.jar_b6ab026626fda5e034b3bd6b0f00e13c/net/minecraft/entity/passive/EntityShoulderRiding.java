package net.minecraft.entity.passive;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public abstract class EntityShoulderRiding extends EntityTameable {
   private int rideCooldownCounter;

   protected EntityShoulderRiding(EntityType<?> type, World p_i48566_2_) {
      super(type, p_i48566_2_);
   }

   public boolean setEntityOnShoulder(EntityPlayer p_191994_1_) {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.setString("id", this.getEntityString());
      this.writeWithoutTypeId(nbttagcompound);
      if (p_191994_1_.addShoulderEntity(nbttagcompound)) {
         this.world.removeEntity(this);
         return true;
      } else {
         return false;
      }
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      ++this.rideCooldownCounter;
      super.tick();
   }

   public boolean canSitOnShoulder() {
      return this.rideCooldownCounter > 100;
   }
}