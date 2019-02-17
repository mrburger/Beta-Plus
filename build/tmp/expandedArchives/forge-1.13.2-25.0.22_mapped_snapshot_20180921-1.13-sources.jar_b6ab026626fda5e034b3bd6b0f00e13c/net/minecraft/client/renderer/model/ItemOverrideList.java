package net.minecraft.client.renderer.model;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemOverrideList {
   public static final ItemOverrideList EMPTY = new ItemOverrideList();
   private final List<ItemOverride> overrides = Lists.newArrayList();
   private final List<IBakedModel> overrideBakedModels;

   protected ItemOverrideList() {
      this.overrideBakedModels = Collections.emptyList();
   }

   // FORGE: No reason for first param to be limited to ModelBlock
   public ItemOverrideList(IUnbakedModel model, Function<ResourceLocation, IUnbakedModel> modelGetter, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, List<ItemOverride> overridesIn) {
      this.overrideBakedModels = overridesIn.stream().map((p_209580_3_) -> {
         IUnbakedModel iunbakedmodel = modelGetter.apply(p_209580_3_.getLocation());
         return Objects.equals(iunbakedmodel, model) ? null : iunbakedmodel.bake(modelGetter, spriteGetter, ModelRotation.X0_Y0, false);
      }).collect(Collectors.toList());
      Collections.reverse(this.overrideBakedModels);

      for(int i = overridesIn.size() - 1; i >= 0; --i) {
         this.overrides.add(overridesIn.get(i));
      }

   }

   @Nullable
   public IBakedModel getModelWithOverrides(IBakedModel model, ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
      if (!this.overrides.isEmpty()) {
         for(int i = 0; i < this.overrides.size(); ++i) {
            ItemOverride itemoverride = this.overrides.get(i);
            if (itemoverride.matchesItemStack(stack, worldIn, entityIn)) {
               IBakedModel ibakedmodel = this.overrideBakedModels.get(i);
               if (ibakedmodel == null) {
                  return model;
               }

               return ibakedmodel;
            }
         }
      }

      return model;
   }

   public com.google.common.collect.ImmutableList<ItemOverride> getOverrides() {
      return com.google.common.collect.ImmutableList.copyOf(overrides);
   }
}