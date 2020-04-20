package com.charles445.rltweaker.handler;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.JsonConfig;
import com.charles445.rltweaker.config.json.JsonDoubleBlockState;
import com.charles445.rltweaker.reflect.ReskillableReflect;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ReskillableHandler
{
	private ReskillableReflect reflector;
	
	public ReskillableHandler()
	{
		try
		{
			reflector = new ReskillableReflect();
			
			MinecraftForge.EVENT_BUS.register(this);
		}
	
		catch (Exception e)
		{
			RLTweaker.logger.error("Failed to setup ReskillableHandler!", e);
			ErrorUtil.logSilent("Reskillable Critical Setup Failure");
			
			//Crash on Critical
			if(e instanceof CriticalException)
				throw new RuntimeException(e);
		}
	}
	
	public void registerTransmutations()
	{
		if(JsonConfig.reskillableTransmutation!=null)
		{
			for(Map.Entry<String, List<JsonDoubleBlockState>> entry : JsonConfig.reskillableTransmutation.entrySet())
			{
				Item activator = ForgeRegistries.ITEMS.getValue(new ResourceLocation(entry.getKey()));
				if(activator==null)
				{
					RLTweaker.logger.warn("Skipping unregistered item in registerTransmutations: "+entry.getKey());
					continue;
				}
				
				for(JsonDoubleBlockState jdbs : entry.getValue())
				{
					try
					{
						reflector.addEntryToReagent(activator, jdbs.input.getAsBlockState(), jdbs.output.getAsBlockState());
					}
					catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
					{
						RLTweaker.logger.error("Invocation error in registerTransmutations", e);
						ErrorUtil.logSilent("Reskillable registerTransmutations Invoke Failure");
					}
				}
			}
		}
	}
}
