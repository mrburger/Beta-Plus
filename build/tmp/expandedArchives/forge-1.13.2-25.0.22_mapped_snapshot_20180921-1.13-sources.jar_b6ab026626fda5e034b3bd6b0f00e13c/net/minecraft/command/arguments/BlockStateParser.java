package net.minecraft.command.arguments;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextComponentTranslation;

public class BlockStateParser {
   public static final SimpleCommandExceptionType field_199831_a = new SimpleCommandExceptionType(new TextComponentTranslation("argument.block.tag.disallowed"));
   public static final DynamicCommandExceptionType field_197259_a = new DynamicCommandExceptionType((p_208687_0_) -> {
      return new TextComponentTranslation("argument.block.id.invalid", p_208687_0_);
   });
   public static final Dynamic2CommandExceptionType field_197260_b = new Dynamic2CommandExceptionType((p_208685_0_, p_208685_1_) -> {
      return new TextComponentTranslation("argument.block.property.unknown", p_208685_0_, p_208685_1_);
   });
   public static final Dynamic2CommandExceptionType field_197261_c = new Dynamic2CommandExceptionType((p_208690_0_, p_208690_1_) -> {
      return new TextComponentTranslation("argument.block.property.duplicate", p_208690_1_, p_208690_0_);
   });
   public static final Dynamic3CommandExceptionType field_197262_d = new Dynamic3CommandExceptionType((p_208684_0_, p_208684_1_, p_208684_2_) -> {
      return new TextComponentTranslation("argument.block.property.invalid", p_208684_0_, p_208684_2_, p_208684_1_);
   });
   public static final Dynamic2CommandExceptionType field_197263_e = new Dynamic2CommandExceptionType((p_208689_0_, p_208689_1_) -> {
      return new TextComponentTranslation("argument.block.property.novalue", p_208689_0_, p_208689_1_);
   });
   public static final SimpleCommandExceptionType field_197264_f = new SimpleCommandExceptionType(new TextComponentTranslation("argument.block.property.unclosed"));
   private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> SUGGEST_NONE = SuggestionsBuilder::buildFuture;
   private final StringReader reader;
   private final boolean tagsAllowed;
   private final Map<IProperty<?>, Comparable<?>> properties = Maps.newHashMap();
   /** Properties in string form */
   private final Map<String, String> stringProperties = Maps.newHashMap();
   private ResourceLocation blockID = new ResourceLocation("");
   private StateContainer<Block, IBlockState> blockStateContainer;
   private IBlockState blockState;
   @Nullable
   private NBTTagCompound tileEntityNBT;
   private ResourceLocation tag = new ResourceLocation("");
   private int cursorPos;
   private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestor = SUGGEST_NONE;

   public BlockStateParser(StringReader p_i48214_1_, boolean p_i48214_2_) {
      this.reader = p_i48214_1_;
      this.tagsAllowed = p_i48214_2_;
   }

   public Map<IProperty<?>, Comparable<?>> getProperties() {
      return this.properties;
   }

   @Nullable
   public IBlockState getState() {
      return this.blockState;
   }

   @Nullable
   public NBTTagCompound getNbt() {
      return this.tileEntityNBT;
   }

   @Nullable
   public ResourceLocation getTag() {
      return this.tag;
   }

   public BlockStateParser parse(boolean parseTileEntity) throws CommandSyntaxException {
      this.suggestor = this::suggestTagOrBlock;
      if (this.reader.canRead() && this.reader.peek() == '#') {
         this.readTag();
         this.suggestor = this::func_212599_i;
         if (this.reader.canRead() && this.reader.peek() == '[') {
            this.readStringProperties();
            this.suggestor = this::suggestNbt;
         }
      } else {
         this.readBlock();
         this.suggestor = this::suggestPropertyOrNbt;
         if (this.reader.canRead() && this.reader.peek() == '[') {
            this.readProperties();
            this.suggestor = this::suggestNbt;
         }
      }

      if (parseTileEntity && this.reader.canRead() && this.reader.peek() == '{') {
         this.suggestor = SUGGEST_NONE;
         this.readTileEntityNBT();
      }

      return this;
   }

   private CompletableFuture<Suggestions> suggestPropertyOrEnd(SuggestionsBuilder p_197252_1_) {
      if (p_197252_1_.getRemaining().isEmpty()) {
         p_197252_1_.suggest(String.valueOf(']'));
      }

      return this.suggestProperty(p_197252_1_);
   }

   private CompletableFuture<Suggestions> suggestStringPropertyOrEnd(SuggestionsBuilder p_200136_1_) {
      if (p_200136_1_.getRemaining().isEmpty()) {
         p_200136_1_.suggest(String.valueOf(']'));
      }

      return this.suggestStringProperty(p_200136_1_);
   }

   private CompletableFuture<Suggestions> suggestProperty(SuggestionsBuilder p_197256_1_) {
      String s = p_197256_1_.getRemaining().toLowerCase(Locale.ROOT);

      for(IProperty<?> iproperty : this.blockState.getProperties()) {
         if (!this.properties.containsKey(iproperty) && iproperty.getName().startsWith(s)) {
            p_197256_1_.suggest(iproperty.getName() + '=');
         }
      }

      return p_197256_1_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestStringProperty(SuggestionsBuilder p_200134_1_) {
      String s = p_200134_1_.getRemaining().toLowerCase(Locale.ROOT);
      if (this.tag != null && !this.tag.getPath().isEmpty()) {
         Tag<Block> tag = BlockTags.getCollection().get(this.tag);
         if (tag != null) {
            for(Block block : tag.getAllElements()) {
               for(IProperty<?> iproperty : block.getStateContainer().getProperties()) {
                  if (!this.stringProperties.containsKey(iproperty.getName()) && iproperty.getName().startsWith(s)) {
                     p_200134_1_.suggest(iproperty.getName() + '=');
                  }
               }
            }
         }
      }

      return p_200134_1_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestNbt(SuggestionsBuilder p_197244_1_) {
      if (p_197244_1_.getRemaining().isEmpty() && this.func_212598_k()) {
         p_197244_1_.suggest(String.valueOf('{'));
      }

      return p_197244_1_.buildFuture();
   }

   private boolean func_212598_k() {
      if (this.blockState != null) {
         return this.blockState.hasTileEntity();
      } else {
         if (this.tag != null) {
            Tag<Block> tag = BlockTags.getCollection().get(this.tag);
            if (tag != null) {
               for(Block block : tag.getAllElements()) {
                  if (block.getDefaultState().hasTileEntity()) {
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }

   private CompletableFuture<Suggestions> suggestEquals(SuggestionsBuilder p_197246_1_) {
      if (p_197246_1_.getRemaining().isEmpty()) {
         p_197246_1_.suggest(String.valueOf('='));
      }

      return p_197246_1_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestPropertyEndOrContinue(SuggestionsBuilder p_197248_1_) {
      if (p_197248_1_.getRemaining().isEmpty()) {
         p_197248_1_.suggest(String.valueOf(']'));
      }

      if (p_197248_1_.getRemaining().isEmpty() && this.properties.size() < this.blockState.getProperties().size()) {
         p_197248_1_.suggest(String.valueOf(','));
      }

      return p_197248_1_.buildFuture();
   }

   private static <T extends Comparable<T>> SuggestionsBuilder suggestValue(SuggestionsBuilder p_201037_0_, IProperty<T> p_201037_1_) {
      for(T t : p_201037_1_.getAllowedValues()) {
         if (t instanceof Integer) {
            p_201037_0_.suggest((Integer)t);
         } else {
            p_201037_0_.suggest(p_201037_1_.getName(t));
         }
      }

      return p_201037_0_;
   }

   private CompletableFuture<Suggestions> func_200140_a(SuggestionsBuilder p_200140_1_, String p_200140_2_) {
      boolean flag = false;
      if (this.tag != null && !this.tag.getPath().isEmpty()) {
         Tag<Block> tag = BlockTags.getCollection().get(this.tag);
         if (tag != null) {
            label40:
            for(Block block : tag.getAllElements()) {
               IProperty<?> iproperty = block.getStateContainer().getProperty(p_200140_2_);
               if (iproperty != null) {
                  suggestValue(p_200140_1_, iproperty);
               }

               if (!flag) {
                  Iterator iterator = block.getStateContainer().getProperties().iterator();

                  while(true) {
                     if (!iterator.hasNext()) {
                        continue label40;
                     }

                     IProperty<?> iproperty1 = (IProperty)iterator.next();
                     if (!this.stringProperties.containsKey(iproperty1.getName())) {
                        break;
                     }
                  }

                  flag = true;
               }
            }
         }
      }

      if (flag) {
         p_200140_1_.suggest(String.valueOf(','));
      }

      p_200140_1_.suggest(String.valueOf(']'));
      return p_200140_1_.buildFuture();
   }

   private CompletableFuture<Suggestions> func_212599_i(SuggestionsBuilder p_212599_1_) {
      if (p_212599_1_.getRemaining().isEmpty()) {
         Tag<Block> tag = BlockTags.getCollection().get(this.tag);
         if (tag != null) {
            boolean flag = false;
            boolean flag1 = false;

            for(Block block : tag.getAllElements()) {
               flag |= !block.getStateContainer().getProperties().isEmpty();
               flag1 |= block.hasTileEntity();
               if (flag && flag1) {
                  break;
               }
            }

            if (flag) {
               p_212599_1_.suggest(String.valueOf('['));
            }

            if (flag1) {
               p_212599_1_.suggest(String.valueOf('{'));
            }
         }
      }

      return this.suggestTag(p_212599_1_);
   }

   private CompletableFuture<Suggestions> suggestPropertyOrNbt(SuggestionsBuilder p_197255_1_) {
      if (p_197255_1_.getRemaining().isEmpty()) {
         if (!this.blockState.getBlock().getStateContainer().getProperties().isEmpty()) {
            p_197255_1_.suggest(String.valueOf('['));
         }

         if (this.blockState.hasTileEntity()) {
            p_197255_1_.suggest(String.valueOf('{'));
         }
      }

      return p_197255_1_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestTag(SuggestionsBuilder p_201953_1_) {
      return ISuggestionProvider.suggestIterable(BlockTags.getCollection().getRegisteredTags(), p_201953_1_.createOffset(this.cursorPos).add(p_201953_1_));
   }

   private CompletableFuture<Suggestions> suggestTagOrBlock(SuggestionsBuilder p_197250_1_) {
      if (this.tagsAllowed) {
         ISuggestionProvider.suggestIterable(BlockTags.getCollection().getRegisteredTags(), p_197250_1_, String.valueOf('#'));
      }

      ISuggestionProvider.suggestIterable(IRegistry.field_212618_g.getKeys(), p_197250_1_);
      return p_197250_1_.buildFuture();
   }

   public void readBlock() throws CommandSyntaxException {
      int i = this.reader.getCursor();
      this.blockID = ResourceLocation.read(this.reader);
      if (IRegistry.field_212618_g.func_212607_c(this.blockID)) {
         Block block = IRegistry.field_212618_g.get(this.blockID);
         this.blockStateContainer = block.getStateContainer();
         this.blockState = block.getDefaultState();
      } else {
         this.reader.setCursor(i);
         throw field_197259_a.createWithContext(this.reader, this.blockID.toString());
      }
   }

   public void readTag() throws CommandSyntaxException {
      if (!this.tagsAllowed) {
         throw field_199831_a.create();
      } else {
         this.suggestor = this::suggestTag;
         this.reader.expect('#');
         this.cursorPos = this.reader.getCursor();
         this.tag = ResourceLocation.read(this.reader);
      }
   }

   public void readProperties() throws CommandSyntaxException {
      this.reader.skip();
      this.suggestor = this::suggestPropertyOrEnd;
      this.reader.skipWhitespace();

      while(true) {
         if (this.reader.canRead() && this.reader.peek() != ']') {
            this.reader.skipWhitespace();
            int i = this.reader.getCursor();
            String s = this.reader.readString();
            IProperty<?> iproperty = this.blockStateContainer.getProperty(s);
            if (iproperty == null) {
               this.reader.setCursor(i);
               throw field_197260_b.createWithContext(this.reader, this.blockID.toString(), s);
            }

            if (this.properties.containsKey(iproperty)) {
               this.reader.setCursor(i);
               throw field_197261_c.createWithContext(this.reader, this.blockID.toString(), s);
            }

            this.reader.skipWhitespace();
            this.suggestor = this::suggestEquals;
            if (!this.reader.canRead() || this.reader.peek() != '=') {
               throw field_197263_e.createWithContext(this.reader, this.blockID.toString(), s);
            }

            this.reader.skip();
            this.reader.skipWhitespace();
            this.suggestor = (p_197251_1_) -> {
               return suggestValue(p_197251_1_, iproperty).buildFuture();
            };
            int j = this.reader.getCursor();
            this.parseValue(iproperty, this.reader.readString(), j);
            this.suggestor = this::suggestPropertyEndOrContinue;
            this.reader.skipWhitespace();
            if (!this.reader.canRead()) {
               continue;
            }

            if (this.reader.peek() == ',') {
               this.reader.skip();
               this.suggestor = this::suggestProperty;
               continue;
            }

            if (this.reader.peek() != ']') {
               throw field_197264_f.createWithContext(this.reader);
            }
         }

         if (this.reader.canRead()) {
            this.reader.skip();
            return;
         }

         throw field_197264_f.createWithContext(this.reader);
      }
   }

   public void readStringProperties() throws CommandSyntaxException {
      this.reader.skip();
      this.suggestor = this::suggestStringPropertyOrEnd;
      int i = -1;
      this.reader.skipWhitespace();

      while(true) {
         if (this.reader.canRead() && this.reader.peek() != ']') {
            this.reader.skipWhitespace();
            int j = this.reader.getCursor();
            String s = this.reader.readString();
            if (this.stringProperties.containsKey(s)) {
               this.reader.setCursor(j);
               throw field_197261_c.createWithContext(this.reader, this.blockID.toString(), s);
            }

            this.reader.skipWhitespace();
            if (!this.reader.canRead() || this.reader.peek() != '=') {
               this.reader.setCursor(j);
               throw field_197263_e.createWithContext(this.reader, this.blockID.toString(), s);
            }

            this.reader.skip();
            this.reader.skipWhitespace();
            this.suggestor = (p_200138_2_) -> {
               return this.func_200140_a(p_200138_2_, s);
            };
            i = this.reader.getCursor();
            String s1 = this.reader.readString();
            this.stringProperties.put(s, s1);
            this.reader.skipWhitespace();
            if (!this.reader.canRead()) {
               continue;
            }

            i = -1;
            if (this.reader.peek() == ',') {
               this.reader.skip();
               this.suggestor = this::suggestStringProperty;
               continue;
            }

            if (this.reader.peek() != ']') {
               throw field_197264_f.createWithContext(this.reader);
            }
         }

         if (this.reader.canRead()) {
            this.reader.skip();
            return;
         }

         if (i >= 0) {
            this.reader.setCursor(i);
         }

         throw field_197264_f.createWithContext(this.reader);
      }
   }

   public void readTileEntityNBT() throws CommandSyntaxException {
      this.tileEntityNBT = (new JsonToNBT(this.reader)).readStruct();
   }

   private <T extends Comparable<T>> void parseValue(IProperty<T> p_197253_1_, String p_197253_2_, int p_197253_3_) throws CommandSyntaxException {
      Optional<T> optional = p_197253_1_.parseValue(p_197253_2_);
      if (optional.isPresent()) {
         this.blockState = this.blockState.with(p_197253_1_, (T)(optional.get()));
         this.properties.put(p_197253_1_, optional.get());
      } else {
         this.reader.setCursor(p_197253_3_);
         throw field_197262_d.createWithContext(this.reader, this.blockID.toString(), p_197253_1_.getName(), p_197253_2_);
      }
   }

   public static String toString(IBlockState p_197247_0_, @Nullable NBTTagCompound p_197247_1_) {
      StringBuilder stringbuilder = new StringBuilder(IRegistry.field_212618_g.getKey(p_197247_0_.getBlock()).toString());
      if (!p_197247_0_.getProperties().isEmpty()) {
         stringbuilder.append('[');
         boolean flag = false;

         for(Entry<IProperty<?>, Comparable<?>> entry : p_197247_0_.getValues().entrySet()) {
            if (flag) {
               stringbuilder.append(',');
            }

            propValToString(stringbuilder, entry.getKey(), entry.getValue());
            flag = true;
         }

         stringbuilder.append(']');
      }

      if (p_197247_1_ != null) {
         stringbuilder.append((Object)p_197247_1_);
      }

      return stringbuilder.toString();
   }

   private static <T extends Comparable<T>> void propValToString(StringBuilder p_211375_0_, IProperty<T> p_211375_1_, Comparable<?> p_211375_2_) {
      p_211375_0_.append(p_211375_1_.getName());
      p_211375_0_.append('=');
      p_211375_0_.append(p_211375_1_.getName((T)p_211375_2_));
   }

   public CompletableFuture<Suggestions> getSuggestions(SuggestionsBuilder p_197245_1_) {
      return this.suggestor.apply(p_197245_1_.createOffset(this.reader.getCursor()));
   }

   public Map<String, String> func_200139_j() {
      return this.stringProperties;
   }
}