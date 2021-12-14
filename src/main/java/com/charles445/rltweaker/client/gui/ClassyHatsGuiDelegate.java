package com.charles445.rltweaker.client.gui;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class ClassyHatsGuiDelegate implements IGuiDelegate
{
	public final Class c_GuiHatBag;
	public final Field f_GuiHatBag_slotPos;
	public final Method m_GuiHatBag_onGuiClosed;
	
	public final Class c_ContainerHatBag;
	public final Field f_ContainerHatBag_slotPos;
	
	public final Class c_PacketHandler;
	public final Field f_PacketHandler_NETWORK;
	
	public final SimpleNetworkWrapper o_PacketHandler_NETWORK;
	
	public final Class c_PacketHatGuiOpen;
	public final Constructor con_PacketHatGuiOpen;
	
	private int savedSlot = -1;
	private int currentSlot = 0;
	
	public ClassyHatsGuiDelegate() throws Exception
	{
		c_GuiHatBag = Class.forName("wiresegal.classyhats.client.gui.GuiHatBag");
		f_GuiHatBag_slotPos = ReflectUtil.findField(c_GuiHatBag, "slotPos");
		m_GuiHatBag_onGuiClosed = ReflectUtil.findMethodAny(c_GuiHatBag, "func_146281_b", "onGuiClosed");
		
		c_ContainerHatBag = Class.forName("wiresegal.classyhats.container.ContainerHatBag");
		f_ContainerHatBag_slotPos = ReflectUtil.findField(c_ContainerHatBag, "slotPos");

		c_PacketHandler = Class.forName("com.teamwizardry.librarianlib.features.network.PacketHandler");
		f_PacketHandler_NETWORK = ReflectUtil.findField(c_PacketHandler, "NETWORK");
		o_PacketHandler_NETWORK = (SimpleNetworkWrapper) f_PacketHandler_NETWORK.get(null);
		
		c_PacketHatGuiOpen = Class.forName("wiresegal.classyhats.network.PacketHatGuiOpen");
		con_PacketHatGuiOpen = c_PacketHatGuiOpen.getDeclaredConstructor(int.class);
	}

	@Override
	public boolean pollMousePre(GuiScreen guiScreen, int width, int height, int x, int y, int button, boolean state)
	{
		//This is going to get run a lot when the window is open
		
		try
		{
			currentSlot = getGuiSlotPos(guiScreen);
			
			if(savedSlot == -1)
				savedSlot = currentSlot;
			
			if(currentSlot != savedSlot)
			{
				savedSlot = currentSlot;
				
				Container container = ((GuiContainer)guiScreen).inventorySlots;
				int containerSlot = getContainerSlotpos(container);
				
				m_GuiHatBag_onGuiClosed.invoke(guiScreen);
				savedSlot = -1;
				o_PacketHandler_NETWORK.sendToServer(newPacketOpenBag());
			}
		}
		catch(Exception e)
		{
			ErrorUtil.logSilent("Classy Hats Gui Delegate Invocation");
			e.printStackTrace();
		}
		
		return false;
	}

	public int getGuiSlotPos(Object guiHatBag) throws IllegalArgumentException, IllegalAccessException
	{
		return f_GuiHatBag_slotPos.getInt(guiHatBag);
	}
	
	public int getContainerSlotpos(Object containerHatBag) throws IllegalArgumentException, IllegalAccessException
	{
		return f_ContainerHatBag_slotPos.getInt(containerHatBag);
	}
	
	public IMessage newPacketOpenBag() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (IMessage)con_PacketHatGuiOpen.newInstance(1);
	}
}
