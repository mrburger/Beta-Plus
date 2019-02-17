package net.minecraft.world.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;

public class MapFrame {
   private final BlockPos field_212771_a;
   private int field_212772_b;
   private int field_212773_c;

   public MapFrame(BlockPos p_i49855_1_, int p_i49855_2_, int p_i49855_3_) {
      this.field_212771_a = p_i49855_1_;
      this.field_212772_b = p_i49855_2_;
      this.field_212773_c = p_i49855_3_;
   }

   public static MapFrame func_212765_a(NBTTagCompound p_212765_0_) {
      BlockPos blockpos = NBTUtil.readBlockPos(p_212765_0_.getCompound("Pos"));
      int i = p_212765_0_.getInt("Rotation");
      int j = p_212765_0_.getInt("EntityId");
      return new MapFrame(blockpos, i, j);
   }

   public NBTTagCompound func_212770_a() {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.setTag("Pos", NBTUtil.writeBlockPos(this.field_212771_a));
      nbttagcompound.setInt("Rotation", this.field_212772_b);
      nbttagcompound.setInt("EntityId", this.field_212773_c);
      return nbttagcompound;
   }

   public BlockPos func_212764_b() {
      return this.field_212771_a;
   }

   public int func_212768_c() {
      return this.field_212772_b;
   }

   public int func_212769_d() {
      return this.field_212773_c;
   }

   public String func_212767_e() {
      return func_212766_a(this.field_212771_a);
   }

   public static String func_212766_a(BlockPos p_212766_0_) {
      return "frame-" + p_212766_0_.getX() + "," + p_212766_0_.getY() + "," + p_212766_0_.getZ();
   }
}