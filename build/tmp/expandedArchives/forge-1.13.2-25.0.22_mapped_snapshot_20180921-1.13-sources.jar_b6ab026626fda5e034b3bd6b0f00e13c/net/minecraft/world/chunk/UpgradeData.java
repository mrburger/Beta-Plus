package net.minecraft.world.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockStem;
import net.minecraft.block.BlockStemGrown;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumDirection8;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UpgradeData {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final UpgradeData EMPTY = new UpgradeData();
   private static final EnumDirection8[] field_208832_b = EnumDirection8.values();
   private final EnumSet<EnumDirection8> field_196995_b = EnumSet.noneOf(EnumDirection8.class);
   private final int[][] field_196996_c = new int[16][];
   private static final Map<Block, UpgradeData.IBlockFixer> field_196997_d = new IdentityHashMap<>();
   private static final Set<UpgradeData.IBlockFixer> FIXERS = Sets.newHashSet();

   private UpgradeData() {
   }

   public UpgradeData(NBTTagCompound p_i47714_1_) {
      this();
      if (p_i47714_1_.contains("Indices", 10)) {
         NBTTagCompound nbttagcompound = p_i47714_1_.getCompound("Indices");

         for(int i = 0; i < this.field_196996_c.length; ++i) {
            String s = String.valueOf(i);
            if (nbttagcompound.contains(s, 11)) {
               this.field_196996_c[i] = nbttagcompound.getIntArray(s);
            }
         }
      }

      int j = p_i47714_1_.getInt("Sides");

      for(EnumDirection8 enumdirection8 : EnumDirection8.values()) {
         if ((j & 1 << enumdirection8.ordinal()) != 0) {
            this.field_196995_b.add(enumdirection8);
         }
      }

   }

   public void postProcessChunk(Chunk chunkIn) {
      this.func_196989_a(chunkIn);

      for(EnumDirection8 enumdirection8 : field_208832_b) {
         func_196991_a(chunkIn, enumdirection8);
      }

      World world = chunkIn.getWorld();
      FIXERS.forEach((p_208829_1_) -> {
         p_208829_1_.func_208826_a(world);
      });
   }

   private static void func_196991_a(Chunk p_196991_0_, EnumDirection8 p_196991_1_) {
      World world = p_196991_0_.getWorld();
      if (p_196991_0_.getUpgradeData().field_196995_b.remove(p_196991_1_)) {
         Set<EnumFacing> set = p_196991_1_.getDirections();
         int i = 0;
         int j = 15;
         boolean flag = set.contains(EnumFacing.EAST);
         boolean flag1 = set.contains(EnumFacing.WEST);
         boolean flag2 = set.contains(EnumFacing.SOUTH);
         boolean flag3 = set.contains(EnumFacing.NORTH);
         boolean flag4 = set.size() == 1;
         int k = (p_196991_0_.x << 4) + (!flag4 || !flag3 && !flag2 ? (flag1 ? 0 : 15) : 1);
         int l = (p_196991_0_.x << 4) + (!flag4 || !flag3 && !flag2 ? (flag1 ? 0 : 15) : 14);
         int i1 = (p_196991_0_.z << 4) + (!flag4 || !flag && !flag1 ? (flag3 ? 0 : 15) : 1);
         int j1 = (p_196991_0_.z << 4) + (!flag4 || !flag && !flag1 ? (flag3 ? 0 : 15) : 14);
         EnumFacing[] aenumfacing = EnumFacing.values();
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

         for(BlockPos.MutableBlockPos blockpos$mutableblockpos1 : BlockPos.getAllInBoxMutable(k, 0, i1, l, world.getHeight() - 1, j1)) {
            IBlockState iblockstate = world.getBlockState(blockpos$mutableblockpos1);
            IBlockState iblockstate1 = iblockstate;

            for(EnumFacing enumfacing : aenumfacing) {
               blockpos$mutableblockpos.setPos(blockpos$mutableblockpos1).move(enumfacing);
               iblockstate1 = func_196987_a(iblockstate1, enumfacing, world, blockpos$mutableblockpos1, blockpos$mutableblockpos);
            }

            Block.replaceBlock(iblockstate, iblockstate1, world, blockpos$mutableblockpos1, 18);
         }

      }
   }

   private static IBlockState func_196987_a(IBlockState p_196987_0_, EnumFacing p_196987_1_, IWorld p_196987_2_, BlockPos.MutableBlockPos p_196987_3_, BlockPos.MutableBlockPos p_196987_4_) {
      return field_196997_d.getOrDefault(p_196987_0_.getBlock(), UpgradeData.BlockFixers.DEFAULT).func_196982_a(p_196987_0_, p_196987_1_, p_196987_2_.getBlockState(p_196987_4_), p_196987_2_, p_196987_3_, p_196987_4_);
   }

   private void func_196989_a(Chunk p_196989_1_) {
      try (
         BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();
         BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos1 = BlockPos.PooledMutableBlockPos.retain();
      ) {
         IWorld iworld = p_196989_1_.getWorld();

         for(int i = 0; i < 16; ++i) {
            ChunkSection chunksection = p_196989_1_.getSections()[i];
            int[] aint = this.field_196996_c[i];
            this.field_196996_c[i] = null;
            if (chunksection != null && aint != null && aint.length > 0) {
               EnumFacing[] aenumfacing = EnumFacing.values();
               BlockStateContainer<IBlockState> blockstatecontainer = chunksection.getData();

               for(int j : aint) {
                  int k = j & 15;
                  int l = j >> 8 & 15;
                  int i1 = j >> 4 & 15;
                  blockpos$pooledmutableblockpos.setPos(k + (p_196989_1_.x << 4), l + (i << 4), i1 + (p_196989_1_.z << 4));
                  IBlockState iblockstate = blockstatecontainer.get(j);
                  IBlockState iblockstate1 = iblockstate;

                  for(EnumFacing enumfacing : aenumfacing) {
                     blockpos$pooledmutableblockpos1.setPos(blockpos$pooledmutableblockpos).move(enumfacing);
                     if (blockpos$pooledmutableblockpos.getX() >> 4 == p_196989_1_.x && blockpos$pooledmutableblockpos.getZ() >> 4 == p_196989_1_.z) {
                        iblockstate1 = func_196987_a(iblockstate1, enumfacing, iworld, blockpos$pooledmutableblockpos, blockpos$pooledmutableblockpos1);
                     }
                  }

                  Block.replaceBlock(iblockstate, iblockstate1, iworld, blockpos$pooledmutableblockpos, 18);
               }
            }
         }

         for(int j1 = 0; j1 < this.field_196996_c.length; ++j1) {
            if (this.field_196996_c[j1] != null) {
               LOGGER.warn("Discarding update data for section {} for chunk ({} {})", j1, p_196989_1_.x, p_196989_1_.z);
            }

            this.field_196996_c[j1] = null;
         }
      }

   }

   public boolean isEmpty() {
      for(int[] aint : this.field_196996_c) {
         if (aint != null) {
            return false;
         }
      }

      return this.field_196995_b.isEmpty();
   }

   public NBTTagCompound write() {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      NBTTagCompound nbttagcompound1 = new NBTTagCompound();

      for(int i = 0; i < this.field_196996_c.length; ++i) {
         String s = String.valueOf(i);
         if (this.field_196996_c[i] != null && this.field_196996_c[i].length != 0) {
            nbttagcompound1.setIntArray(s, this.field_196996_c[i]);
         }
      }

      if (!nbttagcompound1.isEmpty()) {
         nbttagcompound.setTag("Indices", nbttagcompound1);
      }

      int j = 0;

      for(EnumDirection8 enumdirection8 : this.field_196995_b) {
         j |= 1 << enumdirection8.ordinal();
      }

      nbttagcompound.setByte("Sides", (byte)j);
      return nbttagcompound;
   }

   static enum BlockFixers implements UpgradeData.IBlockFixer {
      BLACKLIST(Blocks.OBSERVER, Blocks.NETHER_PORTAL, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER, Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL, Blocks.DRAGON_EGG, Blocks.GRAVEL, Blocks.SAND, Blocks.RED_SAND, Blocks.SIGN, Blocks.WALL_SIGN) {
         public IBlockState func_196982_a(IBlockState p_196982_1_, EnumFacing p_196982_2_, IBlockState p_196982_3_, IWorld p_196982_4_, BlockPos p_196982_5_, BlockPos p_196982_6_) {
            return p_196982_1_;
         }
      },
      DEFAULT {
         public IBlockState func_196982_a(IBlockState p_196982_1_, EnumFacing p_196982_2_, IBlockState p_196982_3_, IWorld p_196982_4_, BlockPos p_196982_5_, BlockPos p_196982_6_) {
            return p_196982_1_.updatePostPlacement(p_196982_2_, p_196982_4_.getBlockState(p_196982_6_), p_196982_4_, p_196982_5_, p_196982_6_);
         }
      },
      CHEST(Blocks.CHEST, Blocks.TRAPPED_CHEST) {
         public IBlockState func_196982_a(IBlockState p_196982_1_, EnumFacing p_196982_2_, IBlockState p_196982_3_, IWorld p_196982_4_, BlockPos p_196982_5_, BlockPos p_196982_6_) {
            if (p_196982_3_.getBlock() == p_196982_1_.getBlock() && p_196982_2_.getAxis().isHorizontal() && p_196982_1_.get(BlockChest.TYPE) == ChestType.SINGLE && p_196982_3_.get(BlockChest.TYPE) == ChestType.SINGLE) {
               EnumFacing enumfacing = p_196982_1_.get(BlockChest.FACING);
               if (p_196982_2_.getAxis() != enumfacing.getAxis() && enumfacing == p_196982_3_.get(BlockChest.FACING)) {
                  ChestType chesttype = p_196982_2_ == enumfacing.rotateY() ? ChestType.LEFT : ChestType.RIGHT;
                  p_196982_4_.setBlockState(p_196982_6_, p_196982_3_.with(BlockChest.TYPE, chesttype.opposite()), 18);
                  if (enumfacing == EnumFacing.NORTH || enumfacing == EnumFacing.EAST) {
                     TileEntity tileentity = p_196982_4_.getTileEntity(p_196982_5_);
                     TileEntity tileentity1 = p_196982_4_.getTileEntity(p_196982_6_);
                     if (tileentity instanceof TileEntityChest && tileentity1 instanceof TileEntityChest) {
                        TileEntityChest.swapContents((TileEntityChest)tileentity, (TileEntityChest)tileentity1);
                     }
                  }

                  return p_196982_1_.with(BlockChest.TYPE, chesttype);
               }
            }

            return p_196982_1_;
         }
      },
      LEAVES(true, Blocks.ACACIA_LEAVES, Blocks.BIRCH_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES) {
         private final ThreadLocal<List<ObjectSet<BlockPos>>> field_208828_g = ThreadLocal.withInitial(() -> {
            return Lists.newArrayListWithCapacity(7);
         });

         public IBlockState func_196982_a(IBlockState p_196982_1_, EnumFacing p_196982_2_, IBlockState p_196982_3_, IWorld p_196982_4_, BlockPos p_196982_5_, BlockPos p_196982_6_) {
            IBlockState iblockstate = p_196982_1_.updatePostPlacement(p_196982_2_, p_196982_4_.getBlockState(p_196982_6_), p_196982_4_, p_196982_5_, p_196982_6_);
            if (p_196982_1_ != iblockstate) {
               int i = iblockstate.get(BlockStateProperties.DISTANCE_1_7);
               List<ObjectSet<BlockPos>> list = this.field_208828_g.get();
               if (list.isEmpty()) {
                  for(int j = 0; j < 7; ++j) {
                     list.add(new ObjectOpenHashSet<>());
                  }
               }

               list.get(i).add(p_196982_5_.toImmutable());
            }

            return p_196982_1_;
         }

         public void func_208826_a(IWorld p_208826_1_) {
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
            List<ObjectSet<BlockPos>> list = this.field_208828_g.get();

            for(int i = 2; i < list.size(); ++i) {
               int j = i - 1;
               ObjectSet<BlockPos> objectset = list.get(j);
               ObjectSet<BlockPos> objectset1 = list.get(i);

               for(BlockPos blockpos : objectset) {
                  IBlockState iblockstate = p_208826_1_.getBlockState(blockpos);
                  if (iblockstate.get(BlockStateProperties.DISTANCE_1_7) >= j) {
                     p_208826_1_.setBlockState(blockpos, iblockstate.with(BlockStateProperties.DISTANCE_1_7, Integer.valueOf(j)), 18);
                     if (i != 7) {
                        for(EnumFacing enumfacing : field_208827_f) {
                           blockpos$mutableblockpos.setPos(blockpos).move(enumfacing);
                           IBlockState iblockstate1 = p_208826_1_.getBlockState(blockpos$mutableblockpos);
                           if (iblockstate1.has(BlockStateProperties.DISTANCE_1_7) && iblockstate.get(BlockStateProperties.DISTANCE_1_7) > i) {
                              objectset1.add(blockpos$mutableblockpos.toImmutable());
                           }
                        }
                     }
                  }
               }
            }

            list.clear();
         }
      },
      STEM_BLOCK(Blocks.MELON_STEM, Blocks.PUMPKIN_STEM) {
         public IBlockState func_196982_a(IBlockState p_196982_1_, EnumFacing p_196982_2_, IBlockState p_196982_3_, IWorld p_196982_4_, BlockPos p_196982_5_, BlockPos p_196982_6_) {
            if (p_196982_1_.get(BlockStem.AGE) == 7) {
               BlockStemGrown blockstemgrown = ((BlockStem)p_196982_1_.getBlock()).getCrop();
               if (p_196982_3_.getBlock() == blockstemgrown) {
                  return blockstemgrown.getAttachedStem().getDefaultState().with(BlockHorizontal.HORIZONTAL_FACING, p_196982_2_);
               }
            }

            return p_196982_1_;
         }
      };

      public static final EnumFacing[] field_208827_f = EnumFacing.values();

      private BlockFixers(Block... p_i47847_3_) {
         this(false, p_i47847_3_);
      }

      private BlockFixers(boolean p_i49366_3_, Block... p_i49366_4_) {
         for(Block block : p_i49366_4_) {
            UpgradeData.field_196997_d.put(block, this);
         }

         if (p_i49366_3_) {
            UpgradeData.FIXERS.add(this);
         }

      }
   }

   public interface IBlockFixer {
      IBlockState func_196982_a(IBlockState p_196982_1_, EnumFacing p_196982_2_, IBlockState p_196982_3_, IWorld p_196982_4_, BlockPos p_196982_5_, BlockPos p_196982_6_);

      default void func_208826_a(IWorld p_208826_1_) {
      }
   }
}