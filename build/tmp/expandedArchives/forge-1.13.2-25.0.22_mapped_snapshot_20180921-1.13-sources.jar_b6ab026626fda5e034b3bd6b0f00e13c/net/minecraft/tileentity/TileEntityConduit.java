package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityConduit extends TileEntity implements ITickable {
   private static final Block[] field_205042_e = new Block[]{Blocks.PRISMARINE, Blocks.PRISMARINE_BRICKS, Blocks.SEA_LANTERN, Blocks.DARK_PRISMARINE};
   public int ticksExisted;
   private float activeRotation;
   private boolean active;
   private boolean eyeOpen;
   private final List<BlockPos> prismarinePositions = Lists.newArrayList();
   private EntityLivingBase target;
   private UUID targetUuid;
   private long nextSoundTime;

   public TileEntityConduit() {
      this(TileEntityType.CONDUIT);
   }

   public TileEntityConduit(TileEntityType<?> p_i48929_1_) {
      super(p_i48929_1_);
   }

   public void read(NBTTagCompound compound) {
      super.read(compound);
      if (compound.hasKey("target_uuid")) {
         this.targetUuid = NBTUtil.readUniqueId(compound.getCompound("target_uuid"));
      } else {
         this.targetUuid = null;
      }

   }

   public NBTTagCompound write(NBTTagCompound compound) {
      super.write(compound);
      if (this.target != null) {
         compound.setTag("target_uuid", NBTUtil.writeUniqueId(this.target.getUniqueID()));
      }

      return compound;
   }

   /**
    * Retrieves packet to send to the client whenever this Tile Entity is resynced via World.notifyBlockUpdate. For
    * modded TE's, this packet comes back to you clientside in {@link #onDataPacket}
    */
   @Nullable
   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.pos, 5, this.getUpdateTag());
   }

   /**
    * Get an NBT compound to sync to the client with SPacketChunkData, used for initial loading of the chunk or when
    * many blocks change at once. This compound comes back to you clientside in {@link handleUpdateTag}
    */
   public NBTTagCompound getUpdateTag() {
      return this.write(new NBTTagCompound());
   }

   public void tick() {
      ++this.ticksExisted;
      long i = this.world.getGameTime();
      if (i % 40L == 0L) {
         this.setActive(this.shouldBeActive());
         if (!this.world.isRemote && this.isActive()) {
            this.addEffectsToPlayers();
            this.attackMobs();
         }
      }

      if (i % 80L == 0L && this.isActive()) {
         this.playSound(SoundEvents.BLOCK_CONDUIT_AMBIENT);
      }

      if (i > this.nextSoundTime && this.isActive()) {
         this.nextSoundTime = i + 60L + (long)this.world.getRandom().nextInt(40);
         this.playSound(SoundEvents.BLOCK_CONDUIT_AMBIENT_SHORT);
      }

      if (this.world.isRemote) {
         this.updateClientTarget();
         this.spawnParticles();
         if (this.isActive()) {
            ++this.activeRotation;
         }
      }

   }

   private boolean shouldBeActive() {
      this.prismarinePositions.clear();

      for(int i = -1; i <= 1; ++i) {
         for(int j = -1; j <= 1; ++j) {
            for(int k = -1; k <= 1; ++k) {
               BlockPos blockpos = this.pos.add(i, j, k);
               if (!this.world.hasWater(blockpos)) {
                  return false;
               }
            }
         }
      }

      for(int j1 = -2; j1 <= 2; ++j1) {
         for(int k1 = -2; k1 <= 2; ++k1) {
            for(int l1 = -2; l1 <= 2; ++l1) {
               int i2 = Math.abs(j1);
               int l = Math.abs(k1);
               int i1 = Math.abs(l1);
               if ((i2 > 1 || l > 1 || i1 > 1) && (j1 == 0 && (l == 2 || i1 == 2) || k1 == 0 && (i2 == 2 || i1 == 2) || l1 == 0 && (i2 == 2 || l == 2))) {
                  BlockPos blockpos1 = this.pos.add(j1, k1, l1);
                  IBlockState iblockstate = this.world.getBlockState(blockpos1);

                  for(Block block : field_205042_e) {
                     if (iblockstate.getBlock() == block) {
                        this.prismarinePositions.add(blockpos1);
                     }
                  }
               }
            }
         }
      }

      this.setEyeOpen(this.prismarinePositions.size() >= 42);
      return this.prismarinePositions.size() >= 16;
   }

   private void addEffectsToPlayers() {
      int i = this.prismarinePositions.size();
      int j = i / 7 * 16;
      int k = this.pos.getX();
      int l = this.pos.getY();
      int i1 = this.pos.getZ();
      AxisAlignedBB axisalignedbb = (new AxisAlignedBB((double)k, (double)l, (double)i1, (double)(k + 1), (double)(l + 1), (double)(i1 + 1))).grow((double)j).expand(0.0D, (double)this.world.getHeight(), 0.0D);
      List<EntityPlayer> list = this.world.getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb);
      if (!list.isEmpty()) {
         for(EntityPlayer entityplayer : list) {
            if (this.pos.getDistance(new BlockPos(entityplayer)) <= (double)j && entityplayer.isWet()) {
               entityplayer.addPotionEffect(new PotionEffect(MobEffects.CONDUIT_POWER, 260, 0, true, true));
            }
         }

      }
   }

   private void attackMobs() {
      EntityLivingBase entitylivingbase = this.target;
      int i = this.prismarinePositions.size();
      if (i < 42) {
         this.target = null;
      } else if (this.target == null && this.targetUuid != null) {
         this.target = this.findExistingTarget();
         this.targetUuid = null;
      } else if (this.target == null) {
         List<EntityLivingBase> list = this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getAreaOfEffect(), (p_205033_0_) -> {
            return p_205033_0_ instanceof IMob && p_205033_0_.isWet();
         });
         if (!list.isEmpty()) {
            this.target = list.get(this.world.rand.nextInt(list.size()));
         }
      } else if (!this.target.isAlive() || this.pos.getDistance(new BlockPos(this.target)) > 8.0D) {
         this.target = null;
      }

      if (this.target != null) {
         this.world.playSound((EntityPlayer)null, this.target.posX, this.target.posY, this.target.posZ, SoundEvents.BLOCK_CONDUIT_ATTACK_TARGET, SoundCategory.BLOCKS, 1.0F, 1.0F);
         this.target.attackEntityFrom(DamageSource.MAGIC, 4.0F);
      }

      if (entitylivingbase != this.target) {
         IBlockState iblockstate = this.getBlockState();
         this.world.notifyBlockUpdate(this.pos, iblockstate, iblockstate, 2);
      }

   }

   private void updateClientTarget() {
      if (this.targetUuid == null) {
         this.target = null;
      } else if (this.target == null || !this.target.getUniqueID().equals(this.targetUuid)) {
         this.target = this.findExistingTarget();
         if (this.target == null) {
            this.targetUuid = null;
         }
      }

   }

   private AxisAlignedBB getAreaOfEffect() {
      int i = this.pos.getX();
      int j = this.pos.getY();
      int k = this.pos.getZ();
      return (new AxisAlignedBB((double)i, (double)j, (double)k, (double)(i + 1), (double)(j + 1), (double)(k + 1))).grow(8.0D);
   }

   @Nullable
   private EntityLivingBase findExistingTarget() {
      List<EntityLivingBase> list = this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getAreaOfEffect(), (p_205032_1_) -> {
         return p_205032_1_.getUniqueID().equals(this.targetUuid);
      });
      return list.size() == 1 ? list.get(0) : null;
   }

   private void spawnParticles() {
      Random random = this.world.rand;
      float f = MathHelper.sin((float)(this.ticksExisted + 35) * 0.1F) / 2.0F + 0.5F;
      f = (f * f + f) * 0.3F;
      Vec3d vec3d = new Vec3d((double)((float)this.pos.getX() + 0.5F), (double)((float)this.pos.getY() + 1.5F + f), (double)((float)this.pos.getZ() + 0.5F));

      for(BlockPos blockpos : this.prismarinePositions) {
         if (random.nextInt(50) == 0) {
            float f1 = -0.5F + random.nextFloat();
            float f2 = -2.0F + random.nextFloat();
            float f3 = -0.5F + random.nextFloat();
            BlockPos blockpos1 = blockpos.subtract(this.pos);
            Vec3d vec3d1 = (new Vec3d((double)f1, (double)f2, (double)f3)).add((double)blockpos1.getX(), (double)blockpos1.getY(), (double)blockpos1.getZ());
            this.world.spawnParticle(Particles.NAUTILUS, vec3d.x, vec3d.y, vec3d.z, vec3d1.x, vec3d1.y, vec3d1.z);
         }
      }

      if (this.target != null) {
         Vec3d vec3d2 = new Vec3d(this.target.posX, this.target.posY + (double)this.target.getEyeHeight(), this.target.posZ);
         float f4 = (-0.5F + random.nextFloat()) * (3.0F + this.target.width);
         float f5 = -1.0F + random.nextFloat() * this.target.height;
         float f6 = (-0.5F + random.nextFloat()) * (3.0F + this.target.width);
         Vec3d vec3d3 = new Vec3d((double)f4, (double)f5, (double)f6);
         this.world.spawnParticle(Particles.NAUTILUS, vec3d2.x, vec3d2.y, vec3d2.z, vec3d3.x, vec3d3.y, vec3d3.z);
      }

   }

   public boolean isActive() {
      return this.active;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isEyeOpen() {
      return this.eyeOpen;
   }

   private void setActive(boolean p_205739_1_) {
      if (p_205739_1_ != this.active) {
         this.playSound(p_205739_1_ ? SoundEvents.BLOCK_CONDUIT_ACTIVATE : SoundEvents.BLOCK_CONDUIT_DEACTIVATE);
      }

      this.active = p_205739_1_;
   }

   private void setEyeOpen(boolean p_207736_1_) {
      this.eyeOpen = p_207736_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public float getActiveRotation(float p_205036_1_) {
      return (this.activeRotation + p_205036_1_) * -0.0375F;
   }

   public void playSound(SoundEvent p_205738_1_) {
      this.world.playSound((EntityPlayer)null, this.pos, p_205738_1_, SoundCategory.BLOCKS, 1.0F, 1.0F);
   }
}