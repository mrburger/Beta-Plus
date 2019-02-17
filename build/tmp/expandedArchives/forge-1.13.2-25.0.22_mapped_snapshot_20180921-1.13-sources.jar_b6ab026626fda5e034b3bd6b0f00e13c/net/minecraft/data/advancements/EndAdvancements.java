package net.minecraft.data.advancements;

import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.criterion.ChangeDimensionTrigger;
import net.minecraft.advancements.criterion.DistancePredicate;
import net.minecraft.advancements.criterion.EnterBlockTrigger;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.KilledTrigger;
import net.minecraft.advancements.criterion.LevitationTrigger;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.PositionTrigger;
import net.minecraft.advancements.criterion.SummonedEntityTrigger;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.dimension.DimensionType;

public class EndAdvancements implements Consumer<Consumer<Advancement>> {
   public void accept(Consumer<Advancement> p_accept_1_) {
      Advancement advancement = Advancement.Builder.builder().withDisplay(Blocks.END_STONE, new TextComponentTranslation("advancements.end.root.title"), new TextComponentTranslation("advancements.end.root.description"), new ResourceLocation("minecraft:textures/gui/advancements/backgrounds/end.png"), FrameType.TASK, false, false, false).withCriterion("entered_end", ChangeDimensionTrigger.Instance.func_203911_a(DimensionType.THE_END)).register(p_accept_1_, "end/root");
      Advancement advancement1 = Advancement.Builder.builder().withParent(advancement).withDisplay(Blocks.DRAGON_HEAD, new TextComponentTranslation("advancements.end.kill_dragon.title"), new TextComponentTranslation("advancements.end.kill_dragon.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).withCriterion("killed_dragon", KilledTrigger.Instance.func_203928_a(EntityPredicate.Builder.func_203996_a().func_203998_a(EntityType.ENDER_DRAGON))).register(p_accept_1_, "end/kill_dragon");
      Advancement advancement2 = Advancement.Builder.builder().withParent(advancement1).withDisplay(Items.ENDER_PEARL, new TextComponentTranslation("advancements.end.enter_end_gateway.title"), new TextComponentTranslation("advancements.end.enter_end_gateway.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).withCriterion("entered_end_gateway", EnterBlockTrigger.Instance.func_203920_a(Blocks.END_GATEWAY)).register(p_accept_1_, "end/enter_end_gateway");
      Advancement advancement3 = Advancement.Builder.builder().withParent(advancement1).withDisplay(Items.END_CRYSTAL, new TextComponentTranslation("advancements.end.respawn_dragon.title"), new TextComponentTranslation("advancements.end.respawn_dragon.description"), (ResourceLocation)null, FrameType.GOAL, true, true, false).withCriterion("summoned_dragon", SummonedEntityTrigger.Instance.func_203937_a(EntityPredicate.Builder.func_203996_a().func_203998_a(EntityType.ENDER_DRAGON))).register(p_accept_1_, "end/respawn_dragon");
      Advancement advancement4 = Advancement.Builder.builder().withParent(advancement2).withDisplay(Blocks.PURPUR_BLOCK, new TextComponentTranslation("advancements.end.find_end_city.title"), new TextComponentTranslation("advancements.end.find_end_city.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).withCriterion("in_city", PositionTrigger.Instance.func_203932_a(LocationPredicate.func_204007_a("EndCity"))).register(p_accept_1_, "end/find_end_city");
      Advancement advancement5 = Advancement.Builder.builder().withParent(advancement1).withDisplay(Items.DRAGON_BREATH, new TextComponentTranslation("advancements.end.dragon_breath.title"), new TextComponentTranslation("advancements.end.dragon_breath.description"), (ResourceLocation)null, FrameType.GOAL, true, true, false).withCriterion("dragon_breath", InventoryChangeTrigger.Instance.func_203922_a(Items.DRAGON_BREATH)).register(p_accept_1_, "end/dragon_breath");
      Advancement advancement6 = Advancement.Builder.builder().withParent(advancement4).withDisplay(Items.SHULKER_SHELL, new TextComponentTranslation("advancements.end.levitate.title"), new TextComponentTranslation("advancements.end.levitate.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).withRewards(AdvancementRewards.Builder.experience(50)).withCriterion("levitated", LevitationTrigger.Instance.func_203930_a(DistancePredicate.func_203993_b(MinMaxBounds.FloatBound.atLeast(50.0F)))).register(p_accept_1_, "end/levitate");
      Advancement advancement7 = Advancement.Builder.builder().withParent(advancement4).withDisplay(Items.ELYTRA, new TextComponentTranslation("advancements.end.elytra.title"), new TextComponentTranslation("advancements.end.elytra.description"), (ResourceLocation)null, FrameType.GOAL, true, true, false).withCriterion("elytra", InventoryChangeTrigger.Instance.func_203922_a(Items.ELYTRA)).register(p_accept_1_, "end/elytra");
      Advancement advancement8 = Advancement.Builder.builder().withParent(advancement1).withDisplay(Blocks.DRAGON_EGG, new TextComponentTranslation("advancements.end.dragon_egg.title"), new TextComponentTranslation("advancements.end.dragon_egg.description"), (ResourceLocation)null, FrameType.GOAL, true, true, false).withCriterion("dragon_egg", InventoryChangeTrigger.Instance.func_203922_a(Blocks.DRAGON_EGG)).register(p_accept_1_, "end/dragon_egg");
   }
}