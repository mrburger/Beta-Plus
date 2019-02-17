package net.minecraft.server;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;

public class CustomBossEvent extends BossInfoServer {
   private final ResourceLocation id;
   private final Set<UUID> players = Sets.newHashSet();
   private int value;
   private int max = 100;

   public CustomBossEvent(ResourceLocation idIn, ITextComponent nameIn) {
      super(nameIn, BossInfo.Color.WHITE, BossInfo.Overlay.PROGRESS);
      this.id = idIn;
      this.setPercent(0.0F);
   }

   public ResourceLocation getId() {
      return this.id;
   }

   /**
    * Makes the boss visible to the given player.
    */
   public void addPlayer(EntityPlayerMP player) {
      super.addPlayer(player);
      this.players.add(player.getUniqueID());
   }

   public void addPlayer(UUID player) {
      this.players.add(player);
   }

   /**
    * Makes the boss non-visible to the given player.
    */
   public void removePlayer(EntityPlayerMP player) {
      super.removePlayer(player);
      this.players.remove(player.getUniqueID());
   }

   public void removeAllPlayers() {
      super.removeAllPlayers();
      this.players.clear();
   }

   public int getValue() {
      return this.value;
   }

   public int getMax() {
      return this.max;
   }

   public void setValue(int p_201362_1_) {
      this.value = p_201362_1_;
      this.setPercent(MathHelper.clamp((float)p_201362_1_ / (float)this.max, 0.0F, 1.0F));
   }

   public void setMax(int p_201366_1_) {
      this.max = p_201366_1_;
      this.setPercent(MathHelper.clamp((float)this.value / (float)p_201366_1_, 0.0F, 1.0F));
   }

   public final ITextComponent getFormattedName() {
      return TextComponentUtils.wrapInSquareBrackets(this.getName()).applyTextStyle((p_211569_1_) -> {
         p_211569_1_.setColor(this.getColor().getFormatting()).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(this.getId().toString()))).setInsertion(this.getId().toString());
      });
   }

   public boolean setPlayers(Collection<EntityPlayerMP> p_201368_1_) {
      Set<UUID> set = Sets.newHashSet();
      Set<EntityPlayerMP> set1 = Sets.newHashSet();

      for(UUID uuid : this.players) {
         boolean flag = false;

         for(EntityPlayerMP entityplayermp : p_201368_1_) {
            if (entityplayermp.getUniqueID().equals(uuid)) {
               flag = true;
               break;
            }
         }

         if (!flag) {
            set.add(uuid);
         }
      }

      for(EntityPlayerMP entityplayermp1 : p_201368_1_) {
         boolean flag1 = false;

         for(UUID uuid2 : this.players) {
            if (entityplayermp1.getUniqueID().equals(uuid2)) {
               flag1 = true;
               break;
            }
         }

         if (!flag1) {
            set1.add(entityplayermp1);
         }
      }

      for(UUID uuid1 : set) {
         for(EntityPlayerMP entityplayermp3 : this.getPlayers()) {
            if (entityplayermp3.getUniqueID().equals(uuid1)) {
               this.removePlayer(entityplayermp3);
               break;
            }
         }

         this.players.remove(uuid1);
      }

      for(EntityPlayerMP entityplayermp2 : set1) {
         this.addPlayer(entityplayermp2);
      }

      return !set.isEmpty() || !set1.isEmpty();
   }

   public NBTTagCompound write() {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.setString("Name", ITextComponent.Serializer.toJson(this.name));
      nbttagcompound.setBoolean("Visible", this.isVisible());
      nbttagcompound.setInt("Value", this.value);
      nbttagcompound.setInt("Max", this.max);
      nbttagcompound.setString("Color", this.getColor().getName());
      nbttagcompound.setString("Overlay", this.getOverlay().getName());
      nbttagcompound.setBoolean("DarkenScreen", this.shouldDarkenSky());
      nbttagcompound.setBoolean("PlayBossMusic", this.shouldPlayEndBossMusic());
      nbttagcompound.setBoolean("CreateWorldFog", this.shouldCreateFog());
      NBTTagList nbttaglist = new NBTTagList();

      for(UUID uuid : this.players) {
         nbttaglist.add((INBTBase)NBTUtil.writeUniqueId(uuid));
      }

      nbttagcompound.setTag("Players", nbttaglist);
      return nbttagcompound;
   }

   public static CustomBossEvent read(NBTTagCompound nbt, ResourceLocation idIn) {
      CustomBossEvent custombossevent = new CustomBossEvent(idIn, ITextComponent.Serializer.fromJson(nbt.getString("Name")));
      custombossevent.setVisible(nbt.getBoolean("Visible"));
      custombossevent.setValue(nbt.getInt("Value"));
      custombossevent.setMax(nbt.getInt("Max"));
      custombossevent.setColor(BossInfo.Color.byName(nbt.getString("Color")));
      custombossevent.setOverlay(BossInfo.Overlay.byName(nbt.getString("Overlay")));
      custombossevent.setDarkenSky(nbt.getBoolean("DarkenScreen"));
      custombossevent.setPlayEndBossMusic(nbt.getBoolean("PlayBossMusic"));
      custombossevent.setCreateFog(nbt.getBoolean("CreateWorldFog"));
      NBTTagList nbttaglist = nbt.getList("Players", 10);

      for(int i = 0; i < nbttaglist.size(); ++i) {
         custombossevent.addPlayer(NBTUtil.readUniqueId(nbttaglist.getCompound(i)));
      }

      return custombossevent;
   }

   public void onPlayerLogin(EntityPlayerMP player) {
      if (this.players.contains(player.getUniqueID())) {
         this.addPlayer(player);
      }

   }

   public void onPlayerLogout(EntityPlayerMP player) {
      super.removePlayer(player);
   }
}