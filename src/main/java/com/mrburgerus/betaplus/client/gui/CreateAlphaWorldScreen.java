package com.mrburgerus.betaplus.client.gui;

import com.mrburgerus.betaplus.world.alpha_plus.WorldTypeAlphaPlus;
import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreateAlphaWorldScreen extends Screen
{
	//Fields
	private final CreateWorldScreen parent;
	// False By Default
	private boolean isSnowWorld;
	private CompoundNBT compound;
	// Required Buttons
	private Button doneButton;
	private Button cancelButton;
	private Button snowButton;


	public CreateAlphaWorldScreen(CreateWorldScreen parentIn, CompoundNBT compoundIn)
	{
		super(new TranslationTextComponent("createworld.customize.alpha.title"));
		parent = parentIn;
		compound = compoundIn;
		// Added so that it can use previous values
		// COULD CAUSE ISSUES IF SNOW WORLD TAG DOES NOT EXIST
		isSnowWorld = this.compound.getBoolean(WorldTypeAlphaPlus.SNOW_WORLD_TAG);
	}

	@Override
	protected void init()
	{
		doneButton = this.addButton(new Button(width / 2 - 55, 160, 50, 20, I18n.format("gui.yes"), exitStuff ->
		{
			CreateAlphaWorldScreen.this.parent.chunkProviderSettingsJson = CreateAlphaWorldScreen.this.serialize();
			this.minecraft.displayGuiScreen(this.parent);
		}
		));

		cancelButton = this.addButton(new Button(width / 2 + 5, 160, 50, 20, I18n.format("gui.no"), closeStuff ->
		{
			this.closeGui();
		}
		));

		snowButton = this.addButton(new Button(this.width / 2 - 75, this.height / 2 - 10, 150, 20, I18n.format("betaplus.isSnowWorld"), snowStuff ->
		{
			isSnowWorld = !isSnowWorld;
			updateDisplay();
		}
		));
		updateDisplay();
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

	// Update the GUI display
	private void updateDisplay()
	{
		snowButton.setMessage(this.getFormattedToggle(I18n.format("betaplus.isSnowWorld"), isSnowWorld));
	}

	private CompoundNBT serialize() {
		CompoundNBT generatorNBT = new CompoundNBT();
		generatorNBT.putBoolean(WorldTypeAlphaPlus.SNOW_WORLD_TAG, isSnowWorld);
		return generatorNBT;
	}

	// Written for Ease
	private void closeGui()
	{
		CreateAlphaWorldScreen.this.minecraft.displayGuiScreen(CreateAlphaWorldScreen.this.parent);
	}

	// Written to reduce code clutter
	private String getFormattedToggle(String prefix, boolean value)
	{
		if (value)
			return prefix + ": " + I18n.format("options.on");

		return prefix + ": " + I18n.format("options.off");
	}
}