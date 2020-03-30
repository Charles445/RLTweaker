package com.charles445.rltweaker.handler;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.debug.DebugUtil;
import com.charles445.rltweaker.util.ErrorUtil;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MotionCheckHandler
{
	public Set<UUID> rotationErrors = new HashSet<UUID>();
	public Set<UUID> motionErrors = new HashSet<UUID>();
	
	public MotionCheckHandler()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onLivingUpdateHighest(LivingUpdateEvent event)
	{
		handleLivingUpdate(event, EventPriority.HIGHEST);
	}
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onLivingUpdateHigh(LivingUpdateEvent event)
	{
		handleLivingUpdate(event, EventPriority.HIGH);
	}
	
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void onLivingUpdateNormal(LivingUpdateEvent event)
	{
		handleLivingUpdate(event, EventPriority.NORMAL);
	}
	
	@SubscribeEvent(priority = EventPriority.LOW)
	public void onLivingUpdateLow(LivingUpdateEvent event)
	{
		handleLivingUpdate(event, EventPriority.LOW);
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onLivingUpdateLowest(LivingUpdateEvent event)
	{
		handleLivingUpdate(event, EventPriority.LOWEST);
	}
	
	private void handleLivingUpdate(LivingUpdateEvent event, EventPriority priority)
	{
		if(!ModConfig.server.minecraft.motionChecker)
			return;
		
		EntityLivingBase entity = event.getEntityLiving();
		World world = entity.getEntityWorld();
		if(world.isRemote)
			return;
		
		//Server side
		if(testMotion(entity.motionX, entity, priority))
		{
			entity.motionX = 0.0d;
			event.setCanceled(true);
			entity.velocityChanged=true;
		}
		
		if(testMotion(entity.motionY, entity, priority))
		{
			entity.motionY = 0.0d;
			event.setCanceled(true);
			entity.velocityChanged=true;
		}
		
		if(testMotion(entity.motionZ, entity, priority))
		{
			entity.motionZ = 0.0d;
			event.setCanceled(true);
			entity.velocityChanged=true;
		}
		
		if(testAngle(entity.rotationPitch, entity, priority))
		{
			fixAngle(entity);
		}
		else if(testAngle(entity.rotationYaw, entity, priority))
		{
			fixAngle(entity);
		}
	}
	
	private void fixAngle(EntityLivingBase entity)
	{
		entity.rotationPitch = 0.0f;
		entity.rotationYaw = 0.0f;
		
		//TODO
		/*
		if(entity.getEntityWorld() instanceof WorldServer)
		{
			((WorldServer)entity.getEntityWorld()).addScheduledTask(() -> 
			{
				entity.setPositionAndRotation(entity.posX, entity.posY, entity.posZ, 0.0f, 0.0f);
			});
		}
		*/
	}
	
	private boolean testAngle(float angle, EntityLivingBase entity, EventPriority priority)
	{
		if(!Float.isFinite(angle))
		{
			//Log only if not in the set
			if(!rotationErrors.contains(entity.getUniqueID()))
			{
				rotationErrors.add(entity.getUniqueID());
				
				RLTweaker.logger.error("Entity has bad rotation! "+priority.name()+" "+angle+" "+dumpEntity(entity));
				ErrorUtil.logSilent("Motion Checker Angle "+priority.name());
				
				if(ModConfig.server.minecraft.debug)
					DebugUtil.messageAll("Entity has bad angles! "+priority.name()+" "+angle);
			}
			
			return true;
		}
		
		return false;
	}
	
	private boolean testMotion(double motion, EntityLivingBase entity, EventPriority priority)
	{
		if(motion > ModConfig.server.minecraft.motionCheckerSpeedCap || motion < (-ModConfig.server.minecraft.motionCheckerSpeedCap) || !Double.isFinite(motion))
		{
			//Log only if not in the set
			if(!motionErrors.contains(entity.getUniqueID()))
			{
				motionErrors.add(entity.getUniqueID());
				
				RLTweaker.logger.error("Entity moving too fast! "+priority.name()+" "+motion+" "+dumpEntity(entity));
				ErrorUtil.logSilent("Motion Checker Speed "+priority.name());
				
				if(ModConfig.server.minecraft.debug)
					DebugUtil.messageAll("Entity moving too fast! "+priority.name()+" "+motion);
			}
			
			return true;
		}
		
		return false;
	}
	
	private String dumpEntity(EntityLivingBase entity)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(entity.getClass().getName());
		sb.append(" - ");
		sb.append(entity.posX);
		sb.append(" ");
		sb.append(entity.posY);
		sb.append(" ");
		sb.append(entity.posZ);
		sb.append(" ");
		sb.append(entity.motionX);
		sb.append(" ");
		sb.append(entity.motionY);
		sb.append(" ");
		sb.append(entity.motionZ);
		sb.append(" hurt time:");
		sb.append(entity.hurtTime);
		sb.append(" dead:");
		sb.append(entity.isDead);
		sb.append(" pitch:");
		sb.append(entity.rotationPitch);
		sb.append(" yaw:");
		sb.append(entity.rotationYaw);
		
		if (entity instanceof EntityLiving)
		{
			EntityLiving living = (EntityLiving) entity;
			DamageSource damagesource = living.getLastDamageSource();
			if(damagesource!=null)
			{
				sb.append(" damagesource class:");
				sb.append(damagesource.getClass().getName());
			}
			EntityLivingBase revenge = living.getRevengeTarget();
			if(revenge!=null)
			{
				sb.append(" revenge target class:");
				sb.append(revenge.getClass().getName());
			}
			EntityLivingBase attack = living.getAttackTarget();
			if(attack!=null)
			{
				sb.append(" attack target class:");
				sb.append(attack.getClass().getName());
			}
		}
		
		return sb.toString();
	}
}
