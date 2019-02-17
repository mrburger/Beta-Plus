package net.minecraft.resources;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum PackCompatibility {
   TOO_OLD("old"),
   TOO_NEW("new"),
   COMPATIBLE("compatible");

   private final ITextComponent field_198975_d;
   private final ITextComponent field_198976_e;

   private PackCompatibility(String p_i47910_3_) {
      this.field_198975_d = new TextComponentTranslation("resourcePack.incompatible." + p_i47910_3_);
      this.field_198976_e = new TextComponentTranslation("resourcePack.incompatible.confirm." + p_i47910_3_);
   }

   public boolean func_198968_a() {
      return this == COMPATIBLE;
   }

   public static PackCompatibility func_198969_a(int p_198969_0_) {
      if (p_198969_0_ < 4) {
         return TOO_OLD;
      } else {
         return p_198969_0_ > 4 ? TOO_NEW : COMPATIBLE;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent func_198967_b() {
      return this.field_198975_d;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent func_198971_c() {
      return this.field_198976_e;
   }
}