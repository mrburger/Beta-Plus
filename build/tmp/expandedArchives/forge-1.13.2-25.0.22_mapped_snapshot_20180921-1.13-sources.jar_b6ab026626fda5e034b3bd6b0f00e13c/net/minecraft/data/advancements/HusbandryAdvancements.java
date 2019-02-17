package net.minecraft.data.advancements;

import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.criterion.BredAnimalsTrigger;
import net.minecraft.advancements.criterion.ConsumeItemTrigger;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.FilledBucketTrigger;
import net.minecraft.advancements.criterion.FishingRodHookedTrigger;
import net.minecraft.advancements.criterion.ItemDurabilityTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.PlacedBlockTrigger;
import net.minecraft.advancements.criterion.TameAnimalTrigger;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextComponentTranslation;

public class HusbandryAdvancements implements Consumer<Consumer<Advancement>> {
   private static final EntityType<?>[] BREEDABLE_ANIMALS = new EntityType[]{EntityType.HORSE, EntityType.SHEEP, EntityType.COW, EntityType.MOOSHROOM, EntityType.PIG, EntityType.CHICKEN, EntityType.WOLF, EntityType.OCELOT, EntityType.RABBIT, EntityType.LLAMA, EntityType.TURTLE};
   private static final Item[] FISH_ITEMS = new Item[]{Items.COD, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.SALMON};
   private static final Item[] FISH_BUCKETS = new Item[]{Items.COD_BUCKET, Items.TROPICAL_FISH_BUCKET, Items.PUFFERFISH_BUCKET, Items.SALMON_BUCKET};
   private static final Item[] BALANCED_DIET = new Item[]{Items.APPLE, Items.MUSHROOM_STEW, Items.BREAD, Items.PORKCHOP, Items.COOKED_PORKCHOP, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE, Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.COOKED_COD, Items.COOKED_SALMON, Items.COOKIE, Items.MELON_SLICE, Items.BEEF, Items.COOKED_BEEF, Items.CHICKEN, Items.COOKED_CHICKEN, Items.ROTTEN_FLESH, Items.SPIDER_EYE, Items.CARROT, Items.POTATO, Items.BAKED_POTATO, Items.POISONOUS_POTATO, Items.GOLDEN_CARROT, Items.PUMPKIN_PIE, Items.RABBIT, Items.COOKED_RABBIT, Items.RABBIT_STEW, Items.MUTTON, Items.COOKED_MUTTON, Items.CHORUS_FRUIT, Items.BEETROOT, Items.BEETROOT_SOUP, Items.DRIED_KELP};

   public void accept(Consumer<Advancement> p_accept_1_) {
      Advancement advancement = Advancement.Builder.builder().withDisplay(Blocks.HAY_BLOCK, new TextComponentTranslation("advancements.husbandry.root.title"), new TextComponentTranslation("advancements.husbandry.root.description"), new ResourceLocation("minecraft:textures/gui/advancements/backgrounds/husbandry.png"), FrameType.TASK, false, false, false).withCriterion("consumed_item", ConsumeItemTrigger.Instance.func_203914_c()).register(p_accept_1_, "husbandry/root");
      Advancement advancement1 = Advancement.Builder.builder().withParent(advancement).withDisplay(Items.WHEAT, new TextComponentTranslation("advancements.husbandry.plant_seed.title"), new TextComponentTranslation("advancements.husbandry.plant_seed.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).withRequirementsStrategy(RequirementsStrategy.OR).withCriterion("wheat", PlacedBlockTrigger.Instance.func_203934_a(Blocks.WHEAT)).withCriterion("pumpkin_stem", PlacedBlockTrigger.Instance.func_203934_a(Blocks.PUMPKIN_STEM)).withCriterion("melon_stem", PlacedBlockTrigger.Instance.func_203934_a(Blocks.MELON_STEM)).withCriterion("beetroots", PlacedBlockTrigger.Instance.func_203934_a(Blocks.BEETROOTS)).withCriterion("nether_wart", PlacedBlockTrigger.Instance.func_203934_a(Blocks.NETHER_WART)).register(p_accept_1_, "husbandry/plant_seed");
      Advancement advancement2 = Advancement.Builder.builder().withParent(advancement).withDisplay(Items.WHEAT, new TextComponentTranslation("advancements.husbandry.breed_an_animal.title"), new TextComponentTranslation("advancements.husbandry.breed_an_animal.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).withRequirementsStrategy(RequirementsStrategy.OR).withCriterion("bred", BredAnimalsTrigger.Instance.func_203908_c()).register(p_accept_1_, "husbandry/breed_an_animal");
      Advancement advancement3 = this.makeBalancedDiet(Advancement.Builder.builder()).withParent(advancement1).withDisplay(Items.APPLE, new TextComponentTranslation("advancements.husbandry.balanced_diet.title"), new TextComponentTranslation("advancements.husbandry.balanced_diet.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).withRewards(AdvancementRewards.Builder.experience(100)).register(p_accept_1_, "husbandry/balanced_diet");
      Advancement advancement4 = Advancement.Builder.builder().withParent(advancement1).withDisplay(Items.DIAMOND_HOE, new TextComponentTranslation("advancements.husbandry.break_diamond_hoe.title"), new TextComponentTranslation("advancements.husbandry.break_diamond_hoe.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).withRewards(AdvancementRewards.Builder.experience(100)).withCriterion("broke_hoe", ItemDurabilityTrigger.Instance.func_211182_a(ItemPredicate.Builder.create().func_200308_a(Items.DIAMOND_HOE).build(), MinMaxBounds.IntBound.exactly(-1))).register(p_accept_1_, "husbandry/break_diamond_hoe");
      Advancement advancement5 = Advancement.Builder.builder().withParent(advancement).withDisplay(Items.LEAD, new TextComponentTranslation("advancements.husbandry.tame_an_animal.title"), new TextComponentTranslation("advancements.husbandry.tame_an_animal.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).withCriterion("tamed_animal", TameAnimalTrigger.Instance.func_203938_c()).register(p_accept_1_, "husbandry/tame_an_animal");
      Advancement advancement6 = this.makeBredAllAnimals(Advancement.Builder.builder()).withParent(advancement2).withDisplay(Items.GOLDEN_CARROT, new TextComponentTranslation("advancements.husbandry.breed_all_animals.title"), new TextComponentTranslation("advancements.husbandry.breed_all_animals.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).withRewards(AdvancementRewards.Builder.experience(100)).register(p_accept_1_, "husbandry/bred_all_animals");
      Advancement advancement7 = this.makeFish(Advancement.Builder.builder()).withParent(advancement).withRequirementsStrategy(RequirementsStrategy.OR).withDisplay(Items.FISHING_ROD, new TextComponentTranslation("advancements.husbandry.fishy_business.title"), new TextComponentTranslation("advancements.husbandry.fishy_business.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).register(p_accept_1_, "husbandry/fishy_business");
      Advancement advancement8 = this.makeFishBucket(Advancement.Builder.builder()).withParent(advancement7).withRequirementsStrategy(RequirementsStrategy.OR).withDisplay(Items.PUFFERFISH_BUCKET, new TextComponentTranslation("advancements.husbandry.tactical_fishing.title"), new TextComponentTranslation("advancements.husbandry.tactical_fishing.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).register(p_accept_1_, "husbandry/tactical_fishing");
   }

   /**
    * Adds all the items in {@link #BALANCED_DIET} to the given advancement's criteria
    */
   private Advancement.Builder makeBalancedDiet(Advancement.Builder builder) {
      for(Item item : BALANCED_DIET) {
         builder.withCriterion(IRegistry.field_212630_s.getKey(item).getPath(), ConsumeItemTrigger.Instance.func_203913_a(item));
      }

      return builder;
   }

   private Advancement.Builder makeBredAllAnimals(Advancement.Builder builder) {
      for(EntityType<?> entitytype : BREEDABLE_ANIMALS) {
         builder.withCriterion(EntityType.getId(entitytype).toString(), BredAnimalsTrigger.Instance.func_203909_a(EntityPredicate.Builder.func_203996_a().func_203998_a(entitytype)));
      }

      return builder;
   }

   private Advancement.Builder makeFishBucket(Advancement.Builder builder) {
      for(Item item : FISH_BUCKETS) {
         builder.withCriterion(IRegistry.field_212630_s.getKey(item).getPath(), FilledBucketTrigger.Instance.func_204827_a(ItemPredicate.Builder.create().func_200308_a(item).build()));
      }

      return builder;
   }

   private Advancement.Builder makeFish(Advancement.Builder builder) {
      for(Item item : FISH_ITEMS) {
         builder.withCriterion(IRegistry.field_212630_s.getKey(item).getPath(), FishingRodHookedTrigger.Instance.func_204829_a(ItemPredicate.ANY, EntityPredicate.ANY, ItemPredicate.Builder.create().func_200308_a(item).build()));
      }

      return builder;
   }
}