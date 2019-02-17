package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMaterialMatcher;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.block.state.pattern.BlockStateMatcher;
import net.minecraft.block.state.pattern.FactoryBlockPattern;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class BlockSkullWither extends BlockSkull {
   private static BlockPattern witherPatternFull;
   private static BlockPattern witherPatternBase;

   protected BlockSkullWither(Block.Properties builder) {
      super(BlockSkull.Types.WITHER_SKELETON, builder);
   }

   /**
    * Called by ItemBlocks after a block is set in the world, to allow post-place logic
    */
   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, @Nullable EntityLivingBase placer, ItemStack stack) {
      super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof TileEntitySkull) {
         checkWitherSpawn(worldIn, pos, (TileEntitySkull)tileentity);
      }

   }

   public static void checkWitherSpawn(World p_196298_0_, BlockPos p_196298_1_, TileEntitySkull p_196298_2_) {
      Block block = p_196298_2_.getBlockState().getBlock();
      boolean flag = block == Blocks.WITHER_SKELETON_SKULL || block == Blocks.WITHER_SKELETON_WALL_SKULL;
      if (flag && p_196298_1_.getY() >= 2 && p_196298_0_.getDifficulty() != EnumDifficulty.PEACEFUL && !p_196298_0_.isRemote) {
         BlockPattern blockpattern = getOrCreateWitherFull();
         BlockPattern.PatternHelper blockpattern$patternhelper = blockpattern.match(p_196298_0_, p_196298_1_);
         if (blockpattern$patternhelper != null) {
            for(int i = 0; i < 3; ++i) {
               TileEntitySkull.disableDrop(p_196298_0_, blockpattern$patternhelper.translateOffset(i, 0, 0).getPos());
            }

            for(int k = 0; k < blockpattern.getPalmLength(); ++k) {
               for(int j = 0; j < blockpattern.getThumbLength(); ++j) {
                  p_196298_0_.setBlockState(blockpattern$patternhelper.translateOffset(k, j, 0).getPos(), Blocks.AIR.getDefaultState(), 2);
               }
            }

            BlockPos blockpos1 = blockpattern$patternhelper.translateOffset(1, 0, 0).getPos();
            EntityWither entitywither = new EntityWither(p_196298_0_);
            BlockPos blockpos = blockpattern$patternhelper.translateOffset(1, 2, 0).getPos();
            entitywither.setLocationAndAngles((double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.55D, (double)blockpos.getZ() + 0.5D, blockpattern$patternhelper.getForwards().getAxis() == EnumFacing.Axis.X ? 0.0F : 90.0F, 0.0F);
            entitywither.renderYawOffset = blockpattern$patternhelper.getForwards().getAxis() == EnumFacing.Axis.X ? 0.0F : 90.0F;
            entitywither.ignite();

            for(EntityPlayerMP entityplayermp : p_196298_0_.getEntitiesWithinAABB(EntityPlayerMP.class, entitywither.getBoundingBox().grow(50.0D))) {
               CriteriaTriggers.SUMMONED_ENTITY.trigger(entityplayermp, entitywither);
            }

            p_196298_0_.spawnEntity(entitywither);

            for(int l = 0; l < 120; ++l) {
               p_196298_0_.spawnParticle(Particles.ITEM_SNOWBALL, (double)blockpos1.getX() + p_196298_0_.rand.nextDouble(), (double)(blockpos1.getY() - 2) + p_196298_0_.rand.nextDouble() * 3.9D, (double)blockpos1.getZ() + p_196298_0_.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
            }

            for(int i1 = 0; i1 < blockpattern.getPalmLength(); ++i1) {
               for(int j1 = 0; j1 < blockpattern.getThumbLength(); ++j1) {
                  p_196298_0_.notifyNeighbors(blockpattern$patternhelper.translateOffset(i1, j1, 0).getPos(), Blocks.AIR);
               }
            }

         }
      }
   }

   public static boolean canSpawnMob(World p_196299_0_, BlockPos p_196299_1_, ItemStack p_196299_2_) {
      if (p_196299_2_.getItem() == Items.WITHER_SKELETON_SKULL && p_196299_1_.getY() >= 2 && p_196299_0_.getDifficulty() != EnumDifficulty.PEACEFUL && !p_196299_0_.isRemote) {
         return getOrCreateWitherBase().match(p_196299_0_, p_196299_1_) != null;
      } else {
         return false;
      }
   }

   protected static BlockPattern getOrCreateWitherFull() {
      if (witherPatternFull == null) {
         witherPatternFull = FactoryBlockPattern.start().aisle("^^^", "###", "~#~").where('#', BlockWorldState.hasState(BlockStateMatcher.forBlock(Blocks.SOUL_SAND))).where('^', BlockWorldState.hasState(BlockStateMatcher.forBlock(Blocks.WITHER_SKELETON_SKULL).or(BlockStateMatcher.forBlock(Blocks.WITHER_SKELETON_WALL_SKULL)))).where('~', BlockWorldState.hasState(BlockMaterialMatcher.forMaterial(Material.AIR))).build();
      }

      return witherPatternFull;
   }

   protected static BlockPattern getOrCreateWitherBase() {
      if (witherPatternBase == null) {
         witherPatternBase = FactoryBlockPattern.start().aisle("   ", "###", "~#~").where('#', BlockWorldState.hasState(BlockStateMatcher.forBlock(Blocks.SOUL_SAND))).where('~', BlockWorldState.hasState(BlockMaterialMatcher.forMaterial(Material.AIR))).build();
      }

      return witherPatternBase;
   }
}