package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelPufferFishBig extends ModelBase {
   private final ModelRenderer field_203741_a;
   private final ModelRenderer field_203742_b;
   private final ModelRenderer field_203743_c;
   private final ModelRenderer field_203744_d;
   private final ModelRenderer field_203745_e;
   private final ModelRenderer field_203746_f;
   private final ModelRenderer field_203747_g;
   private final ModelRenderer field_203748_h;
   private final ModelRenderer field_203749_i;
   private final ModelRenderer field_203750_j;
   private final ModelRenderer field_203751_k;
   private final ModelRenderer field_203752_l;
   private final ModelRenderer field_203753_m;

   public ModelPufferFishBig() {
      this.textureWidth = 32;
      this.textureHeight = 32;
      int i = 22;
      this.field_203741_a = new ModelRenderer(this, 0, 0);
      this.field_203741_a.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8);
      this.field_203741_a.setRotationPoint(0.0F, 22.0F, 0.0F);
      this.field_203742_b = new ModelRenderer(this, 24, 0);
      this.field_203742_b.addBox(-2.0F, 0.0F, -1.0F, 2, 1, 2);
      this.field_203742_b.setRotationPoint(-4.0F, 15.0F, -2.0F);
      this.field_203743_c = new ModelRenderer(this, 24, 3);
      this.field_203743_c.addBox(0.0F, 0.0F, -1.0F, 2, 1, 2);
      this.field_203743_c.setRotationPoint(4.0F, 15.0F, -2.0F);
      this.field_203744_d = new ModelRenderer(this, 15, 17);
      this.field_203744_d.addBox(-4.0F, -1.0F, 0.0F, 8, 1, 0);
      this.field_203744_d.setRotationPoint(0.0F, 14.0F, -4.0F);
      this.field_203744_d.rotateAngleX = ((float)Math.PI / 4F);
      this.field_203745_e = new ModelRenderer(this, 14, 16);
      this.field_203745_e.addBox(-4.0F, -1.0F, 0.0F, 8, 1, 1);
      this.field_203745_e.setRotationPoint(0.0F, 14.0F, 0.0F);
      this.field_203746_f = new ModelRenderer(this, 23, 18);
      this.field_203746_f.addBox(-4.0F, -1.0F, 0.0F, 8, 1, 0);
      this.field_203746_f.setRotationPoint(0.0F, 14.0F, 4.0F);
      this.field_203746_f.rotateAngleX = (-(float)Math.PI / 4F);
      this.field_203747_g = new ModelRenderer(this, 5, 17);
      this.field_203747_g.addBox(-1.0F, -8.0F, 0.0F, 1, 8, 0);
      this.field_203747_g.setRotationPoint(-4.0F, 22.0F, -4.0F);
      this.field_203747_g.rotateAngleY = (-(float)Math.PI / 4F);
      this.field_203748_h = new ModelRenderer(this, 1, 17);
      this.field_203748_h.addBox(0.0F, -8.0F, 0.0F, 1, 8, 0);
      this.field_203748_h.setRotationPoint(4.0F, 22.0F, -4.0F);
      this.field_203748_h.rotateAngleY = ((float)Math.PI / 4F);
      this.field_203749_i = new ModelRenderer(this, 15, 20);
      this.field_203749_i.addBox(-4.0F, 0.0F, 0.0F, 8, 1, 0);
      this.field_203749_i.setRotationPoint(0.0F, 22.0F, -4.0F);
      this.field_203749_i.rotateAngleX = (-(float)Math.PI / 4F);
      this.field_203751_k = new ModelRenderer(this, 15, 20);
      this.field_203751_k.addBox(-4.0F, 0.0F, 0.0F, 8, 1, 0);
      this.field_203751_k.setRotationPoint(0.0F, 22.0F, 0.0F);
      this.field_203750_j = new ModelRenderer(this, 15, 20);
      this.field_203750_j.addBox(-4.0F, 0.0F, 0.0F, 8, 1, 0);
      this.field_203750_j.setRotationPoint(0.0F, 22.0F, 4.0F);
      this.field_203750_j.rotateAngleX = ((float)Math.PI / 4F);
      this.field_203752_l = new ModelRenderer(this, 9, 17);
      this.field_203752_l.addBox(-1.0F, -8.0F, 0.0F, 1, 8, 0);
      this.field_203752_l.setRotationPoint(-4.0F, 22.0F, 4.0F);
      this.field_203752_l.rotateAngleY = ((float)Math.PI / 4F);
      this.field_203753_m = new ModelRenderer(this, 9, 17);
      this.field_203753_m.addBox(0.0F, -8.0F, 0.0F, 1, 8, 0);
      this.field_203753_m.setRotationPoint(4.0F, 22.0F, 4.0F);
      this.field_203753_m.rotateAngleY = (-(float)Math.PI / 4F);
   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
      this.field_203741_a.render(scale);
      this.field_203742_b.render(scale);
      this.field_203743_c.render(scale);
      this.field_203744_d.render(scale);
      this.field_203745_e.render(scale);
      this.field_203746_f.render(scale);
      this.field_203747_g.render(scale);
      this.field_203748_h.render(scale);
      this.field_203749_i.render(scale);
      this.field_203751_k.render(scale);
      this.field_203750_j.render(scale);
      this.field_203752_l.render(scale);
      this.field_203753_m.render(scale);
   }

   /**
    * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
    * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how "far"
    * arms and legs can swing at most.
    */
   public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
      this.field_203742_b.rotateAngleZ = -0.2F + 0.4F * MathHelper.sin(ageInTicks * 0.2F);
      this.field_203743_c.rotateAngleZ = 0.2F - 0.4F * MathHelper.sin(ageInTicks * 0.2F);
   }
}