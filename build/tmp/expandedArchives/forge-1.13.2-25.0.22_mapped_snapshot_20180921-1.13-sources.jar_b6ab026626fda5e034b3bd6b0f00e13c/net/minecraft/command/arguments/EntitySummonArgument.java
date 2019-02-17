package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextComponentTranslation;

public class EntitySummonArgument implements ArgumentType<ResourceLocation> {
   private static final Collection<String> field_211370_b = Arrays.asList("minecraft:pig", "cow");
   public static final DynamicCommandExceptionType field_211369_a = new DynamicCommandExceptionType((p_211367_0_) -> {
      return new TextComponentTranslation("entity.notFound", p_211367_0_);
   });

   public static EntitySummonArgument entitySummon() {
      return new EntitySummonArgument();
   }

   public static ResourceLocation getEntityId(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
      return checkIfEntityExists(context.getArgument(name, ResourceLocation.class));
   }

   private static final ResourceLocation checkIfEntityExists(ResourceLocation p_211365_0_) throws CommandSyntaxException {
      EntityType<?> entitytype = IRegistry.field_212629_r.func_212608_b(p_211365_0_);
      if (entitytype != null && entitytype.isSummonable()) {
         return p_211365_0_;
      } else {
         throw field_211369_a.create(p_211365_0_);
      }
   }

   public ResourceLocation parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return checkIfEntityExists(ResourceLocation.read(p_parse_1_));
   }

   public Collection<String> getExamples() {
      return field_211370_b;
   }
}