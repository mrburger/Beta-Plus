package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiCreateBuffetWorld extends GuiScreen {
   private static final List<ResourceLocation> BUFFET_GENERATORS = IRegistry.field_212627_p.getKeys().stream().filter((p_205307_0_) -> {
      return IRegistry.field_212627_p.func_212608_b(p_205307_0_).isOptionForBuffetWorld();
   }).collect(Collectors.toList());
   private final GuiCreateWorld parent;
   private final List<ResourceLocation> biomes = Lists.newArrayList();
   private final ResourceLocation[] biomeTypes = new ResourceLocation[IRegistry.field_212624_m.getKeys().size()];
   private String title;
   private GuiCreateBuffetWorld.BiomeList biomeList;
   private int field_205312_t;
   private GuiButton field_205313_u;

   public GuiCreateBuffetWorld(GuiCreateWorld parentIn, NBTTagCompound p_i49701_2_) {
      this.parent = parentIn;
      int i = 0;

      for(ResourceLocation resourcelocation : IRegistry.field_212624_m.getKeys()) {
         this.biomeTypes[i] = resourcelocation;
         ++i;
      }

      Arrays.sort(this.biomeTypes, (p_210140_0_, p_210140_1_) -> {
         String s = IRegistry.field_212624_m.func_212608_b(p_210140_0_).getDisplayName().getString();
         String s1 = IRegistry.field_212624_m.func_212608_b(p_210140_1_).getDisplayName().getString();
         return s.compareTo(s1);
      });
      this.deserialize(p_i49701_2_);
   }

   private void deserialize(NBTTagCompound p_210506_1_) {
      if (p_210506_1_.contains("chunk_generator", 10) && p_210506_1_.getCompound("chunk_generator").contains("type", 8)) {
         ResourceLocation resourcelocation = new ResourceLocation(p_210506_1_.getCompound("chunk_generator").getString("type"));

         for(int i = 0; i < BUFFET_GENERATORS.size(); ++i) {
            if (BUFFET_GENERATORS.get(i).equals(resourcelocation)) {
               this.field_205312_t = i;
               break;
            }
         }
      }

      if (p_210506_1_.contains("biome_source", 10) && p_210506_1_.getCompound("biome_source").contains("biomes", 9)) {
         NBTTagList nbttaglist = p_210506_1_.getCompound("biome_source").getList("biomes", 8);

         for(int j = 0; j < nbttaglist.size(); ++j) {
            this.biomes.add(new ResourceLocation(nbttaglist.getString(j)));
         }
      }

   }

   private NBTTagCompound serialize() {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      NBTTagCompound nbttagcompound1 = new NBTTagCompound();
      nbttagcompound1.setString("type", IRegistry.field_212625_n.getKey(BiomeProviderType.FIXED).toString());
      NBTTagCompound nbttagcompound2 = new NBTTagCompound();
      NBTTagList nbttaglist = new NBTTagList();

      for(ResourceLocation resourcelocation : this.biomes) {
         nbttaglist.add((INBTBase)(new NBTTagString(resourcelocation.toString())));
      }

      nbttagcompound2.setTag("biomes", nbttaglist);
      nbttagcompound1.setTag("options", nbttagcompound2);
      NBTTagCompound nbttagcompound3 = new NBTTagCompound();
      NBTTagCompound nbttagcompound4 = new NBTTagCompound();
      nbttagcompound3.setString("type", BUFFET_GENERATORS.get(this.field_205312_t).toString());
      nbttagcompound4.setString("default_block", "minecraft:stone");
      nbttagcompound4.setString("default_fluid", "minecraft:water");
      nbttagcompound3.setTag("options", nbttagcompound4);
      nbttagcompound.setTag("biome_source", nbttagcompound1);
      nbttagcompound.setTag("chunk_generator", nbttagcompound3);
      return nbttagcompound;
   }

   @Nullable
   public IGuiEventListener getFocused() {
      return this.biomeList;
   }

   /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
   protected void initGui() {
      this.mc.keyboardListener.enableRepeatEvents(true);
      this.title = I18n.format("createWorld.customize.buffet.title");
      this.biomeList = new GuiCreateBuffetWorld.BiomeList();
      this.children.add(this.biomeList);
      this.addButton(new GuiButton(2, (this.width - 200) / 2, 40, 200, 20, I18n.format("createWorld.customize.buffet.generatortype") + " " + I18n.format(Util.makeTranslationKey("generator", BUFFET_GENERATORS.get(this.field_205312_t)))) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiCreateBuffetWorld.this.field_205312_t++;
            if (GuiCreateBuffetWorld.this.field_205312_t >= GuiCreateBuffetWorld.BUFFET_GENERATORS.size()) {
               GuiCreateBuffetWorld.this.field_205312_t = 0;
            }

            this.displayString = I18n.format("createWorld.customize.buffet.generatortype") + " " + I18n.format(Util.makeTranslationKey("generator", GuiCreateBuffetWorld.BUFFET_GENERATORS.get(GuiCreateBuffetWorld.this.field_205312_t)));
         }
      });
      this.field_205313_u = this.addButton(new GuiButton(0, this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("gui.done")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiCreateBuffetWorld.this.parent.chunkProviderSettingsJson = GuiCreateBuffetWorld.this.serialize();
            GuiCreateBuffetWorld.this.mc.displayGuiScreen(GuiCreateBuffetWorld.this.parent);
         }
      });
      this.addButton(new GuiButton(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiCreateBuffetWorld.this.mc.displayGuiScreen(GuiCreateBuffetWorld.this.parent);
         }
      });
      this.func_205306_h();
   }

   public void func_205306_h() {
      this.field_205313_u.enabled = !this.biomes.isEmpty();
   }

   /**
    * Draws the screen and all the components in it.
    */
   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawBackground(0);
      this.biomeList.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 8, 16777215);
      this.drawCenteredString(this.fontRenderer, I18n.format("createWorld.customize.buffet.generator"), this.width / 2, 30, 10526880);
      this.drawCenteredString(this.fontRenderer, I18n.format("createWorld.customize.buffet.biome"), this.width / 2, 68, 10526880);
      super.render(mouseX, mouseY, partialTicks);
   }

   @OnlyIn(Dist.CLIENT)
   class BiomeList extends GuiSlot {
      private BiomeList() {
         super(GuiCreateBuffetWorld.this.mc, GuiCreateBuffetWorld.this.width, GuiCreateBuffetWorld.this.height, 80, GuiCreateBuffetWorld.this.height - 37, 16);
      }

      protected int getSize() {
         return GuiCreateBuffetWorld.this.biomeTypes.length;
      }

      /**
       * Called when the mouse is clicked onto an entry.
       *  
       * @return true if the entry did something with the click and it should be selected.
       *  
       * @param index Index of the entry
       * @param button The mouse button that was pressed.
       * @param mouseX The mouse X coordinate.
       * @param mouseY The mouse Y coordinate.
       */
      protected boolean mouseClicked(int index, int button, double mouseX, double mouseY) {
         GuiCreateBuffetWorld.this.biomes.clear();
         GuiCreateBuffetWorld.this.biomes.add(GuiCreateBuffetWorld.this.biomeTypes[index]);
         GuiCreateBuffetWorld.this.func_205306_h();
         return true;
      }

      /**
       * Returns true if the element passed in is currently selected
       */
      protected boolean isSelected(int slotIndex) {
         return GuiCreateBuffetWorld.this.biomes.contains(GuiCreateBuffetWorld.this.biomeTypes[slotIndex]);
      }

      protected void drawBackground() {
      }

      protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks) {
         this.drawString(GuiCreateBuffetWorld.this.fontRenderer, IRegistry.field_212624_m.func_212608_b(GuiCreateBuffetWorld.this.biomeTypes[slotIndex]).getDisplayName().getString(), xPos + 5, yPos + 2, 16777215);
      }
   }
}