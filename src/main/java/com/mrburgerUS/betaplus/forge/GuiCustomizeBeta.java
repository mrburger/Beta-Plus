package com.mrburgerUS.betaplus.forge;

import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.Random;

//HIGLY WIP

@SideOnly(Side.CLIENT)
public class GuiCustomizeBeta extends GuiScreen implements GuiSlider.FormatHelper, GuiPageButtonList.GuiResponder
{
	private final GuiCreateWorld parent;
	protected String title = "Customize Beta Features";
	protected String subtitle = "Page 1 of 3";
	protected String pageTitle = "guicus";
	private GuiButton done;
	private GuiButton randomize;
	private GuiButton defaults;
	private GuiButton confirm;
	private GuiButton cancel;
	private GuiButton presets;
	private boolean settingsModified;
	private int confirmMode;
	private boolean confirmDismissed;
	private final Random random = new Random();

	public GuiCustomizeBeta(GuiScreen parentIn, String p_i45521_2_)
	{
		this.parent = (GuiCreateWorld) parentIn;
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
	 * window resizes, the buttonList is cleared beforehand.
	 */
	public void initGui()
	{
		int i = 0;
		int j = 0;

		this.title = I18n.format("options.customizeTitle");
		this.buttonList.clear();
		this.defaults = this.addButton(new GuiButton(304, this.width / 2 - 187, this.height - 27, 90, 20, I18n.format("createWorld.customize.custom.defaults")));
		this.randomize = this.addButton(new GuiButton(301, this.width / 2 - 92, this.height - 27, 90, 20, I18n.format("createWorld.customize.custom.randomize")));
		this.presets = this.addButton(new GuiButton(305, this.width / 2 + 3, this.height - 27, 90, 20, I18n.format("createWorld.customize.custom.presets")));
		this.done = this.addButton(new GuiButton(300, this.width / 2 + 98, this.height - 27, 90, 20, I18n.format("gui.done")));
		this.defaults.enabled = this.settingsModified;
		this.confirm = new GuiButton(306, this.width / 2 - 55, 160, 50, 20, I18n.format("gui.yes"));
		this.confirm.visible = false;
		this.buttonList.add(this.confirm);
		this.cancel = new GuiButton(307, this.width / 2 + 5, 160, 50, 20, I18n.format("gui.no"));
		this.cancel.visible = false;
		this.buttonList.add(this.cancel);

		if (this.confirmMode != 0)
		{
			this.confirm.visible = true;
			this.cancel.visible = true;
		}

	}

	/**
	 * Handles mouse input.
	 */
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();
	}

	@Override
	public void setEntryValue(int id, boolean value)
	{
		return;
	}

	@Override
	public void setEntryValue(int id, float value)
	{
		return;
	}

	public void setEntryValue(int id, String value)
	{
		float f = 0.0F;

		try
		{
			f = Float.parseFloat(value);
		}
		catch (NumberFormatException var5)
		{

		}

	}

	private void setSettingsModified(boolean modified)
	{
		this.settingsModified = modified;
		this.defaults.enabled = modified;
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
	 */
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if (button.enabled)
		{
			switch (button.id)
			{

			}
		}
	}

	private void enterConfirmation(int confirmModeIn)
	{
		this.confirmMode = confirmModeIn;
		this.setConfirmationControls(true);
	}

	private void exitConfirmation() throws IOException
	{
		switch (this.confirmMode)
		{
			case 300:
				break;
			case 304:
				break;
		}

		this.confirmMode = 0;
		this.confirmDismissed = true;
		this.setConfirmationControls(false);
	}

	private void setConfirmationControls(boolean visible)
	{
		this.confirm.visible = visible;
		this.cancel.visible = visible;
		this.randomize.enabled = !visible;
		this.done.enabled = !visible;
		this.defaults.enabled = this.settingsModified && !visible;
		this.presets.enabled = !visible;
	}


	protected void keyTyped(char typedChar, int keyCode) throws IOException
	{
		super.keyTyped(typedChar, keyCode);

		if (this.confirmMode == 0)
		{
			switch (keyCode)
			{
				case 200:
					this.modifyFocusValue(1.0F);
					break;
				case 208:
					this.modifyFocusValue(-1.0F);
					break;
				default:
					break;
			}
		}
	}

	private void modifyFocusValue(float p_175327_1_)
	{
		return;
	}


	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);

		if (this.confirmMode == 0 && !this.confirmDismissed)
		{
			return;
		}
	}

	protected void mouseReleased(int mouseX, int mouseY, int state)
	{
		super.mouseReleased(mouseX, mouseY, state);

		if (this.confirmDismissed)
		{
			this.confirmDismissed = false;
		}
		else if (this.confirmMode == 0)
		{
			return;
		}
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 2, 16777215);
		this.drawCenteredString(this.fontRenderer, this.subtitle, this.width / 2, 12, 16777215);
		this.drawCenteredString(this.fontRenderer, this.pageTitle, this.width / 2, 22, 16777215);
		super.drawScreen(mouseX, mouseY, partialTicks);

		if (this.confirmMode != 0)
		{
			drawRect(0, 0, this.width, this.height, Integer.MIN_VALUE);
			this.drawHorizontalLine(this.width / 2 - 91, this.width / 2 + 90, 99, -2039584);
			this.drawHorizontalLine(this.width / 2 - 91, this.width / 2 + 90, 185, -6250336);
			this.drawVerticalLine(this.width / 2 - 91, 99, 185, -2039584);
			this.drawVerticalLine(this.width / 2 + 90, 99, 185, -6250336);
			float f = 85.0F;
			float f1 = 180.0F;
			GlStateManager.disableLighting();
			GlStateManager.disableFog();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			this.mc.getTextureManager().bindTexture(OPTIONS_BACKGROUND);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			float f2 = 32.0F;
			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
			bufferbuilder.pos((double) (this.width / 2 - 90), 185.0D, 0.0D).tex(0.0D, 2.65625D).color(64, 64, 64, 64).endVertex();
			bufferbuilder.pos((double) (this.width / 2 + 90), 185.0D, 0.0D).tex(5.625D, 2.65625D).color(64, 64, 64, 64).endVertex();
			bufferbuilder.pos((double) (this.width / 2 + 90), 100.0D, 0.0D).tex(5.625D, 0.0D).color(64, 64, 64, 64).endVertex();
			bufferbuilder.pos((double) (this.width / 2 - 90), 100.0D, 0.0D).tex(0.0D, 0.0D).color(64, 64, 64, 64).endVertex();
			tessellator.draw();
			this.drawCenteredString(this.fontRenderer, I18n.format("createWorld.customize.custom.confirmTitle"), this.width / 2, 105, 16777215);
			this.drawCenteredString(this.fontRenderer, I18n.format("createWorld.customize.custom.confirm1"), this.width / 2, 125, 16777215);
			this.drawCenteredString(this.fontRenderer, I18n.format("createWorld.customize.custom.confirm2"), this.width / 2, 135, 16777215);
			this.confirm.drawButton(this.mc, mouseX, mouseY, partialTicks);
			this.cancel.drawButton(this.mc, mouseX, mouseY, partialTicks);
		}
	}

	@Override
	public String getText(int id, String name, float value)
	{
		return null;
	}
}