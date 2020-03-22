package com.charles445.rltweaker.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import com.charles445.rltweaker.RLTweaker;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CompatUtil
{
	private static CompatUtil instance;
	
	//EventBus
	private final Class c_EventBus;
	private final Method m_register;
	private final Field f_listeners;
	
	/** Manual EVENT_BUS registering 
	 * Don't forget to add the SubscribeEvent annotation
	 * But do not register the whole class to the event bus!
	 * 
	 * @param clazz (Event) Event Class
	 * @param thiz  (this) Event Handler Object
	 * @param thiz_toCall (this.onEvent(Object event)) Method to be invoked
	 * @return
	 */
	
	public CompatUtil() throws Exception
	{
		c_EventBus = Class.forName("net.minecraftforge.fml.common.eventhandler.EventBus");
		m_register = c_EventBus.getDeclaredMethod("register", Class.class, Object.class, Method.class, ModContainer.class);
		m_register.setAccessible(true);
		f_listeners = c_EventBus.getDeclaredField("listeners");
		f_listeners.setAccessible(true);
	}
	
	public static void subscribeEventManually(Class<?> clazz, Object thiz, Method thiz_toCall) throws Exception
	{
		if(instance==null)
			instance = new CompatUtil();
		
		if(!thiz_toCall.isAnnotationPresent(SubscribeEvent.class))
		{
			throw new RuntimeException("Method needs a SubscribeEvent annotation.");
		}
		
		instance.m_register.invoke(MinecraftForge.EVENT_BUS, clazz, thiz, thiz_toCall, Loader.instance().getMinecraftModContainer());
		RLTweaker.logger.info("Registered "+thiz.getClass().getName()+" "+thiz_toCall.getName()+" to the event bus");
		
	}
	
	@Nullable
	public static Object findAndRemoveHandlerFromEventBus(Class clazz) throws Exception
	{
		return findAndRemoveHandlerFromEventBus(clazz.getName());
	}
	
	@Nullable
	public static Object findAndRemoveHandlerFromEventBus(String name) throws Exception
	{
		if(instance==null)
			instance = new CompatUtil();
		
		boolean found_listener = false;
		ConcurrentHashMap<Object, ArrayList<IEventListener>> listeners = (ConcurrentHashMap<Object, ArrayList<IEventListener>>) instance.f_listeners.get(MinecraftForge.EVENT_BUS);
		RLTweaker.logger.debug("Listener Size: "+listeners.size());
		for(Map.Entry<Object, ArrayList<IEventListener>> listener_entry : listeners.entrySet())
        {
			Object handler = listener_entry.getKey();
			if (handler.getClass().getName().equals(name))
			{
				found_listener=true;
				MinecraftForge.EVENT_BUS.unregister(handler);
				RLTweaker.logger.info("Found and removed "+name+" from the event bus");
				RLTweaker.logger.debug("Listener Size Post Unregister: "+listeners.size());
				return handler;
			}
        }

		return null;
	}
}
