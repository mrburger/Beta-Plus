package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class RealmsSliderButton extends RealmsButton {
   public double value = 1.0D;
   public boolean sliding;
   private final double minValue;
   private final double maxValue;
   private int steps;

   public RealmsSliderButton(int buttonId, int x, int y, int width, int maxValueIn, int p_i1056_6_) {
      this(buttonId, x, y, width, p_i1056_6_, 0, 1.0D, (double)maxValueIn);
   }

   public RealmsSliderButton(int p_i47900_1_, int p_i47900_2_, int p_i47900_3_, int p_i47900_4_, int p_i47900_5_, int p_i47900_6_, double p_i47900_7_, double p_i47900_9_) {
      super(p_i47900_1_, p_i47900_2_, p_i47900_3_, p_i47900_4_, 20, "");
      this.minValue = p_i47900_7_;
      this.maxValue = p_i47900_9_;
      this.value = this.toPct((double)p_i47900_6_);
      this.getProxy().displayString = this.getMessage();
   }

   public String getMessage() {
      return "";
   }

   public double toPct(double p_toPct_1_) {
      return MathHelper.clamp((this.clamp(p_toPct_1_) - this.minValue) / (this.maxValue - this.minValue), 0.0D, 1.0D);
   }

   public double toValue(double p_toValue_1_) {
      return this.clamp(this.minValue + (this.maxValue - this.minValue) * MathHelper.clamp(p_toValue_1_, 0.0D, 1.0D));
   }

   public double clamp(double p_clamp_1_) {
      p_clamp_1_ = this.clampSteps(p_clamp_1_);
      return MathHelper.clamp(p_clamp_1_, this.minValue, this.maxValue);
   }

   protected double clampSteps(double p_clampSteps_1_) {
      if (this.steps > 0) {
         p_clampSteps_1_ = (double)((long)this.steps * Math.round(p_clampSteps_1_ / (double)this.steps));
      }

      return p_clampSteps_1_;
   }

   public int getYImage(boolean p_getYImage_1_) {
      return 0;
   }

   public void renderBg(int p_renderBg_1_, int p_renderBg_2_) {
      if (this.getProxy().visible) {
         if (this.sliding) {
            this.value = (double)((float)(p_renderBg_1_ - (this.getProxy().x + 4)) / (float)(this.getProxy().getWidth() - 8));
            this.value = MathHelper.clamp(this.value, 0.0D, 1.0D);
            double d0 = this.toValue(this.value);
            this.clicked(d0);
            this.value = this.toPct(d0);
            this.getProxy().displayString = this.getMessage();
         }

         Minecraft.getInstance().getTextureManager().bindTexture(WIDGETS_LOCATION);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.blit(this.getProxy().x + (int)(this.value * (double)(this.getProxy().getWidth() - 8)), this.getProxy().y, 0, 66, 4, 20);
         this.blit(this.getProxy().x + (int)(this.value * (double)(this.getProxy().getWidth() - 8)) + 4, this.getProxy().y, 196, 66, 4, 20);
      }
   }

   public void onClick(double p_onClick_1_, double p_onClick_3_) {
      this.value = (p_onClick_1_ - (double)(this.getProxy().x + 4)) / (double)(this.getProxy().getWidth() - 8);
      this.value = MathHelper.clamp(this.value, 0.0D, 1.0D);
      this.clicked(this.toValue(this.value));
      this.getProxy().displayString = this.getMessage();
      this.sliding = true;
   }

   public void clicked(double p_clicked_1_) {
   }

   public void onRelease(double p_onRelease_1_, double p_onRelease_3_) {
      this.sliding = false;
   }
}