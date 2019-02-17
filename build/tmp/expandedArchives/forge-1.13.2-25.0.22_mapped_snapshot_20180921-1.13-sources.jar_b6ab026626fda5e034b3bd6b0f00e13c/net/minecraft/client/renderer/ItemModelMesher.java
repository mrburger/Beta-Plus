package net.minecraft.client.renderer;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemModelMesher {
   private final Int2ObjectMap<ModelResourceLocation> modelLocations = new Int2ObjectOpenHashMap<>(256);
   private final Int2ObjectMap<IBakedModel> itemModels = new Int2ObjectOpenHashMap<>(256);
   private final ModelManager modelManager;

   public ItemModelMesher(ModelManager modelManager) {
      this.modelManager = modelManager;
   }

   public TextureAtlasSprite getParticleIcon(IItemProvider itemProvider) {
      return this.getParticleIcon(new ItemStack(itemProvider));
   }

   public TextureAtlasSprite getParticleIcon(ItemStack stack) {
      IBakedModel ibakedmodel = this.getItemModel(stack);
      // FORGE: Make sure to call the item overrides
      return (ibakedmodel == this.modelManager.getMissingModel() || ibakedmodel.isBuiltInRenderer()) && stack.getItem() instanceof ItemBlock ? this.modelManager.getBlockModelShapes().getTexture(((ItemBlock)stack.getItem()).getBlock().getDefaultState()) : ibakedmodel.getOverrides().getModelWithOverrides(ibakedmodel, stack, null, null).getParticleTexture();
   }

   public IBakedModel getItemModel(ItemStack stack) {
      IBakedModel ibakedmodel = this.getItemModel(stack.getItem());
      return ibakedmodel == null ? this.modelManager.getMissingModel() : ibakedmodel;
   }

   @Nullable
   public IBakedModel getItemModel(Item itemIn) {
      return this.itemModels.get(getIndex(itemIn));
   }

   private static int getIndex(Item itemIn) {
      return Item.getIdFromItem(itemIn);
   }

   public void register(Item itemIn, ModelResourceLocation modelLocation) {
      this.modelLocations.put(getIndex(itemIn), modelLocation);
      this.itemModels.put(getIndex(itemIn), this.modelManager.getModel(modelLocation));
   }

   public ModelManager getModelManager() {
      return this.modelManager;
   }

   public void rebuildCache() {
      this.itemModels.clear();

      for(Entry<Integer, ModelResourceLocation> entry : this.modelLocations.entrySet()) {
         this.itemModels.put(entry.getKey(), this.modelManager.getModel(entry.getValue()));
      }

   }
}