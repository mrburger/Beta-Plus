package net.minecraft.command.arguments;

import net.minecraft.command.CommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public interface ILocationArgument {
   Vec3d getPosition(CommandSource p_197281_1_);

   Vec2f getRotation(CommandSource p_197282_1_);

   default BlockPos getBlockPos(CommandSource p_197280_1_) {
      return new BlockPos(this.getPosition(p_197280_1_));
   }

   boolean isXRelative();

   boolean isYRelative();

   boolean isZRelative();
}