package net.minecraft.util.text;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;

public class TextComponentUtils {
   public static ITextComponent mergeStyles(ITextComponent component, Style styleIn) {
      if (styleIn.isEmpty()) {
         return component;
      } else {
         return component.getStyle().isEmpty() ? component.setStyle(styleIn.createShallowCopy()) : (new TextComponentString("")).appendSibling(component).setStyle(styleIn.createShallowCopy());
      }
   }

   public static ITextComponent updateForEntity(@Nullable CommandSource p_197680_0_, ITextComponent p_197680_1_, @Nullable Entity p_197680_2_) throws CommandSyntaxException {
      ITextComponent itextcomponent;
      if (p_197680_1_ instanceof TextComponentScore && p_197680_0_ != null) {
         TextComponentScore textcomponentscore = (TextComponentScore)p_197680_1_;
         String s;
         if (textcomponentscore.getSelector() != null) {
            List<? extends Entity> list = textcomponentscore.getSelector().select(p_197680_0_);
            if (list.isEmpty()) {
               s = textcomponentscore.getName();
            } else {
               if (list.size() != 1) {
                  throw EntityArgument.TOO_MANY_ENTITIES.create();
               }

               s = list.get(0).getScoreboardName();
            }
         } else {
            s = textcomponentscore.getName();
         }

         String s1 = p_197680_2_ != null && s.equals("*") ? p_197680_2_.getScoreboardName() : s;
         itextcomponent = new TextComponentScore(s1, textcomponentscore.getObjective());
         ((TextComponentScore)itextcomponent).setValue(textcomponentscore.getUnformattedComponentText());
         ((TextComponentScore)itextcomponent).resolve(p_197680_0_);
      } else if (p_197680_1_ instanceof TextComponentSelector && p_197680_0_ != null) {
         itextcomponent = ((TextComponentSelector)p_197680_1_).createNames(p_197680_0_);
      } else if (p_197680_1_ instanceof TextComponentString) {
         itextcomponent = new TextComponentString(((TextComponentString)p_197680_1_).getText());
      } else if (p_197680_1_ instanceof TextComponentKeybind) {
         itextcomponent = new TextComponentKeybind(((TextComponentKeybind)p_197680_1_).getKeybind());
      } else {
         if (!(p_197680_1_ instanceof TextComponentTranslation)) {
            return p_197680_1_;
         }

         Object[] aobject = ((TextComponentTranslation)p_197680_1_).getFormatArgs();

         for(int i = 0; i < aobject.length; ++i) {
            Object object = aobject[i];
            if (object instanceof ITextComponent) {
               aobject[i] = updateForEntity(p_197680_0_, (ITextComponent)object, p_197680_2_);
            }
         }

         itextcomponent = new TextComponentTranslation(((TextComponentTranslation)p_197680_1_).getKey(), aobject);
      }

      for(ITextComponent itextcomponent1 : p_197680_1_.getSiblings()) {
         itextcomponent.appendSibling(updateForEntity(p_197680_0_, itextcomponent1, p_197680_2_));
      }

      return mergeStyles(itextcomponent, p_197680_1_.getStyle());
   }

   public static ITextComponent getDisplayName(GameProfile profile) {
      if (profile.getName() != null) {
         return new TextComponentString(profile.getName());
      } else {
         return profile.getId() != null ? new TextComponentString(profile.getId().toString()) : new TextComponentString("(unknown)");
      }
   }

   public static ITextComponent makeGreenSortedList(Collection<String> collection) {
      return makeSortedList(collection, (p_197681_0_) -> {
         return (new TextComponentString(p_197681_0_)).applyTextStyle(TextFormatting.GREEN);
      });
   }

   public static <T extends Comparable<T>> ITextComponent makeSortedList(Collection<T> collection, Function<T, ITextComponent> toTextComponent) {
      if (collection.isEmpty()) {
         return new TextComponentString("");
      } else if (collection.size() == 1) {
         return toTextComponent.apply(collection.iterator().next());
      } else {
         List<T> list = Lists.newArrayList(collection);
         list.sort(Comparable::compareTo);
         return makeList(collection, toTextComponent);
      }
   }

   public static <T> ITextComponent makeList(Collection<T> collection, Function<T, ITextComponent> toTextComponent) {
      if (collection.isEmpty()) {
         return new TextComponentString("");
      } else if (collection.size() == 1) {
         return toTextComponent.apply(collection.iterator().next());
      } else {
         ITextComponent itextcomponent = new TextComponentString("");
         boolean flag = true;

         for(T t : collection) {
            if (!flag) {
               itextcomponent.appendSibling((new TextComponentString(", ")).applyTextStyle(TextFormatting.GRAY));
            }

            itextcomponent.appendSibling(toTextComponent.apply(t));
            flag = false;
         }

         return itextcomponent;
      }
   }

   public static ITextComponent wrapInSquareBrackets(ITextComponent component) {
      return (new TextComponentString("[")).appendSibling(component).appendText("]");
   }

   public static ITextComponent toTextComponent(Message message) {
      return (ITextComponent)(message instanceof ITextComponent ? (ITextComponent)message : new TextComponentString(message.getString()));
   }
}