package net.minecraft.entity.item;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.INameable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class EntityMinecart extends Entity implements INameable, net.minecraftforge.common.extensions.IForgeEntityMinecart {
   private static final DataParameter<Integer> ROLLING_AMPLITUDE = EntityDataManager.createKey(EntityMinecart.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> ROLLING_DIRECTION = EntityDataManager.createKey(EntityMinecart.class, DataSerializers.VARINT);
   private static final DataParameter<Float> DAMAGE = EntityDataManager.createKey(EntityMinecart.class, DataSerializers.FLOAT);
   private static final DataParameter<Integer> DISPLAY_TILE = EntityDataManager.createKey(EntityMinecart.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> DISPLAY_TILE_OFFSET = EntityDataManager.createKey(EntityMinecart.class, DataSerializers.VARINT);
   private static final DataParameter<Boolean> SHOW_BLOCK = EntityDataManager.createKey(EntityMinecart.class, DataSerializers.BOOLEAN);
   private boolean isInReverse;
   /** Minecart rotational logic matrix */
   private static final int[][][] MATRIX = new int[][][]{{{0, 0, -1}, {0, 0, 1}}, {{-1, 0, 0}, {1, 0, 0}}, {{-1, -1, 0}, {1, 0, 0}}, {{-1, 0, 0}, {1, -1, 0}}, {{0, 0, -1}, {0, -1, 1}}, {{0, -1, -1}, {0, 0, 1}}, {{0, 0, 1}, {1, 0, 0}}, {{0, 0, 1}, {-1, 0, 0}}, {{0, 0, -1}, {-1, 0, 0}}, {{0, 0, -1}, {1, 0, 0}}};
   /** appears to be the progress of the turn */
   private int turnProgress;
   private double minecartX;
   private double minecartY;
   private double minecartZ;
   private double minecartYaw;
   private double minecartPitch;
   @OnlyIn(Dist.CLIENT)
   private double velocityX;
   @OnlyIn(Dist.CLIENT)
   private double velocityY;
   @OnlyIn(Dist.CLIENT)
   private double velocityZ;
   private boolean canBePushed = true;

   protected EntityMinecart(EntityType<?> type, World p_i48538_2_) {
      super(type, p_i48538_2_);
      this.preventEntitySpawning = true;
      this.setSize(0.98F, 0.7F);
   }

   protected EntityMinecart(EntityType<?> type, World p_i48539_2_, double p_i48539_3_, double p_i48539_5_, double p_i48539_7_) {
      this(type, p_i48539_2_);
      this.setPosition(p_i48539_3_, p_i48539_5_, p_i48539_7_);
      this.motionX = 0.0D;
      this.motionY = 0.0D;
      this.motionZ = 0.0D;
      this.prevPosX = p_i48539_3_;
      this.prevPosY = p_i48539_5_;
      this.prevPosZ = p_i48539_7_;
   }

   public static EntityMinecart create(World worldIn, double x, double y, double z, EntityMinecart.Type typeIn) {
      switch(typeIn) {
      case CHEST:
         return new EntityMinecartChest(worldIn, x, y, z);
      case FURNACE:
         return new EntityMinecartFurnace(worldIn, x, y, z);
      case TNT:
         return new EntityMinecartTNT(worldIn, x, y, z);
      case SPAWNER:
         return new EntityMinecartMobSpawner(worldIn, x, y, z);
      case HOPPER:
         return new EntityMinecartHopper(worldIn, x, y, z);
      case COMMAND_BLOCK:
         return new EntityMinecartCommandBlock(worldIn, x, y, z);
      default:
         return new EntityMinecartEmpty(worldIn, x, y, z);
      }
   }

   /**
    * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
    * prevent them from trampling crops
    */
   protected boolean canTriggerWalking() {
      return false;
   }

   protected void registerData() {
      this.dataManager.register(ROLLING_AMPLITUDE, 0);
      this.dataManager.register(ROLLING_DIRECTION, 1);
      this.dataManager.register(DAMAGE, 0.0F);
      this.dataManager.register(DISPLAY_TILE, Block.getStateId(Blocks.AIR.getDefaultState()));
      this.dataManager.register(DISPLAY_TILE_OFFSET, 6);
      this.dataManager.register(SHOW_BLOCK, false);
   }

   /**
    * Returns a boundingBox used to collide the entity with other entities and blocks. This enables the entity to be
    * pushable on contact, like boats or minecarts.
    */
   @Nullable
   public AxisAlignedBB getCollisionBox(Entity entityIn) {
      if (getCollisionHandler() != null) return getCollisionHandler().getCollisionBox(this, entityIn);
      return entityIn.canBePushed() ? entityIn.getBoundingBox() : null;
   }

   /**
    * Returns true if this entity should push and be pushed by other entities when colliding.
    */
   public boolean canBePushed() {
      return canBePushed;
   }

   /**
    * Returns the Y offset from the entity's position for any entity riding this one.
    */
   public double getMountedYOffset() {
      return 0.0D;
   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (!this.world.isRemote && !this.removed) {
         if (this.isInvulnerableTo(source)) {
            return false;
         } else {
            this.setRollingDirection(-this.getRollingDirection());
            this.setRollingAmplitude(10);
            this.markVelocityChanged();
            this.setDamage(this.getDamage() + amount * 10.0F);
            boolean flag = source.getTrueSource() instanceof EntityPlayer && ((EntityPlayer)source.getTrueSource()).abilities.isCreativeMode;
            if (flag || this.getDamage() > 40.0F) {
               this.removePassengers();
               if (flag && !this.hasCustomName()) {
                  this.remove();
               } else {
                  this.killMinecart(source);
               }
            }

            return true;
         }
      } else {
         return true;
      }
   }

   public void killMinecart(DamageSource source) {
      this.remove();
      if (this.world.getGameRules().getBoolean("doEntityDrops")) {
         ItemStack itemstack = new ItemStack(Items.MINECART);
         if (this.hasCustomName()) {
            itemstack.setDisplayName(this.getCustomName());
         }

         this.entityDropItem(itemstack);
      }

   }

   /**
    * Setups the entity to do the hurt animation. Only used by packets in multiplayer.
    */
   @OnlyIn(Dist.CLIENT)
   public void performHurtAnimation() {
      this.setRollingDirection(-this.getRollingDirection());
      this.setRollingAmplitude(10);
      this.setDamage(this.getDamage() + this.getDamage() * 10.0F);
   }

   /**
    * Returns true if other Entities should be prevented from moving through this Entity.
    */
   public boolean canBeCollidedWith() {
      return !this.removed;
   }

   /**
    * Gets the horizontal facing direction of this Entity, adjusted to take specially-treated entity types into account.
    */
   public EnumFacing getAdjustedHorizontalFacing() {
      return this.isInReverse ? this.getHorizontalFacing().getOpposite().rotateY() : this.getHorizontalFacing().rotateY();
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      if (this.getRollingAmplitude() > 0) {
         this.setRollingAmplitude(this.getRollingAmplitude() - 1);
      }

      if (this.getDamage() > 0.0F) {
         this.setDamage(this.getDamage() - 1.0F);
      }

      if (this.posY < -64.0D) {
         this.outOfWorld();
      }

      if (!this.world.isRemote && this.world instanceof WorldServer) {
         this.world.profiler.startSection("portal");
         MinecraftServer minecraftserver = this.world.getServer();
         int i = this.getMaxInPortalTime();
         if (this.inPortal) {
            if (minecraftserver.getAllowNether()) {
               if (!this.isPassenger() && this.portalCounter++ >= i) {
                  this.portalCounter = i;
                  this.timeUntilPortal = this.getPortalCooldown();
                  DimensionType dimensiontype;
                  if (this.world.dimension.getType() == DimensionType.NETHER) {
                     dimensiontype = DimensionType.OVERWORLD;
                  } else {
                     dimensiontype = DimensionType.NETHER;
                  }

                  this.func_212321_a(dimensiontype);
               }

               this.inPortal = false;
            }
         } else {
            if (this.portalCounter > 0) {
               this.portalCounter -= 4;
            }

            if (this.portalCounter < 0) {
               this.portalCounter = 0;
            }
         }

         if (this.timeUntilPortal > 0) {
            --this.timeUntilPortal;
         }

         this.world.profiler.endSection();
      }

      if (this.world.isRemote) {
         if (this.turnProgress > 0) {
            double d4 = this.posX + (this.minecartX - this.posX) / (double)this.turnProgress;
            double d5 = this.posY + (this.minecartY - this.posY) / (double)this.turnProgress;
            double d6 = this.posZ + (this.minecartZ - this.posZ) / (double)this.turnProgress;
            double d1 = MathHelper.wrapDegrees(this.minecartYaw - (double)this.rotationYaw);
            this.rotationYaw = (float)((double)this.rotationYaw + d1 / (double)this.turnProgress);
            this.rotationPitch = (float)((double)this.rotationPitch + (this.minecartPitch - (double)this.rotationPitch) / (double)this.turnProgress);
            --this.turnProgress;
            this.setPosition(d4, d5, d6);
            this.setRotation(this.rotationYaw, this.rotationPitch);
         } else {
            this.setPosition(this.posX, this.posY, this.posZ);
            this.setRotation(this.rotationYaw, this.rotationPitch);
         }

      } else {
         this.prevPosX = this.posX;
         this.prevPosY = this.posY;
         this.prevPosZ = this.posZ;
         if (!this.hasNoGravity()) {
            this.motionY -= (double)0.04F;
         }

         int j = MathHelper.floor(this.posX);
         int k = MathHelper.floor(this.posY);
         int l = MathHelper.floor(this.posZ);
         if (this.world.getBlockState(new BlockPos(j, k - 1, l)).isIn(BlockTags.RAILS)) {
            --k;
         }

         BlockPos blockpos = new BlockPos(j, k, l);
         IBlockState iblockstate = this.world.getBlockState(blockpos);
         if (canUseRail() && iblockstate.isIn(BlockTags.RAILS)) {
            this.moveAlongTrack(blockpos, iblockstate);
            if (iblockstate.getBlock() == Blocks.ACTIVATOR_RAIL) {
               this.onActivatorRailPass(j, k, l, iblockstate.get(BlockRailPowered.POWERED));
            }
         } else {
            this.moveDerailedMinecart();
         }

         this.doBlockCollisions();
         this.rotationPitch = 0.0F;
         double d0 = this.prevPosX - this.posX;
         double d2 = this.prevPosZ - this.posZ;
         if (d0 * d0 + d2 * d2 > 0.001D) {
            this.rotationYaw = (float)(MathHelper.atan2(d2, d0) * 180.0D / Math.PI);
            if (this.isInReverse) {
               this.rotationYaw += 180.0F;
            }
         }

         double d3 = (double)MathHelper.wrapDegrees(this.rotationYaw - this.prevRotationYaw);
         if (d3 < -170.0D || d3 >= 170.0D) {
            this.rotationYaw += 180.0F;
            this.isInReverse = !this.isInReverse;
         }

         this.setRotation(this.rotationYaw, this.rotationPitch);
         AxisAlignedBB box;
         if (getCollisionHandler() != null) box = getCollisionHandler().getMinecartCollisionBox(this);
         else                               box = this.getBoundingBox().grow(0.2F, 0.0D, 0.2F);
         if (canBeRidden() && this.motionX * this.motionX + this.motionZ * this.motionZ > 0.01D) {
            List<Entity> list = this.world.getEntitiesInAABBexcluding(this, box, EntitySelectors.pushableBy(this));
            if (!list.isEmpty()) {
               for(int i1 = 0; i1 < list.size(); ++i1) {
                  Entity entity1 = list.get(i1);
                  if (!(entity1 instanceof EntityPlayer) && !(entity1 instanceof EntityIronGolem) && !(entity1 instanceof EntityMinecart) && !this.isBeingRidden() && !entity1.isPassenger()) {
                     entity1.startRiding(this);
                  } else {
                     entity1.applyEntityCollision(this);
                  }
               }
            }
         } else {
            for(Entity entity : this.world.getEntitiesWithinAABBExcludingEntity(this, box)) {
               if (!this.isPassenger(entity) && entity.canBePushed() && entity instanceof EntityMinecart) {
                  entity.applyEntityCollision(this);
               }
            }
         }

         this.handleWaterMovement();
         //net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.minecart.MinecartUpdateEvent(this, this.getCurrentRailPosition()));
      }
   }

   /**
    * Get's the maximum speed for a minecart
    */
   protected double getMaximumSpeed() {
      return 0.4D;
   }

   /**
    * Called every tick the minecart is on an activator rail.
    */
   public void onActivatorRailPass(int x, int y, int z, boolean receivingPower) {
   }

   /**
    * Moves a minecart that is not attached to a rail
    */
   protected void moveDerailedMinecart() {
      double d0 = onGround ? this.getMaximumSpeed() : getMaxSpeedAirLateral();
      this.motionX = MathHelper.clamp(this.motionX, -d0, d0);
      this.motionZ = MathHelper.clamp(this.motionZ, -d0, d0);

      double moveY = motionY;
      if(getMaxSpeedAirVertical() > 0 && motionY > getMaxSpeedAirVertical()) {
          moveY = getMaxSpeedAirVertical();
          if(Math.abs(motionX) < 0.3f && Math.abs(motionZ) < 0.3f) {
              moveY = 0.15f;
              motionY = moveY;
          }
      }

      if (this.onGround) {
         this.motionX *= 0.5D;
         this.motionY *= 0.5D;
         this.motionZ *= 0.5D;
      }

      this.move(MoverType.SELF, this.motionX, moveY, this.motionZ);
      if (!this.onGround) {
         this.motionX *= getDragAir();
         this.motionY *= getDragAir();
         this.motionZ *= getDragAir();
      }

   }

   protected void moveAlongTrack(BlockPos pos, IBlockState state) {
      this.fallDistance = 0.0F;
      Vec3d vec3d = this.getPos(this.posX, this.posY, this.posZ);
      this.posY = (double)pos.getY();
      boolean flag = false;
      boolean flag1 = false;
      BlockRailBase blockrailbase = (BlockRailBase)state.getBlock();
      if (blockrailbase == Blocks.POWERED_RAIL) {
         flag = state.get(BlockRailPowered.POWERED);
         flag1 = !flag;
      }

      double slopeAdjustment = getSlopeAdjustment();
      RailShape railshape = ((BlockRailBase)state.getBlock()).getRailDirection(state, world, pos, this);
      switch(railshape) {
      case ASCENDING_EAST:
         this.motionX -= slopeAdjustment;
         ++this.posY;
         break;
      case ASCENDING_WEST:
         this.motionX += slopeAdjustment;
         ++this.posY;
         break;
      case ASCENDING_NORTH:
         this.motionZ += slopeAdjustment;
         ++this.posY;
         break;
      case ASCENDING_SOUTH:
         this.motionZ -= slopeAdjustment;
         ++this.posY;
      }

      int[][] aint = MATRIX[railshape.getMeta()];
      double d1 = (double)(aint[1][0] - aint[0][0]);
      double d2 = (double)(aint[1][2] - aint[0][2]);
      double d3 = Math.sqrt(d1 * d1 + d2 * d2);
      double d4 = this.motionX * d1 + this.motionZ * d2;
      if (d4 < 0.0D) {
         d1 = -d1;
         d2 = -d2;
      }

      double d5 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
      if (d5 > 2.0D) {
         d5 = 2.0D;
      }

      this.motionX = d5 * d1 / d3;
      this.motionZ = d5 * d2 / d3;
      Entity entity = this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
      if (entity instanceof EntityPlayer) {
         double d6 = (double)((EntityPlayer)entity).moveForward;
         if (d6 > 0.0D) {
            double d7 = -Math.sin((double)(entity.rotationYaw * ((float)Math.PI / 180F)));
            double d8 = Math.cos((double)(entity.rotationYaw * ((float)Math.PI / 180F)));
            double d9 = this.motionX * this.motionX + this.motionZ * this.motionZ;
            if (d9 < 0.01D) {
               this.motionX += d7 * 0.1D;
               this.motionZ += d8 * 0.1D;
               flag1 = false;
            }
         }
      }

      if (flag1 && shouldDoRailFunctions()) {
         double d17 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         if (d17 < 0.03D) {
            this.motionX *= 0.0D;
            this.motionY *= 0.0D;
            this.motionZ *= 0.0D;
         } else {
            this.motionX *= 0.5D;
            this.motionY *= 0.0D;
            this.motionZ *= 0.5D;
         }
      }

      double d18 = (double)pos.getX() + 0.5D + (double)aint[0][0] * 0.5D;
      double d19 = (double)pos.getZ() + 0.5D + (double)aint[0][2] * 0.5D;
      double d20 = (double)pos.getX() + 0.5D + (double)aint[1][0] * 0.5D;
      double d21 = (double)pos.getZ() + 0.5D + (double)aint[1][2] * 0.5D;
      d1 = d20 - d18;
      d2 = d21 - d19;
      double d10;
      if (d1 == 0.0D) {
         this.posX = (double)pos.getX() + 0.5D;
         d10 = this.posZ - (double)pos.getZ();
      } else if (d2 == 0.0D) {
         this.posZ = (double)pos.getZ() + 0.5D;
         d10 = this.posX - (double)pos.getX();
      } else {
         double d11 = this.posX - d18;
         double d12 = this.posZ - d19;
         d10 = (d11 * d1 + d12 * d2) * 2.0D;
      }

      this.posX = d18 + d1 * d10;
      this.posZ = d19 + d2 * d10;
      this.setPosition(this.posX, this.posY, this.posZ);
      this.moveMinecartOnRail(pos);
      if (aint[0][1] != 0 && MathHelper.floor(this.posX) - pos.getX() == aint[0][0] && MathHelper.floor(this.posZ) - pos.getZ() == aint[0][2]) {
         this.setPosition(this.posX, this.posY + (double)aint[0][1], this.posZ);
      } else if (aint[1][1] != 0 && MathHelper.floor(this.posX) - pos.getX() == aint[1][0] && MathHelper.floor(this.posZ) - pos.getZ() == aint[1][2]) {
         this.setPosition(this.posX, this.posY + (double)aint[1][1], this.posZ);
      }

      this.applyDrag();
      Vec3d vec3d1 = this.getPos(this.posX, this.posY, this.posZ);
      if (vec3d1 != null && vec3d != null) {
         double d14 = (vec3d.y - vec3d1.y) * 0.05D;
         d5 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         if (d5 > 0.0D) {
            this.motionX = this.motionX / d5 * (d5 + d14);
            this.motionZ = this.motionZ / d5 * (d5 + d14);
         }

         this.setPosition(this.posX, vec3d1.y, this.posZ);
      }

      int j = MathHelper.floor(this.posX);
      int i = MathHelper.floor(this.posZ);
      if (j != pos.getX() || i != pos.getZ()) {
         d5 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         this.motionX = d5 * (double)(j - pos.getX());
         this.motionZ = d5 * (double)(i - pos.getZ());
      }

      if (shouldDoRailFunctions())
          ((BlockRailBase)state.getBlock()).onMinecartPass(state, world, pos, this);

      if (flag && shouldDoRailFunctions()) {
         double d15 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         if (d15 > 0.01D) {
            double d16 = 0.06D;
            this.motionX += this.motionX / d15 * 0.06D;
            this.motionZ += this.motionZ / d15 * 0.06D;
         } else if (railshape == RailShape.EAST_WEST) {
            if (this.world.getBlockState(pos.west()).isNormalCube()) {
               this.motionX = 0.02D;
            } else if (this.world.getBlockState(pos.east()).isNormalCube()) {
               this.motionX = -0.02D;
            }
         } else if (railshape == RailShape.NORTH_SOUTH) {
            if (this.world.getBlockState(pos.north()).isNormalCube()) {
               this.motionZ = 0.02D;
            } else if (this.world.getBlockState(pos.south()).isNormalCube()) {
               this.motionZ = -0.02D;
            }
         }
      }

   }

   protected void applyDrag() {
      if (this.isBeingRidden()) {
         this.motionX *= (double)0.997F;
         this.motionY *= 0.0D;
         this.motionZ *= (double)0.997F;
      } else {
         this.motionX *= (double)0.96F;
         this.motionY *= 0.0D;
         this.motionZ *= (double)0.96F;
      }

   }

   /**
    * Sets the x,y,z of the entity from the given parameters. Also seems to set up a bounding box.
    */
   public void setPosition(double x, double y, double z) {
      this.posX = x;
      this.posY = y;
      this.posZ = z;
      float f = this.width / 2.0F;
      float f1 = this.height;
      this.setBoundingBox(new AxisAlignedBB(x - (double)f, y, z - (double)f, x + (double)f, y + (double)f1, z + (double)f));
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public Vec3d getPosOffset(double x, double y, double z, double offset) {
      int i = MathHelper.floor(x);
      int j = MathHelper.floor(y);
      int k = MathHelper.floor(z);
      if (this.world.getBlockState(new BlockPos(i, j - 1, k)).isIn(BlockTags.RAILS)) {
         --j;
      }

      IBlockState iblockstate = this.world.getBlockState(new BlockPos(i, j, k));
      if (iblockstate.isIn(BlockTags.RAILS)) {
         RailShape railshape = ((BlockRailBase)iblockstate.getBlock()).getRailDirection(iblockstate, world, new BlockPos(i, j, k), this);
         y = (double)j;
         if (railshape.isAscending()) {
            y = (double)(j + 1);
         }

         int[][] aint = MATRIX[railshape.getMeta()];
         double d0 = (double)(aint[1][0] - aint[0][0]);
         double d1 = (double)(aint[1][2] - aint[0][2]);
         double d2 = Math.sqrt(d0 * d0 + d1 * d1);
         d0 = d0 / d2;
         d1 = d1 / d2;
         x = x + d0 * offset;
         z = z + d1 * offset;
         if (aint[0][1] != 0 && MathHelper.floor(x) - i == aint[0][0] && MathHelper.floor(z) - k == aint[0][2]) {
            y += (double)aint[0][1];
         } else if (aint[1][1] != 0 && MathHelper.floor(x) - i == aint[1][0] && MathHelper.floor(z) - k == aint[1][2]) {
            y += (double)aint[1][1];
         }

         return this.getPos(x, y, z);
      } else {
         return null;
      }
   }

   @Nullable
   public Vec3d getPos(double p_70489_1_, double p_70489_3_, double p_70489_5_) {
      int i = MathHelper.floor(p_70489_1_);
      int j = MathHelper.floor(p_70489_3_);
      int k = MathHelper.floor(p_70489_5_);
      if (this.world.getBlockState(new BlockPos(i, j - 1, k)).isIn(BlockTags.RAILS)) {
         --j;
      }

      IBlockState iblockstate = this.world.getBlockState(new BlockPos(i, j, k));
      if (iblockstate.isIn(BlockTags.RAILS)) {
         RailShape railshape = ((BlockRailBase)iblockstate.getBlock()).getRailDirection(iblockstate, world, new BlockPos(i, j, k), this);
         int[][] aint = MATRIX[railshape.getMeta()];
         double d0 = (double)i + 0.5D + (double)aint[0][0] * 0.5D;
         double d1 = (double)j + 0.0625D + (double)aint[0][1] * 0.5D;
         double d2 = (double)k + 0.5D + (double)aint[0][2] * 0.5D;
         double d3 = (double)i + 0.5D + (double)aint[1][0] * 0.5D;
         double d4 = (double)j + 0.0625D + (double)aint[1][1] * 0.5D;
         double d5 = (double)k + 0.5D + (double)aint[1][2] * 0.5D;
         double d6 = d3 - d0;
         double d7 = (d4 - d1) * 2.0D;
         double d8 = d5 - d2;
         double d9;
         if (d6 == 0.0D) {
            d9 = p_70489_5_ - (double)k;
         } else if (d8 == 0.0D) {
            d9 = p_70489_1_ - (double)i;
         } else {
            double d10 = p_70489_1_ - d0;
            double d11 = p_70489_5_ - d2;
            d9 = (d10 * d6 + d11 * d8) * 2.0D;
         }

         p_70489_1_ = d0 + d6 * d9;
         p_70489_3_ = d1 + d7 * d9;
         p_70489_5_ = d2 + d8 * d9;
         if (d7 < 0.0D) {
            ++p_70489_3_;
         }

         if (d7 > 0.0D) {
            p_70489_3_ += 0.5D;
         }

         return new Vec3d(p_70489_1_, p_70489_3_, p_70489_5_);
      } else {
         return null;
      }
   }

   /**
    * Gets the bounding box of this Entity, adjusted to take auxiliary entities into account (e.g. the tile contained by
    * a minecart, such as a command block).
    */
   @OnlyIn(Dist.CLIENT)
   public AxisAlignedBB getRenderBoundingBox() {
      AxisAlignedBB axisalignedbb = this.getBoundingBox();
      return this.hasDisplayTile() ? axisalignedbb.grow((double)Math.abs(this.getDisplayTileOffset()) / 16.0D) : axisalignedbb;
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   protected void readAdditional(NBTTagCompound compound) {
      if (compound.getBoolean("CustomDisplayTile")) {
         this.setDisplayTile(NBTUtil.readBlockState(compound.getCompound("DisplayState")));
         this.setDisplayTileOffset(compound.getInt("DisplayOffset"));
      }

   }

   /**
    * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
    * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
    */
   protected void writeAdditional(NBTTagCompound compound) {
      if (this.hasDisplayTile()) {
         compound.setBoolean("CustomDisplayTile", true);
         compound.setTag("DisplayState", NBTUtil.writeBlockState(this.getDisplayTile()));
         compound.setInt("DisplayOffset", this.getDisplayTileOffset());
      }

   }

   /**
    * Applies a velocity to the entities, to push them away from eachother.
    */
   public void applyEntityCollision(Entity entityIn) {
      //net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.minecart.MinecartCollisionEvent(this, entityIn));
      if (getCollisionHandler() != null) {
         getCollisionHandler().onEntityCollision(this, entityIn);
         return;
      }
      if (!this.world.isRemote) {
         if (!entityIn.noClip && !this.noClip) {
            if (!this.isPassenger(entityIn)) {
               double d0 = entityIn.posX - this.posX;
               double d1 = entityIn.posZ - this.posZ;
               double d2 = d0 * d0 + d1 * d1;
               if (d2 >= (double)1.0E-4F) {
                  d2 = (double)MathHelper.sqrt(d2);
                  d0 = d0 / d2;
                  d1 = d1 / d2;
                  double d3 = 1.0D / d2;
                  if (d3 > 1.0D) {
                     d3 = 1.0D;
                  }

                  d0 = d0 * d3;
                  d1 = d1 * d3;
                  d0 = d0 * (double)0.1F;
                  d1 = d1 * (double)0.1F;
                  d0 = d0 * (double)(1.0F - this.entityCollisionReduction);
                  d1 = d1 * (double)(1.0F - this.entityCollisionReduction);
                  d0 = d0 * 0.5D;
                  d1 = d1 * 0.5D;
                  if (entityIn instanceof EntityMinecart) {
                     double d4 = entityIn.posX - this.posX;
                     double d5 = entityIn.posZ - this.posZ;
                     Vec3d vec3d = (new Vec3d(d4, 0.0D, d5)).normalize();
                     Vec3d vec3d1 = (new Vec3d((double)MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180F)), 0.0D, (double)MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180F)))).normalize();
                     double d6 = Math.abs(vec3d.dotProduct(vec3d1));
                     if (d6 < (double)0.8F) {
                        return;
                     }

                     double d7 = entityIn.motionX + this.motionX;
                     double d8 = entityIn.motionZ + this.motionZ;
                     if (((EntityMinecart)entityIn).isPoweredCart() && !isPoweredCart()) {
                        this.motionX *= (double)0.2F;
                        this.motionZ *= (double)0.2F;
                        this.addVelocity(entityIn.motionX - d0, 0.0D, entityIn.motionZ - d1);
                        entityIn.motionX *= (double)0.95F;
                        entityIn.motionZ *= (double)0.95F;
                     } else if (!((EntityMinecart)entityIn).isPoweredCart() && isPoweredCart()) {
                        entityIn.motionX *= (double)0.2F;
                        entityIn.motionZ *= (double)0.2F;
                        entityIn.addVelocity(this.motionX + d0, 0.0D, this.motionZ + d1);
                        this.motionX *= (double)0.95F;
                        this.motionZ *= (double)0.95F;
                     } else {
                        d7 = d7 / 2.0D;
                        d8 = d8 / 2.0D;
                        this.motionX *= (double)0.2F;
                        this.motionZ *= (double)0.2F;
                        this.addVelocity(d7 - d0, 0.0D, d8 - d1);
                        entityIn.motionX *= (double)0.2F;
                        entityIn.motionZ *= (double)0.2F;
                        entityIn.addVelocity(d7 + d0, 0.0D, d8 + d1);
                     }
                  } else {
                     this.addVelocity(-d0, 0.0D, -d1);
                     entityIn.addVelocity(d0 / 4.0D, 0.0D, d1 / 4.0D);
                  }
               }

            }
         }
      }
   }

   /**
    * Sets a target for the client to interpolate towards over the next few ticks
    */
   @OnlyIn(Dist.CLIENT)
   public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
      this.minecartX = x;
      this.minecartY = y;
      this.minecartZ = z;
      this.minecartYaw = (double)yaw;
      this.minecartPitch = (double)pitch;
      this.turnProgress = posRotationIncrements + 2;
      this.motionX = this.velocityX;
      this.motionY = this.velocityY;
      this.motionZ = this.velocityZ;
   }

   /**
    * Updates the entity motion clientside, called by packets from the server
    */
   @OnlyIn(Dist.CLIENT)
   public void setVelocity(double x, double y, double z) {
      this.motionX = x;
      this.motionY = y;
      this.motionZ = z;
      this.velocityX = this.motionX;
      this.velocityY = this.motionY;
      this.velocityZ = this.motionZ;
   }

   /**
    * Sets the current amount of damage the minecart has taken. Decreases over time. The cart breaks when this is over
    * 40.
    */
   public void setDamage(float damage) {
      this.dataManager.set(DAMAGE, damage);
   }

   /**
    * Gets the current amount of damage the minecart has taken. Decreases over time. The cart breaks when this is over
    * 40.
    */
   public float getDamage() {
      return this.dataManager.get(DAMAGE);
   }

   /**
    * Sets the rolling amplitude the cart rolls while being attacked.
    */
   public void setRollingAmplitude(int rollingAmplitude) {
      this.dataManager.set(ROLLING_AMPLITUDE, rollingAmplitude);
   }

   /**
    * Gets the rolling amplitude the cart rolls while being attacked.
    */
   public int getRollingAmplitude() {
      return this.dataManager.get(ROLLING_AMPLITUDE);
   }

   /**
    * Sets the rolling direction the cart rolls while being attacked. Can be 1 or -1.
    */
   public void setRollingDirection(int rollingDirection) {
      this.dataManager.set(ROLLING_DIRECTION, rollingDirection);
   }

   /**
    * Gets the rolling direction the cart rolls while being attacked. Can be 1 or -1.
    */
   public int getRollingDirection() {
      return this.dataManager.get(ROLLING_DIRECTION);
   }

   public abstract EntityMinecart.Type getMinecartType();

   public IBlockState getDisplayTile() {
      return !this.hasDisplayTile() ? this.getDefaultDisplayTile() : Block.getStateById(this.getDataManager().get(DISPLAY_TILE));
   }

   public IBlockState getDefaultDisplayTile() {
      return Blocks.AIR.getDefaultState();
   }

   public int getDisplayTileOffset() {
      return !this.hasDisplayTile() ? this.getDefaultDisplayTileOffset() : this.getDataManager().get(DISPLAY_TILE_OFFSET);
   }

   public int getDefaultDisplayTileOffset() {
      return 6;
   }

   public void setDisplayTile(IBlockState displayTile) {
      this.getDataManager().set(DISPLAY_TILE, Block.getStateId(displayTile));
      this.setHasDisplayTile(true);
   }

   public void setDisplayTileOffset(int displayTileOffset) {
      this.getDataManager().set(DISPLAY_TILE_OFFSET, displayTileOffset);
      this.setHasDisplayTile(true);
   }

   public boolean hasDisplayTile() {
      return this.getDataManager().get(SHOW_BLOCK);
   }

   public void setHasDisplayTile(boolean showBlock) {
      this.getDataManager().set(SHOW_BLOCK, showBlock);
   }

   private boolean canUseRail = true;
   @Override public boolean canUseRail() { return canUseRail; }
   @Override public void setCanUseRail(boolean value) { this.canUseRail = value; }
   private float currentSpeedOnRail;
   @Override public float getCurrentCartSpeedCapOnRail() { return currentSpeedOnRail; }
   @Override public void setCurrentCartSpeedCapOnRail(float value) { currentSpeedOnRail = Math.min(value, getMaxCartSpeedOnRail()); }
   private float maxSpeedAirLateral = DEFAULT_MAX_SPEED_AIR_LATERAL;
   @Override public float getMaxSpeedAirLateral() { return maxSpeedAirLateral; }
   @Override public void setMaxSpeedAirLateral(float value) { maxSpeedAirLateral = value; }
   private float maxSpeedAirVertical = DEFAULT_MAX_SPEED_AIR_VERTICAL;
   @Override public float getMaxSpeedAirVertical() { return maxSpeedAirVertical; }
   @Override public void setMaxSpeedAirVertical(float value) { maxSpeedAirVertical = value; }
   private double dragAir = DEFAULT_AIR_DRAG;
   @Override public double getDragAir() { return dragAir; }
   @Override public void setDragAir(double value) { dragAir = value; }
   @Override
   public double getMaxSpeed() { //Non-default because getMaximumSpeed is protected
      if (!canUseRail()) return getMaximumSpeed();
      BlockPos pos = this.getCurrentRailPosition();
      IBlockState state = getMinecart().world.getBlockState(pos);
      if (!state.isIn(BlockTags.RAILS)) return getMaximumSpeed();

      float railMaxSpeed = ((BlockRailBase)state.getBlock()).getRailMaxSpeed(state, getMinecart().world, pos, getMinecart());
      return Math.min(railMaxSpeed, getCurrentCartSpeedCapOnRail());
   }

   public static enum Type {
      RIDEABLE(0),
      CHEST(1),
      FURNACE(2),
      TNT(3),
      SPAWNER(4),
      HOPPER(5),
      COMMAND_BLOCK(6);

      private static final EntityMinecart.Type[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(EntityMinecart.Type::getId)).toArray((p_199766_0_) -> {
         return new EntityMinecart.Type[p_199766_0_];
      });
      private final int id;

      private Type(int p_i48595_3_) {
         this.id = p_i48595_3_;
      }

      public int getId() {
         return this.id;
      }

      @OnlyIn(Dist.CLIENT)
      public static EntityMinecart.Type getById(int idIn) {
         return idIn >= 0 && idIn < BY_ID.length ? BY_ID[idIn] : RIDEABLE;
      }
   }
}