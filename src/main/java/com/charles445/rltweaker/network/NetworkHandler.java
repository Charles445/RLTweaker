package com.charles445.rltweaker.network;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.hook.HookForge;
import com.charles445.rltweaker.util.TriConsumer;
import com.charles445.rltweaker.util.VersionDelimiter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class NetworkHandler
{
	public static boolean serverHasVersioning = false;
	public static VersionDelimiter serverVersion = new VersionDelimiter("0.0.0");
	
	//Accessed by Netty a lot
	private static Map<UUID, VersionDelimiter> clients = new ConcurrentHashMap<>();
	
	public static void removeClient(UUID id)
	{
		if(clients.remove(id)!=null)
			RLTweaker.logger.trace("Removing NetworkHandler UUID");
	}
	
	public static void addClient(UUID id, VersionDelimiter vd)
	{
		if(clients.put(id, vd)==null)
			RLTweaker.logger.trace("Adding NetworkHandler UUID");
			
	}
	
	public static boolean isVersionAtLeast(int major, int minor, EntityPlayer player)
	{
		return isVersionAtLeast(major, minor, 0, player.getGameProfile().getId());
	}
	
	public static boolean isVersionAtLeast(int major, int minor, int patch, EntityPlayer player)
	{
		return isVersionAtLeast(major, minor, patch, player.getGameProfile().getId());
	}
	
	public static boolean isVersionAtLeast(int major, int minor, int patch, UUID uuid)
	{
		VersionDelimiter value = clients.get(uuid);
		
		if(value!=null)
		{
			return value.isSameOrNewerVersion(major, minor, patch);
		}
		else
		{
			return RLTweaker.MINIMUM_VERSION.isSameOrNewerVersion(major, minor, patch);
		}
	}
	
	public static void addServerPacketExecutor(String name, TriConsumer<IMessageHandler, IMessage, MessageContext> consumer)
	{
		HookForge.addServer(name, consumer);
	}
	
	public static void addServerPacketExecutor(String name, TriConsumer<IMessageHandler, IMessage, MessageContext> consumer, boolean override)
	{
		HookForge.addServer(name, consumer, override);
	}
	
	public static void addServerPacketExecutor(String name, HookForge.MessageExecutor executor)
	{
		HookForge.addServer(name, executor);
	}
}
