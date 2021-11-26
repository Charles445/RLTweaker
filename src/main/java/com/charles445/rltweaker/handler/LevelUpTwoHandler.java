package com.charles445.rltweaker.handler;

import java.lang.reflect.InvocationTargetException;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.LevelUpTwoReflect;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LevelUpTwoHandler
{
	LevelUpTwoReflect reflector;
	
	public LevelUpTwoHandler()
	{
		try
		{	
			reflector = new LevelUpTwoReflect();
			
			CompatUtil.wrapSpecificHandler("LUStealthDamage", LUStealthDamage::new, "levelup2.skills.combat.StealthDamage", "onDamage");
			CompatUtil.wrapSpecificHandler("LUSwordCrit", LUSwordCrit::new, "levelup2.skills.combat.SwordCritBonus", "onHurting");
			CompatUtil.wrapSpecificHandler("LUSwordDamage", LUSwordDamage::new, "levelup2.skills.combat.SwordDamageBonus", "onHurting");
			CompatUtil.wrapSpecificHandler("LUXPBonus", LUXPBonus::new, "levelup2.skills.combat.XPBonusCombat", "getCombatBonus");
			CompatUtil.wrapSpecificHandler("LUStealthTarget", LUStealthTarget::new, "levelup2.skills.combat.StealthBonus", "onTargetSet");
		}
		catch(Exception e)
		{
			RLTweaker.logger.error("Failed to setup LevelUpTwoHandler!", e);
			ErrorUtil.logSilent("LevelUpTwo Critical Setup Failure");
			
			//Crash on Critical
			if(e instanceof CriticalException)
				throw new RuntimeException(e);
		}
	}
	
	private boolean isSourceDisallowed(DamageSource source)
	{
		if(source == null)
			return false;
		
		Entity immediateSrc = source.getImmediateSource();
		if(immediateSrc == null)
			return false;
		
		Entity trueSrc = source.getTrueSource();
		if(trueSrc == null)
			return false;
		
		//Check for pets
		if(!ModConfig.server.leveluptwo.petsUseSkills)
		{
			//Immediate is not same as true
			//Immediate is living
			//True is player
			//Immediate is not player
			if(immediateSrc != trueSrc && immediateSrc instanceof EntityLivingBase && trueSrc instanceof EntityPlayer && !(immediateSrc instanceof EntityPlayer))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public class LUStealthDamage
	{
		private IEventListener handler;
		public LUStealthDamage(IEventListener handler)
		{
			this.handler = handler;
			MinecraftForge.EVENT_BUS.register(this);
		}
		
		@SubscribeEvent
		public void onLivingHurt(LivingHurtEvent event)
		{
			if(isSourceDisallowed(event.getSource()))
				return;
			
			handler.invoke(event);
		}
	}
	
	public class LUSwordCrit
	{
		private IEventListener handler;
		public LUSwordCrit(IEventListener handler)
		{
			this.handler = handler;
			MinecraftForge.EVENT_BUS.register(this);
		}
		
		@SubscribeEvent
		public void onLivingHurt(LivingHurtEvent event)
		{
			if(isSourceDisallowed(event.getSource()))
				return;
			
			handler.invoke(event);
		}
	}
	
	public class LUSwordDamage
	{
		private IEventListener handler;
		public LUSwordDamage(IEventListener handler)
		{
			this.handler = handler;
			MinecraftForge.EVENT_BUS.register(this);
		}
		
		@SubscribeEvent
		public void onLivingHurt(LivingHurtEvent event)
		{
			if(isSourceDisallowed(event.getSource()))
				return;
			
			handler.invoke(event);
		}
	}
	
	public class LUXPBonus
	{
		private IEventListener handler;
		public LUXPBonus(IEventListener handler)
		{
			this.handler = handler;
			MinecraftForge.EVENT_BUS.register(this);
		}
		
		@SubscribeEvent
		public void onLivingDeath(LivingDeathEvent event)
		{
			if(isSourceDisallowed(event.getSource()))
				return;
			
			handler.invoke(event);
		}
	}
	
	public class LUStealthTarget
	{
		private IEventListener handler;
		private Object skill;
		public LUStealthTarget(IEventListener handler)
		{
			this.handler = handler;
			this.skill = CompatUtil.getSubscriberInstance(handler);
			
			//Error message if the config is enabled yet failed to set up
			if(this.skill == null)
			{
				if(ModConfig.server.leveluptwo.stealthOverhaul)
					RLTweaker.logger.error("Could not find StealthBonus subscriber instance! LevelUp2 Stealth Overhaul is disabled");
			}
			else
			{
				if(!this.skill.getClass().getName().equals("levelup2.skills.combat.StealthBonus"))
				{
					RLTweaker.logger.error("Subscriber instance of LUStealthTarget was the wrong class!: "+this.skill.getClass().getName());
					this.skill = null;
				}
				else
				{
					RLTweaker.logger.info("Gathered subscriber instance of class: "+this.skill.getClass().getName());
				}
			}
			
			MinecraftForge.EVENT_BUS.register(this);
		}
		
		@SubscribeEvent
		public void onSetAttackTarget(LivingSetAttackTargetEvent event)
		{
			if(this.skill == null || !ModConfig.server.leveluptwo.stealthOverhaul)
			{
				handler.invoke(event);
				return;
			}
			
			//Stealth Overhaul
			try
			{
				if(!reflector.skillIsActive(skill))
					return;
				
				if(event.getTarget() instanceof EntityPlayer && event.getEntityLiving() instanceof EntityMob)
				{
					EntityPlayer player = (EntityPlayer) event.getTarget();
					EntityMob mob = (EntityMob) event.getEntityLiving();
					
					//Don't interfere if the mob is seeking revenge on the player
					if(mob.getRevengeTarget() == player)
						return;
					
					if(!player.isSneaking())
						return;
					
					if(mob.getRevengeTimer() == mob.ticksExisted)
						return;
					
					int skillLevel = reflector.getSkillLevel(player, "levelup:stealth");
					
					if(skillLevel <= 0)
						return;
					
					if(MathHelper.floor(player.getDistanceSq(mob)) > calculateStealthDistance(skillLevel))
					{
						mob.setAttackTarget(null);
						//RLTweaker.logger.debug("Mob lost sight of player at distance: "+MathHelper.floor(player.getDistanceSq(mob)));
					}
				}
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				ErrorUtil.logSilent("LevelUp2 Stealth Overhaul Invocation");
				handler.invoke(event);
			}
		}
		
		private float calculateStealthDistance(int skillLevel)
		{
			//256.0f - (12.8f * skillLevel) is default
			//New default is effectively
			//(16.0f - 0.8f * skillLevel) ^ 2
			float base = (float) ModConfig.server.leveluptwo.stealthOverhaulBaseDistance;
			float per = (float) ModConfig.server.leveluptwo.stealthOverhaulDistancePerLevel * (float) skillLevel;
			float clamped = Math.max(0, base - per);
			return clamped * clamped;
		}
		
	}
}
