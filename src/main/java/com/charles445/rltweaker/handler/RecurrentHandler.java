package com.charles445.rltweaker.handler;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.tuple.Pair;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.debug.DebugUtil;
import com.charles445.rltweaker.reflect.RecurrentReflect;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** Handler for RecurrentComplex! <br>
 * Does a routine DAT cleanup, and modifies natural structure generation for performance purposes.
*/
public class RecurrentHandler
{
	private RecurrentReflect reflector;
	private boolean reflector_enabled = true;
	
	//TODO Does checkedChunks actually get used by anything? Might be worth blocking entirely
	//Cleanup doesn't seem to affect checkedChunks. Shouldn't be a problem I don't think, but it's worth noting.
	
	public RecurrentHandler()
	{
		//Currently created during INIT
		
		try
		{
			//Replaces event handler from PREINIT with one from INIT
			
			//This will currently not cause any incompatibilities
			
			if(ModConfig.server.recurrentcomplex.manageRCForgeEventHandler)
			{
				//Manage RCForgeEventHandler
				
				//TODO split off into separate handlers or rename them appropriately, if any more get made
				
				if(!ModConfig.server.recurrentcomplex.generatePartially)
				{
					Object handler = CompatUtil.findAndRemoveHandlerFromEventBus("ivorius.reccomplex.events.handlers.RCForgeEventHandler", "onPreChunkDecoration");
					if(handler!=null)
					{
						RLTweaker.logger.info("Registering RCHandler to the event bus");
						new RCHandler(handler);
					}
				}
			}
			
			if(ModConfig.server.recurrentcomplex.cleanStructureData)
			{
				//Clean structure data
				RLTweaker.logger.info("Registering RCCleanup to the event bus");
				new RCCleanup(); //Registers itself
			}
		}
		catch(Exception e)
		{
			RLTweaker.logger.error("Failed to setup RecurrentHandler!", e);
			ErrorUtil.logSilent("Recurrent Critical Setup Failure");
			
			//Crash on Critical
			if(e instanceof CriticalException)
				throw new RuntimeException(e);
		}
	}
	
	public class RCCleanup
	{
		//TODO swap direction of sensitivity so configuring ingame actually does what you expect
		private Map<Integer,Integer> dimensionSensitivity;
		
		public RCCleanup()
		{
			dimensionSensitivity = new ConcurrentHashMap<Integer,Integer>();

			if(!checkReflector())
				return;
			
			Class selfClazz = this.getClass();
			
			try
			{
				CompatUtil.subscribeEventManually(WorldEvent.Load.class, this, ReflectUtil.findMethod(selfClazz, "onWorldLoad"));
				CompatUtil.subscribeEventManually(reflector.c_StructureGenerationEventLite$Post, this, ReflectUtil.findMethod(selfClazz, "onStructureGenerationLitePost"));
			}
			catch (Exception e)
			{
				RLTweaker.logger.error("Failed to setup RCCleanup!", e);
				ErrorUtil.logSilent("RCCleanup Critical Failure");
				
				//Crash on Critical
				if(e instanceof CriticalException)
					throw new RuntimeException(e);
			}
		}
		
		private void resetSensitivity(int dimension)
		{
			dimensionSensitivity.put(dimension, ModConfig.server.recurrentcomplex.cleanStructureThreshold);
		}
		
		@SubscribeEvent(priority = EventPriority.LOW)
		public void onStructureGenerationLitePost(Object event)
		{	
			WorldEvent worldEvent = (WorldEvent)event;
			World world = worldEvent.getWorld();
			if(world.isRemote)
				return;
			
			//The key should have been made in WorldEvent.Load, but if it didn't somehow, this will do it.
			int dimension = world.provider.getDimension();
			if(!dimensionSensitivity.containsKey(dimension))
				resetSensitivity(dimension);
			
			//Get current sensitivity for dimension
			int sensitivity = dimensionSensitivity.get(dimension);
			
			//Decrement sensitivity and check if action should be skipped
			sensitivity-=1;
			if(sensitivity>0)
			{
				//Skip action
				dimensionSensitivity.put(dimension, sensitivity);
				return;
			}
			
			//Action is not skipped, reset dimension sensitivity
			resetSensitivity(dimension);
			
			//Clean all structures
			
			int amt = -1;
			
			try
			{
				amt = cleanAllStructures(world);
			}
			catch(IllegalArgumentException | IllegalAccessException | InvocationTargetException e)
			{
				RLTweaker.logger.error("onStructureGenerationLitePost critical failure in RCCLeaner!", e);
				ErrorUtil.logSilent("RCCleanup Critical Failure");
				throw new RuntimeException(e);
			}
			
			if(ModConfig.server.minecraft.debug)
				DebugUtil.messageAll("Cleaned up structures: "+amt);
		}
		
		@SubscribeEvent(priority = EventPriority.HIGH)
		public void onWorldLoad(WorldEvent.Load event)
		{
			World world = event.getWorld();
			if(world.isRemote)
				return;
			
			//Wipe all recurrent data
			try
			{
				wipeRecurrentData(world);
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				RLTweaker.logger.error("onWorldLoad critical failure in RCCleaner!", e);
				ErrorUtil.logSilent("RCCleanup Critical Failure");
				throw new RuntimeException(e);
			}
			
			//Setup dimensionSensitivity for the world. This is important to do to avoid cross-save issues
			int dimension = world.provider.getDimension();
			dimensionSensitivity.put(dimension, ModConfig.server.recurrentcomplex.cleanStructureThreshold);
		}
		
		private int cleanAllStructures(World world) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
		{
			//Get all entry UUIDs
			Object wsgData = reflector.getWorldStructureGenerationData(world);
			
			synchronized(wsgData)
			{
				//Map <UUID, Entry>
				final Map<UUID, Object> entryMap = (Map<UUID, Object>)reflector.getEntryMap(wsgData);
				final Set<UUID> entrySet = entryMap.keySet();
				
				//Avoid concurrency issues by cloning the set
				final Set<UUID> entrySetClone = new HashSet<UUID>();
				entrySetClone.addAll(entrySet);
				
				for(UUID uuid : entrySetClone)
				{
					reflector.removeEntry(wsgData, uuid);
				}
				
				return entrySetClone.size();
			}
		}
		
		private void wipeRecurrentData(World world) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
		{
			Object wsgData = reflector.getWorldStructureGenerationData(world);
			
			synchronized(wsgData)
			{
				WorldSavedData data = (WorldSavedData)wsgData;
				
				NBTTagCompound compound = data.serializeNBT();
				
				compound.removeTag("entries");
				compound.removeTag("checkedChunks");
				compound.removeTag("checkedChunksFinal");
				
				data.readFromNBT(compound);
				world.getPerWorldStorage().setData(data.mapName, data);
			}
		}
	}
	
	public class RCHandler
	{
		//RCForgeEventHandler
		//NOTE: This should be an IEventListener
		private Object handler;
		
		//Currently only onPreChunkDecoration has been intercepted
		//The handler is still being used by forge
		
		public RCHandler(Object handler)
		{
			this.handler = handler;
			if(!checkReflector())
				return;
			
			MinecraftForge.EVENT_BUS.register(this);
		}
		
		@SubscribeEvent
		public void onPreChunkDecoration(PopulateChunkEvent.Pre event)
		{
			try
			{
				decorate((WorldServer)event.getWorld(), event.getRand(), new ChunkPos(event.getChunkX(), event.getChunkZ()));
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				RLTweaker.logger.error("RecurrentReflect invocation failure!", e);
				ErrorUtil.logSilent("RecurrentReflect Critical Invocation Failure");
				
				throw new RuntimeException(e);
			}
		}
		
		private void decorate(WorldServer world, Random random, ChunkPos chunkPos) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
		{
			//Assumes reflector exists
			
			//Treat structurePredicate as null!
			
			boolean mapFeatures = world.getWorldInfo().isMapFeaturesEnabled();
			Object wsgData = reflector.getWorldStructureGenerationData(world);
			
			synchronized(wsgData)
			{
				//Handle complement
				List structuresComplementList = reflector.getStructuresComplementList(wsgData, chunkPos);
				reflector.checkChunk(wsgData, chunkPos);
				reflector.complementStructuresInChunk(chunkPos, world, structuresComplementList);
				
				//Structure generation
				if(mapFeatures || !reflector.honorStructureGenerationOption())
				{
					Biome biome = world.getBiome(chunkPos.getBlock(8, 0, 8));
					BlockPos spawnPos = world.getSpawnPoint();
					
					reflector.planStaticStructuresInChunk(random, chunkPos, world, spawnPos, null);
					
					boolean generate = reflector.isGenerationEnabled(biome) && reflector.isGenerationEnabled(world.provider);
					
					if(world.provider.getDimension() == 0)
					{
						double[] chunkArr = new double[]{chunkPos.x * 16 + 8, chunkPos.z * 16 + 8};
						double[] spawnArr = new double[]{spawnPos.getX(), spawnPos.getZ()};
						
						double spawnDistSq = reflector.distanceSq(chunkArr, spawnArr);
						
						float minDistSq = reflector.minDistToSpawnForGeneration();
						minDistSq = minDistSq * minDistSq;
						
						generate &= spawnDistSq >= minDistSq;
					}
					
					if(generate)
						planStructuresInChunk(random, chunkPos, world, biome);
				}
			}
		}
		
		private void planStructuresInChunk(Random random, ChunkPos chunkPos, WorldServer world, Biome biome) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
		{
			//Assumes reflector exists
			
			//Treat structurePredicate as null!
			
			//I want to get off Mr Reflector's wild ride
			
			float spawnDistance = reflector.distance(new ChunkPos(world.getSpawnPoint()), chunkPos);
			
			Object structureRegistryInstance = reflector.getStructureRegistryInstance(); //StructureRegistry
			Object cachedSelectors = reflector.getNaturalGenerationSelectors(structureRegistryInstance); //CachedStructureSelectors
			Object selector = reflector.getSelectorFromCachedSelectors(cachedSelectors, biome, world.provider); //MixingStructureSelector
			
			List<Pair<Object, Object>> generatedStructures = reflector.getStructurePairsWithSelector(selector, random, world.getBiome(chunkPos.getBlock(0, 0, 0)), world.provider, spawnDistance);
			
			generatedStructures.stream().forEach(pair -> {
				try
				{
					planStructureInChunk(chunkPos, world, pair.getLeft(), pair.getRight(), random.nextLong());
				}
				catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | InstantiationException e)
				{
					RLTweaker.logger.error("planStructuresInChunk failed on stream!",e);
					ErrorUtil.logSilent("RecurrentReflect Critical Stream Failure");
					throw new RuntimeException(e);
				}
			});
			
			
		}
		
		private void planStructureInChunk(ChunkPos chunkPos, WorldServer world, Object structure, Object naturalGeneration, long seed) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException
		{
			//Assumes reflector exists
			
			//THE RIDE NEVER ENDS!
			
			String structureName = reflector.getStructureName(structure);
			
			Object blockSurfacePos = reflector.getRandomSurfacePos(chunkPos, seed);
			
			Object limitations = reflector.getNaturalGenerationLimitations(naturalGeneration);
			
			//TODO safe to skip the hasLimitations reflection?
			if(limitations==null || reflector.isSpawnLimitationResolved(limitations, world, structureName))
			{
				//Set up SUGGEST enum and placer
				Object maturityEnum = reflector.generateMaturityFromString("SUGGEST");
				Object placer = reflector.getNaturalGenerationPlacer(naturalGeneration);
				
				//Set up generator
				Object generator = reflector.newStructureGenerator();
				generator = reflector.setStructure(generator, structure);
				generator = reflector.setWorld(generator, world);
				generator = reflector.setGenerationInfo(generator, naturalGeneration);
				generator = reflector.setSeed(generator, seed);
				generator = reflector.setMaturity(generator, maturityEnum);
				generator = reflector.setRandomPosition(generator, blockSurfacePos, placer);
				generator = reflector.setFromCenter(generator, true);
				
				//This is all we're here for, for now...
				generator = reflector.partially(generator, false, chunkPos);
				
				//Check weight or whatever
				Object environment = reflector.getEnvironment(generator);
				Biome enviroBiome = reflector.environmentToBiome(environment);
				
				if(reflector.getNaturalGenerationWeight(naturalGeneration, world.provider, enviroBiome) <= 0)
				{
					RLTweaker.logger.trace(""+structure+" failed to spawn at "+blockSurfacePos+" (incompatible biome edge)");
					return;
				}
				
				//Generate!
				
				Object generationResult = reflector.generate(generator);
						
				//TODO do anything with this result?
				
				RLTweaker.logger.trace("Structure Generated: "+structure);
			}
		}
	}
	private boolean checkReflector()
	{
		if(this.reflector==null)
		{
			if(this.reflector_enabled)
			{
				try
				{
					this.reflector = new RecurrentReflect();
					return true;
				}
				catch(Exception e)
				{
					this.reflector = null;
					this.reflector_enabled = false;
					RLTweaker.logger.error("Failed to setup RecurrentReflect!", e);
					ErrorUtil.logSilent("RecurrentReflect Critical Setup Failure");
					throw new RuntimeException(e);
				}
			}
			
			//Reflector failed to setup or is disabled
			//Dead code, probably
			return false;
		}
		
		//Reflector exists
		return true;
	}
}
