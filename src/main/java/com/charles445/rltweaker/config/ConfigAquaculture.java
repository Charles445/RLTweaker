package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;

import net.minecraftforge.common.config.Config;

public class ConfigAquaculture
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	@RLConfig.ImprovementsOnly("true")
	public boolean enabled = true;
	
	@Config.Comment("Fix Neptunes Bounty loot to properly drop neptunium bars")
	@Config.Name("Fix Neptunes Bounty Loot")
	@Config.RequiresMcRestart
	public boolean fixNeptunesBounty = true;
	
	@Config.Comment("Fix modded biomes causing other biomes to become freshwater")
	@Config.Name("Fix Freshwater Bug")
	@Config.RequiresMcRestart
	public boolean fixFreshwaterBug = true;
}
