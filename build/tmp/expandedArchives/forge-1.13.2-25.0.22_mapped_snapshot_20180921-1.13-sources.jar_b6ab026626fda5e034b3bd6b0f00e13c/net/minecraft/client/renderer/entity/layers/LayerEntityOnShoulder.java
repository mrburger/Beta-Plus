package net.minecraft.client.renderer.entity.layers;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderParrot;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelParrot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerEntityOnShoulder implements LayerRenderer<EntityPlayer> {
   private final RenderManager renderManager;
   protected RenderLivingBase<? extends EntityLivingBase> leftRenderer;
   private ModelBase leftModel;
   private ResourceLocation leftResource;
   private UUID leftUniqueId;
   private EntityType<?> leftEntityClass;
   protected RenderLivingBase<? extends EntityLivingBase> rightRenderer;
   private ModelBase rightModel;
   private ResourceLocation rightResource;
   private UUID rightUniqueId;
   private EntityType<?> rightEntityClass;

   public LayerEntityOnShoulder(RenderManager renderManagerIn) {
      this.renderManager = renderManagerIn;
   }

   public void render(EntityPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (entitylivingbaseIn.getLeftShoulderEntity() != null || entitylivingbaseIn.getRightShoulderEntity() != null) {
         GlStateManager.enableRescaleNormal();
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         NBTTagCompound nbttagcompound = entitylivingbaseIn.getLeftShoulderEntity();
         if (!nbttagcompound.isEmpty()) {
            LayerEntityOnShoulder.DataHolder layerentityonshoulder$dataholder = this.renderEntityOnShoulder(entitylivingbaseIn, this.leftUniqueId, nbttagcompound, this.leftRenderer, this.leftModel, this.leftResource, this.leftEntityClass, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, true);
            this.leftUniqueId = layerentityonshoulder$dataholder.entityId;
            this.leftRenderer = layerentityonshoulder$dataholder.renderer;
            this.leftResource = layerentityonshoulder$dataholder.textureLocation;
            this.leftModel = layerentityonshoulder$dataholder.model;
            this.leftEntityClass = layerentityonshoulder$dataholder.entityType;
         }

         NBTTagCompound nbttagcompound1 = entitylivingbaseIn.getRightShoulderEntity();
         if (!nbttagcompound1.isEmpty()) {
            LayerEntityOnShoulder.DataHolder layerentityonshoulder$dataholder1 = this.renderEntityOnShoulder(entitylivingbaseIn, this.rightUniqueId, nbttagcompound1, this.rightRenderer, this.rightModel, this.rightResource, this.rightEntityClass, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, false);
            this.rightUniqueId = layerentityonshoulder$dataholder1.entityId;
            this.rightRenderer = layerentityonshoulder$dataholder1.renderer;
            this.rightResource = layerentityonshoulder$dataholder1.textureLocation;
            this.rightModel = layerentityonshoulder$dataholder1.model;
            this.rightEntityClass = layerentityonshoulder$dataholder1.entityType;
         }

         GlStateManager.disableRescaleNormal();
      }
   }

   private LayerEntityOnShoulder.DataHolder renderEntityOnShoulder(EntityPlayer entityPlayerIn, @Nullable UUID uuidIn, NBTTagCompound tagCompoundIn, RenderLivingBase<? extends EntityLivingBase> renderIn, ModelBase modelBaseIn, ResourceLocation locationTexture, EntityType<?> entityTypeIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageTicks, float headYaw, float headPitch, float scaleFactor, boolean leftSide) {
      if (uuidIn == null || !uuidIn.equals(tagCompoundIn.getUniqueId("UUID"))) {
         uuidIn = tagCompoundIn.getUniqueId("UUID");
         entityTypeIn = EntityType.getById(tagCompoundIn.getString("id"));
         if (entityTypeIn == EntityType.PARROT) {
            renderIn = new RenderParrot(this.renderManager);
            modelBaseIn = new ModelParrot();
            locationTexture = RenderParrot.PARROT_TEXTURES[tagCompoundIn.getInt("Variant")];
         }
      }

      renderIn.bindTexture(locationTexture);
      GlStateManager.pushMatrix();
      float f = entityPlayerIn.isSneaking() ? -1.3F : -1.5F;
      float f1 = leftSide ? 0.4F : -0.4F;
      GlStateManager.translatef(f1, f, 0.0F);
      if (entityTypeIn == EntityType.PARROT) {
         ageTicks = 0.0F;
      }

      modelBaseIn.setLivingAnimations(entityPlayerIn, limbSwing, limbSwingAmount, partialTicks);
      modelBaseIn.setRotationAngles(limbSwing, limbSwingAmount, ageTicks, headYaw, headPitch, scaleFactor, entityPlayerIn);
      modelBaseIn.render(entityPlayerIn, limbSwing, limbSwingAmount, ageTicks, headYaw, headPitch, scaleFactor);
      GlStateManager.popMatrix();
      return new LayerEntityOnShoulder.DataHolder(uuidIn, renderIn, modelBaseIn, locationTexture, entityTypeIn);
   }

   public boolean shouldCombineTextures() {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   class DataHolder {
      public UUID entityId;
      public RenderLivingBase<? extends EntityLivingBase> renderer;
      public ModelBase model;
      public ResourceLocation textureLocation;
      public EntityType<?> entityType;

      public DataHolder(UUID p_i48600_2_, RenderLivingBase<? extends EntityLivingBase> p_i48600_3_, ModelBase p_i48600_4_, ResourceLocation p_i48600_5_, EntityType<?> p_i48600_6_) {
         this.entityId = p_i48600_2_;
         this.renderer = p_i48600_3_;
         this.model = p_i48600_4_;
         this.textureLocation = p_i48600_5_;
         this.entityType = p_i48600_6_;
      }
   }
}