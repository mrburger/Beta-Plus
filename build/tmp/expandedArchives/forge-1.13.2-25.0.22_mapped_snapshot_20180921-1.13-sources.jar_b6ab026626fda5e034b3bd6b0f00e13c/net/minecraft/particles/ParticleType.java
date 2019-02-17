package net.minecraft.particles;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class ParticleType<T extends IParticleData> {
   private final ResourceLocation resourceLocation;
   private final boolean alwaysShow;
   private final IParticleData.IDeserializer<T> deserializer;

   protected ParticleType(ResourceLocation resourceLocationIn, boolean alwaysRender, IParticleData.IDeserializer<T> deserializerIn) {
      this.resourceLocation = resourceLocationIn;
      this.alwaysShow = alwaysRender;
      this.deserializer = deserializerIn;
   }

   public static void registerAll() {
      register("ambient_entity_effect", false);
      register("angry_villager", false);
      register("barrier", false);
      register("block", false, BlockParticleData.DESERIALIZER);
      register("bubble", false);
      register("cloud", false);
      register("crit", false);
      register("damage_indicator", true);
      register("dragon_breath", false);
      register("dripping_lava", false);
      register("dripping_water", false);
      register("dust", false, RedstoneParticleData.DESERIALIZER);
      register("effect", false);
      register("elder_guardian", true);
      register("enchanted_hit", false);
      register("enchant", false);
      register("end_rod", false);
      register("entity_effect", false);
      register("explosion_emitter", true);
      register("explosion", true);
      register("falling_dust", false, BlockParticleData.DESERIALIZER);
      register("firework", false);
      register("fishing", false);
      register("flame", false);
      register("happy_villager", false);
      register("heart", false);
      register("instant_effect", false);
      register("item", false, ItemParticleData.DESERIALIZER);
      register("item_slime", false);
      register("item_snowball", false);
      register("large_smoke", false);
      register("lava", false);
      register("mycelium", false);
      register("note", false);
      register("poof", true);
      register("portal", false);
      register("rain", false);
      register("smoke", false);
      register("spit", true);
      register("squid_ink", true);
      register("sweep_attack", true);
      register("totem_of_undying", false);
      register("underwater", false);
      register("splash", false);
      register("witch", false);
      register("bubble_pop", false);
      register("current_down", false);
      register("bubble_column_up", false);
      register("nautilus", false);
      register("dolphin", false);
   }

   public ResourceLocation getId() {
      return this.resourceLocation;
   }

   public boolean getAlwaysShow() {
      return this.alwaysShow;
   }

   public IParticleData.IDeserializer<T> getDeserializer() {
      return this.deserializer;
   }

   private static void register(String id, boolean alwaysRender) {
      IRegistry.field_212632_u.put(new ResourceLocation(id), new BasicParticleType(new ResourceLocation(id), alwaysRender));
   }

   private static <T extends IParticleData> void register(String id, boolean alwaysRender, IParticleData.IDeserializer<T> deserializerIn) {
      IRegistry.field_212632_u.put(new ResourceLocation(id), new ParticleType<>(new ResourceLocation(id), alwaysRender, deserializerIn));
   }
}