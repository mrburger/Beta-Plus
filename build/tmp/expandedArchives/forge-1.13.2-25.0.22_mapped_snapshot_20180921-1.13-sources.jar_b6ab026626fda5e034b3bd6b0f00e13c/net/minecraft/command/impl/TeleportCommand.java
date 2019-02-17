package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ILocationArgument;
import net.minecraft.command.arguments.LocationInput;
import net.minecraft.command.arguments.RotationArgument;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;

public class TeleportCommand {
   public static void register(CommandDispatcher<CommandSource> p_198809_0_) {
      LiteralCommandNode<CommandSource> literalcommandnode = p_198809_0_.register(Commands.literal("teleport").requires((p_198816_0_) -> {
         return p_198816_0_.hasPermissionLevel(2);
      }).then(Commands.argument("targets", EntityArgument.multipleEntities()).then(Commands.argument("location", Vec3Argument.vec3()).executes((p_198807_0_) -> {
         return func_200559_a(p_198807_0_.getSource(), EntityArgument.getEntities(p_198807_0_, "targets"), p_198807_0_.getSource().getWorld(), Vec3Argument.func_200385_b(p_198807_0_, "location"), (ILocationArgument)null, (TeleportCommand.Facing)null);
      }).then(Commands.argument("rotation", RotationArgument.rotation()).executes((p_198811_0_) -> {
         return func_200559_a(p_198811_0_.getSource(), EntityArgument.getEntities(p_198811_0_, "targets"), p_198811_0_.getSource().getWorld(), Vec3Argument.func_200385_b(p_198811_0_, "location"), RotationArgument.getRotation(p_198811_0_, "rotation"), (TeleportCommand.Facing)null);
      })).then(Commands.literal("facing").then(Commands.literal("entity").then(Commands.argument("facingEntity", EntityArgument.singleEntity()).executes((p_198806_0_) -> {
         return func_200559_a(p_198806_0_.getSource(), EntityArgument.getEntities(p_198806_0_, "targets"), p_198806_0_.getSource().getWorld(), Vec3Argument.func_200385_b(p_198806_0_, "location"), (ILocationArgument)null, new TeleportCommand.Facing(EntityArgument.getSingleEntity(p_198806_0_, "facingEntity"), EntityAnchorArgument.Type.FEET));
      }).then(Commands.argument("facingAnchor", EntityAnchorArgument.entityAnchor()).executes((p_198812_0_) -> {
         return func_200559_a(p_198812_0_.getSource(), EntityArgument.getEntities(p_198812_0_, "targets"), p_198812_0_.getSource().getWorld(), Vec3Argument.func_200385_b(p_198812_0_, "location"), (ILocationArgument)null, new TeleportCommand.Facing(EntityArgument.getSingleEntity(p_198812_0_, "facingEntity"), EntityAnchorArgument.getEntityAnchor(p_198812_0_, "facingAnchor")));
      })))).then(Commands.argument("facingLocation", Vec3Argument.vec3()).executes((p_198805_0_) -> {
         return func_200559_a(p_198805_0_.getSource(), EntityArgument.getEntities(p_198805_0_, "targets"), p_198805_0_.getSource().getWorld(), Vec3Argument.func_200385_b(p_198805_0_, "location"), (ILocationArgument)null, new TeleportCommand.Facing(Vec3Argument.getVec3(p_198805_0_, "facingLocation")));
      })))).then(Commands.argument("destination", EntityArgument.singleEntity()).executes((p_198814_0_) -> {
         return func_201126_a(p_198814_0_.getSource(), EntityArgument.getEntities(p_198814_0_, "targets"), EntityArgument.getSingleEntity(p_198814_0_, "destination"));
      }))).then(Commands.argument("location", Vec3Argument.vec3()).executes((p_200560_0_) -> {
         return func_200559_a(p_200560_0_.getSource(), Collections.singleton(p_200560_0_.getSource().assertIsEntity()), p_200560_0_.getSource().getWorld(), Vec3Argument.func_200385_b(p_200560_0_, "location"), LocationInput.func_200383_d(), (TeleportCommand.Facing)null);
      })).then(Commands.argument("destination", EntityArgument.singleEntity()).executes((p_200562_0_) -> {
         return func_201126_a(p_200562_0_.getSource(), Collections.singleton(p_200562_0_.getSource().assertIsEntity()), EntityArgument.getSingleEntity(p_200562_0_, "destination"));
      })));
      p_198809_0_.register(Commands.literal("tp").requires((p_200556_0_) -> {
         return p_200556_0_.hasPermissionLevel(2);
      }).redirect(literalcommandnode));
   }

   private static int func_201126_a(CommandSource p_201126_0_, Collection<? extends Entity> p_201126_1_, Entity p_201126_2_) {
      for(Entity entity : p_201126_1_) {
         func_201127_a(p_201126_0_, entity, p_201126_0_.getWorld(), p_201126_2_.posX, p_201126_2_.posY, p_201126_2_.posZ, EnumSet.noneOf(SPacketPlayerPosLook.EnumFlags.class), p_201126_2_.rotationYaw, p_201126_2_.rotationPitch, (TeleportCommand.Facing)null);
      }

      if (p_201126_1_.size() == 1) {
         p_201126_0_.sendFeedback(new TextComponentTranslation("commands.teleport.success.entity.single", p_201126_1_.iterator().next().getDisplayName(), p_201126_2_.getDisplayName()), true);
      } else {
         p_201126_0_.sendFeedback(new TextComponentTranslation("commands.teleport.success.entity.multiple", p_201126_1_.size(), p_201126_2_.getDisplayName()), true);
      }

      return p_201126_1_.size();
   }

   private static int func_200559_a(CommandSource p_200559_0_, Collection<? extends Entity> p_200559_1_, WorldServer p_200559_2_, ILocationArgument p_200559_3_, @Nullable ILocationArgument p_200559_4_, @Nullable TeleportCommand.Facing p_200559_5_) throws CommandSyntaxException {
      Vec3d vec3d = p_200559_3_.getPosition(p_200559_0_);
      Vec2f vec2f = p_200559_4_ == null ? null : p_200559_4_.getRotation(p_200559_0_);
      Set<SPacketPlayerPosLook.EnumFlags> set = EnumSet.noneOf(SPacketPlayerPosLook.EnumFlags.class);
      if (p_200559_3_.isXRelative()) {
         set.add(SPacketPlayerPosLook.EnumFlags.X);
      }

      if (p_200559_3_.isYRelative()) {
         set.add(SPacketPlayerPosLook.EnumFlags.Y);
      }

      if (p_200559_3_.isZRelative()) {
         set.add(SPacketPlayerPosLook.EnumFlags.Z);
      }

      if (p_200559_4_ == null) {
         set.add(SPacketPlayerPosLook.EnumFlags.X_ROT);
         set.add(SPacketPlayerPosLook.EnumFlags.Y_ROT);
      } else {
         if (p_200559_4_.isXRelative()) {
            set.add(SPacketPlayerPosLook.EnumFlags.X_ROT);
         }

         if (p_200559_4_.isYRelative()) {
            set.add(SPacketPlayerPosLook.EnumFlags.Y_ROT);
         }
      }

      for(Entity entity : p_200559_1_) {
         if (p_200559_4_ == null) {
            func_201127_a(p_200559_0_, entity, p_200559_2_, vec3d.x, vec3d.y, vec3d.z, set, entity.rotationYaw, entity.rotationPitch, p_200559_5_);
         } else {
            func_201127_a(p_200559_0_, entity, p_200559_2_, vec3d.x, vec3d.y, vec3d.z, set, vec2f.y, vec2f.x, p_200559_5_);
         }
      }

      if (p_200559_1_.size() == 1) {
         p_200559_0_.sendFeedback(new TextComponentTranslation("commands.teleport.success.location.single", p_200559_1_.iterator().next().getDisplayName(), vec3d.x, vec3d.y, vec3d.z), true);
      } else {
         p_200559_0_.sendFeedback(new TextComponentTranslation("commands.teleport.success.location.multiple", p_200559_1_.size(), vec3d.x, vec3d.y, vec3d.z), true);
      }

      return p_200559_1_.size();
   }

   private static void func_201127_a(CommandSource p_201127_0_, Entity p_201127_1_, WorldServer p_201127_2_, double p_201127_3_, double p_201127_5_, double p_201127_7_, Set<SPacketPlayerPosLook.EnumFlags> p_201127_9_, float p_201127_10_, float p_201127_11_, @Nullable TeleportCommand.Facing p_201127_12_) {
      if (p_201127_1_ instanceof EntityPlayerMP) {
         p_201127_1_.stopRiding();
         if (((EntityPlayerMP)p_201127_1_).isPlayerSleeping()) {
            ((EntityPlayerMP)p_201127_1_).wakeUpPlayer(true, true, false);
         }

         if (p_201127_2_ == p_201127_1_.world) {
            ((EntityPlayerMP)p_201127_1_).connection.setPlayerLocation(p_201127_3_, p_201127_5_, p_201127_7_, p_201127_10_, p_201127_11_, p_201127_9_);
         } else {
            ((EntityPlayerMP)p_201127_1_).teleport(p_201127_2_, p_201127_3_, p_201127_5_, p_201127_7_, p_201127_10_, p_201127_11_);
         }

         p_201127_1_.setRotationYawHead(p_201127_10_);
      } else {
         float f = MathHelper.wrapDegrees(p_201127_10_);
         float f1 = MathHelper.wrapDegrees(p_201127_11_);
         f1 = MathHelper.clamp(f1, -90.0F, 90.0F);
         if (p_201127_2_ == p_201127_1_.world) {
            p_201127_1_.setLocationAndAngles(p_201127_3_, p_201127_5_, p_201127_7_, f, f1);
            p_201127_1_.setRotationYawHead(f);
         } else {
            WorldServer worldserver = (WorldServer)p_201127_1_.world;
            worldserver.removeEntity(p_201127_1_);
            p_201127_1_.dimension = p_201127_2_.dimension.getType();
            p_201127_1_.removed = false;
            Entity entity = p_201127_1_;
            p_201127_1_ = p_201127_1_.getType().create(p_201127_2_);
            if (p_201127_1_ == null) {
               return;
            }

            p_201127_1_.copyDataFromOld(entity);
            p_201127_1_.setLocationAndAngles(p_201127_3_, p_201127_5_, p_201127_7_, f, f1);
            p_201127_1_.setRotationYawHead(f);
            boolean flag = p_201127_1_.forceSpawn;
            p_201127_1_.forceSpawn = true;
            p_201127_2_.spawnEntity(p_201127_1_);
            p_201127_1_.forceSpawn = flag;
            p_201127_2_.tickEntity(p_201127_1_, false);
            entity.removed = true;
         }
      }

      if (p_201127_12_ != null) {
         p_201127_12_.func_201124_a(p_201127_0_, p_201127_1_);
      }

      if (!(p_201127_1_ instanceof EntityLivingBase) || !((EntityLivingBase)p_201127_1_).isElytraFlying()) {
         p_201127_1_.motionY = 0.0D;
         p_201127_1_.onGround = true;
      }

   }

   static class Facing {
      private final Vec3d field_200549_a;
      private final Entity field_200550_b;
      private final EntityAnchorArgument.Type field_201125_c;

      public Facing(Entity p_i48274_1_, EntityAnchorArgument.Type p_i48274_2_) {
         this.field_200550_b = p_i48274_1_;
         this.field_201125_c = p_i48274_2_;
         this.field_200549_a = p_i48274_2_.apply(p_i48274_1_);
      }

      public Facing(Vec3d p_i48246_1_) {
         this.field_200550_b = null;
         this.field_200549_a = p_i48246_1_;
         this.field_201125_c = null;
      }

      public void func_201124_a(CommandSource p_201124_1_, Entity p_201124_2_) {
         if (this.field_200550_b != null) {
            if (p_201124_2_ instanceof EntityPlayerMP) {
               ((EntityPlayerMP)p_201124_2_).lookAt(p_201124_1_.getEntityAnchorType(), this.field_200550_b, this.field_201125_c);
            } else {
               p_201124_2_.lookAt(p_201124_1_.getEntityAnchorType(), this.field_200549_a);
            }
         } else {
            p_201124_2_.lookAt(p_201124_1_.getEntityAnchorType(), this.field_200549_a);
         }

      }
   }
}