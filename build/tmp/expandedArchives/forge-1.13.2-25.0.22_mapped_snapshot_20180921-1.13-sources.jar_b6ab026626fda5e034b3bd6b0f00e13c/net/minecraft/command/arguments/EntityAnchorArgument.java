package net.minecraft.command.arguments;

import com.google.common.collect.Maps;
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
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.Entity;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;

public class EntityAnchorArgument implements ArgumentType<EntityAnchorArgument.Type> {
   private static final Collection<String> EXMAPLES = Arrays.asList("eyes", "feet");
   private static final DynamicCommandExceptionType field_201025_a = new DynamicCommandExceptionType((p_208661_0_) -> {
      return new TextComponentTranslation("argument.anchor.invalid", p_208661_0_);
   });

   public static EntityAnchorArgument.Type getEntityAnchor(CommandContext<CommandSource> context, String name) {
      return context.getArgument(name, EntityAnchorArgument.Type.class);
   }

   public static EntityAnchorArgument entityAnchor() {
      return new EntityAnchorArgument();
   }

   public EntityAnchorArgument.Type parse(StringReader p_parse_1_) throws CommandSyntaxException {
      int i = p_parse_1_.getCursor();
      String s = p_parse_1_.readUnquotedString();
      EntityAnchorArgument.Type entityanchorargument$type = EntityAnchorArgument.Type.getByName(s);
      if (entityanchorargument$type == null) {
         p_parse_1_.setCursor(i);
         throw field_201025_a.createWithContext(p_parse_1_, s);
      } else {
         return entityanchorargument$type;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      return ISuggestionProvider.suggest(EntityAnchorArgument.Type.field_201020_c.keySet(), p_listSuggestions_2_);
   }

   public Collection<String> getExamples() {
      return EXMAPLES;
   }

   public static enum Type {
      FEET("feet", (p_201019_0_, p_201019_1_) -> {
         return p_201019_0_;
      }),
      EYES("eyes", (p_201018_0_, p_201018_1_) -> {
         return new Vec3d(p_201018_0_.x, p_201018_0_.y + (double)p_201018_1_.getEyeHeight(), p_201018_0_.z);
      });

      private static final Map<String, EntityAnchorArgument.Type> field_201020_c = Util.make(Maps.newHashMap(), (p_209384_0_) -> {
         for(EntityAnchorArgument.Type entityanchorargument$type : values()) {
            p_209384_0_.put(entityanchorargument$type.field_201021_d, entityanchorargument$type);
         }

      });
      private final String field_201021_d;
      private final BiFunction<Vec3d, Entity, Vec3d> field_201022_e;

      private Type(String p_i48597_3_, BiFunction<Vec3d, Entity, Vec3d> p_i48597_4_) {
         this.field_201021_d = p_i48597_3_;
         this.field_201022_e = p_i48597_4_;
      }

      @Nullable
      public static EntityAnchorArgument.Type getByName(String p_201016_0_) {
         return field_201020_c.get(p_201016_0_);
      }

      public Vec3d apply(Entity p_201017_1_) {
         return this.field_201022_e.apply(new Vec3d(p_201017_1_.posX, p_201017_1_.posY, p_201017_1_.posZ), p_201017_1_);
      }

      public Vec3d apply(CommandSource p_201015_1_) {
         Entity entity = p_201015_1_.getEntity();
         return entity == null ? p_201015_1_.getPos() : this.field_201022_e.apply(p_201015_1_.getPos(), entity);
      }
   }
}