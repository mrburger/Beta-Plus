package net.minecraft.command.arguments.serializers;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.util.ResourceLocation;

public class BrigadierSerializers {
   public static void registerArgumentTypes() {
      ArgumentTypes.register(new ResourceLocation("brigadier:bool"), BoolArgumentType.class, new ArgumentSerializer<>(BoolArgumentType::bool));
      ArgumentTypes.register(new ResourceLocation("brigadier:float"), FloatArgumentType.class, new FloatSerializer());
      ArgumentTypes.register(new ResourceLocation("brigadier:double"), DoubleArgumentType.class, new DoubleSerializer());
      ArgumentTypes.register(new ResourceLocation("brigadier:integer"), IntegerArgumentType.class, new IntSerializer());
      ArgumentTypes.register(new ResourceLocation("brigadier:string"), StringArgumentType.class, new StringSerializer());
   }

   public static byte minMaxFlags(boolean min, boolean max) {
      byte b0 = 0;
      if (min) {
         b0 = (byte)(b0 | 1);
      }

      if (max) {
         b0 = (byte)(b0 | 2);
      }

      return b0;
   }

   public static boolean hasMin(byte p_197510_0_) {
      return (p_197510_0_ & 1) != 0;
   }

   public static boolean hasMax(byte p_197509_0_) {
      return (p_197509_0_ & 2) != 0;
   }
}