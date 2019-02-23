package com.mrburgerus.betaplus.client.gui;

import com.google.common.annotations.Beta;
import com.mrburgerus.betaplus.BetaPlus;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateBuffetWorld;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiCreateAlphaWorld extends GuiScreen
{
	//Fields
	private final GuiCreateWorld parent;
	// False By Default
	private boolean isSnowWorld = false;
	// Required Buttons
	private int confirmId = 0;
	private int cancelId = 1;
	private GuiButton snowButton;
	private final int snowButtonId = 3;


	public GuiCreateAlphaWorld(GuiCreateWorld parentIn)
	{
		parent = parentIn;
	}

	@Override
	protected void initGui()
	{
		//super.initGui();
		buttons.clear();
		labels.clear();

		this.addButton(new GuiButton(confirmId, width / 2 - 55, 160, 50, 20, I18n.format("gui.yes")){
			@Override
			public void onClick(double mouseX, double mouseY)
			{
				// Save the value
				GuiCreateAlphaWorld.this.parent.chunkProviderSettingsJson = GuiCreateAlphaWorld.this.serialize();
				closeGui();

			}
		});

		this.addButton(new GuiButton(cancelId, width / 2 + 5, 160, 50, 20, I18n.format("gui.no")){
			@Override
			public void onClick(double mouseX, double mouseY)
			{
				// Do not save value
				closeGui();
			}
		});

		this.snowButton = this.addButton(new GuiButton(snowButtonId, this.width / 2 - 75, this.height / 2 - 10, 150, 20, getFormattedToggle(I18n.format("betaplus.isSnowWorld"), isSnowWorld)) {
			public void onClick(double mouseX, double mouseY)
			{
				isSnowWorld = !isSnowWorld;
				snowButton.displayString = getFormattedToggle(I18n.format("betaplus.isSnowWorld"), isSnowWorld);
			}
		});
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
	{
		this.drawDefaultBackground();
		drawCenteredString(mc.fontRenderer, "NOT WORKING!", this.width / 2, this.height / 2, 0xFFFFFF);
		/* Copied from GuiScreen */
		for(int i = 0; i < this.buttons.size(); ++i) {
			this.buttons.get(i).render(mouseX, mouseY, partialTicks);
		}
	}

	/* Not Working */
	private NBTTagCompound serialize() {
		NBTTagCompound generatorNBT = new NBTTagCompound();
		/*
		NBTTagCompound biomeSourceNBT = new NBTTagCompound();
		biomeSourceNBT.setString("type", IRegistry.field_212625_n.getKey(BiomeProviderType.FIXED).toString());
		NBTTagCompound optionsNBT = new NBTTagCompound();
		NBTTagList nbttaglist = new NBTTagList();


		optionsNBT.setTag("biomes", nbttaglist);
		biomeSourceNBT.setTag("options", optionsNBT);
		NBTTagCompound generatorTypeNBT = new NBTTagCompound();
		NBTTagCompound nbttagcompound4 = new NBTTagCompound();
		generatorTypeNBT.setString("type", "LS");
		nbttagcompound4.setString("default_block", "minecraft:stone");
		nbttagcompound4.setString("default_fluid", "minecraft:water");
		generatorTypeNBT.setTag("options", nbttagcompound4);
		generatorNBT.setTag("biome_source", biomeSourceNBT);
		generatorNBT.setTag("chunk_generator", generatorTypeNBT);
		*/
		generatorNBT.setString("snowWorld", String.valueOf(isSnowWorld));
		System.out.println(generatorNBT.toFormattedComponent().getFormattedText());
		return generatorNBT;
	}

	// Written for Ease
	private void closeGui()
	{
		GuiCreateAlphaWorld.this.mc.displayGuiScreen(GuiCreateAlphaWorld.this.parent);
	}

	// Written to reduce code clutter
	private String getFormattedToggle(String prefix, boolean value)
	{
		if (value)
			return prefix + ": " + I18n.format("options.on");

		return prefix + ": " + I18n.format("options.off");
	}
}
