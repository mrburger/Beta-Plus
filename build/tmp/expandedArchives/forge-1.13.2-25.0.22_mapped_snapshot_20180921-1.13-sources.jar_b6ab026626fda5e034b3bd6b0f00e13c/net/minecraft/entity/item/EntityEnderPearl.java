package net.minecraft.entity.item;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Particles;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityEnderPearl extends EntityThrowable {
   private EntityLivingBase perlThrower;

   public EntityEnderPearl(World worldIn) {
      super(EntityType.ENDER_PEARL, worldIn);
   }

   public EntityEnderPearl(World worldIn, EntityLivingBase throwerIn) {
      super(EntityType.ENDER_PEARL, throwerIn, worldIn);
      this.perlThrower = throwerIn;
   }

   @OnlyIn(Dist.CLIENT)
   public EntityEnderPearl(World worldIn, double x, double y, double z) {
      super(EntityType.ENDER_PEARL, x, y, z, worldIn);
   }

   /**
    * Called when this EntityThrowable hits a block or entity.
    */
   protected void onImpact(RayTraceResult result) {
      EntityLivingBase entitylivingbase = this.getThrower();
      if (result.entity != null) {
         if (result.entity == this.perlThrower) {
            return;
         }

         result.entity.attackEntityFrom(DamageSource.causeThrownDamage(this, entitylivingbase), 0.0F);
      }

      if (result.type == RayTraceResult.Type.BLOCK) {
         BlockPos blockpos = result.getBlockPos();
         TileEntity tileentity = this.world.getTileEntity(blockpos);
         if (tileentity instanceof TileEntityEndGateway) {
            TileEntityEndGateway tileentityendgateway = (TileEntityEndGateway)tileentity;
            if (entitylivingbase != null) {
               if (entitylivingbase instanceof EntityPlayerMP) {
                  CriteriaTriggers.ENTER_BLOCK.trigger((EntityPlayerMP)entitylivingbase, this.world.getBlockState(blockpos));
               }

               tileentityendgateway.teleportEntity(entitylivingbase);
               this.remove();
               return;
            }

            tileentityendgateway.teleportEntity(this);
            return;
         }
      }

      for(int i = 0; i < 32; ++i) {
         this.world.spawnParticle(Particles.PORTAL, this.posX, this.posY + this.rand.nextDouble() * 2.0D, this.posZ, this.rand.nextGaussian(), 0.0D, this.rand.nextGaussian());
      }

      if (!this.world.isRemote) {
         if (entitylivingbase instanceof EntityPlayerMP) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP)entitylivingbase;
            if (entityplayermp.connection.getNetworkManager().isChannelOpen() && entityplayermp.world == this.world && !entityplayermp.isPlayerSleeping()) {

               net.minecraftforge.event.entity.living.EnderTeleportEvent event = new net.minecraftforge.event.entity.living.EnderTeleportEvent(entityplayermp, this.posX, this.posY, this.posZ, 5.0F);
               if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) { // Don't indent to lower patch size
               if (this.rand.nextFloat() < 0.05F && this.world.getGameRules().getBoolean("doMobSpawning")) {
                  EntityEndermite entityendermite = new EntityEndermite(this.world);
                  entityendermite.setSpawnedByPlayer(true);
                  entityendermite.setLocationAndAngles(entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ, entitylivingbase.rotationYaw, entitylivingbase.rotationPitch);
                  this.world.spawnEntity(entityendermite);
               }

               if (entitylivingbase.isPassenger()) {
                  entitylivingbase.stopRiding();
               }

               entitylivingbase.setPositionAndUpdate(event.getTargetX(), event.getTargetY(), event.getTargetZ());
               entitylivingbase.fallDistance = 0.0F;
               entitylivingbase.attackEntityFrom(DamageSource.FALL, event.getAttackDamage());
               }
            }
         } else if (entitylivingbase != null) {
            entitylivingbase.setPositionAndUpdate(this.posX, this.posY, this.posZ);
            entitylivingbase.fallDistance = 0.0F;
         }

         this.remove();
      }

   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      EntityLivingBase entitylivingbase = this.getThrower();
      if (entitylivingbase != null && entitylivingbase instanceof EntityPlayer && !entitylivingbase.isAlive()) {
         this.remove();
      } else {
         super.tick();
      }

   }

   @Override
   @Nullable
   public Entity changeDimension(DimensionType p_212321_1_, net.minecraftforge.common.util.ITeleporter teleporter) {
      if (this.thrower.dimension != p_212321_1_) {
         this.thrower = null;
      }

      return super.changeDimension(p_212321_1_, teleporter);
   }
}