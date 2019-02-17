package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntityConduit;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntityConduitRenderer extends TileEntityRenderer<TileEntityConduit> {
   private static final ResourceLocation BASE_TEXTURE = new ResourceLocation("textures/entity/conduit/base.png");
   private static final ResourceLocation CAGE_TEXTURE = new ResourceLocation("textures/entity/conduit/cage.png");
   private static final ResourceLocation WIND_TEXTURE = new ResourceLocation("textures/entity/conduit/wind.png");
   private static final ResourceLocation VERTICAL_WIND_TEXTURE = new ResourceLocation("textures/entity/conduit/wind_vertical.png");
   private static final ResourceLocation OPEN_EYE_TEXTURE = new ResourceLocation("textures/entity/conduit/open_eye.png");
   private static final ResourceLocation CLOSED_EYE_TEXTURE = new ResourceLocation("textures/entity/conduit/closed_eye.png");
   private final ModelBase shellModel = new TileEntityConduitRenderer.ShellModel();
   private final ModelBase cageModel = new TileEntityConduitRenderer.CageModel();
   private final TileEntityConduitRenderer.WindModel windModel = new TileEntityConduitRenderer.WindModel();
   private final TileEntityConduitRenderer.EyeModel eyeModel = new TileEntityConduitRenderer.EyeModel();

   public void render(TileEntityConduit tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
      float f = (float)tileEntityIn.ticksExisted + partialTicks;
      if (!tileEntityIn.isActive()) {
         float f1 = tileEntityIn.getActiveRotation(0.0F);
         this.bindTexture(BASE_TEXTURE);
         GlStateManager.pushMatrix();
         GlStateManager.translatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
         GlStateManager.rotatef(f1, 0.0F, 1.0F, 0.0F);
         this.shellModel.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
         GlStateManager.popMatrix();
      } else if (tileEntityIn.isActive()) {
         float f3 = tileEntityIn.getActiveRotation(partialTicks) * (180F / (float)Math.PI);
         float f2 = MathHelper.sin(f * 0.1F) / 2.0F + 0.5F;
         f2 = f2 * f2 + f2;
         this.bindTexture(CAGE_TEXTURE);
         GlStateManager.disableCull();
         GlStateManager.pushMatrix();
         GlStateManager.translatef((float)x + 0.5F, (float)y + 0.3F + f2 * 0.2F, (float)z + 0.5F);
         GlStateManager.rotatef(f3, 0.5F, 1.0F, 0.5F);
         this.cageModel.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
         GlStateManager.popMatrix();
         int i = 3;
         int j = tileEntityIn.ticksExisted / 3 % TileEntityConduitRenderer.WindModel.field_205078_a;
         this.windModel.func_205077_a(j);
         int k = tileEntityIn.ticksExisted / (3 * TileEntityConduitRenderer.WindModel.field_205078_a) % 3;
         switch(k) {
         case 0:
            this.bindTexture(WIND_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
            this.windModel.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
            GlStateManager.scalef(0.875F, 0.875F, 0.875F);
            GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
            this.windModel.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            GlStateManager.popMatrix();
            break;
         case 1:
            this.bindTexture(VERTICAL_WIND_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
            GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
            this.windModel.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
            GlStateManager.scalef(0.875F, 0.875F, 0.875F);
            GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
            this.windModel.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            GlStateManager.popMatrix();
            break;
         case 2:
            this.bindTexture(WIND_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
            GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
            this.windModel.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
            GlStateManager.scalef(0.875F, 0.875F, 0.875F);
            GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
            this.windModel.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            GlStateManager.popMatrix();
         }

         Entity entity = Minecraft.getInstance().getRenderViewEntity();
         Vec2f vec2f = Vec2f.ZERO;
         if (entity != null) {
            vec2f = entity.getPitchYaw();
         }

         if (tileEntityIn.isEyeOpen()) {
            this.bindTexture(OPEN_EYE_TEXTURE);
         } else {
            this.bindTexture(CLOSED_EYE_TEXTURE);
         }

         GlStateManager.pushMatrix();
         GlStateManager.translatef((float)x + 0.5F, (float)y + 0.3F + f2 * 0.2F, (float)z + 0.5F);
         GlStateManager.scalef(0.5F, 0.5F, 0.5F);
         GlStateManager.rotatef(-vec2f.y, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(vec2f.x, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
         this.eyeModel.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.083333336F);
         GlStateManager.popMatrix();
      }

      super.render(tileEntityIn, x, y, z, partialTicks, destroyStage);
   }

   @OnlyIn(Dist.CLIENT)
   static class CageModel extends ModelBase {
      private final ModelRenderer field_205075_a;

      public CageModel() {
         this.textureWidth = 32;
         this.textureHeight = 16;
         this.field_205075_a = new ModelRenderer(this, 0, 0);
         this.field_205075_a.addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
      }

      /**
       * Sets the models various rotation angles then renders the model.
       */
      public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
         this.field_205075_a.render(scale);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class EyeModel extends ModelBase {
      private final ModelRenderer field_207745_a;

      public EyeModel() {
         this.textureWidth = 8;
         this.textureHeight = 8;
         this.field_207745_a = new ModelRenderer(this, 0, 0);
         this.field_207745_a.addBox(-4.0F, -4.0F, 0.0F, 8, 8, 0, 0.01F);
      }

      /**
       * Sets the models various rotation angles then renders the model.
       */
      public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
         this.field_207745_a.render(scale);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class ShellModel extends ModelBase {
      private final ModelRenderer field_205076_a;

      public ShellModel() {
         this.textureWidth = 32;
         this.textureHeight = 16;
         this.field_205076_a = new ModelRenderer(this, 0, 0);
         this.field_205076_a.addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6);
      }

      /**
       * Sets the models various rotation angles then renders the model.
       */
      public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
         this.field_205076_a.render(scale);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class WindModel extends ModelBase {
      public static int field_205078_a = 22;
      private final ModelRenderer[] field_205079_b = new ModelRenderer[field_205078_a];
      private int field_205080_c;

      public WindModel() {
         this.textureWidth = 64;
         this.textureHeight = 1024;

         for(int i = 0; i < field_205078_a; ++i) {
            this.field_205079_b[i] = new ModelRenderer(this, 0, 32 * i);
            this.field_205079_b[i].addBox(-8.0F, -8.0F, -8.0F, 16, 16, 16);
         }

      }

      /**
       * Sets the models various rotation angles then renders the model.
       */
      public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
         this.field_205079_b[this.field_205080_c].render(scale);
      }

      public void func_205077_a(int p_205077_1_) {
         this.field_205080_c = p_205077_1_;
      }
   }
}