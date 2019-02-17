package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import net.minecraft.command.CommandSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextComponentTranslation;

public class SwizzleArgument implements ArgumentType<EnumSet<EnumFacing.Axis>> {
   private static final Collection<String> EXAMPLES = Arrays.asList("xyz", "x");
   private static final SimpleCommandExceptionType field_197294_a = new SimpleCommandExceptionType(new TextComponentTranslation("arguments.swizzle.invalid"));

   public static SwizzleArgument swizzle() {
      return new SwizzleArgument();
   }

   public static EnumSet<EnumFacing.Axis> getSwizzle(CommandContext<CommandSource> p_197291_0_, String p_197291_1_) {
      return p_197291_0_.getArgument(p_197291_1_, EnumSet.class);
   }

   public EnumSet<EnumFacing.Axis> parse(StringReader p_parse_1_) throws CommandSyntaxException {
      EnumSet<EnumFacing.Axis> enumset = EnumSet.noneOf(EnumFacing.Axis.class);

      while(p_parse_1_.canRead() && p_parse_1_.peek() != ' ') {
         char c0 = p_parse_1_.read();
         EnumFacing.Axis enumfacing$axis;
         switch(c0) {
         case 'x':
            enumfacing$axis = EnumFacing.Axis.X;
            break;
         case 'y':
            enumfacing$axis = EnumFacing.Axis.Y;
            break;
         case 'z':
            enumfacing$axis = EnumFacing.Axis.Z;
            break;
         default:
            throw field_197294_a.create();
         }

         if (enumset.contains(enumfacing$axis)) {
            throw field_197294_a.create();
         }

         enumset.add(enumfacing$axis);
      }

      return enumset;
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}