package net.minecraft.client.gui.spectator;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketSpectate;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerMenuObject implements ISpectatorMenuObject {
   private final GameProfile profile;
   private final ResourceLocation resourceLocation;

   public PlayerMenuObject(GameProfile profileIn) {
      this.profile = profileIn;
      Minecraft minecraft = Minecraft.getInstance();
      Map<Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(profileIn);
      if (map.containsKey(Type.SKIN)) {
         this.resourceLocation = minecraft.getSkinManager().loadSkin(map.get(Type.SKIN), Type.SKIN);
      } else {
         this.resourceLocation = DefaultPlayerSkin.getDefaultSkin(EntityPlayer.getUUID(profileIn));
      }

   }

   public void selectItem(SpectatorMenu menu) {
      Minecraft.getInstance().getConnection().sendPacket(new CPacketSpectate(this.profile.getId()));
   }

   public ITextComponent getSpectatorName() {
      return new TextComponentString(this.profile.getName());
   }

   public void renderIcon(float brightness, int alpha) {
      Minecraft.getInstance().getTextureManager().bindTexture(this.resourceLocation);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, (float)alpha / 255.0F);
      Gui.drawScaledCustomSizeModalRect(2, 2, 8.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
      Gui.drawScaledCustomSizeModalRect(2, 2, 40.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
   }

   public boolean isEnabled() {
      return true;
   }
}