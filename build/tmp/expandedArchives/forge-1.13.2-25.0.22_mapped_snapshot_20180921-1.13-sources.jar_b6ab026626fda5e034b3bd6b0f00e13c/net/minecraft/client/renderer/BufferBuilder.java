package net.minecraft.client.renderer;

import com.google.common.primitives.Floats;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.BitSet;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class BufferBuilder {
   private static final Logger LOGGER = LogManager.getLogger();
   private ByteBuffer byteBuffer;
   private IntBuffer rawIntBuffer;
   private ShortBuffer rawShortBuffer;
   private FloatBuffer rawFloatBuffer;
   private int vertexCount;
   private VertexFormatElement vertexFormatElement;
   private int vertexFormatIndex;
   /** None */
   private boolean noColor;
   private int drawMode;
   private double xOffset;
   private double yOffset;
   private double zOffset;
   private VertexFormat vertexFormat;
   private boolean isDrawing;

   public BufferBuilder(int bufferSizeIn) {
      this.byteBuffer = GLAllocation.createDirectByteBuffer(bufferSizeIn * 4);
      this.rawIntBuffer = this.byteBuffer.asIntBuffer();
      this.rawShortBuffer = this.byteBuffer.asShortBuffer();
      this.rawFloatBuffer = this.byteBuffer.asFloatBuffer();
   }

   private void growBuffer(int increaseAmount) {
      if (this.vertexCount * this.vertexFormat.getSize() + increaseAmount > this.byteBuffer.capacity()) {
         int i = this.byteBuffer.capacity();
         int j = i + MathHelper.roundUp(increaseAmount, 2097152);
         LOGGER.debug("Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.", i, j);
         int k = this.rawIntBuffer.position();
         ByteBuffer bytebuffer = GLAllocation.createDirectByteBuffer(j);
         this.byteBuffer.position(0);
         bytebuffer.put(this.byteBuffer);
         bytebuffer.rewind();
         this.byteBuffer = bytebuffer;
         this.rawFloatBuffer = this.byteBuffer.asFloatBuffer().asReadOnlyBuffer();
         this.rawIntBuffer = this.byteBuffer.asIntBuffer();
         this.rawIntBuffer.position(k);
         this.rawShortBuffer = this.byteBuffer.asShortBuffer();
         this.rawShortBuffer.position(k << 1);
      }
   }

   public void sortVertexData(float cameraX, float cameraY, float cameraZ) {
      int i = this.vertexCount / 4;
      float[] afloat = new float[i];

      for(int j = 0; j < i; ++j) {
         afloat[j] = getDistanceSq(this.rawFloatBuffer, (float)((double)cameraX + this.xOffset), (float)((double)cameraY + this.yOffset), (float)((double)cameraZ + this.zOffset), this.vertexFormat.getIntegerSize(), j * this.vertexFormat.getSize());
      }

      Integer[] ainteger = new Integer[i];

      for(int k = 0; k < ainteger.length; ++k) {
         ainteger[k] = k;
      }

      Arrays.sort(ainteger, (p_210255_1_, p_210255_2_) -> {
         return Floats.compare(afloat[p_210255_2_], afloat[p_210255_1_]);
      });
      BitSet bitset = new BitSet();
      int l = this.vertexFormat.getSize();
      int[] aint = new int[l];

      for(int i1 = bitset.nextClearBit(0); i1 < ainteger.length; i1 = bitset.nextClearBit(i1 + 1)) {
         int j1 = ainteger[i1];
         if (j1 != i1) {
            this.rawIntBuffer.limit(j1 * l + l);
            this.rawIntBuffer.position(j1 * l);
            this.rawIntBuffer.get(aint);
            int k1 = j1;

            for(int l1 = ainteger[j1]; k1 != i1; l1 = ainteger[l1]) {
               this.rawIntBuffer.limit(l1 * l + l);
               this.rawIntBuffer.position(l1 * l);
               IntBuffer intbuffer = this.rawIntBuffer.slice();
               this.rawIntBuffer.limit(k1 * l + l);
               this.rawIntBuffer.position(k1 * l);
               this.rawIntBuffer.put(intbuffer);
               bitset.set(k1);
               k1 = l1;
            }

            this.rawIntBuffer.limit(i1 * l + l);
            this.rawIntBuffer.position(i1 * l);
            this.rawIntBuffer.put(aint);
         }

         bitset.set(i1);
      }
      this.rawIntBuffer.limit(this.rawIntBuffer.capacity());
      this.rawIntBuffer.position(this.getBufferSize());
   }

   public BufferBuilder.State getVertexState() {
      this.rawIntBuffer.rewind();
      int i = this.getBufferSize();
      this.rawIntBuffer.limit(i);
      int[] aint = new int[i];
      this.rawIntBuffer.get(aint);
      this.rawIntBuffer.limit(this.rawIntBuffer.capacity());
      this.rawIntBuffer.position(i);
      return new BufferBuilder.State(aint, new VertexFormat(this.vertexFormat));
   }

   private int getBufferSize() {
      return this.vertexCount * this.vertexFormat.getIntegerSize();
   }

   private static float getDistanceSq(FloatBuffer floatBufferIn, float x, float y, float z, int integerSize, int offset) {
      float f = floatBufferIn.get(offset + integerSize * 0 + 0);
      float f1 = floatBufferIn.get(offset + integerSize * 0 + 1);
      float f2 = floatBufferIn.get(offset + integerSize * 0 + 2);
      float f3 = floatBufferIn.get(offset + integerSize * 1 + 0);
      float f4 = floatBufferIn.get(offset + integerSize * 1 + 1);
      float f5 = floatBufferIn.get(offset + integerSize * 1 + 2);
      float f6 = floatBufferIn.get(offset + integerSize * 2 + 0);
      float f7 = floatBufferIn.get(offset + integerSize * 2 + 1);
      float f8 = floatBufferIn.get(offset + integerSize * 2 + 2);
      float f9 = floatBufferIn.get(offset + integerSize * 3 + 0);
      float f10 = floatBufferIn.get(offset + integerSize * 3 + 1);
      float f11 = floatBufferIn.get(offset + integerSize * 3 + 2);
      float f12 = (f + f3 + f6 + f9) * 0.25F - x;
      float f13 = (f1 + f4 + f7 + f10) * 0.25F - y;
      float f14 = (f2 + f5 + f8 + f11) * 0.25F - z;
      return f12 * f12 + f13 * f13 + f14 * f14;
   }

   public void setVertexState(BufferBuilder.State state) {
      this.rawIntBuffer.clear();
      this.growBuffer(state.getRawBuffer().length * 4);
      this.rawIntBuffer.put(state.getRawBuffer());
      this.vertexCount = state.getVertexCount();
      this.vertexFormat = new VertexFormat(state.getVertexFormat());
   }

   public void reset() {
      this.vertexCount = 0;
      this.vertexFormatElement = null;
      this.vertexFormatIndex = 0;
   }

   public void begin(int glMode, VertexFormat format) {
      if (this.isDrawing) {
         throw new IllegalStateException("Already building!");
      } else {
         this.isDrawing = true;
         this.reset();
         this.drawMode = glMode;
         this.vertexFormat = format;
         this.vertexFormatElement = format.getElement(this.vertexFormatIndex);
         this.noColor = false;
         this.byteBuffer.limit(this.byteBuffer.capacity());
      }
   }

   public BufferBuilder tex(double u, double v) {
      int i = this.vertexCount * this.vertexFormat.getSize() + this.vertexFormat.getOffset(this.vertexFormatIndex);
      switch(this.vertexFormatElement.getType()) {
      case FLOAT:
         this.byteBuffer.putFloat(i, (float)u);
         this.byteBuffer.putFloat(i + 4, (float)v);
         break;
      case UINT:
      case INT:
         this.byteBuffer.putInt(i, (int)u);
         this.byteBuffer.putInt(i + 4, (int)v);
         break;
      case USHORT:
      case SHORT:
         this.byteBuffer.putShort(i, (short)((int)v));
         this.byteBuffer.putShort(i + 2, (short)((int)u));
         break;
      case UBYTE:
      case BYTE:
         this.byteBuffer.put(i, (byte)((int)v));
         this.byteBuffer.put(i + 1, (byte)((int)u));
      }

      this.nextVertexFormatIndex();
      return this;
   }

   public BufferBuilder lightmap(int skyLight, int blockLight) {
      int i = this.vertexCount * this.vertexFormat.getSize() + this.vertexFormat.getOffset(this.vertexFormatIndex);
      switch(this.vertexFormatElement.getType()) {
      case FLOAT:
         this.byteBuffer.putFloat(i, (float)skyLight);
         this.byteBuffer.putFloat(i + 4, (float)blockLight);
         break;
      case UINT:
      case INT:
         this.byteBuffer.putInt(i, skyLight);
         this.byteBuffer.putInt(i + 4, blockLight);
         break;
      case USHORT:
      case SHORT:
         this.byteBuffer.putShort(i, (short)blockLight);
         this.byteBuffer.putShort(i + 2, (short)skyLight);
         break;
      case UBYTE:
      case BYTE:
         this.byteBuffer.put(i, (byte)blockLight);
         this.byteBuffer.put(i + 1, (byte)skyLight);
      }

      this.nextVertexFormatIndex();
      return this;
   }

   /**
    * Set the brightness for the previously stored quad (4 vertices)
    */
   public void putBrightness4(int vertex0, int vertex1, int vertex2, int vertex3) {
      int i = (this.vertexCount - 4) * this.vertexFormat.getIntegerSize() + this.vertexFormat.getUvOffsetById(1) / 4;
      int j = this.vertexFormat.getSize() >> 2;
      this.rawIntBuffer.put(i, vertex0);
      this.rawIntBuffer.put(i + j, vertex1);
      this.rawIntBuffer.put(i + j * 2, vertex2);
      this.rawIntBuffer.put(i + j * 3, vertex3);
   }

   public void putPosition(double x, double y, double z) {
      int i = this.vertexFormat.getIntegerSize();
      int j = (this.vertexCount - 4) * i;

      for(int k = 0; k < 4; ++k) {
         int l = j + k * i;
         int i1 = l + 1;
         int j1 = i1 + 1;
         this.rawIntBuffer.put(l, Float.floatToRawIntBits((float)(x + this.xOffset) + Float.intBitsToFloat(this.rawIntBuffer.get(l))));
         this.rawIntBuffer.put(i1, Float.floatToRawIntBits((float)(y + this.yOffset) + Float.intBitsToFloat(this.rawIntBuffer.get(i1))));
         this.rawIntBuffer.put(j1, Float.floatToRawIntBits((float)(z + this.zOffset) + Float.intBitsToFloat(this.rawIntBuffer.get(j1))));
      }

   }

   /**
    * Gets the position into the vertex data buffer at which the given vertex's color data can be found, in {@code
    * int}s.
    */
   public int getColorIndex(int vertexIndex) {
      return ((this.vertexCount - vertexIndex) * this.vertexFormat.getSize() + this.vertexFormat.getColorOffset()) / 4;
   }

   /**
    * Modify the color data of the given vertex with the given multipliers.
    */
   public void putColorMultiplier(float red, float green, float blue, int vertexIndex) {
      int i = this.getColorIndex(vertexIndex);
      int j = -1;
      if (!this.noColor) {
         j = this.rawIntBuffer.get(i);
         if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            int k = (int)((float)(j & 255) * red);
            int l = (int)((float)(j >> 8 & 255) * green);
            int i1 = (int)((float)(j >> 16 & 255) * blue);
            j = j & -16777216;
            j = j | i1 << 16 | l << 8 | k;
         } else {
            int j1 = (int)((float)(j >> 24 & 255) * red);
            int k1 = (int)((float)(j >> 16 & 255) * green);
            int l1 = (int)((float)(j >> 8 & 255) * blue);
            j = j & 255;
            j = j | j1 << 24 | k1 << 16 | l1 << 8;
         }
      }

      this.rawIntBuffer.put(i, j);
   }

   private void putColor(int argb, int vertexIndex) {
      int i = this.getColorIndex(vertexIndex);
      int j = argb >> 16 & 255;
      int k = argb >> 8 & 255;
      int l = argb & 255;
      this.putColorRGBA(i, j, k, l);
   }

   public void putColorRGB_F(float red, float green, float blue, int vertexIndex) {
      int i = this.getColorIndex(vertexIndex);
      int j = MathHelper.clamp((int)(red * 255.0F), 0, 255);
      int k = MathHelper.clamp((int)(green * 255.0F), 0, 255);
      int l = MathHelper.clamp((int)(blue * 255.0F), 0, 255);
      this.putColorRGBA(i, j, k, l);
   }

   /**
    * Write the given color data of 4 bytes at the given index into the vertex data buffer, accounting for system
    * endianness.
    */
   public void putColorRGBA(int index, int red, int green, int blue) {
      if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
         this.rawIntBuffer.put(index, -16777216 | blue << 16 | green << 8 | red);
      } else {
         this.rawIntBuffer.put(index, red << 24 | green << 16 | blue << 8 | 255);
      }

   }

   /**
    * Disables color processing.
    */
   public void noColor() {
      this.noColor = true;
   }

   public BufferBuilder color(float red, float green, float blue, float alpha) {
      return this.color((int)(red * 255.0F), (int)(green * 255.0F), (int)(blue * 255.0F), (int)(alpha * 255.0F));
   }

   public BufferBuilder color(int red, int green, int blue, int alpha) {
      if (this.noColor) {
         return this;
      } else {
         int i = this.vertexCount * this.vertexFormat.getSize() + this.vertexFormat.getOffset(this.vertexFormatIndex);
         switch(this.vertexFormatElement.getType()) {
         case FLOAT:
            this.byteBuffer.putFloat(i, (float)red / 255.0F);
            this.byteBuffer.putFloat(i + 4, (float)green / 255.0F);
            this.byteBuffer.putFloat(i + 8, (float)blue / 255.0F);
            this.byteBuffer.putFloat(i + 12, (float)alpha / 255.0F);
            break;
         case UINT:
         case INT:
            this.byteBuffer.putFloat(i, (float)red);
            this.byteBuffer.putFloat(i + 4, (float)green);
            this.byteBuffer.putFloat(i + 8, (float)blue);
            this.byteBuffer.putFloat(i + 12, (float)alpha);
            break;
         case USHORT:
         case SHORT:
            this.byteBuffer.putShort(i, (short)red);
            this.byteBuffer.putShort(i + 2, (short)green);
            this.byteBuffer.putShort(i + 4, (short)blue);
            this.byteBuffer.putShort(i + 6, (short)alpha);
            break;
         case UBYTE:
         case BYTE:
            if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
               this.byteBuffer.put(i, (byte)red);
               this.byteBuffer.put(i + 1, (byte)green);
               this.byteBuffer.put(i + 2, (byte)blue);
               this.byteBuffer.put(i + 3, (byte)alpha);
            } else {
               this.byteBuffer.put(i, (byte)alpha);
               this.byteBuffer.put(i + 1, (byte)blue);
               this.byteBuffer.put(i + 2, (byte)green);
               this.byteBuffer.put(i + 3, (byte)red);
            }
         }

         this.nextVertexFormatIndex();
         return this;
      }
   }

   public void addVertexData(int[] vertexData) {
      this.growBuffer(vertexData.length * 4 + this.vertexFormat.getSize());
      this.rawIntBuffer.position(this.getBufferSize());
      this.rawIntBuffer.put(vertexData);
      this.vertexCount += vertexData.length / this.vertexFormat.getIntegerSize();
   }

   public void endVertex() {
      ++this.vertexCount;
      this.growBuffer(this.vertexFormat.getSize());
   }

   public BufferBuilder pos(double x, double y, double z) {
      int i = this.vertexCount * this.vertexFormat.getSize() + this.vertexFormat.getOffset(this.vertexFormatIndex);
      switch(this.vertexFormatElement.getType()) {
      case FLOAT:
         this.byteBuffer.putFloat(i, (float)(x + this.xOffset));
         this.byteBuffer.putFloat(i + 4, (float)(y + this.yOffset));
         this.byteBuffer.putFloat(i + 8, (float)(z + this.zOffset));
         break;
      case UINT:
      case INT:
         this.byteBuffer.putInt(i, Float.floatToRawIntBits((float)(x + this.xOffset)));
         this.byteBuffer.putInt(i + 4, Float.floatToRawIntBits((float)(y + this.yOffset)));
         this.byteBuffer.putInt(i + 8, Float.floatToRawIntBits((float)(z + this.zOffset)));
         break;
      case USHORT:
      case SHORT:
         this.byteBuffer.putShort(i, (short)((int)(x + this.xOffset)));
         this.byteBuffer.putShort(i + 2, (short)((int)(y + this.yOffset)));
         this.byteBuffer.putShort(i + 4, (short)((int)(z + this.zOffset)));
         break;
      case UBYTE:
      case BYTE:
         this.byteBuffer.put(i, (byte)((int)(x + this.xOffset)));
         this.byteBuffer.put(i + 1, (byte)((int)(y + this.yOffset)));
         this.byteBuffer.put(i + 2, (byte)((int)(z + this.zOffset)));
      }

      this.nextVertexFormatIndex();
      return this;
   }

   public void putNormal(float x, float y, float z) {
      int i = (byte)((int)(x * 127.0F)) & 255;
      int j = (byte)((int)(y * 127.0F)) & 255;
      int k = (byte)((int)(z * 127.0F)) & 255;
      int l = i | j << 8 | k << 16;
      int i1 = this.vertexFormat.getSize() >> 2;
      int j1 = (this.vertexCount - 4) * i1 + this.vertexFormat.getNormalOffset() / 4;
      this.rawIntBuffer.put(j1, l);
      this.rawIntBuffer.put(j1 + i1, l);
      this.rawIntBuffer.put(j1 + i1 * 2, l);
      this.rawIntBuffer.put(j1 + i1 * 3, l);
   }

   private void nextVertexFormatIndex() {
      ++this.vertexFormatIndex;
      this.vertexFormatIndex %= this.vertexFormat.getElementCount();
      this.vertexFormatElement = this.vertexFormat.getElement(this.vertexFormatIndex);
      if (this.vertexFormatElement.getUsage() == VertexFormatElement.EnumUsage.PADDING) {
         this.nextVertexFormatIndex();
      }

   }

   public BufferBuilder normal(float x, float y, float z) {
      int i = this.vertexCount * this.vertexFormat.getSize() + this.vertexFormat.getOffset(this.vertexFormatIndex);
      switch(this.vertexFormatElement.getType()) {
      case FLOAT:
         this.byteBuffer.putFloat(i, x);
         this.byteBuffer.putFloat(i + 4, y);
         this.byteBuffer.putFloat(i + 8, z);
         break;
      case UINT:
      case INT:
         this.byteBuffer.putInt(i, (int)x);
         this.byteBuffer.putInt(i + 4, (int)y);
         this.byteBuffer.putInt(i + 8, (int)z);
         break;
      case USHORT:
      case SHORT:
         this.byteBuffer.putShort(i, (short)((int)(x * Short.MAX_VALUE) & 0xFFFF));
         this.byteBuffer.putShort(i + 2, (short)((int)(y * Short.MAX_VALUE) & 0xFFFF));
         this.byteBuffer.putShort(i + 4, (short)((int)(z * Short.MAX_VALUE) & 0xFFFF));
         break;
      case UBYTE:
      case BYTE:
         this.byteBuffer.put(i, (byte)((int)(x * Byte.MAX_VALUE) & 0xFF));
         this.byteBuffer.put(i + 1, (byte)((int)(y * Byte.MAX_VALUE) & 0xFF));
         this.byteBuffer.put(i + 2, (byte)((int)(z * Byte.MAX_VALUE) & 0xFF));
      }

      this.nextVertexFormatIndex();
      return this;
   }

   public void setTranslation(double x, double y, double z) {
      this.xOffset = x;
      this.yOffset = y;
      this.zOffset = z;
   }

   public void finishDrawing() {
      if (!this.isDrawing) {
         throw new IllegalStateException("Not building!");
      } else {
         this.isDrawing = false;
         this.byteBuffer.position(0);
         this.byteBuffer.limit(this.getBufferSize() * 4);
      }
   }

   public ByteBuffer getByteBuffer() {
      return this.byteBuffer;
   }

   public VertexFormat getVertexFormat() {
      return this.vertexFormat;
   }

   public int getVertexCount() {
      return this.vertexCount;
   }

   public int getDrawMode() {
      return this.drawMode;
   }

   public void putColor4(int argb) {
      for(int i = 0; i < 4; ++i) {
         this.putColor(argb, i + 1);
      }

   }

   public void putColorRGB_F4(float red, float green, float blue) {
      for(int i = 0; i < 4; ++i) {
         this.putColorRGB_F(red, green, blue, i + 1);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public class State {
      private final int[] stateRawBuffer;
      private final VertexFormat stateVertexFormat;

      public State(int[] buffer, VertexFormat format) {
         this.stateRawBuffer = buffer;
         this.stateVertexFormat = format;
      }

      public int[] getRawBuffer() {
         return this.stateRawBuffer;
      }

      public int getVertexCount() {
         return this.stateRawBuffer.length / this.stateVertexFormat.getIntegerSize();
      }

      public VertexFormat getVertexFormat() {
         return this.stateVertexFormat;
      }
   }
   
   //For some unknown reason Mojang changed the vanilla function to hardcode alpha as 255.... So lets re-add the parameter -.-
   public void putColorRGBA(int index, int red, int green, int blue, int alpha) {
      if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
         this.rawIntBuffer.put(index, alpha << 24 | blue << 16 | green << 8 | red);
      else
         this.rawIntBuffer.put(index, red << 24 | green << 16 | blue << 8 | alpha);
   }
   
   public boolean isColorDisabled() {
      return noColor;
   }
   
   public void putBulkData(ByteBuffer buffer) {
      growBuffer(buffer.limit() + this.vertexFormat.getSize());
      this.byteBuffer.position(this.vertexCount * this.vertexFormat.getSize());
      this.byteBuffer.put(buffer);
      this.vertexCount += buffer.limit() / this.vertexFormat.getSize();
   }
}