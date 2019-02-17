package net.minecraft.tileentity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.ITickable;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntitySkull extends TileEntity implements ITickable {
   private GameProfile playerProfile;
   private int dragonAnimatedTicks;
   private boolean dragonAnimated;
   private boolean shouldDrop = true;
   private static PlayerProfileCache profileCache;
   private static MinecraftSessionService sessionService;

   public TileEntitySkull() {
      super(TileEntityType.SKULL);
   }

   public static void setProfileCache(PlayerProfileCache profileCacheIn) {
      profileCache = profileCacheIn;
   }

   public static void setSessionService(MinecraftSessionService sessionServiceIn) {
      sessionService = sessionServiceIn;
   }

   public NBTTagCompound write(NBTTagCompound compound) {
      super.write(compound);
      if (this.playerProfile != null) {
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         NBTUtil.writeGameProfile(nbttagcompound, this.playerProfile);
         compound.setTag("Owner", nbttagcompound);
      }

      return compound;
   }

   public void read(NBTTagCompound compound) {
      super.read(compound);
      if (compound.contains("Owner", 10)) {
         this.setPlayerProfile(NBTUtil.readGameProfile(compound.getCompound("Owner")));
      } else if (compound.contains("ExtraType", 8)) {
         String s = compound.getString("ExtraType");
         if (!StringUtils.isNullOrEmpty(s)) {
            this.setPlayerProfile(new GameProfile((UUID)null, s));
         }
      }

   }

   public void tick() {
      Block block = this.getBlockState().getBlock();
      if (block == Blocks.DRAGON_HEAD || block == Blocks.DRAGON_WALL_HEAD) {
         if (this.world.isBlockPowered(this.pos)) {
            this.dragonAnimated = true;
            ++this.dragonAnimatedTicks;
         } else {
            this.dragonAnimated = false;
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public float getAnimationProgress(float p_184295_1_) {
      return this.dragonAnimated ? (float)this.dragonAnimatedTicks + p_184295_1_ : (float)this.dragonAnimatedTicks;
   }

   @Nullable
   public GameProfile getPlayerProfile() {
      return this.playerProfile;
   }

   /**
    * Retrieves packet to send to the client whenever this Tile Entity is resynced via World.notifyBlockUpdate. For
    * modded TE's, this packet comes back to you clientside in {@link #onDataPacket}
    */
   @Nullable
   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.pos, 4, this.getUpdateTag());
   }

   /**
    * Get an NBT compound to sync to the client with SPacketChunkData, used for initial loading of the chunk or when
    * many blocks change at once. This compound comes back to you clientside in {@link handleUpdateTag}
    */
   public NBTTagCompound getUpdateTag() {
      return this.write(new NBTTagCompound());
   }

   public void setPlayerProfile(@Nullable GameProfile p_195485_1_) {
      this.playerProfile = p_195485_1_;
      this.updatePlayerProfile();
   }

   private void updatePlayerProfile() {
      this.playerProfile = updateGameProfile(this.playerProfile);
      this.markDirty();
   }

   public static GameProfile updateGameProfile(GameProfile input) {
      if (input != null && !StringUtils.isNullOrEmpty(input.getName())) {
         if (input.isComplete() && input.getProperties().containsKey("textures")) {
            return input;
         } else if (profileCache != null && sessionService != null) {
            GameProfile gameprofile = profileCache.getGameProfileForUsername(input.getName());
            if (gameprofile == null) {
               return input;
            } else {
               Property property = Iterables.getFirst(gameprofile.getProperties().get("textures"), (Property)null);
               if (property == null) {
                  gameprofile = sessionService.fillProfileProperties(gameprofile, true);
               }

               return gameprofile;
            }
         } else {
            return input;
         }
      } else {
         return input;
      }
   }

   public static void disableDrop(IBlockReader p_195486_0_, BlockPos p_195486_1_) {
      TileEntity tileentity = p_195486_0_.getTileEntity(p_195486_1_);
      if (tileentity instanceof TileEntitySkull) {
         TileEntitySkull tileentityskull = (TileEntitySkull)tileentity;
         tileentityskull.shouldDrop = false;
      }

   }

   public boolean shouldDrop() {
      return this.shouldDrop;
   }
}