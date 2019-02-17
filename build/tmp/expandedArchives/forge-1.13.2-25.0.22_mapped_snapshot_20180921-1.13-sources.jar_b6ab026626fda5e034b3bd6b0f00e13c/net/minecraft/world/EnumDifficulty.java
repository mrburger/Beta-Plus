package net.minecraft.world;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public enum EnumDifficulty {
   PEACEFUL(0, "peaceful"),
   EASY(1, "easy"),
   NORMAL(2, "normal"),
   HARD(3, "hard");

   private static final EnumDifficulty[] ID_MAPPING = Arrays.stream(values()).sorted(Comparator.comparingInt(EnumDifficulty::getId)).toArray((p_199928_0_) -> {
      return new EnumDifficulty[p_199928_0_];
   });
   private final int id;
   private final String translationKey;

   private EnumDifficulty(int difficultyIdIn, String difficultyResourceKeyIn) {
      this.id = difficultyIdIn;
      this.translationKey = difficultyResourceKeyIn;
   }

   public int getId() {
      return this.id;
   }

   public ITextComponent getDisplayName() {
      return new TextComponentTranslation("options.difficulty." + this.translationKey);
   }

   public static EnumDifficulty byId(int id) {
      return ID_MAPPING[id % ID_MAPPING.length];
   }

   public String getTranslationKey() {
      return this.translationKey;
   }
}