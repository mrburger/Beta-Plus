package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientSuggestionProvider implements ISuggestionProvider {
   private final NetHandlerPlayClient connection;
   private final Minecraft mc;
   private int field_197017_b = -1;
   private CompletableFuture<Suggestions> field_197018_c;

   public ClientSuggestionProvider(NetHandlerPlayClient p_i49558_1_, Minecraft p_i49558_2_) {
      this.connection = p_i49558_1_;
      this.mc = p_i49558_2_;
   }

   public Collection<String> getPlayerNames() {
      List<String> list = Lists.newArrayList();

      for(NetworkPlayerInfo networkplayerinfo : this.connection.getPlayerInfoMap()) {
         list.add(networkplayerinfo.getGameProfile().getName());
      }

      return list;
   }

   public Collection<String> getTargetedEntity() {
      return (Collection<String>)(this.mc.objectMouseOver != null && this.mc.objectMouseOver.type == RayTraceResult.Type.ENTITY ? Collections.singleton(this.mc.objectMouseOver.entity.getCachedUniqueIdString()) : Collections.emptyList());
   }

   public Collection<String> getTeamNames() {
      return this.connection.getWorld().getScoreboard().getTeamNames();
   }

   public Collection<ResourceLocation> getSoundResourceLocations() {
      return this.mc.getSoundHandler().getAvailableSounds();
   }

   public Collection<ResourceLocation> getRecipeResourceLocations() {
      return this.connection.getRecipeManager().getIds();
   }

   public boolean hasPermissionLevel(int p_197034_1_) {
      EntityPlayerSP entityplayersp = this.mc.player;
      return entityplayersp != null ? entityplayersp.hasPermissionLevel(p_197034_1_) : p_197034_1_ == 0;
   }

   public CompletableFuture<Suggestions> getSuggestionsFromServer(CommandContext<ISuggestionProvider> context, SuggestionsBuilder suggestionsBuilder) {
      if (this.field_197018_c != null) {
         this.field_197018_c.cancel(false);
      }

      this.field_197018_c = new CompletableFuture<>();
      int i = ++this.field_197017_b;
      this.connection.sendPacket(new CPacketTabComplete(i, context.getInput()));
      return this.field_197018_c;
   }

   private static String func_209001_a(double p_209001_0_) {
      return String.format(Locale.ROOT, "%.2f", p_209001_0_);
   }

   private static String func_209002_a(int p_209002_0_) {
      return Integer.toString(p_209002_0_);
   }

   public Collection<ISuggestionProvider.Coordinates> getCoordinates(boolean allowFloatCoords) {
      if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.type == RayTraceResult.Type.BLOCK) {
         if (allowFloatCoords) {
            Vec3d vec3d = this.mc.objectMouseOver.hitVec;
            return Collections.singleton(new ISuggestionProvider.Coordinates(func_209001_a(vec3d.x), func_209001_a(vec3d.y), func_209001_a(vec3d.z)));
         } else {
            BlockPos blockpos = this.mc.objectMouseOver.getBlockPos();
            return Collections.singleton(new ISuggestionProvider.Coordinates(func_209002_a(blockpos.getX()), func_209002_a(blockpos.getY()), func_209002_a(blockpos.getZ())));
         }
      } else {
         return Collections.singleton(ISuggestionProvider.Coordinates.DEFAULT_GLOBAL);
      }
   }

   public void func_197015_a(int p_197015_1_, Suggestions p_197015_2_) {
      if (p_197015_1_ == this.field_197017_b) {
         this.field_197018_c.complete(p_197015_2_);
         this.field_197018_c = null;
         this.field_197017_b = -1;
      }

   }
}