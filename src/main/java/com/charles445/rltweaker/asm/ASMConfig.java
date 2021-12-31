package com.charles445.rltweaker.asm;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.charles445.rltweaker.asm.util.ASMLogger;

//FoamFix loads these three classes... Seems to work fine?
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ASMConfig
{
	private static boolean setup = false;
	
	@Nullable
	private static Configuration config = null;
	
	@Nonnull
	public static Map<String, Property> configMap = new ConcurrentHashMap<>();
	
	//Example map strings
	
	//general.server.roguelike dungeons.ENABLED
	//general.server.battle towers.Change Tower Explosion Owner
	
	public static boolean getBoolean(String id, boolean _default)
	{
		if(hasConfig())
		{
			Property prop = getProperty(id);
			if(prop == null)
			{
				//Warn
				ASMLogger.warn("WARNING: unknown config request: "+id);
				return _default;
			}
			
			return prop.getBoolean(_default);
			
		}

		return _default;
	}
	
	@Nullable
	public static Property getProperty(String s)
	{
		return configMap.get(s);
	}
	
	public static boolean hasConfig()
	{
		return configMap.size() > 0;
	}
	
	public static void setup()
	{
		//Run once
		if(setup)
			return;
		
		setup = true;
		
		ASMLogger.info("ConfigSetup is running.");
		
		//Look for the RLTweaker config
		Path path = getConfig("rltweaker");
		
		//TODO the boolean here is "Case Sensitive Categories"
		//Which one is appropriate?
		config = new Configuration(path.toFile(), true);
		
		processConfiguration();
	}
	
	private static void processConfiguration()
	{
		if(config != null)
		{
			//logger.info("Processing configuration");
			
			/*
			logger.info"CATEGORIES");
			for(String s : config.getCategoryNames())
			{
				logger.info(s);
			}
			*/
			
			//logger.info("Populating configMap");
			
			for(String s : config.getCategoryNames())
			{
				loadCategory(config.getCategory(s), s);
			}
			
			/*
			logger.info("Dumping configuration keys");
			
			for(String s : configMap.keySet())
			{
				logger.info(s);
			}
			*/
			
			//Categories are all loaded
		}
		else
		{
			ASMLogger.info("Config does not exist, patcher will assume defaults");
		}
	}
	
	private static void loadCategory(ConfigCategory category, String header)
	{
		for(Map.Entry<String, Property> entry : category.getValues().entrySet())
		{
			String key = header + "." + entry.getKey();
			if(configMap.containsKey(key))
			{
				ASMLogger.warn("WARNING: Duplicate key for: "+key);
			}
			
			configMap.put(key, entry.getValue());
		}
		
		//No need to run through child categories, "getCategoryNames" already does this
		//Optionally could do it manually by skipping anything with a dot in it but I really do not care at all
	}
	
	
	private static Path getConfig(String modName)
	{
		//TODO is File.separator appropriate here?
		return Paths.get("config"+File.separator+modName+".cfg").toAbsolutePath();
	}
}
