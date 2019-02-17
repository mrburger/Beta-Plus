package net.minecraft.client.renderer.model.multipart;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBlockDefinition;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.model.MultipartBakedModel;
import net.minecraft.client.renderer.model.VariantList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Multipart implements IUnbakedModel {
   private final StateContainer<Block, IBlockState> stateContainer;
   private final List<Selector> selectors;

   public Multipart(StateContainer<Block, IBlockState> stateContainerIn, List<Selector> selectorsIn) {
      this.stateContainer = stateContainerIn;
      this.selectors = selectorsIn;
   }

   public List<Selector> getSelectors() {
      return this.selectors;
   }

   public Set<VariantList> getVariants() {
      Set<VariantList> set = Sets.newHashSet();

      for(Selector selector : this.selectors) {
         set.add(selector.getVariantList());
      }

      return set;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof Multipart)) {
         return false;
      } else {
         Multipart multipart = (Multipart)p_equals_1_;
         return Objects.equals(this.stateContainer, multipart.stateContainer) && Objects.equals(this.selectors, multipart.selectors);
      }
   }

   public int hashCode() {
      return Objects.hash(this.stateContainer, this.selectors);
   }

   public Collection<ResourceLocation> getOverrideLocations() {
      return this.getSelectors().stream().flatMap((p_209563_0_) -> {
         return p_209563_0_.getVariantList().getOverrideLocations().stream();
      }).collect(Collectors.toSet());
   }

   public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors) {
      return this.getSelectors().stream().flatMap((p_209562_2_) -> {
         return p_209562_2_.getVariantList().getTextures(modelGetter, missingTextureErrors).stream();
      }).collect(Collectors.toSet());
   }

   @Nullable
   @Override
   public IBakedModel bake(Function<ResourceLocation, IUnbakedModel> modelGetter, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, net.minecraftforge.common.model.IModelState rotationIn, boolean uvlock, net.minecraft.client.renderer.vertex.VertexFormat format) {
      MultipartBakedModel.Builder multipartbakedmodel$builder = new MultipartBakedModel.Builder();

      for(Selector selector : this.getSelectors()) {
         IBakedModel ibakedmodel = selector.getVariantList().bake(modelGetter, spriteGetter, rotationIn, uvlock, format);
         if (ibakedmodel != null) {
            multipartbakedmodel$builder.putModel(selector.getPredicate(this.stateContainer), ibakedmodel);
         }
      }

      return multipartbakedmodel$builder.build();
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<Multipart> {
      private final ModelBlockDefinition.ContainerHolder containerHolder;

      public Deserializer(ModelBlockDefinition.ContainerHolder containerHolderIn) {
         this.containerHolder = containerHolderIn;
      }

      public Multipart deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         return new Multipart(this.containerHolder.getStateContainer(), this.getSelectors(p_deserialize_3_, p_deserialize_1_.getAsJsonArray()));
      }

      private List<Selector> getSelectors(JsonDeserializationContext context, JsonArray elements) {
         List<Selector> list = Lists.newArrayList();

         for(JsonElement jsonelement : elements) {
            list.add(context.deserialize(jsonelement, Selector.class));
         }

         return list;
      }
   }
}