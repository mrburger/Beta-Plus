package net.minecraft.entity.player;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PlayerCapabilities {
   /** Disables player damage. */
   public boolean disableDamage;
   /** Sets/indicates whether the player is flying. */
   public boolean isFlying;
   /** whether or not to allow the player to fly when they double jump. */
   public boolean allowFlying;
   /** Used to determine if creative mode is enabled, and therefore if items should be depleted on usage */
   public boolean isCreativeMode;
   /** Indicates whether the player is allowed to modify the surroundings */
   public boolean allowEdit = true;
   private double flySpeed = (double)0.05F;
   private float walkSpeed = 0.1F;

   public void write(NBTTagCompound tagCompound) {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.setBoolean("invulnerable", this.disableDamage);
      nbttagcompound.setBoolean("flying", this.isFlying);
      nbttagcompound.setBoolean("mayfly", this.allowFlying);
      nbttagcompound.setBoolean("instabuild", this.isCreativeMode);
      nbttagcompound.setBoolean("mayBuild", this.allowEdit);
      nbttagcompound.setFloat("flySpeed", (float)this.flySpeed);
      nbttagcompound.setFloat("walkSpeed", this.walkSpeed);
      tagCompound.setTag("abilities", nbttagcompound);
   }

   public void read(NBTTagCompound tagCompound) {
      if (tagCompound.contains("abilities", 10)) {
         NBTTagCompound nbttagcompound = tagCompound.getCompound("abilities");
         this.disableDamage = nbttagcompound.getBoolean("invulnerable");
         this.isFlying = nbttagcompound.getBoolean("flying");
         this.allowFlying = nbttagcompound.getBoolean("mayfly");
         this.isCreativeMode = nbttagcompound.getBoolean("instabuild");
         if (nbttagcompound.contains("flySpeed", 99)) {
            this.flySpeed = (double)nbttagcompound.getFloat("flySpeed");
            this.walkSpeed = nbttagcompound.getFloat("walkSpeed");
         }

         if (nbttagcompound.contains("mayBuild", 1)) {
            this.allowEdit = nbttagcompound.getBoolean("mayBuild");
         }
      }

   }

   public float getFlySpeed() {
      return (float)this.flySpeed;
   }

   @OnlyIn(Dist.CLIENT)
   public void setFlySpeed(double p_195931_1_) {
      this.flySpeed = p_195931_1_;
   }

   public float getWalkSpeed() {
      return this.walkSpeed;
   }

   @OnlyIn(Dist.CLIENT)
   public void setWalkSpeed(float speed) {
      this.walkSpeed = speed;
   }
}