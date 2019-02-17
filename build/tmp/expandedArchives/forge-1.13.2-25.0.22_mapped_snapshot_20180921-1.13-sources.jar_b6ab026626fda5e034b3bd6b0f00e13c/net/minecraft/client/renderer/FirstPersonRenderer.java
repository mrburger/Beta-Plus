package net.minecraft.client.renderer;

import com.google.common.base.MoreObjects;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FirstPersonRenderer {
   private static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
   private static final ResourceLocation RES_UNDERWATER_OVERLAY = new ResourceLocation("textures/misc/underwater.png");
   /** A reference to the Minecraft object. */
   private final Minecraft mc;
   private ItemStack itemStackMainHand = ItemStack.EMPTY;
   private ItemStack itemStackOffHand = ItemStack.EMPTY;
   private float equippedProgressMainHand;
   private float prevEquippedProgressMainHand;
   private float equippedProgressOffHand;
   private float prevEquippedProgressOffHand;
   private final RenderManager renderManager;
   private final ItemRenderer itemRenderer;

   public FirstPersonRenderer(Minecraft mcIn) {
      this.mc = mcIn;
      this.renderManager = mcIn.getRenderManager();
      this.itemRenderer = mcIn.getItemRenderer();
   }

   public void renderItem(EntityLivingBase entityIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform) {
      this.renderItemSide(entityIn, heldStack, transform, false);
   }

   public void renderItemSide(EntityLivingBase entitylivingbaseIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform, boolean leftHanded) {
      if (!heldStack.isEmpty()) {
         Item item = heldStack.getItem();
         Block block = Block.getBlockFromItem(item);
         GlStateManager.pushMatrix();
         boolean flag = this.itemRenderer.shouldRenderItemIn3D(heldStack) && block.getRenderLayer() == BlockRenderLayer.TRANSLUCENT;
         if (flag) {
            GlStateManager.depthMask(false);
         }

         this.itemRenderer.renderItem(heldStack, entitylivingbaseIn, transform, leftHanded);
         if (flag) {
            GlStateManager.depthMask(true);
         }

         GlStateManager.popMatrix();
      }
   }

   /**
    * Rotate the render around X and Y
    */
   private void rotateArroundXAndY(float angle, float angleY) {
      GlStateManager.pushMatrix();
      GlStateManager.rotatef(angle, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef(angleY, 0.0F, 1.0F, 0.0F);
      RenderHelper.enableStandardItemLighting();
      GlStateManager.popMatrix();
   }

   private void setLightmap() {
      AbstractClientPlayer abstractclientplayer = this.mc.player;
      int i = this.mc.world.getCombinedLight(new BlockPos(abstractclientplayer.posX, abstractclientplayer.posY + (double)abstractclientplayer.getEyeHeight(), abstractclientplayer.posZ), 0);
      float f = (float)(i & '\uffff');
      float f1 = (float)(i >> 16);
      OpenGlHelper.glMultiTexCoord2f(OpenGlHelper.GL_TEXTURE1, f, f1);
   }

   private void rotateArm(float partialTicks) {
      EntityPlayerSP entityplayersp = this.mc.player;
      float f = entityplayersp.prevRenderArmPitch + (entityplayersp.renderArmPitch - entityplayersp.prevRenderArmPitch) * partialTicks;
      float f1 = entityplayersp.prevRenderArmYaw + (entityplayersp.renderArmYaw - entityplayersp.prevRenderArmYaw) * partialTicks;
      GlStateManager.rotatef((entityplayersp.rotationPitch - f) * 0.1F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef((entityplayersp.rotationYaw - f1) * 0.1F, 0.0F, 1.0F, 0.0F);
   }

   /**
    * Return the angle to render the Map
    */
   private float getMapAngleFromPitch(float pitch) {
      float f = 1.0F - pitch / 45.0F + 0.1F;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      f = -MathHelper.cos(f * (float)Math.PI) * 0.5F + 0.5F;
      return f;
   }

   private void renderArms() {
      if (!this.mc.player.isInvisible()) {
         GlStateManager.disableCull();
         GlStateManager.pushMatrix();
         GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
         this.renderArm(EnumHandSide.RIGHT);
         this.renderArm(EnumHandSide.LEFT);
         GlStateManager.popMatrix();
         GlStateManager.enableCull();
      }
   }

   private void renderArm(EnumHandSide side) {
      this.mc.getTextureManager().bindTexture(this.mc.player.getLocationSkin());
      Render<AbstractClientPlayer> render = this.renderManager.getEntityRenderObject(this.mc.player);
      RenderPlayer renderplayer = (RenderPlayer)render;
      GlStateManager.pushMatrix();
      float f = side == EnumHandSide.RIGHT ? 1.0F : -1.0F;
      GlStateManager.rotatef(92.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(45.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef(f * -41.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.translatef(f * 0.3F, -1.1F, 0.45F);
      if (side == EnumHandSide.RIGHT) {
         renderplayer.renderRightArm(this.mc.player);
      } else {
         renderplayer.renderLeftArm(this.mc.player);
      }

      GlStateManager.popMatrix();
   }

   private void renderMapFirstPersonSide(float equippedProgress, EnumHandSide hand, float swingProgress, ItemStack stack) {
      float f = hand == EnumHandSide.RIGHT ? 1.0F : -1.0F;
      GlStateManager.translatef(f * 0.125F, -0.125F, 0.0F);
      if (!this.mc.player.isInvisible()) {
         GlStateManager.pushMatrix();
         GlStateManager.rotatef(f * 10.0F, 0.0F, 0.0F, 1.0F);
         this.renderArmFirstPerson(equippedProgress, swingProgress, hand);
         GlStateManager.popMatrix();
      }

      GlStateManager.pushMatrix();
      GlStateManager.translatef(f * 0.51F, -0.08F + equippedProgress * -1.2F, -0.75F);
      float f1 = MathHelper.sqrt(swingProgress);
      float f2 = MathHelper.sin(f1 * (float)Math.PI);
      float f3 = -0.5F * f2;
      float f4 = 0.4F * MathHelper.sin(f1 * ((float)Math.PI * 2F));
      float f5 = -0.3F * MathHelper.sin(swingProgress * (float)Math.PI);
      GlStateManager.translatef(f * f3, f4 - 0.3F * f2, f5);
      GlStateManager.rotatef(f2 * -45.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef(f * f2 * -30.0F, 0.0F, 1.0F, 0.0F);
      this.renderMapFirstPerson(stack);
      GlStateManager.popMatrix();
   }

   private void renderMapFirstPerson(float pitch, float equippedProgress, float swingProgress) {
      float f = MathHelper.sqrt(swingProgress);
      float f1 = -0.2F * MathHelper.sin(swingProgress * (float)Math.PI);
      float f2 = -0.4F * MathHelper.sin(f * (float)Math.PI);
      GlStateManager.translatef(0.0F, -f1 / 2.0F, f2);
      float f3 = this.getMapAngleFromPitch(pitch);
      GlStateManager.translatef(0.0F, 0.04F + equippedProgress * -1.2F + f3 * -0.5F, -0.72F);
      GlStateManager.rotatef(f3 * -85.0F, 1.0F, 0.0F, 0.0F);
      this.renderArms();
      float f4 = MathHelper.sin(f * (float)Math.PI);
      GlStateManager.rotatef(f4 * 20.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.scalef(2.0F, 2.0F, 2.0F);
      this.renderMapFirstPerson(this.itemStackMainHand);
   }

   private void renderMapFirstPerson(ItemStack stack) {
      GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.scalef(0.38F, 0.38F, 0.38F);
      GlStateManager.disableLighting();
      this.mc.getTextureManager().bindTexture(RES_MAP_BACKGROUND);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      GlStateManager.translatef(-0.5F, -0.5F, 0.0F);
      GlStateManager.scalef(0.0078125F, 0.0078125F, 0.0078125F);
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
      bufferbuilder.pos(-7.0D, 135.0D, 0.0D).tex(0.0D, 1.0D).endVertex();
      bufferbuilder.pos(135.0D, 135.0D, 0.0D).tex(1.0D, 1.0D).endVertex();
      bufferbuilder.pos(135.0D, -7.0D, 0.0D).tex(1.0D, 0.0D).endVertex();
      bufferbuilder.pos(-7.0D, -7.0D, 0.0D).tex(0.0D, 0.0D).endVertex();
      tessellator.draw();
      MapData mapdata = ItemMap.getMapData(stack, this.mc.world);
      if (mapdata != null) {
         this.mc.entityRenderer.getMapItemRenderer().renderMap(mapdata, false);
      }

      GlStateManager.enableLighting();
   }

   private void renderArmFirstPerson(float equippedProgress, float swingProgress, EnumHandSide side) {
      boolean flag = side != EnumHandSide.LEFT;
      float f = flag ? 1.0F : -1.0F;
      float f1 = MathHelper.sqrt(swingProgress);
      float f2 = -0.3F * MathHelper.sin(f1 * (float)Math.PI);
      float f3 = 0.4F * MathHelper.sin(f1 * ((float)Math.PI * 2F));
      float f4 = -0.4F * MathHelper.sin(swingProgress * (float)Math.PI);
      GlStateManager.translatef(f * (f2 + 0.64000005F), f3 + -0.6F + equippedProgress * -0.6F, f4 + -0.71999997F);
      GlStateManager.rotatef(f * 45.0F, 0.0F, 1.0F, 0.0F);
      float f5 = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
      float f6 = MathHelper.sin(f1 * (float)Math.PI);
      GlStateManager.rotatef(f * f6 * 70.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(f * f5 * -20.0F, 0.0F, 0.0F, 1.0F);
      AbstractClientPlayer abstractclientplayer = this.mc.player;
      this.mc.getTextureManager().bindTexture(abstractclientplayer.getLocationSkin());
      GlStateManager.translatef(f * -1.0F, 3.6F, 3.5F);
      GlStateManager.rotatef(f * 120.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotatef(200.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef(f * -135.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.translatef(f * 5.6F, 0.0F, 0.0F);
      RenderPlayer renderplayer = (RenderPlayer)this.renderManager.<AbstractClientPlayer>getEntityRenderObject(abstractclientplayer);
      GlStateManager.disableCull();
      if (flag) {
         renderplayer.renderRightArm(abstractclientplayer);
      } else {
         renderplayer.renderLeftArm(abstractclientplayer);
      }

      GlStateManager.enableCull();
   }

   private void transformEatFirstPerson(float partialTicks, EnumHandSide hand, ItemStack stack) {
      float f = (float)this.mc.player.getItemInUseCount() - partialTicks + 1.0F;
      float f1 = f / (float)stack.getUseDuration();
      if (f1 < 0.8F) {
         float f2 = MathHelper.abs(MathHelper.cos(f / 4.0F * (float)Math.PI) * 0.1F);
         GlStateManager.translatef(0.0F, f2, 0.0F);
      }

      float f3 = 1.0F - (float)Math.pow((double)f1, 27.0D);
      int i = hand == EnumHandSide.RIGHT ? 1 : -1;
      GlStateManager.translatef(f3 * 0.6F * (float)i, f3 * -0.5F, f3 * 0.0F);
      GlStateManager.rotatef((float)i * f3 * 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(f3 * 10.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef((float)i * f3 * 30.0F, 0.0F, 0.0F, 1.0F);
   }

   private void transformFirstPerson(EnumHandSide hand, float swingProgress) {
      int i = hand == EnumHandSide.RIGHT ? 1 : -1;
      float f = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
      GlStateManager.rotatef((float)i * (45.0F + f * -20.0F), 0.0F, 1.0F, 0.0F);
      float f1 = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
      GlStateManager.rotatef((float)i * f1 * -20.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotatef(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef((float)i * -45.0F, 0.0F, 1.0F, 0.0F);
   }

   private void transformSideFirstPerson(EnumHandSide hand, float equippedProg) {
      int i = hand == EnumHandSide.RIGHT ? 1 : -1;
      GlStateManager.translatef((float)i * 0.56F, -0.52F + equippedProg * -0.6F, -0.72F);
   }

   /**
    * Renders the active item in the player's hand when in first person mode.
    */
   public void renderItemInFirstPerson(float partialTicks) {
      AbstractClientPlayer abstractclientplayer = this.mc.player;
      float f = abstractclientplayer.getSwingProgress(partialTicks);
      EnumHand enumhand = MoreObjects.firstNonNull(abstractclientplayer.swingingHand, EnumHand.MAIN_HAND);
      float f1 = abstractclientplayer.prevRotationPitch + (abstractclientplayer.rotationPitch - abstractclientplayer.prevRotationPitch) * partialTicks;
      float f2 = abstractclientplayer.prevRotationYaw + (abstractclientplayer.rotationYaw - abstractclientplayer.prevRotationYaw) * partialTicks;
      boolean flag = true;
      boolean flag1 = true;
      if (abstractclientplayer.isHandActive()) {
         ItemStack itemstack = abstractclientplayer.getActiveItemStack();
         if (itemstack.getItem() == Items.BOW) {
            flag = abstractclientplayer.getActiveHand() == EnumHand.MAIN_HAND;
            flag1 = !flag;
         }
      }

      this.rotateArroundXAndY(f1, f2);
      this.setLightmap();
      this.rotateArm(partialTicks);
      GlStateManager.enableRescaleNormal();
      if (flag) {
         float f4 = enumhand == EnumHand.MAIN_HAND ? f : 0.0F;
         float f3 = 1.0F - (this.prevEquippedProgressMainHand + (this.equippedProgressMainHand - this.prevEquippedProgressMainHand) * partialTicks);
         if(!net.minecraftforge.client.ForgeHooksClient.renderSpecificFirstPersonHand(EnumHand.MAIN_HAND, partialTicks, f1, f4, f3, this.itemStackMainHand))
         this.renderItemInFirstPerson(abstractclientplayer, partialTicks, f1, EnumHand.MAIN_HAND, f4, this.itemStackMainHand, f3);
      }

      if (flag1) {
         float f5 = enumhand == EnumHand.OFF_HAND ? f : 0.0F;
         float f6 = 1.0F - (this.prevEquippedProgressOffHand + (this.equippedProgressOffHand - this.prevEquippedProgressOffHand) * partialTicks);
         if(!net.minecraftforge.client.ForgeHooksClient.renderSpecificFirstPersonHand(EnumHand.OFF_HAND, partialTicks, f1, f5, f6, this.itemStackOffHand))
         this.renderItemInFirstPerson(abstractclientplayer, partialTicks, f1, EnumHand.OFF_HAND, f5, this.itemStackOffHand, f6);
      }

      GlStateManager.disableRescaleNormal();
      RenderHelper.disableStandardItemLighting();
   }

   public void renderItemInFirstPerson(AbstractClientPlayer player, float partialTicks, float pitch, EnumHand hand, float swingProgress, ItemStack stack, float equippedProgress) {
      boolean flag = hand == EnumHand.MAIN_HAND;
      EnumHandSide enumhandside = flag ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
      GlStateManager.pushMatrix();
      if (stack.isEmpty()) {
         if (flag && !player.isInvisible()) {
            this.renderArmFirstPerson(equippedProgress, swingProgress, enumhandside);
         }
      } else if (stack.getItem() instanceof net.minecraft.item.ItemMap) {
         if (flag && this.itemStackOffHand.isEmpty()) {
            this.renderMapFirstPerson(pitch, equippedProgress, swingProgress);
         } else {
            this.renderMapFirstPersonSide(equippedProgress, enumhandside, swingProgress, stack);
         }
      } else {
         boolean flag1 = enumhandside == EnumHandSide.RIGHT;
         if (player.isHandActive() && player.getItemInUseCount() > 0 && player.getActiveHand() == hand) {
            int k = flag1 ? 1 : -1;
            switch(stack.getUseAction()) {
            case NONE:
               this.transformSideFirstPerson(enumhandside, equippedProgress);
               break;
            case EAT:
            case DRINK:
               this.transformEatFirstPerson(partialTicks, enumhandside, stack);
               this.transformSideFirstPerson(enumhandside, equippedProgress);
               break;
            case BLOCK:
               this.transformSideFirstPerson(enumhandside, equippedProgress);
               break;
            case BOW:
               this.transformSideFirstPerson(enumhandside, equippedProgress);
               GlStateManager.translatef((float)k * -0.2785682F, 0.18344387F, 0.15731531F);
               GlStateManager.rotatef(-13.935F, 1.0F, 0.0F, 0.0F);
               GlStateManager.rotatef((float)k * 35.3F, 0.0F, 1.0F, 0.0F);
               GlStateManager.rotatef((float)k * -9.785F, 0.0F, 0.0F, 1.0F);
               float f6 = (float)stack.getUseDuration() - ((float)this.mc.player.getItemInUseCount() - partialTicks + 1.0F);
               float f8 = f6 / 20.0F;
               f8 = (f8 * f8 + f8 * 2.0F) / 3.0F;
               if (f8 > 1.0F) {
                  f8 = 1.0F;
               }

               if (f8 > 0.1F) {
                  float f10 = MathHelper.sin((f6 - 0.1F) * 1.3F);
                  float f11 = f8 - 0.1F;
                  float f12 = f10 * f11;
                  GlStateManager.translatef(f12 * 0.0F, f12 * 0.004F, f12 * 0.0F);
               }

               GlStateManager.translatef(f8 * 0.0F, f8 * 0.0F, f8 * 0.04F);
               GlStateManager.scalef(1.0F, 1.0F, 1.0F + f8 * 0.2F);
               GlStateManager.rotatef((float)k * 45.0F, 0.0F, -1.0F, 0.0F);
               break;
            case SPEAR:
               this.transformSideFirstPerson(enumhandside, equippedProgress);
               GlStateManager.translatef((float)k * -0.5F, 0.7F, 0.1F);
               GlStateManager.rotatef(-55.0F, 1.0F, 0.0F, 0.0F);
               GlStateManager.rotatef((float)k * 35.3F, 0.0F, 1.0F, 0.0F);
               GlStateManager.rotatef((float)k * -9.785F, 0.0F, 0.0F, 1.0F);
               float f5 = (float)stack.getUseDuration() - ((float)this.mc.player.getItemInUseCount() - partialTicks + 1.0F);
               float f7 = f5 / 10.0F;
               if (f7 > 1.0F) {
                  f7 = 1.0F;
               }

               if (f7 > 0.1F) {
                  float f9 = MathHelper.sin((f5 - 0.1F) * 1.3F);
                  float f2 = f7 - 0.1F;
                  float f3 = f9 * f2;
                  GlStateManager.translatef(f3 * 0.0F, f3 * 0.004F, f3 * 0.0F);
               }

               GlStateManager.translatef(0.0F, 0.0F, f7 * 0.2F);
               GlStateManager.scalef(1.0F, 1.0F, 1.0F + f7 * 0.2F);
               GlStateManager.rotatef((float)k * 45.0F, 0.0F, -1.0F, 0.0F);
            }
         } else if (player.isSpinAttacking()) {
            this.transformSideFirstPerson(enumhandside, equippedProgress);
            int i = flag1 ? 1 : -1;
            GlStateManager.translatef((float)i * -0.4F, 0.8F, 0.3F);
            GlStateManager.rotatef((float)i * 65.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef((float)i * -85.0F, 0.0F, 0.0F, 1.0F);
         } else {
            float f4 = -0.4F * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
            float f = 0.2F * MathHelper.sin(MathHelper.sqrt(swingProgress) * ((float)Math.PI * 2F));
            float f1 = -0.2F * MathHelper.sin(swingProgress * (float)Math.PI);
            int j = flag1 ? 1 : -1;
            GlStateManager.translatef((float)j * f4, f, f1);
            this.transformSideFirstPerson(enumhandside, equippedProgress);
            this.transformFirstPerson(enumhandside, swingProgress);
         }

         this.renderItemSide(player, stack, flag1 ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !flag1);
      }

      GlStateManager.popMatrix();
   }

   /**
    * Renders the overlays.
    */
   public void renderOverlays(float partialTicks) {
      GlStateManager.disableAlphaTest();
      if (this.mc.player.isEntityInsideOpaqueBlock()) {
         IBlockState iblockstate = this.mc.world.getBlockState(new BlockPos(this.mc.player));
         BlockPos overlayPos = new BlockPos(this.mc.player);
         EntityPlayer entityplayer = this.mc.player;

         for(int i = 0; i < 8; ++i) {
            double d0 = entityplayer.posX + (double)(((float)((i >> 0) % 2) - 0.5F) * entityplayer.width * 0.8F);
            double d1 = entityplayer.posY + (double)(((float)((i >> 1) % 2) - 0.5F) * 0.1F);
            double d2 = entityplayer.posZ + (double)(((float)((i >> 2) % 2) - 0.5F) * entityplayer.width * 0.8F);
            BlockPos blockpos = new BlockPos(d0, d1 + (double)entityplayer.getEyeHeight(), d2);
            IBlockState iblockstate1 = this.mc.world.getBlockState(blockpos);
            if (iblockstate1.causesSuffocation()) {
               iblockstate = iblockstate1;
               overlayPos = blockpos;
            }
         }

         if (iblockstate.getRenderType() != EnumBlockRenderType.INVISIBLE) {
            if (!net.minecraftforge.event.ForgeEventFactory.renderBlockOverlay(mc.player, partialTicks, net.minecraftforge.client.event.RenderBlockOverlayEvent.OverlayType.BLOCK, iblockstate, overlayPos))
            this.renderSuffocationOverlay(this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(iblockstate));
         }
      }

      if (!this.mc.player.isSpectator()) {
         if (this.mc.player.areEyesInFluid(FluidTags.WATER)) {
            if (!net.minecraftforge.event.ForgeEventFactory.renderWaterOverlay(mc.player, partialTicks))
            this.renderWaterOverlayTexture(partialTicks);
         }

         if (this.mc.player.isBurning()) {
            if (!net.minecraftforge.event.ForgeEventFactory.renderFireOverlay(mc.player, partialTicks))
            this.renderFireInFirstPerson();
         }
      }

      GlStateManager.enableAlphaTest();
   }

   /**
    * Renders the given sprite over the player's view
    */
   private void renderSuffocationOverlay(TextureAtlasSprite sprite) {
      this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      float f = 0.1F;
      GlStateManager.color4f(0.1F, 0.1F, 0.1F, 0.5F);
      GlStateManager.pushMatrix();
      float f1 = -1.0F;
      float f2 = 1.0F;
      float f3 = -1.0F;
      float f4 = 1.0F;
      float f5 = -0.5F;
      float f6 = sprite.getMinU();
      float f7 = sprite.getMaxU();
      float f8 = sprite.getMinV();
      float f9 = sprite.getMaxV();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
      bufferbuilder.pos(-1.0D, -1.0D, -0.5D).tex((double)f7, (double)f9).endVertex();
      bufferbuilder.pos(1.0D, -1.0D, -0.5D).tex((double)f6, (double)f9).endVertex();
      bufferbuilder.pos(1.0D, 1.0D, -0.5D).tex((double)f6, (double)f8).endVertex();
      bufferbuilder.pos(-1.0D, 1.0D, -0.5D).tex((double)f7, (double)f8).endVertex();
      tessellator.draw();
      GlStateManager.popMatrix();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   /**
    * Renders a texture that warps around based on the direction the player is looking. Texture needs to be bound before
    * being called. Used for the water overlay.
    */
   private void renderWaterOverlayTexture(float partialTicks) {
      this.mc.getTextureManager().bindTexture(RES_UNDERWATER_OVERLAY);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      float f = this.mc.player.getBrightness();
      GlStateManager.color4f(f, f, f, 0.1F);
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.pushMatrix();
      float f1 = 4.0F;
      float f2 = -1.0F;
      float f3 = 1.0F;
      float f4 = -1.0F;
      float f5 = 1.0F;
      float f6 = -0.5F;
      float f7 = -this.mc.player.rotationYaw / 64.0F;
      float f8 = this.mc.player.rotationPitch / 64.0F;
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
      bufferbuilder.pos(-1.0D, -1.0D, -0.5D).tex((double)(4.0F + f7), (double)(4.0F + f8)).endVertex();
      bufferbuilder.pos(1.0D, -1.0D, -0.5D).tex((double)(0.0F + f7), (double)(4.0F + f8)).endVertex();
      bufferbuilder.pos(1.0D, 1.0D, -0.5D).tex((double)(0.0F + f7), (double)(0.0F + f8)).endVertex();
      bufferbuilder.pos(-1.0D, 1.0D, -0.5D).tex((double)(4.0F + f7), (double)(0.0F + f8)).endVertex();
      tessellator.draw();
      GlStateManager.popMatrix();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableBlend();
   }

   /**
    * Renders the fire on the screen for first person mode. Arg: partialTickTime
    */
   private void renderFireInFirstPerson() {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.9F);
      GlStateManager.depthFunc(519);
      GlStateManager.depthMask(false);
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      float f = 1.0F;

      for(int i = 0; i < 2; ++i) {
         GlStateManager.pushMatrix();
         TextureAtlasSprite textureatlassprite = this.mc.getTextureMap().getSprite(ModelBakery.LOCATION_FIRE_1);
         this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
         float f1 = textureatlassprite.getMinU();
         float f2 = textureatlassprite.getMaxU();
         float f3 = textureatlassprite.getMinV();
         float f4 = textureatlassprite.getMaxV();
         float f5 = -0.5F;
         float f6 = 0.5F;
         float f7 = -0.5F;
         float f8 = 0.5F;
         float f9 = -0.5F;
         GlStateManager.translatef((float)(-(i * 2 - 1)) * 0.24F, -0.3F, 0.0F);
         GlStateManager.rotatef((float)(i * 2 - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
         bufferbuilder.pos(-0.5D, -0.5D, -0.5D).tex((double)f2, (double)f4).endVertex();
         bufferbuilder.pos(0.5D, -0.5D, -0.5D).tex((double)f1, (double)f4).endVertex();
         bufferbuilder.pos(0.5D, 0.5D, -0.5D).tex((double)f1, (double)f3).endVertex();
         bufferbuilder.pos(-0.5D, 0.5D, -0.5D).tex((double)f2, (double)f3).endVertex();
         tessellator.draw();
         GlStateManager.popMatrix();
      }

      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableBlend();
      GlStateManager.depthMask(true);
      GlStateManager.depthFunc(515);
   }

   public void tick() {
      this.prevEquippedProgressMainHand = this.equippedProgressMainHand;
      this.prevEquippedProgressOffHand = this.equippedProgressOffHand;
      EntityPlayerSP entityplayersp = this.mc.player;
      ItemStack itemstack = entityplayersp.getHeldItemMainhand();
      ItemStack itemstack1 = entityplayersp.getHeldItemOffhand();
      if (entityplayersp.isRowingBoat()) {
         this.equippedProgressMainHand = MathHelper.clamp(this.equippedProgressMainHand - 0.4F, 0.0F, 1.0F);
         this.equippedProgressOffHand = MathHelper.clamp(this.equippedProgressOffHand - 0.4F, 0.0F, 1.0F);
      } else {
         float f = entityplayersp.getCooledAttackStrength(1.0F);

         boolean requipM = net.minecraftforge.client.ForgeHooksClient.shouldCauseReequipAnimation(this.itemStackMainHand, itemstack, entityplayersp.inventory.currentItem);
         boolean requipO = net.minecraftforge.client.ForgeHooksClient.shouldCauseReequipAnimation(this.itemStackOffHand, itemstack1, -1);

         if (!requipM && !Objects.equals(this.itemStackMainHand, itemstack))
             this.itemStackMainHand = itemstack;
         if (!requipO && !Objects.equals(this.itemStackOffHand, itemstack1))
             this.itemStackOffHand = itemstack1;

         this.equippedProgressMainHand += MathHelper.clamp((Objects.equals(this.itemStackMainHand, itemstack) ? f * f * f : 0.0F) - this.equippedProgressMainHand, -0.4F, 0.4F);
         this.equippedProgressOffHand += MathHelper.clamp((float)(Objects.equals(this.itemStackOffHand, itemstack1) ? 1 : 0) - this.equippedProgressOffHand, -0.4F, 0.4F);
      }

      if (this.equippedProgressMainHand < 0.1F) {
         this.itemStackMainHand = itemstack;
      }

      if (this.equippedProgressOffHand < 0.1F) {
         this.itemStackOffHand = itemstack1;
      }

   }

   public void resetEquippedProgress(EnumHand hand) {
      if (hand == EnumHand.MAIN_HAND) {
         this.equippedProgressMainHand = 0.0F;
      } else {
         this.equippedProgressOffHand = 0.0F;
      }

   }
}