package com.mrburgerus.betaplus.client.gui;

import com.google.common.annotations.Beta;
import com.mrburgerus.betaplus.BetaPlus;
import com.mrburgerus.betaplus.world.alpha_plus.AlphaPlusGenSettings;
import com.mrburgerus.betaplus.world.alpha_plus.WorldTypeAlphaPlus;
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
	private NBTTagCompound compound;


	//TODO: PROPERLY SET THE VALUE OF SNOW WORLD WHEN RE-CREATING WORLDS
	public GuiCreateAlphaWorld(GuiCreateWorld parentIn, NBTTagCompound compoundIn)
	{
		parent = parentIn;
		compound = compoundIn;
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
		/* Copied from GuiScreen */
		for(int i = 0; i < this.buttons.size(); ++i) {
			this.buttons.get(i).render(mouseX, mouseY, partialTicks);
		}
	}

	/* Taken from GuiCreateBuffetWorld.class, Modified */
	/* is it actually needed? */
	/*
	private void deserialize(NBTTagCompound compound) {
		int lvt_3_2_;
		if (compound.contains("chunk_generator", 10) && compound.getCompound("chunk_generator").contains("type", 8)) {
			ResourceLocation lvt_2_1_ = new ResourceLocation(compound.getCompound("chunk_generator").getString("type"));

			for(lvt_3_2_ = 0; lvt_3_2_ < BUFFET_GENERATORS.size(); ++lvt_3_2_) {
				if (((ResourceLocation)BUFFET_GENERATORS.get(lvt_3_2_)).equals(lvt_2_1_)) {
					this.field_205312_t = lvt_3_2_;
					break;
				}
			}
		}

		if (compound.contains("biome_source", 10) && compound.getCompound("biome_source").contains("biomes", 9)) {
			NBTTagList lvt_2_2_ = compound.getCompound("biome_source").getList("biomes", 8);

			for(lvt_3_2_ = 0; lvt_3_2_ < lvt_2_2_.size(); ++lvt_3_2_) {
				this.biomes.add(new ResourceLocation(lvt_2_2_.getString(lvt_3_2_)));
			}
		}

	}
	*/

	/* Not Working */
	private NBTTagCompound serialize() {
		NBTTagCompound generatorNBT = new NBTTagCompound();
		generatorNBT.setString(WorldTypeAlphaPlus.SNOW_WORLD_TAG, String.valueOf(isSnowWorld));
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
