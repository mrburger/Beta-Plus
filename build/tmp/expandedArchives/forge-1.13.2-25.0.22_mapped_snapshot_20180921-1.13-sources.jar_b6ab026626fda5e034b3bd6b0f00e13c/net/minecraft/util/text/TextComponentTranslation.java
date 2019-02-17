package net.minecraft.util.text;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.minecraft.util.text.translation.LanguageMap;

public class TextComponentTranslation extends TextComponentBase {
   private static final LanguageMap field_200526_d = new LanguageMap();
   private static final LanguageMap field_200527_e = LanguageMap.getInstance();
   private final String key;
   private final Object[] formatArgs;
   private final Object syncLock = new Object();
   private long lastTranslationUpdateTimeInMilliseconds = -1L;
   /**
    * The discrete elements that make up this component.  For example, this would be ["Prefix, ", "FirstArg",
    * "SecondArg", " again ", "SecondArg", " and ", "FirstArg", " lastly ", "ThirdArg", " and also ", "FirstArg", "
    * again!"] for "translation.test.complex" (see en-US.lang)
    */
   @VisibleForTesting
   List<ITextComponent> children = Lists.newArrayList();
   public static final Pattern STRING_VARIABLE_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

   public TextComponentTranslation(String translationKey, Object... args) {
      this.key = translationKey;
      this.formatArgs = args;

      for(int i = 0; i < args.length; ++i) {
         Object object = args[i];
         if (object instanceof ITextComponent) {
            ITextComponent itextcomponent = ((ITextComponent)object).func_212638_h();
            this.formatArgs[i] = itextcomponent;
            itextcomponent.getStyle().setParentStyle(this.getStyle());
         } else if (object == null) {
            this.formatArgs[i] = "null";
         }
      }

   }

   /**
    * Ensures that all of the children are up to date with the most recent translation mapping.
    */
   @VisibleForTesting
   synchronized void ensureInitialized() {
      synchronized(this.syncLock) {
         long i = field_200527_e.getLastUpdateTimeInMilliseconds();
         if (i == this.lastTranslationUpdateTimeInMilliseconds) {
            return;
         }

         this.lastTranslationUpdateTimeInMilliseconds = i;
         this.children.clear();
      }

      try {
         this.initializeFromFormat(field_200527_e.translateKey(this.key));
      } catch (TextComponentTranslationFormatException textcomponenttranslationformatexception) {
         this.children.clear();

         try {
            this.initializeFromFormat(field_200526_d.translateKey(this.key));
         } catch (TextComponentTranslationFormatException var5) {
            throw textcomponenttranslationformatexception;
         }
      }

   }

   /**
    * Initializes the content of this component, substituting in variables.
    */
   protected void initializeFromFormat(String format) {
      Matcher matcher = STRING_VARIABLE_PATTERN.matcher(format);

      try {
         int i = 0;

         int j;
         int l;
         for(j = 0; matcher.find(j); j = l) {
            int k = matcher.start();
            l = matcher.end();
            if (k > j) {
               ITextComponent itextcomponent = new TextComponentString(String.format(format.substring(j, k)));
               itextcomponent.getStyle().setParentStyle(this.getStyle());
               this.children.add(itextcomponent);
            }

            String s2 = matcher.group(2);
            String s = format.substring(k, l);
            if ("%".equals(s2) && "%%".equals(s)) {
               ITextComponent itextcomponent2 = new TextComponentString("%");
               itextcomponent2.getStyle().setParentStyle(this.getStyle());
               this.children.add(itextcomponent2);
            } else {
               if (!"s".equals(s2)) {
                  throw new TextComponentTranslationFormatException(this, "Unsupported format: '" + s + "'");
               }

               String s1 = matcher.group(1);
               int i1 = s1 != null ? Integer.parseInt(s1) - 1 : i++;
               if (i1 < this.formatArgs.length) {
                  this.children.add(this.getFormatArgumentAsComponent(i1));
               }
            }
         }

         if (j == 0) {
            // if we failed to match above, lets try the messageformat handler instead.
            j = net.minecraftforge.fml.TextComponentMessageFormatHandler.handle(this, this.children, this.formatArgs, format);
         }
         if (j < format.length()) {
            ITextComponent itextcomponent1 = new TextComponentString(String.format(format.substring(j)));
            itextcomponent1.getStyle().setParentStyle(this.getStyle());
            this.children.add(itextcomponent1);
         }

      } catch (IllegalFormatException illegalformatexception) {
         throw new TextComponentTranslationFormatException(this, illegalformatexception);
      }
   }

   private ITextComponent getFormatArgumentAsComponent(int index) {
      if (index >= this.formatArgs.length) {
         throw new TextComponentTranslationFormatException(this, index);
      } else {
         Object object = this.formatArgs[index];
         ITextComponent itextcomponent;
         if (object instanceof ITextComponent) {
            itextcomponent = (ITextComponent)object;
         } else {
            itextcomponent = new TextComponentString(object == null ? "null" : object.toString());
            itextcomponent.getStyle().setParentStyle(this.getStyle());
         }

         return itextcomponent;
      }
   }

   /**
    * Sets the style of this component and updates the parent style of all of the sibling components.
    */
   public ITextComponent setStyle(Style style) {
      super.setStyle(style);

      for(Object object : this.formatArgs) {
         if (object instanceof ITextComponent) {
            ((ITextComponent)object).getStyle().setParentStyle(this.getStyle());
         }
      }

      if (this.lastTranslationUpdateTimeInMilliseconds > -1L) {
         for(ITextComponent itextcomponent : this.children) {
            itextcomponent.getStyle().setParentStyle(style);
         }
      }

      return this;
   }

   public Stream<ITextComponent> func_212640_c() {
      this.ensureInitialized();
      return Streams.<ITextComponent>concat(this.children.stream(), this.siblings.stream()).flatMap(ITextComponent::func_212640_c);
   }

   /**
    * Gets the raw content of this component (but not its sibling components), without any formatting codes. For
    * example, this is the raw text in a {@link TextComponentString}, but it's the translated text for a {@link
    * TextComponentTranslation} and it's the score value for a {@link TextComponentScore}.
    */
   public String getUnformattedComponentText() {
      this.ensureInitialized();
      StringBuilder stringbuilder = new StringBuilder();

      for(ITextComponent itextcomponent : this.children) {
         stringbuilder.append(itextcomponent.getUnformattedComponentText());
      }

      return stringbuilder.toString();
   }

   /**
    * Creates a copy of this component.  Almost a deep copy, except the style is shallow-copied.
    */
   public TextComponentTranslation createCopy() {
      Object[] aobject = new Object[this.formatArgs.length];

      for(int i = 0; i < this.formatArgs.length; ++i) {
         if (this.formatArgs[i] instanceof ITextComponent) {
            aobject[i] = ((ITextComponent)this.formatArgs[i]).func_212638_h();
         } else {
            aobject[i] = this.formatArgs[i];
         }
      }

      return new TextComponentTranslation(this.key, aobject);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof TextComponentTranslation)) {
         return false;
      } else {
         TextComponentTranslation textcomponenttranslation = (TextComponentTranslation)p_equals_1_;
         return Arrays.equals(this.formatArgs, textcomponenttranslation.formatArgs) && this.key.equals(textcomponenttranslation.key) && super.equals(p_equals_1_);
      }
   }

   public int hashCode() {
      int i = super.hashCode();
      i = 31 * i + this.key.hashCode();
      i = 31 * i + Arrays.hashCode(this.formatArgs);
      return i;
   }

   public String toString() {
      return "TranslatableComponent{key='" + this.key + '\'' + ", args=" + Arrays.toString(this.formatArgs) + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
   }

   /**
    * Gets the key used to translate this component.
    */
   public String getKey() {
      return this.key;
   }

   /**
    * Gets the object array that is used to translate the key.
    */
   public Object[] getFormatArgs() {
      return this.formatArgs;
   }
}