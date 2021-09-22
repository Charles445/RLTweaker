package com.charles445.rltweaker.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.TextComponentString;

public abstract class GuiDelegateBase implements IGuiDelegate
{
	protected Minecraft mc = Minecraft.getMinecraft();
	
	@Override
	public boolean pollMousePre(GuiScreen guiScreen, int width, int height, int x, int y, int button, boolean state)
	{
		return false;
	}
	
	protected boolean isButtonLeft(int button)
	{
		return button == 0;
	}
	
	protected boolean isButtonRight(int button)
	{
		return button == 1;
	}
	
	protected boolean isButtonMiddle(int button)
	{
		return button == 2;
	}
	
	protected void message(String s)
	{
		if(mc.player == null)
			return;
		
		mc.player.sendMessage(new TextComponentString(s));
	}
	
	protected void playClickSound()
	{
		mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}
}
