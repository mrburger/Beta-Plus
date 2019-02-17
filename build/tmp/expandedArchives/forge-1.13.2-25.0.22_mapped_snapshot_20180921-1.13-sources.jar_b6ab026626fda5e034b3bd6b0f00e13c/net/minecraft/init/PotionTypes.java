package net.minecraft.init;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

@net.minecraftforge.registries.ObjectHolder("minecraft")
public class PotionTypes {
   /**
    * During the registration process, contains all potions that have been registered so far (along with null) to avoid
    * duplicates (and including null).
    */
   private static final Set<PotionType> CACHE;
   public static final PotionType EMPTY;
   public static final PotionType WATER;
   public static final PotionType MUNDANE;
   public static final PotionType THICK;
   public static final PotionType AWKWARD;
   public static final PotionType NIGHT_VISION;
   public static final PotionType LONG_NIGHT_VISION;
   public static final PotionType INVISIBILITY;
   public static final PotionType LONG_INVISIBILITY;
   public static final PotionType LEAPING;
   public static final PotionType LONG_LEAPING;
   public static final PotionType STRONG_LEAPING;
   public static final PotionType FIRE_RESISTANCE;
   public static final PotionType LONG_FIRE_RESISTANCE;
   public static final PotionType SWIFTNESS;
   public static final PotionType LONG_SWIFTNESS;
   public static final PotionType STRONG_SWIFTNESS;
   public static final PotionType SLOWNESS;
   public static final PotionType LONG_SLOWNESS;
   public static final PotionType STRONG_SLOWNESS;
   public static final PotionType TURTLE_MASTER;
   public static final PotionType LONG_TURTLE_MASTER;
   public static final PotionType STRONG_TURTLE_MASTER;
   public static final PotionType WATER_BREATHING;
   public static final PotionType LONG_WATER_BREATHING;
   public static final PotionType HEALING;
   public static final PotionType STRONG_HEALING;
   public static final PotionType HARMING;
   public static final PotionType STRONG_HARMING;
   public static final PotionType POISON;
   public static final PotionType LONG_POISON;
   public static final PotionType STRONG_POISON;
   public static final PotionType REGENERATION;
   public static final PotionType LONG_REGENERATION;
   public static final PotionType STRONG_REGENERATION;
   public static final PotionType STRENGTH;
   public static final PotionType LONG_STRENGTH;
   public static final PotionType STRONG_STRENGTH;
   public static final PotionType WEAKNESS;
   public static final PotionType LONG_WEAKNESS;
   public static final PotionType SLOW_FALLING;
   public static final PotionType LONG_SLOW_FALLING;

   private static PotionType getRegisteredPotionType(String id) {
      PotionType potiontype = IRegistry.field_212621_j.get(new ResourceLocation(id));
      if (!CACHE.add(potiontype)) {
         throw new IllegalStateException("Invalid Potion requested: " + id);
      } else {
         return potiontype;
      }
   }

   static {
      if (!Bootstrap.isRegistered()) {
         throw new RuntimeException("Accessed Potions before Bootstrap!");
      } else {
         CACHE = Sets.newHashSet((PotionType)null);
         EMPTY = getRegisteredPotionType("empty");
         WATER = getRegisteredPotionType("water");
         MUNDANE = getRegisteredPotionType("mundane");
         THICK = getRegisteredPotionType("thick");
         AWKWARD = getRegisteredPotionType("awkward");
         NIGHT_VISION = getRegisteredPotionType("night_vision");
         LONG_NIGHT_VISION = getRegisteredPotionType("long_night_vision");
         INVISIBILITY = getRegisteredPotionType("invisibility");
         LONG_INVISIBILITY = getRegisteredPotionType("long_invisibility");
         LEAPING = getRegisteredPotionType("leaping");
         LONG_LEAPING = getRegisteredPotionType("long_leaping");
         STRONG_LEAPING = getRegisteredPotionType("strong_leaping");
         FIRE_RESISTANCE = getRegisteredPotionType("fire_resistance");
         LONG_FIRE_RESISTANCE = getRegisteredPotionType("long_fire_resistance");
         SWIFTNESS = getRegisteredPotionType("swiftness");
         LONG_SWIFTNESS = getRegisteredPotionType("long_swiftness");
         STRONG_SWIFTNESS = getRegisteredPotionType("strong_swiftness");
         SLOWNESS = getRegisteredPotionType("slowness");
         LONG_SLOWNESS = getRegisteredPotionType("long_slowness");
         STRONG_SLOWNESS = getRegisteredPotionType("strong_slowness");
         TURTLE_MASTER = getRegisteredPotionType("turtle_master");
         LONG_TURTLE_MASTER = getRegisteredPotionType("long_turtle_master");
         STRONG_TURTLE_MASTER = getRegisteredPotionType("strong_turtle_master");
         WATER_BREATHING = getRegisteredPotionType("water_breathing");
         LONG_WATER_BREATHING = getRegisteredPotionType("long_water_breathing");
         HEALING = getRegisteredPotionType("healing");
         STRONG_HEALING = getRegisteredPotionType("strong_healing");
         HARMING = getRegisteredPotionType("harming");
         STRONG_HARMING = getRegisteredPotionType("strong_harming");
         POISON = getRegisteredPotionType("poison");
         LONG_POISON = getRegisteredPotionType("long_poison");
         STRONG_POISON = getRegisteredPotionType("strong_poison");
         REGENERATION = getRegisteredPotionType("regeneration");
         LONG_REGENERATION = getRegisteredPotionType("long_regeneration");
         STRONG_REGENERATION = getRegisteredPotionType("strong_regeneration");
         STRENGTH = getRegisteredPotionType("strength");
         LONG_STRENGTH = getRegisteredPotionType("long_strength");
         STRONG_STRENGTH = getRegisteredPotionType("strong_strength");
         WEAKNESS = getRegisteredPotionType("weakness");
         LONG_WEAKNESS = getRegisteredPotionType("long_weakness");
         SLOW_FALLING = getRegisteredPotionType("slow_falling");
         LONG_SLOW_FALLING = getRegisteredPotionType("long_slow_falling");
         CACHE.clear();
      }
   }
}