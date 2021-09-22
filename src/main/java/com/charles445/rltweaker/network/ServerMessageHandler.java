package com.charles445.rltweaker.network;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.util.ErrorUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class ServerMessageHandler
{
	public static Map<Class, IServerMessageReceiver> registeredMessages = new ConcurrentHashMap<>();
	
	public static void registerMessage(Class message, IServerMessageReceiver receiver)
	{
		if(IMessage.class.isAssignableFrom(message))
		{
			registeredMessages.put(message, receiver);
		}
		else
		{
			RLTweaker.logger.error("Failed to ServerMessageHandler register message of class, not an IMessage: "+message.getName());
		}
	}
	
	private static void executeRegisteredMessage(IMessage message, EntityPlayer player)
	{
		IServerMessageReceiver receiver = registeredMessages.get(message.getClass());
		if(receiver == null)
			return;
		
		receiver.receiveMessage(message, player);
	}
	
	public static IMessage executeMessage(IMessage message, MessageContext ctx)
	{
		if(ctx.side == Side.SERVER)
		{
			if(ctx.netHandler instanceof NetHandlerPlayServer)
			{
				NetHandlerPlayServer netHandler = (NetHandlerPlayServer)ctx.netHandler;
				if(netHandler.player!=null)
				{
					netHandler.player.getServerWorld().addScheduledTask(() -> 
					{
						ServerMessageHandler.executeRegisteredMessage(message, netHandler.player);
					});
				}
				else
				{
					RLTweaker.logger.error("NetHandlerPlayServer had null player...");
					ErrorUtil.logSilent("NetHandlerPlayServer NULL PLAYER");
				}
			}
		}
		
		return null;
	}
}
