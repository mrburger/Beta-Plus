package net.minecraft.client.renderer;

import java.nio.FloatBuffer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderHelper {
   /** Float buffer used to set OpenGL material colors */
   private static final FloatBuffer COLOR_BUFFER = GLAllocation.createDirectFloatBuffer(4);
   private static final Vec3d LIGHT0_POS = (new Vec3d((double)0.2F, 1.0D, (double)-0.7F)).normalize();
   private static final Vec3d LIGHT1_POS = (new Vec3d((double)-0.2F, 1.0D, (double)0.7F)).normalize();

   /**
    * Disables the OpenGL lighting properties enabled by enableStandardItemLighting
    */
   public static void disableStandardItemLighting() {
      GlStateManager.disableLighting();
      GlStateManager.disableLight(0);
      GlStateManager.disableLight(1);
      GlStateManager.disableColorMaterial();
   }

   /**
    * Sets the OpenGL lighting properties to the values used when rendering blocks as items
    */
   public static void enableStandardItemLighting() {
      GlStateManager.enableLighting();
      GlStateManager.enableLight(0);
      GlStateManager.enableLight(1);
      GlStateManager.enableColorMaterial();
      GlStateManager.colorMaterial(1032, 5634);
      GlStateManager.lightfv(16384, 4611, setColorBuffer(LIGHT0_POS.x, LIGHT0_POS.y, LIGHT0_POS.z, 0.0D));
      float f = 0.6F;
      GlStateManager.lightfv(16384, 4609, setColorBuffer(0.6F, 0.6F, 0.6F, 1.0F));
      GlStateManager.lightfv(16384, 4608, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
      GlStateManager.lightfv(16384, 4610, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
      GlStateManager.lightfv(16385, 4611, setColorBuffer(LIGHT1_POS.x, LIGHT1_POS.y, LIGHT1_POS.z, 0.0D));
      GlStateManager.lightfv(16385, 4609, setColorBuffer(0.6F, 0.6F, 0.6F, 1.0F));
      GlStateManager.lightfv(16385, 4608, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
      GlStateManager.lightfv(16385, 4610, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
      GlStateManager.shadeModel(7424);
      float f1 = 0.4F;
      GlStateManager.lightModelfv(2899, setColorBuffer(0.4F, 0.4F, 0.4F, 1.0F));
   }

   /**
    * Update and return colorBuffer with the RGBA values passed as arguments
    */
   private static FloatBuffer setColorBuffer(double red, double green, double blue, double alpha) {
      return setColorBuffer((float)red, (float)green, (float)blue, (float)alpha);
   }

   /**
    * Update and return colorBuffer with the RGBA values passed as arguments
    */
   public static FloatBuffer setColorBuffer(float red, float green, float blue, float alpha) {
      COLOR_BUFFER.clear();
      COLOR_BUFFER.put(red).put(green).put(blue).put(alpha);
      COLOR_BUFFER.flip();
      return COLOR_BUFFER;
   }

   /**
    * Sets OpenGL lighting for rendering blocks as items inside GUI screens (such as containers).
    */
   public static void enableGUIStandardItemLighting() {
      GlStateManager.pushMatrix();
      GlStateManager.rotatef(-30.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(165.0F, 1.0F, 0.0F, 0.0F);
      enableStandardItemLighting();
      GlStateManager.popMatrix();
   }
}