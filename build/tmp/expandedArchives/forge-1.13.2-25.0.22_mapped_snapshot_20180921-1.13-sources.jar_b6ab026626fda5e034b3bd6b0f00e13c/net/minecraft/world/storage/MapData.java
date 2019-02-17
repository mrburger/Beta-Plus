package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;

public class MapData extends WorldSavedData {
   public int xCenter;
   public int zCenter;
   public DimensionType dimension;
   public boolean trackingPosition;
   public boolean unlimitedTracking;
   public byte scale;
   /**
    * A flattened 128x128 grid representing the contents of the map. Each byte has format ([index into MapColor.COLORS]
    * << 4 | brightness flag)
    */
   public byte[] colors = new byte[16384];
   /** Holds a reference to the MapInfo of the players who own a copy of the map */
   public List<MapData.MapInfo> playersArrayList = Lists.newArrayList();
   /** Holds a reference to the players who own a copy of the map and a reference to their MapInfo */
   private final Map<EntityPlayer, MapData.MapInfo> playersHashMap = Maps.newHashMap();
   private final Map<String, MapBanner> banners = Maps.newHashMap();
   public Map<String, MapDecoration> mapDecorations = Maps.newLinkedHashMap();
   private final Map<String, MapFrame> field_212442_l = Maps.newHashMap();

   public MapData(String mapname) {
      super(mapname);
   }

   public void func_212440_a(int p_212440_1_, int p_212440_2_, int p_212440_3_, boolean p_212440_4_, boolean p_212440_5_, DimensionType p_212440_6_) {
      this.scale = (byte)p_212440_3_;
      this.calculateMapCenter((double)p_212440_1_, (double)p_212440_2_, this.scale);
      this.dimension = p_212440_6_;
      this.trackingPosition = p_212440_4_;
      this.unlimitedTracking = p_212440_5_;
      this.markDirty();
   }

   public void calculateMapCenter(double x, double z, int mapScale) {
      int i = 128 * (1 << mapScale);
      int j = MathHelper.floor((x + 64.0D) / (double)i);
      int k = MathHelper.floor((z + 64.0D) / (double)i);
      this.xCenter = j * i + i / 2 - 64;
      this.zCenter = k * i + i / 2 - 64;
   }

   /**
    * reads in data from the NBTTagCompound into this MapDataBase
    */
   public void read(NBTTagCompound nbt) {
      this.dimension = DimensionType.getById(nbt.getInt("dimension"));
      this.xCenter = nbt.getInt("xCenter");
      this.zCenter = nbt.getInt("zCenter");
      this.scale = (byte)MathHelper.clamp(nbt.getByte("scale"), 0, 4);
      this.trackingPosition = !nbt.contains("trackingPosition", 1) || nbt.getBoolean("trackingPosition");
      this.unlimitedTracking = nbt.getBoolean("unlimitedTracking");
      this.colors = nbt.getByteArray("colors");
      if (this.colors.length != 16384) {
         this.colors = new byte[16384];
      }

      NBTTagList nbttaglist = nbt.getList("banners", 10);

      for(int i = 0; i < nbttaglist.size(); ++i) {
         MapBanner mapbanner = MapBanner.fromNbt(nbttaglist.getCompound(i));
         this.banners.put(mapbanner.getMapDecorationId(), mapbanner);
         this.updateDecorations(mapbanner.getDecorationType(), (IWorld)null, mapbanner.getMapDecorationId(), (double)mapbanner.getPos().getX(), (double)mapbanner.getPos().getZ(), 180.0D, mapbanner.getName());
      }

      NBTTagList nbttaglist1 = nbt.getList("frames", 10);

      for(int j = 0; j < nbttaglist1.size(); ++j) {
         MapFrame mapframe = MapFrame.func_212765_a(nbttaglist1.getCompound(j));
         this.field_212442_l.put(mapframe.func_212767_e(), mapframe);
         this.updateDecorations(MapDecoration.Type.FRAME, (IWorld)null, "frame-" + mapframe.func_212769_d(), (double)mapframe.func_212764_b().getX(), (double)mapframe.func_212764_b().getZ(), (double)mapframe.func_212768_c(), (ITextComponent)null);
      }

   }

   public NBTTagCompound write(NBTTagCompound compound) {
      compound.setInt("dimension", this.dimension.getId());
      compound.setInt("xCenter", this.xCenter);
      compound.setInt("zCenter", this.zCenter);
      compound.setByte("scale", this.scale);
      compound.setByteArray("colors", this.colors);
      compound.setBoolean("trackingPosition", this.trackingPosition);
      compound.setBoolean("unlimitedTracking", this.unlimitedTracking);
      NBTTagList nbttaglist = new NBTTagList();

      for(MapBanner mapbanner : this.banners.values()) {
         nbttaglist.add((INBTBase)mapbanner.toNbt());
      }

      compound.setTag("banners", nbttaglist);
      NBTTagList nbttaglist1 = new NBTTagList();

      for(MapFrame mapframe : this.field_212442_l.values()) {
         nbttaglist1.add((INBTBase)mapframe.func_212770_a());
      }

      compound.setTag("frames", nbttaglist1);
      return compound;
   }

   /**
    * Adds the player passed to the list of visible players and checks to see which players are visible
    */
   public void updateVisiblePlayers(EntityPlayer player, ItemStack mapStack) {
      if (!this.playersHashMap.containsKey(player)) {
         MapData.MapInfo mapdata$mapinfo = new MapData.MapInfo(player);
         this.playersHashMap.put(player, mapdata$mapinfo);
         this.playersArrayList.add(mapdata$mapinfo);
      }

      if (!player.inventory.hasItemStack(mapStack)) {
         this.mapDecorations.remove(player.getName().getString());
      }

      for(int i = 0; i < this.playersArrayList.size(); ++i) {
         MapData.MapInfo mapdata$mapinfo1 = this.playersArrayList.get(i);
         String s = mapdata$mapinfo1.player.getName().getString();
         if (!mapdata$mapinfo1.player.removed && (mapdata$mapinfo1.player.inventory.hasItemStack(mapStack) || mapStack.isOnItemFrame())) {
            if (!mapStack.isOnItemFrame() && mapdata$mapinfo1.player.dimension == this.dimension && this.trackingPosition) {
               this.updateDecorations(MapDecoration.Type.PLAYER, mapdata$mapinfo1.player.world, s, mapdata$mapinfo1.player.posX, mapdata$mapinfo1.player.posZ, (double)mapdata$mapinfo1.player.rotationYaw, (ITextComponent)null);
            }
         } else {
            this.playersHashMap.remove(mapdata$mapinfo1.player);
            this.playersArrayList.remove(mapdata$mapinfo1);
            this.mapDecorations.remove(s);
         }
      }

      if (mapStack.isOnItemFrame() && this.trackingPosition) {
         EntityItemFrame entityitemframe = mapStack.getItemFrame();
         BlockPos blockpos = entityitemframe.getHangingPosition();
         MapFrame mapframe1 = this.field_212442_l.get(MapFrame.func_212766_a(blockpos));
         if (mapframe1 != null && entityitemframe.getEntityId() != mapframe1.func_212769_d() && this.field_212442_l.containsKey(mapframe1.func_212767_e())) {
            this.mapDecorations.remove("frame-" + mapframe1.func_212769_d());
         }

         MapFrame mapframe = new MapFrame(blockpos, entityitemframe.facingDirection.getHorizontalIndex() * 90, entityitemframe.getEntityId());
         this.updateDecorations(MapDecoration.Type.FRAME, player.world, "frame-" + entityitemframe.getEntityId(), (double)blockpos.getX(), (double)blockpos.getZ(), (double)(entityitemframe.facingDirection.getHorizontalIndex() * 90), (ITextComponent)null);
         this.field_212442_l.put(mapframe.func_212767_e(), mapframe);
      }

      NBTTagCompound nbttagcompound = mapStack.getTag();
      if (nbttagcompound != null && nbttagcompound.contains("Decorations", 9)) {
         NBTTagList nbttaglist = nbttagcompound.getList("Decorations", 10);

         for(int j = 0; j < nbttaglist.size(); ++j) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(j);
            if (!this.mapDecorations.containsKey(nbttagcompound1.getString("id"))) {
               this.updateDecorations(MapDecoration.Type.byIcon(nbttagcompound1.getByte("type")), player.world, nbttagcompound1.getString("id"), nbttagcompound1.getDouble("x"), nbttagcompound1.getDouble("z"), nbttagcompound1.getDouble("rot"), (ITextComponent)null);
            }
         }
      }

   }

   public static void addTargetDecoration(ItemStack map, BlockPos target, String decorationName, MapDecoration.Type type) {
      NBTTagList nbttaglist;
      if (map.hasTag() && map.getTag().contains("Decorations", 9)) {
         nbttaglist = map.getTag().getList("Decorations", 10);
      } else {
         nbttaglist = new NBTTagList();
         map.setTagInfo("Decorations", nbttaglist);
      }

      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.setByte("type", type.getIcon());
      nbttagcompound.setString("id", decorationName);
      nbttagcompound.setDouble("x", (double)target.getX());
      nbttagcompound.setDouble("z", (double)target.getZ());
      nbttagcompound.setDouble("rot", 180.0D);
      nbttaglist.add((INBTBase)nbttagcompound);
      if (type.hasMapColor()) {
         NBTTagCompound nbttagcompound1 = map.getOrCreateChildTag("display");
         nbttagcompound1.setInt("MapColor", type.getMapColor());
      }

   }

   private void updateDecorations(MapDecoration.Type type, @Nullable IWorld worldIn, String decorationName, double worldX, double worldZ, double rotationIn, @Nullable ITextComponent p_191095_10_) {
      int i = 1 << this.scale;
      float f = (float)(worldX - (double)this.xCenter) / (float)i;
      float f1 = (float)(worldZ - (double)this.zCenter) / (float)i;
      byte b0 = (byte)((int)((double)(f * 2.0F) + 0.5D));
      byte b1 = (byte)((int)((double)(f1 * 2.0F) + 0.5D));
      int j = 63;
      byte b2;
      if (f >= -63.0F && f1 >= -63.0F && f <= 63.0F && f1 <= 63.0F) {
         rotationIn = rotationIn + (rotationIn < 0.0D ? -8.0D : 8.0D);
         b2 = (byte)((int)(rotationIn * 16.0D / 360.0D));
         if (worldIn != null && worldIn.getWorld().dimension.shouldMapSpin(decorationName, worldX, worldZ, rotationIn)) {
            int l = (int)(worldIn.getWorld().getDayTime() / 10L);
            b2 = (byte)(l * l * 34187121 + l * 121 >> 15 & 15);
         }
      } else {
         if (type != MapDecoration.Type.PLAYER) {
            this.mapDecorations.remove(decorationName);
            return;
         }

         int k = 320;
         if (Math.abs(f) < 320.0F && Math.abs(f1) < 320.0F) {
            type = MapDecoration.Type.PLAYER_OFF_MAP;
         } else {
            if (!this.unlimitedTracking) {
               this.mapDecorations.remove(decorationName);
               return;
            }

            type = MapDecoration.Type.PLAYER_OFF_LIMITS;
         }

         b2 = 0;
         if (f <= -63.0F) {
            b0 = -128;
         }

         if (f1 <= -63.0F) {
            b1 = -128;
         }

         if (f >= 63.0F) {
            b0 = 127;
         }

         if (f1 >= 63.0F) {
            b1 = 127;
         }
      }

      this.mapDecorations.put(decorationName, new MapDecoration(type, b0, b1, b2, p_191095_10_));
   }

   @Nullable
   public Packet<?> getMapPacket(ItemStack mapStack, IBlockReader worldIn, EntityPlayer player) {
      MapData.MapInfo mapdata$mapinfo = this.playersHashMap.get(player);
      return mapdata$mapinfo == null ? null : mapdata$mapinfo.getPacket(mapStack);
   }

   public void updateMapData(int x, int y) {
      this.markDirty();

      for(MapData.MapInfo mapdata$mapinfo : this.playersArrayList) {
         mapdata$mapinfo.update(x, y);
      }

   }

   public MapData.MapInfo getMapInfo(EntityPlayer player) {
      MapData.MapInfo mapdata$mapinfo = this.playersHashMap.get(player);
      if (mapdata$mapinfo == null) {
         mapdata$mapinfo = new MapData.MapInfo(player);
         this.playersHashMap.put(player, mapdata$mapinfo);
         this.playersArrayList.add(mapdata$mapinfo);
      }

      return mapdata$mapinfo;
   }

   public void tryAddBanner(IWorld p_204269_1_, BlockPos p_204269_2_) {
      float f = (float)p_204269_2_.getX() + 0.5F;
      float f1 = (float)p_204269_2_.getZ() + 0.5F;
      int i = 1 << this.scale;
      float f2 = (f - (float)this.xCenter) / (float)i;
      float f3 = (f1 - (float)this.zCenter) / (float)i;
      int j = 63;
      boolean flag = false;
      if (f2 >= -63.0F && f3 >= -63.0F && f2 <= 63.0F && f3 <= 63.0F) {
         MapBanner mapbanner = MapBanner.fromWorld(p_204269_1_, p_204269_2_);
         if (mapbanner == null) {
            return;
         }

         boolean flag1 = true;
         if (this.banners.containsKey(mapbanner.getMapDecorationId()) && this.banners.get(mapbanner.getMapDecorationId()).equals(mapbanner)) {
            this.banners.remove(mapbanner.getMapDecorationId());
            this.mapDecorations.remove(mapbanner.getMapDecorationId());
            flag1 = false;
            flag = true;
         }

         if (flag1) {
            this.banners.put(mapbanner.getMapDecorationId(), mapbanner);
            this.updateDecorations(mapbanner.getDecorationType(), p_204269_1_, mapbanner.getMapDecorationId(), (double)f, (double)f1, 180.0D, mapbanner.getName());
            flag = true;
         }

         if (flag) {
            this.markDirty();
         }
      }

   }

   public void removeStaleBanners(IBlockReader p_204268_1_, int p_204268_2_, int p_204268_3_) {
      Iterator<MapBanner> iterator = this.banners.values().iterator();

      while(iterator.hasNext()) {
         MapBanner mapbanner = iterator.next();
         if (mapbanner.getPos().getX() == p_204268_2_ && mapbanner.getPos().getZ() == p_204268_3_) {
            MapBanner mapbanner1 = MapBanner.fromWorld(p_204268_1_, mapbanner.getPos());
            if (!mapbanner.equals(mapbanner1)) {
               iterator.remove();
               this.mapDecorations.remove(mapbanner.getMapDecorationId());
            }
         }
      }

   }

   public void func_212441_a(BlockPos p_212441_1_, int p_212441_2_) {
      this.mapDecorations.remove("frame-" + p_212441_2_);
      this.field_212442_l.remove(MapFrame.func_212766_a(p_212441_1_));
   }

   public class MapInfo {
      /** Reference for EntityPlayer object in MapInfo */
      public final EntityPlayer player;
      private boolean isDirty = true;
      /** The lowest dirty x value */
      private int minX;
      /** The lowest dirty z value */
      private int minY;
      /** The highest dirty x value */
      private int maxX = 127;
      /** The highest dirty z value */
      private int maxY = 127;
      private int tick;
      /**
       * Incremented each time the map attempts to rescan its surroundings. Used to reveal new regions over several
       * seconds instead of all at once.
       */
      public int step;

      public MapInfo(EntityPlayer player) {
         this.player = player;
      }

      @Nullable
      public Packet<?> getPacket(ItemStack stack) {
         if (this.isDirty) {
            this.isDirty = false;
            return new SPacketMaps(ItemMap.getMapId(stack), MapData.this.scale, MapData.this.trackingPosition, MapData.this.mapDecorations.values(), MapData.this.colors, this.minX, this.minY, this.maxX + 1 - this.minX, this.maxY + 1 - this.minY);
         } else {
            return this.tick++ % 5 == 0 ? new SPacketMaps(ItemMap.getMapId(stack), MapData.this.scale, MapData.this.trackingPosition, MapData.this.mapDecorations.values(), MapData.this.colors, 0, 0, 0, 0) : null;
         }
      }

      public void update(int x, int y) {
         if (this.isDirty) {
            this.minX = Math.min(this.minX, x);
            this.minY = Math.min(this.minY, y);
            this.maxX = Math.max(this.maxX, x);
            this.maxY = Math.max(this.maxY, y);
         } else {
            this.isDirty = true;
            this.minX = x;
            this.minY = y;
            this.maxX = x;
            this.maxY = y;
         }

      }
   }
}