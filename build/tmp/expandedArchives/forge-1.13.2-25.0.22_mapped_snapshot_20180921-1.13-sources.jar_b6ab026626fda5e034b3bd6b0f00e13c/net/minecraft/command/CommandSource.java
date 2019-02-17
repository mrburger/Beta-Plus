package net.minecraft.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.BinaryOperator;
import javax.annotation.Nullable;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;

public class CommandSource implements ISuggestionProvider {
   public static final SimpleCommandExceptionType REQUIRES_PLAYER_EXCEPTION_TYPE = new SimpleCommandExceptionType(new TextComponentTranslation("permissions.requires.player"));
   public static final SimpleCommandExceptionType REQUIRES_ENTITY_EXCEPTION_TYPE = new SimpleCommandExceptionType(new TextComponentTranslation("permissions.requires.entity"));
   private final ICommandSource source;
   private final Vec3d pos;
   private final WorldServer world;
   private final int permissionLevel;
   private final String name;
   private final ITextComponent displayName;
   private final MinecraftServer server;
   private final boolean feedbackDisabled;
   @Nullable
   private final Entity entity;
   private final ResultConsumer<CommandSource> resultConsumer;
   private final EntityAnchorArgument.Type entityAnchorType;
   private final Vec2f pitchYaw;

   public CommandSource(ICommandSource p_i49552_1_, Vec3d p_i49552_2_, Vec2f p_i49552_3_, WorldServer p_i49552_4_, int p_i49552_5_, String p_i49552_6_, ITextComponent p_i49552_7_, MinecraftServer p_i49552_8_, @Nullable Entity p_i49552_9_) {
      this(p_i49552_1_, p_i49552_2_, p_i49552_3_, p_i49552_4_, p_i49552_5_, p_i49552_6_, p_i49552_7_, p_i49552_8_, p_i49552_9_, false, (p_197032_0_, p_197032_1_, p_197032_2_) -> {
      }, EntityAnchorArgument.Type.FEET);
   }

   protected CommandSource(ICommandSource p_i49553_1_, Vec3d p_i49553_2_, Vec2f p_i49553_3_, WorldServer p_i49553_4_, int p_i49553_5_, String p_i49553_6_, ITextComponent p_i49553_7_, MinecraftServer p_i49553_8_, @Nullable Entity p_i49553_9_, boolean p_i49553_10_, ResultConsumer<CommandSource> p_i49553_11_, EntityAnchorArgument.Type p_i49553_12_) {
      this.source = p_i49553_1_;
      this.pos = p_i49553_2_;
      this.world = p_i49553_4_;
      this.feedbackDisabled = p_i49553_10_;
      this.entity = p_i49553_9_;
      this.permissionLevel = p_i49553_5_;
      this.name = p_i49553_6_;
      this.displayName = p_i49553_7_;
      this.server = p_i49553_8_;
      this.resultConsumer = p_i49553_11_;
      this.entityAnchorType = p_i49553_12_;
      this.pitchYaw = p_i49553_3_;
   }

   public CommandSource withEntity(Entity entityIn) {
      return this.entity == entityIn ? this : new CommandSource(this.source, this.pos, this.pitchYaw, this.world, this.permissionLevel, entityIn.getName().getString(), entityIn.getDisplayName(), this.server, entityIn, this.feedbackDisabled, this.resultConsumer, this.entityAnchorType);
   }

   public CommandSource withPos(Vec3d posIn) {
      return this.pos.equals(posIn) ? this : new CommandSource(this.source, posIn, this.pitchYaw, this.world, this.permissionLevel, this.name, this.displayName, this.server, this.entity, this.feedbackDisabled, this.resultConsumer, this.entityAnchorType);
   }

   public CommandSource withPitchYaw(Vec2f pitchYawIn) {
      return this.pitchYaw.equals(pitchYawIn) ? this : new CommandSource(this.source, this.pos, pitchYawIn, this.world, this.permissionLevel, this.name, this.displayName, this.server, this.entity, this.feedbackDisabled, this.resultConsumer, this.entityAnchorType);
   }

   public CommandSource withResultConsumer(ResultConsumer<CommandSource> resultConsumerIn) {
      return this.resultConsumer.equals(resultConsumerIn) ? this : new CommandSource(this.source, this.pos, this.pitchYaw, this.world, this.permissionLevel, this.name, this.displayName, this.server, this.entity, this.feedbackDisabled, resultConsumerIn, this.entityAnchorType);
   }

   public CommandSource withResultConsumer(ResultConsumer<CommandSource> resultConsumerIn, BinaryOperator<ResultConsumer<CommandSource>> resultConsumerSelector) {
      ResultConsumer<CommandSource> resultconsumer = resultConsumerSelector.apply(this.resultConsumer, resultConsumerIn);
      return this.withResultConsumer(resultconsumer);
   }

   public CommandSource withFeedbackDisabled() {
      return this.feedbackDisabled ? this : new CommandSource(this.source, this.pos, this.pitchYaw, this.world, this.permissionLevel, this.name, this.displayName, this.server, this.entity, true, this.resultConsumer, this.entityAnchorType);
   }

   public CommandSource withPermissionLevel(int p_197033_1_) {
      return p_197033_1_ == this.permissionLevel ? this : new CommandSource(this.source, this.pos, this.pitchYaw, this.world, p_197033_1_, this.name, this.displayName, this.server, this.entity, this.feedbackDisabled, this.resultConsumer, this.entityAnchorType);
   }

   public CommandSource withMinPermissionLevel(int p_197026_1_) {
      return p_197026_1_ <= this.permissionLevel ? this : new CommandSource(this.source, this.pos, this.pitchYaw, this.world, p_197026_1_, this.name, this.displayName, this.server, this.entity, this.feedbackDisabled, this.resultConsumer, this.entityAnchorType);
   }

   public CommandSource withEntityAnchorType(EntityAnchorArgument.Type entityAnchorTypeIn) {
      return entityAnchorTypeIn == this.entityAnchorType ? this : new CommandSource(this.source, this.pos, this.pitchYaw, this.world, this.permissionLevel, this.name, this.displayName, this.server, this.entity, this.feedbackDisabled, this.resultConsumer, entityAnchorTypeIn);
   }

   public CommandSource withWorld(WorldServer worldIn) {
      return worldIn == this.world ? this : new CommandSource(this.source, this.pos, this.pitchYaw, worldIn, this.permissionLevel, this.name, this.displayName, this.server, this.entity, this.feedbackDisabled, this.resultConsumer, this.entityAnchorType);
   }

   public CommandSource withPitchYaw(Entity entityIn, EntityAnchorArgument.Type anchorType) throws CommandSyntaxException {
      return this.withPitchYaw(anchorType.apply(entityIn));
   }

   public CommandSource withPitchYaw(Vec3d lookPos) throws CommandSyntaxException {
      Vec3d vec3d = this.entityAnchorType.apply(this);
      double d0 = lookPos.x - vec3d.x;
      double d1 = lookPos.y - vec3d.y;
      double d2 = lookPos.z - vec3d.z;
      double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
      float f = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(d1, d3) * (double)(180F / (float)Math.PI))));
      float f1 = MathHelper.wrapDegrees((float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F);
      return this.withPitchYaw(new Vec2f(f, f1));
   }

   public ITextComponent getDisplayName() {
      return this.displayName;
   }

   public String getName() {
      return this.name;
   }

   public boolean hasPermissionLevel(int p_197034_1_) {
      return this.permissionLevel >= p_197034_1_;
   }

   public Vec3d getPos() {
      return this.pos;
   }

   public WorldServer getWorld() {
      return this.world;
   }

   @Nullable
   public Entity getEntity() {
      return this.entity;
   }

   public Entity assertIsEntity() throws CommandSyntaxException {
      if (this.entity == null) {
         throw REQUIRES_ENTITY_EXCEPTION_TYPE.create();
      } else {
         return this.entity;
      }
   }

   public EntityPlayerMP asPlayer() throws CommandSyntaxException {
      if (!(this.entity instanceof EntityPlayerMP)) {
         throw REQUIRES_PLAYER_EXCEPTION_TYPE.create();
      } else {
         return (EntityPlayerMP)this.entity;
      }
   }

   public Vec2f getPitchYaw() {
      return this.pitchYaw;
   }

   public MinecraftServer getServer() {
      return this.server;
   }

   public EntityAnchorArgument.Type getEntityAnchorType() {
      return this.entityAnchorType;
   }

   public void sendFeedback(ITextComponent message, boolean allowLogging) {
      if (this.source.shouldReceiveFeedback() && !this.feedbackDisabled) {
         this.source.sendMessage(message);
      }

      if (allowLogging && this.source.allowLogging() && !this.feedbackDisabled) {
         this.logFeedback(message);
      }

   }

   private void logFeedback(ITextComponent message) {
      ITextComponent itextcomponent = (new TextComponentTranslation("chat.type.admin", this.getDisplayName(), message)).applyTextStyles(new TextFormatting[]{TextFormatting.GRAY, TextFormatting.ITALIC});
      if (this.server.getGameRules().getBoolean("sendCommandFeedback")) {
         for(EntityPlayerMP entityplayermp : this.server.getPlayerList().getPlayers()) {
            if (entityplayermp != this.source && this.server.getPlayerList().canSendCommands(entityplayermp.getGameProfile())) {
               entityplayermp.sendMessage(itextcomponent);
            }
         }
      }

      if (this.source != this.server && this.server.getGameRules().getBoolean("logAdminCommands")) {
         this.server.sendMessage(itextcomponent);
      }

   }

   public void sendErrorMessage(ITextComponent message) {
      if (this.source.shouldReceiveErrors() && !this.feedbackDisabled) {
         this.source.sendMessage((new TextComponentString("")).appendSibling(message).applyTextStyle(TextFormatting.RED));
      }

   }

   public void onCommandComplete(CommandContext<CommandSource> context, boolean success, int result) {
      if (this.resultConsumer != null) {
         this.resultConsumer.onCommandComplete(context, success, result);
      }

   }

   public Collection<String> getPlayerNames() {
      return Lists.newArrayList(this.server.getOnlinePlayerNames());
   }

   public Collection<String> getTeamNames() {
      return this.server.getWorldScoreboard().getTeamNames();
   }

   public Collection<ResourceLocation> getSoundResourceLocations() {
      return IRegistry.field_212633_v.getKeys();
   }

   public Collection<ResourceLocation> getRecipeResourceLocations() {
      return this.server.getRecipeManager().getIds();
   }

   public CompletableFuture<Suggestions> getSuggestionsFromServer(CommandContext<ISuggestionProvider> context, SuggestionsBuilder suggestionsBuilder) {
      return null;
   }

   public Collection<ISuggestionProvider.Coordinates> getCoordinates(boolean allowFloatCoords) {
      return Collections.singleton(ISuggestionProvider.Coordinates.DEFAULT_GLOBAL);
   }
}