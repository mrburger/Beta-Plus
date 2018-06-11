package com.mrburgerus.betaplus.gui;

import net.minecraft.client.gui.*;

import java.util.Random;

public class GuiCustomizeBeta extends GuiScreen implements GuiSlider.FormatHelper, GuiPageButtonList.GuiResponder
{
	//Fields
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




	// Required for GUI Components
	@Override
	public void setEntryValue(int id, boolean value)
	{

	}

	@Override
	public void setEntryValue(int id, float value)
	{

	}

	@Override
	public void setEntryValue(int id, String value)
	{

	}

	@Override
	public String getText(int id, String name, float value)
	{
		return null;
	}
}
