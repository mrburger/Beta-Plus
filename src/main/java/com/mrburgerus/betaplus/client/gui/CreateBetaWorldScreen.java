package com.mrburgerus.betaplus.client.gui;

import com.mrburgerus.betaplus.world.beta_plus.WorldTypeBetaPlus;
import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;


//TODO:
// Add Biome Selector Button
// Add Scale Sliders
// Add more FEATURES
// Remove many of the Config Options from the config file. Only biomes should be assigned there.
public class CreateBetaWorldScreen extends Screen
{
	// Required
	private final CreateWorldScreen parent;
	private CompoundNBT compound;
	// Buttons
	private Button doneButton;
	private Button cancelButton;
	private Button oldCavesButton;
	private Button providerButton;
	// Values
	private boolean useOldCaves;
	private int providerSelect;

	//TODO: MAKE PROGRAMMATIC
	private int numProviders = 2;

	public CreateBetaWorldScreen(CreateWorldScreen screen, CompoundNBT settings)
	{
		super(new TranslationTextComponent("createworld.customize.beta.title"));
		parent = screen;
		compound = settings;
		// Pre-assign
		useOldCaves = compound.getBoolean(WorldTypeBetaPlus.OLD_CAVES_TAG);
		providerSelect = compound.getInt(WorldTypeBetaPlus.BIOME_PROVIDER_TAG);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
	{
		this.renderBackground();
		/* Copied from GuiScreen */
		for(int i = 0; i < this.buttons.size(); ++i) {
			this.buttons.get(i).render(mouseX, mouseY, partialTicks);
		}
	}

	@Override
	protected void init()
	{
		doneButton = this.addButton(new Button(width / 2 - 55, 160, 50, 20, I18n.format("gui.yes"), exitStuff ->
		{
			this.parent.chunkProviderSettingsJson = this.serialize();
			this.minecraft.displayGuiScreen(this.parent);
		}
		));

		cancelButton = this.addButton(new Button(width / 2 + 5, 160, 50, 20, I18n.format("gui.no"), closeStuff ->
		{
			this.closeGui();
		}
		));

		oldCavesButton = this.addButton(new Button(this.width / 2 - 75, this.height / 2 - 10, 150, 20, I18n.format("betaplus.useOldCaves"), snowStuff ->
		{
			useOldCaves = !useOldCaves;
			updateDisplay();
		}
		));

		providerButton = this.addButton(new Button(this.width / 2 - 75, this.height / 2 - 32, 150, 20, I18n.format("betaplus.provider"), providerStuff ->
		{
			// Modulo to prevent overflow
			providerSelect = ++providerSelect % numProviders;
			updateDisplay();
		}
		));

		updateDisplay();
	}

	// Update the GUI display
	private void updateDisplay()
	{
		oldCavesButton.setMessage(this.getFormattedToggle(I18n.format("betaplus.useOldCaves"), useOldCaves));
		providerButton.setMessage(I18n.format("betaplus.provider") + ": " + getProviderString(providerSelect));
	}

	// Put values into NBT
	private CompoundNBT serialize()
	{
		CompoundNBT generatorNBT = new CompoundNBT();
		// Add values
		generatorNBT.putBoolean(WorldTypeBetaPlus.OLD_CAVES_TAG, useOldCaves);
		generatorNBT.putInt(WorldTypeBetaPlus.BIOME_PROVIDER_TAG, providerSelect);

		return generatorNBT;
	}

	// Written for Ease
	private void closeGui()
	{
		this.minecraft.displayGuiScreen(this.parent);
	}

	// Written to reduce code clutter
	private String getFormattedToggle(String prefix, boolean value)
	{
		if (value)
			return prefix + ": " + I18n.format("options.on");

		return prefix + ": " + I18n.format("options.off");
	}

	private String getProviderString(int select)
	{
		String provide;
		switch (select)
		{
			case 0:
				provide = "Old";
				break;
			case 1:
				provide = "New";
				break;
			default:
				provide = "????";
		}
		return provide;
	}
}
