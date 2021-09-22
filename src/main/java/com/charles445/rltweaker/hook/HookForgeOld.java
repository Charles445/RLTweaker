package com.charles445.rltweaker.hook;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import javax.annotation.Nullable;

import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class HookForgeOld
{
	private static final Map<String, BiConsumer<IMessage, MessageContext>> serverExecutors = new ConcurrentHashMap<>();
	private static final Map<Class, Field[]> serverFieldCaches = new ConcurrentHashMap<>();
	
	//net.minecraftforge.fml.common.network.simpleimpl.SimpleChannelHandlerWrapper
	//channelRead0
	
	//com/charles445/rltweaker/hook/HookForge
	//receiveMessage
	//(Lnet/minecraftforge/fml/common/network/simpleimpl/IMessage;Lnet/minecraftforge/fml/common/network/simpleimpl/MessageContext;)V
	public static void receiveMessage(IMessage message, MessageContext ctx) // channelRead0 throws Exception by itself
	{
		//Observe same threading and class loading rules as IMessageHandler
		//Whatever those are...
		if(ctx.side == Side.SERVER)
		{
			BiConsumer<IMessage, MessageContext> consumer = serverExecutors.get(message.getClass().getName());
			if(consumer != null)
				consumer.accept(message, ctx);
		}
		//else if(ctx.side == Side.CLIENT)
		//{
		//	
		//}
		
		//Cannot addScheduledTask to manipulate a packet because they use a Queue that polls
	}
	
	private static void addServer(String name, BiConsumer<IMessage, MessageContext> consumer)
	{
		serverExecutors.put(name, consumer);
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
			setParam(message, field[0], any);
		}
	}
	*/
	
	
}
