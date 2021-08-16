package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;

import net.minecraftforge.common.config.Config;

public class ConfigLostCities
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean enabled = true;
	
	@Config.Comment("Whether the chunk generation cache should be cleaned regularly to avoid memory leaks.")
	@Config.Name("GenerationCacheCleanupEnabled")
	@RLConfig.ImprovementsOnly("false") 	//TODO re-evaluate after sufficient testing
	@RLConfig.RLCraftTwoEightTwo("false")	//TODO re-evaluate after sufficient testing
	@RLConfig.RLCraftTwoNine("true")
	public boolean generationCacheCleanupEnabled = false;
	
	@Config.Comment("How many entries the caches are allowed to get before they are cleared. Default is recommended.")
	@Config.Name("GenerationCacheMaxCount")
	@Config.RangeInt(min=0)
	@RLConfig.ImprovementsOnly("200")
	@RLConfig.RLCraftTwoEightTwo("200")
	@RLConfig.RLCraftTwoNine("200")
	public int generationCacheMaxCount = 200;
}
