package net.minecraft.init;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

@net.minecraftforge.registries.ObjectHolder("minecraft")
public class Particles {
   public static final BasicParticleType AMBIENT_ENTITY_EFFECT;
   public static final BasicParticleType ANGRY_VILLAGER;
   public static final BasicParticleType BARRIER;
   public static final ParticleType<BlockParticleData> BLOCK;
   public static final BasicParticleType BUBBLE;
   public static final BasicParticleType BUBBLE_COLUMN_UP;
   public static final BasicParticleType CLOUD;
   public static final BasicParticleType CRIT;
   public static final BasicParticleType DAMAGE_INDICATOR;
   public static final BasicParticleType DRAGON_BREATH;
   public static final BasicParticleType DRIPPING_LAVA;
   public static final BasicParticleType DRIPPING_WATER;
   public static final ParticleType<RedstoneParticleData> DUST;
   public static final BasicParticleType EFFECT;
   public static final BasicParticleType ELDER_GUARDIAN;
   public static final BasicParticleType ENCHANTED_HIT;
   public static final BasicParticleType ENCHANT;
   public static final BasicParticleType END_ROD;
   public static final BasicParticleType ENTITY_EFFECT;
   public static final BasicParticleType EXPLOSION_EMITTER;
   public static final BasicParticleType EXPLOSION;
   public static final ParticleType<BlockParticleData> FALLING_DUST;
   public static final BasicParticleType FIREWORK;
   public static final BasicParticleType FISHING;
   public static final BasicParticleType FLAME;
   public static final BasicParticleType HAPPY_VILLAGER;
   public static final BasicParticleType HEART;
   public static final BasicParticleType INSTANT_EFFECT;
   public static final ParticleType<ItemParticleData> ITEM;
   public static final BasicParticleType ITEM_SLIME;
   public static final BasicParticleType ITEM_SNOWBALL;
   public static final BasicParticleType LARGE_SMOKE;
   public static final BasicParticleType LAVA;
   public static final BasicParticleType MYCELIUM;
   public static final BasicParticleType NOTE;
   public static final BasicParticleType POOF;
   public static final BasicParticleType PORTAL;
   public static final BasicParticleType RAIN;
   public static final BasicParticleType SMOKE;
   public static final BasicParticleType SPIT;
   public static final BasicParticleType SWEEP_ATTACK;
   public static final BasicParticleType TOTEM_OF_UNDYING;
   public static final BasicParticleType UNDERWATER;
   public static final BasicParticleType SPLASH;
   public static final BasicParticleType WITCH;
   public static final BasicParticleType BUBBLE_POP;
   public static final BasicParticleType CURRENT_DOWN;
   public static final BasicParticleType SQUID_INK;
   public static final BasicParticleType NAUTILUS;
   public static final BasicParticleType DOLPHIN;

   private static <T extends ParticleType<?>> T getRegisteredParticleTypes(String p_197589_0_) {
      T t = (T)IRegistry.field_212632_u.func_212608_b(new ResourceLocation(p_197589_0_));
      if (t == null) {
         throw new IllegalStateException("Invalid or unknown particle type: " + p_197589_0_);
      } else {
         return t;
      }
   }

   static {
      if (!Bootstrap.isRegistered()) {
         throw new RuntimeException("Accessed particles before Bootstrap!");
      } else {
         AMBIENT_ENTITY_EFFECT = getRegisteredParticleTypes("ambient_entity_effect");
         ANGRY_VILLAGER = getRegisteredParticleTypes("angry_villager");
         BARRIER = getRegisteredParticleTypes("barrier");
         BLOCK = getRegisteredParticleTypes("block");
         BUBBLE = getRegisteredParticleTypes("bubble");
         BUBBLE_COLUMN_UP = getRegisteredParticleTypes("bubble_column_up");
         CLOUD = getRegisteredParticleTypes("cloud");
         CRIT = getRegisteredParticleTypes("crit");
         DAMAGE_INDICATOR = getRegisteredParticleTypes("damage_indicator");
         DRAGON_BREATH = getRegisteredParticleTypes("dragon_breath");
         DRIPPING_LAVA = getRegisteredParticleTypes("dripping_lava");
         DRIPPING_WATER = getRegisteredParticleTypes("dripping_water");
         DUST = getRegisteredParticleTypes("dust");
         EFFECT = getRegisteredParticleTypes("effect");
         ELDER_GUARDIAN = getRegisteredParticleTypes("elder_guardian");
         ENCHANTED_HIT = getRegisteredParticleTypes("enchanted_hit");
         ENCHANT = getRegisteredParticleTypes("enchant");
         END_ROD = getRegisteredParticleTypes("end_rod");
         ENTITY_EFFECT = getRegisteredParticleTypes("entity_effect");
         EXPLOSION_EMITTER = getRegisteredParticleTypes("explosion_emitter");
         EXPLOSION = getRegisteredParticleTypes("explosion");
         FALLING_DUST = getRegisteredParticleTypes("falling_dust");
         FIREWORK = getRegisteredParticleTypes("firework");
         FISHING = getRegisteredParticleTypes("fishing");
         FLAME = getRegisteredParticleTypes("flame");
         HAPPY_VILLAGER = getRegisteredParticleTypes("happy_villager");
         HEART = getRegisteredParticleTypes("heart");
         INSTANT_EFFECT = getRegisteredParticleTypes("instant_effect");
         ITEM = getRegisteredParticleTypes("item");
         ITEM_SLIME = getRegisteredParticleTypes("item_slime");
         ITEM_SNOWBALL = getRegisteredParticleTypes("item_snowball");
         LARGE_SMOKE = getRegisteredParticleTypes("large_smoke");
         LAVA = getRegisteredParticleTypes("lava");
         MYCELIUM = getRegisteredParticleTypes("mycelium");
         NOTE = getRegisteredParticleTypes("note");
         POOF = getRegisteredParticleTypes("poof");
         PORTAL = getRegisteredParticleTypes("portal");
         RAIN = getRegisteredParticleTypes("rain");
         SMOKE = getRegisteredParticleTypes("smoke");
         SPIT = getRegisteredParticleTypes("spit");
         SWEEP_ATTACK = getRegisteredParticleTypes("sweep_attack");
         TOTEM_OF_UNDYING = getRegisteredParticleTypes("totem_of_undying");
         UNDERWATER = getRegisteredParticleTypes("underwater");
         SPLASH = getRegisteredParticleTypes("splash");
         WITCH = getRegisteredParticleTypes("witch");
         BUBBLE_POP = getRegisteredParticleTypes("bubble_pop");
         CURRENT_DOWN = getRegisteredParticleTypes("current_down");
         SQUID_INK = getRegisteredParticleTypes("squid_ink");
         NAUTILUS = getRegisteredParticleTypes("nautilus");
         DOLPHIN = getRegisteredParticleTypes("dolphin");
      }
   }
}