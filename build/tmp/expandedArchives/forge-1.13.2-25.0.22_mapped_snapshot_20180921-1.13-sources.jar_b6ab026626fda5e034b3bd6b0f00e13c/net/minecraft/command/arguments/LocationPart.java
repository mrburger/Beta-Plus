package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.util.text.TextComponentTranslation;

public class LocationPart {
   public static final SimpleCommandExceptionType field_197311_b = new SimpleCommandExceptionType(new TextComponentTranslation("argument.pos.missing.double"));
   public static final SimpleCommandExceptionType field_197312_c = new SimpleCommandExceptionType(new TextComponentTranslation("argument.pos.missing.int"));
   private final boolean relative;
   private final double value;

   public LocationPart(boolean p_i47963_1_, double p_i47963_2_) {
      this.relative = p_i47963_1_;
      this.value = p_i47963_2_;
   }

   public double func_197306_a(double p_197306_1_) {
      return this.relative ? this.value + p_197306_1_ : this.value;
   }

   public static LocationPart func_197308_a(StringReader p_197308_0_, boolean p_197308_1_) throws CommandSyntaxException {
      if (p_197308_0_.canRead() && p_197308_0_.peek() == '^') {
         throw Vec3Argument.field_200149_b.createWithContext(p_197308_0_);
      } else if (!p_197308_0_.canRead()) {
         throw field_197311_b.createWithContext(p_197308_0_);
      } else {
         boolean flag = isRelative(p_197308_0_);
         int i = p_197308_0_.getCursor();
         double d0 = p_197308_0_.canRead() && p_197308_0_.peek() != ' ' ? p_197308_0_.readDouble() : 0.0D;
         String s = p_197308_0_.getString().substring(i, p_197308_0_.getCursor());
         if (flag && s.isEmpty()) {
            return new LocationPart(true, 0.0D);
         } else {
            if (!s.contains(".") && !flag && p_197308_1_) {
               d0 += 0.5D;
            }

            return new LocationPart(flag, d0);
         }
      }
   }

   public static LocationPart func_197307_a(StringReader p_197307_0_) throws CommandSyntaxException {
      if (p_197307_0_.canRead() && p_197307_0_.peek() == '^') {
         throw Vec3Argument.field_200149_b.createWithContext(p_197307_0_);
      } else if (!p_197307_0_.canRead()) {
         throw field_197312_c.createWithContext(p_197307_0_);
      } else {
         boolean flag = isRelative(p_197307_0_);
         double d0;
         if (p_197307_0_.canRead() && p_197307_0_.peek() != ' ') {
            d0 = flag ? p_197307_0_.readDouble() : (double)p_197307_0_.readInt();
         } else {
            d0 = 0.0D;
         }

         return new LocationPart(flag, d0);
      }
   }

   private static boolean isRelative(StringReader p_197309_0_) {
      boolean flag;
      if (p_197309_0_.peek() == '~') {
         flag = true;
         p_197309_0_.skip();
      } else {
         flag = false;
      }

      return flag;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof LocationPart)) {
         return false;
      } else {
         LocationPart locationpart = (LocationPart)p_equals_1_;
         if (this.relative != locationpart.relative) {
            return false;
         } else {
            return Double.compare(locationpart.value, this.value) == 0;
         }
      }
   }

   public int hashCode() {
      int i = this.relative ? 1 : 0;
      long j = Double.doubleToLongBits(this.value);
      i = 31 * i + (int)(j ^ j >>> 32);
      return i;
   }

   public boolean isRelative() {
      return this.relative;
   }
}