package net.minecraft.command.impl.data;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.NBTPathArgument;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class BlockDataAccessor implements IDataAccessor {
   private static final SimpleCommandExceptionType DATA_BLOCK_INVALID_EXCEPTION = new SimpleCommandExceptionType(new TextComponentTranslation("commands.data.block.invalid"));
   public static final DataCommand.IDataProvider DATA_PROVIDER = new DataCommand.IDataProvider() {
      public IDataAccessor func_198919_a(CommandContext<CommandSource> p_198919_1_) throws CommandSyntaxException {
         BlockPos blockpos = BlockPosArgument.getLoadedBlockPos(p_198919_1_, "pos");
         TileEntity tileentity = p_198919_1_.getSource().getWorld().getTileEntity(blockpos);
         if (tileentity == null) {
            throw BlockDataAccessor.DATA_BLOCK_INVALID_EXCEPTION.create();
         } else {
            return new BlockDataAccessor(tileentity, blockpos);
         }
      }

      public ArgumentBuilder<CommandSource, ?> func_198920_a(ArgumentBuilder<CommandSource, ?> p_198920_1_, Function<ArgumentBuilder<CommandSource, ?>, ArgumentBuilder<CommandSource, ?>> p_198920_2_) {
         return p_198920_1_.then(Commands.literal("block").then(p_198920_2_.apply(Commands.argument("pos", BlockPosArgument.blockPos()))));
      }
   };
   private final TileEntity tileEntity;
   private final BlockPos pos;

   public BlockDataAccessor(TileEntity tileEntityIn, BlockPos posIn) {
      this.tileEntity = tileEntityIn;
      this.pos = posIn;
   }

   public void mergeData(NBTTagCompound other) {
      other.setInt("x", this.pos.getX());
      other.setInt("y", this.pos.getY());
      other.setInt("z", this.pos.getZ());
      this.tileEntity.read(other);
      this.tileEntity.markDirty();
      IBlockState iblockstate = this.tileEntity.getWorld().getBlockState(this.pos);
      this.tileEntity.getWorld().notifyBlockUpdate(this.pos, iblockstate, iblockstate, 3);
   }

   public NBTTagCompound getData() {
      return this.tileEntity.write(new NBTTagCompound());
   }

   public ITextComponent getModifiedMessage() {
      return new TextComponentTranslation("commands.data.block.modified", this.pos.getX(), this.pos.getY(), this.pos.getZ());
   }

   public ITextComponent getQueryMessage(INBTBase p_198924_1_) {
      return new TextComponentTranslation("commands.data.block.query", this.pos.getX(), this.pos.getY(), this.pos.getZ(), p_198924_1_.toFormattedComponent());
   }

   public ITextComponent getGetMessage(NBTPathArgument.NBTPath p_198922_1_, double p_198922_2_, int p_198922_4_) {
      return new TextComponentTranslation("commands.data.block.get", p_198922_1_, this.pos.getX(), this.pos.getY(), this.pos.getZ(), String.format(Locale.ROOT, "%.2f", p_198922_2_), p_198922_4_);
   }
}