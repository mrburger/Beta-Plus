package net.minecraft.tileentity;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TileEntity extends net.minecraftforge.common.capabilities.CapabilityProvider<TileEntity> implements net.minecraftforge.common.extensions.IForgeTileEntity {
   private static final Logger LOGGER = LogManager.getLogger();
   private final TileEntityType<?> type;
   /** the instance of the world the tile entity is in. */
   protected World world;
   protected BlockPos pos = BlockPos.ORIGIN;
   protected boolean removed;
   @Nullable
   private IBlockState cachedBlockState;
   private NBTTagCompound customTileData;


   public TileEntity(TileEntityType<?> tileEntityTypeIn) {
      super(TileEntity.class);
      this.type = tileEntityTypeIn;
      this.gatherCapabilities();
   }

   /**
    * Returns the worldObj for this tileEntity.
    */
   @Nullable
   public World getWorld() {
      return this.world;
   }

   /**
    * Sets the worldObj for this tileEntity.
    */
   public void setWorld(World worldIn) {
      this.world = worldIn;
   }

   /**
    * Returns true if the worldObj isn't null.
    */
   public boolean hasWorld() {
      return this.world != null;
   }

   public void read(NBTTagCompound compound) {
      this.pos = new BlockPos(compound.getInt("x"), compound.getInt("y"), compound.getInt("z"));
      if (compound.hasKey("ForgeData")) this.customTileData = compound.getCompound("ForgeData");
      if (getCapabilities() != null && compound.hasKey("ForgeCaps")) deserializeCaps(compound.getCompound("ForgeCaps"));
   }

   public NBTTagCompound write(NBTTagCompound compound) {
      return this.writeInternal(compound);
   }

   private NBTTagCompound writeInternal(NBTTagCompound compound) {
      ResourceLocation resourcelocation = TileEntityType.getId(this.getType());
      if (resourcelocation == null) {
         throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
      } else {
         compound.setString("id", resourcelocation.toString());
         compound.setInt("x", this.pos.getX());
         compound.setInt("y", this.pos.getY());
         compound.setInt("z", this.pos.getZ());
         if (this.customTileData != null) compound.setTag("ForgeData", this.customTileData);
         if (getCapabilities() != null) compound.setTag("ForgeCaps", serializeCaps());
         return compound;
      }
   }

   @Nullable
   public static TileEntity create(NBTTagCompound tag) {
      TileEntity tileentity = null;
      String s = tag.getString("id");

      try {
         tileentity = TileEntityType.create(s);
      } catch (Throwable throwable1) {
         LOGGER.error("Failed to create block entity {}", s, throwable1);
      }

      if (tileentity != null) {
         try {
            tileentity.read(tag);
         } catch (Throwable throwable) {
            LOGGER.error("Failed to load data for block entity {}", s, throwable);
            tileentity = null;
         }
      } else {
         LOGGER.warn("Skipping BlockEntity with id {}", (Object)s);
      }

      return tileentity;
   }

   /**
    * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
    * hasn't changed and skip it.
    */
   public void markDirty() {
      if (this.world != null) {
         this.cachedBlockState = this.world.getBlockState(this.pos);
         this.world.markChunkDirty(this.pos, this);
         if (!this.cachedBlockState.isAir(world, this.pos)) {
            this.world.updateComparatorOutputLevel(this.pos, this.cachedBlockState.getBlock());
         }
      }

   }

   /**
    * Returns the square of the distance between this entity and the passed in coordinates.
    */
   public double getDistanceSq(double x, double y, double z) {
      double d0 = (double)this.pos.getX() + 0.5D - x;
      double d1 = (double)this.pos.getY() + 0.5D - y;
      double d2 = (double)this.pos.getZ() + 0.5D - z;
      return d0 * d0 + d1 * d1 + d2 * d2;
   }

   @OnlyIn(Dist.CLIENT)
   public double getMaxRenderDistanceSquared() {
      return 4096.0D;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public IBlockState getBlockState() {
      if (this.cachedBlockState == null) {
         this.cachedBlockState = this.world.getBlockState(this.pos);
      }

      return this.cachedBlockState;
   }

   /**
    * Retrieves packet to send to the client whenever this Tile Entity is resynced via World.notifyBlockUpdate. For
    * modded TE's, this packet comes back to you clientside in {@link #onDataPacket}
    */
   @Nullable
   public SPacketUpdateTileEntity getUpdatePacket() {
      return null;
   }

   /**
    * Get an NBT compound to sync to the client with SPacketChunkData, used for initial loading of the chunk or when
    * many blocks change at once. This compound comes back to you clientside in {@link handleUpdateTag}
    */
   public NBTTagCompound getUpdateTag() {
      return this.writeInternal(new NBTTagCompound());
   }

   public boolean isRemoved() {
      return this.removed;
   }

   /**
    * invalidates a tile entity
    */
   public void remove() {
      this.removed = true;
      this.invalidateCaps();
   }

   /**
    * validates a tile entity
    */
   public void validate() {
      this.removed = false;
   }

   /**
    * See {@link Block#eventReceived} for more information. This must return true serverside before it is called
    * clientside.
    */
   public boolean receiveClientEvent(int id, int type) {
      return false;
   }

   public void updateContainingBlockInfo() {
      this.cachedBlockState = null;
   }

   public void addInfoToCrashReport(CrashReportCategory reportCategory) {
      reportCategory.addDetail("Name", () -> {
         return IRegistry.field_212626_o.getKey(this.getType()) + " // " + this.getClass().getCanonicalName();
      });
      if (this.world != null) {
         CrashReportCategory.addBlockInfo(reportCategory, this.pos, this.getBlockState());
         CrashReportCategory.addBlockInfo(reportCategory, this.pos, this.world.getBlockState(this.pos));
      }
   }

   public void setPos(BlockPos posIn) {
      this.pos = posIn.toImmutable();
   }

   public boolean onlyOpsCanSetNbt() {
      return false;
   }

   public void rotate(Rotation rotationIn) {
   }

   public void mirror(Mirror mirrorIn) {
   }

   public TileEntityType<?> getType() {
      return this.type;
   }

   @Override
   public NBTTagCompound getTileData() {
      if (this.customTileData == null)
         this.customTileData = new NBTTagCompound();
      return this.customTileData;
   }
}