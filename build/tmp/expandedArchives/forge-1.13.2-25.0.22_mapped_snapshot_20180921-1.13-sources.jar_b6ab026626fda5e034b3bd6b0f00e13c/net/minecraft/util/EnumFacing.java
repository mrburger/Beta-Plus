package net.minecraft.util;

import com.google.common.collect.Iterators;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

public enum EnumFacing implements IStringSerializable {
   DOWN(0, 1, -1, "down", EnumFacing.AxisDirection.NEGATIVE, EnumFacing.Axis.Y, new Vec3i(0, -1, 0)),
   UP(1, 0, -1, "up", EnumFacing.AxisDirection.POSITIVE, EnumFacing.Axis.Y, new Vec3i(0, 1, 0)),
   NORTH(2, 3, 2, "north", EnumFacing.AxisDirection.NEGATIVE, EnumFacing.Axis.Z, new Vec3i(0, 0, -1)),
   SOUTH(3, 2, 0, "south", EnumFacing.AxisDirection.POSITIVE, EnumFacing.Axis.Z, new Vec3i(0, 0, 1)),
   WEST(4, 5, 1, "west", EnumFacing.AxisDirection.NEGATIVE, EnumFacing.Axis.X, new Vec3i(-1, 0, 0)),
   EAST(5, 4, 3, "east", EnumFacing.AxisDirection.POSITIVE, EnumFacing.Axis.X, new Vec3i(1, 0, 0));

   /** Ordering index for D-U-N-S-W-E */
   private final int index;
   /** Index of the opposite Facing in the VALUES array */
   private final int opposite;
   /** Ordering index for the HORIZONTALS field (S-W-N-E) */
   private final int horizontalIndex;
   private final String name;
   private final EnumFacing.Axis axis;
   private final EnumFacing.AxisDirection axisDirection;
   /** Normalized Vector that points in the direction of this Facing */
   private final Vec3i directionVec;
   private static final EnumFacing[] VALUES = values();
   private static final Map<String, EnumFacing> NAME_LOOKUP = Arrays.stream(VALUES).collect(Collectors.toMap(EnumFacing::getName2, (p_199787_0_) -> {
      return p_199787_0_;
   }));
   /** All facings in D-U-N-S-W-E order */
   public static final EnumFacing[] BY_INDEX = Arrays.stream(VALUES).sorted(Comparator.comparingInt((p_199790_0_) -> {
      return p_199790_0_.index;
   })).toArray((p_199788_0_) -> {
      return new EnumFacing[p_199788_0_];
   });
   /** All Facings with horizontal axis in order S-W-N-E */
   public static final EnumFacing[] BY_HORIZONTAL_INDEX = Arrays.stream(VALUES).filter((p_199786_0_) -> {
      return p_199786_0_.getAxis().isHorizontal();
   }).sorted(Comparator.comparingInt((p_199789_0_) -> {
      return p_199789_0_.horizontalIndex;
   })).toArray((p_199791_0_) -> {
      return new EnumFacing[p_199791_0_];
   });

   private EnumFacing(int indexIn, int oppositeIn, int horizontalIndexIn, String nameIn, EnumFacing.AxisDirection axisDirectionIn, EnumFacing.Axis axisIn, Vec3i directionVecIn) {
      this.index = indexIn;
      this.horizontalIndex = horizontalIndexIn;
      this.opposite = oppositeIn;
      this.name = nameIn;
      this.axis = axisIn;
      this.axisDirection = axisDirectionIn;
      this.directionVec = directionVecIn;
   }

   /**
    * Gets the {@link EnumFacing} values for the provided entity's
    *  looking direction. Dependent on yaw and pitch of entity looking.
    */
   public static EnumFacing[] getFacingDirections(Entity entityIn) {
      float f = entityIn.getPitch(1.0F) * ((float)Math.PI / 180F);
      float f1 = -entityIn.getYaw(1.0F) * ((float)Math.PI / 180F);
      float f2 = MathHelper.sin(f);
      float f3 = MathHelper.cos(f);
      float f4 = MathHelper.sin(f1);
      float f5 = MathHelper.cos(f1);
      boolean flag = f4 > 0.0F;
      boolean flag1 = f2 < 0.0F;
      boolean flag2 = f5 > 0.0F;
      float f6 = flag ? f4 : -f4;
      float f7 = flag1 ? -f2 : f2;
      float f8 = flag2 ? f5 : -f5;
      float f9 = f6 * f3;
      float f10 = f8 * f3;
      EnumFacing enumfacing = flag ? EAST : WEST;
      EnumFacing enumfacing1 = flag1 ? UP : DOWN;
      EnumFacing enumfacing2 = flag2 ? SOUTH : NORTH;
      if (f6 > f8) {
         if (f7 > f9) {
            return compose(enumfacing1, enumfacing, enumfacing2);
         } else {
            return f10 > f7 ? compose(enumfacing, enumfacing2, enumfacing1) : compose(enumfacing, enumfacing1, enumfacing2);
         }
      } else if (f7 > f10) {
         return compose(enumfacing1, enumfacing2, enumfacing);
      } else {
         return f9 > f7 ? compose(enumfacing2, enumfacing, enumfacing1) : compose(enumfacing2, enumfacing1, enumfacing);
      }
   }

   /**
    * Creates an array of x y z equivalent facing values.
    */
   private static EnumFacing[] compose(EnumFacing first, EnumFacing second, EnumFacing third) {
      return new EnumFacing[]{first, second, third, third.getOpposite(), second.getOpposite(), first.getOpposite()};
   }

   /**
    * Get the Index of this Facing (0-5). The order is D-U-N-S-W-E
    */
   public int getIndex() {
      return this.index;
   }

   /**
    * Get the index of this horizontal facing (0-3). The order is S-W-N-E
    */
   public int getHorizontalIndex() {
      return this.horizontalIndex;
   }

   /**
    * Get the AxisDirection of this Facing.
    */
   public EnumFacing.AxisDirection getAxisDirection() {
      return this.axisDirection;
   }

   /**
    * Get the opposite Facing (e.g. DOWN => UP)
    */
   public EnumFacing getOpposite() {
      return byIndex(this.opposite);
   }

   /**
    * Rotate this Facing around the given axis clockwise. If this facing cannot be rotated around the given axis,
    * returns this facing without rotating.
    */
   public EnumFacing rotateAround(EnumFacing.Axis axis) {
      switch(axis) {
      case X:
         if (this != WEST && this != EAST) {
            return this.rotateX();
         }

         return this;
      case Y:
         if (this != UP && this != DOWN) {
            return this.rotateY();
         }

         return this;
      case Z:
         if (this != NORTH && this != SOUTH) {
            return this.rotateZ();
         }

         return this;
      default:
         throw new IllegalStateException("Unable to get CW facing for axis " + axis);
      }
   }

   /**
    * Rotate this Facing around the Y axis clockwise (NORTH => EAST => SOUTH => WEST => NORTH)
    */
   public EnumFacing rotateY() {
      switch(this) {
      case NORTH:
         return EAST;
      case EAST:
         return SOUTH;
      case SOUTH:
         return WEST;
      case WEST:
         return NORTH;
      default:
         throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
      }
   }

   /**
    * Rotate this Facing around the X axis (NORTH => DOWN => SOUTH => UP => NORTH)
    */
   private EnumFacing rotateX() {
      switch(this) {
      case NORTH:
         return DOWN;
      case EAST:
      case WEST:
      default:
         throw new IllegalStateException("Unable to get X-rotated facing of " + this);
      case SOUTH:
         return UP;
      case UP:
         return NORTH;
      case DOWN:
         return SOUTH;
      }
   }

   /**
    * Rotate this Facing around the Z axis (EAST => DOWN => WEST => UP => EAST)
    */
   private EnumFacing rotateZ() {
      switch(this) {
      case EAST:
         return DOWN;
      case SOUTH:
      default:
         throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
      case WEST:
         return UP;
      case UP:
         return EAST;
      case DOWN:
         return WEST;
      }
   }

   /**
    * Rotate this Facing around the Y axis counter-clockwise (NORTH => WEST => SOUTH => EAST => NORTH)
    */
   public EnumFacing rotateYCCW() {
      switch(this) {
      case NORTH:
         return WEST;
      case EAST:
         return NORTH;
      case SOUTH:
         return EAST;
      case WEST:
         return SOUTH;
      default:
         throw new IllegalStateException("Unable to get CCW facing of " + this);
      }
   }

   /**
    * Gets the offset in the x direction to the block in front of this facing.
    */
   public int getXOffset() {
      return this.axis == EnumFacing.Axis.X ? this.axisDirection.getOffset() : 0;
   }

   /**
    * Gets the offset in the y direction to the block in front of this facing.
    */
   public int getYOffset() {
      return this.axis == EnumFacing.Axis.Y ? this.axisDirection.getOffset() : 0;
   }

   /**
    * Gets the offset in the z direction to the block in front of this facing.
    */
   public int getZOffset() {
      return this.axis == EnumFacing.Axis.Z ? this.axisDirection.getOffset() : 0;
   }

   /**
    * Same as getName, but does not override the method from Enum.
    */
   public String getName2() {
      return this.name;
   }

   public EnumFacing.Axis getAxis() {
      return this.axis;
   }

   /**
    * Get the facing specified by the given name
    */
   @Nullable
   public static EnumFacing byName(@Nullable String name) {
      return name == null ? null : NAME_LOOKUP.get(name.toLowerCase(Locale.ROOT));
   }

   /**
    * Gets the EnumFacing corresponding to the given index (0-5). Out of bounds values are wrapped around. The order is
    * D-U-N-S-W-E.
    */
   public static EnumFacing byIndex(int index) {
      return BY_INDEX[MathHelper.abs(index % BY_INDEX.length)];
   }

   /**
    * Gets the EnumFacing corresponding to the given horizontal index (0-3). Out of bounds values are wrapped around.
    * The order is S-W-N-E.
    */
   public static EnumFacing byHorizontalIndex(int horizontalIndexIn) {
      return BY_HORIZONTAL_INDEX[MathHelper.abs(horizontalIndexIn % BY_HORIZONTAL_INDEX.length)];
   }

   /**
    * Get the EnumFacing corresponding to the given angle in degrees (0-360). Out of bounds values are wrapped around.
    * An angle of 0 is SOUTH, an angle of 90 would be WEST.
    */
   public static EnumFacing fromAngle(double angle) {
      return byHorizontalIndex(MathHelper.floor(angle / 90.0D + 0.5D) & 3);
   }

   public static EnumFacing getFacingFromAxisDirection(EnumFacing.Axis axisIn, EnumFacing.AxisDirection axisDirectionIn) {
      switch(axisIn) {
      case X:
         return axisDirectionIn == EnumFacing.AxisDirection.POSITIVE ? EAST : WEST;
      case Y:
         return axisDirectionIn == EnumFacing.AxisDirection.POSITIVE ? UP : DOWN;
      case Z:
      default:
         return axisDirectionIn == EnumFacing.AxisDirection.POSITIVE ? SOUTH : NORTH;
      }
   }

   /**
    * Gets the angle in degrees corresponding to this EnumFacing.
    */
   public float getHorizontalAngle() {
      return (float)((this.horizontalIndex & 3) * 90);
   }

   /**
    * Choose a random Facing using the given Random
    */
   public static EnumFacing random(Random rand) {
      return values()[rand.nextInt(values().length)];
   }

   public static EnumFacing getFacingFromVector(double x, double y, double z) {
      return getFacingFromVector((float)x, (float)y, (float)z);
   }

   public static EnumFacing getFacingFromVector(float x, float y, float z) {
      EnumFacing enumfacing = NORTH;
      float f = Float.MIN_VALUE;

      for(EnumFacing enumfacing1 : VALUES) {
         float f1 = x * (float)enumfacing1.directionVec.getX() + y * (float)enumfacing1.directionVec.getY() + z * (float)enumfacing1.directionVec.getZ();
         if (f1 > f) {
            f = f1;
            enumfacing = enumfacing1;
         }
      }

      return enumfacing;
   }

   public String toString() {
      return this.name;
   }

   public String getName() {
      return this.name;
   }

   public static EnumFacing getFacingFromAxis(EnumFacing.AxisDirection axisDirectionIn, EnumFacing.Axis axisIn) {
      for(EnumFacing enumfacing : values()) {
         if (enumfacing.getAxisDirection() == axisDirectionIn && enumfacing.getAxis() == axisIn) {
            return enumfacing;
         }
      }

      throw new IllegalArgumentException("No such direction: " + axisDirectionIn + " " + axisIn);
   }

   /**
    * Get a normalized Vector that points in the direction of this Facing.
    */
   public Vec3i getDirectionVec() {
      return this.directionVec;
   }

   public static enum Axis implements Predicate<EnumFacing>, IStringSerializable {
      X("x") {
         public int getCoordinate(int x, int y, int z) {
            return x;
         }

         public double getCoordinate(double x, double y, double z) {
            return x;
         }
      },
      Y("y") {
         public int getCoordinate(int x, int y, int z) {
            return y;
         }

         public double getCoordinate(double x, double y, double z) {
            return y;
         }
      },
      Z("z") {
         public int getCoordinate(int x, int y, int z) {
            return z;
         }

         public double getCoordinate(double x, double y, double z) {
            return z;
         }
      };

      private static final Map<String, EnumFacing.Axis> NAME_LOOKUP = Arrays.stream(values()).collect(Collectors.toMap(EnumFacing.Axis::getName2, (p_199785_0_) -> {
         return p_199785_0_;
      }));
      private final String name;

      private Axis(String nameIn) {
         this.name = nameIn;
      }

      /**
       * Get the axis specified by the given name
       */
      @Nullable
      public static EnumFacing.Axis byName(String name) {
         return NAME_LOOKUP.get(name.toLowerCase(Locale.ROOT));
      }

      /**
       * Like getName but doesn't override the method from Enum.
       */
      public String getName2() {
         return this.name;
      }

      public boolean isVertical() {
         return this == Y;
      }

      /**
       * If this Axis is on the horizontal plane (true for X and Z)
       */
      public boolean isHorizontal() {
         return this == X || this == Z;
      }

      public String toString() {
         return this.name;
      }

      public boolean test(@Nullable EnumFacing p_test_1_) {
         return p_test_1_ != null && p_test_1_.getAxis() == this;
      }

      /**
       * Get this Axis' Plane (VERTICAL for Y, HORIZONTAL for X and Z)
       */
      public EnumFacing.Plane getPlane() {
         switch(this) {
         case X:
         case Z:
            return EnumFacing.Plane.HORIZONTAL;
         case Y:
            return EnumFacing.Plane.VERTICAL;
         default:
            throw new Error("Someone's been tampering with the universe!");
         }
      }

      public String getName() {
         return this.name;
      }

      public abstract int getCoordinate(int x, int y, int z);

      public abstract double getCoordinate(double x, double y, double z);
   }

   public static enum AxisDirection {
      POSITIVE(1, "Towards positive"),
      NEGATIVE(-1, "Towards negative");

      private final int offset;
      private final String description;

      private AxisDirection(int offset, String description) {
         this.offset = offset;
         this.description = description;
      }

      /**
       * Get the offset for this AxisDirection. 1 for POSITIVE, -1 for NEGATIVE
       */
      public int getOffset() {
         return this.offset;
      }

      public String toString() {
         return this.description;
      }
   }

   public static enum Plane implements Iterable<EnumFacing>, Predicate<EnumFacing> {
      HORIZONTAL(new EnumFacing[]{EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST}, new EnumFacing.Axis[]{EnumFacing.Axis.X, EnumFacing.Axis.Z}),
      VERTICAL(new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN}, new EnumFacing.Axis[]{EnumFacing.Axis.Y});

      private final EnumFacing[] facingValues;
      private final EnumFacing.Axis[] axisValues;

      private Plane(EnumFacing[] facingValuesIn, EnumFacing.Axis[] axisValuesIn) {
         this.facingValues = facingValuesIn;
         this.axisValues = axisValuesIn;
      }

      /**
       * Choose a random Facing from this Plane using the given Random
       */
      public EnumFacing random(Random rand) {
         return this.facingValues[rand.nextInt(this.facingValues.length)];
      }

      public boolean test(@Nullable EnumFacing p_test_1_) {
         return p_test_1_ != null && p_test_1_.getAxis().getPlane() == this;
      }

      public Iterator<EnumFacing> iterator() {
         return Iterators.forArray(this.facingValues);
      }
   }
}