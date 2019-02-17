package net.minecraft.entity.projectile;

import javax.annotation.Nullable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityTrident extends EntityArrow {
   private static final DataParameter<Byte> LOYALTY_LEVEL = EntityDataManager.createKey(EntityTrident.class, DataSerializers.BYTE);
   private ItemStack thrownStack = new ItemStack(Items.TRIDENT);
   private boolean dealtDamage;
   public int returningTicks;

   public EntityTrident(World p_i48789_1_) {
      super(EntityType.TRIDENT, p_i48789_1_);
   }

   public EntityTrident(World p_i48790_1_, EntityLivingBase p_i48790_2_, ItemStack p_i48790_3_) {
      super(EntityType.TRIDENT, p_i48790_2_, p_i48790_1_);
      this.thrownStack = p_i48790_3_.copy();
      this.dataManager.set(LOYALTY_LEVEL, (byte)EnchantmentHelper.getLoyaltyModifier(p_i48790_3_));
   }

   @OnlyIn(Dist.CLIENT)
   public EntityTrident(World p_i48791_1_, double p_i48791_2_, double p_i48791_4_, double p_i48791_6_) {
      super(EntityType.TRIDENT, p_i48791_2_, p_i48791_4_, p_i48791_6_, p_i48791_1_);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(LOYALTY_LEVEL, (byte)0);
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      if (this.timeInGround > 4) {
         this.dealtDamage = true;
      }

      Entity entity = this.func_212360_k();
      if ((this.dealtDamage || this.func_203047_q()) && entity != null) {
         int i = this.dataManager.get(LOYALTY_LEVEL);
         if (i > 0 && !this.shouldReturnToThrower()) {
            if (!this.world.isRemote && this.pickupStatus == EntityArrow.PickupStatus.ALLOWED) {
               this.entityDropItem(this.getArrowStack(), 0.1F);
            }

            this.remove();
         } else if (i > 0) {
            this.func_203045_n(true);
            Vec3d vec3d = new Vec3d(entity.posX - this.posX, entity.posY + (double)entity.getEyeHeight() - this.posY, entity.posZ - this.posZ);
            this.posY += vec3d.y * 0.015D * (double)i;
            if (this.world.isRemote) {
               this.lastTickPosY = this.posY;
            }

            vec3d = vec3d.normalize();
            double d0 = 0.05D * (double)i;
            this.motionX += vec3d.x * d0 - this.motionX * 0.05D;
            this.motionY += vec3d.y * d0 - this.motionY * 0.05D;
            this.motionZ += vec3d.z * d0 - this.motionZ * 0.05D;
            if (this.returningTicks == 0) {
               this.playSound(SoundEvents.ITEM_TRIDENT_RETURN, 10.0F, 1.0F);
            }

            ++this.returningTicks;
         }
      }

      super.tick();
   }

   private boolean shouldReturnToThrower() {
      Entity entity = this.func_212360_k();
      if (entity != null && entity.isAlive()) {
         return !(entity instanceof EntityPlayerMP) || !((EntityPlayerMP)entity).isSpectator();
      } else {
         return false;
      }
   }

   protected ItemStack getArrowStack() {
      return this.thrownStack.copy();
   }

   @Nullable
   protected Entity findEntityOnPath(Vec3d start, Vec3d end) {
      return this.dealtDamage ? null : super.findEntityOnPath(start, end);
   }

   protected void onHitEntity(RayTraceResult p_203046_1_) {
      Entity entity = p_203046_1_.entity;
      float f = 8.0F;
      if (entity instanceof EntityLivingBase) {
         EntityLivingBase entitylivingbase = (EntityLivingBase)entity;
         f += EnchantmentHelper.getModifierForCreature(this.thrownStack, entitylivingbase.getCreatureAttribute());
      }

      Entity entity1 = this.func_212360_k();
      DamageSource damagesource = DamageSource.causeTridentDamage(this, (Entity)(entity1 == null ? this : entity1));
      this.dealtDamage = true;
      SoundEvent soundevent = SoundEvents.ITEM_TRIDENT_HIT;
      if (entity.attackEntityFrom(damagesource, f) && entity instanceof EntityLivingBase) {
         EntityLivingBase entitylivingbase1 = (EntityLivingBase)entity;
         if (entity1 instanceof EntityLivingBase) {
            EnchantmentHelper.applyThornEnchantments(entitylivingbase1, entity1);
            EnchantmentHelper.applyArthropodEnchantments((EntityLivingBase)entity1, entitylivingbase1);
         }

         this.arrowHit(entitylivingbase1);
      }

      this.motionX *= (double)-0.01F;
      this.motionY *= (double)-0.1F;
      this.motionZ *= (double)-0.01F;
      float f1 = 1.0F;
      if (this.world.isThundering() && EnchantmentHelper.hasChanneling(this.thrownStack)) {
         BlockPos blockpos = entity.getPosition();
         if (this.world.canSeeSky(blockpos)) {
            EntityLightningBolt entitylightningbolt = new EntityLightningBolt(this.world, (double)blockpos.getX() + 0.5D, (double)blockpos.getY(), (double)blockpos.getZ() + 0.5D, false);
            entitylightningbolt.setCaster(entity1 instanceof EntityPlayerMP ? (EntityPlayerMP)entity1 : null);
            this.world.addWeatherEffect(entitylightningbolt);
            soundevent = SoundEvents.ITEM_TRIDENT_THUNDER;
            f1 = 5.0F;
         }
      }

      this.playSound(soundevent, f1, 1.0F);
   }

   protected SoundEvent getHitGroundSound() {
      return SoundEvents.ITEM_TRIDENT_HIT_GROUND;
   }

   /**
    * Called by a player entity when they collide with an entity
    */
   public void onCollideWithPlayer(EntityPlayer entityIn) {
      Entity entity = this.func_212360_k();
      if (entity == null || entity.getUniqueID() == entityIn.getUniqueID()) {
         super.onCollideWithPlayer(entityIn);
      }
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(NBTTagCompound compound) {
      super.readAdditional(compound);
      if (compound.contains("Trident", 10)) {
         this.thrownStack = ItemStack.read(compound.getCompound("Trident"));
      }

      this.dealtDamage = compound.getBoolean("DealtDamage");
      this.dataManager.set(LOYALTY_LEVEL, (byte)EnchantmentHelper.getLoyaltyModifier(this.thrownStack));
   }

   /**
    * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
    * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
    */
   public void writeAdditional(NBTTagCompound compound) {
      super.writeAdditional(compound);
      compound.setTag("Trident", this.thrownStack.write(new NBTTagCompound()));
      compound.setBoolean("DealtDamage", this.dealtDamage);
   }

   protected void tryDespawn() {
      int i = this.dataManager.get(LOYALTY_LEVEL);
      if (this.pickupStatus != EntityArrow.PickupStatus.ALLOWED || i <= 0) {
         super.tryDespawn();
      }

   }

   protected float getWaterDrag() {
      return 0.99F;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRender3d(double x, double y, double z) {
      return true;
   }
}