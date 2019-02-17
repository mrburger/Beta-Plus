package net.minecraft.client.renderer.entity.model;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelTrident extends ModelBase {
   public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/trident.png");
   private final ModelRenderer modelRenderer;

   public ModelTrident() {
      this.textureWidth = 32;
      this.textureHeight = 32;
      this.modelRenderer = new ModelRenderer(this, 0, 0);
      this.modelRenderer.addBox(-0.5F, -4.0F, -0.5F, 1, 31, 1, 0.0F);
      ModelRenderer modelrenderer = new ModelRenderer(this, 4, 0);
      modelrenderer.addBox(-1.5F, 0.0F, -0.5F, 3, 2, 1);
      this.modelRenderer.addChild(modelrenderer);
      ModelRenderer modelrenderer1 = new ModelRenderer(this, 4, 3);
      modelrenderer1.addBox(-2.5F, -3.0F, -0.5F, 1, 4, 1);
      this.modelRenderer.addChild(modelrenderer1);
      ModelRenderer modelrenderer2 = new ModelRenderer(this, 4, 3);
      modelrenderer2.mirror = true;
      modelrenderer2.addBox(1.5F, -3.0F, -0.5F, 1, 4, 1);
      this.modelRenderer.addChild(modelrenderer2);
   }

   public void renderer() {
      this.modelRenderer.render(0.0625F);
   }
}