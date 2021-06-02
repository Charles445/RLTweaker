package com.charles445.rltweaker.handler;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Set;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.BattleTowersReflect;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class BattleTowersHandler
{
	public long tickedTime;
	
	private BattleTowersReflect reflector;
	
	public BattleTowersHandler()
	{
		try
		{
			reflector = new BattleTowersReflect();
			MinecraftForge.EVENT_BUS.register(this);
		}
		catch(Exception e)
		{
			RLTweaker.logger.error("Failed to setup BattleTowersHandler!", e);
			ErrorUtil.logSilent("BattleTowers Critical Setup Failure");
			
			//Crash on Critical
			if(e instanceof CriticalException)
				throw new RuntimeException(e);
		}
	}
	
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		if(reflector.isLycanitesAvailable() && ModConfig.server.battletowers.golemLycanitesProjectile)
		{
			//LycanitesMobs is available, and the config is enabled
			
			if(reflector.isEntityGolemFireball(event.getEntity()))
			{
				try
				{
					Entity fireball = event.getEntity();
					EntityLiving shooter = (EntityLiving) reflector.f_AS_EntityGolemFireball_shooterEntity.get(fireball);
					Entity demonFireball = reflector.createLycanitesProjectileWithNameAndShooter(
							ModConfig.server.battletowers.golemLycanitesProjectileName, 
							shooter, 
							(float)ModConfig.server.battletowers.golemLycanitesProjectileScaleModifier);
					
					if(demonFireball == null)
					{
						//Failed to create a new fireball, just exit and allow the normal one to spawn
						return;
					}
					
					demonFireball.setPosition(fireball.posX, fireball.posY, fireball.posZ);
					demonFireball.motionX = reflector.getGolemFireballAccelerationX(fireball);
					demonFireball.motionY = reflector.getGolemFireballAccelerationY(fireball);
					demonFireball.motionZ = reflector.getGolemFireballAccelerationZ(fireball);
					demonFireball.rotationYaw = fireball.rotationYaw;
					demonFireball.rotationPitch = fireball.rotationPitch;
					demonFireball.prevRotationYaw = fireball.prevRotationYaw;
					demonFireball.prevRotationPitch = fireball.prevRotationPitch;
					
					//Speedup
					double speedModifier = ModConfig.server.battletowers.golemLycanitesProjectileSpeedModifier;
					demonFireball.motionX *= speedModifier;
					demonFireball.motionY *= speedModifier;
					demonFireball.motionZ *= speedModifier;
	
					
					shooter.getEntityWorld().spawnEntity(demonFireball);
					
				}
				catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
				{
					ErrorUtil.logSilent("BT golemLycanitesProjectile Invocation");
				}
				
				
				/*
				 * ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile(projectileName);
					if(projectileInfo == null) {
						return null;
					}
					
					
					//en = projectileInfo.createProjectile(this.getEntityWorld(), this)
					
				 */
				
				//Cancel original fireball
				event.setCanceled(true);
				
				
			}
		}
	}
	
	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event)
	{
		//Golem Dormant Speed Fix / Golem Drowning Fix / Golem Speed Cap / Golem Dismount Fix
		boolean dormantSpeedFix = ModConfig.server.battletowers.golemDormantSpeedFix;
		boolean drownFix = ModConfig.server.battletowers.golemDrowningFix;
		double golemSpeedCap = ModConfig.server.battletowers.golemSpeedCap;
		boolean dismountFix = ModConfig.server.battletowers.golemAutoDismount;
		
		if(!dormantSpeedFix && !drownFix && !dismountFix && golemSpeedCap < 0.0d)
			return;
		
		//Check if the updating entity is a golem
		if(reflector.isEntityGolem(event.getEntityLiving()))
		{
			EntityLivingBase golem = event.getEntityLiving();
			
			if(dismountFix)
			{
				golem.dismountRidingEntity();
			}
			
			if(drownFix)
			{
				golem.setAir(300);
			}
			
			if(golemSpeedCap >= 0.0d)
			{
				if(golem.motionX > golemSpeedCap)
				{
					golem.motionX = golemSpeedCap;
				}
				else if(golem.motionX < -golemSpeedCap)
				{
					golem.motionX = -golemSpeedCap;
				}
				
				if(golem.motionZ > golemSpeedCap)
				{
					golem.motionZ = golemSpeedCap;
				}
				else if(golem.motionZ < -golemSpeedCap)
				{
					golem.motionZ = -golemSpeedCap;
				}

				//Allow golems to fall, but not rise
				//Removed, was preventing golems from using their stomp attack
				/*
				if(golem.motionY > golemSpeedCap)
				{
					golem.motionY = golemSpeedCap;
				}
				*/
			}
			
			if(dormantSpeedFix)
			{
				//Check if the golem is dormant
				
				try
				{
					if(reflector.getIsDormant(golem))
					{
						//If the golem is dormant, reset its motion
						golem.motionX = 0.0d;
						golem.motionY = 0.0d;
						golem.motionZ = 0.0d;
					}
				}
				catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
				{
					//Report
					ErrorUtil.logSilent("BT getIsDormant Invocation");
				}
			}
		}
	}
	
	//Registering on high so it always runs before BattleTowers' ServerTickHandler
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onTick(TickEvent.WorldTickEvent tick)
	{
		//Tower Explosion Credit
		
		if(!ModConfig.server.battletowers.towerExplosionNoCredit)
			return;
		
		if(System.currentTimeMillis() > tickedTime) 
		{
			tickedTime = System.currentTimeMillis() + 14000L; // its a fourteen second timer ZZZ
			
			//It takes 15000L for the tower destroyer to run its first explosion, so this will intervene before then
			//If the game gets paused while these timers are counting down, due to priority this will run before the tower starts exploding
			//Really shouldn't be pausing the game during these anyway...
			
			try
			{
				Set<Object> towerDestroyers = reflector.getTowerDestroyers();
				
				if(towerDestroyers!=null && towerDestroyers.size() > 0)
				{
					Iterator<Object> iterator = towerDestroyers.iterator();
					while(iterator.hasNext())
					{
						Object destroyer = iterator.next();
						if(destroyer!=null)
						{
							//TODO is null safe? There have been some recoil issues with null targets, does this apply here?
							reflector.setDestroyerPlayer(destroyer, null);
						}
					}
				}
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				//Just quietly put it in the rlerrorreport and call it a day
				ErrorUtil.logSilent("BT getTowerDestroyers Invocation");
				return;
			}
			
		}
	}
}
