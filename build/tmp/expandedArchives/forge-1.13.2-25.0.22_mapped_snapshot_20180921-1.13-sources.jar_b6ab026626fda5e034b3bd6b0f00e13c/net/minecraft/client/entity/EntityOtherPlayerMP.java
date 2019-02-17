package net.minecraft.client.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EntityOtherPlayerMP extends AbstractClientPlayer {
   public EntityOtherPlayerMP(World worldIn, GameProfile gameProfileIn) {
      super(worldIn, gameProfileIn);
      this.stepHeight = 1.0F;
      this.noClip = true;
      this.renderOffsetY = 0.25F;
   }

   /**
    * Checks if the entity is in range to render.
    */
   public boolean isInRangeToRenderDist(double distance) {
      double d0 = this.getBoundingBox().getAverageEdgeLength() * 10.0D;
      if (Double.isNaN(d0)) {
         d0 = 1.0D;
      }

      d0 = d0 * 64.0D * getRenderDistanceWeight();
      return distance < d0 * d0;
   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      net.minecraftforge.common.ForgeHooks.onPlayerAttack(this, source, amount);
      return true;
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      this.renderOffsetY = 0.0F;
      super.tick();
      this.prevLimbSwingAmount = this.limbSwingAmount;
      double d0 = this.posX - this.prevPosX;
      double d1 = this.posZ - this.prevPosZ;
      float f = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;
      if (f > 1.0F) {
         f = 1.0F;
      }

      this.limbSwingAmount += (f - this.limbSwingAmount) * 0.4F;
      this.limbSwing += this.limbSwingAmount;
   }

   /**
    * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
    * use this to react to sunlight and start to burn.
    */
   public void livingTick() {
      if (this.newPosRotationIncrements > 0) {
         double d0 = this.posX + (this.interpTargetX - this.posX) / (double)this.newPosRotationIncrements;
         double d1 = this.posY + (this.interpTargetY - this.posY) / (double)this.newPosRotationIncrements;
         double d2 = this.posZ + (this.interpTargetZ - this.posZ) / (double)this.newPosRotationIncrements;
         this.rotationYaw = (float)((double)this.rotationYaw + MathHelper.wrapDegrees(this.interpTargetYaw - (double)this.rotationYaw) / (double)this.newPosRotationIncrements);
         this.rotationPitch = (float)((double)this.rotationPitch + (this.interpTargetPitch - (double)this.rotationPitch) / (double)this.newPosRotationIncrements);
         --this.newPosRotationIncrements;
         this.setPosition(d0, d1, d2);
         this.setRotation(this.rotationYaw, this.rotationPitch);
      }

      if (this.field_208002_br > 0) {
         this.rotationYawHead = (float)((double)this.rotationYawHead + MathHelper.wrapDegrees(this.field_208001_bq - (double)this.rotationYawHead) / (double)this.field_208002_br);
         --this.field_208002_br;
      }

      this.prevCameraYaw = this.cameraYaw;
      this.updateArmSwingProgress();
      float f1 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
      float f = (float)Math.atan(-this.motionY * (double)0.2F) * 15.0F;
      if (f1 > 0.1F) {
         f1 = 0.1F;
      }

      if (!this.onGround || this.getHealth() <= 0.0F) {
         f1 = 0.0F;
      }

      if (this.onGround || this.getHealth() <= 0.0F) {
         f = 0.0F;
      }

      this.cameraYaw += (f1 - this.cameraYaw) * 0.4F;
      this.cameraPitch += (f - this.cameraPitch) * 0.8F;
      this.world.profiler.startSection("push");
      this.collideWithNearbyEntities();
      this.world.profiler.endSection();
   }

   /**
    * Send a chat message to the CommandSender
    */
   public void sendMessage(ITextComponent component) {
      Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage(component);
   }
}