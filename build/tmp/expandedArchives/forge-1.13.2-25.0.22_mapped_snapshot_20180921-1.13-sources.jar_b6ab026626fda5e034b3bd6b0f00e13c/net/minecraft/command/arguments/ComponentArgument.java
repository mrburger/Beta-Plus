package net.minecraft.command.arguments;

import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class ComponentArgument implements ArgumentType<ITextComponent> {
   private static final Collection<String> EXAMPLES = Arrays.asList("\"hello world\"", "\"\"", "\"{\"text\":\"hello world\"}", "[\"\"]");
   public static final DynamicCommandExceptionType field_197070_a = new DynamicCommandExceptionType((p_208660_0_) -> {
      return new TextComponentTranslation("argument.component.invalid", p_208660_0_);
   });

   public static ITextComponent getComponent(CommandContext<CommandSource> context, String name) {
      return context.getArgument(name, ITextComponent.class);
   }

   public static ComponentArgument component() {
      return new ComponentArgument();
   }

   public ITextComponent parse(StringReader p_parse_1_) throws CommandSyntaxException {
      try {
         ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(p_parse_1_);
         if (itextcomponent == null) {
            throw field_197070_a.createWithContext(p_parse_1_, "empty");
         } else {
            return itextcomponent;
         }
      } catch (JsonParseException jsonparseexception) {
         String s = jsonparseexception.getCause() != null ? jsonparseexception.getCause().getMessage() : jsonparseexception.getMessage();
         throw field_197070_a.createWithContext(p_parse_1_, s);
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}