package net.minecraft.entity.passive;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class EntityAnimal extends EntityAgeable implements IAnimal {
   protected Block spawnableBlock = Blocks.GRASS_BLOCK;
   private int inLove;
   private UUID playerInLove;

   protected EntityAnimal(EntityType<?> type, World p_i48568_2_) {
      super(type, p_i48568_2_);
   }

   protected void updateAITasks() {
      if (this.getGrowingAge() != 0) {
         this.inLove = 0;
      }

      super.updateAITasks();
   }

   /**
    * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
    * use this to react to sunlight and start to burn.
    */
   public void livingTick() {
      super.livingTick();
      if (this.getGrowingAge() != 0) {
         this.inLove = 0;
      }

      if (this.inLove > 0) {
         --this.inLove;
         if (this.inLove % 10 == 0) {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            this.world.spawnParticle(Particles.HEART, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
         }
      }

   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isInvulnerableTo(source)) {
         return false;
      } else {
         this.inLove = 0;
         return super.attackEntityFrom(source, amount);
      }
   }

   public float getBlockPathWeight(BlockPos p_205022_1_, IWorldReaderBase worldIn) {
      return worldIn.getBlockState(p_205022_1_.down()).getBlock() == this.spawnableBlock ? 10.0F : worldIn.getBrightness(p_205022_1_) - 0.5F;
   }

   /**
    * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
    * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
    */
   public void writeAdditional(NBTTagCompound compound) {
      super.writeAdditional(compound);
      compound.setInt("InLove", this.inLove);
      if (this.playerInLove != null) {
         compound.setUniqueId("LoveCause", this.playerInLove);
      }

   }

   /**
    * Returns the Y Offset of this entity.
    */
   public double getYOffset() {
      return 0.14D;
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(NBTTagCompound compound) {
      super.readAdditional(compound);
      this.inLove = compound.getInt("InLove");
      this.playerInLove = compound.hasUniqueId("LoveCause") ? compound.getUniqueId("LoveCause") : null;
   }

   public boolean canSpawn(IWorld worldIn, boolean p_205020_2_) {
      int i = MathHelper.floor(this.posX);
      int j = MathHelper.floor(this.getBoundingBox().minY);
      int k = MathHelper.floor(this.posZ);
      BlockPos blockpos = new BlockPos(i, j, k);
      return worldIn.getBlockState(blockpos.down()).getBlock() == this.spawnableBlock && worldIn.getLightSubtracted(blockpos, 0) > 8 && super.canSpawn(worldIn, p_205020_2_);
   }

   /**
    * Get number of ticks, at least during which the living entity will be silent.
    */
   public int getTalkInterval() {
      return 120;
   }

   /**
    * Determines if an entity can be despawned, used on idle far away entities
    */
   public boolean canDespawn() {
      return false;
   }

   /**
    * Get the experience points the entity currently has.
    */
   protected int getExperiencePoints(EntityPlayer player) {
      return 1 + this.world.rand.nextInt(3);
   }

   /**
    * Checks if the parameter is an item which this animal can be fed to breed it (wheat, carrots or seeds depending on
    * the animal type)
    */
   public boolean isBreedingItem(ItemStack stack) {
      return stack.getItem() == Items.WHEAT;
   }

   public boolean processInteract(EntityPlayer player, EnumHand hand) {
      ItemStack itemstack = player.getHeldItem(hand);
      if (this.isBreedingItem(itemstack)) {
         if (this.getGrowingAge() == 0 && this.canBreed()) {
            this.consumeItemFromStack(player, itemstack);
            this.setInLove(player);
            return true;
         }

         if (this.isChild()) {
            this.consumeItemFromStack(player, itemstack);
            this.ageUp((int)((float)(-this.getGrowingAge() / 20) * 0.1F), true);
            return true;
         }
      }

      return super.processInteract(player, hand);
   }

   /**
    * Decreases ItemStack size by one
    */
   protected void consumeItemFromStack(EntityPlayer player, ItemStack stack) {
      if (!player.abilities.isCreativeMode) {
         stack.shrink(1);
      }

   }

   public boolean canBreed() {
      return this.inLove <= 0;
   }

   public void setInLove(@Nullable EntityPlayer player) {
      this.inLove = 600;
      if (player != null) {
         this.playerInLove = player.getUniqueID();
      }

      this.world.setEntityState(this, (byte)18);
   }

   public void func_204700_e(int p_204700_1_) {
      this.inLove = p_204700_1_;
   }

   @Nullable
   public EntityPlayerMP getLoveCause() {
      if (this.playerInLove == null) {
         return null;
      } else {
         EntityPlayer entityplayer = this.world.getPlayerEntityByUUID(this.playerInLove);
         return entityplayer instanceof EntityPlayerMP ? (EntityPlayerMP)entityplayer : null;
      }
   }

   /**
    * Returns if the entity is currently in 'love mode'.
    */
   public boolean isInLove() {
      return this.inLove > 0;
   }

   public void resetInLove() {
      this.inLove = 0;
   }

   /**
    * Returns true if the mob is currently able to mate with the specified mob.
    */
   public boolean canMateWith(EntityAnimal otherAnimal) {
      if (otherAnimal == this) {
         return false;
      } else if (otherAnimal.getClass() != this.getClass()) {
         return false;
      } else {
         return this.isInLove() && otherAnimal.isInLove();
      }
   }

   /**
    * Handler for {@link World#setEntityState}
    */
   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 18) {
         for(int i = 0; i < 7; ++i) {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            this.world.spawnParticle(Particles.HEART, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
         }
      } else {
         super.handleStatusUpdate(id);
      }

   }
}