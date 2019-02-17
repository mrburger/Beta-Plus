package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.IProperty;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

public class BlockPredicateArgument implements ArgumentType<BlockPredicateArgument.IResult> {
   private static final Collection<String> field_201331_a = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "#stone", "#stone[foo=bar]{baz=nbt}");
   private static final DynamicCommandExceptionType field_199826_a = new DynamicCommandExceptionType((p_208682_0_) -> {
      return new TextComponentTranslation("arguments.block.tag.unknown", p_208682_0_);
   });

   public static BlockPredicateArgument blockPredicateArgument() {
      return new BlockPredicateArgument();
   }

   public BlockPredicateArgument.IResult parse(StringReader p_parse_1_) throws CommandSyntaxException {
      BlockStateParser blockstateparser = (new BlockStateParser(p_parse_1_, true)).parse(true);
      if (blockstateparser.getState() != null) {
         BlockPredicateArgument.BlockPredicate blockpredicateargument$blockpredicate = new BlockPredicateArgument.BlockPredicate(blockstateparser.getState(), blockstateparser.getProperties().keySet(), blockstateparser.getNbt());
         return (p_199823_1_) -> {
            return blockpredicateargument$blockpredicate;
         };
      } else {
         ResourceLocation resourcelocation = blockstateparser.getTag();
         return (p_199822_2_) -> {
            Tag<Block> tag = p_199822_2_.getBlocks().get(resourcelocation);
            if (tag == null) {
               throw field_199826_a.create(resourcelocation.toString());
            } else {
               return new BlockPredicateArgument.TagPredicate(tag, blockstateparser.func_200139_j(), blockstateparser.getNbt());
            }
         };
      }
   }

   public static Predicate<BlockWorldState> getBlockPredicate(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
      return context.getArgument(name, BlockPredicateArgument.IResult.class).create(context.getSource().getServer().getNetworkTagManager());
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      StringReader stringreader = new StringReader(p_listSuggestions_2_.getInput());
      stringreader.setCursor(p_listSuggestions_2_.getStart());
      BlockStateParser blockstateparser = new BlockStateParser(stringreader, true);

      try {
         blockstateparser.parse(true);
      } catch (CommandSyntaxException var6) {
         ;
      }

      return blockstateparser.getSuggestions(p_listSuggestions_2_);
   }

   public Collection<String> getExamples() {
      return field_201331_a;
   }

   static class BlockPredicate implements Predicate<BlockWorldState> {
      private final IBlockState field_199817_a;
      private final Set<IProperty<?>> field_199818_b;
      @Nullable
      private final NBTTagCompound field_199819_c;

      public BlockPredicate(IBlockState p_i48210_1_, Set<IProperty<?>> p_i48210_2_, @Nullable NBTTagCompound p_i48210_3_) {
         this.field_199817_a = p_i48210_1_;
         this.field_199818_b = p_i48210_2_;
         this.field_199819_c = p_i48210_3_;
      }

      public boolean test(BlockWorldState p_test_1_) {
         IBlockState iblockstate = p_test_1_.getBlockState();
         if (iblockstate.getBlock() != this.field_199817_a.getBlock()) {
            return false;
         } else {
            for(IProperty<?> iproperty : this.field_199818_b) {
               if (iblockstate.get(iproperty) != this.field_199817_a.get(iproperty)) {
                  return false;
               }
            }

            if (this.field_199819_c == null) {
               return true;
            } else {
               TileEntity tileentity = p_test_1_.getTileEntity();
               return tileentity != null && NBTUtil.areNBTEquals(this.field_199819_c, tileentity.write(new NBTTagCompound()), true);
            }
         }
      }
   }

   public interface IResult {
      Predicate<BlockWorldState> create(NetworkTagManager p_create_1_) throws CommandSyntaxException;
   }

   static class TagPredicate implements Predicate<BlockWorldState> {
      private final Tag<Block> field_199820_a;
      @Nullable
      private final NBTTagCompound field_199821_b;
      private final Map<String, String> field_200133_c;

      private TagPredicate(Tag<Block> p_i48238_1_, Map<String, String> p_i48238_2_, @Nullable NBTTagCompound p_i48238_3_) {
         this.field_199820_a = p_i48238_1_;
         this.field_200133_c = p_i48238_2_;
         this.field_199821_b = p_i48238_3_;
      }

      public boolean test(BlockWorldState p_test_1_) {
         IBlockState iblockstate = p_test_1_.getBlockState();
         if (!iblockstate.isIn(this.field_199820_a)) {
            return false;
         } else {
            for(Entry<String, String> entry : this.field_200133_c.entrySet()) {
               IProperty<?> iproperty = iblockstate.getBlock().getStateContainer().getProperty(entry.getKey());
               if (iproperty == null) {
                  return false;
               }

               Comparable<?> comparable = (Comparable)iproperty.parseValue(entry.getValue()).orElse(null);
               if (comparable == null) {
                  return false;
               }

               if (iblockstate.get(iproperty) != comparable) {
                  return false;
               }
            }

            if (this.field_199821_b == null) {
               return true;
            } else {
               TileEntity tileentity = p_test_1_.getTileEntity();
               return tileentity != null && NBTUtil.areNBTEquals(this.field_199821_b, tileentity.write(new NBTTagCompound()), true);
            }
         }
      }
   }
}