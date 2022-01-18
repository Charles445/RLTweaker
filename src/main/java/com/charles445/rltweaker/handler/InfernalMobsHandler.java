package com.charles445.rltweaker.handler;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.InfernalMobsReflect;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.Watchdog;

import net.minecraft.enchantment.Enchantment;

public class InfernalMobsHandler
{
	InfernalMobsReflect reflector;
	
	public InfernalMobsHandler()
	{
		try
		{
			reflector = new InfernalMobsReflect();
			
			if(ModConfig.server.infernalmobs.useEnchantmentBlacklist)
				tryRemoveBlacklistedEnchantments();
			
			if(ModConfig.server.infernalmobs.stallWatchdog)
				Watchdog.addRoutine("InfernalMobs Stall", new InfernalMobsStallRoutine());
		}
		catch(Exception e)
		{
			RLTweaker.logger.error("Failed to setup InfernalMobsHandler!", e);
			ErrorUtil.logSilent("Infernal Mobs Critical Setup Failure");
			
			//Crash on Critical
			if(e instanceof CriticalException)
				throw new RuntimeException(e);
		}
	}
	
	private void tryRemoveBlacklistedEnchantments()
	{
		try
		{
			ArrayList<Enchantment> enchantments = reflector.getEnchantmentList();
			
			Iterator<Enchantment> it = enchantments.iterator();
			
			String[] enchantmentBlacklist = ModConfig.server.minecraft.blacklistedEnchantments;
			
			while(it.hasNext())
			{
				//Enchantments in this list are nonnull
				String enchantName = it.next().getRegistryName().toString();
				
				for(int i=0;i<enchantmentBlacklist.length;i++)
				{
					if(enchantmentBlacklist[i].equals(enchantName))
					{
						it.remove();
						RLTweaker.logger.info("Removed Infernal Mobs Blacklisted Enchantment: "+enchantName);
						break;
					}
				}
			}
		}
		catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e)
		{
			ErrorUtil.logSilent("Infernal Mobs Remove Blacklisted Enchantments Invocation");
		}
	}
	
	public class InfernalMobsStallRoutine extends Watchdog.Routine
	{
		private AtomicInteger semaphoreCount = new AtomicInteger(0);

		//Runs on a separate thread
		@Override
		public void run() throws Exception
		{
			if(reflector.getSemaphor())
			{
				int sem = this.semaphoreCount.incrementAndGet();
				if(sem > 5)
				{
					Watchdog.logger.warn("InfernalMobs may be stalling, attempting recovery");
					reflector.setSemaphor(false);
					this.semaphoreCount.set(0);
				}
			}
			else
			{
				this.semaphoreCount.set(0);
			}
		}
	}
}
