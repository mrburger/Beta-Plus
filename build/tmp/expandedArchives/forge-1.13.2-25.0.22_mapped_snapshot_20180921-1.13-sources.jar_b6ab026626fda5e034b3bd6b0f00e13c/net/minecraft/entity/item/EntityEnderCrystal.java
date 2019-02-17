package net.minecraft.entity.item;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.EndDimension;
import net.minecraft.world.end.DragonFightManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityEnderCrystal extends Entity {
   private static final DataParameter<Optional<BlockPos>> BEAM_TARGET = EntityDataManager.createKey(EntityEnderCrystal.class, DataSerializers.OPTIONAL_BLOCK_POS);
   private static final DataParameter<Boolean> SHOW_BOTTOM = EntityDataManager.createKey(EntityEnderCrystal.class, DataSerializers.BOOLEAN);
   /** Used to create the rotation animation when rendering the crystal. */
   public int innerRotation;

   public EntityEnderCrystal(World worldIn) {
      super(EntityType.END_CRYSTAL, worldIn);
      this.preventEntitySpawning = true;
      this.setSize(2.0F, 2.0F);
      this.innerRotation = this.rand.nextInt(100000);
   }

   public EntityEnderCrystal(World worldIn, double x, double y, double z) {
      this(worldIn);
      this.setPosition(x, y, z);
   }

   /**
    * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
    * prevent them from trampling crops
    */
   protected boolean canTriggerWalking() {
      return false;
   }

   protected void registerData() {
      this.getDataManager().register(BEAM_TARGET, Optional.empty());
      this.getDataManager().register(SHOW_BOTTOM, true);
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      ++this.innerRotation;
      if (!this.world.isRemote) {
         BlockPos blockpos = new BlockPos(this);
         if (this.world.dimension instanceof EndDimension && this.world.getBlockState(blockpos).isAir()) {
            this.world.setBlockState(blockpos, Blocks.FIRE.getDefaultState());
         }
      }

   }

   /**
    * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
    * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
    */
   protected void writeAdditional(NBTTagCompound compound) {
      if (this.getBeamTarget() != null) {
         compound.setTag("BeamTarget", NBTUtil.writeBlockPos(this.getBeamTarget()));
      }

      compound.setBoolean("ShowBottom", this.shouldShowBottom());
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   protected void readAdditional(NBTTagCompound compound) {
      if (compound.contains("BeamTarget", 10)) {
         this.setBeamTarget(NBTUtil.readBlockPos(compound.getCompound("BeamTarget")));
      }

      if (compound.contains("ShowBottom", 1)) {
         this.setShowBottom(compound.getBoolean("ShowBottom"));
      }

   }

   /**
    * Returns true if other Entities should be prevented from moving through this Entity.
    */
   public boolean canBeCollidedWith() {
      return true;
   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isInvulnerableTo(source)) {
         return false;
      } else if (source.getTrueSource() instanceof EntityDragon) {
         return false;
      } else {
         if (!this.removed && !this.world.isRemote) {
            this.remove();
            if (!this.world.isRemote) {
               if (!source.isExplosion()) {
                  this.world.createExplosion((Entity)null, this.posX, this.posY, this.posZ, 6.0F, true);
               }

               this.onCrystalDestroyed(source);
            }
         }

         return true;
      }
   }

   /**
    * Called by the /kill command.
    */
   public void onKillCommand() {
      this.onCrystalDestroyed(DamageSource.GENERIC);
      super.onKillCommand();
   }

   private void onCrystalDestroyed(DamageSource source) {
      if (this.world.dimension instanceof EndDimension) {
         EndDimension enddimension = (EndDimension)this.world.dimension;
         DragonFightManager dragonfightmanager = enddimension.getDragonFightManager();
         if (dragonfightmanager != null) {
            dragonfightmanager.onCrystalDestroyed(this, source);
         }
      }

   }

   public void setBeamTarget(@Nullable BlockPos beamTarget) {
      this.getDataManager().set(BEAM_TARGET, Optional.ofNullable(beamTarget));
   }

   @Nullable
   public BlockPos getBeamTarget() {
      return this.getDataManager().get(BEAM_TARGET).orElse((BlockPos)null);
   }

   public void setShowBottom(boolean showBottom) {
      this.getDataManager().set(SHOW_BOTTOM, showBottom);
   }

   public boolean shouldShowBottom() {
      return this.getDataManager().get(SHOW_BOTTOM);
   }

   /**
    * Checks if the entity is in range to render.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRenderDist(double distance) {
      return super.isInRangeToRenderDist(distance) || this.getBeamTarget() != null;
   }
}