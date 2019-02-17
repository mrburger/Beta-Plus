package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.List;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.WorldInfo;

public class DataPackCommand {
   private static final DynamicCommandExceptionType UNKNOWN_DATA_PACK_EXCEPTION = new DynamicCommandExceptionType((p_208808_0_) -> {
      return new TextComponentTranslation("commands.datapack.unknown", p_208808_0_);
   });
   private static final DynamicCommandExceptionType ENABLE_FAILED_EXCEPTION = new DynamicCommandExceptionType((p_208818_0_) -> {
      return new TextComponentTranslation("commands.datapack.enable.failed", p_208818_0_);
   });
   private static final DynamicCommandExceptionType DISABLE_FAILED_EXCEPTION = new DynamicCommandExceptionType((p_208815_0_) -> {
      return new TextComponentTranslation("commands.datapack.disable.failed", p_208815_0_);
   });
   private static final SuggestionProvider<CommandSource> field_198319_d = (p_198305_0_, p_198305_1_) -> {
      return ISuggestionProvider.suggest(p_198305_0_.getSource().getServer().getResourcePacks().getPackInfos().stream().map(ResourcePackInfo::getName).map(StringArgumentType::escapeIfRequired), p_198305_1_);
   };
   private static final SuggestionProvider<CommandSource> field_198320_e = (p_198296_0_, p_198296_1_) -> {
      return ISuggestionProvider.suggest(p_198296_0_.getSource().getServer().getResourcePacks().func_198979_c().stream().map(ResourcePackInfo::getName).map(StringArgumentType::escapeIfRequired), p_198296_1_);
   };

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("datapack").requires((p_198301_0_) -> {
         return p_198301_0_.hasPermissionLevel(2);
      }).then(Commands.literal("enable").then(Commands.argument("name", StringArgumentType.string()).suggests(field_198320_e).executes((p_198292_0_) -> {
         return enablePack(p_198292_0_.getSource(), parsePackInfo(p_198292_0_, "name", true), (p_198289_0_, p_198289_1_) -> {
            p_198289_1_.getPriority().func_198993_a(p_198289_0_, p_198289_1_, (p_198304_0_) -> {
               return p_198304_0_;
            }, false);
         });
      }).then(Commands.literal("after").then(Commands.argument("existing", StringArgumentType.string()).suggests(field_198319_d).executes((p_198307_0_) -> {
         return enablePack(p_198307_0_.getSource(), parsePackInfo(p_198307_0_, "name", true), (p_198308_1_, p_198308_2_) -> {
            p_198308_1_.add(p_198308_1_.indexOf(parsePackInfo(p_198307_0_, "existing", false)) + 1, p_198308_2_);
         });
      }))).then(Commands.literal("before").then(Commands.argument("existing", StringArgumentType.string()).suggests(field_198319_d).executes((p_198311_0_) -> {
         return enablePack(p_198311_0_.getSource(), parsePackInfo(p_198311_0_, "name", true), (p_198302_1_, p_198302_2_) -> {
            p_198302_1_.add(p_198302_1_.indexOf(parsePackInfo(p_198311_0_, "existing", false)), p_198302_2_);
         });
      }))).then(Commands.literal("last").executes((p_198298_0_) -> {
         return enablePack(p_198298_0_.getSource(), parsePackInfo(p_198298_0_, "name", true), List::add);
      })).then(Commands.literal("first").executes((p_198300_0_) -> {
         return enablePack(p_198300_0_.getSource(), parsePackInfo(p_198300_0_, "name", true), (p_198310_0_, p_198310_1_) -> {
            p_198310_0_.add(0, p_198310_1_);
         });
      })))).then(Commands.literal("disable").then(Commands.argument("name", StringArgumentType.string()).suggests(field_198319_d).executes((p_198295_0_) -> {
         return disablePack(p_198295_0_.getSource(), parsePackInfo(p_198295_0_, "name", false));
      }))).then(Commands.literal("list").executes((p_198290_0_) -> {
         return func_198313_a(p_198290_0_.getSource());
      }).then(Commands.literal("available").executes((p_198288_0_) -> {
         return func_198314_b(p_198288_0_.getSource());
      })).then(Commands.literal("enabled").executes((p_198309_0_) -> {
         return func_198315_c(p_198309_0_.getSource());
      }))));
   }

   private static int enablePack(CommandSource p_198297_0_, ResourcePackInfo p_198297_1_, DataPackCommand.IHandler p_198297_2_) throws CommandSyntaxException {
      ResourcePackList<ResourcePackInfo> resourcepacklist = p_198297_0_.getServer().getResourcePacks();
      List<ResourcePackInfo> list = Lists.newArrayList(resourcepacklist.getPackInfos());
      p_198297_2_.apply(list, p_198297_1_);
      resourcepacklist.func_198985_a(list);
      WorldInfo worldinfo = p_198297_0_.getServer().getWorld(DimensionType.OVERWORLD).getWorldInfo();
      worldinfo.getEnabledDataPacks().clear();
      resourcepacklist.getPackInfos().forEach((p_198294_1_) -> {
         worldinfo.getEnabledDataPacks().add(p_198294_1_.getName());
      });
      worldinfo.getDisabledDataPacks().remove(p_198297_1_.getName());
      p_198297_0_.sendFeedback(new TextComponentTranslation("commands.datapack.enable.success", p_198297_1_.func_195794_a(true)), true);
      p_198297_0_.getServer().reload();
      return resourcepacklist.getPackInfos().size();
   }

   private static int disablePack(CommandSource p_198312_0_, ResourcePackInfo p_198312_1_) {
      ResourcePackList<ResourcePackInfo> resourcepacklist = p_198312_0_.getServer().getResourcePacks();
      List<ResourcePackInfo> list = Lists.newArrayList(resourcepacklist.getPackInfos());
      list.remove(p_198312_1_);
      resourcepacklist.func_198985_a(list);
      WorldInfo worldinfo = p_198312_0_.getServer().getWorld(DimensionType.OVERWORLD).getWorldInfo();
      worldinfo.getEnabledDataPacks().clear();
      resourcepacklist.getPackInfos().forEach((p_198291_1_) -> {
         worldinfo.getEnabledDataPacks().add(p_198291_1_.getName());
      });
      worldinfo.getDisabledDataPacks().add(p_198312_1_.getName());
      p_198312_0_.sendFeedback(new TextComponentTranslation("commands.datapack.disable.success", p_198312_1_.func_195794_a(true)), true);
      p_198312_0_.getServer().reload();
      return resourcepacklist.getPackInfos().size();
   }

   private static int func_198313_a(CommandSource p_198313_0_) {
      return func_198315_c(p_198313_0_) + func_198314_b(p_198313_0_);
   }

   private static int func_198314_b(CommandSource p_198314_0_) {
      ResourcePackList<ResourcePackInfo> resourcepacklist = p_198314_0_.getServer().getResourcePacks();
      if (resourcepacklist.func_198979_c().isEmpty()) {
         p_198314_0_.sendFeedback(new TextComponentTranslation("commands.datapack.list.available.none"), false);
      } else {
         p_198314_0_.sendFeedback(new TextComponentTranslation("commands.datapack.list.available.success", resourcepacklist.func_198979_c().size(), TextComponentUtils.makeList(resourcepacklist.func_198979_c(), (p_198293_0_) -> {
            return p_198293_0_.func_195794_a(false);
         })), false);
      }

      return resourcepacklist.func_198979_c().size();
   }

   private static int func_198315_c(CommandSource p_198315_0_) {
      ResourcePackList<ResourcePackInfo> resourcepacklist = p_198315_0_.getServer().getResourcePacks();
      if (resourcepacklist.getPackInfos().isEmpty()) {
         p_198315_0_.sendFeedback(new TextComponentTranslation("commands.datapack.list.enabled.none"), false);
      } else {
         p_198315_0_.sendFeedback(new TextComponentTranslation("commands.datapack.list.enabled.success", resourcepacklist.getPackInfos().size(), TextComponentUtils.makeList(resourcepacklist.getPackInfos(), (p_198306_0_) -> {
            return p_198306_0_.func_195794_a(true);
         })), false);
      }

      return resourcepacklist.getPackInfos().size();
   }

   private static ResourcePackInfo parsePackInfo(CommandContext<CommandSource> context, String p_198303_1_, boolean p_198303_2_) throws CommandSyntaxException {
      String s = StringArgumentType.getString(context, p_198303_1_);
      ResourcePackList<ResourcePackInfo> resourcepacklist = context.getSource().getServer().getResourcePacks();
      ResourcePackInfo resourcepackinfo = resourcepacklist.getPackInfo(s);
      if (resourcepackinfo == null) {
         throw UNKNOWN_DATA_PACK_EXCEPTION.create(s);
      } else {
         boolean flag = resourcepacklist.getPackInfos().contains(resourcepackinfo);
         if (p_198303_2_ && flag) {
            throw ENABLE_FAILED_EXCEPTION.create(s);
         } else if (!p_198303_2_ && !flag) {
            throw DISABLE_FAILED_EXCEPTION.create(s);
         } else {
            return resourcepackinfo;
         }
      }
   }

   interface IHandler {
      void apply(List<ResourcePackInfo> p_apply_1_, ResourcePackInfo p_apply_2_) throws CommandSyntaxException;
   }
}