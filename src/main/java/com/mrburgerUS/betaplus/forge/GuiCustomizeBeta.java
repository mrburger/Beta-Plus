package com.mrburgerUS.betaplus.forge;

import com.google.common.primitives.Floats;
import com.mrburgerUS.betaplus.BetaPlusSettings;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
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
	protected String subtitle = "WORK IN PROGRESS!";
	private GuiButton done;
	private GuiButton randomize;
	private GuiButton defaults;
	private GuiButton confirm;
	private GuiButton cancel;
	private boolean settingsModified;
	private int confirmMode;
	private boolean confirmDismissed;
	private GuiPageButtonList list;
	private final Random random = new Random();
	private BetaPlusSettings.Factory settingsFactory = new BetaPlusSettings.Factory();

	public GuiCustomizeBeta(GuiScreen parentIn)
	{
		parent = (GuiCreateWorld) parentIn;
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
	 * window resizes, the buttonList is cleared beforehand.
	 */
	public void initGui()
	{
		title = I18n.format("options.customizeTitle");
		buttonList.clear();
		defaults = addButton(new GuiButton(GuiHelper.defaultId, width / 2 - 187, height - 27, 90, 20, I18n.format("createWorld.customize.custom.defaults")));
		randomize = addButton(new GuiButton(GuiHelper.randomizeId, width / 2 - 92, height - 27, 90, 20, I18n.format("createWorld.customize.custom.randomize")));
		done = addButton(new GuiButton(GuiHelper.doneId, width / 2 + 98, height - 27, 90, 20, I18n.format("gui.done")));
		defaults.enabled = settingsModified;
		confirm = new GuiButton(GuiHelper.confirmId, width / 2 - 55, 160, 50, 20, I18n.format("gui.yes"));
		confirm.visible = false;
		buttonList.add(confirm);
		cancel = new GuiButton(GuiHelper.cancelId, width / 2 + 5, 160, 50, 20, I18n.format("gui.no"));
		cancel.visible = false;
		buttonList.add(cancel);

		if (confirmMode != 0)
		{
			confirm.visible = true;
			cancel.visible = true;
		}

		createPage();
	}

	private void createPage()
	{
		GuiPageButtonList.GuiListEntry[] entries = new GuiPageButtonList.GuiListEntry[]
				{
						new GuiPageButtonList.GuiButtonEntry(GuiHelper.oldCaveId, I18n.format("createWorld.customize.custom.useOldCaves"), true, settingsFactory.useOldCaves),
						new GuiPageButtonList.GuiButtonEntry(GuiHelper.strongholdId, I18n.format("createWorld.customize.custom.useStrongholds"), true, settingsFactory.useStrongholds),
						//new GuiPageButtonList.GuiButtonEntry(151, I18n.format("createWorld.customize.custom.useVillages"), true, settings.useVillages),
						new GuiPageButtonList.GuiButtonEntry(GuiHelper.mineshaftId, I18n.format("createWorld.customize.custom.useMineShafts"), true, settingsFactory.useMineShafts),
						new GuiPageButtonList.GuiButtonEntry(GuiHelper.templeId, I18n.format("createWorld.customize.custom.useTemples"), true, settingsFactory.useTemples),
						//new GuiPageButtonList.GuiButtonEntry(210, I18n.format("createWorld.customize.custom.useMonuments"), true, settings.useMonuments),
						// new GuiPageButtonList.GuiButtonEntry(211, I18n.format("createWorld.customize.custom.useMansions"), true, settings.useMansions),
						new GuiPageButtonList.GuiButtonEntry(GuiHelper.ravineId, I18n.format("createWorld.customize.custom.useRavines"), true, settingsFactory.useRavines),
						new GuiPageButtonList.GuiButtonEntry(GuiHelper.dungeonId, I18n.format("createWorld.customize.custom.useDungeons"), true, settingsFactory.useDungeons),
						new GuiPageButtonList.GuiSlideEntry(157, I18n.format("createWorld.customize.custom.dungeonChance"), true, this, 1, 100, (float) settingsFactory.dungeonChance),
						new GuiPageButtonList.GuiButtonEntry(155, I18n.format("createWorld.customize.custom.useWaterLakes"), true, settingsFactory.useWaterLakes),
						new GuiPageButtonList.GuiSlideEntry(158, I18n.format("createWorld.customize.custom.waterLakeChance"), true, this, 1, 100, (float) settingsFactory.waterLakeChance),
						new GuiPageButtonList.GuiButtonEntry(156, I18n.format("createWorld.customize.custom.useLavaLakes"), true, settingsFactory.useLavaLakes),
						new GuiPageButtonList.GuiSlideEntry(159, I18n.format("createWorld.customize.custom.lavaLakeChance"), true, this, 10, 256, (float) settingsFactory.lavaLakeChance),
				};
		list = new GuiPageButtonList(mc, width, height, 32, height - 32, 25, this, new GuiPageButtonList.GuiListEntry[][]{entries});
	}

	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();
		this.list.handleMouseInput();
	}

	@Override
	public void setEntryValue(int id, boolean value)
	{
		switch (id)
		{
			case GuiHelper.dungeonId:
				settingsFactory.useDungeons = value;
				break;
			case GuiHelper.oldCaveId:
				settingsFactory.useOldCaves = value;
				break;
			case GuiHelper.strongholdId:
				settingsFactory.useStrongholds = value;
				break;
			case GuiHelper.mineshaftId:
				settingsFactory.useMineShafts = value;
				break;
			case GuiHelper.templeId:
				System.out.println("I work");
				settingsFactory.useTemples = value;
				break;
			default:
				break;
		}

		setSettingsModified(true);
	}

	@Override
	public void setEntryValue(int id, float value)
	{
		return;
	}

	private void setSettingsModified(boolean modified)
	{
		settingsModified = modified;
		defaults.enabled = modified;
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if (button.enabled)
		{
			switch (button.id)
			{
				case GuiHelper.doneId:
					this.parent.chunkProviderSettingsJson = this.settingsFactory.toString();
					mc.displayGuiScreen(parent);
					break;
				case GuiHelper.confirmId:
					exitConfirmation();
					break;
				case GuiHelper.defaultId:
					if (settingsModified)
						enterConfirmation(GuiHelper.defaultId);
					break;
				case GuiHelper.randomizeId:
					for (int i = 0; i < this.list.getSize(); ++i)
					{
						GuiPageButtonList.GuiEntry guipagebuttonlist$guientry = this.list.getListEntry(i);
						Gui gui = guipagebuttonlist$guientry.getComponent1();

						if (gui instanceof GuiButton)
						{
							GuiButton guibutton = (GuiButton) gui;

							if (guibutton instanceof GuiSlider)
							{
								float f = ((GuiSlider) guibutton).getSliderPosition() * (0.75F + this.random.nextFloat() * 0.5F) + (this.random.nextFloat() * 0.1F - 0.05F);
								((GuiSlider) guibutton).setSliderPosition(MathHelper.clamp(f, 0.0F, 1.0F));
							}
							else if (guibutton instanceof GuiListButton)
							{
								((GuiListButton) guibutton).setValue(this.random.nextBoolean());
							}
						}

						Gui gui1 = guipagebuttonlist$guientry.getComponent2();

						if (gui1 instanceof GuiButton)
						{
							GuiButton guibutton1 = (GuiButton) gui1;

							if (guibutton1 instanceof GuiSlider)
							{
								float f1 = ((GuiSlider) guibutton1).getSliderPosition() * (0.75F + this.random.nextFloat() * 0.5F) + (this.random.nextFloat() * 0.1F - 0.05F);
								((GuiSlider) guibutton1).setSliderPosition(MathHelper.clamp(f1, 0.0F, 1.0F));
							}
							else if (guibutton1 instanceof GuiListButton)
							{
								((GuiListButton) guibutton1).setValue(this.random.nextBoolean());
							}
						}
					}
					return;


				default:
					break;
			}
		}
	}

	private void enterConfirmation(int confirmModeIn)
	{
		confirmMode = confirmModeIn;
		setConfirmationControls(true);
	}

	private void exitConfirmation() throws IOException
	{
		switch (confirmMode)
		{
			case GuiHelper.doneId:
				actionPerformed(done);
			case GuiHelper.cancelId:
				break;
			case GuiHelper.defaultId:
				restoreDefaults();
			default:
				break;

		}
		confirmMode = 0;
		confirmDismissed = true;
		setConfirmationControls(false);
	}

	private void setConfirmationControls(boolean visible)
	{
		confirm.visible = visible;
		cancel.visible = visible;
		randomize.enabled = !visible;
		done.enabled = !visible;
		defaults.enabled = settingsModified && !visible;
	}


	protected void keyTyped(char typedChar, int keyCode) throws IOException
	{
		super.keyTyped(typedChar, keyCode);

		if (confirmMode == 0)
		{
			switch (keyCode)
			{
				case GuiHelper.upButton:
					modifyFocusValue(1.0F);
					break;
				case GuiHelper.downButton:
					modifyFocusValue(-1.0F);
					break;
				default:
					break;
			}
		}
	}

	private void modifyFocusValue(float inValue)
	{
		Gui gui = this.list.getFocusedControl();

		if (gui instanceof GuiTextField)
		{
			float f = inValue;

			if (GuiScreen.isShiftKeyDown())
			{
				f = inValue * 0.1F;

				if (GuiScreen.isCtrlKeyDown())
				{
					f *= 0.1F;
				}
			}
			else if (GuiScreen.isCtrlKeyDown())
			{
				f = inValue * 10.0F;

				if (GuiScreen.isAltKeyDown())
				{
					f *= 10.0F;
				}
			}

			GuiTextField guitextfield = (GuiTextField) gui;
			Float f1 = Floats.tryParse(guitextfield.getText());

			if (f1 != null)
			{
				f1 = f1 + f;
				int i = guitextfield.getId();
				String s = String.format("%s: %2.1f", guitextfield.getId(), f1);
				guitextfield.setText(s);
				this.setEntryValue(i, s);
			}
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);

		if (confirmMode == 0 && !confirmDismissed)
		{
			list.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state)
	{
		super.mouseReleased(mouseX, mouseY, state);

		if (this.confirmDismissed)
		{
			this.confirmDismissed = false;
		}
		else if (this.confirmMode == 0)
		{
			this.list.mouseReleased(mouseX, mouseY, state);
		}
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		drawDefaultBackground();
		list.drawScreen(mouseX, mouseY, partialTicks);
		drawCenteredString(fontRenderer, title, width / 2, 2, 16777215);
		drawCenteredString(fontRenderer, subtitle, width / 2, 12, 16777215);
		super.drawScreen(mouseX, mouseY, partialTicks);

		if (confirmMode != 0)
		{
			drawRect(0, 0, width, height, Integer.MIN_VALUE);
			drawHorizontalLine(width / 2 - 91, width / 2 + 90, 99, -2039584);
			drawHorizontalLine(width / 2 - 91, width / 2 + 90, 185, -6250336);
			drawVerticalLine(width / 2 - 91, 99, 185, -2039584);
			drawVerticalLine(width / 2 + 90, 99, 185, -6250336);
			float f = 85.0F;
			float f1 = 180.0F;
			GlStateManager.disableLighting();
			GlStateManager.disableFog();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			mc.getTextureManager().bindTexture(OPTIONS_BACKGROUND);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			float f2 = 32.0F;
			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
			bufferbuilder.pos((double) (width / 2 - 90), 185.0D, 0.0D).tex(0.0D, 2.65625D).color(64, 64, 64, 64).endVertex();
			bufferbuilder.pos((double) (width / 2 + 90), 185.0D, 0.0D).tex(5.625D, 2.65625D).color(64, 64, 64, 64).endVertex();
			bufferbuilder.pos((double) (width / 2 + 90), 100.0D, 0.0D).tex(5.625D, 0.0D).color(64, 64, 64, 64).endVertex();
			bufferbuilder.pos((double) (width / 2 - 90), 100.0D, 0.0D).tex(0.0D, 0.0D).color(64, 64, 64, 64).endVertex();
			tessellator.draw();
			drawCenteredString(fontRenderer, I18n.format("createWorld.customize.custom.confirmTitle"), width / 2, 105, 16777215);
			drawCenteredString(fontRenderer, I18n.format("createWorld.customize.custom.confirm1"), width / 2, 125, 16777215);
			drawCenteredString(fontRenderer, I18n.format("createWorld.customize.custom.confirm2"), width / 2, 135, 16777215);
			confirm.drawButton(mc, mouseX, mouseY, partialTicks);
			cancel.drawButton(mc, mouseX, mouseY, partialTicks);
		}
	}

	@Override
	public String getText(int id, String name, float value)
	{
		return name + ": " + value;
	}

	private void restoreDefaults()
	{
		settingsFactory.setDefaults();
		createPage();
		setSettingsModified(false);
	}

	@Override
	public void setEntryValue(int id, String value)
	{
		switch (id)
		{
			case GuiHelper.cancelId:
				break;
			case GuiHelper.confirmId:
				break;
			case GuiHelper.defaultId:

		}
	}
}