package com.charles445.rltweaker.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.GuiScreenEvent.MouseInputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiDelegator
{
	//TODO consider class lookup for performance if necessary
	public static Map<String, List<IGuiDelegate>> delegates = new ConcurrentHashMap<>();
	
	private Minecraft mc = Minecraft.getMinecraft();
	
	public GuiDelegator()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	//Static handling
	
	public static void addDelegate(String guiClazz, IGuiDelegate delegate)
	{
		List<IGuiDelegate> delegateList = delegates.get(guiClazz);
		if(delegateList == null)
		{
			delegateList = new ArrayList<>();
			delegates.put(guiClazz, delegateList);
		}
		
		delegateList.add(delegate);
	}
	
	//Event catching

	@SubscribeEvent
	public void onMouseInputPre(MouseInputEvent.Pre event)
	{
		try
		{
			pollMousePre(event);
		}
		catch(IOException e)
		{
			
		}
	}
	
	public void pollMousePre(MouseInputEvent.Pre event) throws IOException
	{
		GuiScreen guiScreen = event.getGui();
		List<IGuiDelegate> delegateList = delegates.get(guiScreen.getClass().getName());
		if(delegateList == null)
			return;
		
		int width = guiScreen.width;
		int height = guiScreen.height;
		int x = Mouse.getEventX() * width / mc.displayWidth;
		int y = height - Mouse.getEventY() * height / mc.displayHeight - 1;
		int button = Mouse.getEventButton();
		boolean state = Mouse.getEventButtonState();
		
		//message("M: "+guiScreen.getClass().getName()+" : "+height+" , "+width+" : "+x+" , "+y+" , "+button+", "+state);
		
		for(IGuiDelegate delegate : delegateList)
		{
			if(delegate.pollMousePre(guiScreen, width, height, x, y, button, state))
			{
				//Eaten mouse use, cancel events
				event.setCanceled(true);
				return;
			}
		}
	}
	
	private void message(String s)
	{
		if(mc.player != null)
			mc.player.sendMessage(new TextComponentString(s));
	}
}
