package com.charles445.rltweaker.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Watchdog
{
	public static Logger logger = LogManager.getLogger("RLTweakerWatchdog");
	protected static ScheduledExecutorService executorService;
	protected static Map<String, Routine> routines = new ConcurrentHashMap<>();
	
	public static void init()
	{
		if(executorService == null)
		{
			executorService = Executors.newScheduledThreadPool(1);
			executorService.scheduleAtFixedRate(new RoutineManager(), 5, 1, TimeUnit.SECONDS);
		}
	}
	
	public static Routine addRoutine(String name, Routine routine)
	{
		Watchdog.logger.info("Adding Watchdog Routine: "+name);
		return routines.put(name, routine);
	}
	
	public static Routine removeRoutine(String name)
	{
		return routines.remove(name);
	}
	
	static class RoutineManager implements Runnable
	{
		private Set<String> failedSet = new HashSet<String>();
		
		@Override
		public void run()
		{
			for(Map.Entry<String, Routine> entry : routines.entrySet())
			{
				try
				{
					entry.getValue().run();
				}
				catch(Exception e)
				{
					failedSet.add(entry.getKey());
					Watchdog.logger.error("Failed to run watchdog routine: "+entry.getKey(), e);
				}
			}
			
			if(!failedSet.isEmpty())
			{
				for(String removal : failedSet)
				{
					Watchdog.removeRoutine(removal);
					Watchdog.logger.warn("Routine was removed: "+removal);
				}
				
				failedSet.clear();
			}
		}
	}
	
	public static abstract class Routine
	{
		public abstract void run() throws Exception;
	}
}
