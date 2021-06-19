package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.GrapplemodReflect;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class GrapplemodHandler
{
	private GrapplemodReflect reflector;
	
	public GrapplemodHandler()
	{
		try
		{
			reflector = new GrapplemodReflect();
			
			//TODO configurable wrapping?
			CompatUtil.wrapSpecificHandler("GMLivingFallEvent", GMLivingFallEvent::new, "com.yyon.grapplinghook.CommonProxyClass", "onLivingFallEvent");
			CompatUtil.wrapSpecificHandler("GMLivingFallEvent", GMLivingFallEvent::new, "com.yyon.grapplinghook.ServerProxyClass", "onLivingFallEvent");
			CompatUtil.wrapSpecificHandler("GMLivingFallEvent", GMLivingFallEvent::new, "com.yyon.grapplinghook.ClientProxyClass", "onLivingFallEvent");
			
			MinecraftForge.EVENT_BUS.register(this);
		}
		catch(Exception e)
		{
			RLTweaker.logger.error("Failed to setup GrapplemodHandler!", e);
			ErrorUtil.logSilent("GrapplemodHandler Critical Setup Failure");
			
			//Crash on Critical
			if(e instanceof CriticalException)
				throw new RuntimeException(e);
		}
	}
	/*
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event)
	{
		if(ModConfig.server.grapplemod.grapplingHookFallDamage && event.player != null && !event.player.world.isRemote)
		{
			UUID gpID = event.player.getPersistentID();
			if(reflector.getIsAttached(event.player.getEntityId()))
			{
				if(gpID != null)
				{
					Boolean downwards = movingDownwards.get(gpID);
					if(downwards == null)
					{
						downwards = getEntityDownwards(event.player);
						setDownwards(gpID, downwards);
					}
					
					boolean currentDownwards = getEntityDownwards(event.player);
					if(downwards != currentDownwards)
					{
						setDownwards(gpID, currentDownwards);
						event.player.fallDistance = 0.0f;
					}
				}
			}
			else if(movingDownwards.containsKey(gpID))
			{
				movingDownwards.remove(gpID);
			}
		}
	}
	*/
	
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event)
	{
		if(event.phase == TickEvent.Phase.START && ModConfig.server.grapplemod.grapplingHookFallDamage && event.player != null && !event.player.world.isRemote)
		{
			if(reflector.getIsAttached(event.player.getEntityId()))
			{
				//onGround is set a tick before fallDistance is set to zero, so it must be checked as well
				if(event.player.fallDistance > 3.0F && !event.player.onGround && event.player.motionY >= -0.6d)
				{
					//DebugUtil.messageAll("Reset FallDistance, motionY: "+event.player.motionY);
					event.player.fallDistance = 0.0f;
				}
			}
		}
	}
	
	/*
	 * @SubscribeEvent
	public void onPlayerUpdateEvent(LivingUpdateEvent event)
	{
		if(event.getEntityLiving() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)event.getEntityLiving();
			if(ModConfig.server.grapplemod.grapplingHookFallDamage && player != null && !player.world.isRemote)
			{
				if(reflector.getIsAttached(player.getEntityId()))
				{
					if(player.fallDistance > 3.0F && player.motionY >= -0.6d)
					{
						DebugUtil.messageAll("Reset FallDistance, motionY: "+player.motionY);
						player.fallDistance = 0.0f;
					}
				}
			}
		}
	}
	 */
	
	public class GMLivingFallEvent
	{
		private IEventListener handler;
		public GMLivingFallEvent(IEventListener handler)
		{
			this.handler = handler;
			MinecraftForge.EVENT_BUS.register(this);
		}
		
		@SubscribeEvent
		public void onLivingFallEvent(final LivingFallEvent event)
		{
			handler.invoke(event); //Clean!
			if(event.isCanceled() && ModConfig.server.grapplemod.grapplingHookFallDamage)
			{
				event.setCanceled(false);
			}
		}
	}
}
