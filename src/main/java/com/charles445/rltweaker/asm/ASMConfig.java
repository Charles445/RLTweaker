package com.charles445.rltweaker.asm;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.nio.file.Paths;

//FoamFix loads these three classes... Seems to work fine?

public class ASMConfig
{
	private final Configuration config;
	
	public ASMConfig(String modName) {
		System.out.println("ConfigSetup is running.");
		
		//Look for the RLTweaker config
		Path path = getConfig(modName);
		
		//TODO the boolean here is "Case Sensitive Categories"
		//Which one is appropriate?
		config = new Configuration(path.toFile(), true);
	}
	
	//Example map strings
	
	//general.server.roguelike dungeons.ENABLED
	//general.server.battle towers.Change Tower Explosion Owner
	
	public boolean getBoolean(String id, boolean _default)
	{
		Property prop = getProperty(id);
		if(prop == null)
		{
			//Warn
			// TODO: shhhh, let's not be so harsh and just assume default
			System.out.println("WARNING: unknown config request: "+id);
			return _default;
		}

		return prop.getBoolean(_default);
	}
	
	@Nullable
	public <T> Property getProperty(String s)
	{
		int i = s.lastIndexOf(Configuration.CATEGORY_SPLITTER);
		if (i < 0) {
			System.out.println("WARNING: malformed config key: " + s);
		}
		
		return this.config.getCategory(s.substring(0, i)).get(s.substring(i + 1));
	}
	
	private static Path getConfig(String modName)
	{
		return Paths.get("config").resolve(modName + ".cfg").toAbsolutePath();
	}
}
