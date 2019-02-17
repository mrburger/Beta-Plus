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
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextComponentTranslation;

public class ParticleArgument implements ArgumentType<IParticleData> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "particle with options");
   public static final DynamicCommandExceptionType field_197191_a = new DynamicCommandExceptionType((p_208673_0_) -> {
      return new TextComponentTranslation("particle.notFound", p_208673_0_);
   });

   public static ParticleArgument particle() {
      return new ParticleArgument();
   }

   public static IParticleData getParticle(CommandContext<CommandSource> context, String name) {
      return context.getArgument(name, IParticleData.class);
   }

   public IParticleData parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return func_197189_a(p_parse_1_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static IParticleData func_197189_a(StringReader p_197189_0_) throws CommandSyntaxException {
      ResourceLocation resourcelocation = ResourceLocation.read(p_197189_0_);
      ParticleType<?> particletype = IRegistry.field_212632_u.func_212608_b(resourcelocation);
      if (particletype == null) {
         throw field_197191_a.create(resourcelocation);
      } else {
         return func_199816_a(p_197189_0_, particletype);
      }
   }

   private static <T extends IParticleData> T func_199816_a(StringReader p_199816_0_, ParticleType<T> p_199816_1_) throws CommandSyntaxException {
      return p_199816_1_.getDeserializer().deserialize(p_199816_1_, p_199816_0_);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      return ISuggestionProvider.suggestIterable(IRegistry.field_212632_u.getKeys(), p_listSuggestions_2_);
   }
}