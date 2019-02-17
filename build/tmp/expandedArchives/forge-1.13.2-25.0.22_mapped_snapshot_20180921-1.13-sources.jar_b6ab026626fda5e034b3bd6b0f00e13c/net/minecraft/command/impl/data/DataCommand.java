package net.minecraft.command.impl.data;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.List;
import java.util.function.Function;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.NBTArgument;
import net.minecraft.command.arguments.NBTPathArgument;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCollection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;

public class DataCommand {
   private static final SimpleCommandExceptionType MERGE_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TextComponentTranslation("commands.data.merge.failed"));
   private static final DynamicCommandExceptionType GET_INVALID_EXCEPTION = new DynamicCommandExceptionType((p_208922_0_) -> {
      return new TextComponentTranslation("commands.data.get.invalid", p_208922_0_);
   });
   private static final DynamicCommandExceptionType GET_UNKNOWN_EXCEPTION = new DynamicCommandExceptionType((p_208919_0_) -> {
      return new TextComponentTranslation("commands.data.get.unknown", p_208919_0_);
   });
   public static final List<DataCommand.IDataProvider> DATA_PROVIDERS = Lists.newArrayList(EntityDataAccessor.DATA_PROVIDER, BlockDataAccessor.DATA_PROVIDER);

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      LiteralArgumentBuilder<CommandSource> literalargumentbuilder = Commands.literal("data").requires((p_198939_0_) -> {
         return p_198939_0_.hasPermissionLevel(2);
      });

      for(DataCommand.IDataProvider datacommand$idataprovider : DATA_PROVIDERS) {
         literalargumentbuilder.then(datacommand$idataprovider.func_198920_a(Commands.literal("merge"), (p_198943_1_) -> {
            return p_198943_1_.then(Commands.argument("nbt", NBTArgument.nbt()).executes((p_198936_1_) -> {
               return dataMergeAll(p_198936_1_.getSource(), datacommand$idataprovider.func_198919_a(p_198936_1_), NBTArgument.func_197130_a(p_198936_1_, "nbt"));
            }));
         })).then(datacommand$idataprovider.func_198920_a(Commands.literal("get"), (p_198940_1_) -> {
            return p_198940_1_.executes((p_198944_1_) -> {
               return dataGetAll(p_198944_1_.getSource(), datacommand$idataprovider.func_198919_a(p_198944_1_));
            }).then(Commands.argument("path", NBTPathArgument.nbtPath()).executes((p_198945_1_) -> {
               return dataGet(p_198945_1_.getSource(), datacommand$idataprovider.func_198919_a(p_198945_1_), NBTPathArgument.getNBTPath(p_198945_1_, "path"));
            }).then(Commands.argument("scale", DoubleArgumentType.doubleArg()).executes((p_198935_1_) -> {
               return func_198938_a(p_198935_1_.getSource(), datacommand$idataprovider.func_198919_a(p_198935_1_), NBTPathArgument.getNBTPath(p_198935_1_, "path"), DoubleArgumentType.getDouble(p_198935_1_, "scale"));
            })));
         })).then(datacommand$idataprovider.func_198920_a(Commands.literal("remove"), (p_198934_1_) -> {
            return p_198934_1_.then(Commands.argument("path", NBTPathArgument.nbtPath()).executes((p_198941_1_) -> {
               return dataMerge(p_198941_1_.getSource(), datacommand$idataprovider.func_198919_a(p_198941_1_), NBTPathArgument.getNBTPath(p_198941_1_, "path"));
            }));
         }));
      }

      dispatcher.register(literalargumentbuilder);
   }

   private static int dataMerge(CommandSource source, IDataAccessor accessor, NBTPathArgument.NBTPath pathIn) throws CommandSyntaxException {
      NBTTagCompound nbttagcompound = accessor.getData();
      NBTTagCompound nbttagcompound1 = nbttagcompound.copy();
      pathIn.func_197140_b(nbttagcompound);
      if (nbttagcompound1.equals(nbttagcompound)) {
         throw MERGE_FAILED_EXCEPTION.create();
      } else {
         accessor.mergeData(nbttagcompound);
         source.sendFeedback(accessor.getModifiedMessage(), true);
         return 1;
      }
   }

   private static int dataGet(CommandSource p_201228_0_, IDataAccessor p_201228_1_, NBTPathArgument.NBTPath p_201228_2_) throws CommandSyntaxException {
      INBTBase inbtbase = p_201228_2_.func_197143_a(p_201228_1_.getData());
      int i;
      if (inbtbase instanceof NBTPrimitive) {
         i = MathHelper.floor(((NBTPrimitive)inbtbase).getDouble());
      } else if (inbtbase instanceof NBTTagCollection) {
         i = ((NBTTagCollection)inbtbase).size();
      } else if (inbtbase instanceof NBTTagCompound) {
         i = ((NBTTagCompound)inbtbase).size();
      } else {
         if (!(inbtbase instanceof NBTTagString)) {
            throw GET_UNKNOWN_EXCEPTION.create(p_201228_2_.toString());
         }

         i = ((NBTTagString)inbtbase).getString().length();
      }

      p_201228_0_.sendFeedback(p_201228_1_.getQueryMessage(inbtbase), false);
      return i;
   }

   private static int func_198938_a(CommandSource p_198938_0_, IDataAccessor p_198938_1_, NBTPathArgument.NBTPath p_198938_2_, double p_198938_3_) throws CommandSyntaxException {
      INBTBase inbtbase = p_198938_2_.func_197143_a(p_198938_1_.getData());
      if (!(inbtbase instanceof NBTPrimitive)) {
         throw GET_INVALID_EXCEPTION.create(p_198938_2_.toString());
      } else {
         int i = MathHelper.floor(((NBTPrimitive)inbtbase).getDouble() * p_198938_3_);
         p_198938_0_.sendFeedback(p_198938_1_.getGetMessage(p_198938_2_, p_198938_3_, i), false);
         return i;
      }
   }

   private static int dataGetAll(CommandSource p_198947_0_, IDataAccessor p_198947_1_) throws CommandSyntaxException {
      p_198947_0_.sendFeedback(p_198947_1_.getQueryMessage(p_198947_1_.getData()), false);
      return 1;
   }

   private static int dataMergeAll(CommandSource p_198946_0_, IDataAccessor p_198946_1_, NBTTagCompound p_198946_2_) throws CommandSyntaxException {
      NBTTagCompound nbttagcompound = p_198946_1_.getData();
      NBTTagCompound nbttagcompound1 = nbttagcompound.copy().merge(p_198946_2_);
      if (nbttagcompound.equals(nbttagcompound1)) {
         throw MERGE_FAILED_EXCEPTION.create();
      } else {
         p_198946_1_.mergeData(nbttagcompound1);
         p_198946_0_.sendFeedback(p_198946_1_.getModifiedMessage(), true);
         return 1;
      }
   }

   public interface IDataProvider {
      IDataAccessor func_198919_a(CommandContext<CommandSource> p_198919_1_) throws CommandSyntaxException;

      ArgumentBuilder<CommandSource, ?> func_198920_a(ArgumentBuilder<CommandSource, ?> p_198920_1_, Function<ArgumentBuilder<CommandSource, ?>, ArgumentBuilder<CommandSource, ?>> p_198920_2_);
   }
}