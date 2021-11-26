package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.BaublesReflect;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;

import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BaublesHandler
{
	BaublesReflect reflector;
	
	public BaublesHandler()
	{
		try
		{
			reflector = new BaublesReflect();

			MinecraftForge.EVENT_BUS.register(this);
		}
		catch (Exception e)
		{
			RLTweaker.logger.error("Failed to setup BaublesHandler!", e);
			ErrorUtil.logSilent("Baubles Critical Setup Failure");
			
			//Crash on Critical
			if(e instanceof CriticalException)
				throw new RuntimeException(e);
		}
	}
	
	@SubscribeEvent
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event)
	{
		if(event.getHand() == EnumHand.OFF_HAND && ModConfig.server.baubles.minersRingDupeFix)
		{
			//Fix for Miner's Rings (or derivatives of ItemRing) causing dupes and other problems in the offhand
			//Thanks Venom for reporting this one
			if(!event.getItemStack().isEmpty() && reflector.c_ItemRing.isInstance(event.getItemStack().getItem()))
			{
				//Cancel event to make sure the bauble doesn't get used in the offhand
				event.setCanceled(true);
			}
		}
	}
}
