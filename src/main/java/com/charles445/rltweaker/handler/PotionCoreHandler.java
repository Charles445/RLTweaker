package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PotionCoreHandler
{
	boolean controllableJumpBoost = false;
	
	public PotionCoreHandler()
	{
		try
		{
			if(ModConfig.server.potioncore.capJumpBoost)
			{
				CompatUtil.wrapSpecificHandler("PCJump", PCJump::new, "com.tmtravlr.potioncore.PotionCoreEventHandler", "onLivingJump");
			}
		}
		catch (Exception e)
		{
			RLTweaker.logger.error("Failed to setup PotionCoreHandler!", e);
			ErrorUtil.logSilent("PotionCore Critical Setup Failure");
			
			//Crash on Critical
			if(e instanceof CriticalException)
				throw new RuntimeException(e);
		}
	}
	
	public class PCJump
	{
		private IEventListener handler;
		
		public PCJump(IEventListener handler)
		{
			this.handler = handler;
			MinecraftForge.EVENT_BUS.register(this);
		}
		
		@SubscribeEvent
		public void onJump(LivingEvent.LivingJumpEvent event)
		{
			EntityLivingBase living = event.getEntityLiving();
			
			PotionEffect jumpEffect = living.getActivePotionEffect(MobEffects.JUMP_BOOST);
			
			//Exit on invalid jump boost
			if (jumpEffect != null && (jumpEffect.getAmplifier() < 0 || jumpEffect.getAmplifier() > 127))
			{
				if(living.world.isRemote)
					living.getEntityData().setBoolean("Potion Core - Jump Boost Jumping", false);
				return;
			}
			
			handler.invoke(event);
		}
	}
}
