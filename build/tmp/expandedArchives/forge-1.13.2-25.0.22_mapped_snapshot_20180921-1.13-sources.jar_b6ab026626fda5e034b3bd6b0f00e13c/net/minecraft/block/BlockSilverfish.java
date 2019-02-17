package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockSilverfish extends Block {
   private final Block mimickedBlock;
   private static final Map<Block, Block> field_196470_b = Maps.newIdentityHashMap();

   public BlockSilverfish(Block p_i48374_1_, Block.Properties p_i48374_2_) {
      super(p_i48374_2_);
      this.mimickedBlock = p_i48374_1_;
      field_196470_b.put(p_i48374_1_, this);
   }

   public int quantityDropped(IBlockState state, Random random) {
      return 0;
   }

   public Block getMimickedBlock() {
      return this.mimickedBlock;
   }

   public static boolean canContainSilverfish(IBlockState p_196466_0_) {
      return field_196470_b.containsKey(p_196466_0_.getBlock());
   }

   protected ItemStack getSilkTouchDrop(IBlockState state) {
      return new ItemStack(this.mimickedBlock);
   }

   public void dropBlockAsItemWithChance(IBlockState state, World worldIn, BlockPos pos, float chancePerItem, int fortune) {
      if (!worldIn.isRemote && worldIn.getGameRules().getBoolean("doTileDrops")) {
         EntitySilverfish entitysilverfish = new EntitySilverfish(worldIn);
         entitysilverfish.setLocationAndAngles((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, 0.0F, 0.0F);
         worldIn.spawnEntity(entitysilverfish);
         entitysilverfish.spawnExplosionParticle();
      }

   }

   public static IBlockState infest(Block p_196467_0_) {
      return field_196470_b.get(p_196467_0_).getDefaultState();
   }
}