package net.minecraft.data.advancements;

import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.criterion.ChanneledLightningTrigger;
import net.minecraft.advancements.criterion.DamagePredicate;
import net.minecraft.advancements.criterion.DamageSourcePredicate;
import net.minecraft.advancements.criterion.DistancePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.KilledTrigger;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.PlayerHurtEntityTrigger;
import net.minecraft.advancements.criterion.PositionTrigger;
import net.minecraft.advancements.criterion.SummonedEntityTrigger;
import net.minecraft.advancements.criterion.UsedTotemTrigger;
import net.minecraft.advancements.criterion.VillagerTradeTrigger;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.biome.Biome;

public class AdventureAdvancements implements Consumer<Consumer<Advancement>> {
   private static final Biome[] EXPLORATION_BIOMES = new Biome[]{Biomes.BIRCH_FOREST_HILLS, Biomes.RIVER, Biomes.SWAMP, Biomes.DESERT, Biomes.WOODED_HILLS, Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.SNOWY_TAIGA, Biomes.BADLANDS, Biomes.FOREST, Biomes.STONE_SHORE, Biomes.SNOWY_TUNDRA, Biomes.TAIGA_HILLS, Biomes.SNOWY_MOUNTAINS, Biomes.WOODED_BADLANDS_PLATEAU, Biomes.SAVANNA, Biomes.PLAINS, Biomes.FROZEN_RIVER, Biomes.GIANT_TREE_TAIGA, Biomes.SNOWY_BEACH, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE, Biomes.MUSHROOM_FIELD_SHORE, Biomes.MOUNTAINS, Biomes.DESERT_HILLS, Biomes.JUNGLE, Biomes.BEACH, Biomes.SAVANNA_PLATEAU, Biomes.SNOWY_TAIGA_HILLS, Biomes.BADLANDS_PLATEAU, Biomes.DARK_FOREST, Biomes.TAIGA, Biomes.BIRCH_FOREST, Biomes.MUSHROOM_FIELDS, Biomes.WOODED_MOUNTAINS, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN};
   private static final EntityType<?>[] MOB_ENTITIES = new EntityType[]{EntityType.CAVE_SPIDER, EntityType.SPIDER, EntityType.ZOMBIE_PIGMAN, EntityType.ENDERMAN, EntityType.POLAR_BEAR, EntityType.BLAZE, EntityType.CREEPER, EntityType.EVOKER, EntityType.GHAST, EntityType.GUARDIAN, EntityType.HUSK, EntityType.MAGMA_CUBE, EntityType.SHULKER, EntityType.SILVERFISH, EntityType.SKELETON, EntityType.SLIME, EntityType.STRAY, EntityType.VINDICATOR, EntityType.WITCH, EntityType.WITHER_SKELETON, EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER, EntityType.PHANTOM, EntityType.DROWNED};

   public void accept(Consumer<Advancement> p_accept_1_) {
      Advancement advancement = Advancement.Builder.builder().withDisplay(Items.MAP, new TextComponentTranslation("advancements.adventure.root.title"), new TextComponentTranslation("advancements.adventure.root.description"), new ResourceLocation("minecraft:textures/gui/advancements/backgrounds/adventure.png"), FrameType.TASK, false, false, false).withRequirementsStrategy(RequirementsStrategy.OR).withCriterion("killed_something", KilledTrigger.Instance.func_203927_c()).withCriterion("killed_by_something", KilledTrigger.Instance.func_203926_d()).register(p_accept_1_, "adventure/root");
      Advancement advancement1 = Advancement.Builder.builder().withParent(advancement).withDisplay(Blocks.RED_BED, new TextComponentTranslation("advancements.adventure.sleep_in_bed.title"), new TextComponentTranslation("advancements.adventure.sleep_in_bed.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).withCriterion("slept_in_bed", PositionTrigger.Instance.func_203931_c()).register(p_accept_1_, "adventure/sleep_in_bed");
      Advancement advancement2 = this.makeBiomeAdvancement(Advancement.Builder.builder()).withParent(advancement1).withDisplay(Items.DIAMOND_BOOTS, new TextComponentTranslation("advancements.adventure.adventuring_time.title"), new TextComponentTranslation("advancements.adventure.adventuring_time.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).withRewards(AdvancementRewards.Builder.experience(500)).register(p_accept_1_, "adventure/adventuring_time");
      Advancement advancement3 = Advancement.Builder.builder().withParent(advancement).withDisplay(Items.EMERALD, new TextComponentTranslation("advancements.adventure.trade.title"), new TextComponentTranslation("advancements.adventure.trade.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).withCriterion("traded", VillagerTradeTrigger.Instance.func_203939_c()).register(p_accept_1_, "adventure/trade");
      Advancement advancement4 = this.makeMobAdvancement(Advancement.Builder.builder()).withParent(advancement).withDisplay(Items.IRON_SWORD, new TextComponentTranslation("advancements.adventure.kill_a_mob.title"), new TextComponentTranslation("advancements.adventure.kill_a_mob.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).withRequirementsStrategy(RequirementsStrategy.OR).register(p_accept_1_, "adventure/kill_a_mob");
      Advancement advancement5 = this.makeMobAdvancement(Advancement.Builder.builder()).withParent(advancement4).withDisplay(Items.DIAMOND_SWORD, new TextComponentTranslation("advancements.adventure.kill_all_mobs.title"), new TextComponentTranslation("advancements.adventure.kill_all_mobs.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).withRewards(AdvancementRewards.Builder.experience(100)).register(p_accept_1_, "adventure/kill_all_mobs");
      Advancement advancement6 = Advancement.Builder.builder().withParent(advancement4).withDisplay(Items.BOW, new TextComponentTranslation("advancements.adventure.shoot_arrow.title"), new TextComponentTranslation("advancements.adventure.shoot_arrow.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).withCriterion("shot_arrow", PlayerHurtEntityTrigger.Instance.func_203936_a(DamagePredicate.Builder.create().type(DamageSourcePredicate.Builder.damageType().isProjectile(true).direct(EntityPredicate.Builder.func_203996_a().func_203998_a(EntityType.ARROW))))).register(p_accept_1_, "adventure/shoot_arrow");
      Advancement advancement7 = Advancement.Builder.builder().withParent(advancement4).withDisplay(Items.TRIDENT, new TextComponentTranslation("advancements.adventure.throw_trident.title"), new TextComponentTranslation("advancements.adventure.throw_trident.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).withCriterion("shot_trident", PlayerHurtEntityTrigger.Instance.func_203936_a(DamagePredicate.Builder.create().type(DamageSourcePredicate.Builder.damageType().isProjectile(true).direct(EntityPredicate.Builder.func_203996_a().func_203998_a(EntityType.TRIDENT))))).register(p_accept_1_, "adventure/throw_trident");
      Advancement advancement8 = Advancement.Builder.builder().withParent(advancement7).withDisplay(Items.TRIDENT, new TextComponentTranslation("advancements.adventure.very_very_frightening.title"), new TextComponentTranslation("advancements.adventure.very_very_frightening.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).withCriterion("struck_villager", ChanneledLightningTrigger.Instance.func_204824_a(EntityPredicate.Builder.func_203996_a().func_203998_a(EntityType.VILLAGER).build())).register(p_accept_1_, "adventure/very_very_frightening");
      Advancement advancement9 = Advancement.Builder.builder().withParent(advancement3).withDisplay(Blocks.CARVED_PUMPKIN, new TextComponentTranslation("advancements.adventure.summon_iron_golem.title"), new TextComponentTranslation("advancements.adventure.summon_iron_golem.description"), (ResourceLocation)null, FrameType.GOAL, true, true, false).withCriterion("summoned_golem", SummonedEntityTrigger.Instance.func_203937_a(EntityPredicate.Builder.func_203996_a().func_203998_a(EntityType.IRON_GOLEM))).register(p_accept_1_, "adventure/summon_iron_golem");
      Advancement advancement10 = Advancement.Builder.builder().withParent(advancement6).withDisplay(Items.ARROW, new TextComponentTranslation("advancements.adventure.sniper_duel.title"), new TextComponentTranslation("advancements.adventure.sniper_duel.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).withRewards(AdvancementRewards.Builder.experience(50)).withCriterion("killed_skeleton", KilledTrigger.Instance.func_203929_a(EntityPredicate.Builder.func_203996_a().func_203998_a(EntityType.SKELETON).func_203997_a(DistancePredicate.func_203995_a(MinMaxBounds.FloatBound.atLeast(50.0F))), DamageSourcePredicate.Builder.damageType().isProjectile(true))).register(p_accept_1_, "adventure/sniper_duel");
      Advancement advancement11 = Advancement.Builder.builder().withParent(advancement4).withDisplay(Items.TOTEM_OF_UNDYING, new TextComponentTranslation("advancements.adventure.totem_of_undying.title"), new TextComponentTranslation("advancements.adventure.totem_of_undying.description"), (ResourceLocation)null, FrameType.GOAL, true, true, false).withCriterion("used_totem", UsedTotemTrigger.Instance.func_203941_a(Items.TOTEM_OF_UNDYING)).register(p_accept_1_, "adventure/totem_of_undying");
   }

   /**
    * Adds all the entities in {@link #MOB_ENTITIES} to the given advancement's criteria
    */
   private Advancement.Builder makeMobAdvancement(Advancement.Builder builder) {
      for(EntityType<?> entitytype : MOB_ENTITIES) {
         builder.withCriterion(IRegistry.field_212629_r.getKey(entitytype).toString(), KilledTrigger.Instance.func_203928_a(EntityPredicate.Builder.func_203996_a().func_203998_a(entitytype)));
      }

      return builder;
   }

   /**
    * Adds all the biomes in {@link #EXPLORATION_BIOMES} to the given advancement's criteria
    */
   private Advancement.Builder makeBiomeAdvancement(Advancement.Builder builder) {
      for(Biome biome : EXPLORATION_BIOMES) {
         builder.withCriterion(IRegistry.field_212624_m.getKey(biome).toString(), PositionTrigger.Instance.func_203932_a(LocationPredicate.func_204010_a(biome)));
      }

      return builder;
   }
}