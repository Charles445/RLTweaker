package com.charles445.rltweaker.handler;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Set;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.BattleTowersReflect;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class BattleTowersHandler
{
	public long tickedTime;
	
	private BattleTowersReflect reflector;
	
	public BattleTowersHandler()
	{
		try
		{
			reflector = new BattleTowersReflect();
			MinecraftForge.EVENT_BUS.register(this);
		}
		catch(Exception e)
		{
			RLTweaker.logger.error("Failed to setup BattleTowersHandler!", e);
			ErrorUtil.logSilent("BattleTowers Critical Setup Failure");
			
			//Crash on Critical
			if(e instanceof CriticalException)
				throw new RuntimeException(e);
		}
	}
	
	//Registering on high so it always runs before BattleTowers' ServerTickHandler
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onTick(TickEvent.WorldTickEvent tick)
	{
		//Tower Explosion Credit
		
		if(!ModConfig.server.battletowers.towerExplosionNoCredit)
			return;
		
		if(System.currentTimeMillis() > tickedTime + 14000L) // its a fourteen second timer ZZZ
		{
			tickedTime = System.currentTimeMillis();
			//It takes 15000L for the tower destroyer to run its first explosion, so this will intervene before then
			//If the game gets paused while these timers are counting down, due to priority this will run before the tower starts exploding
			//Really shouldn't be pausing the game during these anyway...
			
			try
			{
				Set<Object> towerDestroyers = reflector.getTowerDestroyers();
				
				if(towerDestroyers!=null && towerDestroyers.size() > 0)
				{
					Iterator<Object> iterator = towerDestroyers.iterator();
					while(iterator.hasNext())
					{
						Object destroyer = iterator.next();
						if(destroyer!=null)
						{
							//TODO is null safe? There have been some recoil issues with null targets, does this apply here?
							reflector.setDestroyerPlayer(destroyer, null);
						}
					}
				}
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				//Just quietly put it in the rlerrorreport and call it a day
				ErrorUtil.logSilent("BT getTowerDestroyers Invocation");
				return;
			}
			
		}
	}
}
