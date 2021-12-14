package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.ClassyHatsReflect;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;

import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClassyHatsHandler
{
	ClassyHatsReflect reflector;
	
	public ClassyHatsHandler()
	{
		try
		{
			reflector = new ClassyHatsReflect();
			
			MinecraftForge.EVENT_BUS.register(this);
		}
		catch (Exception e)
		{
			RLTweaker.logger.error("Failed to setup ClassyHatsHandler!", e);
			ErrorUtil.logSilent("ClassyHats Critical Setup Failure");
			
			//Crash on Critical
			if(e instanceof CriticalException)
				throw new RuntimeException(e);
		}
	}

	@SubscribeEvent
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event)
	{
		if(event.getHand() == EnumHand.OFF_HAND && ModConfig.server.classyhats.hatDupeFix)
		{
			//Fix for hats duping when used in offhand
			if(!event.getItemStack().isEmpty() && reflector.c_ItemHat.isInstance(event.getItemStack().getItem()))
				event.setCanceled(true);
		}
	}
}
