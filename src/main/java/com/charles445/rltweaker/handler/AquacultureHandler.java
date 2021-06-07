package com.charles445.rltweaker.handler;

import java.lang.reflect.InvocationTargetException;

import javax.annotation.Nullable;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.AquacultureReflect;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;

import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
