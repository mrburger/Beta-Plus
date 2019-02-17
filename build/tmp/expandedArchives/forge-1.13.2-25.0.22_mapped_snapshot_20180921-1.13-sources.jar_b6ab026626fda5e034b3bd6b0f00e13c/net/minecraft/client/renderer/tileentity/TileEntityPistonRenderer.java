package net.minecraft.client.renderer.tileentity;

import java.util.Random;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.state.properties.PistonType;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntityPistonRenderer extends TileEntityRenderer<TileEntityPiston> {
   private final BlockRendererDispatcher blockRenderer = Minecraft.getInstance().getBlockRendererDispatcher();

   public void render(TileEntityPiston tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
      BlockPos blockpos = tileEntityIn.getPos().offset(tileEntityIn.getMotionDirection().getOpposite());
      IBlockState iblockstate = tileEntityIn.getPistonState();
      if (!iblockstate.isAir() && !(tileEntityIn.getProgress(partialTicks) >= 1.0F)) {
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder bufferbuilder = tessellator.getBuffer();
         this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
         RenderHelper.disableStandardItemLighting();
         GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
         GlStateManager.enableBlend();
         GlStateManager.disableCull();
         if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(7425);
         } else {
            GlStateManager.shadeModel(7424);
         }

         bufferbuilder.begin(7, DefaultVertexFormats.BLOCK);
         bufferbuilder.setTranslation(x - (double)blockpos.getX() + (double)tileEntityIn.getOffsetX(partialTicks), y - (double)blockpos.getY() + (double)tileEntityIn.getOffsetY(partialTicks), z - (double)blockpos.getZ() + (double)tileEntityIn.getOffsetZ(partialTicks));
         World world = this.getWorld();
         if (iblockstate.getBlock() == Blocks.PISTON_HEAD && tileEntityIn.getProgress(partialTicks) <= 4.0F) {
            iblockstate = iblockstate.with(BlockPistonExtension.SHORT, Boolean.valueOf(true));
            this.renderStateModel(blockpos, iblockstate, bufferbuilder, world, false);
         } else if (tileEntityIn.shouldPistonHeadBeRendered() && !tileEntityIn.isExtending()) {
            PistonType pistontype = iblockstate.getBlock() == Blocks.STICKY_PISTON ? PistonType.STICKY : PistonType.DEFAULT;
            IBlockState iblockstate1 = Blocks.PISTON_HEAD.getDefaultState().with(BlockPistonExtension.TYPE, pistontype).with(BlockPistonExtension.FACING, iblockstate.get(BlockPistonBase.FACING));
            iblockstate1 = iblockstate1.with(BlockPistonExtension.SHORT, Boolean.valueOf(tileEntityIn.getProgress(partialTicks) >= 0.5F));
            this.renderStateModel(blockpos, iblockstate1, bufferbuilder, world, false);
            BlockPos blockpos1 = blockpos.offset(tileEntityIn.getMotionDirection());
            bufferbuilder.setTranslation(x - (double)blockpos1.getX(), y - (double)blockpos1.getY(), z - (double)blockpos1.getZ());
            iblockstate = iblockstate.with(BlockPistonBase.EXTENDED, Boolean.valueOf(true));
            this.renderStateModel(blockpos1, iblockstate, bufferbuilder, world, true);
         } else {
            this.renderStateModel(blockpos, iblockstate, bufferbuilder, world, false);
         }

         bufferbuilder.setTranslation(0.0D, 0.0D, 0.0D);
         tessellator.draw();
         RenderHelper.enableStandardItemLighting();
      }
   }

   private boolean renderStateModel(BlockPos pos, IBlockState state, BufferBuilder buffer, World p_188186_4_, boolean checkSides) {
      return this.blockRenderer.getBlockModelRenderer().renderModel(p_188186_4_, this.blockRenderer.getModelForState(state), state, pos, buffer, checkSides, new Random(), state.getPositionRandom(pos));
   }
}