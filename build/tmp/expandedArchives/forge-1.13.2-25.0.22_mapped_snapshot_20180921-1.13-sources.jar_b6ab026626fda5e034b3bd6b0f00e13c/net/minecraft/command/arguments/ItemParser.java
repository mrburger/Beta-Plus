package net.minecraft.command.arguments;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.item.Item;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.state.IProperty;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextComponentTranslation;

public class ItemParser {
   public static final SimpleCommandExceptionType field_199838_a = new SimpleCommandExceptionType(new TextComponentTranslation("argument.item.tag.disallowed"));
   public static final DynamicCommandExceptionType field_197333_a = new DynamicCommandExceptionType((p_208696_0_) -> {
      return new TextComponentTranslation("argument.item.id.invalid", p_208696_0_);
   });
   private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> field_197334_b = SuggestionsBuilder::buildFuture;
   private final StringReader reader;
   private final boolean field_199839_e;
   private final Map<IProperty<?>, Comparable<?>> field_197336_d = Maps.newHashMap();
   private Item item;
   @Nullable
   private NBTTagCompound nbt;
   private ResourceLocation tag = new ResourceLocation("");
   private int field_201956_j;
   private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> field_197339_g = field_197334_b;

   public ItemParser(StringReader p_i48213_1_, boolean p_i48213_2_) {
      this.reader = p_i48213_1_;
      this.field_199839_e = p_i48213_2_;
   }

   public Item getItem() {
      return this.item;
   }

   @Nullable
   public NBTTagCompound getNbt() {
      return this.nbt;
   }

   public ResourceLocation getTag() {
      return this.tag;
   }

   public void func_197332_d() throws CommandSyntaxException {
      int i = this.reader.getCursor();
      ResourceLocation resourcelocation = ResourceLocation.read(this.reader);
      if (IRegistry.field_212630_s.func_212607_c(resourcelocation)) {
         this.item = IRegistry.field_212630_s.func_212608_b(resourcelocation);
      } else {
         this.reader.setCursor(i);
         throw field_197333_a.createWithContext(this.reader, resourcelocation.toString());
      }
   }

   public void func_199834_f() throws CommandSyntaxException {
      if (!this.field_199839_e) {
         throw field_199838_a.create();
      } else {
         this.field_197339_g = this::func_201955_c;
         this.reader.expect('#');
         this.field_201956_j = this.reader.getCursor();
         this.tag = ResourceLocation.read(this.reader);
      }
   }

   public void func_197330_e() throws CommandSyntaxException {
      this.nbt = (new JsonToNBT(this.reader)).readStruct();
   }

   public ItemParser parse() throws CommandSyntaxException {
      this.field_197339_g = this::func_197331_c;
      if (this.reader.canRead() && this.reader.peek() == '#') {
         this.func_199834_f();
      } else {
         this.func_197332_d();
         this.field_197339_g = this::func_197328_b;
      }

      if (this.reader.canRead() && this.reader.peek() == '{') {
         this.field_197339_g = field_197334_b;
         this.func_197330_e();
      }

      return this;
   }

   private CompletableFuture<Suggestions> func_197328_b(SuggestionsBuilder p_197328_1_) {
      if (p_197328_1_.getRemaining().isEmpty()) {
         p_197328_1_.suggest(String.valueOf('{'));
      }

      return p_197328_1_.buildFuture();
   }

   private CompletableFuture<Suggestions> func_201955_c(SuggestionsBuilder p_201955_1_) {
      return ISuggestionProvider.suggestIterable(ItemTags.getCollection().getRegisteredTags(), p_201955_1_.createOffset(this.field_201956_j));
   }

   private CompletableFuture<Suggestions> func_197331_c(SuggestionsBuilder p_197331_1_) {
      if (this.field_199839_e) {
         ISuggestionProvider.suggestIterable(ItemTags.getCollection().getRegisteredTags(), p_197331_1_, String.valueOf('#'));
      }

      return ISuggestionProvider.suggestIterable(IRegistry.field_212630_s.getKeys(), p_197331_1_);
   }

   public CompletableFuture<Suggestions> func_197329_a(SuggestionsBuilder p_197329_1_) {
      return this.field_197339_g.apply(p_197329_1_.createOffset(this.reader.getCursor()));
   }
}