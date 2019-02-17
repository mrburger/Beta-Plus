package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderDrowned;
import net.minecraft.client.renderer.entity.model.ModelDrowned;
import net.minecraft.entity.monster.EntityDrowned;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerDrownedOuter implements LayerRenderer<EntityDrowned> {
   private static final ResourceLocation field_204721_a = new ResourceLocation("textures/entity/zombie/drowned_outer_layer.png");
   private final RenderDrowned field_204722_b;
   private final ModelDrowned field_204723_c = new ModelDrowned(0.25F, 0.0F, 64, 64);

   public LayerDrownedOuter(RenderDrowned p_i48905_1_) {
      this.field_204722_b = p_i48905_1_;
   }

   public void render(EntityDrowned entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (!entitylivingbaseIn.isInvisible()) {
         this.field_204723_c.setModelAttributes(this.field_204722_b.getMainModel());
         this.field_204723_c.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.field_204722_b.bindTexture(field_204721_a);
         this.field_204723_c.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      }
   }

   public boolean shouldCombineTextures() {
      return true;
   }
}