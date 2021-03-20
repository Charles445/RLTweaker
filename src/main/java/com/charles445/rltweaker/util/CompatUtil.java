package com.charles445.rltweaker.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.ASMEventHandler;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.ListenerList;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CompatUtil
{
	private static CompatUtil instance;
	
	//EventBus
	private final Class c_EventBus;
	private final Method m_register;
	private final Field f_listeners;
	private final Field f_busID;
	
	public CompatUtil() throws Exception
	{
		c_EventBus = Class.forName("net.minecraftforge.fml.common.eventhandler.EventBus");
		m_register = c_EventBus.getDeclaredMethod("register", Class.class, Object.class, Method.class, ModContainer.class);
		m_register.setAccessible(true);
		f_listeners = c_EventBus.getDeclaredField("listeners");
		f_listeners.setAccessible(true);
		f_busID = c_EventBus.getDeclaredField("busID");
		f_busID.setAccessible(true);
	}
	
	/** Removes a specific handler from the event bus and creates a new handler.<br>
	 *  The new handler is NOT registered automatically
	 * 
	 * @param name (String) name of the new handler to show in the log
	 * @param constructor (Consumer) Constructor of the new handler class, needs IEventListener parameter
	 * @param clazzName (String) name of the class to search for
	 * @param methodName (String) name of the method to search for
	 * @throws Exception
	 */
	public static void wrapSpecificHandler(String name, Consumer<IEventListener> constructor, String clazzName, String methodName) throws Exception
	{
		Object handler = CompatUtil.findAndRemoveHandlerFromEventBus(clazzName, methodName);
		if(handler instanceof IEventListener)
		{
			RLTweaker.logger.info("Registering "+name+" to the event bus");
			constructor.accept((IEventListener)handler);
		}
	}
	
	/** Manual EVENT_BUS registering 
	 * Don't forget to add the SubscribeEvent annotation
	 * But do not register the whole class to the event bus!
	 * 
	 * @param clazz (Event) Event Class
	 * @param thiz  (this) Event Handler Object
	 * @param thiz_toCall (this.onEvent(Object event)) Method to be invoked
	 * @return
	 */
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
	public static Object getModInstance(String modid)
	{
		for(ModContainer modContainer : Loader.instance().getModList())
		{
			if(modContainer.getModId().equals(modid))
			{
				return modContainer.getMod();
			}
		}
		
		RLTweaker.logger.warn("Asked to find mod instance "+modid+", but could not find it!");
		
		return null;
	}
	
	@Nullable
	public static Object findAndRemoveHandlerFromEventBus(Class clazz) throws Exception
	{
		return findAndRemoveHandlerFromEventBus(clazz.getName());
	}
	
	@Nullable
	public static Object findAndRemoveHandlerFromEventBus(String name) throws Exception
	{
		return findAndRemoveHandlerFromEventBus(name, null);
	}
	
	/** 
	 * @param name : Class name to remove from the event bus.
	 * @param specific : If specified, removes only a single method.
	 * @return : If specific, returns the specific IEventListener, otherwise returns the entire handler.
	 * @throws Exception
	 */
	@Nullable
	public static Object findAndRemoveHandlerFromEventBus(String name, @Nullable String specific) throws Exception
	{
		boolean criticalCrash = false;
		
		try
		{
			if(instance==null)
				instance = new CompatUtil();
			
			boolean found_listener = false;
			ConcurrentHashMap<Object, ArrayList<IEventListener>> listeners = (ConcurrentHashMap<Object, ArrayList<IEventListener>>) instance.f_listeners.get(MinecraftForge.EVENT_BUS);
			RLTweaker.logger.debug("Listener Size: "+listeners.size());
			
			Object handler = null;
			
			for(Map.Entry<Object, ArrayList<IEventListener>> listener_entry : listeners.entrySet())
			{
				handler = listener_entry.getKey();
				
				String handlerName = handler.getClass().getName();
				
				//Name fixup for static handlers
				if(handlerName.equals("java.lang.Class"))
				{
					handlerName = ((Class)handler).getName();
				}
				
				if (handlerName.equals(name))
				{
					if(ModConfig.server.minecraft.debug)
					{
						ArrayList<IEventListener> eventListeners = listener_entry.getValue();
						if(eventListeners==null)
						{
							RLTweaker.logger.debug("eventListeners: null");
						}
						else
						{
							for(IEventListener eventListener : eventListeners)
							{
								RLTweaker.logger.debug(eventListener.toString());
							}
						}
					}
					
					found_listener=true;
					
					if(specific==null)
					{
						MinecraftForge.EVENT_BUS.unregister(handler);
						RLTweaker.logger.info("Found and removed "+name+" from the event bus");
						RLTweaker.logger.debug("Listener Size Post Unregister: "+listeners.size());
						return handler;
					}
					else
					{
						//Specific
						ArrayList<IEventListener> eventListeners = listener_entry.getValue();
						RLTweaker.logger.debug("EventListener Size Pre Unregister: "+eventListeners.size());
						
						Iterator<IEventListener> elIterator = eventListeners.iterator();
						
						while(elIterator.hasNext())
						{
							IEventListener eventListener = elIterator.next();
							if(eventListener.toString().contains(specific))
							{
								//From this point on, any crash in here is a critical failure
								criticalCrash = true;
								
								//Remove it from the collection
								elIterator.remove();
								
								RLTweaker.logger.debug("EventListener Size Post Unregister: "+eventListeners.size());
								
								//Tell the internal bus to get rid of it as well
								int busID = instance.f_busID.getInt(MinecraftForge.EVENT_BUS);
								ListenerList.unregisterAll(busID, eventListener);
								
								
								RLTweaker.logger.info("Found and removed "+name+" IEventListener "+specific+" from the event bus");
								return eventListener;
							}
						}
						
						break;
					}
				}
			}
			
			return null;
		}
		catch(Exception e)
		{
			if(criticalCrash)
			{
				throw new CriticalException(e);
			}
			else
			{
				throw e;
			}
		}
	}
}
