package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class SpawnerControlHandler
{
	public SpawnerControlHandler()
	{
		try
		{
			if(ModConfig.server.spawnercontrol.synchronizeSpawnerIteration)
			{
				//Wrap the WorldTick handler
				CompatUtil.wrapSpecificHandler("SCWorldTick", SCWorldTick::new, "ladysnake.spawnercontrol.SpawnerEventHandler", "onTickWorldTick");
			}
		}
		catch (Exception e)
		{
			RLTweaker.logger.error("Failed to setup SpawnerControlHandler!", e);
			ErrorUtil.logSilent("Spawner Control Critical Setup Failure");
			
			//Crash on Critical
			if(e instanceof CriticalException)
				throw new RuntimeException(e);
		}
	}
	
	public class SCWorldTick
	{
		private IEventListener handler;
		public SCWorldTick(IEventListener handler)
		{
			this.handler = handler;
			MinecraftForge.EVENT_BUS.register(this);
		}
		
		@SubscribeEvent
		public void onWorldTick(final TickEvent.WorldTickEvent event)
		{
			//Avoid weird synchronization issues by doing the standard checks done in Spawner Control
			if(event.phase == TickEvent.Phase.START || event.side == Side.CLIENT)
				return;
			
			//Confident side is server and phase end, synchronize and run original handler
			synchronized(this)
			{
				handler.invoke(event);
			}
		}
	}
}
