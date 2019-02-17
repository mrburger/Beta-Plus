package net.minecraft.entity.ai;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityAIBreakBlock extends EntityAIMoveToBlock {
   private final Block block;
   private final EntityLiving field_203118_g;
   private int breakingTime;

   public EntityAIBreakBlock(Block p_i48795_1_, EntityCreature p_i48795_2_, double p_i48795_3_, int p_i48795_5_) {
      super(p_i48795_2_, p_i48795_3_, 24, p_i48795_5_);
      this.block = p_i48795_1_;
      this.field_203118_g = p_i48795_2_;
   }

   /**
    * Returns whether the EntityAIBase should begin execution.
    */
   public boolean shouldExecute() {
      if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.field_203118_g.world, this.field_203118_g) || !this.field_203118_g.world.getBlockState(this.destinationBlock).canEntityDestroy(this.field_203118_g.world, this.destinationBlock, this.field_203118_g) || !net.minecraftforge.event.ForgeEventFactory.onEntityDestroyBlock(this.field_203118_g, this.destinationBlock, this.field_203118_g.world.getBlockState(this.destinationBlock))) {
         return false;
      } else {
         return this.field_203118_g.getRNG().nextInt(20) != 0 ? false : super.shouldExecute();
      }
   }

   protected int getRunDelay(EntityCreature p_203109_1_) {
      return 0;
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      return super.shouldContinueExecuting();
   }

   /**
    * Reset the task's internal state. Called when this task is interrupted by another one
    */
   public void resetTask() {
      super.resetTask();
      this.field_203118_g.fallDistance = 1.0F;
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      super.startExecuting();
      this.breakingTime = 0;
   }

   public void playBreakingSound(IWorld p_203114_1_, BlockPos p_203114_2_) {
   }

   public void playBrokenSound(World p_203116_1_, BlockPos p_203116_2_) {
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      super.tick();
      World world = this.field_203118_g.world;
      BlockPos blockpos = new BlockPos(this.field_203118_g);
      BlockPos blockpos1 = this.findTarget(blockpos, world);
      Random random = this.field_203118_g.getRNG();
      if (this.getIsAboveDestination() && blockpos1 != null) {
         if (this.breakingTime > 0) {
            this.field_203118_g.motionY = 0.3D;
            if (!world.isRemote) {
               double d0 = 0.08D;
               ((WorldServer)world).spawnParticle(new ItemParticleData(Particles.ITEM, new ItemStack(Items.EGG)), (double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.7D, (double)blockpos1.getZ() + 0.5D, 3, ((double)random.nextFloat() - 0.5D) * 0.08D, ((double)random.nextFloat() - 0.5D) * 0.08D, ((double)random.nextFloat() - 0.5D) * 0.08D, (double)0.15F);
            }
         }

         if (this.breakingTime % 2 == 0) {
            this.field_203118_g.motionY = -0.3D;
            if (this.breakingTime % 6 == 0) {
               this.playBreakingSound(world, this.destinationBlock);
            }
         }

         if (this.breakingTime > 60) {
            world.removeBlock(blockpos1);
            if (!world.isRemote) {
               for(int i = 0; i < 20; ++i) {
                  double d1 = random.nextGaussian() * 0.02D;
                  double d2 = random.nextGaussian() * 0.02D;
                  double d3 = random.nextGaussian() * 0.02D;
                  ((WorldServer)world).spawnParticle(Particles.POOF, (double)blockpos1.getX() + 0.5D, (double)blockpos1.getY(), (double)blockpos1.getZ() + 0.5D, 1, d1, d2, d3, (double)0.15F);
               }

               this.playBrokenSound(world, this.destinationBlock);
            }
         }

         ++this.breakingTime;
      }

   }

   @Nullable
   private BlockPos findTarget(BlockPos p_203115_1_, IBlockReader p_203115_2_) {
      if (p_203115_2_.getBlockState(p_203115_1_).getBlock() == this.block) {
         return p_203115_1_;
      } else {
         BlockPos[] ablockpos = new BlockPos[]{p_203115_1_.down(), p_203115_1_.west(), p_203115_1_.east(), p_203115_1_.north(), p_203115_1_.south(), p_203115_1_.down().down()};

         for(BlockPos blockpos : ablockpos) {
            if (p_203115_2_.getBlockState(blockpos).getBlock() == this.block) {
               return blockpos;
            }
         }

         return null;
      }
   }

   /**
    * Return true to set given position as destination
    */
   protected boolean shouldMoveTo(IWorldReaderBase worldIn, BlockPos pos) {
      Block block = worldIn.getBlockState(pos).getBlock();
      return block == this.block && worldIn.getBlockState(pos.up()).isAir() && worldIn.getBlockState(pos.up(2)).isAir();
   }
}