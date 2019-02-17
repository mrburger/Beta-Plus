package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelBook;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntityEnchantmentTableRenderer extends TileEntityRenderer<TileEntityEnchantmentTable> {
   /** The texture for the book above the enchantment table. */
   private static final ResourceLocation TEXTURE_BOOK = new ResourceLocation("textures/entity/enchanting_table_book.png");
   private final ModelBook modelBook = new ModelBook();

   public void render(TileEntityEnchantmentTable tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)x + 0.5F, (float)y + 0.75F, (float)z + 0.5F);
      float f = (float)tileEntityIn.field_195522_a + partialTicks;
      GlStateManager.translatef(0.0F, 0.1F + MathHelper.sin(f * 0.1F) * 0.01F, 0.0F);

      float f1;
      for(f1 = tileEntityIn.field_195529_l - tileEntityIn.field_195530_m; f1 >= (float)Math.PI; f1 -= ((float)Math.PI * 2F)) {
         ;
      }

      while(f1 < -(float)Math.PI) {
         f1 += ((float)Math.PI * 2F);
      }

      float f2 = tileEntityIn.field_195530_m + f1 * partialTicks;
      GlStateManager.rotatef(-f2 * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(80.0F, 0.0F, 0.0F, 1.0F);
      this.bindTexture(TEXTURE_BOOK);
      float f3 = tileEntityIn.field_195524_g + (tileEntityIn.field_195523_f - tileEntityIn.field_195524_g) * partialTicks + 0.25F;
      float f4 = tileEntityIn.field_195524_g + (tileEntityIn.field_195523_f - tileEntityIn.field_195524_g) * partialTicks + 0.75F;
      f3 = (f3 - (float)MathHelper.fastFloor((double)f3)) * 1.6F - 0.3F;
      f4 = (f4 - (float)MathHelper.fastFloor((double)f4)) * 1.6F - 0.3F;
      if (f3 < 0.0F) {
         f3 = 0.0F;
      }

      if (f4 < 0.0F) {
         f4 = 0.0F;
      }

      if (f3 > 1.0F) {
         f3 = 1.0F;
      }

      if (f4 > 1.0F) {
         f4 = 1.0F;
      }

      float f5 = tileEntityIn.field_195528_k + (tileEntityIn.field_195527_j - tileEntityIn.field_195528_k) * partialTicks;
      GlStateManager.enableCull();
      this.modelBook.render((Entity)null, f, f3, f4, f5, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
   }
}