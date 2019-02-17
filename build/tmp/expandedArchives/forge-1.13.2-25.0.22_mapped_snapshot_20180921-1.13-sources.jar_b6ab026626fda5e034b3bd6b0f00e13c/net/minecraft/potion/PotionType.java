package net.minecraft.potion;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.init.MobEffects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class PotionType extends net.minecraftforge.registries.ForgeRegistryEntry<PotionType> {
   /** The unlocalized name of this PotionType. If null, the registry name is used. */
   private final String baseName;
   private final ImmutableList<PotionEffect> effects;

   public static PotionType getPotionTypeForName(String name) {
      return IRegistry.field_212621_j.get(ResourceLocation.makeResourceLocation(name));
   }

   public PotionType(PotionEffect... p_i46739_1_) {
      this((String)null, p_i46739_1_);
   }

   public PotionType(@Nullable String p_i46740_1_, PotionEffect... p_i46740_2_) {
      this.baseName = p_i46740_1_;
      this.effects = ImmutableList.copyOf(p_i46740_2_);
   }

   /**
    * Gets the name of this PotionType with a prefix (such as "Splash" or "Lingering") prepended
    */
   public String getNamePrefixed(String p_185174_1_) {
      return p_185174_1_ + (this.baseName == null ? IRegistry.field_212621_j.getKey(this).getPath() : this.baseName);
   }

   public List<PotionEffect> getEffects() {
      return this.effects;
   }

   public static void registerPotionTypes() {
      register("empty", new PotionType());
      register("water", new PotionType());
      register("mundane", new PotionType());
      register("thick", new PotionType());
      register("awkward", new PotionType());
      register("night_vision", new PotionType(new PotionEffect(MobEffects.NIGHT_VISION, 3600)));
      register("long_night_vision", new PotionType("night_vision", new PotionEffect(MobEffects.NIGHT_VISION, 9600)));
      register("invisibility", new PotionType(new PotionEffect(MobEffects.INVISIBILITY, 3600)));
      register("long_invisibility", new PotionType("invisibility", new PotionEffect(MobEffects.INVISIBILITY, 9600)));
      register("leaping", new PotionType(new PotionEffect(MobEffects.JUMP_BOOST, 3600)));
      register("long_leaping", new PotionType("leaping", new PotionEffect(MobEffects.JUMP_BOOST, 9600)));
      register("strong_leaping", new PotionType("leaping", new PotionEffect(MobEffects.JUMP_BOOST, 1800, 1)));
      register("fire_resistance", new PotionType(new PotionEffect(MobEffects.FIRE_RESISTANCE, 3600)));
      register("long_fire_resistance", new PotionType("fire_resistance", new PotionEffect(MobEffects.FIRE_RESISTANCE, 9600)));
      register("swiftness", new PotionType(new PotionEffect(MobEffects.SPEED, 3600)));
      register("long_swiftness", new PotionType("swiftness", new PotionEffect(MobEffects.SPEED, 9600)));
      register("strong_swiftness", new PotionType("swiftness", new PotionEffect(MobEffects.SPEED, 1800, 1)));
      register("slowness", new PotionType(new PotionEffect(MobEffects.SLOWNESS, 1800)));
      register("long_slowness", new PotionType("slowness", new PotionEffect(MobEffects.SLOWNESS, 4800)));
      register("strong_slowness", new PotionType("slowness", new PotionEffect(MobEffects.SLOWNESS, 400, 3)));
      register("turtle_master", new PotionType("turtle_master", new PotionEffect(MobEffects.SLOWNESS, 400, 3), new PotionEffect(MobEffects.RESISTANCE, 400, 2)));
      register("long_turtle_master", new PotionType("turtle_master", new PotionEffect(MobEffects.SLOWNESS, 800, 3), new PotionEffect(MobEffects.RESISTANCE, 800, 2)));
      register("strong_turtle_master", new PotionType("turtle_master", new PotionEffect(MobEffects.SLOWNESS, 400, 5), new PotionEffect(MobEffects.RESISTANCE, 400, 3)));
      register("water_breathing", new PotionType(new PotionEffect(MobEffects.WATER_BREATHING, 3600)));
      register("long_water_breathing", new PotionType("water_breathing", new PotionEffect(MobEffects.WATER_BREATHING, 9600)));
      register("healing", new PotionType(new PotionEffect(MobEffects.INSTANT_HEALTH, 1)));
      register("strong_healing", new PotionType("healing", new PotionEffect(MobEffects.INSTANT_HEALTH, 1, 1)));
      register("harming", new PotionType(new PotionEffect(MobEffects.INSTANT_DAMAGE, 1)));
      register("strong_harming", new PotionType("harming", new PotionEffect(MobEffects.INSTANT_DAMAGE, 1, 1)));
      register("poison", new PotionType(new PotionEffect(MobEffects.POISON, 900)));
      register("long_poison", new PotionType("poison", new PotionEffect(MobEffects.POISON, 1800)));
      register("strong_poison", new PotionType("poison", new PotionEffect(MobEffects.POISON, 432, 1)));
      register("regeneration", new PotionType(new PotionEffect(MobEffects.REGENERATION, 900)));
      register("long_regeneration", new PotionType("regeneration", new PotionEffect(MobEffects.REGENERATION, 1800)));
      register("strong_regeneration", new PotionType("regeneration", new PotionEffect(MobEffects.REGENERATION, 450, 1)));
      register("strength", new PotionType(new PotionEffect(MobEffects.STRENGTH, 3600)));
      register("long_strength", new PotionType("strength", new PotionEffect(MobEffects.STRENGTH, 9600)));
      register("strong_strength", new PotionType("strength", new PotionEffect(MobEffects.STRENGTH, 1800, 1)));
      register("weakness", new PotionType(new PotionEffect(MobEffects.WEAKNESS, 1800)));
      register("long_weakness", new PotionType("weakness", new PotionEffect(MobEffects.WEAKNESS, 4800)));
      register("luck", new PotionType("luck", new PotionEffect(MobEffects.LUCK, 6000)));
      register("slow_falling", new PotionType(new PotionEffect(MobEffects.SLOW_FALLING, 1800)));
      register("long_slow_falling", new PotionType("slow_falling", new PotionEffect(MobEffects.SLOW_FALLING, 4800)));
   }

   protected static void register(String p_185173_0_, PotionType p_185173_1_) {
      IRegistry.field_212621_j.put(new ResourceLocation(p_185173_0_), p_185173_1_);
   }

   public boolean hasInstantEffect() {
      if (!this.effects.isEmpty()) {
         for(PotionEffect potioneffect : this.effects) {
            if (potioneffect.getPotion().isInstant()) {
               return true;
            }
         }
      }

      return false;
   }
}