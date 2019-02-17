package net.minecraft.client.renderer.tileentity;

import com.mojang.authlib.GameProfile;
import java.util.Arrays;
import java.util.Comparator;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAbstractSkull;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BannerTextures;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.model.ModelShield;
import net.minecraft.client.renderer.entity.model.ModelTrident;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityConduit;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.tileentity.TileEntityTrappedChest;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

@OnlyIn(Dist.CLIENT)
public class TileEntityItemStackRenderer {
   private static final TileEntityShulkerBox[] SHULKER_BOXES = Arrays.stream(EnumDyeColor.values()).sorted(Comparator.comparingInt(EnumDyeColor::getId)).map(TileEntityShulkerBox::new).toArray((p_199929_0_) -> {
      return new TileEntityShulkerBox[p_199929_0_];
   });
   private static final TileEntityShulkerBox SHULKER_BOX = new TileEntityShulkerBox((EnumDyeColor)null);
   public static TileEntityItemStackRenderer instance = new TileEntityItemStackRenderer();
   private final TileEntityChest chestBasic = new TileEntityChest();
   private final TileEntityChest chestTrap = new TileEntityTrappedChest();
   private final TileEntityEnderChest enderChest = new TileEntityEnderChest();
   private final TileEntityBanner banner = new TileEntityBanner();
   private final TileEntityBed bed = new TileEntityBed();
   private final TileEntitySkull skull = new TileEntitySkull();
   private final TileEntityConduit conduit = new TileEntityConduit();
   private final ModelShield modelShield = new ModelShield();
   private final ModelTrident trident = new ModelTrident();

   public void renderByItem(ItemStack itemStackIn) {
      Item item = itemStackIn.getItem();
      if (item instanceof ItemBanner) {
         this.banner.loadFromItemStack(itemStackIn, ((ItemBanner)item).getColor());
         TileEntityRendererDispatcher.instance.renderAsItem(this.banner);
      } else if (item instanceof ItemBlock && ((ItemBlock)item).getBlock() instanceof BlockBed) {
         this.bed.setColor(((BlockBed)((ItemBlock)item).getBlock()).getColor());
         TileEntityRendererDispatcher.instance.renderAsItem(this.bed);
      } else if (item == Items.SHIELD) {
         if (itemStackIn.getChildTag("BlockEntityTag") != null) {
            this.banner.loadFromItemStack(itemStackIn, ItemShield.getColor(itemStackIn));
            Minecraft.getInstance().getTextureManager().bindTexture(BannerTextures.SHIELD_DESIGNS.getResourceLocation(this.banner.getPatternResourceLocation(), this.banner.getPatternList(), this.banner.getColorList()));
         } else {
            Minecraft.getInstance().getTextureManager().bindTexture(BannerTextures.SHIELD_BASE_TEXTURE);
         }

         GlStateManager.pushMatrix();
         GlStateManager.scalef(1.0F, -1.0F, -1.0F);
         this.modelShield.render();
         if (itemStackIn.hasEffect()) {
            this.renderEffect(this.modelShield::render);
         }

         GlStateManager.popMatrix();
      } else if (item instanceof ItemBlock && ((ItemBlock)item).getBlock() instanceof BlockAbstractSkull) {
         GameProfile gameprofile = null;
         if (itemStackIn.hasTag()) {
            NBTTagCompound nbttagcompound = itemStackIn.getTag();
            if (nbttagcompound.contains("SkullOwner", 10)) {
               gameprofile = NBTUtil.readGameProfile(nbttagcompound.getCompound("SkullOwner"));
            } else if (nbttagcompound.contains("SkullOwner", 8) && !StringUtils.isBlank(nbttagcompound.getString("SkullOwner"))) {
               GameProfile gameprofile1 = new GameProfile((UUID)null, nbttagcompound.getString("SkullOwner"));
               gameprofile = TileEntitySkull.updateGameProfile(gameprofile1);
               nbttagcompound.removeTag("SkullOwner");
               nbttagcompound.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), gameprofile));
            }
         }

         if (TileEntitySkullRenderer.instance != null) {
            GlStateManager.pushMatrix();
            GlStateManager.disableCull();
            TileEntitySkullRenderer.instance.render(0.0F, 0.0F, 0.0F, (EnumFacing)null, 180.0F, ((BlockAbstractSkull)((ItemBlock)item).getBlock()).getSkullType(), gameprofile, -1, 0.0F);
            GlStateManager.enableCull();
            GlStateManager.popMatrix();
         }
      } else if (item == Items.TRIDENT) {
         Minecraft.getInstance().getTextureManager().bindTexture(ModelTrident.TEXTURE_LOCATION);
         GlStateManager.pushMatrix();
         GlStateManager.scalef(1.0F, -1.0F, -1.0F);
         this.trident.renderer();
         if (itemStackIn.hasEffect()) {
            this.renderEffect(this.trident::renderer);
         }

         GlStateManager.popMatrix();
      } else if (item instanceof ItemBlock && ((ItemBlock)item).getBlock() == Blocks.CONDUIT) {
         TileEntityRendererDispatcher.instance.renderAsItem(this.conduit);
      } else if (item == Blocks.ENDER_CHEST.asItem()) {
         TileEntityRendererDispatcher.instance.renderAsItem(this.enderChest);
      } else if (item == Blocks.TRAPPED_CHEST.asItem()) {
         TileEntityRendererDispatcher.instance.renderAsItem(this.chestTrap);
      } else if (Block.getBlockFromItem(item) instanceof BlockShulkerBox) {
         EnumDyeColor enumdyecolor = BlockShulkerBox.getColorFromItem(item);
         if (enumdyecolor == null) {
            TileEntityRendererDispatcher.instance.renderAsItem(SHULKER_BOX);
         } else {
            TileEntityRendererDispatcher.instance.renderAsItem(SHULKER_BOXES[enumdyecolor.getId()]);
         }
      } else {
         TileEntityRendererDispatcher.instance.renderAsItem(this.chestBasic);
      }

   }

   private void renderEffect(Runnable renderModelFunction) {
      GlStateManager.color3f(0.5019608F, 0.2509804F, 0.8F);
      Minecraft.getInstance().getTextureManager().bindTexture(ItemRenderer.RES_ITEM_GLINT);
      ItemRenderer.renderEffect(Minecraft.getInstance().getTextureManager(), renderModelFunction, 1);
   }
}