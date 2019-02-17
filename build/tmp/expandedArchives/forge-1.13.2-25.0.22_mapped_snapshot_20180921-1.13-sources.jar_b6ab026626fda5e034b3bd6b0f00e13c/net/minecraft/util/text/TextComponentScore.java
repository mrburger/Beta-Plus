package net.minecraft.util.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.command.arguments.EntitySelectorParser;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtils;

public class TextComponentScore extends TextComponentBase {
   private final String name;
   @Nullable
   private final EntitySelector selector;
   private final String objective;
   /** The value displayed instead of the real score (may be null) */
   private String value = "";

   public TextComponentScore(String nameIn, String objectiveIn) {
      this.name = nameIn;
      this.objective = objectiveIn;
      EntitySelector entityselector = null;

      try {
         EntitySelectorParser entityselectorparser = new EntitySelectorParser(new StringReader(nameIn));
         entityselector = entityselectorparser.parse();
      } catch (CommandSyntaxException var5) {
         ;
      }

      this.selector = entityselector;
   }

   /**
    * Gets the name of the entity who owns this score.
    */
   public String getName() {
      return this.name;
   }

   @Nullable
   public EntitySelector getSelector() {
      return this.selector;
   }

   /**
    * Gets the name of the objective for this score.
    */
   public String getObjective() {
      return this.objective;
   }

   /**
    * Sets the value that is displayed for the score. Generally, you do not want to call this as the score is resolved
    * automatically. (If you want to manually set text, use a {@link TextComponentString})
    */
   public void setValue(String valueIn) {
      this.value = valueIn;
   }

   /**
    * Gets the raw content of this component (but not its sibling components), without any formatting codes. For
    * example, this is the raw text in a {@link TextComponentString}, but it's the translated text for a {@link
    * TextComponentTranslation} and it's the score value for a {@link TextComponentScore}.
    */
   public String getUnformattedComponentText() {
      return this.value;
   }

   public void resolve(CommandSource p_197665_1_) {
      MinecraftServer minecraftserver = p_197665_1_.getServer();
      if (minecraftserver != null && minecraftserver.isAnvilFileSet() && StringUtils.isNullOrEmpty(this.value)) {
         Scoreboard scoreboard = minecraftserver.getWorldScoreboard();
         ScoreObjective scoreobjective = scoreboard.getObjective(this.objective);
         if (scoreboard.entityHasObjective(this.name, scoreobjective)) {
            Score score = scoreboard.getOrCreateScore(this.name, scoreobjective);
            this.setValue(String.format("%d", score.getScorePoints()));
         } else {
            this.value = "";
         }
      }

   }

   /**
    * Creates a copy of this component.  Almost a deep copy, except the style is shallow-copied.
    */
   public TextComponentScore createCopy() {
      TextComponentScore textcomponentscore = new TextComponentScore(this.name, this.objective);
      textcomponentscore.setValue(this.value);
      return textcomponentscore;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof TextComponentScore)) {
         return false;
      } else {
         TextComponentScore textcomponentscore = (TextComponentScore)p_equals_1_;
         return this.name.equals(textcomponentscore.name) && this.objective.equals(textcomponentscore.objective) && super.equals(p_equals_1_);
      }
   }

   public String toString() {
      return "ScoreComponent{name='" + this.name + '\'' + "objective='" + this.objective + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
   }
}