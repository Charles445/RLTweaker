package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;

import net.minecraftforge.common.config.Config;

public class ConfigGooglyEyesClient
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean enabled = true;
	
	@Config.Comment("Entity blacklist for googly eyes that uses the registry name instead")
	@Config.Name("Entity Blacklist")
	@RLConfig.ImprovementsOnly("examplemod:mob")
	@RLConfig.RLCraftTwoEightTwo("examplemod:mob")
	@RLConfig.RLCraftTwoNine("examplemod:mob")
	public String[] entityBlacklist = new String[]{"examplemod:mob"};
}
