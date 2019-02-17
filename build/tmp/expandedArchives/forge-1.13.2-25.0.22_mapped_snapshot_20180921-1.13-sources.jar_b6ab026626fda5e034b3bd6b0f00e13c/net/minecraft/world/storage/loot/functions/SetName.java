package net.minecraft.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public class SetName extends LootFunction {
   private final ITextComponent name;

   public SetName(LootCondition[] p_i48242_1_, @Nullable ITextComponent p_i48242_2_) {
      super(p_i48242_1_);
      this.name = p_i48242_2_;
   }

   public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
      if (this.name != null) {
         stack.setDisplayName(this.name);
      }

      return stack;
   }

   public static class Serializer extends LootFunction.Serializer<SetName> {
      public Serializer() {
         super(new ResourceLocation("set_name"), SetName.class);
      }

      public void serialize(JsonObject object, SetName functionClazz, JsonSerializationContext serializationContext) {
         if (functionClazz.name != null) {
            object.add("name", ITextComponent.Serializer.toJsonTree(functionClazz.name));
         }

      }

      public SetName deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootCondition[] conditionsIn) {
         ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(object.get("name"));
         return new SetName(conditionsIn, itextcomponent);
      }
   }
}