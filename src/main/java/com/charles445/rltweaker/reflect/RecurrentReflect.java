package com.charles445.rltweaker.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;

public class RecurrentReflect
{
	//Anything to avoid using a class transformer, right?
	
	//Also reflects to the IvToolkit occasionally
	//Shouldn't be a problem..?
	
	public final Class c_WorldStructureGenerationData;
	public final Method m_WorldStructureGenerationData_get;
	public final Method m_WorldStructureGenerationData_structureEntriesIn;
	public final Method m_WorldStructureGenerationData_checkChunk;
	public final Field f_WorldStructureGenerationData_entryMap;
	public final Method m_WorldStructureGenerationData_removeEntry;
	
	public final Class c_WorldGenStructures;
	public final Method m_WorldGenStructures_complementStructuresInChunk;
	public final Method m_WorldGenStructures_planStaticStructuresInChunk;
	public final Method m_WorldGenStructures_distance;
	public final Method m_WorldGenStructures_randomSurfacePos;
	
	
	public final Class c_RCConfig;
	public final Field f_RCConfig_honorStructureGenerationOption;
	public final Field f_RCConfig_minDistToSpawnForGeneration;
	public final Method m_RCConfig_isGenerationEnabled_biome;
	public final Method m_RCConfig_isGenerationEnabled_worldprovider;
	
	
	public final Class c_StructureRegistry;
	public final Field f_StructureRegistry_instance;
	
	public final Class c_SelectivePlacer;
	
	public final Class c_Environment;
	public final Field f_Environment_biome;
	
	public final Class c_NaturalGeneration;
	public final Method m_NaturalGeneration_selectors;
	public final Method m_NaturalGeneration_getLimitations;
	public final Field f_NaturalGeneration_placer;
	public final Method m_NaturalGeneration_getGenerationWeight_params;
	
	public final Class c_NaturalGeneration$SpawnLimitation;
	public final Method m_NaturalGeneration$SpawnLimitation_areResolved;
	
	public final Class c_CachedStructureSelectors;
	public final Method m_CachedStructureSelectors_get;
	
	public final Class c_MixingStructureSelector;
	public final Method m_MixingStructureSelector_generatedStructures;
	
	public final Class c_SimpleLeveledRegistry;
	public final Method m_SimpleLeveledRegistry_id;

	public final Class c_Structure;
	public final Class c_GenerationType;
	
	public final Class c_StructureSpawnContext$GenerateMaturity;
	
	public final Class c_StructureGenerator;
	public final Constructor m_StructureGenerator_init;
	public final Method m_StructureGenerator_structure_structure;
	public final Method m_StructureGenerator_world_world;
	public final Method m_StructureGenerator_generationInfo_generationtype;
	public final Method m_StructureGenerator_seed_long;
	public final Method m_StructureGenerator_maturity_enum;
	public final Method m_StructureGenerator_randomPosition;
	public final Method m_StructureGenerator_fromCenter;
	public final Method m_StructureGenerator_partially;
	public final Method m_StructureGenerator_environment_none;
	public final Method m_StructureGenerator_generate;
	
	public final Class c_StructureGenerationEventLite$Post;
	
	//IvToolkit
	
	public final Class c_IvVecMathHelper;
	public final Method m_IvVecMathHelper_distanceSQ_d_d;
	
	public RecurrentReflect() throws Exception
	{
		c_WorldStructureGenerationData = Class.forName("ivorius.reccomplex.world.gen.feature.WorldStructureGenerationData");
		m_WorldStructureGenerationData_get = ReflectUtil.findMethod(c_WorldStructureGenerationData, "get");
		m_WorldStructureGenerationData_structureEntriesIn = ReflectUtil.findMethod(c_WorldStructureGenerationData, "structureEntriesIn");
		m_WorldStructureGenerationData_checkChunk = ReflectUtil.findMethod(c_WorldStructureGenerationData, "checkChunk");
		f_WorldStructureGenerationData_entryMap = ReflectUtil.findField(c_WorldStructureGenerationData, "entryMap");
		m_WorldStructureGenerationData_removeEntry = ReflectUtil.findMethod(c_WorldStructureGenerationData, "removeEntry");
		
		c_WorldGenStructures = Class.forName("ivorius.reccomplex.world.gen.feature.WorldGenStructures");
		m_WorldGenStructures_complementStructuresInChunk = ReflectUtil.findMethod(c_WorldGenStructures, "complementStructuresInChunk");
		m_WorldGenStructures_planStaticStructuresInChunk = ReflectUtil.findMethod(c_WorldGenStructures, "planStaticStructuresInChunk");
		m_WorldGenStructures_distance = ReflectUtil.findMethod(c_WorldGenStructures, "distance");
		m_WorldGenStructures_randomSurfacePos = ReflectUtil.findMethod(c_WorldGenStructures, "randomSurfacePos");
		
		c_RCConfig = Class.forName("ivorius.reccomplex.RCConfig");
		f_RCConfig_honorStructureGenerationOption = ReflectUtil.findField(c_RCConfig, "honorStructureGenerationOption");
		f_RCConfig_minDistToSpawnForGeneration = ReflectUtil.findField(c_RCConfig, "minDistToSpawnForGeneration");
		m_RCConfig_isGenerationEnabled_biome = c_RCConfig.getDeclaredMethod("isGenerationEnabled", Biome.class);
		m_RCConfig_isGenerationEnabled_biome.setAccessible(true);
		m_RCConfig_isGenerationEnabled_worldprovider = c_RCConfig.getDeclaredMethod("isGenerationEnabled", WorldProvider.class);
		m_RCConfig_isGenerationEnabled_worldprovider.setAccessible(true);
		
		c_StructureRegistry = Class.forName("ivorius.reccomplex.world.gen.feature.structure.StructureRegistry");
		f_StructureRegistry_instance = ReflectUtil.findField(c_StructureRegistry, "INSTANCE");

		c_SelectivePlacer = Class.forName("ivorius.reccomplex.world.gen.feature.structure.generic.placement.SelectivePlacer");
		
		c_Environment = Class.forName("ivorius.reccomplex.world.gen.feature.structure.Environment");
		f_Environment_biome = ReflectUtil.findField(c_Environment, "biome");
		
		c_NaturalGeneration = Class.forName("ivorius.reccomplex.world.gen.feature.structure.generic.generation.NaturalGeneration");
		m_NaturalGeneration_selectors = ReflectUtil.findMethod(c_NaturalGeneration, "selectors");
		m_NaturalGeneration_getLimitations = ReflectUtil.findMethod(c_NaturalGeneration, "getLimitations");
		f_NaturalGeneration_placer = ReflectUtil.findField(c_NaturalGeneration, "placer");
		m_NaturalGeneration_getGenerationWeight_params = c_NaturalGeneration.getDeclaredMethod("getGenerationWeight", WorldProvider.class, Biome.class);
		
		c_NaturalGeneration$SpawnLimitation = Class.forName("ivorius.reccomplex.world.gen.feature.structure.generic.generation.NaturalGeneration$SpawnLimitation");
		m_NaturalGeneration$SpawnLimitation_areResolved = ReflectUtil.findMethod(c_NaturalGeneration$SpawnLimitation, "areResolved");
		
		c_CachedStructureSelectors = Class.forName("ivorius.reccomplex.world.gen.feature.selector.CachedStructureSelectors");
		m_CachedStructureSelectors_get = ReflectUtil.findMethod(c_CachedStructureSelectors, "get");
		
		c_MixingStructureSelector = Class.forName("ivorius.reccomplex.world.gen.feature.selector.MixingStructureSelector");
		m_MixingStructureSelector_generatedStructures = ReflectUtil.findMethod(c_MixingStructureSelector, "generatedStructures");
		
		c_SimpleLeveledRegistry = Class.forName("ivorius.reccomplex.files.SimpleLeveledRegistry");
		m_SimpleLeveledRegistry_id = ReflectUtil.findMethod(c_SimpleLeveledRegistry, "id");
		
		c_Structure = Class.forName("ivorius.reccomplex.world.gen.feature.structure.Structure");
		c_GenerationType = Class.forName("ivorius.reccomplex.world.gen.feature.structure.generic.generation.GenerationType");
		
		c_StructureSpawnContext$GenerateMaturity = Class.forName("ivorius.reccomplex.world.gen.feature.structure.context.StructureSpawnContext$GenerateMaturity");
		
		
		c_StructureGenerator = Class.forName("ivorius.reccomplex.world.gen.feature.StructureGenerator");
		m_StructureGenerator_init = c_StructureGenerator.getDeclaredConstructor();
		m_StructureGenerator_init.setAccessible(true);
		m_StructureGenerator_structure_structure = c_StructureGenerator.getDeclaredMethod("structure", c_Structure);
		m_StructureGenerator_structure_structure.setAccessible(true);
		m_StructureGenerator_world_world = c_StructureGenerator.getDeclaredMethod("world", WorldServer.class);
		m_StructureGenerator_world_world.setAccessible(true);
		m_StructureGenerator_generationInfo_generationtype = c_StructureGenerator.getDeclaredMethod("generationInfo", c_GenerationType);
		m_StructureGenerator_generationInfo_generationtype.setAccessible(true);
		m_StructureGenerator_seed_long = c_StructureGenerator.getDeclaredMethod("seed", Long.class);
		m_StructureGenerator_seed_long.setAccessible(true);
		m_StructureGenerator_maturity_enum = c_StructureGenerator.getDeclaredMethod("maturity", c_StructureSpawnContext$GenerateMaturity);
		m_StructureGenerator_maturity_enum.setAccessible(true);
		m_StructureGenerator_randomPosition = ReflectUtil.findMethod(c_StructureGenerator, "randomPosition");
		m_StructureGenerator_fromCenter = ReflectUtil.findMethod(c_StructureGenerator, "fromCenter");
		m_StructureGenerator_partially = ReflectUtil.findMethod(c_StructureGenerator, "partially");
		m_StructureGenerator_environment_none = c_StructureGenerator.getDeclaredMethod("environment");
		m_StructureGenerator_environment_none.setAccessible(true);
		m_StructureGenerator_generate = ReflectUtil.findMethod(c_StructureGenerator, "generate");
		
		c_StructureGenerationEventLite$Post = Class.forName("ivorius.reccomplex.events.StructureGenerationEventLite$Post");
		
		//IvToolkit
		
		c_IvVecMathHelper = Class.forName("ivorius.ivtoolkit.math.IvVecMathHelper");
		m_IvVecMathHelper_distanceSQ_d_d = c_IvVecMathHelper.getDeclaredMethod("distanceSQ", double[].class, double[].class);
		m_IvVecMathHelper_distanceSQ_d_d.setAccessible(true);
	}
	
	public Enum generateMaturityFromString(String name)
	{
		return Enum.valueOf(c_StructureSpawnContext$GenerateMaturity, name);
	}
	
	public Object getWorldStructureGenerationData(World world) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return m_WorldStructureGenerationData_get.invoke(null, world); //static
	}
	
	public List getStructuresComplementList(Object i_WorldStructureGenerationData, ChunkPos chunkPos) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (List)((Stream)m_WorldStructureGenerationData_structureEntriesIn.invoke(i_WorldStructureGenerationData, chunkPos)).collect(Collectors.toList());
	}
	
	public boolean checkChunk(Object i_WorldStructureGenerationData, ChunkPos chunkPos) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (boolean) m_WorldStructureGenerationData_checkChunk.invoke(i_WorldStructureGenerationData, chunkPos);
	}
	
	public Object getEntryMap(Object i_WorldStructureGenerationData) throws IllegalArgumentException, IllegalAccessException
	{
		return f_WorldStructureGenerationData_entryMap.get(i_WorldStructureGenerationData);
	}
	
	public Object removeEntry(Object i_WorldStructureGenerationData, UUID uuid) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return m_WorldStructureGenerationData_removeEntry.invoke(i_WorldStructureGenerationData, uuid);
	}
	
	public void complementStructuresInChunk(ChunkPos chunkPos, WorldServer world, List structuresComplementList) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		m_WorldGenStructures_complementStructuresInChunk.invoke(null, chunkPos, world, structuresComplementList);
	}
	
	public void planStaticStructuresInChunk(Random random, ChunkPos chunkPos, WorldServer world, BlockPos spawnPos, @Nullable Object structurePredicate) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		m_WorldGenStructures_planStaticStructuresInChunk.invoke(null, random, chunkPos, world, spawnPos, structurePredicate);
	}
	
	public float distance(ChunkPos chunkA, ChunkPos chunkB) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (float) m_WorldGenStructures_distance.invoke(null, chunkA, chunkB);
	}
	
	public Object getRandomSurfacePos(ChunkPos chunkPos, long seed) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return m_WorldGenStructures_randomSurfacePos.invoke(null, chunkPos, seed);
	}
	
	public boolean honorStructureGenerationOption() throws IllegalArgumentException, IllegalAccessException
	{
		return f_RCConfig_honorStructureGenerationOption.getBoolean(null);
	}
	
	public float minDistToSpawnForGeneration() throws IllegalArgumentException, IllegalAccessException
	{
		return f_RCConfig_minDistToSpawnForGeneration.getFloat(null);
	}
	
	public boolean isGenerationEnabled(Biome biome) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (boolean) m_RCConfig_isGenerationEnabled_biome.invoke(null, biome);
	}
	
	public boolean isGenerationEnabled(WorldProvider provider) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (boolean) m_RCConfig_isGenerationEnabled_worldprovider.invoke(null, provider);
	}
	
	public Object getStructureRegistryInstance() throws IllegalArgumentException, IllegalAccessException
	{
		return f_StructureRegistry_instance.get(null);
	}
	
	public Object getNaturalGenerationSelectors(Object structureRegistry) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return m_NaturalGeneration_selectors.invoke(null, structureRegistry);
	}
	
	public Object getNaturalGenerationLimitations(Object naturalGeneration) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return m_NaturalGeneration_getLimitations.invoke(naturalGeneration);
	}
	
	public Object getNaturalGenerationPlacer(Object naturalGeneration) throws IllegalArgumentException, IllegalAccessException
	{
		return f_NaturalGeneration_placer.get(naturalGeneration);
	}
	
	public double getNaturalGenerationWeight(Object naturalGeneration, WorldProvider provider, Biome biome) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (double) m_NaturalGeneration_getGenerationWeight_params.invoke(naturalGeneration, provider, biome);
	}
	
	public boolean isSpawnLimitationResolved(Object spawnLimitation, World world, String structureName) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (boolean) m_NaturalGeneration$SpawnLimitation_areResolved.invoke(spawnLimitation, world, structureName);
	}
	
	public Object getSelectorFromCachedSelectors(Object cached, Biome biome, WorldProvider provider) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return m_CachedStructureSelectors_get.invoke(cached, biome, provider);
	}
	
	public List<Pair<Object, Object>> getStructurePairsWithSelector(Object selector, Random random, Biome biome, WorldProvider provider, Float spawnDistance) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (List<Pair<Object, Object>>) m_MixingStructureSelector_generatedStructures.invoke(selector, random, biome, provider, spawnDistance);
	}
	
	public String getStructureName(Object structure) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Object structureRegistryInstance = getStructureRegistryInstance();
		return (String) m_SimpleLeveledRegistry_id.invoke(structureRegistryInstance, structure);
	}
	
	public Object newStructureGenerator() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return m_StructureGenerator_init.newInstance();
	}
	
	public Biome environmentToBiome(Object environment) throws IllegalArgumentException, IllegalAccessException
	{
		return (Biome) f_Environment_biome.get(environment);
	}
	
	//Generator 
	public Object setStructure(Object generator, Object structure) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return m_StructureGenerator_structure_structure.invoke(generator, structure);
	}
	
	public Object setWorld(Object generator, WorldServer world) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return m_StructureGenerator_world_world.invoke(generator, world);
	}
	
	public Object setGenerationInfo(Object generator, Object generationType) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return m_StructureGenerator_generationInfo_generationtype.invoke(generator, generationType);
	}
	
	public Object setSeed(Object generator, long seed) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return m_StructureGenerator_seed_long.invoke(generator, seed);
	}
	
	public Object setMaturity(Object generator, Object maturityEnum) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return m_StructureGenerator_maturity_enum.invoke(generator, maturityEnum);
	}
	
	public Object setRandomPosition(Object generator, Object blockSurfacePos, @Nullable Object placer) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return m_StructureGenerator_randomPosition.invoke(generator, blockSurfacePos, placer);
	}
	
	public Object setFromCenter(Object generator, boolean value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return m_StructureGenerator_fromCenter.invoke(generator, value);
	}
	
	public Object partially(Object generator, boolean partially, ChunkPos chunkPos) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		//This is the important one!
		return m_StructureGenerator_partially.invoke(generator, partially, chunkPos);
	}
	
	public Object getEnvironment(Object generator) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return m_StructureGenerator_environment_none.invoke(generator);
	}
	
	public Object generate(Object generator) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return m_StructureGenerator_generate.invoke(generator);
	}
	
	//IvToolkit
	
	public double distanceSq(double[] pos1, double[] pos2) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (double) m_IvVecMathHelper_distanceSQ_d_d.invoke(null, pos1, pos2);
	}
}
