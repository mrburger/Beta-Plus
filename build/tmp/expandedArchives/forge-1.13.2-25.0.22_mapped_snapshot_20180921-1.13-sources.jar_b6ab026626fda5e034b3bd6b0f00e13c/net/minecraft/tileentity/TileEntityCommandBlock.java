package net.minecraft.tileentity;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityCommandBlock extends TileEntity {
   private boolean powered;
   private boolean auto;
   private boolean conditionMet;
   private boolean sendToClient;
   private final CommandBlockBaseLogic commandBlockLogic = new CommandBlockBaseLogic() {
      /**
       * Sets the command.
       */
      public void setCommand(String command) {
         super.setCommand(command);
         TileEntityCommandBlock.this.markDirty();
      }

      public WorldServer getWorld() {
         return (WorldServer)TileEntityCommandBlock.this.world;
      }

      public void updateCommand() {
         IBlockState iblockstate = TileEntityCommandBlock.this.world.getBlockState(TileEntityCommandBlock.this.pos);
         this.getWorld().notifyBlockUpdate(TileEntityCommandBlock.this.pos, iblockstate, iblockstate, 3);
      }

      @OnlyIn(Dist.CLIENT)
      public Vec3d getPositionVector() {
         return new Vec3d((double)TileEntityCommandBlock.this.pos.getX() + 0.5D, (double)TileEntityCommandBlock.this.pos.getY() + 0.5D, (double)TileEntityCommandBlock.this.pos.getZ() + 0.5D);
      }

      public CommandSource getCommandSource() {
         return new CommandSource(this, new Vec3d((double)TileEntityCommandBlock.this.pos.getX() + 0.5D, (double)TileEntityCommandBlock.this.pos.getY() + 0.5D, (double)TileEntityCommandBlock.this.pos.getZ() + 0.5D), Vec2f.ZERO, this.getWorld(), 2, this.getName().getString(), this.getName(), this.getWorld().getServer(), (Entity)null);
      }
   };

   public TileEntityCommandBlock() {
      super(TileEntityType.COMMAND_BLOCK);
   }

   public NBTTagCompound write(NBTTagCompound compound) {
      super.write(compound);
      this.commandBlockLogic.writeToNBT(compound);
      compound.setBoolean("powered", this.isPowered());
      compound.setBoolean("conditionMet", this.isConditionMet());
      compound.setBoolean("auto", this.isAuto());
      return compound;
   }

   public void read(NBTTagCompound compound) {
      super.read(compound);
      this.commandBlockLogic.readDataFromNBT(compound);
      this.powered = compound.getBoolean("powered");
      this.conditionMet = compound.getBoolean("conditionMet");
      this.setAuto(compound.getBoolean("auto"));
   }

   /**
    * Retrieves packet to send to the client whenever this Tile Entity is resynced via World.notifyBlockUpdate. For
    * modded TE's, this packet comes back to you clientside in {@link #onDataPacket}
    */
   @Nullable
   public SPacketUpdateTileEntity getUpdatePacket() {
      if (this.isSendToClient()) {
         this.setSendToClient(false);
         NBTTagCompound nbttagcompound = this.write(new NBTTagCompound());
         return new SPacketUpdateTileEntity(this.pos, 2, nbttagcompound);
      } else {
         return null;
      }
   }

   public boolean onlyOpsCanSetNbt() {
      return true;
   }

   public CommandBlockBaseLogic getCommandBlockLogic() {
      return this.commandBlockLogic;
   }

   public void setPowered(boolean poweredIn) {
      this.powered = poweredIn;
   }

   public boolean isPowered() {
      return this.powered;
   }

   public boolean isAuto() {
      return this.auto;
   }

   public void setAuto(boolean autoIn) {
      boolean flag = this.auto;
      this.auto = autoIn;
      if (!flag && autoIn && !this.powered && this.world != null && this.getMode() != TileEntityCommandBlock.Mode.SEQUENCE) {
         Block block = this.getBlockState().getBlock();
         if (block instanceof BlockCommandBlock) {
            this.setConditionMet();
            this.world.getPendingBlockTicks().scheduleTick(this.pos, block, block.tickRate(this.world));
         }
      }

   }

   public boolean isConditionMet() {
      return this.conditionMet;
   }

   public boolean setConditionMet() {
      this.conditionMet = true;
      if (this.isConditional()) {
         BlockPos blockpos = this.pos.offset(this.world.getBlockState(this.pos).get(BlockCommandBlock.FACING).getOpposite());
         if (this.world.getBlockState(blockpos).getBlock() instanceof BlockCommandBlock) {
            TileEntity tileentity = this.world.getTileEntity(blockpos);
            this.conditionMet = tileentity instanceof TileEntityCommandBlock && ((TileEntityCommandBlock)tileentity).getCommandBlockLogic().getSuccessCount() > 0;
         } else {
            this.conditionMet = false;
         }
      }

      return this.conditionMet;
   }

   public boolean isSendToClient() {
      return this.sendToClient;
   }

   public void setSendToClient(boolean p_184252_1_) {
      this.sendToClient = p_184252_1_;
   }

   public TileEntityCommandBlock.Mode getMode() {
      Block block = this.getBlockState().getBlock();
      if (block == Blocks.COMMAND_BLOCK) {
         return TileEntityCommandBlock.Mode.REDSTONE;
      } else if (block == Blocks.REPEATING_COMMAND_BLOCK) {
         return TileEntityCommandBlock.Mode.AUTO;
      } else {
         return block == Blocks.CHAIN_COMMAND_BLOCK ? TileEntityCommandBlock.Mode.SEQUENCE : TileEntityCommandBlock.Mode.REDSTONE;
      }
   }

   public boolean isConditional() {
      IBlockState iblockstate = this.world.getBlockState(this.getPos());
      return iblockstate.getBlock() instanceof BlockCommandBlock ? iblockstate.get(BlockCommandBlock.CONDITIONAL) : false;
   }

   /**
    * validates a tile entity
    */
   public void validate() {
      this.updateContainingBlockInfo();
      super.validate();
   }

   public static enum Mode {
      SEQUENCE,
      AUTO,
      REDSTONE;
   }
}