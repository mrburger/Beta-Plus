package net.minecraft.client.renderer;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.SimpleBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.fluid.IFluidState;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockRendererDispatcher implements IResourceManagerReloadListener {
   private final BlockModelShapes blockModelShapes;
   private final BlockModelRenderer blockModelRenderer;
   private final ChestRenderer chestRenderer = new ChestRenderer();
   private final BlockFluidRenderer fluidRenderer;
   private final Random random = new Random();

   public BlockRendererDispatcher(BlockModelShapes p_i46577_1_, BlockColors p_i46577_2_) {
      this.blockModelShapes = p_i46577_1_;
      this.blockModelRenderer = new net.minecraftforge.client.model.pipeline.ForgeBlockModelRenderer(p_i46577_2_);
      this.fluidRenderer = new BlockFluidRenderer();
   }

   public BlockModelShapes getBlockModelShapes() {
      return this.blockModelShapes;
   }

   public void renderBlockDamage(IBlockState state, BlockPos pos, TextureAtlasSprite texture, IWorldReader blockAccess) {
      if (state.getRenderType() == EnumBlockRenderType.MODEL) {
         IBakedModel ibakedmodel = this.blockModelShapes.getModel(state);
         long i = state.getPositionRandom(pos);
         IBakedModel ibakedmodel1 = net.minecraftforge.client.ForgeHooksClient.getDamageModel(ibakedmodel, texture, state, blockAccess, pos);
         this.blockModelRenderer.renderModel(blockAccess, ibakedmodel1, state, pos, Tessellator.getInstance().getBuffer(), true, this.random, i);
      }
   }

   public boolean renderBlock(IBlockState p_195475_1_, BlockPos p_195475_2_, IWorldReader p_195475_3_, BufferBuilder p_195475_4_, Random p_195475_5_) {
      try {
         EnumBlockRenderType enumblockrendertype = p_195475_1_.getRenderType();
         if (enumblockrendertype == EnumBlockRenderType.INVISIBLE) {
            return false;
         } else {
            switch(enumblockrendertype) {
            case MODEL:
               IBakedModel model = this.getModelForState(p_195475_1_);
               p_195475_1_ = p_195475_1_.getBlock().getExtendedState(p_195475_1_, p_195475_3_, p_195475_2_);
               return this.blockModelRenderer.renderModel(p_195475_3_, this.getModelForState(p_195475_1_), p_195475_1_, p_195475_2_, p_195475_4_, true, p_195475_5_, p_195475_1_.getPositionRandom(p_195475_2_));
            case ENTITYBLOCK_ANIMATED:
               return false;
            default:
               return false;
            }
         }
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Tesselating block in world");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being tesselated");
         CrashReportCategory.addBlockInfo(crashreportcategory, p_195475_2_, p_195475_1_);
         throw new ReportedException(crashreport);
      }
   }

   public boolean renderFluid(BlockPos p_205318_1_, IWorldReader p_205318_2_, BufferBuilder p_205318_3_, IFluidState p_205318_4_) {
      try {
         return this.fluidRenderer.render(p_205318_2_, p_205318_1_, p_205318_3_, p_205318_4_);
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Tesselating liquid in world");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being tesselated");
         CrashReportCategory.addBlockInfo(crashreportcategory, p_205318_1_, (IBlockState)null);
         throw new ReportedException(crashreport);
      }
   }

   public BlockModelRenderer getBlockModelRenderer() {
      return this.blockModelRenderer;
   }

   public IBakedModel getModelForState(IBlockState state) {
      return this.blockModelShapes.getModel(state);
   }

   public void renderBlockBrightness(IBlockState state, float brightness) {
      EnumBlockRenderType enumblockrendertype = state.getRenderType();
      if (enumblockrendertype != EnumBlockRenderType.INVISIBLE) {
         switch(enumblockrendertype) {
         case MODEL:
            IBakedModel ibakedmodel = this.getModelForState(state);
            this.blockModelRenderer.renderModelBrightness(ibakedmodel, state, brightness, true);
            break;
         case ENTITYBLOCK_ANIMATED:
            this.chestRenderer.renderChestBrightness(state.getBlock(), brightness);
         }

      }
   }

   public void onResourceManagerReload(IResourceManager resourceManager) {
      this.fluidRenderer.initAtlasSprites();
   }

   @Override
   public net.minecraftforge.resource.IResourceType getResourceType() {
      return net.minecraftforge.resource.VanillaResourceType.MODELS;
   }
}