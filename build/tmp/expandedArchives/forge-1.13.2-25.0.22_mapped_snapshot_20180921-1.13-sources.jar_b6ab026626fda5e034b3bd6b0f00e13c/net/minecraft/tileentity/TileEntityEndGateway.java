package net.minecraft.tileentity;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.EndDimension;
import net.minecraft.world.gen.feature.EndGatewayConfig;
import net.minecraft.world.gen.feature.EndIslandFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TileEntityEndGateway extends TileEntityEndPortal implements ITickable {
   private static final Logger LOGGER = LogManager.getLogger();
   private long age;
   private int teleportCooldown;
   private BlockPos exitPortal;
   private boolean exactTeleport;

   public TileEntityEndGateway() {
      super(TileEntityType.END_GATEWAY);
   }

   public NBTTagCompound write(NBTTagCompound compound) {
      super.write(compound);
      compound.setLong("Age", this.age);
      if (this.exitPortal != null) {
         compound.setTag("ExitPortal", NBTUtil.writeBlockPos(this.exitPortal));
      }

      if (this.exactTeleport) {
         compound.setBoolean("ExactTeleport", this.exactTeleport);
      }

      return compound;
   }

   public void read(NBTTagCompound compound) {
      super.read(compound);
      this.age = compound.getLong("Age");
      if (compound.contains("ExitPortal", 10)) {
         this.exitPortal = NBTUtil.readBlockPos(compound.getCompound("ExitPortal"));
      }

      this.exactTeleport = compound.getBoolean("ExactTeleport");
   }

   @OnlyIn(Dist.CLIENT)
   public double getMaxRenderDistanceSquared() {
      return 65536.0D;
   }

   public void tick() {
      boolean flag = this.isSpawning();
      boolean flag1 = this.isCoolingDown();
      ++this.age;
      if (flag1) {
         --this.teleportCooldown;
      } else if (!this.world.isRemote) {
         List<Entity> list = this.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(this.getPos()));
         if (!list.isEmpty()) {
            this.teleportEntity(list.get(0));
         }

         if (this.age % 2400L == 0L) {
            this.triggerCooldown();
         }
      }

      if (flag != this.isSpawning() || flag1 != this.isCoolingDown()) {
         this.markDirty();
      }

   }

   public boolean isSpawning() {
      return this.age < 200L;
   }

   public boolean isCoolingDown() {
      return this.teleportCooldown > 0;
   }

   @OnlyIn(Dist.CLIENT)
   public float getSpawnPercent(float partialTicks) {
      return MathHelper.clamp(((float)this.age + partialTicks) / 200.0F, 0.0F, 1.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public float getCooldownPercent(float partialTicks) {
      return 1.0F - MathHelper.clamp(((float)this.teleportCooldown - partialTicks) / 40.0F, 0.0F, 1.0F);
   }

   /**
    * Retrieves packet to send to the client whenever this Tile Entity is resynced via World.notifyBlockUpdate. For
    * modded TE's, this packet comes back to you clientside in {@link #onDataPacket}
    */
   @Nullable
   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.pos, 8, this.getUpdateTag());
   }

   /**
    * Get an NBT compound to sync to the client with SPacketChunkData, used for initial loading of the chunk or when
    * many blocks change at once. This compound comes back to you clientside in {@link handleUpdateTag}
    */
   public NBTTagCompound getUpdateTag() {
      return this.write(new NBTTagCompound());
   }

   public void triggerCooldown() {
      if (!this.world.isRemote) {
         this.teleportCooldown = 40;
         this.world.addBlockEvent(this.getPos(), this.getBlockState().getBlock(), 1, 0);
         this.markDirty();
      }

   }

   /**
    * See {@link Block#eventReceived} for more information. This must return true serverside before it is called
    * clientside.
    */
   public boolean receiveClientEvent(int id, int type) {
      if (id == 1) {
         this.teleportCooldown = 40;
         return true;
      } else {
         return super.receiveClientEvent(id, type);
      }
   }

   public void teleportEntity(Entity entityIn) {
      if (!this.world.isRemote && !this.isCoolingDown()) {
         this.teleportCooldown = 100;
         if (this.exitPortal == null && this.world.dimension instanceof EndDimension) {
            this.findExitPortal();
         }

         if (this.exitPortal != null) {
            BlockPos blockpos = this.exactTeleport ? this.exitPortal : this.findExitPosition();
            entityIn.setPositionAndUpdate((double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.5D, (double)blockpos.getZ() + 0.5D);
         }

         this.triggerCooldown();
      }
   }

   private BlockPos findExitPosition() {
      BlockPos blockpos = findHighestBlock(this.world, this.exitPortal, 5, false);
      LOGGER.debug("Best exit position for portal at {} is {}", this.exitPortal, blockpos);
      return blockpos.up();
   }

   private void findExitPortal() {
      Vec3d vec3d = (new Vec3d((double)this.getPos().getX(), 0.0D, (double)this.getPos().getZ())).normalize();
      Vec3d vec3d1 = vec3d.scale(1024.0D);

      for(int i = 16; getChunk(this.world, vec3d1).getTopFilledSegment() > 0 && i-- > 0; vec3d1 = vec3d1.add(vec3d.scale(-16.0D))) {
         LOGGER.debug("Skipping backwards past nonempty chunk at {}", (Object)vec3d1);
      }

      for(int j = 16; getChunk(this.world, vec3d1).getTopFilledSegment() == 0 && j-- > 0; vec3d1 = vec3d1.add(vec3d.scale(16.0D))) {
         LOGGER.debug("Skipping forward past empty chunk at {}", (Object)vec3d1);
      }

      LOGGER.debug("Found chunk at {}", (Object)vec3d1);
      Chunk chunk = getChunk(this.world, vec3d1);
      this.exitPortal = findSpawnpointInChunk(chunk);
      if (this.exitPortal == null) {
         this.exitPortal = new BlockPos(vec3d1.x + 0.5D, 75.0D, vec3d1.z + 0.5D);
         LOGGER.debug("Failed to find suitable block, settling on {}", (Object)this.exitPortal);
         (new EndIslandFeature()).func_212245_a(this.world, this.world.getChunkProvider().getChunkGenerator(), new Random(this.exitPortal.toLong()), this.exitPortal, IFeatureConfig.NO_FEATURE_CONFIG);
      } else {
         LOGGER.debug("Found block at {}", (Object)this.exitPortal);
      }

      this.exitPortal = findHighestBlock(this.world, this.exitPortal, 16, true);
      LOGGER.debug("Creating portal at {}", (Object)this.exitPortal);
      this.exitPortal = this.exitPortal.up(10);
      this.createExitPortal(this.exitPortal);
      this.markDirty();
   }

   private static BlockPos findHighestBlock(IBlockReader worldIn, BlockPos posIn, int radius, boolean allowBedrock) {
      BlockPos blockpos = null;

      for(int i = -radius; i <= radius; ++i) {
         for(int j = -radius; j <= radius; ++j) {
            if (i != 0 || j != 0 || allowBedrock) {
               for(int k = 255; k > (blockpos == null ? 0 : blockpos.getY()); --k) {
                  BlockPos blockpos1 = new BlockPos(posIn.getX() + i, k, posIn.getZ() + j);
                  IBlockState iblockstate = worldIn.getBlockState(blockpos1);
                  if (iblockstate.isBlockNormalCube() && (allowBedrock || iblockstate.getBlock() != Blocks.BEDROCK)) {
                     blockpos = blockpos1;
                     break;
                  }
               }
            }
         }
      }

      return blockpos == null ? posIn : blockpos;
   }

   private static Chunk getChunk(World worldIn, Vec3d vec3) {
      return worldIn.getChunk(MathHelper.floor(vec3.x / 16.0D), MathHelper.floor(vec3.z / 16.0D));
   }

   @Nullable
   private static BlockPos findSpawnpointInChunk(Chunk chunkIn) {
      BlockPos blockpos = new BlockPos(chunkIn.x * 16, 30, chunkIn.z * 16);
      int i = chunkIn.getTopFilledSegment() + 16 - 1;
      BlockPos blockpos1 = new BlockPos(chunkIn.x * 16 + 16 - 1, i, chunkIn.z * 16 + 16 - 1);
      BlockPos blockpos2 = null;
      double d0 = 0.0D;

      for(BlockPos blockpos3 : BlockPos.getAllInBox(blockpos, blockpos1)) {
         IBlockState iblockstate = chunkIn.getBlockState(blockpos3);
         if (iblockstate.getBlock() == Blocks.END_STONE && !chunkIn.getBlockState(blockpos3.up(1)).isBlockNormalCube() && !chunkIn.getBlockState(blockpos3.up(2)).isBlockNormalCube()) {
            double d1 = blockpos3.distanceSqToCenter(0.0D, 0.0D, 0.0D);
            if (blockpos2 == null || d1 < d0) {
               blockpos2 = blockpos3;
               d0 = d1;
            }
         }
      }

      return blockpos2;
   }

   private void createExitPortal(BlockPos posIn) {
      Feature.END_GATEWAY.func_212245_a(this.world, this.world.getChunkProvider().getChunkGenerator(), new Random(), posIn, new EndGatewayConfig(false));
      TileEntity tileentity = this.world.getTileEntity(posIn);
      if (tileentity instanceof TileEntityEndGateway) {
         TileEntityEndGateway tileentityendgateway = (TileEntityEndGateway)tileentity;
         tileentityendgateway.exitPortal = new BlockPos(this.getPos());
         tileentityendgateway.markDirty();
      } else {
         LOGGER.warn("Couldn't save exit portal at {}", (Object)posIn);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldRenderFace(EnumFacing face) {
      return Block.shouldSideBeRendered(this.getBlockState(), this.world, this.getPos(), face);
   }

   @OnlyIn(Dist.CLIENT)
   public int getParticleAmount() {
      int i = 0;

      for(EnumFacing enumfacing : EnumFacing.values()) {
         i += this.shouldRenderFace(enumfacing) ? 1 : 0;
      }

      return i;
   }

   public void setExitPortal(BlockPos exitPortalIn) {
      this.exactTeleport = true;
      this.exitPortal = exitPortalIn;
   }
}