package net.minecraft.util.datafix;

import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DSL.TypeReference;

public class TypeReferences {
   public static final TypeReference LEVEL = DataFixTypes.LEVEL;
   public static final TypeReference PLAYER = DataFixTypes.PLAYER;
   public static final TypeReference CHUNK = DataFixTypes.CHUNK;
   public static final TypeReference HOTBAR = DataFixTypes.HOTBAR;
   public static final TypeReference OPTIONS = DataFixTypes.OPTIONS;
   public static final TypeReference STRUCTURE = DataFixTypes.STRUCTURE;
   public static final TypeReference STATS = DataFixTypes.STATS;
   public static final TypeReference SAVED_DATA = DataFixTypes.SAVED_DATA;
   public static final TypeReference ADVANCEMENTS = DataFixTypes.ADVANCEMENTS;
   public static final TypeReference BLOCK_ENTITY = () -> {
      return "block_entity";
   };
   public static final TypeReference ITEM_STACK = () -> {
      return "item_stack";
   };
   public static final TypeReference BLOCK_STATE = () -> {
      return "block_state";
   };
   public static final TypeReference ENTITY_NAME = () -> {
      return "entity_name";
   };
   public static final TypeReference ENTITY_TYPE = () -> {
      return "entity_tree";
   };
   public static final TypeReference ENTITY = () -> {
      return "entity";
   };
   public static final TypeReference BLOCK_NAME = () -> {
      return "block_name";
   };
   public static final TypeReference ITEM_NAME = () -> {
      return "item_name";
   };
   public static final TypeReference UNTAGGED_SPAWNER = () -> {
      return "untagged_spawner";
   };
   public static final TypeReference STRUCTURE_FEATURE = () -> {
      return "structure_feature";
   };
   public static final TypeReference OBJECTIVE = () -> {
      return "objective";
   };
   public static final TypeReference TEAM = () -> {
      return "team";
   };
   public static final TypeReference RECIPE = () -> {
      return "recipe";
   };
   public static final TypeReference BIOME = () -> {
      return "biome";
   };
}