package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class LocationInput implements ILocationArgument {
   private final LocationPart x;
   private final LocationPart y;
   private final LocationPart z;

   public LocationInput(LocationPart x, LocationPart y, LocationPart z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public Vec3d getPosition(CommandSource p_197281_1_) {
      Vec3d vec3d = p_197281_1_.getPos();
      return new Vec3d(this.x.func_197306_a(vec3d.x), this.y.func_197306_a(vec3d.y), this.z.func_197306_a(vec3d.z));
   }

   public Vec2f getRotation(CommandSource p_197282_1_) {
      Vec2f vec2f = p_197282_1_.getPitchYaw();
      return new Vec2f((float)this.x.func_197306_a((double)vec2f.x), (float)this.y.func_197306_a((double)vec2f.y));
   }

   public boolean isXRelative() {
      return this.x.isRelative();
   }

   public boolean isYRelative() {
      return this.y.isRelative();
   }

   public boolean isZRelative() {
      return this.z.isRelative();
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof LocationInput)) {
         return false;
      } else {
         LocationInput locationinput = (LocationInput)p_equals_1_;
         if (!this.x.equals(locationinput.x)) {
            return false;
         } else {
            return !this.y.equals(locationinput.y) ? false : this.z.equals(locationinput.z);
         }
      }
   }

   public static LocationInput func_200148_a(StringReader p_200148_0_) throws CommandSyntaxException {
      int i = p_200148_0_.getCursor();
      LocationPart locationpart = LocationPart.func_197307_a(p_200148_0_);
      if (p_200148_0_.canRead() && p_200148_0_.peek() == ' ') {
         p_200148_0_.skip();
         LocationPart locationpart1 = LocationPart.func_197307_a(p_200148_0_);
         if (p_200148_0_.canRead() && p_200148_0_.peek() == ' ') {
            p_200148_0_.skip();
            LocationPart locationpart2 = LocationPart.func_197307_a(p_200148_0_);
            return new LocationInput(locationpart, locationpart1, locationpart2);
         } else {
            p_200148_0_.setCursor(i);
            throw Vec3Argument.field_197304_a.createWithContext(p_200148_0_);
         }
      } else {
         p_200148_0_.setCursor(i);
         throw Vec3Argument.field_197304_a.createWithContext(p_200148_0_);
      }
   }

   public static LocationInput func_200147_a(StringReader p_200147_0_, boolean p_200147_1_) throws CommandSyntaxException {
      int i = p_200147_0_.getCursor();
      LocationPart locationpart = LocationPart.func_197308_a(p_200147_0_, p_200147_1_);
      if (p_200147_0_.canRead() && p_200147_0_.peek() == ' ') {
         p_200147_0_.skip();
         LocationPart locationpart1 = LocationPart.func_197308_a(p_200147_0_, false);
         if (p_200147_0_.canRead() && p_200147_0_.peek() == ' ') {
            p_200147_0_.skip();
            LocationPart locationpart2 = LocationPart.func_197308_a(p_200147_0_, p_200147_1_);
            return new LocationInput(locationpart, locationpart1, locationpart2);
         } else {
            p_200147_0_.setCursor(i);
            throw Vec3Argument.field_197304_a.createWithContext(p_200147_0_);
         }
      } else {
         p_200147_0_.setCursor(i);
         throw Vec3Argument.field_197304_a.createWithContext(p_200147_0_);
      }
   }

   public static LocationInput func_200383_d() {
      return new LocationInput(new LocationPart(true, 0.0D), new LocationPart(true, 0.0D), new LocationPart(true, 0.0D));
   }

   public int hashCode() {
      int i = this.x.hashCode();
      i = 31 * i + this.y.hashCode();
      i = 31 * i + this.z.hashCode();
      return i;
   }
}