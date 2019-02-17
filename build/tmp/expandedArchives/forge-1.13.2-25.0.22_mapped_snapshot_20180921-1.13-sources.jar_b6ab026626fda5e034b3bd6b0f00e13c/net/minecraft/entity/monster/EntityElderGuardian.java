package net.minecraft.entity.monster;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityElderGuardian extends EntityGuardian {
   public EntityElderGuardian(World worldIn) {
      super(EntityType.ELDER_GUARDIAN, worldIn);
      this.setSize(this.width * 2.35F, this.height * 2.35F);
      this.enablePersistence();
      if (this.wander != null) {
         this.wander.setExecutionChance(400);
      }

   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.3F);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(8.0D);
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(80.0D);
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_ELDER_GUARDIAN;
   }

   public int getAttackDuration() {
      return 60;
   }

   @OnlyIn(Dist.CLIENT)
   public void setGhost() {
      this.clientSideSpikesAnimation = 1.0F;
      this.clientSideSpikesAnimationO = this.clientSideSpikesAnimation;
   }

   protected SoundEvent getAmbientSound() {
      return this.isInWaterOrBubbleColumn() ? SoundEvents.ENTITY_ELDER_GUARDIAN_AMBIENT : SoundEvents.ENTITY_ELDER_GUARDIAN_AMBIENT_LAND;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return this.isInWaterOrBubbleColumn() ? SoundEvents.ENTITY_ELDER_GUARDIAN_HURT : SoundEvents.ENTITY_ELDER_GUARDIAN_HURT_LAND;
   }

   protected SoundEvent getDeathSound() {
      return this.isInWaterOrBubbleColumn() ? SoundEvents.ENTITY_ELDER_GUARDIAN_DEATH : SoundEvents.ENTITY_ELDER_GUARDIAN_DEATH_LAND;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.ENTITY_ELDER_GUARDIAN_FLOP;
   }

   protected void updateAITasks() {
      super.updateAITasks();
      int i = 1200;
      if ((this.ticksExisted + this.getEntityId()) % 1200 == 0) {
         Potion potion = MobEffects.MINING_FATIGUE;
         List<EntityPlayerMP> list = this.world.getPlayers(EntityPlayerMP.class, (p_210138_1_) -> {
            return this.getDistanceSq(p_210138_1_) < 2500.0D && p_210138_1_.interactionManager.survivalOrAdventure();
         });
         int j = 2;
         int k = 6000;
         int l = 1200;

         for(EntityPlayerMP entityplayermp : list) {
            if (!entityplayermp.isPotionActive(potion) || entityplayermp.getActivePotionEffect(potion).getAmplifier() < 2 || entityplayermp.getActivePotionEffect(potion).getDuration() < 1200) {
               entityplayermp.connection.sendPacket(new SPacketChangeGameState(10, 0.0F));
               entityplayermp.addPotionEffect(new PotionEffect(potion, 6000, 2));
            }
         }
      }

      if (!this.hasHome()) {
         this.setHomePosAndDistance(new BlockPos(this), 16);
      }

   }
}