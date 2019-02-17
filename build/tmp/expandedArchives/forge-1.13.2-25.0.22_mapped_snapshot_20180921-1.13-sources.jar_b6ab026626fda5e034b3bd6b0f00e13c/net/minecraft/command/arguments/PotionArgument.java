package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextComponentTranslation;

public class PotionArgument implements ArgumentType<Potion> {
   private static final Collection<String> EXAMPLES = Arrays.asList("spooky", "effect");
   public static final DynamicCommandExceptionType field_197128_a = new DynamicCommandExceptionType((p_208663_0_) -> {
      return new TextComponentTranslation("effect.effectNotFound", p_208663_0_);
   });

   public static PotionArgument mobEffect() {
      return new PotionArgument();
   }

   public static Potion getMobEffect(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
      return context.getArgument(name, Potion.class);
   }

   public Potion parse(StringReader p_parse_1_) throws CommandSyntaxException {
      ResourceLocation resourcelocation = ResourceLocation.read(p_parse_1_);
      Potion potion = IRegistry.field_212631_t.func_212608_b(resourcelocation);
      if (potion == null) {
         throw field_197128_a.create(resourcelocation);
      } else {
         return potion;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      return ISuggestionProvider.suggestIterable(IRegistry.field_212631_t.getKeys(), p_listSuggestions_2_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}