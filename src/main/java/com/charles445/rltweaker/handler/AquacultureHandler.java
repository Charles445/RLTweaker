package com.charles445.rltweaker.handler;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.AquacultureReflect;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;

import net.minecraft.world.biome.Biome;

public class AquacultureHandler
{
	private AquacultureReflect reflector;
	
	public AquacultureHandler()
	{
		try
		{
			reflector = new AquacultureReflect();
			
			if(ModConfig.server.aquaculture.fixNeptunesBounty)
			{
				fixNeptunesBounty();
			}
			
			if(ModConfig.server.aquaculture.fixFreshwaterBug)
			{
				fixFreshwaterBug();
			}
			
			//No need for bus yet
			//MinecraftForge.EVENT_BUS.register(this);
		}
		catch(Exception e)
		{
			RLTweaker.logger.error("Failed to setup AquacultureHandler!", e);
			ErrorUtil.logSilent("Aquaculture Critical Setup Failure");
			
			//Crash on Critical
			if(e instanceof CriticalException)
				throw new RuntimeException(e);
		}
	}
	
	public void fixFreshwaterBug()
	{
		try
		{
			Map<Integer, ArrayList<Object>> biomeMap = reflector.getBiomeMap();
			
			if(biomeMap == null)
			{
				RLTweaker.logger.error("Aquaculture had null biomeMap, failed to fix freshwater bug");
				ErrorUtil.logSilent("Aquaculture Fix Freshwater Bug Null BiomeMap");
				return;
			}
			
			Object freshwaterBiomeType = reflector.getFreshwaterBiomeType();
			if(freshwaterBiomeType == null)
			{
				RLTweaker.logger.error("Aquaculture had null freshwater BiomeType, failed to fix freshwater bug");
				ErrorUtil.logSilent("Aquaculture Fix Freshwater Bug Null Freshwater BiomeType");
				return;
			}
			
			for (Biome biome : Biome.REGISTRY)
			{
				int biomeID = Biome.getIdForBiome(biome);
				if(!biomeMap.containsKey(biomeID))
				{
					biomeMap.put(biomeID, new ArrayList<>());
					biomeMap.get(biomeID).add(freshwaterBiomeType);
				}
			}
		}
		catch (IllegalArgumentException | IllegalAccessException e)
		{
			e.printStackTrace();
			ErrorUtil.logSilent("Aquaculture Fix Freshwater Bug Failure");
		}
	}
	
	public void fixNeptunesBounty()
	{
		try
		{
			if(!reflector.getNeptuniumLootEnabled())
				return;
			
			reflector.addLoot(reflector.getNeptunesBountyLoot(), reflector.getNeptuniumBarStack(), 2, 1, 4);
		}
		catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e)
		{
			e.printStackTrace();
			ErrorUtil.logSilent("Aquaculture Neptunes Bounty Loot Failure");
		}
	}
}
