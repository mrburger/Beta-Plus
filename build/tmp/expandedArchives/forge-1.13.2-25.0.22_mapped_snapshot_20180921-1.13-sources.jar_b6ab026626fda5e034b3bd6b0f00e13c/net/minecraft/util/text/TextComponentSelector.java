package net.minecraft.util.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.command.arguments.EntitySelectorParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextComponentSelector extends TextComponentBase {
   private static final Logger LOGGER = LogManager.getLogger();
   /** The selector used to find the matching entities of this text component */
   private final String selector;
   @Nullable
   private final EntitySelector field_197670_d;

   public TextComponentSelector(String selectorIn) {
      this.selector = selectorIn;
      EntitySelector entityselector = null;

      try {
         EntitySelectorParser entityselectorparser = new EntitySelectorParser(new StringReader(selectorIn));
         entityselector = entityselectorparser.parse();
      } catch (CommandSyntaxException commandsyntaxexception) {
         LOGGER.warn("Invalid selector component: {}", selectorIn, commandsyntaxexception.getMessage());
      }

      this.field_197670_d = entityselector;
   }

   /**
    * Gets the selector of this component, in plain text.
    */
   public String getSelector() {
      return this.selector;
   }

   public ITextComponent createNames(CommandSource p_197668_1_) throws CommandSyntaxException {
      return (ITextComponent)(this.field_197670_d == null ? new TextComponentString("") : EntitySelector.joinNames(this.field_197670_d.select(p_197668_1_)));
   }

   /**
    * Gets the raw content of this component (but not its sibling components), without any formatting codes. For
    * example, this is the raw text in a {@link TextComponentString}, but it's the translated text for a {@link
    * TextComponentTranslation} and it's the score value for a {@link TextComponentScore}.
    */
   public String getUnformattedComponentText() {
      return this.selector;
   }

   /**
    * Creates a copy of this component.  Almost a deep copy, except the style is shallow-copied.
    */
   public TextComponentSelector createCopy() {
      return new TextComponentSelector(this.selector);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof TextComponentSelector)) {
         return false;
      } else {
         TextComponentSelector textcomponentselector = (TextComponentSelector)p_equals_1_;
         return this.selector.equals(textcomponentselector.selector) && super.equals(p_equals_1_);
      }
   }

   public String toString() {
      return "SelectorComponent{pattern='" + this.selector + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
   }
}