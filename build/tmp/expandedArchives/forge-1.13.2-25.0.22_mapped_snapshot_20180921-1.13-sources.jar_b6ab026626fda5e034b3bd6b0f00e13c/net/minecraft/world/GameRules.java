package net.minecraft.world;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;

public class GameRules {
   private static final TreeMap<String, GameRules.ValueDefinition> DEFINITIONS = Util.make(new TreeMap<>(), (p_209363_0_) -> {
      p_209363_0_.put("doFireTick", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      p_209363_0_.put("mobGriefing", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      p_209363_0_.put("keepInventory", new GameRules.ValueDefinition("false", GameRules.ValueType.BOOLEAN_VALUE));
      p_209363_0_.put("doMobSpawning", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      p_209363_0_.put("doMobLoot", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      p_209363_0_.put("doTileDrops", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      p_209363_0_.put("doEntityDrops", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      p_209363_0_.put("commandBlockOutput", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      p_209363_0_.put("naturalRegeneration", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      p_209363_0_.put("doDaylightCycle", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      p_209363_0_.put("logAdminCommands", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      p_209363_0_.put("showDeathMessages", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      p_209363_0_.put("randomTickSpeed", new GameRules.ValueDefinition("3", GameRules.ValueType.NUMERICAL_VALUE));
      p_209363_0_.put("sendCommandFeedback", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      p_209363_0_.put("reducedDebugInfo", new GameRules.ValueDefinition("false", GameRules.ValueType.BOOLEAN_VALUE, (p_209364_0_, p_209364_1_) -> {
         byte b0 = (byte)(p_209364_1_.getBoolean() ? 22 : 23);

         for(EntityPlayerMP entityplayermp : p_209364_0_.getPlayerList().getPlayers()) {
            entityplayermp.connection.sendPacket(new SPacketEntityStatus(entityplayermp, b0));
         }

      }));
      p_209363_0_.put("spectatorsGenerateChunks", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      p_209363_0_.put("spawnRadius", new GameRules.ValueDefinition("10", GameRules.ValueType.NUMERICAL_VALUE));
      p_209363_0_.put("disableElytraMovementCheck", new GameRules.ValueDefinition("false", GameRules.ValueType.BOOLEAN_VALUE));
      p_209363_0_.put("maxEntityCramming", new GameRules.ValueDefinition("24", GameRules.ValueType.NUMERICAL_VALUE));
      p_209363_0_.put("doWeatherCycle", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      p_209363_0_.put("doLimitedCrafting", new GameRules.ValueDefinition("false", GameRules.ValueType.BOOLEAN_VALUE));
      p_209363_0_.put("maxCommandChainLength", new GameRules.ValueDefinition("65536", GameRules.ValueType.NUMERICAL_VALUE));
      p_209363_0_.put("announceAdvancements", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
   });
   private final TreeMap<String, GameRules.Value> rules = new TreeMap<>();

   public GameRules() {
      for(Entry<String, GameRules.ValueDefinition> entry : DEFINITIONS.entrySet()) {
         this.rules.put(entry.getKey(), entry.getValue().createValue());
      }

   }

   public void setOrCreateGameRule(String key, String ruleValue, @Nullable MinecraftServer server) {
      GameRules.Value gamerules$value = this.rules.get(key);
      if (gamerules$value != null) {
         gamerules$value.setValue(ruleValue, server);
      }

   }

   /**
    * Gets the boolean Game Rule value.
    */
   public boolean getBoolean(String name) {
      GameRules.Value gamerules$value = this.rules.get(name);
      return gamerules$value != null ? gamerules$value.getBoolean() : false;
   }

   public int getInt(String name) {
      GameRules.Value gamerules$value = this.rules.get(name);
      return gamerules$value != null ? gamerules$value.getInt() : 0;
   }

   /**
    * Return the defined game rules as NBT.
    */
   public NBTTagCompound write() {
      NBTTagCompound nbttagcompound = new NBTTagCompound();

      for(String s : this.rules.keySet()) {
         GameRules.Value gamerules$value = this.rules.get(s);
         nbttagcompound.setString(s, gamerules$value.getString());
      }

      return nbttagcompound;
   }

   /**
    * Set defined game rules from NBT.
    */
   public void read(NBTTagCompound nbt) {
      for(String s : nbt.keySet()) {
         this.setOrCreateGameRule(s, nbt.getString(s), (MinecraftServer)null);
      }

   }

   public GameRules.Value get(String name) {
      return this.rules.get(name);
   }

   public static TreeMap<String, GameRules.ValueDefinition> getDefinitions() {
      return DEFINITIONS;
   }

   public static class Value {
      private String valueString;
      private boolean valueBoolean;
      private int valueInteger;
      private double valueDouble;
      private final GameRules.ValueType type;
      /**
       * A callback that is triggered when the value of the rule changes. Only called when the server is known, which
       * excludes initial creation.
       */
      private final BiConsumer<MinecraftServer, GameRules.Value> changeCallback;

      public Value(String stringValue, GameRules.ValueType typeIn, BiConsumer<MinecraftServer, GameRules.Value> callback) {
         this.type = typeIn;
         this.changeCallback = callback;
         this.setValue(stringValue, (MinecraftServer)null);
      }

      /**
       * Sets the value of this gamerule, updating the various internal representations.
       */
      public void setValue(String stringValue, @Nullable MinecraftServer server) {
         this.valueString = stringValue;
         this.valueBoolean = Boolean.parseBoolean(stringValue);
         this.valueInteger = this.valueBoolean ? 1 : 0;

         try {
            this.valueInteger = Integer.parseInt(stringValue);
         } catch (NumberFormatException var5) {
            ;
         }

         try {
            this.valueDouble = Double.parseDouble(stringValue);
         } catch (NumberFormatException var4) {
            ;
         }

         if (server != null) {
            this.changeCallback.accept(server, this);
         }

      }

      /**
       * Gets the GameRule's value as String.
       */
      public String getString() {
         return this.valueString;
      }

      /**
       * Gets the GameRule's value as boolean.
       */
      public boolean getBoolean() {
         return this.valueBoolean;
      }

      public int getInt() {
         return this.valueInteger;
      }

      public GameRules.ValueType getType() {
         return this.type;
      }
   }

   public static class ValueDefinition {
      private final GameRules.ValueType type;
      private final String defaultValue;
      /** A callback that is triggered when the value of the rule changes. */
      private final BiConsumer<MinecraftServer, GameRules.Value> changeCallback;

      public ValueDefinition(String defaultValue, GameRules.ValueType typeIn) {
         this(defaultValue, typeIn, (p_201202_0_, p_201202_1_) -> {
         });
      }

      public ValueDefinition(String defaultValue, GameRules.ValueType typeIn, BiConsumer<MinecraftServer, GameRules.Value> callback) {
         this.type = typeIn;
         this.defaultValue = defaultValue;
         this.changeCallback = callback;
      }

      public GameRules.Value createValue() {
         return new GameRules.Value(this.defaultValue, this.type, this.changeCallback);
      }

      public GameRules.ValueType getType() {
         return this.type;
      }
   }

   public static enum ValueType {
      ANY_VALUE(StringArgumentType::greedyString, (p_196224_0_, p_196224_1_) -> {
         return p_196224_0_.getArgument(p_196224_1_, String.class);
      }),
      BOOLEAN_VALUE(BoolArgumentType::bool, (p_196227_0_, p_196227_1_) -> {
         return p_196227_0_.getArgument(p_196227_1_, Boolean.class).toString();
      }),
      NUMERICAL_VALUE(IntegerArgumentType::integer, (p_196226_0_, p_196226_1_) -> {
         return p_196226_0_.getArgument(p_196226_1_, Integer.class).toString();
      });

      private final Supplier<ArgumentType<?>> argumentType;
      /** Gets the argument from the command, returning the value (or throwing an exception) */
      private final BiFunction<CommandContext<CommandSource>, String, String> argumentGetter;

      private ValueType(Supplier<ArgumentType<?>> argumentTypeSupplier, BiFunction<CommandContext<CommandSource>, String, String> argumentValueSupplier) {
         this.argumentType = argumentTypeSupplier;
         this.argumentGetter = argumentValueSupplier;
      }

      public RequiredArgumentBuilder<CommandSource, ?> createArgument(String name) {
         return Commands.argument(name, this.argumentType.get());
      }

      /**
       * Updates the value of the given rule, making sure that the new value is acceptable for this type.
       *  
       * @param name Name of the argument (not the rule)
       * @param value The rule to update.
       */
      public void updateValue(CommandContext<CommandSource> context, String name, GameRules.Value value) {
         value.setValue(this.argumentGetter.apply(context, name), context.getSource().getServer());
      }
   }
}