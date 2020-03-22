package com.charles445.rltweaker.handler;

import java.util.Collection;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.util.ErrorUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RoguelikeHandler
{
	public RoguelikeHandler()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	//Run before Roguelike Dungeons'
	@SubscribeEvent(priority=EventPriority.HIGH)
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		if(ModConfig.server.roguelike.preventFatigueCrash)
		{
			//Follow roguelike's behavior first
			
			World world = event.getWorld();
			if(world.isRemote)
				return;
			
			Entity entity = event.getEntity();
			
			if(!(entity instanceof EntityMob) || entity instanceof EntitySlime)
				return;
			
			EntityLiving living = (EntityLiving) entity;
			
			Collection<PotionEffect> effects = living.getActivePotionEffects();
			
			boolean doRemove = false;
			int duration = 1;
			for(PotionEffect effect : effects)
			{
				Potion potion = effect.getPotion();
				if(Potion.getIdFromPotion(potion) == 4)
				{
					//Mining Fatigue
					if(effect.getAmplifier() > 4)
					{
						doRemove = true;
						duration = effect.getDuration();
						RLTweaker.logger.error("Roguelike crash avoided, mob had high mining fatigue: "+effect.getAmplifier());
						ErrorUtil.logSilent("Roguelike Fatigue Crash");
						break;
					}
				}
			}
			if(doRemove)
			{
				Potion potion = Potion.getPotionById(4);
				living.removePotionEffect(potion);
				living.addPotionEffect(new PotionEffect(potion, duration, 4));
			}
		}
	}
}
