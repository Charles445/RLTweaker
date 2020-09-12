package com.charles445.rltweaker.config;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.json.JsonDoubleBlockState;
import com.charles445.rltweaker.config.json.JsonFileName;
import com.charles445.rltweaker.config.json.JsonTypeToken;
import com.charles445.rltweaker.handler.ReskillableHandler;
import com.charles445.rltweaker.util.CollisionUtil;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ModNames;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraftforge.fml.common.Loader;

public class JsonConfig
{
	public static List<String> jsonErrors = new ArrayList<String>();
	
	public static Map<String, Double> lessCollisions = new HashMap<>();
	public static Map<String, List<JsonDoubleBlockState>> reskillableTransmutation = new HashMap<>();
	
	public static void init()
	{
		if(ModConfig.patches.lessCollisions)
		{
			JsonConfig.lessCollisions.clear();
			JsonConfig.lessCollisions.put("net.minecraft.entity.item.EntityItem", 2.0d);
			JsonConfig.lessCollisions.put("net.minecraft.entity.passive.EntityChicken", 2.0d);
			JsonConfig.lessCollisions.put("net.minecraft.entity.passive.EntitySquid", 2.0d);
			
			JsonConfig.lessCollisions = processJson(JsonFileName.lessCollisions, JsonConfig.lessCollisions, false);
			
			if(JsonConfig.lessCollisions == null)
				JsonConfig.lessCollisions = new HashMap<>();
			
			CollisionUtil.instance.addToStringReference(lessCollisions);
		}
		
		if(Loader.isModLoaded(ModNames.RESKILLABLE) && ModConfig.server.reskillable.enabled && ModConfig.server.reskillable.customTransmutation)
		{
			JsonConfig.reskillableTransmutation.clear();
			JsonConfig.reskillableTransmutation.put("minecraft:stick", Arrays.asList(new JsonDoubleBlockState[]{JsonDoubleBlockState.AIR}));
			
			JsonConfig.reskillableTransmutation = processJson(JsonFileName.reskillableTransmutation, JsonConfig.reskillableTransmutation, false);
			if(JsonConfig.reskillableTransmutation!=null)
			{
				Object reskillableHandler = RLTweaker.handlers.get(ModNames.RESKILLABLE);
				if(reskillableHandler instanceof ReskillableHandler)
				{
					((ReskillableHandler)reskillableHandler).registerTransmutations();
				}
			}
		}
		
	}
	
	/** Nullable when forMerging is true */
	@Nullable
	public static <T> T processJson(JsonFileName jfn, final T container, boolean forMerging)
	{
		try
		{
			return processUncaughtJson(jfn, container, forMerging);
		}
		catch(Exception e)
		{
			RLTweaker.logger.error("Error managing JSON File: "+jfn.get(), e);
			jsonErrors.add("config/rltweaker/"+jfn.get()+" failed to load!");
			ErrorUtil.logSilent("JSON Error: "+jfn.get());
			if(forMerging)
			{
				return null;
			}
			else
			{
				return container;
			}
		}
	}
	
	@Nullable
	public static <T> T processUncaughtJson(JsonFileName jfn, final T container, boolean forMerging) throws Exception
	{
		String jsonFileName = jfn.get();
		Type type = JsonTypeToken.get(jfn);
		
		File jsonFile = new File(RLTweaker.jsonDirectory,jsonFileName);
		if(jsonFile.exists())
		{
			Gson gson = buildNewGson();
			//Read
			return (T) gson.fromJson(new FileReader(jsonFile), type);
		}
		else
		{
			Gson gson = buildNewGson();
			//Write
			
			FileUtils.write(jsonFile,gson.toJson(container, type),(String)null);
			if(forMerging)
			{
				return null;
			}
			else
			{
				return container;
			}
		}
	}
	
	private static <T> void manuallyWriteToJson(JsonFileName jfn, final T container) throws Exception
	{
		String jsonFileName = jfn.get();
		Type type = JsonTypeToken.get(jfn);
		
		Gson gson = buildNewGson();
		File jsonFile = new File(RLTweaker.jsonDirectory,jsonFileName);
		FileUtils.write(jsonFile, gson.toJson(container, type),(String)null);
	}
	
	private static Gson buildNewGson()
	{
		//Pretty printing, and private modifiers are not serialized
		return new GsonBuilder().setPrettyPrinting().excludeFieldsWithModifiers(Modifier.PRIVATE, Modifier.STATIC).create();
	}
}
