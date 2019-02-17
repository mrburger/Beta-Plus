package net.minecraft.client.renderer;

import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VboRenderList extends ChunkRenderContainer {
   public void renderChunkLayer(BlockRenderLayer layer) {
      if (this.initialized) {
         for(RenderChunk renderchunk : this.renderChunks) {
            VertexBuffer vertexbuffer = renderchunk.getVertexBufferByLayer(layer.ordinal());
            GlStateManager.pushMatrix();
            this.preRenderChunk(renderchunk);
            renderchunk.multModelviewMatrix();
            vertexbuffer.bindBuffer();
            this.setupArrayPointers();
            vertexbuffer.drawArrays(7);
            GlStateManager.popMatrix();
         }

         OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
         GlStateManager.resetColor();
         this.renderChunks.clear();
      }
   }

   private void setupArrayPointers() {
      GlStateManager.vertexPointer(3, 5126, 28, 0);
      GlStateManager.colorPointer(4, 5121, 28, 12);
      GlStateManager.texCoordPointer(2, 5126, 28, 16);
      OpenGlHelper.glClientActiveTexture(OpenGlHelper.GL_TEXTURE1);
      GlStateManager.texCoordPointer(2, 5122, 28, 24);
      OpenGlHelper.glClientActiveTexture(OpenGlHelper.GL_TEXTURE0);
   }
}