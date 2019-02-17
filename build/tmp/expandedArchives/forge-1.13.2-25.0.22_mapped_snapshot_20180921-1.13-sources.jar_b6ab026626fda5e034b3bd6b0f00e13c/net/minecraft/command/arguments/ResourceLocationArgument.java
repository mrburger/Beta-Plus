package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.advancements.Advancement;
import net.minecraft.command.CommandSource;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

public class ResourceLocationArgument implements ArgumentType<ResourceLocation> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
   public static final DynamicCommandExceptionType field_197199_a = new DynamicCommandExceptionType((p_208676_0_) -> {
      return new TextComponentTranslation("argument.id.unknown", p_208676_0_);
   });
   public static final DynamicCommandExceptionType field_197200_b = new DynamicCommandExceptionType((p_208677_0_) -> {
      return new TextComponentTranslation("advancement.advancementNotFound", p_208677_0_);
   });
   public static final DynamicCommandExceptionType field_197202_d = new DynamicCommandExceptionType((p_208674_0_) -> {
      return new TextComponentTranslation("recipe.notFound", p_208674_0_);
   });

   public static ResourceLocationArgument resourceLocation() {
      return new ResourceLocationArgument();
   }

   public static Advancement getAdvancement(CommandContext<CommandSource> context, String p_197198_1_) throws CommandSyntaxException {
      ResourceLocation resourcelocation = context.getArgument(p_197198_1_, ResourceLocation.class);
      Advancement advancement = context.getSource().getServer().getAdvancementManager().getAdvancement(resourcelocation);
      if (advancement == null) {
         throw field_197200_b.create(resourcelocation);
      } else {
         return advancement;
      }
   }

   public static IRecipe getRecipe(CommandContext<CommandSource> context, String p_197194_1_) throws CommandSyntaxException {
      ResourceLocation resourcelocation = context.getArgument(p_197194_1_, ResourceLocation.class);
      IRecipe irecipe = context.getSource().getServer().getRecipeManager().getRecipe(resourcelocation);
      if (irecipe == null) {
         throw field_197202_d.create(resourcelocation);
      } else {
         return irecipe;
      }
   }

   public static ResourceLocation getResourceLocation(CommandContext<CommandSource> context, String p_197195_1_) {
      return context.getArgument(p_197195_1_, ResourceLocation.class);
   }

   public ResourceLocation parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return ResourceLocation.read(p_parse_1_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}