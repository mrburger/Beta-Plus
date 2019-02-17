package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.server.SPacketRecipeBook;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerRecipeBook extends RecipeBook {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RecipeManager recipeManager;

   public ServerRecipeBook(RecipeManager p_i48175_1_) {
      this.recipeManager = p_i48175_1_;
   }

   public int add(Collection<IRecipe> p_197926_1_, EntityPlayerMP p_197926_2_) {
      List<ResourceLocation> list = Lists.newArrayList();
      int i = 0;

      for(IRecipe irecipe : p_197926_1_) {
         ResourceLocation resourcelocation = irecipe.getId();
         if (!this.recipes.contains(resourcelocation) && !irecipe.isDynamic()) {
            this.unlock(resourcelocation);
            this.markNew(resourcelocation);
            list.add(resourcelocation);
            CriteriaTriggers.RECIPE_UNLOCKED.trigger(p_197926_2_, irecipe);
            ++i;
         }
      }

      this.sendPacket(SPacketRecipeBook.State.ADD, p_197926_2_, list);
      return i;
   }

   public int remove(Collection<IRecipe> p_197925_1_, EntityPlayerMP p_197925_2_) {
      List<ResourceLocation> list = Lists.newArrayList();
      int i = 0;

      for(IRecipe irecipe : p_197925_1_) {
         ResourceLocation resourcelocation = irecipe.getId();
         if (this.recipes.contains(resourcelocation)) {
            this.lock(resourcelocation);
            list.add(resourcelocation);
            ++i;
         }
      }

      this.sendPacket(SPacketRecipeBook.State.REMOVE, p_197925_2_, list);
      return i;
   }

   private void sendPacket(SPacketRecipeBook.State state, EntityPlayerMP player, List<ResourceLocation> recipesIn) {
      player.connection.sendPacket(new SPacketRecipeBook(state, recipesIn, Collections.emptyList(), this.isGuiOpen, this.isFilteringCraftable, this.isFurnaceGuiOpen, this.isFurnaceFilteringCraftable));
   }

   public NBTTagCompound write() {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.setBoolean("isGuiOpen", this.isGuiOpen);
      nbttagcompound.setBoolean("isFilteringCraftable", this.isFilteringCraftable);
      nbttagcompound.setBoolean("isFurnaceGuiOpen", this.isFurnaceGuiOpen);
      nbttagcompound.setBoolean("isFurnaceFilteringCraftable", this.isFurnaceFilteringCraftable);
      NBTTagList nbttaglist = new NBTTagList();

      for(ResourceLocation resourcelocation : this.recipes) {
         nbttaglist.add((INBTBase)(new NBTTagString(resourcelocation.toString())));
      }

      nbttagcompound.setTag("recipes", nbttaglist);
      NBTTagList nbttaglist1 = new NBTTagList();

      for(ResourceLocation resourcelocation1 : this.newRecipes) {
         nbttaglist1.add((INBTBase)(new NBTTagString(resourcelocation1.toString())));
      }

      nbttagcompound.setTag("toBeDisplayed", nbttaglist1);
      return nbttagcompound;
   }

   public void read(NBTTagCompound tag) {
      this.isGuiOpen = tag.getBoolean("isGuiOpen");
      this.isFilteringCraftable = tag.getBoolean("isFilteringCraftable");
      this.isFurnaceGuiOpen = tag.getBoolean("isFurnaceGuiOpen");
      this.isFurnaceFilteringCraftable = tag.getBoolean("isFurnaceFilteringCraftable");
      NBTTagList nbttaglist = tag.getList("recipes", 8);

      for(int i = 0; i < nbttaglist.size(); ++i) {
         ResourceLocation resourcelocation = new ResourceLocation(nbttaglist.getString(i));
         IRecipe irecipe = this.recipeManager.getRecipe(resourcelocation);
         if (irecipe == null) {
            LOGGER.error("Tried to load unrecognized recipe: {} removed now.", (Object)resourcelocation);
         } else {
            this.unlock(irecipe);
         }
      }

      NBTTagList nbttaglist1 = tag.getList("toBeDisplayed", 8);

      for(int j = 0; j < nbttaglist1.size(); ++j) {
         ResourceLocation resourcelocation1 = new ResourceLocation(nbttaglist1.getString(j));
         IRecipe irecipe1 = this.recipeManager.getRecipe(resourcelocation1);
         if (irecipe1 == null) {
            LOGGER.error("Tried to load unrecognized recipe: {} removed now.", (Object)resourcelocation1);
         } else {
            this.markNew(irecipe1);
         }
      }

   }

   public void init(EntityPlayerMP player) {
      player.connection.sendPacket(new SPacketRecipeBook(SPacketRecipeBook.State.INIT, this.recipes, this.newRecipes, this.isGuiOpen, this.isFilteringCraftable, this.isFurnaceGuiOpen, this.isFurnaceFilteringCraftable));
   }
}