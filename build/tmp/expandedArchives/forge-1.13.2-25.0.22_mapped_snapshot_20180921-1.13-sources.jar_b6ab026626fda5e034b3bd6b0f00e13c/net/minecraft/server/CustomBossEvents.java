package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class CustomBossEvents {
   private final MinecraftServer server;
   private final Map<ResourceLocation, CustomBossEvent> bars = Maps.newHashMap();

   public CustomBossEvents(MinecraftServer serverIn) {
      this.server = serverIn;
   }

   @Nullable
   public CustomBossEvent get(ResourceLocation id) {
      return this.bars.get(id);
   }

   public CustomBossEvent add(ResourceLocation id, ITextComponent p_201379_2_) {
      CustomBossEvent custombossevent = new CustomBossEvent(id, p_201379_2_);
      this.bars.put(id, custombossevent);
      return custombossevent;
   }

   public void remove(CustomBossEvent bossbar) {
      this.bars.remove(bossbar.getId());
   }

   public Collection<ResourceLocation> getIDs() {
      return this.bars.keySet();
   }

   public Collection<CustomBossEvent> getBossbars() {
      return this.bars.values();
   }

   public NBTTagCompound write() {
      NBTTagCompound nbttagcompound = new NBTTagCompound();

      for(CustomBossEvent custombossevent : this.bars.values()) {
         nbttagcompound.setTag(custombossevent.getId().toString(), custombossevent.write());
      }

      return nbttagcompound;
   }

   public void read(NBTTagCompound p_201381_1_) {
      for(String s : p_201381_1_.keySet()) {
         ResourceLocation resourcelocation = new ResourceLocation(s);
         this.bars.put(resourcelocation, CustomBossEvent.read(p_201381_1_.getCompound(s), resourcelocation));
      }

   }

   public void onPlayerLogin(EntityPlayerMP player) {
      for(CustomBossEvent custombossevent : this.bars.values()) {
         custombossevent.onPlayerLogin(player);
      }

   }

   public void onPlayerLogout(EntityPlayerMP player) {
      for(CustomBossEvent custombossevent : this.bars.values()) {
         custombossevent.onPlayerLogout(player);
      }

   }
}