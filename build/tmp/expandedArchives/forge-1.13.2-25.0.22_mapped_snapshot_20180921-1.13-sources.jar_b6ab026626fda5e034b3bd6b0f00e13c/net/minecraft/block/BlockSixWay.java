package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.state.IBlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class BlockSixWay extends Block {
   private static final EnumFacing[] FACING_VALUES = EnumFacing.values();
   public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
   public static final BooleanProperty EAST = BlockStateProperties.EAST;
   public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
   public static final BooleanProperty WEST = BlockStateProperties.WEST;
   public static final BooleanProperty UP = BlockStateProperties.UP;
   public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
   public static final Map<EnumFacing, BooleanProperty> FACING_TO_PROPERTY_MAP = Util.make(Maps.newEnumMap(EnumFacing.class), (p_203421_0_) -> {
      p_203421_0_.put(EnumFacing.NORTH, NORTH);
      p_203421_0_.put(EnumFacing.EAST, EAST);
      p_203421_0_.put(EnumFacing.SOUTH, SOUTH);
      p_203421_0_.put(EnumFacing.WEST, WEST);
      p_203421_0_.put(EnumFacing.UP, UP);
      p_203421_0_.put(EnumFacing.DOWN, DOWN);
   });
   protected final VoxelShape[] shapes;

   protected BlockSixWay(float p_i48355_1_, Block.Properties builder) {
      super(builder);
      this.shapes = this.makeShapes(p_i48355_1_);
   }

   private VoxelShape[] makeShapes(float p_196487_1_) {
      float f = 0.5F - p_196487_1_;
      float f1 = 0.5F + p_196487_1_;
      VoxelShape voxelshape = Block.makeCuboidShape((double)(f * 16.0F), (double)(f * 16.0F), (double)(f * 16.0F), (double)(f1 * 16.0F), (double)(f1 * 16.0F), (double)(f1 * 16.0F));
      VoxelShape[] avoxelshape = new VoxelShape[FACING_VALUES.length];

      for(int i = 0; i < FACING_VALUES.length; ++i) {
         EnumFacing enumfacing = FACING_VALUES[i];
         avoxelshape[i] = VoxelShapes.create(0.5D + Math.min((double)(-p_196487_1_), (double)enumfacing.getXOffset() * 0.5D), 0.5D + Math.min((double)(-p_196487_1_), (double)enumfacing.getYOffset() * 0.5D), 0.5D + Math.min((double)(-p_196487_1_), (double)enumfacing.getZOffset() * 0.5D), 0.5D + Math.max((double)p_196487_1_, (double)enumfacing.getXOffset() * 0.5D), 0.5D + Math.max((double)p_196487_1_, (double)enumfacing.getYOffset() * 0.5D), 0.5D + Math.max((double)p_196487_1_, (double)enumfacing.getZOffset() * 0.5D));
      }

      VoxelShape[] avoxelshape1 = new VoxelShape[64];

      for(int k = 0; k < 64; ++k) {
         VoxelShape voxelshape1 = voxelshape;

         for(int j = 0; j < FACING_VALUES.length; ++j) {
            if ((k & 1 << j) != 0) {
               voxelshape1 = VoxelShapes.or(voxelshape1, avoxelshape[j]);
            }
         }

         avoxelshape1[k] = voxelshape1;
      }

      return avoxelshape1;
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return this.shapes[this.getShapeIndex(state)];
   }

   protected int getShapeIndex(IBlockState state) {
      int i = 0;

      for(int j = 0; j < FACING_VALUES.length; ++j) {
         if (state.get(FACING_TO_PROPERTY_MAP.get(FACING_VALUES[j]))) {
            i |= 1 << j;
         }
      }

      return i;
   }
}