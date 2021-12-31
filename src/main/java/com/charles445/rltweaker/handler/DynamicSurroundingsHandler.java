package com.charles445.rltweaker.handler;

import javax.annotation.Nullable;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ServerRunnable;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class DynamicSurroundingsHandler
{
	@Nullable
	EnvironmentServiceHelper environmentServiceHelper;
	
	public DynamicSurroundingsHandler()
	{
		if(ModConfig.server.dynamicsurroundings.environmentServiceDelay)
		{
			environmentServiceHelper = new EnvironmentServiceHelper();
			
			RLTweaker.serverRunnables.put("EnvironmentServiceHelper", environmentServiceHelper);
		}
	}
	
	public class EnvironmentServiceHelper implements ServerRunnable
	{
		IEventListener handler_tickEvent;
		
		@Override
		public void onServerStarting()
		{
			try
			{
				handler_tickEvent = (IEventListener) CompatUtil.findAndRemoveHandlerFromEventBus("org.orecruncher.dsurround.server.services.EnvironmentService", "tickEvent");
				MinecraftForge.EVENT_BUS.register(this);
			}
			catch (Exception e)
			{
				ErrorUtil.logSilent("DynamicSurroundings EnvironmentServiceHelper Wrapper");
				
				if(e instanceof CriticalException)
				{
					throw new RuntimeException("Critical error setting up EnvironmentServiceHelper!", e);
				}
			}
		}
		
		@Override
		public void onServerStopping()
		{
			MinecraftForge.EVENT_BUS.unregister(this);
			handler_tickEvent = null;
		}
		
		@SubscribeEvent
		public void onPlayerTick(TickEvent.PlayerTickEvent event)
		{
			if(handler_tickEvent != null)
			{
				if(event.player.ticksExisted % 20 == 0)
				{
					handler_tickEvent.invoke(event);
				}
			}
		}
	}
}
