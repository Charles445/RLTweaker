package com.charles445.rltweaker.proxy;

import com.charles445.rltweaker.client.ClientCommandDebug;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.ClientCommandHandler;

public class ClientProxy extends CommonProxy
{
	@Override
	public void postInit()
	{
		super.postInit();
		
		ClientCommandHandler.instance.registerCommand(new ClientCommandDebug());
	}
	
	@Override
	public EntityPlayer getClientMinecraftPlayer()
	{
		return Minecraft.getMinecraft().player;
	}

	@Override
	public Boolean isClientConnectedToServer()
	{
		return Minecraft.getMinecraft().getConnection().getNetworkManager().isChannelOpen();
	}
}
