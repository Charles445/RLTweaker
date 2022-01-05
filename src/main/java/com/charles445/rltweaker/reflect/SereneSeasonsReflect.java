package com.charles445.rltweaker.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockIce;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class SereneSeasonsReflect
{
	public final Class c_SeasonHelper;
	public final Method m_SeasonHelper_getSeasonState;
	
	public final Class c_ISeasonState;
	public final Method m_ISeasonState_getSubSeason;
	
	public final Class c_Season$SubSeason;
	
	public final Class c_ModConfig;
	public final Field f_ModConfig_seasons;
	
	public final Class c_SeasonsConfig;
	public final Method m_SeasonsConfig_isDimensionWhitelisted;
	public final Field f_SeasonsConfig_generateSnowAndIce;
	public final Field f_SeasonsConfig_changeWeatherFrequency;
	
	public final Class c_BiomeConfig;
	public final Field f_BiomeConfig_biomeDataMap;
	
	public final Class c_BiomeData;
	public final Field f_BiomeData_enableSeasonalEffects;
	public final Field f_BiomeData_useTropicalSeasons;
	
	public final Method m_BlockIce_turnIntoWater;
	
	public SereneSeasonsReflect() throws Exception
	{
		c_SeasonHelper = Class.forName("sereneseasons.api.season.SeasonHelper");
		m_SeasonHelper_getSeasonState = c_SeasonHelper.getDeclaredMethod("getSeasonState", World.class);
		
		c_ISeasonState = Class.forName("sereneseasons.api.season.ISeasonState");
		m_ISeasonState_getSubSeason = c_ISeasonState.getDeclaredMethod("getSubSeason");
		
		c_Season$SubSeason = Class.forName("sereneseasons.api.season.Season$SubSeason");
		
		c_ModConfig = Class.forName("sereneseasons.init.ModConfig");
		f_ModConfig_seasons = ReflectUtil.findField(c_ModConfig, "seasons");
		
		c_SeasonsConfig = Class.forName("sereneseasons.config.SeasonsConfig");
		m_SeasonsConfig_isDimensionWhitelisted = ReflectUtil.findMethod(c_SeasonsConfig, "isDimensionWhitelisted");
		f_SeasonsConfig_generateSnowAndIce = ReflectUtil.findField(c_SeasonsConfig, "generateSnowAndIce");
		f_SeasonsConfig_changeWeatherFrequency = ReflectUtil.findField(c_SeasonsConfig, "changeWeatherFrequency");
		
		c_BiomeConfig = Class.forName("sereneseasons.config.BiomeConfig");
		f_BiomeConfig_biomeDataMap = ReflectUtil.findField(c_BiomeConfig, "biomeDataMap");
		
		c_BiomeData = Class.forName("sereneseasons.config.json.BiomeData");
		f_BiomeData_enableSeasonalEffects = ReflectUtil.findField(c_BiomeData, "enableSeasonalEffects");
		f_BiomeData_useTropicalSeasons = ReflectUtil.findField(c_BiomeData, "useTropicalSeasons");
		
		m_BlockIce_turnIntoWater = ReflectUtil.findMethodAny(BlockIce.class, "func_185679_b", "turnIntoWater", World.class, BlockPos.class);
	}
	
	public Object getISeasonState(World world) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return m_SeasonHelper_getSeasonState.invoke(null, world);
	}
	
	public Object getSeasonConfig() throws IllegalArgumentException, IllegalAccessException
	{
		return f_ModConfig_seasons.get(null);
	}
	
	public boolean getConfigGenerateSnowAndIce(Object config) throws IllegalArgumentException, IllegalAccessException
	{
		return f_SeasonsConfig_generateSnowAndIce.getBoolean(config);
	}
	
	public boolean getConfigChangeWeatherFrequency(Object config) throws IllegalArgumentException, IllegalAccessException
	{
		return f_SeasonsConfig_changeWeatherFrequency.getBoolean(config);
	}
	
	public boolean isDimensionWhitelisted(int dimension) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (boolean) m_SeasonsConfig_isDimensionWhitelisted.invoke(null, dimension);
	}
	
	public Map<ResourceLocation, Object> getBiomeDataMap() throws IllegalArgumentException, IllegalAccessException
	{
		return (Map<ResourceLocation, Object>) f_BiomeConfig_biomeDataMap.get(null);
	}
	
	public Map<ResourceLocation, Boolean> createBiomeSeasonalEffects() throws IllegalArgumentException, IllegalAccessException
	{
		Map<ResourceLocation, Boolean> result = new ConcurrentHashMap<ResourceLocation, Boolean>();
		
		for(Map.Entry<ResourceLocation, Object> entry : getBiomeDataMap().entrySet())
		{
			result.put(entry.getKey(), (boolean) f_BiomeData_enableSeasonalEffects.get(entry.getValue()));
		}
		
		return result;
	}
	
	public Map<ResourceLocation, Boolean> createBiomeTropicalSeasons() throws IllegalArgumentException, IllegalAccessException
	{
		Map<ResourceLocation, Boolean> result = new ConcurrentHashMap<ResourceLocation, Boolean>();
		
		for(Map.Entry<ResourceLocation, Object> entry : getBiomeDataMap().entrySet())
		{
			result.put(entry.getKey(), (boolean) f_BiomeData_useTropicalSeasons.get(entry.getValue()));
		}
		
		return result;
	}
	
	public void turnIntoWater(Block blockIce, World world, BlockPos pos) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		m_BlockIce_turnIntoWater.invoke(blockIce, world, pos);
	}
	
	public SubSeason getSubSeason(Object iSeasonState) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		switch(((Enum)m_ISeasonState_getSubSeason.invoke(iSeasonState)).name())
		{
			case "EARLY_AUTUMN": 	return SubSeason.EARLY_AUTUMN;
			case "EARLY_SPRING": 	return SubSeason.EARLY_SPRING;
			case "EARLY_SUMMER": 	return SubSeason.EARLY_SUMMER;
			case "EARLY_WINTER": 	return SubSeason.EARLY_WINTER;
			case "LATE_AUTUMN": 	return SubSeason.LATE_AUTUMN;
			case "LATE_SPRING": 	return SubSeason.LATE_SPRING;
			case "LATE_SUMMER":  	return SubSeason.LATE_SUMMER;
			case "LATE_WINTER":  	return SubSeason.LATE_WINTER;
			case "MID_AUTUMN":  	return SubSeason.MID_AUTUMN;
			case "MID_SPRING":  	return SubSeason.MID_SPRING;
			case "MID_SUMMER":  	return SubSeason.MID_SUMMER;
			case "MID_WINTER":  	return SubSeason.MID_WINTER;
			default: return null;
		}
	}
	
	public static enum SubSeason
	{
		EARLY_AUTUMN(Season.AUTUMN),
		EARLY_SPRING(Season.SPRING),
		EARLY_SUMMER(Season.SUMMER),
		EARLY_WINTER(Season.WINTER),
		LATE_AUTUMN(Season.AUTUMN),
		LATE_SPRING(Season.SPRING),
		LATE_SUMMER(Season.SUMMER),
		LATE_WINTER(Season.WINTER),
		MID_AUTUMN(Season.AUTUMN),
		MID_SPRING(Season.SPRING),
		MID_SUMMER(Season.SUMMER),
		MID_WINTER(Season.WINTER);
		
		private Season season;
		
		private SubSeason(Season season)
		{
			this.season = season;
		}
		
		public Season getSeason()
		{
			return this.season;
		}
	}
	
	public static enum Season
	{
		WINTER,
		SPRING,
		SUMMER,
		AUTUMN
	}
}
