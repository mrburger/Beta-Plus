package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.CommandSource;
import net.minecraft.command.FunctionObject;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

public class FunctionArgument implements ArgumentType<FunctionArgument.IResult> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "#foo");
   private static final DynamicCommandExceptionType field_200023_a = new DynamicCommandExceptionType((p_208691_0_) -> {
      return new TextComponentTranslation("arguments.function.tag.unknown", p_208691_0_);
   });
   private static final DynamicCommandExceptionType field_200024_b = new DynamicCommandExceptionType((p_208694_0_) -> {
      return new TextComponentTranslation("arguments.function.unknown", p_208694_0_);
   });

   public static FunctionArgument function() {
      return new FunctionArgument();
   }

   public FunctionArgument.IResult parse(StringReader p_parse_1_) throws CommandSyntaxException {
      if (p_parse_1_.canRead() && p_parse_1_.peek() == '#') {
         p_parse_1_.skip();
         ResourceLocation resourcelocation1 = ResourceLocation.read(p_parse_1_);
         return (p_200020_1_) -> {
            Tag<FunctionObject> tag = ((CommandSource)p_200020_1_.getSource()).getServer().getFunctionManager().getTagCollection().get(resourcelocation1);
            if (tag == null) {
               throw field_200023_a.create(resourcelocation1.toString());
            } else {
               return tag.getAllElements();
            }
         };
      } else {
         ResourceLocation resourcelocation = ResourceLocation.read(p_parse_1_);
         return (p_200019_1_) -> {
            FunctionObject functionobject = ((CommandSource)p_200019_1_.getSource()).getServer().getFunctionManager().getFunction(resourcelocation);
            if (functionobject == null) {
               throw field_200024_b.create(resourcelocation.toString());
            } else {
               return Collections.singleton(functionobject);
            }
         };
      }
   }

   public static Collection<FunctionObject> getFunctions(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
      return context.getArgument(name, FunctionArgument.IResult.class).create(context);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public interface IResult {
      Collection<FunctionObject> create(CommandContext<CommandSource> p_create_1_) throws CommandSyntaxException;
   }
}