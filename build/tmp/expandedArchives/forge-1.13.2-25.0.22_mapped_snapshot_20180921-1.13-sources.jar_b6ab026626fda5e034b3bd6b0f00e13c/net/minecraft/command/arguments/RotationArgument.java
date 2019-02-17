package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.TextComponentTranslation;

public class RotationArgument implements ArgumentType<ILocationArgument> {
   private static final Collection<String> EXAMPLES = Arrays.asList("0 0", "~ ~", "~-5 ~5");
   public static final SimpleCommandExceptionType field_197290_a = new SimpleCommandExceptionType(new TextComponentTranslation("argument.rotation.incomplete"));

   public static RotationArgument rotation() {
      return new RotationArgument();
   }

   public static ILocationArgument getRotation(CommandContext<CommandSource> context, String name) {
      return context.getArgument(name, ILocationArgument.class);
   }

   public ILocationArgument parse(StringReader p_parse_1_) throws CommandSyntaxException {
      int i = p_parse_1_.getCursor();
      if (!p_parse_1_.canRead()) {
         throw field_197290_a.createWithContext(p_parse_1_);
      } else {
         LocationPart locationpart = LocationPart.func_197308_a(p_parse_1_, false);
         if (p_parse_1_.canRead() && p_parse_1_.peek() == ' ') {
            p_parse_1_.skip();
            LocationPart locationpart1 = LocationPart.func_197308_a(p_parse_1_, false);
            return new LocationInput(locationpart1, locationpart, new LocationPart(true, 0.0D));
         } else {
            p_parse_1_.setCursor(i);
            throw field_197290_a.createWithContext(p_parse_1_);
         }
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}