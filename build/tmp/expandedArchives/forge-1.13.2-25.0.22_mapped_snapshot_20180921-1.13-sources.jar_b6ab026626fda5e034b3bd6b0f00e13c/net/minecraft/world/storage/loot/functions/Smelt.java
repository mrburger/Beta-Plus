package net.minecraft.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Smelt extends LootFunction {
   private static final Logger LOGGER = LogManager.getLogger();

   public Smelt(LootCondition[] conditionsIn) {
      super(conditionsIn);
   }

   public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
      if (stack.isEmpty()) {
         return stack;
      } else {
         IRecipe irecipe = findMatchingRecipe(context, stack);
         if (irecipe != null) {
            ItemStack itemstack = irecipe.getRecipeOutput();
            if (!itemstack.isEmpty()) {
               ItemStack itemstack1 = itemstack.copy();
               itemstack1.setCount(stack.getCount());
               return itemstack1;
            }
         }

         LOGGER.warn("Couldn't smelt {} because there is no smelting recipe", (Object)stack);
         return stack;
      }
   }

   @Nullable
   public static IRecipe findMatchingRecipe(LootContext p_202880_0_, ItemStack p_202880_1_) {
      for(IRecipe irecipe : p_202880_0_.getWorld().getRecipeManager().getRecipes(net.minecraftforge.common.crafting.VanillaRecipeTypes.SMELTING)) {
         if (irecipe.getIngredients().get(0).test(p_202880_1_)) {
            return irecipe;
         }
      }

      return null;
   }

   public static class Serializer extends LootFunction.Serializer<Smelt> {
      protected Serializer() {
         super(new ResourceLocation("furnace_smelt"), Smelt.class);
      }

      public void serialize(JsonObject object, Smelt functionClazz, JsonSerializationContext serializationContext) {
      }

      public Smelt deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootCondition[] conditionsIn) {
         return new Smelt(conditionsIn);
      }
   }
}