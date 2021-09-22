package com.charles445.rltweaker.client.gui;

import net.minecraft.client.gui.GuiScreen;

public interface IGuiDelegate
{
	/** Return true only if eating the mouse input **/
	public boolean pollMousePre(GuiScreen guiScreen, int width, int height, int x, int y, int button, boolean state);
}
