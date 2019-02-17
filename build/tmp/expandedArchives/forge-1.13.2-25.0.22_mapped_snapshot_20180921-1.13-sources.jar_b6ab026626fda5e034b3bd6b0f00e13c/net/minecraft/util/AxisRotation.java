package net.minecraft.util;

public enum AxisRotation {
   NONE {
      public int getCoordinate(int x, int y, int z, EnumFacing.Axis axis) {
         return axis.getCoordinate(x, y, z);
      }

      public EnumFacing.Axis rotate(EnumFacing.Axis axisIn) {
         return axisIn;
      }

      public AxisRotation reverse() {
         return this;
      }
   },
   FORWARD {
      public int getCoordinate(int x, int y, int z, EnumFacing.Axis axis) {
         return axis.getCoordinate(z, x, y);
      }

      public EnumFacing.Axis rotate(EnumFacing.Axis axisIn) {
         return AXES[Math.floorMod(axisIn.ordinal() + 1, 3)];
      }

      public AxisRotation reverse() {
         return BACKWARD;
      }
   },
   BACKWARD {
      public int getCoordinate(int x, int y, int z, EnumFacing.Axis axis) {
         return axis.getCoordinate(y, z, x);
      }

      public EnumFacing.Axis rotate(EnumFacing.Axis axisIn) {
         return AXES[Math.floorMod(axisIn.ordinal() - 1, 3)];
      }

      public AxisRotation reverse() {
         return FORWARD;
      }
   };

   public static final EnumFacing.Axis[] AXES = EnumFacing.Axis.values();
   public static final AxisRotation[] AXIS_ROTATIONS = values();

   private AxisRotation() {
   }

   public abstract int getCoordinate(int x, int y, int z, EnumFacing.Axis axis);

   public abstract EnumFacing.Axis rotate(EnumFacing.Axis axisIn);

   public abstract AxisRotation reverse();

   public static AxisRotation from(EnumFacing.Axis axis1, EnumFacing.Axis axis2) {
      return AXIS_ROTATIONS[Math.floorMod(axis2.ordinal() - axis1.ordinal(), 3)];
   }
}