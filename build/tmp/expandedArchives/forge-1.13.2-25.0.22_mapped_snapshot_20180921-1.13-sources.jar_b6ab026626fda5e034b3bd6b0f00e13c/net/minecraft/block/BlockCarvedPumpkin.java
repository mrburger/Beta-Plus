package net.minecraft.block;

import java.util.function.Predicate;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMaterialMatcher;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.block.state.pattern.BlockStateMatcher;
import net.minecraft.block.state.pattern.FactoryBlockPattern;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockCarvedPumpkin extends BlockHorizontal {
   public static final DirectionProperty FACING = BlockHorizontal.HORIZONTAL_FACING;
   private BlockPattern field_196361_b;
   private BlockPattern field_196362_c;
   private BlockPattern field_196363_y;
   private BlockPattern field_196364_z;
   private static final Predicate<IBlockState> IS_PUMPKIN = (p_210301_0_) -> {
      return p_210301_0_ != null && (p_210301_0_.getBlock() == Blocks.CARVED_PUMPKIN || p_210301_0_.getBlock() == Blocks.JACK_O_LANTERN);
   };

   protected BlockCarvedPumpkin(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.NORTH));
   }

   public void onBlockAdded(IBlockState state, World worldIn, BlockPos pos, IBlockState oldState) {
      if (oldState.getBlock() != state.getBlock()) {
         this.trySpawnGolem(worldIn, pos);
      }
   }

   public boolean canDispenserPlace(IWorldReaderBase p_196354_1_, BlockPos p_196354_2_) {
      return this.getSnowmanBasePattern().match(p_196354_1_, p_196354_2_) != null || this.getGolemBasePattern().match(p_196354_1_, p_196354_2_) != null;
   }

   private void trySpawnGolem(World p_196358_1_, BlockPos p_196358_2_) {
      BlockPattern.PatternHelper blockpattern$patternhelper = this.getSnowmanPattern().match(p_196358_1_, p_196358_2_);
      if (blockpattern$patternhelper != null) {
         for(int i = 0; i < this.getSnowmanPattern().getThumbLength(); ++i) {
            BlockWorldState blockworldstate = blockpattern$patternhelper.translateOffset(0, i, 0);
            p_196358_1_.setBlockState(blockworldstate.getPos(), Blocks.AIR.getDefaultState(), 2);
         }

         EntitySnowman entitysnowman = new EntitySnowman(p_196358_1_);
         BlockPos blockpos1 = blockpattern$patternhelper.translateOffset(0, 2, 0).getPos();
         entitysnowman.setLocationAndAngles((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.05D, (double)blockpos1.getZ() + 0.5D, 0.0F, 0.0F);
         p_196358_1_.spawnEntity(entitysnowman);

         for(EntityPlayerMP entityplayermp : p_196358_1_.getEntitiesWithinAABB(EntityPlayerMP.class, entitysnowman.getBoundingBox().grow(5.0D))) {
            CriteriaTriggers.SUMMONED_ENTITY.trigger(entityplayermp, entitysnowman);
         }

         int l = Block.getStateId(Blocks.SNOW_BLOCK.getDefaultState());
         p_196358_1_.playEvent(2001, blockpos1, l);
         p_196358_1_.playEvent(2001, blockpos1.up(), l);

         for(int k1 = 0; k1 < this.getSnowmanPattern().getThumbLength(); ++k1) {
            BlockWorldState blockworldstate1 = blockpattern$patternhelper.translateOffset(0, k1, 0);
            p_196358_1_.notifyNeighbors(blockworldstate1.getPos(), Blocks.AIR);
         }
      } else {
         blockpattern$patternhelper = this.getGolemPattern().match(p_196358_1_, p_196358_2_);
         if (blockpattern$patternhelper != null) {
            for(int j = 0; j < this.getGolemPattern().getPalmLength(); ++j) {
               for(int k = 0; k < this.getGolemPattern().getThumbLength(); ++k) {
                  p_196358_1_.setBlockState(blockpattern$patternhelper.translateOffset(j, k, 0).getPos(), Blocks.AIR.getDefaultState(), 2);
               }
            }

            BlockPos blockpos = blockpattern$patternhelper.translateOffset(1, 2, 0).getPos();
            EntityIronGolem entityirongolem = new EntityIronGolem(p_196358_1_);
            entityirongolem.setPlayerCreated(true);
            entityirongolem.setLocationAndAngles((double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.05D, (double)blockpos.getZ() + 0.5D, 0.0F, 0.0F);
            p_196358_1_.spawnEntity(entityirongolem);

            for(EntityPlayerMP entityplayermp1 : p_196358_1_.getEntitiesWithinAABB(EntityPlayerMP.class, entityirongolem.getBoundingBox().grow(5.0D))) {
               CriteriaTriggers.SUMMONED_ENTITY.trigger(entityplayermp1, entityirongolem);
            }

            for(int i1 = 0; i1 < 120; ++i1) {
               p_196358_1_.spawnParticle(Particles.ITEM_SNOWBALL, (double)blockpos.getX() + p_196358_1_.rand.nextDouble(), (double)blockpos.getY() + p_196358_1_.rand.nextDouble() * 3.9D, (double)blockpos.getZ() + p_196358_1_.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
            }

            for(int j1 = 0; j1 < this.getGolemPattern().getPalmLength(); ++j1) {
               for(int l1 = 0; l1 < this.getGolemPattern().getThumbLength(); ++l1) {
                  BlockWorldState blockworldstate2 = blockpattern$patternhelper.translateOffset(j1, l1, 0);
                  p_196358_1_.notifyNeighbors(blockworldstate2.getPos(), Blocks.AIR);
               }
            }
         }
      }

   }

   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(FACING);
   }

   protected BlockPattern getSnowmanBasePattern() {
      if (this.field_196361_b == null) {
         this.field_196361_b = FactoryBlockPattern.start().aisle(" ", "#", "#").where('#', BlockWorldState.hasState(BlockStateMatcher.forBlock(Blocks.SNOW_BLOCK))).build();
      }

      return this.field_196361_b;
   }

   protected BlockPattern getSnowmanPattern() {
      if (this.field_196362_c == null) {
         this.field_196362_c = FactoryBlockPattern.start().aisle("^", "#", "#").where('^', BlockWorldState.hasState(IS_PUMPKIN)).where('#', BlockWorldState.hasState(BlockStateMatcher.forBlock(Blocks.SNOW_BLOCK))).build();
      }

      return this.field_196362_c;
   }

   protected BlockPattern getGolemBasePattern() {
      if (this.field_196363_y == null) {
         this.field_196363_y = FactoryBlockPattern.start().aisle("~ ~", "###", "~#~").where('#', BlockWorldState.hasState(BlockStateMatcher.forBlock(Blocks.IRON_BLOCK))).where('~', BlockWorldState.hasState(BlockMaterialMatcher.forMaterial(Material.AIR))).build();
      }

      return this.field_196363_y;
   }

   protected BlockPattern getGolemPattern() {
      if (this.field_196364_z == null) {
         this.field_196364_z = FactoryBlockPattern.start().aisle("~^~", "###", "~#~").where('^', BlockWorldState.hasState(IS_PUMPKIN)).where('#', BlockWorldState.hasState(BlockStateMatcher.forBlock(Blocks.IRON_BLOCK))).where('~', BlockWorldState.hasState(BlockMaterialMatcher.forMaterial(Material.AIR))).build();
      }

      return this.field_196364_z;
   }
}