package net.minecraft.item.crafting;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public interface IRecipeSerializer<T extends IRecipe> {
   T read(ResourceLocation recipeId, JsonObject json);

   T read(ResourceLocation recipeId, PacketBuffer buffer);

   void write(PacketBuffer buffer, T recipe);

   @Deprecated //Modders, do not use, this is un-namespaced and thus could cause clashes.
   default String getId() {
       ResourceLocation name = getName(); //To keep compatibility with vanilla, anything in the "minecraft" namespace doesn't get a prefix.
       return name.getNamespace().equals("minecraft") ? name.getPath() : name.toString();
   }

   ResourceLocation getName();
}