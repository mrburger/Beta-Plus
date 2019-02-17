package net.minecraft.client.renderer.tileentity;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.BlockAbstractSkull;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSkullWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelDragonHead;
import net.minecraft.client.renderer.entity.model.ModelHumanoidHead;
import net.minecraft.client.renderer.entity.model.ModelSkeletonHead;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntitySkullRenderer extends TileEntityRenderer<TileEntitySkull> {
   public static TileEntitySkullRenderer instance;
   private static final Map<BlockSkull.ISkullType, ModelBase> MODELS = Util.make(Maps.newHashMap(), (p_209262_0_) -> {
      ModelSkeletonHead modelskeletonhead = new ModelSkeletonHead(0, 0, 64, 32);
      ModelSkeletonHead modelskeletonhead1 = new ModelHumanoidHead();
      ModelDragonHead modeldragonhead = new ModelDragonHead(0.0F);
      p_209262_0_.put(BlockSkull.Types.SKELETON, modelskeletonhead);
      p_209262_0_.put(BlockSkull.Types.WITHER_SKELETON, modelskeletonhead);
      p_209262_0_.put(BlockSkull.Types.PLAYER, modelskeletonhead1);
      p_209262_0_.put(BlockSkull.Types.ZOMBIE, modelskeletonhead1);
      p_209262_0_.put(BlockSkull.Types.CREEPER, modelskeletonhead);
      p_209262_0_.put(BlockSkull.Types.DRAGON, modeldragonhead);
   });
   private static final Map<BlockSkull.ISkullType, ResourceLocation> SKINS = Util.make(Maps.newHashMap(), (p_209263_0_) -> {
      p_209263_0_.put(BlockSkull.Types.SKELETON, new ResourceLocation("textures/entity/skeleton/skeleton.png"));
      p_209263_0_.put(BlockSkull.Types.WITHER_SKELETON, new ResourceLocation("textures/entity/skeleton/wither_skeleton.png"));
      p_209263_0_.put(BlockSkull.Types.ZOMBIE, new ResourceLocation("textures/entity/zombie/zombie.png"));
      p_209263_0_.put(BlockSkull.Types.CREEPER, new ResourceLocation("textures/entity/creeper/creeper.png"));
      p_209263_0_.put(BlockSkull.Types.DRAGON, new ResourceLocation("textures/entity/enderdragon/dragon.png"));
      p_209263_0_.put(BlockSkull.Types.PLAYER, DefaultPlayerSkin.getDefaultSkinLegacy());
   });

   public void render(TileEntitySkull tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
      float f = tileEntityIn.getAnimationProgress(partialTicks);
      IBlockState iblockstate = tileEntityIn.getBlockState();
      boolean flag = iblockstate.getBlock() instanceof BlockSkullWall;
      EnumFacing enumfacing = flag ? iblockstate.get(BlockSkullWall.FACING) : null;
      float f1 = 22.5F * (float)(flag ? (2 + enumfacing.getHorizontalIndex()) * 4 : iblockstate.get(BlockSkull.ROTATION));
      this.render((float)x, (float)y, (float)z, enumfacing, f1, ((BlockAbstractSkull)iblockstate.getBlock()).getSkullType(), tileEntityIn.getPlayerProfile(), destroyStage, f);
   }

   public void setRendererDispatcher(TileEntityRendererDispatcher rendererDispatcherIn) {
      super.setRendererDispatcher(rendererDispatcherIn);
      instance = this;
   }

   public void render(float x, float y, float z, @Nullable EnumFacing facing, float rotationIn, BlockSkull.ISkullType type, @Nullable GameProfile playerProfile, int destroyStage, float animationProgress) {
      ModelBase modelbase = MODELS.get(type);
      if (destroyStage >= 0) {
         this.bindTexture(DESTROY_STAGES[destroyStage]);
         GlStateManager.matrixMode(5890);
         GlStateManager.pushMatrix();
         GlStateManager.scalef(4.0F, 2.0F, 1.0F);
         GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
         GlStateManager.matrixMode(5888);
      } else {
         this.bindTexture(this.func_199356_a(type, playerProfile));
      }

      GlStateManager.pushMatrix();
      GlStateManager.disableCull();
      if (facing == null) {
         GlStateManager.translatef(x + 0.5F, y, z + 0.5F);
      } else {
         switch(facing) {
         case NORTH:
            GlStateManager.translatef(x + 0.5F, y + 0.25F, z + 0.74F);
            break;
         case SOUTH:
            GlStateManager.translatef(x + 0.5F, y + 0.25F, z + 0.26F);
            break;
         case WEST:
            GlStateManager.translatef(x + 0.74F, y + 0.25F, z + 0.5F);
            break;
         case EAST:
         default:
            GlStateManager.translatef(x + 0.26F, y + 0.25F, z + 0.5F);
         }
      }

      GlStateManager.enableRescaleNormal();
      GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
      GlStateManager.enableAlphaTest();
      if (type == BlockSkull.Types.PLAYER) {
         GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
      }

      modelbase.render((Entity)null, animationProgress, 0.0F, 0.0F, rotationIn, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
      if (destroyStage >= 0) {
         GlStateManager.matrixMode(5890);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
      }

   }

   private ResourceLocation func_199356_a(BlockSkull.ISkullType p_199356_1_, @Nullable GameProfile p_199356_2_) {
      ResourceLocation resourcelocation = SKINS.get(p_199356_1_);
      if (p_199356_1_ == BlockSkull.Types.PLAYER && p_199356_2_ != null) {
         Minecraft minecraft = Minecraft.getInstance();
         Map<Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(p_199356_2_);
         if (map.containsKey(Type.SKIN)) {
            resourcelocation = minecraft.getSkinManager().loadSkin(map.get(Type.SKIN), Type.SKIN);
         } else {
            resourcelocation = DefaultPlayerSkin.getDefaultSkin(EntityPlayer.getUUID(p_199356_2_));
         }
      }

      return resourcelocation;
   }
}