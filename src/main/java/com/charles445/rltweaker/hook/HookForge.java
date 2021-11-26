package com.charles445.rltweaker.hook;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import com.charles445.rltweaker.util.ReflectUtil;
import com.charles445.rltweaker.util.TriConsumer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class HookForge
{
	private static final Map<String, MessageExecutor> serverExecutors = new ConcurrentHashMap<>();
	private static final Map<Class, Field[]> serverFieldCaches = new ConcurrentHashMap<>();

	//net.minecraftforge.fml.common.network.simpleimpl.SimpleChannelHandlerWrapper
	//channelRead0
	
	public static IMessage onMessage(IMessageHandler handler, IMessage message, MessageContext ctx)
	{
		//Check server executors first, and if there is a match, schedule a task in the queue before the real handler  to manipulate the packet
		//If the executor is an override, ask the executor for a reply instead of the default handler
		
		//Netty thread
		//System.out.println(handler.getClass().getSimpleName()+" : "+message.getClass().getName()+" : "+ctx.getClass().getName());
		if(ctx.side == Side.SERVER)
		{
			MessageExecutor executor = serverExecutors.get(message.getClass().getName());
			if(executor != null && ctx.getServerHandler().player != null )
			{
				ctx.getServerHandler().player.getServerWorld().addScheduledTask(new Runnable()
				{
					@Override
					public void run()
					{
						//Server thread
						executor.consumer.accept(handler, message, ctx);
					}
				});
				
				if(executor.override)
					return executor.reply();
			}
		}
		
		return handler.onMessage(message, ctx);
	}
	
	public static class MessageExecutor
	{
		boolean override;
		TriConsumer<IMessageHandler, IMessage, MessageContext> consumer;
		MessageExecutor(boolean override, TriConsumer<IMessageHandler, IMessage, MessageContext> consumer)
		{
			this.override = override;
			this.consumer = consumer;
		}
		
		IMessage reply()
		{
			return null;
		}
	}
	
	public static void addServer(String name, TriConsumer<IMessageHandler, IMessage, MessageContext> consumer)
	{
		addServer(name, consumer, false);
	}
	
	public static void addServer(String name, TriConsumer<IMessageHandler, IMessage, MessageContext> consumer, boolean override)
	{
		addServer(name, new MessageExecutor(override, consumer));
	}
	
	public static void addServer(String name, MessageExecutor executor)
	{
		serverExecutors.put(name, executor);
	}
	
	@Nullable
	private static Field[] getFieldsForMessage(IMessage message, String... names)
	{
		Field[] fields = serverFieldCaches.get(message.getClass());
		if(fields == null)
		{
			try
			{
				fields = ReflectUtil.findFields(message.getClass(), names);
				serverFieldCaches.put(message.getClass(), fields);
			}
			catch (Exception e){}
		}
		return fields;
	}
	
	@Nullable
	private static <T> T getParam(@Nullable Object message, Field f) throws Exception
	{
		return (T) f.get(message);
	}
	
	private static void setParam(@Nullable Object message, Field f, Object value) throws Exception
	{
		f.set(message, value);
	}
	
	//
	// Executors
	//
	
	static
	{
		//Add executors here
		//addServer("full.clazzname.Name", HookForge::receiveName)
		//addServer("com.yyon.grapplinghook.network.PlayerMovementMessage", HookForge::grapplePack, true);
	}
	
	//Executors have a simple format
	/*
	private static void receiveName(IMessage message, MessageContext ctx)
	{
		Field[] fields = getFieldsForMessage(message, "any", "needed", "fields");
		
		try
		{
			//Get values
			Object any = getParam(message, fields[0]);
			
			//Do whatever processing
			
			//Set values
			setParam(message, fields[0], any);
		}
		catch(Exception e){}
	}
	*/
}
