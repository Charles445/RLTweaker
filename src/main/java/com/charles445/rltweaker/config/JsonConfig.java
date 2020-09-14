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
			lessCollisions.clear();
			
			//Np combat allies or offensive tools
			//No mountables, except for pigs.
			
			if(Loader.isModLoaded(ModNames.LYCANITESMOBS))
			{
				lessCollisions.put("com.lycanitesmobs.core.entity.EntityItemCustom", 2.0d);
			}
			
			//Minecraft
			
			lessCollisions.put("net.minecraft.entity.item.EntityArmorStand", 2.0d);
			lessCollisions.put("net.minecraft.entity.item.EntityItem", 2.0d);
			lessCollisions.put("net.minecraft.entity.item.EntityItemFrame", 2.0d);
			lessCollisions.put("net.minecraft.entity.item.EntityPainting", 2.0d);
			lessCollisions.put("net.minecraft.entity.item.EntityXPOrb", 2.0d);
			lessCollisions.put("net.minecraft.entity.monster.EntityBlaze", 2.0d);
			lessCollisions.put("net.minecraft.entity.monster.EntityCaveSpider", 2.0d);
			lessCollisions.put("net.minecraft.entity.monster.EntityCreeper", 2.0d);
			lessCollisions.put("net.minecraft.entity.monster.EntityElderGuardian", 2.0d);
			lessCollisions.put("net.minecraft.entity.monster.EntityEnderman", 2.0d);
			lessCollisions.put("net.minecraft.entity.monster.EntityEndermite", 2.0d);
			lessCollisions.put("net.minecraft.entity.monster.EntityEvoker", 2.0d);
			lessCollisions.put("net.minecraft.entity.monster.EntityGhast", 2.0d);
			lessCollisions.put("net.minecraft.entity.monster.EntityGuardian", 2.0d);
			lessCollisions.put("net.minecraft.entity.monster.EntityHusk", 2.0d);
			lessCollisions.put("net.minecraft.entity.monster.EntityIllusionIllager", 2.0d);
			lessCollisions.put("net.minecraft.entity.monster.EntityMagmaCube", 2.0d);
			lessCollisions.put("net.minecraft.entity.monster.EntityPigZombie", 2.0d);
			lessCollisions.put("net.minecraft.entity.monster.EntityPolarBear", 2.0d);
			lessCollisions.put("net.minecraft.entity.monster.EntityShulker", 2.0d);
			lessCollisions.put("net.minecraft.entity.monster.EntitySilverfish", 2.0d);
			lessCollisions.put("net.minecraft.entity.monster.EntitySkeleton", 2.0d);
			lessCollisions.put("net.minecraft.entity.monster.EntitySlime", 2.0d);
			lessCollisions.put("net.minecraft.entity.monster.EntitySpider", 2.0d);
			lessCollisions.put("net.minecraft.entity.monster.EntityStray", 2.0d);
			lessCollisions.put("net.minecraft.entity.monster.EntityVex", 2.0d);
			lessCollisions.put("net.minecraft.entity.monster.EntityVindicator", 2.0d);
			lessCollisions.put("net.minecraft.entity.monster.EntityWitch", 2.0d);
			lessCollisions.put("net.minecraft.entity.monster.EntityWitherSkeleton", 2.0d);
			lessCollisions.put("net.minecraft.entity.monster.EntityZombie", 2.0d);
			lessCollisions.put("net.minecraft.entity.monster.EntityZombieVillager", 2.0d);

			lessCollisions.put("net.minecraft.entity.passive.EntityBat", 2.0d);
			lessCollisions.put("net.minecraft.entity.passive.EntityChicken", 2.0d);
			lessCollisions.put("net.minecraft.entity.passive.EntityCow", 2.0d);
			lessCollisions.put("net.minecraft.entity.passive.EntityMooshroom", 2.0d);
			lessCollisions.put("net.minecraft.entity.passive.EntityOcelot", 2.0d);
			lessCollisions.put("net.minecraft.entity.passive.EntityParrot", 2.0d);
			lessCollisions.put("net.minecraft.entity.passive.EntityPig", 2.0d); //Take a pig ride through asmodeus
			lessCollisions.put("net.minecraft.entity.passive.EntityRabbit", 2.0d);
			lessCollisions.put("net.minecraft.entity.passive.EntitySheep", 2.0d);
			lessCollisions.put("net.minecraft.entity.passive.EntitySquid", 2.0d);
			lessCollisions.put("net.minecraft.entity.passive.EntityVillager", 2.0d);
			
			Map<String, Double> lcJson = processJson(JsonFileName.lessCollisions, lessCollisions, true);
			if(lcJson!=null)
			{
				try
				{
					lessCollisions.putAll(lcJson);
					manuallyWriteToJson(JsonFileName.lessCollisions, lessCollisions);
				}
				catch(Exception e)
				{
					RLTweaker.logger.error("Failed to merge write lessCollisions!");
					ErrorUtil.logSilent("JSON Merge Write LessCollisions");
				}
			}
			
			if(lessCollisions == null)
				lessCollisions = new HashMap<>();
			
			CollisionUtil.instance.addToStringReference(lessCollisions);
		}
		
		if(Loader.isModLoaded(ModNames.RESKILLABLE) && ModConfig.server.reskillable.enabled && ModConfig.server.reskillable.customTransmutation)
		{
			reskillableTransmutation.clear();
			reskillableTransmutation.put("minecraft:stick", Arrays.asList(new JsonDoubleBlockState[]{JsonDoubleBlockState.AIR}));
			
			reskillableTransmutation = processJson(JsonFileName.reskillableTransmutation, reskillableTransmutation, false);
			if(reskillableTransmutation!=null)
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
