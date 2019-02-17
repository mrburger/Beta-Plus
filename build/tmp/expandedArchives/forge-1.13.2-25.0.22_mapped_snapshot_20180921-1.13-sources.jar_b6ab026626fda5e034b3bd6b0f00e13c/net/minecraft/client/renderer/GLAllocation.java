package net.minecraft.client.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GLAllocation {
   /**
    * Generates the specified number of display lists and returns the first index.
    */
   public static synchronized int generateDisplayLists(int range) {
      int i = GlStateManager.genLists(range);
      if (i == 0) {
         int j = GlStateManager.getError();
         String s = "No error code reported";
         if (j != 0) {
            s = OpenGlHelper.getErrorMessage(j);
         }

         throw new IllegalStateException("glGenLists returned an ID of 0 for a count of " + range + ", GL error (" + j + "): " + s);
      } else {
         return i;
      }
   }

   public static synchronized void deleteDisplayLists(int list, int range) {
      GlStateManager.deleteLists(list, range);
   }

   public static synchronized void deleteDisplayLists(int list) {
      deleteDisplayLists(list, 1);
   }

   /**
    * Creates and returns a direct byte buffer with the specified capacity. Applies native ordering to speed up access.
    */
   public static synchronized ByteBuffer createDirectByteBuffer(int capacity) {
      return ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
   }

   /**
    * Creates and returns a direct float buffer with the specified capacity. Applies native ordering to speed up access.
    */
   public static FloatBuffer createDirectFloatBuffer(int capacity) {
      return createDirectByteBuffer(capacity << 2).asFloatBuffer();
   }
}