package com.charles445.rltweaker.handler;

import java.util.concurrent.ConcurrentHashMap;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.RuinsReflect;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RuinsHandler
{
	private RuinsReflect reflector;
	
	public RuinsHandler()
	{
		try
		{
			reflector = new RuinsReflect();
			
			//Register
			CompatUtil.subscribeEventManually(WorldEvent.Load.class, this, ReflectUtil.findMethod(this.getClass(), "onWorldLoad"));
		}
		catch(Exception e)
		{
			RLTweaker.logger.error("Failed to setup RuinsHandler!", e);
			ErrorUtil.logSilent("Ruins Critical Setup Failure");
			
			//Crash on Critical
			if(e instanceof CriticalException)
				throw new RuntimeException(e);
		}
	}
	
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event)
	{
		World world = event.getWorld();
		if(world.isRemote)
			return;
		
		//Server Side
		
		if(ModConfig.server.ruins.cleanupChunkLogger)
		{
			try
			{
				//Object is WorldHandle
				ConcurrentHashMap<Integer, Object> generatorMap = reflector.getGeneratorMap();
				
				int dimension = world.provider.getDimension();
				
				//Remove the world handle. Ruins will diligently create a new one as soon as it is needed.
				//This may not actually end up removing anything, but that's okay, as all that is necessary is for it to be missing
				generatorMap.remove(dimension);
				
				//Now that the world handle is gone, wipe the data of the chunk logger dat manually if it exists
				WorldSavedData chunkLoggerData = world.getPerWorldStorage().getOrLoadData(reflector.c_ChunkLoggerData, "ruinschunklogger");
				if(chunkLoggerData != null)
				{
					synchronized(chunkLoggerData)
					{
						//File exists, quickly wipe it to see if that works
						try
						{
							if(reflector.wipeChunkLoggerData(chunkLoggerData, ModConfig.server.ruins.chunkThreshold))
							{
								chunkLoggerData.setDirty(true);
								RLTweaker.logger.info("Wiped ruinschunklogger.dat for dimension "+world.provider.getDimension());
							}
						}
						catch (IllegalArgumentException | IllegalAccessException e)
						{
							RLTweaker.logger.error("onWorldLoad critical failure in RuinsHandler!", e);
							ErrorUtil.logSilent("RuinsHandler Critical Failure");
							throw new RuntimeException(e);
						}
					}
				}
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				RLTweaker.logger.error("onWorldLoad critical failure in RuinsHandler!", e);
				ErrorUtil.logSilent("RuinsHandler Critical Failure");
				throw new RuntimeException(e);
			}
		}
	}
}
