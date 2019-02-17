package net.minecraft.client.resources;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiResourcePackSelected;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.resources.PackCompatibility;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ResourcePackListEntryFound extends GuiListExtended.IGuiListEntry<ResourcePackListEntryFound> {
   private static final ResourceLocation field_195028_e = new ResourceLocation("textures/gui/resource_packs.png");
   private static final ITextComponent field_195029_f = new TextComponentTranslation("resourcePack.incompatible");
   private static final ITextComponent field_195030_g = new TextComponentTranslation("resourcePack.incompatible.confirm.title");
   protected final Minecraft field_195026_c;
   protected final GuiScreenResourcePacks field_195027_d;
   private final ResourcePackInfoClient resourcePackEntry;

   public ResourcePackListEntryFound(GuiScreenResourcePacks p_i47650_1_, ResourcePackInfoClient p_i47650_2_) {
      this.field_195027_d = p_i47650_1_;
      this.field_195026_c = Minecraft.getInstance();
      this.resourcePackEntry = p_i47650_2_;
   }

   public void func_195020_a(GuiResourcePackSelected p_195020_1_) {
      this.func_195017_i().getPriority().func_198993_a(p_195020_1_.getChildren(), this, ResourcePackListEntryFound::func_195017_i, true);
   }

   protected void bindResourcePackIcon() {
      this.resourcePackEntry.func_195808_a(this.field_195026_c.getTextureManager());
   }

   protected PackCompatibility func_195019_f() {
      return this.resourcePackEntry.getCompatibility();
   }

   protected String getResourcePackDescription() {
      return this.resourcePackEntry.getDescription().getFormattedText();
   }

   protected String getResourcePackName() {
      return this.resourcePackEntry.func_195789_b().getFormattedText();
   }

   public ResourcePackInfoClient func_195017_i() {
      return this.resourcePackEntry;
   }

   public void drawEntry(int entryWidth, int entryHeight, int mouseX, int mouseY, boolean p_194999_5_, float partialTicks) {
      int i = this.getY();
      int j = this.getX();
      PackCompatibility packcompatibility = this.func_195019_f();
      if (!packcompatibility.func_198968_a()) {
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         Gui.drawRect(j - 1, i - 1, j + entryWidth - 9, i + entryHeight + 1, -8978432);
      }

      this.bindResourcePackIcon();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      Gui.drawModalRectWithCustomSizedTexture(j, i, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
      String s = this.getResourcePackName();
      String s1 = this.getResourcePackDescription();
      if (this.func_195024_j() && (this.field_195026_c.gameSettings.touchscreen || p_194999_5_)) {
         this.field_195026_c.getTextureManager().bindTexture(field_195028_e);
         Gui.drawRect(j, i, j + 32, i + 32, -1601138544);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         int k = mouseX - j;
         int l = mouseY - i;
         if (!packcompatibility.func_198968_a()) {
            s = field_195029_f.getFormattedText();
            s1 = packcompatibility.func_198967_b().getFormattedText();
         }

         if (this.func_195025_k()) {
            if (k < 32) {
               Gui.drawModalRectWithCustomSizedTexture(j, i, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
               Gui.drawModalRectWithCustomSizedTexture(j, i, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }
         } else {
            if (this.func_195022_l()) {
               if (k < 16) {
                  Gui.drawModalRectWithCustomSizedTexture(j, i, 32.0F, 32.0F, 32, 32, 256.0F, 256.0F);
               } else {
                  Gui.drawModalRectWithCustomSizedTexture(j, i, 32.0F, 0.0F, 32, 32, 256.0F, 256.0F);
               }
            }

            if (this.func_195023_m()) {
               if (k < 32 && k > 16 && l < 16) {
                  Gui.drawModalRectWithCustomSizedTexture(j, i, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
               } else {
                  Gui.drawModalRectWithCustomSizedTexture(j, i, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
               }
            }

            if (this.func_195021_n()) {
               if (k < 32 && k > 16 && l > 16) {
                  Gui.drawModalRectWithCustomSizedTexture(j, i, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
               } else {
                  Gui.drawModalRectWithCustomSizedTexture(j, i, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
               }
            }
         }
      }

      int j1 = this.field_195026_c.fontRenderer.getStringWidth(s);
      if (j1 > 157) {
         s = this.field_195026_c.fontRenderer.trimStringToWidth(s, 157 - this.field_195026_c.fontRenderer.getStringWidth("...")) + "...";
      }

      this.field_195026_c.fontRenderer.drawStringWithShadow(s, (float)(j + 32 + 2), (float)(i + 1), 16777215);
      List<String> list = this.field_195026_c.fontRenderer.listFormattedStringToWidth(s1, 157);

      for(int i1 = 0; i1 < 2 && i1 < list.size(); ++i1) {
         this.field_195026_c.fontRenderer.drawStringWithShadow(list.get(i1), (float)(j + 32 + 2), (float)(i + 12 + 10 * i1), 8421504);
      }

   }

   protected boolean func_195024_j() {
      return !this.resourcePackEntry.func_195798_h() || !this.resourcePackEntry.func_195797_g();
   }

   protected boolean func_195025_k() {
      return !this.field_195027_d.func_195312_c(this);
   }

   protected boolean func_195022_l() {
      return this.field_195027_d.func_195312_c(this) && !this.resourcePackEntry.func_195797_g();
   }

   protected boolean func_195023_m() {
      List<ResourcePackListEntryFound> list = this.getList().getChildren();
      int i = list.indexOf(this);
      return i > 0 && !(list.get(i - 1)).resourcePackEntry.func_195798_h();
   }

   protected boolean func_195021_n() {
      List<ResourcePackListEntryFound> list = this.getList().getChildren();
      int i = list.indexOf(this);
      return i >= 0 && i < list.size() - 1 && !(list.get(i + 1)).resourcePackEntry.func_195798_h();
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      double d0 = p_mouseClicked_1_ - (double)this.getX();
      double d1 = p_mouseClicked_3_ - (double)this.getY();
      if (this.func_195024_j() && d0 <= 32.0D) {
         if (this.func_195025_k()) {
            this.func_195018_o().markChanged();
            PackCompatibility packcompatibility = this.func_195019_f();
            if (packcompatibility.func_198968_a()) {
               this.func_195018_o().func_195301_a(this);
            } else {
               String s1 = field_195030_g.getFormattedText();
               String s = packcompatibility.func_198971_c().getFormattedText();
               this.field_195026_c.displayGuiScreen(new GuiYesNo((p_210102_1_, p_210102_2_) -> {
                  this.field_195026_c.displayGuiScreen(this.func_195018_o());
                  if (p_210102_1_) {
                     this.func_195018_o().func_195301_a(this);
                  }

               }, s1, s, 0));
            }

            return true;
         }

         if (d0 < 16.0D && this.func_195022_l()) {
            this.func_195018_o().func_195305_b(this);
            return true;
         }

         if (d0 > 16.0D && d1 < 16.0D && this.func_195023_m()) {
            List<ResourcePackListEntryFound> list1 = this.getList().getChildren();
            int j = list1.indexOf(this);
            list1.remove(this);
            list1.add(j - 1, this);
            this.func_195018_o().markChanged();
            return true;
         }

         if (d0 > 16.0D && d1 > 16.0D && this.func_195021_n()) {
            List<ResourcePackListEntryFound> list = this.getList().getChildren();
            int i = list.indexOf(this);
            list.remove(this);
            list.add(i + 1, this);
            this.func_195018_o().markChanged();
            return true;
         }
      }

      return false;
   }

   public GuiScreenResourcePacks func_195018_o() {
      return this.field_195027_d;
   }
}