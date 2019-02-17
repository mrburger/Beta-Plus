package net.minecraft.command;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.command.impl.AdvancementCommand;
import net.minecraft.command.impl.BanCommand;
import net.minecraft.command.impl.BanIpCommand;
import net.minecraft.command.impl.BanListCommand;
import net.minecraft.command.impl.BossBarCommand;
import net.minecraft.command.impl.ClearCommand;
import net.minecraft.command.impl.CloneCommand;
import net.minecraft.command.impl.DataPackCommand;
import net.minecraft.command.impl.DeOpCommand;
import net.minecraft.command.impl.DebugCommand;
import net.minecraft.command.impl.DefaultGameModeCommand;
import net.minecraft.command.impl.DifficultyCommand;
import net.minecraft.command.impl.EffectCommand;
import net.minecraft.command.impl.EnchantCommand;
import net.minecraft.command.impl.ExecuteCommand;
import net.minecraft.command.impl.ExperienceCommand;
import net.minecraft.command.impl.FillCommand;
import net.minecraft.command.impl.ForceLoadCommand;
import net.minecraft.command.impl.FunctionCommand;
import net.minecraft.command.impl.GameModeCommand;
import net.minecraft.command.impl.GameRuleCommand;
import net.minecraft.command.impl.GiveCommand;
import net.minecraft.command.impl.HelpCommand;
import net.minecraft.command.impl.KickCommand;
import net.minecraft.command.impl.KillCommand;
import net.minecraft.command.impl.ListCommand;
import net.minecraft.command.impl.LocateCommand;
import net.minecraft.command.impl.MeCommand;
import net.minecraft.command.impl.MessageCommand;
import net.minecraft.command.impl.OpCommand;
import net.minecraft.command.impl.PardonCommand;
import net.minecraft.command.impl.PardonIpCommand;
import net.minecraft.command.impl.ParticleCommand;
import net.minecraft.command.impl.PlaySoundCommand;
import net.minecraft.command.impl.PublishCommand;
import net.minecraft.command.impl.RecipeCommand;
import net.minecraft.command.impl.ReloadCommand;
import net.minecraft.command.impl.ReplaceItemCommand;
import net.minecraft.command.impl.SaveAllCommand;
import net.minecraft.command.impl.SaveOffCommand;
import net.minecraft.command.impl.SaveOnCommand;
import net.minecraft.command.impl.SayCommand;
import net.minecraft.command.impl.ScoreboardCommand;
import net.minecraft.command.impl.SeedCommand;
import net.minecraft.command.impl.SetBlockCommand;
import net.minecraft.command.impl.SetIdleTimeoutCommand;
import net.minecraft.command.impl.SetWorldSpawnCommand;
import net.minecraft.command.impl.SpawnPointCommand;
import net.minecraft.command.impl.SpreadPlayersCommand;
import net.minecraft.command.impl.StopCommand;
import net.minecraft.command.impl.StopSoundCommand;
import net.minecraft.command.impl.SummonCommand;
import net.minecraft.command.impl.TagCommand;
import net.minecraft.command.impl.TeamCommand;
import net.minecraft.command.impl.TeleportCommand;
import net.minecraft.command.impl.TellRawCommand;
import net.minecraft.command.impl.TimeCommand;
import net.minecraft.command.impl.TitleCommand;
import net.minecraft.command.impl.TriggerCommand;
import net.minecraft.command.impl.WeatherCommand;
import net.minecraft.command.impl.WhitelistCommand;
import net.minecraft.command.impl.WorldBorderCommand;
import net.minecraft.command.impl.data.DataCommand;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketCommandList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Commands {
   private static final Logger LOGGER = LogManager.getLogger();
   private final CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher<>();

   public Commands(boolean isDedicatedServer) {
      AdvancementCommand.register(this.dispatcher);
      ExecuteCommand.register(this.dispatcher);
      BossBarCommand.register(this.dispatcher);
      ClearCommand.register(this.dispatcher);
      CloneCommand.register(this.dispatcher);
      DataCommand.register(this.dispatcher);
      DataPackCommand.register(this.dispatcher);
      DebugCommand.register(this.dispatcher);
      DefaultGameModeCommand.register(this.dispatcher);
      DifficultyCommand.register(this.dispatcher);
      EffectCommand.register(this.dispatcher);
      MeCommand.register(this.dispatcher);
      EnchantCommand.register(this.dispatcher);
      ExperienceCommand.register(this.dispatcher);
      FillCommand.register(this.dispatcher);
      FunctionCommand.register(this.dispatcher);
      GameModeCommand.register(this.dispatcher);
      GameRuleCommand.register(this.dispatcher);
      GiveCommand.register(this.dispatcher);
      HelpCommand.register(this.dispatcher);
      KickCommand.register(this.dispatcher);
      KillCommand.register(this.dispatcher);
      ListCommand.register(this.dispatcher);
      LocateCommand.register(this.dispatcher);
      MessageCommand.register(this.dispatcher);
      ParticleCommand.register(this.dispatcher);
      PlaySoundCommand.register(this.dispatcher);
      PublishCommand.register(this.dispatcher);
      ReloadCommand.register(this.dispatcher);
      RecipeCommand.register(this.dispatcher);
      ReplaceItemCommand.register(this.dispatcher);
      SayCommand.register(this.dispatcher);
      ScoreboardCommand.register(this.dispatcher);
      SeedCommand.register(this.dispatcher);
      SetBlockCommand.register(this.dispatcher);
      SpawnPointCommand.register(this.dispatcher);
      SetWorldSpawnCommand.register(this.dispatcher);
      SpreadPlayersCommand.register(this.dispatcher);
      StopSoundCommand.register(this.dispatcher);
      SummonCommand.register(this.dispatcher);
      TagCommand.register(this.dispatcher);
      TeamCommand.register(this.dispatcher);
      TeleportCommand.register(this.dispatcher);
      TellRawCommand.register(this.dispatcher);
      ForceLoadCommand.func_212712_a(this.dispatcher);
      TimeCommand.register(this.dispatcher);
      TitleCommand.register(this.dispatcher);
      TriggerCommand.register(this.dispatcher);
      WeatherCommand.register(this.dispatcher);
      WorldBorderCommand.register(this.dispatcher);
      if (isDedicatedServer) {
         BanIpCommand.register(this.dispatcher);
         BanListCommand.register(this.dispatcher);
         BanCommand.register(this.dispatcher);
         DeOpCommand.register(this.dispatcher);
         OpCommand.register(this.dispatcher);
         PardonCommand.register(this.dispatcher);
         PardonIpCommand.register(this.dispatcher);
         SaveAllCommand.register(this.dispatcher);
         SaveOffCommand.register(this.dispatcher);
         SaveOnCommand.register(this.dispatcher);
         SetIdleTimeoutCommand.register(this.dispatcher);
         StopCommand.register(this.dispatcher);
         WhitelistCommand.register(this.dispatcher);
      }

      this.dispatcher.findAmbiguities((p_201302_1_, p_201302_2_, p_201302_3_, p_201302_4_) -> {
         LOGGER.warn("Ambiguity between arguments {} and {} with inputs: {}", this.dispatcher.getPath(p_201302_2_), this.dispatcher.getPath(p_201302_3_), p_201302_4_);
      });
      this.dispatcher.setConsumer((p_197058_0_, p_197058_1_, p_197058_2_) -> {
         p_197058_0_.getSource().onCommandComplete(p_197058_0_, p_197058_1_, p_197058_2_);
      });
   }

   public void writeCommandTreeAsGson(File fileIn) {
      try {
         Files.write((new GsonBuilder()).setPrettyPrinting().create().toJson((JsonElement)ArgumentTypes.serialize(this.dispatcher, this.dispatcher.getRoot())), fileIn, StandardCharsets.UTF_8);
      } catch (IOException ioexception) {
         LOGGER.error("Couldn't write out command tree!", (Throwable)ioexception);
      }

   }

   public int handleCommand(CommandSource p_197059_1_, String command) {
      StringReader stringreader = new StringReader(command);
      if (stringreader.canRead() && stringreader.peek() == '/') {
         stringreader.skip();
      }

      p_197059_1_.getServer().profiler.startSection(command);

      try {
         try {
            com.mojang.brigadier.ParseResults<CommandSource> parse = this.dispatcher.parse(stringreader, p_197059_1_);
            net.minecraftforge.event.CommandEvent event = new net.minecraftforge.event.CommandEvent(parse);
            if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) {
               if (event.getException() != null) {
                  com.google.common.base.Throwables.throwIfUnchecked(event.getException());
               }
               return 1;
            }
            int lvt_4_3_ = this.dispatcher.execute(event.getParseResults());
            return lvt_4_3_;
         } catch (CommandException commandexception) {
            p_197059_1_.sendErrorMessage(commandexception.getComponent());
            byte b1 = 0;
            return b1;
         } catch (CommandSyntaxException commandsyntaxexception) {
            p_197059_1_.sendErrorMessage(TextComponentUtils.toTextComponent(commandsyntaxexception.getRawMessage()));
            if (commandsyntaxexception.getInput() != null && commandsyntaxexception.getCursor() >= 0) {
               int k = Math.min(commandsyntaxexception.getInput().length(), commandsyntaxexception.getCursor());
               ITextComponent itextcomponent1 = (new TextComponentString("")).applyTextStyle(TextFormatting.GRAY).applyTextStyle((p_211705_1_) -> {
                  p_211705_1_.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
               });
               if (k > 10) {
                  itextcomponent1.appendText("...");
               }

               itextcomponent1.appendText(commandsyntaxexception.getInput().substring(Math.max(0, k - 10), k));
               if (k < commandsyntaxexception.getInput().length()) {
                  ITextComponent itextcomponent2 = (new TextComponentString(commandsyntaxexception.getInput().substring(k))).applyTextStyles(new TextFormatting[]{TextFormatting.RED, TextFormatting.UNDERLINE});
                  itextcomponent1.appendSibling(itextcomponent2);
               }

               itextcomponent1.appendSibling((new TextComponentTranslation("command.context.here")).applyTextStyles(new TextFormatting[]{TextFormatting.RED, TextFormatting.ITALIC}));
               p_197059_1_.sendErrorMessage(itextcomponent1);
            }
         } catch (Exception exception) {
            TextComponentString textcomponentstring = new TextComponentString(exception.getMessage() == null ? exception.getClass().getName() : exception.getMessage());
            ITextComponent itextcomponent = textcomponentstring;
            if (LOGGER.isDebugEnabled()) {
               StackTraceElement[] astacktraceelement = exception.getStackTrace();

               for(int j = 0; j < Math.min(astacktraceelement.length, 3); ++j) {
                  itextcomponent.appendText("\n\n").appendText(astacktraceelement[j].getMethodName()).appendText("\n ").appendText(astacktraceelement[j].getFileName()).appendText(":").appendText(String.valueOf(astacktraceelement[j].getLineNumber()));
               }
            }

            p_197059_1_.sendErrorMessage((new TextComponentTranslation("command.failed")).applyTextStyle((p_211704_1_) -> {
               p_211704_1_.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, itextcomponent));
            }));
            byte b2 = 0;
            return b2;
         }

         byte b0 = 0;
         return b0;
      } finally {
         p_197059_1_.getServer().profiler.endSection();
      }
   }

   public void sendCommandListPacket(EntityPlayerMP player) {
      Map<CommandNode<CommandSource>, CommandNode<ISuggestionProvider>> map = Maps.newHashMap();
      RootCommandNode<ISuggestionProvider> rootcommandnode = new RootCommandNode<>();
      map.put(this.dispatcher.getRoot(), rootcommandnode);
      this.func_197052_a(this.dispatcher.getRoot(), rootcommandnode, player.getCommandSource(), map);
      player.connection.sendPacket(new SPacketCommandList(rootcommandnode));
   }

   private void func_197052_a(CommandNode<CommandSource> p_197052_1_, CommandNode<ISuggestionProvider> p_197052_2_, CommandSource p_197052_3_, Map<CommandNode<CommandSource>, CommandNode<ISuggestionProvider>> p_197052_4_) {
      for(CommandNode<CommandSource> commandnode : p_197052_1_.getChildren()) {
         if (commandnode.canUse(p_197052_3_)) {
            ArgumentBuilder<ISuggestionProvider, ?> argumentbuilder = (ArgumentBuilder)commandnode.createBuilder();
            argumentbuilder.requires((p_197060_0_) -> {
               return true;
            });
            if (argumentbuilder.getCommand() != null) {
               argumentbuilder.executes((p_197053_0_) -> {
                  return 0;
               });
            }

            if (argumentbuilder instanceof RequiredArgumentBuilder) {
               RequiredArgumentBuilder<ISuggestionProvider, ?> requiredargumentbuilder = (RequiredArgumentBuilder)argumentbuilder;
               if (requiredargumentbuilder.getSuggestionsProvider() != null) {
                  requiredargumentbuilder.suggests(SuggestionProviders.func_197496_b(requiredargumentbuilder.getSuggestionsProvider()));
               }
            }

            if (argumentbuilder.getRedirect() != null) {
               argumentbuilder.redirect(p_197052_4_.get(argumentbuilder.getRedirect()));
            }

            CommandNode<ISuggestionProvider> commandnode1 = argumentbuilder.build();
            p_197052_4_.put(commandnode, commandnode1);
            p_197052_2_.addChild(commandnode1);
            if (!commandnode.getChildren().isEmpty()) {
               this.func_197052_a(commandnode, commandnode1, p_197052_3_, p_197052_4_);
            }
         }
      }

   }

   public static LiteralArgumentBuilder<CommandSource> literal(String name) {
      return LiteralArgumentBuilder.literal(name);
   }

   public static <T> RequiredArgumentBuilder<CommandSource, T> argument(String name, ArgumentType<T> type) {
      return RequiredArgumentBuilder.argument(name, type);
   }

   public static Predicate<String> func_212590_a(Commands.IParser p_212590_0_) {
      return (p_212591_1_) -> {
         try {
            p_212590_0_.parse(new StringReader(p_212591_1_));
            return true;
         } catch (CommandSyntaxException var3) {
            return false;
         }
      };
   }

   public CommandDispatcher<CommandSource> getDispatcher() {
      return this.dispatcher;
   }

   @FunctionalInterface
   public interface IParser {
      void parse(StringReader p_parse_1_) throws CommandSyntaxException;
   }
}