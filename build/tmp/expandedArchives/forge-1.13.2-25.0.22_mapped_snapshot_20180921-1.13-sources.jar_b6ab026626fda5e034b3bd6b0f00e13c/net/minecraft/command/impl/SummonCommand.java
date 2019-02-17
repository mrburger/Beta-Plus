package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntitySummonArgument;
import net.minecraft.command.arguments.NBTArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;

public class SummonCommand {
   private static final SimpleCommandExceptionType field_198741_a = new SimpleCommandExceptionType(new TextComponentTranslation("commands.summon.failed"));

   public static void register(CommandDispatcher<CommandSource> p_198736_0_) {
      p_198736_0_.register(Commands.literal("summon").requires((p_198740_0_) -> {
         return p_198740_0_.hasPermissionLevel(2);
      }).then(Commands.argument("entity", EntitySummonArgument.entitySummon()).suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes((p_198738_0_) -> {
         return func_198737_a(p_198738_0_.getSource(), EntitySummonArgument.getEntityId(p_198738_0_, "entity"), p_198738_0_.getSource().getPos(), new NBTTagCompound(), true);
      }).then(Commands.argument("pos", Vec3Argument.vec3()).executes((p_198735_0_) -> {
         return func_198737_a(p_198735_0_.getSource(), EntitySummonArgument.getEntityId(p_198735_0_, "entity"), Vec3Argument.getVec3(p_198735_0_, "pos"), new NBTTagCompound(), true);
      }).then(Commands.argument("nbt", NBTArgument.nbt()).executes((p_198739_0_) -> {
         return func_198737_a(p_198739_0_.getSource(), EntitySummonArgument.getEntityId(p_198739_0_, "entity"), Vec3Argument.getVec3(p_198739_0_, "pos"), NBTArgument.func_197130_a(p_198739_0_, "nbt"), false);
      })))));
   }

   private static int func_198737_a(CommandSource p_198737_0_, ResourceLocation p_198737_1_, Vec3d p_198737_2_, NBTTagCompound p_198737_3_, boolean p_198737_4_) throws CommandSyntaxException {
      NBTTagCompound nbttagcompound = p_198737_3_.copy();
      nbttagcompound.setString("id", p_198737_1_.toString());
      if (EntityType.getId(EntityType.LIGHTNING_BOLT).equals(p_198737_1_)) {
         Entity entity1 = new EntityLightningBolt(p_198737_0_.getWorld(), p_198737_2_.x, p_198737_2_.y, p_198737_2_.z, false);
         p_198737_0_.getWorld().addWeatherEffect(entity1);
         p_198737_0_.sendFeedback(new TextComponentTranslation("commands.summon.success", entity1.getDisplayName()), true);
         return 1;
      } else {
         Entity entity = AnvilChunkLoader.readWorldEntityPos(nbttagcompound, p_198737_0_.getWorld(), p_198737_2_.x, p_198737_2_.y, p_198737_2_.z, true);
         if (entity == null) {
            throw field_198741_a.create();
         } else {
            entity.setLocationAndAngles(p_198737_2_.x, p_198737_2_.y, p_198737_2_.z, entity.rotationYaw, entity.rotationPitch);
            if (p_198737_4_ && entity instanceof EntityLiving) {
               ((EntityLiving)entity).onInitialSpawn(p_198737_0_.getWorld().getDifficultyForLocation(new BlockPos(entity)), (IEntityLivingData)null, (NBTTagCompound)null);
            }

            p_198737_0_.sendFeedback(new TextComponentTranslation("commands.summon.success", entity.getDisplayName()), true);
            return 1;
         }
      }
   }
}