package com.charles445.rltweaker.handler;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.debug.DebugUtil;
import com.charles445.rltweaker.reflect.BattleTowersReflect;
import com.charles445.rltweaker.util.AIUtil;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.WorldGeneratorWrapper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class BattleTowersHandler
{
	public long tickedTime;
	
	BattleTowersReflect reflector;
	
	public BattleTowersHandler()
	{
		try
		{
			reflector = new BattleTowersReflect();
			
			if(ModConfig.server.battletowers.dimensionBlacklistEnabled || ModConfig.server.battletowers.consistentTowerGeneration)
			{
				CompatUtil.wrapSpecificHandler("BTWorldLoadEvent", BTWorldLoadEvent::new, "atomicstryker.battletowers.common.WorldGenHandler", "eventWorldLoad");
				//BattleTowers oversight means there are two WorldGenHandler instances on the event bus
				CompatUtil.findAndRemoveHandlerFromEventBus("atomicstryker.battletowers.common.WorldGenHandler", "eventWorldLoad");
			}
			if(ModConfig.server.battletowers.consistentTowerGeneration)
			{
				CompatUtil.wrapSpecificHandler("BTWorldSaveEvent", BTWorldSaveEvent::new, "atomicstryker.battletowers.common.WorldGenHandler", "eventWorldSave");
				//BattleTowers oversight means there are two WorldGenHandler instances on the event bus
				CompatUtil.findAndRemoveHandlerFromEventBus("atomicstryker.battletowers.common.WorldGenHandler", "eventWorldSave");
				
				//Wrap generator
				BTWorldGenerator generator = new BTWorldGenerator();
				generator = CompatUtil.tryWrapWorldGenerator(generator, reflector.c_WorldGenHandler);
			}
			
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
		if(ModConfig.server.battletowers.golemHighAggression && reflector.isEntityGolem(event.getEntity()))
		{
			EntityCreature golem = (EntityCreature)event.getEntity();
			AIUtil.tryAndReplaceAllTasks(golem, golem.targetTasks, EntityAIHurtByTarget.class, (oldTask -> new GolemHurtByTarget(golem, false, new Class[0])));
		}
		
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
	public void onLivingAttack(LivingAttackEvent event)
	{
		boolean suffocationFix = ModConfig.server.battletowers.golemSuffocatingFix;
		boolean fallingBlockFix = ModConfig.server.battletowers.golemFallingBlockFix;
		boolean anvilFix = ModConfig.server.battletowers.golemAnvilFix;
		boolean lycanitesFluidFix = ModConfig.server.battletowers.golemLycanitesFluidFix;
		
		//Check applicable config
		if(!suffocationFix && !fallingBlockFix && !anvilFix && !lycanitesFluidFix)
			return;
		
		String damageType = event.getSource().getDamageType();
		
		//Suffocation
		if(suffocationFix && damageType.equals("inWall") && reflector.isEntityGolem(event.getEntityLiving()))
		{
			event.setCanceled(true);
			return;
		}
		
		//Falling Blocks
		if(fallingBlockFix && damageType.equals("fallingBlock") && reflector.isEntityGolem(event.getEntityLiving()))
		{
			event.setCanceled(true);
			return;
		}
		
		//Anvil
		if(anvilFix && damageType.equals("anvil") && reflector.isEntityGolem(event.getEntityLiving()))
		{
			event.setCanceled(true);
			return;
		}
		
		//Lycanites Fluid
		if(lycanitesFluidFix)
		{
			if(damageType.equals("acid") || damageType.equals("ooze"))
			{
				if(reflector.isEntityGolem(event.getEntityLiving()))
				{
					event.setCanceled(true);
					return;
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event)
	{
		//Golem Dormant Speed Fix / Golem Drowning Fix / Golem Speed Cap / Golem Dismount Fix / Golem Lycanites Fluid Fix
		boolean dormantSpeedFix = ModConfig.server.battletowers.golemDormantSpeedFix;
		boolean drownFix = ModConfig.server.battletowers.golemDrowningFix;
		double golemSpeedCap = ModConfig.server.battletowers.golemSpeedCap;
		double golemSpeedCapUpwards = ModConfig.server.battletowers.golemSpeedCapUpwards;
		boolean dismountFix = ModConfig.server.battletowers.golemAutoDismount;
		
		if(!dormantSpeedFix && !drownFix && !dismountFix && golemSpeedCap < 0.0d && golemSpeedCapUpwards < 0.0d)
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
			
			if(golemSpeedCapUpwards >= 0.0d)
			{
				if(golem.motionY > golemSpeedCapUpwards)
				{
					golem.motionY = golemSpeedCapUpwards;
				}
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
	
	@SubscribeEvent
	public void onApplyPotion(PotionApplicableEvent event)
	{
		if(ModConfig.server.battletowers.golemLycanitesFluidFix && reflector.isEntityGolem(event.getEntityLiving()))
		{
			PotionEffect effect = event.getPotionEffect();
			if(effect == null)
				return;
			
			String effectName = effect.getEffectName();
			if(effectName.equals("effect.plague"))
			{
				event.setResult(Event.Result.DENY);
			}
		}
	}
	
	public boolean isDimensionWhitelisted(World world)
	{
		if(!ModConfig.server.battletowers.dimensionBlacklistEnabled)
			return true;
		
		int dimension = world.provider.getDimension();
		int[] blackListIds = ModConfig.server.battletowers.dimensionBlacklistIds;
		boolean blacklistHasDimension = false;
		
		for(int i=0;i<blackListIds.length;i++)
		{
			if(blackListIds[i] == dimension)
			{
				blacklistHasDimension = true;
				break;
			}
		}
		
		return blacklistHasDimension == ModConfig.server.battletowers.dimensionBlacklistIsWhitelist;
	}
	
	public class BTWorldLoadEvent
	{
		private IEventListener handler;
		public BTWorldLoadEvent(IEventListener handler)
		{
			this.handler = handler;
			MinecraftForge.EVENT_BUS.register(this);
		}
		
		@SubscribeEvent
		public void onWorldLoad(final WorldEvent.Load event)
		{
			//Disable world handle interactions if using consistent tower generation
			if(ModConfig.server.battletowers.consistentTowerGeneration)
				return;
			handler.invoke(event);
			
			if(ModConfig.server.battletowers.dimensionBlacklistEnabled && !isDimensionWhitelisted(event.getWorld()))
			{
				//Block the dimension by sabotaging the WorldHandle
				boolean success = false;
				try
				{
					success = reflector.setWorldDisableGenerationHook(event.getWorld(), 1000);
				}
				catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e)
				{
					
				}
				
				if(!success)
				{
					RLTweaker.logger.error("Failed to prevent Battletowers from spawning in loaded dimension: "+event.getWorld().provider.getDimension());
					ErrorUtil.logSilent("BT Dimension Blacklist Failure");
				}
			}
		}
	}
	
	public class BTWorldSaveEvent
	{
		private IEventListener handler;
		public BTWorldSaveEvent(IEventListener handler)
		{
			this.handler = handler;
			MinecraftForge.EVENT_BUS.register(this);
		}
		
		@SubscribeEvent
		public void onWorldSave(final WorldEvent.Save event)
		{
			//Disable world handle interactions if using consistent tower generation
			if(ModConfig.server.battletowers.consistentTowerGeneration)
				return;
			handler.invoke(event);
		}
	}
	
	public class GolemHurtByTarget extends EntityAIHurtByTarget
	{
		public GolemHurtByTarget(EntityCreature creature, boolean entityCallsForHelp, Class<?>[] excludedReinforcements)
		{
			super(creature, entityCallsForHelp, excludedReinforcements);
			this.shouldCheckSight = false;
		}
		
		@Override
		public double getTargetDistance()
		{
			double ret = super.getTargetDistance();
			ret = 64.0d;
			//DebugUtil.messageAll("GHBT getTargetDistance "+ret);
			return ret;
		}
	}
	
	public class BTWorldGenerator extends WorldGeneratorWrapper
	{
		private int setup = -1; //-1 is init, 0 is failed, other is success
		private Object worldGenHandler = null;
		private int minDistanceFromSpawn;
		private int minDistanceBetweenTowers;
		private double minDistanceBetweenTowersArea;
		
		@Override
		public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
		{
			//Guarantee battletowers generate on even chunks, to avoid certain troublesome cascading cases and tower collisions
			int alignment = 2; 
			
			if(chunkX % alignment != 0)
				return;
			
			if(chunkZ % alignment != 0)
				return;
			
			if(setup == -1)
			{
				//First time setup
				try
				{
					worldGenHandler = reflector.c_WorldGenHandler.cast(getWrappedGenerator());
					minDistanceFromSpawn = reflector.getMinDistanceFromSpawn();
					minDistanceBetweenTowers = reflector.getMinDistanceBetweenTowers();
					minDistanceBetweenTowersArea = Math.PI * minDistanceBetweenTowers * minDistanceBetweenTowers / 1024.0d; //1024 = chunkSq * halfRadiusSq (16 * 16 * 2 * 2)
					
					//Adjust for alignment
					minDistanceBetweenTowersArea /= alignment;
					minDistanceBetweenTowersArea /= alignment;
					
					if(minDistanceBetweenTowersArea < 1.0d)
						minDistanceBetweenTowersArea = 1.0d;
					
					//Succeeded in setup
					setup = 1;
				}
				catch(ClassCastException | IllegalArgumentException | IllegalAccessException e)
				{
					//Failed
					//TODO error
					e.printStackTrace();
					setup = 0;
				}
			}
			
			if(setup == 0)
			{
				//Failed, use default generator
				this.generateWrapped(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
				return;
			}
			
			if(!isDimensionWhitelisted(world))
				return;
			
			try
			{
				//Check biome and provider
				int blockX = chunkX * 16;
				int blockZ = chunkZ * 16;
				BlockPos blockPos = new BlockPos(blockX, 0, blockZ);
				Biome biome = world.getBiome(blockPos.add(16, 0, 16));
				BlockPos middlePos = blockPos.add(8, 0, 8);
				
				if (biome != Biome.getBiome(8) && reflector.getIsBiomeAllowed(worldGenHandler, biome) && reflector.getIsChunkProviderAllowed(worldGenHandler, chunkProvider))
				{
					//Check distance from spawn
					BlockPos spawn = world.getSpawnPoint();
					int xx = spawn.getX() - middlePos.getX();
					int zz = spawn.getZ() - middlePos.getZ();
					if(Math.sqrt((xx * xx) + (zz * zz)) < minDistanceFromSpawn)
						return;
					
					//Make the seed somewhat unique so our next random call doesn't overlap with other mods
					//For example, waystones were showng up on towers a suspiciously high amount of the time
					random.setSeed(((random.nextLong() * 4455743L) + 7L) * 4455743L);
					
					
					//Check tower randomness based on minimum distance
					double threshold = 1.0d / minDistanceBetweenTowersArea;
					if(random.nextDouble() > threshold)
						return;
					
					int blockY = reflector.getSurfaceBlockHeight(worldGenHandler, world, blockX, blockZ);
					if(blockY <= 49)
						return;
					
					//Construct new TowerPosition
					Object towerPosition = reflector.newTowerPosition(worldGenHandler, blockX, 0, blockZ, 0, false);
					
					//BattleTowers uses world rand for some stuff, so we need to freshen up the world random seed a little bit
					world.rand.setSeed(random.nextLong());
					
					//Spawn tower
					boolean spawned = reflector.attemptToSpawnTower(worldGenHandler, world, towerPosition, random, middlePos.getX(), blockY, middlePos.getZ());
					if(spawned)
					{
						RLTweaker.logger.trace("BattleTower generation success: "+middlePos.getX()+" "+middlePos.getZ());
					}
					else
					{
						RLTweaker.logger.trace("BattleTower generation failure: "+middlePos.getX()+" "+middlePos.getZ());
					}
				}
			}
			catch(IllegalArgumentException | IllegalAccessException | InvocationTargetException | InstantiationException e)
			{
				//TODO error once
				this.generateWrapped(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
				return;
			}
		}
	}
	
}
