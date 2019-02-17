package net.minecraft.client.renderer;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.state.IProperty;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockModelShapes {
   private final Map<IBlockState, IBakedModel> bakedModelStore = Maps.newIdentityHashMap();
   private final ModelManager modelManager;

   public BlockModelShapes(ModelManager manager) {
      this.modelManager = manager;
   }

   public TextureAtlasSprite getTexture(IBlockState state) {
      return this.getModel(state).getParticleTexture();
   }

   public IBakedModel getModel(IBlockState state) {
      IBakedModel ibakedmodel = this.bakedModelStore.get(state);
      if (ibakedmodel == null) {
         ibakedmodel = this.modelManager.getMissingModel();
      }

      return ibakedmodel;
   }

   public ModelManager getModelManager() {
      return this.modelManager;
   }

   public void reloadModels() {
      this.bakedModelStore.clear();

      for(Block block : IRegistry.field_212618_g) {
         block.getStateContainer().getValidStates().forEach((p_209551_1_) -> {
            IBakedModel ibakedmodel = this.bakedModelStore.put(p_209551_1_, this.modelManager.getModel(getModelLocation(p_209551_1_)));
         });
      }

   }

   public static ModelResourceLocation getModelLocation(IBlockState state) {
      return getModelLocation(IRegistry.field_212618_g.getKey(state.getBlock()), state);
   }

   public static ModelResourceLocation getModelLocation(ResourceLocation location, IBlockState state) {
      return new ModelResourceLocation(location, getPropertyMapString(state.getValues()));
   }

   public static String getPropertyMapString(Map<IProperty<?>, Comparable<?>> propertyValues) {
      StringBuilder stringbuilder = new StringBuilder();

      for(Entry<IProperty<?>, Comparable<?>> entry : propertyValues.entrySet()) {
         if (stringbuilder.length() != 0) {
            stringbuilder.append(',');
         }

         IProperty<?> iproperty = entry.getKey();
         stringbuilder.append(iproperty.getName());
         stringbuilder.append('=');
         stringbuilder.append(getPropertyValueString(iproperty, entry.getValue()));
      }

      return stringbuilder.toString();
   }

   private static <T extends Comparable<T>> String getPropertyValueString(IProperty<T> property, Comparable<?> value) {
      return property.getName((T)value);
   }
}