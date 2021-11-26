package com.charles445.rltweaker.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.IWorldGenerator;
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
	
	//GameRegistry
	private final Class c_GameRegistry;
	private final Method m_GameRegistry_registerWorldGenerator;
	private final Field f_GameRegistry_worldGenerators;
	private final Field f_GameRegistry_worldGeneratorIndex;
	
	public CompatUtil() throws Exception
	{
		c_EventBus = Class.forName("net.minecraftforge.fml.common.eventhandler.EventBus");
		m_register = c_EventBus.getDeclaredMethod("register", Class.class, Object.class, Method.class, ModContainer.class);
		m_register.setAccessible(true);
		f_listeners = c_EventBus.getDeclaredField("listeners");
		f_listeners.setAccessible(true);
		f_busID = c_EventBus.getDeclaredField("busID");
		f_busID.setAccessible(true);
		
		c_GameRegistry = Class.forName("net.minecraftforge.fml.common.registry.GameRegistry");
		m_GameRegistry_registerWorldGenerator = ReflectUtil.findMethod(c_GameRegistry, "registerWorldGenerator");
		f_GameRegistry_worldGenerators = ReflectUtil.findField(c_GameRegistry, "worldGenerators");
		f_GameRegistry_worldGeneratorIndex = ReflectUtil.findField(c_GameRegistry, "worldGeneratorIndex");
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
							if(eventListener.toString().contains(specific)) //TODO consider accuracy, or not, as this allows for desc usage as-is
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
						
						//break;
						//Normally this would break and be satisfied that it found the matching handler but no listener to wrap
						//However, due to certain mods registering multiple handlers of the same class, continuing to the next handler is appropriate instead
						RLTweaker.logger.debug("Instance Missing Specific EventListener, Continuing...");
					}
				}
			}
			//TODO inform?
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
	
	@Nullable
	public static <W extends WorldGeneratorWrapper> W tryWrapWorldGenerator(final W wrapperIn, Class<?> targetGeneratorClazz) throws Exception
	{
		boolean criticalCrash = false;
		
		try
		{
			if(instance==null)
				instance = new CompatUtil();
			
			//Reflect GameRegistry fields
			Set<IWorldGenerator> worldGenerators = (Set<IWorldGenerator>) instance.f_GameRegistry_worldGenerators.get(null);
			Map<IWorldGenerator, Integer> worldGeneratorIndex = (Map<IWorldGenerator, Integer>) instance.f_GameRegistry_worldGeneratorIndex.get(null);
			
			if(worldGenerators == null)
			{
				RLTweaker.logger.error("worldGenerators was null in tryWrapWorldGenerator");
				ErrorUtil.logSilent("tryWrapWorldGenerator worldGenerators null");
				return null;
			}
			
			if(worldGeneratorIndex == null)
			{
				RLTweaker.logger.error("worldGeneratorIndex was null in tryWrapWorldGenerator");
				ErrorUtil.logSilent("tryWrapWorldGenerator worldGeneratorIndex null");
				return null;
			}
			
			//Find the first matching class
			IWorldGenerator foundGenerator = null;
			for(IWorldGenerator generator : worldGenerators)
			{
				if(generator.getClass().equals(targetGeneratorClazz))
				{
					foundGenerator = generator;
					break;
				}
			}
			
			//Quit if generator wasn't found
			if(foundGenerator == null)
			{
				RLTweaker.logger.error("Could not find world generator of class: "+targetGeneratorClazz.getName());
				ErrorUtil.logSilent("tryWrapWorldGenerator worldGenerators null");
				return null;
			}
			
			//Get the world generator's weight
			Integer weight = worldGeneratorIndex.get(foundGenerator);
			if(weight == null)
			{
				RLTweaker.logger.error("Found world generator of class but weight was null: "+targetGeneratorClazz.getName());
				ErrorUtil.logSilent("tryWrapWorldGenerator weight null");
				return null;
			}
			
			//Desired generator was found, errors after this point are critical
			criticalCrash = true;
			
			//Remove generator from set and map
			if(!worldGenerators.remove(foundGenerator))
			{
				RLTweaker.logger.error("Failed to remove in worldGenerators with class: "+targetGeneratorClazz.getName());
				return null;
			}
			
			if(worldGeneratorIndex.remove(foundGenerator) == null)
			{
				RLTweaker.logger.error("Failed to remove in worldGeneratorIndex with class: "+targetGeneratorClazz.getName());
				return null;
			}
			
			//Register new generator
			wrapperIn.setWrappedGenerator(foundGenerator);
			instance.m_GameRegistry_registerWorldGenerator.invoke(null, wrapperIn, weight.intValue());
			RLTweaker.logger.info("Wrapped IWorldGenerator of class "+targetGeneratorClazz.getName());
			
			return wrapperIn;
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
	
	/**Gets the mod instance that corresponds to an IEventListener <br>
	 * DO NOT RUN THIS ON STATIC HANDLERS! <br>
	 * Will return null on failure <br>
	 * Possibly performance intensive as no reflection is being cached, so cache the result where possible<br>
	 **/
	@Nullable
	public static Object getSubscriberInstance(IEventListener eventListener)
	{
		try
		{
			if(eventListener instanceof ASMEventHandler)
			{
				IEventListener customHandler = (IEventListener) ReflectUtil.findField(ASMEventHandler.class, "handler").get(eventListener);
				return ReflectUtil.findField(customHandler.getClass(), "instance").get(customHandler);
			}
			else
			{
				//Other forms of event handler wrapper not supported
				return null;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}

	}
}
