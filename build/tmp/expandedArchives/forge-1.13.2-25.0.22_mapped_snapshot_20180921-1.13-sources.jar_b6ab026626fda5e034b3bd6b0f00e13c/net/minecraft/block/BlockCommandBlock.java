package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockCommandBlock extends BlockContainer {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final DirectionProperty FACING = BlockDirectional.FACING;
   public static final BooleanProperty CONDITIONAL = BlockStateProperties.CONDITIONAL;

   public BlockCommandBlock(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(CONDITIONAL, Boolean.valueOf(false)));
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      TileEntityCommandBlock tileentitycommandblock = new TileEntityCommandBlock();
      tileentitycommandblock.setAuto(this == Blocks.CHAIN_COMMAND_BLOCK);
      return tileentitycommandblock;
   }

   /**
    * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
    * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
    * block, etc.
    */
   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (!worldIn.isRemote) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof TileEntityCommandBlock) {
            TileEntityCommandBlock tileentitycommandblock = (TileEntityCommandBlock)tileentity;
            boolean flag = worldIn.isBlockPowered(pos);
            boolean flag1 = tileentitycommandblock.isPowered();
            tileentitycommandblock.setPowered(flag);
            if (!flag1 && !tileentitycommandblock.isAuto() && tileentitycommandblock.getMode() != TileEntityCommandBlock.Mode.SEQUENCE) {
               if (flag) {
                  tileentitycommandblock.setConditionMet();
                  worldIn.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(worldIn));
               }

            }
         }
      }
   }

   public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      if (!worldIn.isRemote) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof TileEntityCommandBlock) {
            TileEntityCommandBlock tileentitycommandblock = (TileEntityCommandBlock)tileentity;
            CommandBlockBaseLogic commandblockbaselogic = tileentitycommandblock.getCommandBlockLogic();
            boolean flag = !StringUtils.isNullOrEmpty(commandblockbaselogic.getCommand());
            TileEntityCommandBlock.Mode tileentitycommandblock$mode = tileentitycommandblock.getMode();
            boolean flag1 = tileentitycommandblock.isConditionMet();
            if (tileentitycommandblock$mode == TileEntityCommandBlock.Mode.AUTO) {
               tileentitycommandblock.setConditionMet();
               if (flag1) {
                  this.execute(state, worldIn, pos, commandblockbaselogic, flag);
               } else if (tileentitycommandblock.isConditional()) {
                  commandblockbaselogic.setSuccessCount(0);
               }

               if (tileentitycommandblock.isPowered() || tileentitycommandblock.isAuto()) {
                  worldIn.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(worldIn));
               }
            } else if (tileentitycommandblock$mode == TileEntityCommandBlock.Mode.REDSTONE) {
               if (flag1) {
                  this.execute(state, worldIn, pos, commandblockbaselogic, flag);
               } else if (tileentitycommandblock.isConditional()) {
                  commandblockbaselogic.setSuccessCount(0);
               }
            }

            worldIn.updateComparatorOutputLevel(pos, this);
         }

      }
   }

   private void execute(IBlockState p_193387_1_, World p_193387_2_, BlockPos p_193387_3_, CommandBlockBaseLogic p_193387_4_, boolean p_193387_5_) {
      if (p_193387_5_) {
         p_193387_4_.trigger(p_193387_2_);
      } else {
         p_193387_4_.setSuccessCount(0);
      }

      executeChain(p_193387_2_, p_193387_3_, p_193387_1_.get(FACING));
   }

   /**
    * How many world ticks before ticking
    */
   public int tickRate(IWorldReaderBase worldIn) {
      return 1;
   }

   public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof TileEntityCommandBlock && player.canUseCommandBlock()) {
         player.openCommandBlock((TileEntityCommandBlock)tileentity);
         return true;
      } else {
         return false;
      }
   }

   /**
    * @deprecated call via {@link IBlockState#hasComparatorInputOverride()} whenever possible. Implementing/overriding
    * is fine.
    */
   public boolean hasComparatorInputOverride(IBlockState state) {
      return true;
   }

   /**
    * @deprecated call via {@link IBlockState#getComparatorInputOverride(World,BlockPos)} whenever possible.
    * Implementing/overriding is fine.
    */
   public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      return tileentity instanceof TileEntityCommandBlock ? ((TileEntityCommandBlock)tileentity).getCommandBlockLogic().getSuccessCount() : 0;
   }

   /**
    * Called by ItemBlocks after a block is set in the world, to allow post-place logic
    */
   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof TileEntityCommandBlock) {
         TileEntityCommandBlock tileentitycommandblock = (TileEntityCommandBlock)tileentity;
         CommandBlockBaseLogic commandblockbaselogic = tileentitycommandblock.getCommandBlockLogic();
         if (stack.hasDisplayName()) {
            commandblockbaselogic.setName(stack.getDisplayName());
         }

         if (!worldIn.isRemote) {
            if (stack.getChildTag("BlockEntityTag") == null) {
               commandblockbaselogic.setTrackOutput(worldIn.getGameRules().getBoolean("sendCommandFeedback"));
               tileentitycommandblock.setAuto(this == Blocks.CHAIN_COMMAND_BLOCK);
            }

            if (tileentitycommandblock.getMode() == TileEntityCommandBlock.Mode.SEQUENCE) {
               boolean flag = worldIn.isBlockPowered(pos);
               tileentitycommandblock.setPowered(flag);
            }
         }

      }
   }

   public int quantityDropped(IBlockState state, Random random) {
      return 0;
   }

   /**
    * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
    * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
    * @deprecated call via {@link IBlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
    */
   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.MODEL;
   }

   /**
    * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
    * fine.
    */
   public IBlockState rotate(IBlockState state, Rotation rot) {
      return state.with(FACING, rot.rotate(state.get(FACING)));
   }

   /**
    * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
    */
   public IBlockState mirror(IBlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.toRotation(state.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(FACING, CONDITIONAL);
   }

   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      return this.getDefaultState().with(FACING, context.getNearestLookingDirection().getOpposite());
   }

   private static void executeChain(World p_193386_0_, BlockPos p_193386_1_, EnumFacing p_193386_2_) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(p_193386_1_);
      GameRules gamerules = p_193386_0_.getGameRules();

      int i;
      IBlockState iblockstate;
      for(i = gamerules.getInt("maxCommandChainLength"); i-- > 0; p_193386_2_ = iblockstate.get(FACING)) {
         blockpos$mutableblockpos.move(p_193386_2_);
         iblockstate = p_193386_0_.getBlockState(blockpos$mutableblockpos);
         Block block = iblockstate.getBlock();
         if (block != Blocks.CHAIN_COMMAND_BLOCK) {
            break;
         }

         TileEntity tileentity = p_193386_0_.getTileEntity(blockpos$mutableblockpos);
         if (!(tileentity instanceof TileEntityCommandBlock)) {
            break;
         }

         TileEntityCommandBlock tileentitycommandblock = (TileEntityCommandBlock)tileentity;
         if (tileentitycommandblock.getMode() != TileEntityCommandBlock.Mode.SEQUENCE) {
            break;
         }

         if (tileentitycommandblock.isPowered() || tileentitycommandblock.isAuto()) {
            CommandBlockBaseLogic commandblockbaselogic = tileentitycommandblock.getCommandBlockLogic();
            if (tileentitycommandblock.setConditionMet()) {
               if (!commandblockbaselogic.trigger(p_193386_0_)) {
                  break;
               }

               p_193386_0_.updateComparatorOutputLevel(blockpos$mutableblockpos, block);
            } else if (tileentitycommandblock.isConditional()) {
               commandblockbaselogic.setSuccessCount(0);
            }
         }
      }

      if (i <= 0) {
         int j = Math.max(gamerules.getInt("maxCommandChainLength"), 0);
         LOGGER.warn("Command Block chain tried to execute more than {} steps!", (int)j);
      }

   }
}