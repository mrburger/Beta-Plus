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
	// Required Buttons
	private Button doneButton;
	private Button cancelButton;
	private Button snowButton;
	private CompoundNBT compound;

	public CreateAlphaWorldScreen(CreateWorldScreen parentIn, CompoundNBT compoundIn)
	{
		super(new TranslationTextComponent("createworld.customize.alpha.title"));
		parent = parentIn;
		compound = compoundIn;
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