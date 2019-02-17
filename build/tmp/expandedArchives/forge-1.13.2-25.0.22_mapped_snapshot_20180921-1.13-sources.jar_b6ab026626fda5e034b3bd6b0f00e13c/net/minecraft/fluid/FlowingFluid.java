package net.minecraft.fluid;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public abstract class FlowingFluid extends Fluid {
   public static final BooleanProperty FALLING = BlockStateProperties.FALLING;
   public static final IntegerProperty LEVEL_1_TO_8 = BlockStateProperties.LEVEL_1_8;
   private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey>> field_212756_e = ThreadLocal.withInitial(() -> {
      Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey> object2bytelinkedopenhashmap = new Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey>(200) {
         protected void rehash(int p_rehash_1_) {
         }
      };
      object2bytelinkedopenhashmap.defaultReturnValue((byte)127);
      return object2bytelinkedopenhashmap;
   });

   protected void fillStateContainer(StateContainer.Builder<Fluid, IFluidState> builder) {
      builder.add(FALLING);
   }

   public Vec3d getFlow(IWorldReaderBase worldIn, BlockPos pos, IFluidState state) {
      double d0 = 0.0D;
      double d1 = 0.0D;

      Vec3d vec3d1;
      try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
         for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            blockpos$pooledmutableblockpos.setPos(pos).move(enumfacing);
            IFluidState ifluidstate = worldIn.getFluidState(blockpos$pooledmutableblockpos);
            if (this.func_212189_g(ifluidstate)) {
               float f = ifluidstate.getHeight();
               float f1 = 0.0F;
               if (f == 0.0F) {
                  if (!worldIn.getBlockState(blockpos$pooledmutableblockpos).getMaterial().blocksMovement()) {
                     IFluidState ifluidstate1 = worldIn.getFluidState(blockpos$pooledmutableblockpos.down());
                     if (this.func_212189_g(ifluidstate1)) {
                        f = ifluidstate1.getHeight();
                        if (f > 0.0F) {
                           f1 = state.getHeight() - (f - 0.8888889F);
                        }
                     }
                  }
               } else if (f > 0.0F) {
                  f1 = state.getHeight() - f;
               }

               if (f1 != 0.0F) {
                  d0 += (double)((float)enumfacing.getXOffset() * f1);
                  d1 += (double)((float)enumfacing.getZOffset() * f1);
               }
            }
         }

         Vec3d vec3d = new Vec3d(d0, 0.0D, d1);
         if (state.get(FALLING)) {
            for(EnumFacing enumfacing1 : EnumFacing.Plane.HORIZONTAL) {
               blockpos$pooledmutableblockpos.setPos(pos).move(enumfacing1);
               if (this.func_205573_a(worldIn, blockpos$pooledmutableblockpos, enumfacing1) || this.func_205573_a(worldIn, blockpos$pooledmutableblockpos.up(), enumfacing1)) {
                  vec3d = vec3d.normalize().add(0.0D, -6.0D, 0.0D);
                  break;
               }
            }
         }

         vec3d1 = vec3d.normalize();
      }

      return vec3d1;
   }

   private boolean func_212189_g(IFluidState p_212189_1_) {
      return p_212189_1_.isEmpty() || p_212189_1_.getFluid().isEquivalentTo(this);
   }

   protected boolean func_205573_a(IBlockReader p_205573_1_, BlockPos p_205573_2_, EnumFacing p_205573_3_) {
      IBlockState iblockstate = p_205573_1_.getBlockState(p_205573_2_);
      Block block = iblockstate.getBlock();
      IFluidState ifluidstate = p_205573_1_.getFluidState(p_205573_2_);
      if (ifluidstate.getFluid().isEquivalentTo(this)) {
         return false;
      } else if (p_205573_3_ == EnumFacing.UP) {
         return true;
      } else if (iblockstate.getMaterial() == Material.ICE) {
         return false;
      } else {
         boolean flag = Block.isExceptBlockForAttachWithPiston(block) || block instanceof BlockStairs;
         return !flag && iblockstate.getBlockFaceShape(p_205573_1_, p_205573_2_, p_205573_3_) == BlockFaceShape.SOLID;
      }
   }

   protected void flowAround(IWorld worldIn, BlockPos pos, IFluidState stateIn) {
      if (!stateIn.isEmpty()) {
         IBlockState iblockstate = worldIn.getBlockState(pos);
         BlockPos blockpos = pos.down();
         IBlockState iblockstate1 = worldIn.getBlockState(blockpos);
         IFluidState ifluidstate = this.calculateCorrectFlowingState(worldIn, blockpos, iblockstate1);
         if (this.canFlow(worldIn, pos, iblockstate, EnumFacing.DOWN, blockpos, iblockstate1, worldIn.getFluidState(blockpos), ifluidstate.getFluid())) {
            this.flowInto(worldIn, blockpos, iblockstate1, EnumFacing.DOWN, ifluidstate);
            if (this.func_207936_a(worldIn, pos) >= 3) {
               this.func_207937_a(worldIn, pos, stateIn, iblockstate);
            }
         } else if (stateIn.isSource() || !this.func_211759_a(worldIn, ifluidstate.getFluid(), pos, iblockstate, blockpos, iblockstate1)) {
            this.func_207937_a(worldIn, pos, stateIn, iblockstate);
         }

      }
   }

   private void func_207937_a(IWorld p_207937_1_, BlockPos p_207937_2_, IFluidState p_207937_3_, IBlockState p_207937_4_) {
      int i = p_207937_3_.getLevel() - this.getLevelDecreasePerBlock(p_207937_1_);
      if (p_207937_3_.get(FALLING)) {
         i = 7;
      }

      if (i > 0) {
         Map<EnumFacing, IFluidState> map = this.func_205572_b(p_207937_1_, p_207937_2_, p_207937_4_);

         for(Entry<EnumFacing, IFluidState> entry : map.entrySet()) {
            EnumFacing enumfacing = entry.getKey();
            IFluidState ifluidstate = entry.getValue();
            BlockPos blockpos = p_207937_2_.offset(enumfacing);
            IBlockState iblockstate = p_207937_1_.getBlockState(blockpos);
            if (this.canFlow(p_207937_1_, p_207937_2_, p_207937_4_, enumfacing, blockpos, iblockstate, p_207937_1_.getFluidState(blockpos), ifluidstate.getFluid())) {
               this.flowInto(p_207937_1_, blockpos, iblockstate, enumfacing, ifluidstate);
            }
         }

      }
   }

   protected IFluidState calculateCorrectFlowingState(IWorldReaderBase worldIn, BlockPos pos, IBlockState blockStateIn) {
      int i = 0;
      int j = 0;

      for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
         BlockPos blockpos = pos.offset(enumfacing);
         IBlockState iblockstate = worldIn.getBlockState(blockpos);
         IFluidState ifluidstate = iblockstate.getFluidState();
         if (ifluidstate.getFluid().isEquivalentTo(this) && this.func_212751_a(enumfacing, worldIn, pos, blockStateIn, blockpos, iblockstate)) {
            if (ifluidstate.isSource()) {
               ++j;
            }

            i = Math.max(i, ifluidstate.getLevel());
         }
      }

      if (this.canSourcesMultiply() && j >= 2) {
         IBlockState iblockstate1 = worldIn.getBlockState(pos.down());
         IFluidState ifluidstate1 = iblockstate1.getFluidState();
         if (iblockstate1.getMaterial().isSolid() || this.isSameAs(ifluidstate1)) {
            return this.getStillFluidState(false);
         }
      }

      BlockPos blockpos1 = pos.up();
      IBlockState iblockstate2 = worldIn.getBlockState(blockpos1);
      IFluidState ifluidstate2 = iblockstate2.getFluidState();
      if (!ifluidstate2.isEmpty() && ifluidstate2.getFluid().isEquivalentTo(this) && this.func_212751_a(EnumFacing.UP, worldIn, pos, blockStateIn, blockpos1, iblockstate2)) {
         return this.getFlowingFluidState(8, true);
      } else {
         int k = i - this.getLevelDecreasePerBlock(worldIn);
         if (k <= 0) {
            return Fluids.EMPTY.getDefaultState();
         } else {
            return this.getFlowingFluidState(k, false);
         }
      }
   }

   private boolean func_212751_a(EnumFacing p_212751_1_, IBlockReader p_212751_2_, BlockPos p_212751_3_, IBlockState p_212751_4_, BlockPos p_212751_5_, IBlockState p_212751_6_) {
      Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey> object2bytelinkedopenhashmap;
      if (!p_212751_4_.getBlock().isVariableOpacity() && !p_212751_6_.getBlock().isVariableOpacity()) {
         object2bytelinkedopenhashmap = field_212756_e.get();
      } else {
         object2bytelinkedopenhashmap = null;
      }

      Block.RenderSideCacheKey block$rendersidecachekey;
      if (object2bytelinkedopenhashmap != null) {
         block$rendersidecachekey = new Block.RenderSideCacheKey(p_212751_4_, p_212751_6_, p_212751_1_);
         byte b0 = object2bytelinkedopenhashmap.getAndMoveToFirst(block$rendersidecachekey);
         if (b0 != 127) {
            return b0 != 0;
         }
      } else {
         block$rendersidecachekey = null;
      }

      VoxelShape voxelshape1 = p_212751_4_.getCollisionShape(p_212751_2_, p_212751_3_);
      VoxelShape voxelshape = p_212751_6_.getCollisionShape(p_212751_2_, p_212751_5_);
      boolean flag = !VoxelShapes.doAdjacentCubeSidesFillSquare(voxelshape1, voxelshape, p_212751_1_);
      if (object2bytelinkedopenhashmap != null) {
         if (object2bytelinkedopenhashmap.size() == 200) {
            object2bytelinkedopenhashmap.removeLastByte();
         }

         object2bytelinkedopenhashmap.putAndMoveToFirst(block$rendersidecachekey, (byte)(flag ? 1 : 0));
      }

      return flag;
   }

   public abstract Fluid getFlowingFluid();

   public IFluidState getFlowingFluidState(int level, boolean falling) {
      return this.getFlowingFluid().getDefaultState().with(LEVEL_1_TO_8, Integer.valueOf(level)).with(FALLING, Boolean.valueOf(falling));
   }

   public abstract Fluid getStillFluid();

   public IFluidState getStillFluidState(boolean falling) {
      return this.getStillFluid().getDefaultState().with(FALLING, Boolean.valueOf(falling));
   }

   protected abstract boolean canSourcesMultiply();

   protected void flowInto(IWorld worldIn, BlockPos pos, IBlockState blockStateIn, EnumFacing direction, IFluidState fluidStateIn) {
      if (blockStateIn.getBlock() instanceof ILiquidContainer) {
         ((ILiquidContainer)blockStateIn.getBlock()).receiveFluid(worldIn, pos, blockStateIn, fluidStateIn);
      } else {
         if (!blockStateIn.isAir()) {
            this.beforeReplacingBlock(worldIn, pos, blockStateIn);
         }

         worldIn.setBlockState(pos, fluidStateIn.getBlockState(), 3);
      }

   }

   protected abstract void beforeReplacingBlock(IWorld worldIn, BlockPos pos, IBlockState state);

   private static short func_212752_a(BlockPos p_212752_0_, BlockPos p_212752_1_) {
      int i = p_212752_1_.getX() - p_212752_0_.getX();
      int j = p_212752_1_.getZ() - p_212752_0_.getZ();
      return (short)((i + 128 & 255) << 8 | j + 128 & 255);
   }

   protected int func_205571_a(IWorldReaderBase p_205571_1_, BlockPos p_205571_2_, int p_205571_3_, EnumFacing p_205571_4_, IBlockState p_205571_5_, BlockPos p_205571_6_, Short2ObjectMap<Pair<IBlockState, IFluidState>> p_205571_7_, Short2BooleanMap p_205571_8_) {
      int i = 1000;

      for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
         if (enumfacing != p_205571_4_) {
            BlockPos blockpos = p_205571_2_.offset(enumfacing);
            short short1 = func_212752_a(p_205571_6_, blockpos);
            Pair<IBlockState, IFluidState> pair = p_205571_7_.computeIfAbsent(short1, (p_212748_2_) -> {
               IBlockState iblockstate1 = p_205571_1_.getBlockState(blockpos);
               return Pair.of(iblockstate1, iblockstate1.getFluidState());
            });
            IBlockState iblockstate = pair.getFirst();
            IFluidState ifluidstate = pair.getSecond();
            if (this.func_211760_a(p_205571_1_, this.getFlowingFluid(), p_205571_2_, p_205571_5_, enumfacing, blockpos, iblockstate, ifluidstate)) {
               boolean flag = p_205571_8_.computeIfAbsent(short1, (p_212749_4_) -> {
                  BlockPos blockpos1 = blockpos.down();
                  IBlockState iblockstate1 = p_205571_1_.getBlockState(blockpos1);
                  return this.func_211759_a(p_205571_1_, this.getFlowingFluid(), blockpos, iblockstate, blockpos1, iblockstate1);
               });
               if (flag) {
                  return p_205571_3_;
               }

               if (p_205571_3_ < this.getSlopeFindDistance(p_205571_1_)) {
                  int j = this.func_205571_a(p_205571_1_, blockpos, p_205571_3_ + 1, enumfacing.getOpposite(), iblockstate, p_205571_6_, p_205571_7_, p_205571_8_);
                  if (j < i) {
                     i = j;
                  }
               }
            }
         }
      }

      return i;
   }

   private boolean func_211759_a(IBlockReader p_211759_1_, Fluid p_211759_2_, BlockPos p_211759_3_, IBlockState p_211759_4_, BlockPos p_211759_5_, IBlockState p_211759_6_) {
      if (!this.func_212751_a(EnumFacing.DOWN, p_211759_1_, p_211759_3_, p_211759_4_, p_211759_5_, p_211759_6_)) {
         return false;
      } else {
         return p_211759_6_.getFluidState().getFluid().isEquivalentTo(this) ? true : this.func_211761_a(p_211759_1_, p_211759_5_, p_211759_6_, p_211759_2_);
      }
   }

   private boolean func_211760_a(IBlockReader p_211760_1_, Fluid p_211760_2_, BlockPos p_211760_3_, IBlockState p_211760_4_, EnumFacing p_211760_5_, BlockPos p_211760_6_, IBlockState p_211760_7_, IFluidState p_211760_8_) {
      return !this.isSameAs(p_211760_8_) && this.func_212751_a(p_211760_5_, p_211760_1_, p_211760_3_, p_211760_4_, p_211760_6_, p_211760_7_) && this.func_211761_a(p_211760_1_, p_211760_6_, p_211760_7_, p_211760_2_);
   }

   private boolean isSameAs(IFluidState stateIn) {
      return stateIn.getFluid().isEquivalentTo(this) && stateIn.isSource();
   }

   protected abstract int getSlopeFindDistance(IWorldReaderBase worldIn);

   private int func_207936_a(IWorldReaderBase worldIn, BlockPos pos) {
      int i = 0;

      for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
         BlockPos blockpos = pos.offset(enumfacing);
         IFluidState ifluidstate = worldIn.getFluidState(blockpos);
         if (this.isSameAs(ifluidstate)) {
            ++i;
         }
      }

      return i;
   }

   protected Map<EnumFacing, IFluidState> func_205572_b(IWorldReaderBase p_205572_1_, BlockPos p_205572_2_, IBlockState p_205572_3_) {
      int i = 1000;
      Map<EnumFacing, IFluidState> map = Maps.newEnumMap(EnumFacing.class);
      Short2ObjectMap<Pair<IBlockState, IFluidState>> short2objectmap = new Short2ObjectOpenHashMap<>();
      Short2BooleanMap short2booleanmap = new Short2BooleanOpenHashMap();

      for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
         BlockPos blockpos = p_205572_2_.offset(enumfacing);
         short short1 = func_212752_a(p_205572_2_, blockpos);
         Pair<IBlockState, IFluidState> pair = short2objectmap.computeIfAbsent(short1, (p_212755_2_) -> {
            IBlockState iblockstate1 = p_205572_1_.getBlockState(blockpos);
            return Pair.of(iblockstate1, iblockstate1.getFluidState());
         });
         IBlockState iblockstate = pair.getFirst();
         IFluidState ifluidstate = pair.getSecond();
         IFluidState ifluidstate1 = this.calculateCorrectFlowingState(p_205572_1_, blockpos, iblockstate);
         if (this.func_211760_a(p_205572_1_, ifluidstate1.getFluid(), p_205572_2_, p_205572_3_, enumfacing, blockpos, iblockstate, ifluidstate)) {
            BlockPos blockpos1 = blockpos.down();
            boolean flag = short2booleanmap.computeIfAbsent(short1, (p_212753_5_) -> {
               IBlockState iblockstate1 = p_205572_1_.getBlockState(blockpos1);
               return this.func_211759_a(p_205572_1_, this.getFlowingFluid(), blockpos, iblockstate, blockpos1, iblockstate1);
            });
            int j;
            if (flag) {
               j = 0;
            } else {
               j = this.func_205571_a(p_205572_1_, blockpos, 1, enumfacing.getOpposite(), iblockstate, p_205572_2_, short2objectmap, short2booleanmap);
            }

            if (j < i) {
               map.clear();
            }

            if (j <= i) {
               map.put(enumfacing, ifluidstate1);
               i = j;
            }
         }
      }

      return map;
   }

   private boolean func_211761_a(IBlockReader p_211761_1_, BlockPos p_211761_2_, IBlockState p_211761_3_, Fluid p_211761_4_) {
      Block block = p_211761_3_.getBlock();
      if (block instanceof ILiquidContainer) {
         return ((ILiquidContainer)block).canContainFluid(p_211761_1_, p_211761_2_, p_211761_3_, p_211761_4_);
      } else if (!(block instanceof BlockDoor) && block != Blocks.SIGN && block != Blocks.LADDER && block != Blocks.SUGAR_CANE && block != Blocks.BUBBLE_COLUMN) {
         Material material = p_211761_3_.getMaterial();
         if (material != Material.PORTAL && material != Material.STRUCTURE_VOID && material != Material.OCEAN_PLANT && material != Material.SEA_GRASS) {
            return !material.blocksMovement();
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   protected boolean canFlow(IBlockReader worldIn, BlockPos fromPos, IBlockState fromBlockState, EnumFacing direction, BlockPos toPos, IBlockState toBlockState, IFluidState toFluidState, Fluid fluidIn) {
      return toFluidState.canOtherFlowInto(fluidIn, direction) && this.func_212751_a(direction, worldIn, fromPos, fromBlockState, toPos, toBlockState) && this.func_211761_a(worldIn, toPos, toBlockState, fluidIn);
   }

   protected abstract int getLevelDecreasePerBlock(IWorldReaderBase worldIn);

   protected int getTickRate(World worldIn, IFluidState p_205578_2_, IFluidState p_205578_3_) {
      return this.getTickRate(worldIn);
   }

   public void tick(World worldIn, BlockPos pos, IFluidState state) {
      if (!state.isSource()) {
         IFluidState ifluidstate = this.calculateCorrectFlowingState(worldIn, pos, worldIn.getBlockState(pos));
         int i = this.getTickRate(worldIn, state, ifluidstate);
         if (ifluidstate.isEmpty()) {
            state = ifluidstate;
            worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
         } else if (!ifluidstate.equals(state)) {
            state = ifluidstate;
            IBlockState iblockstate = ifluidstate.getBlockState();
            worldIn.setBlockState(pos, iblockstate, 2);
            worldIn.getPendingFluidTicks().scheduleTick(pos, ifluidstate.getFluid(), i);
            worldIn.notifyNeighborsOfStateChange(pos, iblockstate.getBlock());
         }
      }

      this.flowAround(worldIn, pos, state);
   }

   protected static int getLevelFromState(IFluidState state) {
      return state.isSource() ? 0 : 8 - Math.min(state.getLevel(), 8) + (state.get(FALLING) ? 8 : 0);
   }

   public float getHeight(IFluidState state) {
      return (float)state.getLevel() / 9.0F;
   }
}