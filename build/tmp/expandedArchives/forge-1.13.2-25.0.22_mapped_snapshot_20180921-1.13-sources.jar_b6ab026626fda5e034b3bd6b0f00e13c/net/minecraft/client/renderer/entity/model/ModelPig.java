package net.minecraft.client.renderer.entity.model;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelPig extends ModelQuadruped {
   public ModelPig() {
      this(0.0F);
   }

   public ModelPig(float scale) {
      super(6, scale);
      this.head.setTextureOffset(16, 16).addBox(-2.0F, 0.0F, -9.0F, 4, 3, 1, scale);
      this.childYOffset = 4.0F;
   }
}