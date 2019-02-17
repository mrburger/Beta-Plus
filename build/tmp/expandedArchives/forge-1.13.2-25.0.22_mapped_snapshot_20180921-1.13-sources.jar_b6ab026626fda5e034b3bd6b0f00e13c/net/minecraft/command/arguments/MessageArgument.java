package net.minecraft.command.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class MessageArgument implements ArgumentType<MessageArgument.Message> {
   private static final Collection<String> EXAMPLES = Arrays.asList("Hello world!", "foo", "@e", "Hello @p :)");

   public static MessageArgument message() {
      return new MessageArgument();
   }

   public static ITextComponent getMessage(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
      return context.getArgument(name, MessageArgument.Message.class).func_201312_a(context.getSource(), context.getSource().hasPermissionLevel(2));
   }

   public MessageArgument.Message parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return MessageArgument.Message.func_197113_a(p_parse_1_, true);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static class Message {
      private final String field_197114_a;
      private final MessageArgument.Part[] field_197115_b;

      public Message(String p_i48021_1_, MessageArgument.Part[] p_i48021_2_) {
         this.field_197114_a = p_i48021_1_;
         this.field_197115_b = p_i48021_2_;
      }

      public ITextComponent func_201312_a(CommandSource p_201312_1_, boolean p_201312_2_) throws CommandSyntaxException {
         if (this.field_197115_b.length != 0 && p_201312_2_) {
            ITextComponent itextcomponent = new TextComponentString(this.field_197114_a.substring(0, this.field_197115_b[0].func_197117_a()));
            int i = this.field_197115_b[0].func_197117_a();

            for(MessageArgument.Part messageargument$part : this.field_197115_b) {
               ITextComponent itextcomponent1 = messageargument$part.func_197116_a(p_201312_1_);
               if (i < messageargument$part.func_197117_a()) {
                  itextcomponent.appendText(this.field_197114_a.substring(i, messageargument$part.func_197117_a()));
               }

               if (itextcomponent1 != null) {
                  itextcomponent.appendSibling(itextcomponent1);
               }

               i = messageargument$part.func_197118_b();
            }

            if (i < this.field_197114_a.length()) {
               itextcomponent.appendText(this.field_197114_a.substring(i, this.field_197114_a.length()));
            }

            return itextcomponent;
         } else {
            return new TextComponentString(this.field_197114_a);
         }
      }

      public static MessageArgument.Message func_197113_a(StringReader p_197113_0_, boolean p_197113_1_) throws CommandSyntaxException {
         String s = p_197113_0_.getString().substring(p_197113_0_.getCursor(), p_197113_0_.getTotalLength());
         if (!p_197113_1_) {
            p_197113_0_.setCursor(p_197113_0_.getTotalLength());
            return new MessageArgument.Message(s, new MessageArgument.Part[0]);
         } else {
            List<MessageArgument.Part> list = Lists.newArrayList();
            int i = p_197113_0_.getCursor();

            while(true) {
               int j;
               EntitySelector entityselector;
               while(true) {
                  if (!p_197113_0_.canRead()) {
                     return new MessageArgument.Message(s, list.toArray(new MessageArgument.Part[list.size()]));
                  }

                  if (p_197113_0_.peek() == '@') {
                     j = p_197113_0_.getCursor();

                     try {
                        EntitySelectorParser entityselectorparser = new EntitySelectorParser(p_197113_0_);
                        entityselector = entityselectorparser.parse();
                        break;
                     } catch (CommandSyntaxException commandsyntaxexception) {
                        if (commandsyntaxexception.getType() != EntitySelectorParser.SELECTOR_TYPE_MISSING && commandsyntaxexception.getType() != EntitySelectorParser.UNKNOWN_SELECTOR_TYPE) {
                           throw commandsyntaxexception;
                        }

                        p_197113_0_.setCursor(j + 1);
                     }
                  } else {
                     p_197113_0_.skip();
                  }
               }

               list.add(new MessageArgument.Part(j - i, p_197113_0_.getCursor() - i, entityselector));
            }
         }
      }
   }

   public static class Part {
      private final int field_197119_a;
      private final int field_197120_b;
      private final EntitySelector field_197121_c;

      public Part(int p_i48020_1_, int p_i48020_2_, EntitySelector p_i48020_3_) {
         this.field_197119_a = p_i48020_1_;
         this.field_197120_b = p_i48020_2_;
         this.field_197121_c = p_i48020_3_;
      }

      public int func_197117_a() {
         return this.field_197119_a;
      }

      public int func_197118_b() {
         return this.field_197120_b;
      }

      @Nullable
      public ITextComponent func_197116_a(CommandSource p_197116_1_) throws CommandSyntaxException {
         return EntitySelector.joinNames(this.field_197121_c.select(p_197116_1_));
      }
   }
}