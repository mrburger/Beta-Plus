package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ComponentArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.CustomBossEvent;
import net.minecraft.server.CustomBossEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.world.BossInfo;

public class BossBarCommand {
   private static final DynamicCommandExceptionType BOSS_BAR_ID_TAKEN = new DynamicCommandExceptionType((p_208783_0_) -> {
      return new TextComponentTranslation("commands.bossbar.create.failed", p_208783_0_);
   });
   private static final DynamicCommandExceptionType NO_BOSSBAR_WITH_ID = new DynamicCommandExceptionType((p_208782_0_) -> {
      return new TextComponentTranslation("commands.bossbar.unknown", p_208782_0_);
   });
   private static final SimpleCommandExceptionType PLAYERS_ALREADY_ON_BOSSBAR = new SimpleCommandExceptionType(new TextComponentTranslation("commands.bossbar.set.players.unchanged"));
   private static final SimpleCommandExceptionType ALREADY_NAME_OF_BOSSBAR = new SimpleCommandExceptionType(new TextComponentTranslation("commands.bossbar.set.name.unchanged"));
   private static final SimpleCommandExceptionType ALREADY_COLOR_OF_BOSSBAR = new SimpleCommandExceptionType(new TextComponentTranslation("commands.bossbar.set.color.unchanged"));
   private static final SimpleCommandExceptionType ALREADY_STYLE_OF_BOSSBAR = new SimpleCommandExceptionType(new TextComponentTranslation("commands.bossbar.set.style.unchanged"));
   private static final SimpleCommandExceptionType ALREADY_VALUE_OF_BOSSBAR = new SimpleCommandExceptionType(new TextComponentTranslation("commands.bossbar.set.value.unchanged"));
   private static final SimpleCommandExceptionType ALREADY_MAX_OF_BOSSBAR = new SimpleCommandExceptionType(new TextComponentTranslation("commands.bossbar.set.max.unchanged"));
   private static final SimpleCommandExceptionType BOSSBAR_ALREADY_HIDDEN = new SimpleCommandExceptionType(new TextComponentTranslation("commands.bossbar.set.visibility.unchanged.hidden"));
   private static final SimpleCommandExceptionType BOSSBAR_ALREADY_VISIBLE = new SimpleCommandExceptionType(new TextComponentTranslation("commands.bossbar.set.visibility.unchanged.visible"));
   public static final SuggestionProvider<CommandSource> SUGGESTIONS_PROVIDER = (p_201404_0_, p_201404_1_) -> {
      return ISuggestionProvider.suggestIterable(p_201404_0_.getSource().getServer().getCustomBossEvents().getIDs(), p_201404_1_);
   };

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("bossbar").requires((p_201423_0_) -> {
         return p_201423_0_.hasPermissionLevel(2);
      }).then(Commands.literal("add").then(Commands.argument("id", ResourceLocationArgument.resourceLocation()).then(Commands.argument("name", ComponentArgument.component()).executes((p_201426_0_) -> {
         return createBossbar(p_201426_0_.getSource(), ResourceLocationArgument.getResourceLocation(p_201426_0_, "id"), ComponentArgument.getComponent(p_201426_0_, "name"));
      })))).then(Commands.literal("remove").then(Commands.argument("id", ResourceLocationArgument.resourceLocation()).suggests(SUGGESTIONS_PROVIDER).executes((p_201429_0_) -> {
         return removeBossbar(p_201429_0_.getSource(), getBossbar(p_201429_0_));
      }))).then(Commands.literal("list").executes((p_201396_0_) -> {
         return func_201428_a(p_201396_0_.getSource());
      })).then(Commands.literal("set").then(Commands.argument("id", ResourceLocationArgument.resourceLocation()).suggests(SUGGESTIONS_PROVIDER).then(Commands.literal("name").then(Commands.argument("name", ComponentArgument.component()).executes((p_201401_0_) -> {
         return setName(p_201401_0_.getSource(), getBossbar(p_201401_0_), ComponentArgument.getComponent(p_201401_0_, "name"));
      }))).then(Commands.literal("color").then(Commands.literal("pink").executes((p_201409_0_) -> {
         return setColor(p_201409_0_.getSource(), getBossbar(p_201409_0_), BossInfo.Color.PINK);
      })).then(Commands.literal("blue").executes((p_201422_0_) -> {
         return setColor(p_201422_0_.getSource(), getBossbar(p_201422_0_), BossInfo.Color.BLUE);
      })).then(Commands.literal("red").executes((p_201417_0_) -> {
         return setColor(p_201417_0_.getSource(), getBossbar(p_201417_0_), BossInfo.Color.RED);
      })).then(Commands.literal("green").executes((p_201424_0_) -> {
         return setColor(p_201424_0_.getSource(), getBossbar(p_201424_0_), BossInfo.Color.GREEN);
      })).then(Commands.literal("yellow").executes((p_201393_0_) -> {
         return setColor(p_201393_0_.getSource(), getBossbar(p_201393_0_), BossInfo.Color.YELLOW);
      })).then(Commands.literal("purple").executes((p_201391_0_) -> {
         return setColor(p_201391_0_.getSource(), getBossbar(p_201391_0_), BossInfo.Color.PURPLE);
      })).then(Commands.literal("white").executes((p_201406_0_) -> {
         return setColor(p_201406_0_.getSource(), getBossbar(p_201406_0_), BossInfo.Color.WHITE);
      }))).then(Commands.literal("style").then(Commands.literal("progress").executes((p_201399_0_) -> {
         return setStyle(p_201399_0_.getSource(), getBossbar(p_201399_0_), BossInfo.Overlay.PROGRESS);
      })).then(Commands.literal("notched_6").executes((p_201419_0_) -> {
         return setStyle(p_201419_0_.getSource(), getBossbar(p_201419_0_), BossInfo.Overlay.NOTCHED_6);
      })).then(Commands.literal("notched_10").executes((p_201412_0_) -> {
         return setStyle(p_201412_0_.getSource(), getBossbar(p_201412_0_), BossInfo.Overlay.NOTCHED_10);
      })).then(Commands.literal("notched_12").executes((p_201421_0_) -> {
         return setStyle(p_201421_0_.getSource(), getBossbar(p_201421_0_), BossInfo.Overlay.NOTCHED_12);
      })).then(Commands.literal("notched_20").executes((p_201403_0_) -> {
         return setStyle(p_201403_0_.getSource(), getBossbar(p_201403_0_), BossInfo.Overlay.NOTCHED_20);
      }))).then(Commands.literal("value").then(Commands.argument("value", IntegerArgumentType.integer(0)).executes((p_201408_0_) -> {
         return setValue(p_201408_0_.getSource(), getBossbar(p_201408_0_), IntegerArgumentType.getInteger(p_201408_0_, "value"));
      }))).then(Commands.literal("max").then(Commands.argument("max", IntegerArgumentType.integer(1)).executes((p_201395_0_) -> {
         return setMax(p_201395_0_.getSource(), getBossbar(p_201395_0_), IntegerArgumentType.getInteger(p_201395_0_, "max"));
      }))).then(Commands.literal("visible").then(Commands.argument("visible", BoolArgumentType.bool()).executes((p_201427_0_) -> {
         return setVisibility(p_201427_0_.getSource(), getBossbar(p_201427_0_), BoolArgumentType.getBool(p_201427_0_, "visible"));
      }))).then(Commands.literal("players").executes((p_201430_0_) -> {
         return setPlayers(p_201430_0_.getSource(), getBossbar(p_201430_0_), Collections.emptyList());
      }).then(Commands.argument("targets", EntityArgument.multiplePlayers()).executes((p_201411_0_) -> {
         return setPlayers(p_201411_0_.getSource(), getBossbar(p_201411_0_), EntityArgument.getPlayersAllowingNone(p_201411_0_, "targets"));
      }))))).then(Commands.literal("get").then(Commands.argument("id", ResourceLocationArgument.resourceLocation()).suggests(SUGGESTIONS_PROVIDER).then(Commands.literal("value").executes((p_201418_0_) -> {
         return getValue(p_201418_0_.getSource(), getBossbar(p_201418_0_));
      })).then(Commands.literal("max").executes((p_201398_0_) -> {
         return getMax(p_201398_0_.getSource(), getBossbar(p_201398_0_));
      })).then(Commands.literal("visible").executes((p_201392_0_) -> {
         return getVisibility(p_201392_0_.getSource(), getBossbar(p_201392_0_));
      })).then(Commands.literal("players").executes((p_201388_0_) -> {
         return getPlayers(p_201388_0_.getSource(), getBossbar(p_201388_0_));
      })))));
   }

   private static int getValue(CommandSource source, CustomBossEvent bossbar) {
      source.sendFeedback(new TextComponentTranslation("commands.bossbar.get.value", bossbar.getFormattedName(), bossbar.getValue()), true);
      return bossbar.getValue();
   }

   private static int getMax(CommandSource source, CustomBossEvent bossbar) {
      source.sendFeedback(new TextComponentTranslation("commands.bossbar.get.max", bossbar.getFormattedName(), bossbar.getMax()), true);
      return bossbar.getMax();
   }

   private static int getVisibility(CommandSource source, CustomBossEvent bossbar) {
      if (bossbar.isVisible()) {
         source.sendFeedback(new TextComponentTranslation("commands.bossbar.get.visible.visible", bossbar.getFormattedName()), true);
         return 1;
      } else {
         source.sendFeedback(new TextComponentTranslation("commands.bossbar.get.visible.hidden", bossbar.getFormattedName()), true);
         return 0;
      }
   }

   private static int getPlayers(CommandSource source, CustomBossEvent bossbar) {
      if (bossbar.getPlayers().isEmpty()) {
         source.sendFeedback(new TextComponentTranslation("commands.bossbar.get.players.none", bossbar.getFormattedName()), true);
      } else {
         source.sendFeedback(new TextComponentTranslation("commands.bossbar.get.players.some", bossbar.getFormattedName(), bossbar.getPlayers().size(), TextComponentUtils.makeList(bossbar.getPlayers(), EntityPlayer::getDisplayName)), true);
      }

      return bossbar.getPlayers().size();
   }

   private static int setVisibility(CommandSource source, CustomBossEvent bossbar, boolean visible) throws CommandSyntaxException {
      if (bossbar.isVisible() == visible) {
         if (visible) {
            throw BOSSBAR_ALREADY_VISIBLE.create();
         } else {
            throw BOSSBAR_ALREADY_HIDDEN.create();
         }
      } else {
         bossbar.setVisible(visible);
         if (visible) {
            source.sendFeedback(new TextComponentTranslation("commands.bossbar.set.visible.success.visible", bossbar.getFormattedName()), true);
         } else {
            source.sendFeedback(new TextComponentTranslation("commands.bossbar.set.visible.success.hidden", bossbar.getFormattedName()), true);
         }

         return 0;
      }
   }

   private static int setValue(CommandSource source, CustomBossEvent bossbar, int value) throws CommandSyntaxException {
      if (bossbar.getValue() == value) {
         throw ALREADY_VALUE_OF_BOSSBAR.create();
      } else {
         bossbar.setValue(value);
         source.sendFeedback(new TextComponentTranslation("commands.bossbar.set.value.success", bossbar.getFormattedName(), value), true);
         return value;
      }
   }

   private static int setMax(CommandSource source, CustomBossEvent bossbar, int max) throws CommandSyntaxException {
      if (bossbar.getMax() == max) {
         throw ALREADY_MAX_OF_BOSSBAR.create();
      } else {
         bossbar.setMax(max);
         source.sendFeedback(new TextComponentTranslation("commands.bossbar.set.max.success", bossbar.getFormattedName(), max), true);
         return max;
      }
   }

   private static int setColor(CommandSource source, CustomBossEvent bossbar, BossInfo.Color color) throws CommandSyntaxException {
      if (bossbar.getColor().equals(color)) {
         throw ALREADY_COLOR_OF_BOSSBAR.create();
      } else {
         bossbar.setColor(color);
         source.sendFeedback(new TextComponentTranslation("commands.bossbar.set.color.success", bossbar.getFormattedName()), true);
         return 0;
      }
   }

   private static int setStyle(CommandSource source, CustomBossEvent bossbar, BossInfo.Overlay styleIn) throws CommandSyntaxException {
      if (bossbar.getOverlay().equals(styleIn)) {
         throw ALREADY_STYLE_OF_BOSSBAR.create();
      } else {
         bossbar.setOverlay(styleIn);
         source.sendFeedback(new TextComponentTranslation("commands.bossbar.set.style.success", bossbar.getFormattedName()), true);
         return 0;
      }
   }

   private static int setName(CommandSource source, CustomBossEvent bossbar, ITextComponent name) throws CommandSyntaxException {
      ITextComponent itextcomponent = TextComponentUtils.updateForEntity(source, name, (Entity)null);
      if (bossbar.getName().equals(itextcomponent)) {
         throw ALREADY_NAME_OF_BOSSBAR.create();
      } else {
         bossbar.setName(itextcomponent);
         source.sendFeedback(new TextComponentTranslation("commands.bossbar.set.name.success", bossbar.getFormattedName()), true);
         return 0;
      }
   }

   private static int setPlayers(CommandSource source, CustomBossEvent bossbar, Collection<EntityPlayerMP> players) throws CommandSyntaxException {
      boolean flag = bossbar.setPlayers(players);
      if (!flag) {
         throw PLAYERS_ALREADY_ON_BOSSBAR.create();
      } else {
         if (bossbar.getPlayers().isEmpty()) {
            source.sendFeedback(new TextComponentTranslation("commands.bossbar.set.players.success.none", bossbar.getFormattedName()), true);
         } else {
            source.sendFeedback(new TextComponentTranslation("commands.bossbar.set.players.success.some", bossbar.getFormattedName(), players.size(), TextComponentUtils.makeList(players, EntityPlayer::getDisplayName)), true);
         }

         return bossbar.getPlayers().size();
      }
   }

   private static int func_201428_a(CommandSource source) {
      Collection<CustomBossEvent> collection = source.getServer().getCustomBossEvents().getBossbars();
      if (collection.isEmpty()) {
         source.sendFeedback(new TextComponentTranslation("commands.bossbar.list.bars.none"), false);
      } else {
         source.sendFeedback(new TextComponentTranslation("commands.bossbar.list.bars.some", collection.size(), TextComponentUtils.makeList(collection, CustomBossEvent::getFormattedName)), false);
      }

      return collection.size();
   }

   private static int createBossbar(CommandSource source, ResourceLocation id, ITextComponent p_201400_2_) throws CommandSyntaxException {
      CustomBossEvents custombossevents = source.getServer().getCustomBossEvents();
      if (custombossevents.get(id) != null) {
         throw BOSS_BAR_ID_TAKEN.create(id.toString());
      } else {
         CustomBossEvent custombossevent = custombossevents.add(id, TextComponentUtils.updateForEntity(source, p_201400_2_, (Entity)null));
         source.sendFeedback(new TextComponentTranslation("commands.bossbar.create.success", custombossevent.getFormattedName()), true);
         return custombossevents.getBossbars().size();
      }
   }

   private static int removeBossbar(CommandSource source, CustomBossEvent bossbar) {
      CustomBossEvents custombossevents = source.getServer().getCustomBossEvents();
      bossbar.removeAllPlayers();
      custombossevents.remove(bossbar);
      source.sendFeedback(new TextComponentTranslation("commands.bossbar.remove.success", bossbar.getFormattedName()), true);
      return custombossevents.getBossbars().size();
   }

   public static CustomBossEvent getBossbar(CommandContext<CommandSource> source) throws CommandSyntaxException {
      ResourceLocation resourcelocation = ResourceLocationArgument.getResourceLocation(source, "id");
      CustomBossEvent custombossevent = source.getSource().getServer().getCustomBossEvents().get(resourcelocation);
      if (custombossevent == null) {
         throw NO_BOSSBAR_WITH_ID.create(resourcelocation.toString());
      } else {
         return custombossevent;
      }
   }
}