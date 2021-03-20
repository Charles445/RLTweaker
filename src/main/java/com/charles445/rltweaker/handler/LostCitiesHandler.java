package com.charles445.rltweaker.handler;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.LostCitiesReflect;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;

import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LostCitiesHandler
{
	//postInit
		
	private LostCitiesReflect reflector;
	private LCTerrainHandler terrainHandler;
	
	public LostCitiesHandler()
	{
		try
		{
			reflector = new LostCitiesReflect();
			terrainHandler = new LCTerrainHandler();
			
			MinecraftForge.TERRAIN_GEN_BUS.register(terrainHandler);
			//MinecraftForge.EVENT_BUS.register(this);
		}
		catch(Exception e)
		{
			RLTweaker.logger.error("Failed to setup LostCitiesHandler!", e);
			ErrorUtil.logSilent("LostCities Critical Setup Failure");
			
			//Crash on Critical
			if(e instanceof CriticalException)
				throw new RuntimeException(e);
		}
	}
	
	public class LCTerrainHandler
	{
		@SubscribeEvent
		public void onDecorate(DecorateBiomeEvent.Decorate event)
		{
			World world = event.getWorld();
			
			if(world.isRemote)
				return;
			
			if(!ModConfig.server.lostcities.generationCacheCleanupEnabled)
				return;
			
			//Server side
			
			try
			{
				if(!reflector.isLostCities(world))
					return;
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				ErrorUtil.logSilent("LCTerrainHandler isLostCities Invocation");
				return;
			}
			
			//Server side, in a lost cities dimension (and reflector is working)
			
			Object chunkGenerator = null;
			
			try
			{
				chunkGenerator = reflector.getLostCityChunkGenerator(world);
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				ErrorUtil.logSilent("LCTerrainHandler getLostCityChunkGenerator Invocation");
				return;
			}
			
			if(chunkGenerator == null)
				return;
			
			//Chunk generator exists
			
			int generationCacheMaxCount = ModConfig.server.lostcities.generationCacheMaxCount;
			
			try
			{
				Map cachedPrimers = reflector.getCachedPrimers(chunkGenerator);
				
				if(cachedPrimers.size() >= generationCacheMaxCount)
				{
					RLTweaker.logger.debug("Clearing Lost Cities cachedPrimers: "+cachedPrimers.size());
					cachedPrimers.clear();
				}
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				ErrorUtil.logSilent("LCTerrainHandler getCachedPrimers Invocation");
			}
			
			try
			{
				Map cachedHeightmaps = reflector.getCachedHeightmaps(chunkGenerator);
				
				if(cachedHeightmaps.size() >= generationCacheMaxCount)
				{
					RLTweaker.logger.debug("Clearing Lost Cities cachedHeightmaps: "+cachedHeightmaps.size());
					cachedHeightmaps.clear();
				}
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				ErrorUtil.logSilent("LCTerrainHandler getCachedHeightmaps Invocation");
			}
			
		}
	}
}
