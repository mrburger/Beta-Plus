package net.minecraft.stats;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class StatList {
   public static final StatType<Block> BLOCK_MINED = registerType("mined", IRegistry.field_212618_g);
   public static final StatType<Item> ITEM_CRAFTED = registerType("crafted", IRegistry.field_212630_s);
   /** Tracks the number of times a given block or item has been used. */
   public static final StatType<Item> ITEM_USED = registerType("used", IRegistry.field_212630_s);
   public static final StatType<Item> ITEM_BROKEN = registerType("broken", IRegistry.field_212630_s);
   public static final StatType<Item> ITEM_PICKED_UP = registerType("picked_up", IRegistry.field_212630_s);
   public static final StatType<Item> ITEM_DROPPED = registerType("dropped", IRegistry.field_212630_s);
   public static final StatType<EntityType<?>> ENTITY_KILLED = registerType("killed", IRegistry.field_212629_r);
   public static final StatType<EntityType<?>> ENTITY_KILLED_BY = registerType("killed_by", IRegistry.field_212629_r);
   public static final StatType<ResourceLocation> CUSTOM = registerType("custom", IRegistry.field_212623_l);
   /** number of times you've left a game */
   public static final ResourceLocation LEAVE_GAME = registerCustom("leave_game", IStatFormater.DEFAULT);
   public static final ResourceLocation PLAY_ONE_MINUTE = registerCustom("play_one_minute", IStatFormater.TIME);
   public static final ResourceLocation TIME_SINCE_DEATH = registerCustom("time_since_death", IStatFormater.TIME);
   public static final ResourceLocation TIME_SINCE_REST = registerCustom("time_since_rest", IStatFormater.TIME);
   public static final ResourceLocation SNEAK_TIME = registerCustom("sneak_time", IStatFormater.TIME);
   public static final ResourceLocation WALK_ONE_CM = registerCustom("walk_one_cm", IStatFormater.DISTANCE);
   public static final ResourceLocation CROUCH_ONE_CM = registerCustom("crouch_one_cm", IStatFormater.DISTANCE);
   public static final ResourceLocation SPRINT_ONE_CM = registerCustom("sprint_one_cm", IStatFormater.DISTANCE);
   public static final ResourceLocation WALK_ON_WATER_ONE_CM = registerCustom("walk_on_water_one_cm", IStatFormater.DISTANCE);
   /** the distance you have fallen */
   public static final ResourceLocation FALL_ONE_CM = registerCustom("fall_one_cm", IStatFormater.DISTANCE);
   public static final ResourceLocation CLIMB_ONE_CM = registerCustom("climb_one_cm", IStatFormater.DISTANCE);
   public static final ResourceLocation FLY_ONE_CM = registerCustom("fly_one_cm", IStatFormater.DISTANCE);
   public static final ResourceLocation WALK_UNDER_WATER_ONE_CM = registerCustom("walk_under_water_one_cm", IStatFormater.DISTANCE);
   public static final ResourceLocation MINECART_ONE_CM = registerCustom("minecart_one_cm", IStatFormater.DISTANCE);
   public static final ResourceLocation BOAT_ONE_CM = registerCustom("boat_one_cm", IStatFormater.DISTANCE);
   public static final ResourceLocation PIG_ONE_CM = registerCustom("pig_one_cm", IStatFormater.DISTANCE);
   public static final ResourceLocation HORSE_ONE_CM = registerCustom("horse_one_cm", IStatFormater.DISTANCE);
   public static final ResourceLocation AVIATE_ONE_CM = registerCustom("aviate_one_cm", IStatFormater.DISTANCE);
   /** distance you have swam */
   public static final ResourceLocation SWIM_ONE_CM = registerCustom("swim_one_cm", IStatFormater.DISTANCE);
   /** the times you've jumped */
   public static final ResourceLocation JUMP = registerCustom("jump", IStatFormater.DEFAULT);
   /** the distance you've dropped (or times you've fallen?) */
   public static final ResourceLocation DROP = registerCustom("drop", IStatFormater.DEFAULT);
   public static final ResourceLocation DAMAGE_DEALT = registerCustom("damage_dealt", IStatFormater.DIVIDE_BY_TEN);
   public static final ResourceLocation field_212735_F = registerCustom("damage_dealt_absorbed", IStatFormater.DIVIDE_BY_TEN);
   public static final ResourceLocation field_212736_G = registerCustom("damage_dealt_resisted", IStatFormater.DIVIDE_BY_TEN);
   public static final ResourceLocation DAMAGE_TAKEN = registerCustom("damage_taken", IStatFormater.DIVIDE_BY_TEN);
   public static final ResourceLocation field_212737_I = registerCustom("damage_blocked_by_shield", IStatFormater.DIVIDE_BY_TEN);
   public static final ResourceLocation field_212738_J = registerCustom("damage_absorbed", IStatFormater.DIVIDE_BY_TEN);
   public static final ResourceLocation field_212739_K = registerCustom("damage_resisted", IStatFormater.DIVIDE_BY_TEN);
   public static final ResourceLocation DEATHS = registerCustom("deaths", IStatFormater.DEFAULT);
   public static final ResourceLocation MOB_KILLS = registerCustom("mob_kills", IStatFormater.DEFAULT);
   /** the number of animals you have bred */
   public static final ResourceLocation ANIMALS_BRED = registerCustom("animals_bred", IStatFormater.DEFAULT);
   /** counts the number of times you've killed a player */
   public static final ResourceLocation PLAYER_KILLS = registerCustom("player_kills", IStatFormater.DEFAULT);
   public static final ResourceLocation FISH_CAUGHT = registerCustom("fish_caught", IStatFormater.DEFAULT);
   public static final ResourceLocation TALKED_TO_VILLAGER = registerCustom("talked_to_villager", IStatFormater.DEFAULT);
   public static final ResourceLocation TRADED_WITH_VILLAGER = registerCustom("traded_with_villager", IStatFormater.DEFAULT);
   public static final ResourceLocation EAT_CAKE_SLICE = registerCustom("eat_cake_slice", IStatFormater.DEFAULT);
   public static final ResourceLocation FILL_CAULDRON = registerCustom("fill_cauldron", IStatFormater.DEFAULT);
   public static final ResourceLocation USE_CAULDRON = registerCustom("use_cauldron", IStatFormater.DEFAULT);
   public static final ResourceLocation CLEAN_ARMOR = registerCustom("clean_armor", IStatFormater.DEFAULT);
   public static final ResourceLocation CLEAN_BANNER = registerCustom("clean_banner", IStatFormater.DEFAULT);
   public static final ResourceLocation field_212740_X = registerCustom("clean_shulker_box", IStatFormater.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_BREWINGSTAND = registerCustom("interact_with_brewingstand", IStatFormater.DEFAULT);
   public static final ResourceLocation BEACON_INTERACTION = registerCustom("interact_with_beacon", IStatFormater.DEFAULT);
   public static final ResourceLocation INSPECT_DROPPER = registerCustom("inspect_dropper", IStatFormater.DEFAULT);
   public static final ResourceLocation INSPECT_HOPPER = registerCustom("inspect_hopper", IStatFormater.DEFAULT);
   public static final ResourceLocation INSPECT_DISPENSER = registerCustom("inspect_dispenser", IStatFormater.DEFAULT);
   public static final ResourceLocation PLAY_NOTEBLOCK = registerCustom("play_noteblock", IStatFormater.DEFAULT);
   public static final ResourceLocation TUNE_NOTEBLOCK = registerCustom("tune_noteblock", IStatFormater.DEFAULT);
   public static final ResourceLocation POT_FLOWER = registerCustom("pot_flower", IStatFormater.DEFAULT);
   public static final ResourceLocation TRIGGER_TRAPPED_CHEST = registerCustom("trigger_trapped_chest", IStatFormater.DEFAULT);
   public static final ResourceLocation OPEN_ENDERCHEST = registerCustom("open_enderchest", IStatFormater.DEFAULT);
   public static final ResourceLocation ENCHANT_ITEM = registerCustom("enchant_item", IStatFormater.DEFAULT);
   public static final ResourceLocation PLAY_RECORD = registerCustom("play_record", IStatFormater.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_FURNACE = registerCustom("interact_with_furnace", IStatFormater.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_CRAFTING_TABLE = registerCustom("interact_with_crafting_table", IStatFormater.DEFAULT);
   public static final ResourceLocation OPEN_CHEST = registerCustom("open_chest", IStatFormater.DEFAULT);
   public static final ResourceLocation SLEEP_IN_BED = registerCustom("sleep_in_bed", IStatFormater.DEFAULT);
   public static final ResourceLocation OPEN_SHULKER_BOX = registerCustom("open_shulker_box", IStatFormater.DEFAULT);

   public static void func_212734_a() {
   }

   private static ResourceLocation registerCustom(String id, IStatFormater formatter) {
      ResourceLocation resourcelocation = new ResourceLocation(id);
      IRegistry.field_212623_l.put(resourcelocation, resourcelocation);
      CUSTOM.get(resourcelocation, formatter);
      return resourcelocation;
   }

   private static <T> StatType<T> registerType(String id, IRegistry<T> registry) {
      StatType<T> stattype = new StatType<>(registry);
      IRegistry.field_212634_w.put(new ResourceLocation(id), stattype);
      return stattype;
   }
}