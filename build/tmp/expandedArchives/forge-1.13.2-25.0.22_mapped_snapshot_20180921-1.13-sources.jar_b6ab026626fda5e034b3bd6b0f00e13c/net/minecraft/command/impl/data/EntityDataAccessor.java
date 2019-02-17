package net.minecraft.command.impl.data;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.advancements.criterion.NBTPredicate;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.NBTPathArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class EntityDataAccessor implements IDataAccessor {
   private static final SimpleCommandExceptionType DATA_ENTITY_INVALID = new SimpleCommandExceptionType(new TextComponentTranslation("commands.data.entity.invalid"));
   public static final DataCommand.IDataProvider DATA_PROVIDER = new DataCommand.IDataProvider() {
      public IDataAccessor func_198919_a(CommandContext<CommandSource> p_198919_1_) throws CommandSyntaxException {
         return new EntityDataAccessor(EntityArgument.getSingleEntity(p_198919_1_, "target"));
      }

      public ArgumentBuilder<CommandSource, ?> func_198920_a(ArgumentBuilder<CommandSource, ?> p_198920_1_, Function<ArgumentBuilder<CommandSource, ?>, ArgumentBuilder<CommandSource, ?>> p_198920_2_) {
         return p_198920_1_.then(Commands.literal("entity").then(p_198920_2_.apply(Commands.argument("target", EntityArgument.singleEntity()))));
      }
   };
   private final Entity entity;

   public EntityDataAccessor(Entity entityIn) {
      this.entity = entityIn;
   }

   public void mergeData(NBTTagCompound other) throws CommandSyntaxException {
      if (this.entity instanceof EntityPlayer) {
         throw DATA_ENTITY_INVALID.create();
      } else {
         UUID uuid = this.entity.getUniqueID();
         this.entity.read(other);
         this.entity.setUniqueId(uuid);
      }
   }

   public NBTTagCompound getData() {
      return NBTPredicate.writeToNBTWithSelectedItem(this.entity);
   }

   public ITextComponent getModifiedMessage() {
      return new TextComponentTranslation("commands.data.entity.modified", this.entity.getDisplayName());
   }

   public ITextComponent getQueryMessage(INBTBase p_198924_1_) {
      return new TextComponentTranslation("commands.data.entity.query", this.entity.getDisplayName(), p_198924_1_.toFormattedComponent());
   }

   public ITextComponent getGetMessage(NBTPathArgument.NBTPath p_198922_1_, double p_198922_2_, int p_198922_4_) {
      return new TextComponentTranslation("commands.data.entity.get", p_198922_1_, this.entity.getDisplayName(), String.format(Locale.ROOT, "%.2f", p_198922_2_), p_198922_4_);
   }
}