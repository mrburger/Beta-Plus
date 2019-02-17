package net.minecraft.entity.item;

import net.minecraft.block.BlockFurnace;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityMinecartFurnace extends EntityMinecart {
   private static final DataParameter<Boolean> POWERED = EntityDataManager.createKey(EntityMinecartFurnace.class, DataSerializers.BOOLEAN);
   private int fuel;
   public double pushX;
   public double pushZ;
   private static final Ingredient field_195407_e = Ingredient.fromItems(Items.COAL, Items.CHARCOAL);

   public EntityMinecartFurnace(World worldIn) {
      super(EntityType.FURNACE_MINECART, worldIn);
   }

   public EntityMinecartFurnace(World worldIn, double x, double y, double z) {
      super(EntityType.FURNACE_MINECART, worldIn, x, y, z);
   }

   public EntityMinecart.Type getMinecartType() {
      return EntityMinecart.Type.FURNACE;
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(POWERED, false);
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
      if (this.fuel > 0) {
         --this.fuel;
      }

      if (this.fuel <= 0) {
         this.pushX = 0.0D;
         this.pushZ = 0.0D;
      }

      this.setMinecartPowered(this.fuel > 0);
      if (this.isMinecartPowered() && this.rand.nextInt(4) == 0) {
         this.world.spawnParticle(Particles.LARGE_SMOKE, this.posX, this.posY + 0.8D, this.posZ, 0.0D, 0.0D, 0.0D);
      }

   }

   /**
    * Get's the maximum speed for a minecart
    */
   protected double getMaximumSpeed() {
      return 0.2D;
   }

   public void killMinecart(DamageSource source) {
      super.killMinecart(source);
      if (!source.isExplosion() && this.world.getGameRules().getBoolean("doEntityDrops")) {
         this.entityDropItem(Blocks.FURNACE);
      }

   }

   protected void moveAlongTrack(BlockPos pos, IBlockState state) {
      super.moveAlongTrack(pos, state);
      double d0 = this.pushX * this.pushX + this.pushZ * this.pushZ;
      if (d0 > 1.0E-4D && this.motionX * this.motionX + this.motionZ * this.motionZ > 0.001D) {
         d0 = (double)MathHelper.sqrt(d0);
         this.pushX /= d0;
         this.pushZ /= d0;
         if (this.pushX * this.motionX + this.pushZ * this.motionZ < 0.0D) {
            this.pushX = 0.0D;
            this.pushZ = 0.0D;
         } else {
            double d1 = d0 / this.getMaximumSpeed();
            this.pushX *= d1;
            this.pushZ *= d1;
         }
      }

   }

   protected void applyDrag() {
      double d0 = this.pushX * this.pushX + this.pushZ * this.pushZ;
      if (d0 > 1.0E-4D) {
         d0 = (double)MathHelper.sqrt(d0);
         this.pushX /= d0;
         this.pushZ /= d0;
         double d1 = 1.0D;
         this.motionX *= (double)0.8F;
         this.motionY *= 0.0D;
         this.motionZ *= (double)0.8F;
         this.motionX += this.pushX * 1.0D;
         this.motionZ += this.pushZ * 1.0D;
      } else {
         this.motionX *= (double)0.98F;
         this.motionY *= 0.0D;
         this.motionZ *= (double)0.98F;
      }

      super.applyDrag();
   }

   public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
      if (super.processInitialInteract(player, hand)) return true;
      ItemStack itemstack = player.getHeldItem(hand);
      if (field_195407_e.test(itemstack) && this.fuel + 3600 <= 32000) {
         if (!player.abilities.isCreativeMode) {
            itemstack.shrink(1);
         }

         this.fuel += 3600;
      }

      this.pushX = this.posX - player.posX;
      this.pushZ = this.posZ - player.posZ;
      return true;
   }

   /**
    * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
    * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
    */
   protected void writeAdditional(NBTTagCompound compound) {
      super.writeAdditional(compound);
      compound.setDouble("PushX", this.pushX);
      compound.setDouble("PushZ", this.pushZ);
      compound.setShort("Fuel", (short)this.fuel);
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   protected void readAdditional(NBTTagCompound compound) {
      super.readAdditional(compound);
      this.pushX = compound.getDouble("PushX");
      this.pushZ = compound.getDouble("PushZ");
      this.fuel = compound.getShort("Fuel");
   }

   protected boolean isMinecartPowered() {
      return this.dataManager.get(POWERED);
   }

   protected void setMinecartPowered(boolean p_94107_1_) {
      this.dataManager.set(POWERED, p_94107_1_);
   }

   public IBlockState getDefaultDisplayTile() {
      return Blocks.FURNACE.getDefaultState().with(BlockFurnace.FACING, EnumFacing.NORTH).with(BlockFurnace.LIT, Boolean.valueOf(this.isMinecartPowered()));
   }
}