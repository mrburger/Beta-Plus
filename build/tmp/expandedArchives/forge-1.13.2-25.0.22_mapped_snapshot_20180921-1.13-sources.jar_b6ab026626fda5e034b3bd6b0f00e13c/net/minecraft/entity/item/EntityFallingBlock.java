package net.minecraft.entity.item;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockConcretePowder;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityFallingBlock extends Entity {
   private IBlockState fallTile = Blocks.SAND.getDefaultState();
   public int fallTime;
   public boolean shouldDropItem = true;
   private boolean dontSetBlock;
   private boolean hurtEntities;
   private int fallHurtMax = 40;
   private float fallHurtAmount = 2.0F;
   public NBTTagCompound tileEntityData;
   protected static final DataParameter<BlockPos> ORIGIN = EntityDataManager.createKey(EntityFallingBlock.class, DataSerializers.BLOCK_POS);

   public EntityFallingBlock(World worldIn) {
      super(EntityType.FALLING_BLOCK, worldIn);
   }

   public EntityFallingBlock(World worldIn, double x, double y, double z, IBlockState fallingBlockState) {
      this(worldIn);
      this.fallTile = fallingBlockState;
      this.preventEntitySpawning = true;
      this.setSize(0.98F, 0.98F);
      this.setPosition(x, y + (double)((1.0F - this.height) / 2.0F), z);
      this.motionX = 0.0D;
      this.motionY = 0.0D;
      this.motionZ = 0.0D;
      this.prevPosX = x;
      this.prevPosY = y;
      this.prevPosZ = z;
      this.setOrigin(new BlockPos(this));
   }

   /**
    * Returns true if it's possible to attack this entity with an item.
    */
   public boolean canBeAttackedWithItem() {
      return false;
   }

   public void setOrigin(BlockPos p_184530_1_) {
      this.dataManager.set(ORIGIN, p_184530_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getOrigin() {
      return this.dataManager.get(ORIGIN);
   }

   /**
    * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
    * prevent them from trampling crops
    */
   protected boolean canTriggerWalking() {
      return false;
   }

   protected void registerData() {
      this.dataManager.register(ORIGIN, BlockPos.ORIGIN);
   }

   /**
    * Returns true if other Entities should be prevented from moving through this Entity.
    */
   public boolean canBeCollidedWith() {
      return !this.removed;
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      if (this.fallTile.isAir()) {
         this.remove();
      } else {
         this.prevPosX = this.posX;
         this.prevPosY = this.posY;
         this.prevPosZ = this.posZ;
         Block block = this.fallTile.getBlock();
         if (this.fallTime++ == 0) {
            BlockPos blockpos = new BlockPos(this);
            if (this.world.getBlockState(blockpos).getBlock() == block) {
               this.world.removeBlock(blockpos);
            } else if (!this.world.isRemote) {
               this.remove();
               return;
            }
         }

         if (!this.hasNoGravity()) {
            this.motionY -= (double)0.04F;
         }

         this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
         if (!this.world.isRemote) {
            BlockPos blockpos1 = new BlockPos(this);
            boolean flag = this.fallTile.getBlock() instanceof BlockConcretePowder;
            boolean flag1 = flag && this.world.getFluidState(blockpos1).isTagged(FluidTags.WATER);
            double d0 = this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ;
            if (flag && d0 > 1.0D) {
               RayTraceResult raytraceresult = this.world.rayTraceBlocks(new Vec3d(this.prevPosX, this.prevPosY, this.prevPosZ), new Vec3d(this.posX, this.posY, this.posZ), RayTraceFluidMode.SOURCE_ONLY);
               if (raytraceresult != null && this.world.getFluidState(raytraceresult.getBlockPos()).isTagged(FluidTags.WATER)) {
                  blockpos1 = raytraceresult.getBlockPos();
                  flag1 = true;
               }
            }

            if (!this.onGround && !flag1) {
               if (this.fallTime > 100 && !this.world.isRemote && (blockpos1.getY() < 1 || blockpos1.getY() > 256) || this.fallTime > 600) {
                  if (this.shouldDropItem && this.world.getGameRules().getBoolean("doEntityDrops")) {
                     this.entityDropItem(block);
                  }

                  this.remove();
               }
            } else {
               IBlockState iblockstate = this.world.getBlockState(blockpos1);
               if (this.world.isAirBlock(new BlockPos(this.posX, this.posY - (double)0.01F, this.posZ))) //Forge: Don't indent below.
               if (!flag1 && BlockFalling.canFallThrough(this.world.getBlockState(new BlockPos(this.posX, this.posY - (double)0.01F, this.posZ)))) {
                  this.onGround = false;
                  return;
               }

               this.motionX *= (double)0.7F;
               this.motionZ *= (double)0.7F;
               this.motionY *= -0.5D;
               if (iblockstate.getBlock() != Blocks.MOVING_PISTON) {
                  this.remove();
                  if (!this.dontSetBlock) {
                     if (iblockstate.getMaterial().isReplaceable() && (flag1 || !BlockFalling.canFallThrough(this.world.getBlockState(blockpos1.down()))) && this.world.setBlockState(blockpos1, this.fallTile, 3)) {
                        if (block instanceof BlockFalling) {
                           ((BlockFalling)block).onEndFalling(this.world, blockpos1, this.fallTile, iblockstate);
                        }

                        if (this.tileEntityData != null && this.fallTile.hasTileEntity()) {
                           TileEntity tileentity = this.world.getTileEntity(blockpos1);
                           if (tileentity != null) {
                              NBTTagCompound nbttagcompound = tileentity.write(new NBTTagCompound());

                              for(String s : this.tileEntityData.keySet()) {
                                 INBTBase inbtbase = this.tileEntityData.getTag(s);
                                 if (!"x".equals(s) && !"y".equals(s) && !"z".equals(s)) {
                                    nbttagcompound.setTag(s, inbtbase.copy());
                                 }
                              }

                              tileentity.read(nbttagcompound);
                              tileentity.markDirty();
                           }
                        }
                     } else if (this.shouldDropItem && this.world.getGameRules().getBoolean("doEntityDrops")) {
                        this.entityDropItem(block);
                     }
                  } else if (block instanceof BlockFalling) {
                     ((BlockFalling)block).onBroken(this.world, blockpos1);
                  }
               }
            }
         }

         this.motionX *= (double)0.98F;
         this.motionY *= (double)0.98F;
         this.motionZ *= (double)0.98F;
      }
   }

   public void fall(float distance, float damageMultiplier) {
      if (this.hurtEntities) {
         int i = MathHelper.ceil(distance - 1.0F);
         if (i > 0) {
            List<Entity> list = Lists.newArrayList(this.world.getEntitiesWithinAABBExcludingEntity(this, this.getBoundingBox()));
            boolean flag = this.fallTile.isIn(BlockTags.ANVIL);
            DamageSource damagesource = flag ? DamageSource.ANVIL : DamageSource.FALLING_BLOCK;

            for(Entity entity : list) {
               entity.attackEntityFrom(damagesource, (float)Math.min(MathHelper.floor((float)i * this.fallHurtAmount), this.fallHurtMax));
            }

            if (flag && (double)this.rand.nextFloat() < (double)0.05F + (double)i * 0.05D) {
               IBlockState iblockstate = BlockAnvil.damage(this.fallTile);
               if (iblockstate == null) {
                  this.dontSetBlock = true;
               } else {
                  this.fallTile = iblockstate;
               }
            }
         }
      }

   }

   /**
    * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
    * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
    */
   protected void writeAdditional(NBTTagCompound compound) {
      compound.setTag("BlockState", NBTUtil.writeBlockState(this.fallTile));
      compound.setInt("Time", this.fallTime);
      compound.setBoolean("DropItem", this.shouldDropItem);
      compound.setBoolean("HurtEntities", this.hurtEntities);
      compound.setFloat("FallHurtAmount", this.fallHurtAmount);
      compound.setInt("FallHurtMax", this.fallHurtMax);
      if (this.tileEntityData != null) {
         compound.setTag("TileEntityData", this.tileEntityData);
      }

   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   protected void readAdditional(NBTTagCompound compound) {
      this.fallTile = NBTUtil.readBlockState(compound.getCompound("BlockState"));
      this.fallTime = compound.getInt("Time");
      if (compound.contains("HurtEntities", 99)) {
         this.hurtEntities = compound.getBoolean("HurtEntities");
         this.fallHurtAmount = compound.getFloat("FallHurtAmount");
         this.fallHurtMax = compound.getInt("FallHurtMax");
      } else if (this.fallTile.isIn(BlockTags.ANVIL)) {
         this.hurtEntities = true;
      }

      if (compound.contains("DropItem", 99)) {
         this.shouldDropItem = compound.getBoolean("DropItem");
      }

      if (compound.contains("TileEntityData", 10)) {
         this.tileEntityData = compound.getCompound("TileEntityData");
      }

      if (this.fallTile.isAir()) {
         this.fallTile = Blocks.SAND.getDefaultState();
      }

   }

   @OnlyIn(Dist.CLIENT)
   public World getWorldObj() {
      return this.world;
   }

   public void setHurtEntities(boolean hurtEntitiesIn) {
      this.hurtEntities = hurtEntitiesIn;
   }

   /**
    * Return whether this entity should be rendered as on fire.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean canRenderOnFire() {
      return false;
   }

   public void fillCrashReport(CrashReportCategory category) {
      super.fillCrashReport(category);
      category.addDetail("Immitating BlockState", this.fallTile.toString());
   }

   public IBlockState getBlockState() {
      return this.fallTile;
   }

   public boolean ignoreItemEntityData() {
      return true;
   }
}