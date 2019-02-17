package net.minecraft.command.arguments;

import com.google.common.collect.Maps;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextComponentTranslation;

public class SuggestionProviders {
   private static final Map<ResourceLocation, SuggestionProvider<ISuggestionProvider>> REGISTRY = Maps.newHashMap();
   private static final ResourceLocation ASK_SERVER_ID = new ResourceLocation("minecraft:ask_server");
   public static final SuggestionProvider<ISuggestionProvider> ASK_SERVER = register(ASK_SERVER_ID, (p_197500_0_, p_197500_1_) -> {
      return p_197500_0_.getSource().getSuggestionsFromServer(p_197500_0_, p_197500_1_);
   });
   public static final SuggestionProvider<CommandSource> ALL_RECIPES = register(new ResourceLocation("minecraft:all_recipes"), (p_197501_0_, p_197501_1_) -> {
      return ISuggestionProvider.suggestIterable(p_197501_0_.getSource().getRecipeResourceLocations(), p_197501_1_);
   });
   public static final SuggestionProvider<CommandSource> AVAILABLE_SOUNDS = register(new ResourceLocation("minecraft:available_sounds"), (p_197495_0_, p_197495_1_) -> {
      return ISuggestionProvider.suggestIterable(p_197495_0_.getSource().getSoundResourceLocations(), p_197495_1_);
   });
   public static final SuggestionProvider<CommandSource> SUMMONABLE_ENTITIES = register(new ResourceLocation("minecraft:summonable_entities"), (p_201210_0_, p_201210_1_) -> {
      return ISuggestionProvider.func_201725_a(IRegistry.field_212629_r.stream().filter(EntityType::isSummonable), p_201210_1_, EntityType::getId, (p_201209_0_) -> {
         return new TextComponentTranslation(Util.makeTranslationKey("entity", EntityType.getId(p_201209_0_)));
      });
   });

   public static <S extends ISuggestionProvider> SuggestionProvider<S> register(ResourceLocation p_197494_0_, SuggestionProvider<ISuggestionProvider> p_197494_1_) {
      if (REGISTRY.containsKey(p_197494_0_)) {
         throw new IllegalArgumentException("A command suggestion provider is already registered with the name " + p_197494_0_);
      } else {
         REGISTRY.put(p_197494_0_, p_197494_1_);
         return (SuggestionProvider<S>)new SuggestionProviders.Wrapper(p_197494_0_, p_197494_1_);
      }
   }

   public static SuggestionProvider<ISuggestionProvider> get(ResourceLocation p_197498_0_) {
      return REGISTRY.getOrDefault(p_197498_0_, ASK_SERVER);
   }

   public static ResourceLocation getId(SuggestionProvider<ISuggestionProvider> p_197497_0_) {
      return p_197497_0_ instanceof SuggestionProviders.Wrapper ? ((SuggestionProviders.Wrapper)p_197497_0_).id : ASK_SERVER_ID;
   }

   public static SuggestionProvider<ISuggestionProvider> func_197496_b(SuggestionProvider<ISuggestionProvider> p_197496_0_) {
      return p_197496_0_ instanceof SuggestionProviders.Wrapper ? p_197496_0_ : ASK_SERVER;
   }

   public static class Wrapper implements SuggestionProvider<ISuggestionProvider> {
      private final SuggestionProvider<ISuggestionProvider> provider;
      private final ResourceLocation id;

      public Wrapper(ResourceLocation p_i47984_1_, SuggestionProvider<ISuggestionProvider> p_i47984_2_) {
         this.provider = p_i47984_2_;
         this.id = p_i47984_1_;
      }

      public CompletableFuture<Suggestions> getSuggestions(CommandContext<ISuggestionProvider> p_getSuggestions_1_, SuggestionsBuilder p_getSuggestions_2_) throws CommandSyntaxException {
         return this.provider.getSuggestions(p_getSuggestions_1_, p_getSuggestions_2_);
      }
   }
}