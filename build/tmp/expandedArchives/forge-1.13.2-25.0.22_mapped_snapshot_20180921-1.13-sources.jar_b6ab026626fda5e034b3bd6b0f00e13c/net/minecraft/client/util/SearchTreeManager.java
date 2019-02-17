package net.minecraft.client.util;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SearchTreeManager implements IResourceManagerReloadListener {
   /** The item search tree, used for the creative inventory's search tab */
   public static final SearchTreeManager.Key<ItemStack> ITEMS = new SearchTreeManager.Key<>();
   /** The recipe search tree, used for the recipe book */
   public static final SearchTreeManager.Key<RecipeList> RECIPES = new SearchTreeManager.Key<>();
   /** Map of search tree keys to search trees */
   private final Map<SearchTreeManager.Key<?>, SearchTree<?>> trees = Maps.newHashMap();

   public void onResourceManagerReload(IResourceManager resourceManager) {
      for(SearchTree<?> searchtree : this.trees.values()) {
         searchtree.recalculate();
      }

   }

   public <T> void register(SearchTreeManager.Key<T> key, SearchTree<T> searchTreeIn) {
      this.trees.put(key, searchTreeIn);
   }

   /**
    * Gets the {@link ISearchTree} for the given search tree key, returning null if no such tree exists.
    */
   public <T> ISearchTree<T> get(SearchTreeManager.Key<T> key) {
      return (ISearchTree<T>) this.trees.get(key);
   }

   @Override
   public net.minecraftforge.resource.IResourceType getResourceType() {
      return net.minecraftforge.resource.VanillaResourceType.LANGUAGES;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Key<T> {
   }
}