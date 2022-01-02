package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;

import net.minecraftforge.common.config.Config;

public class ConfigRuins
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean enabled = true;
	
	@Config.Comment("Clean up the Ruins Chunk Logger automatically")
	@Config.Name("Chunk Logger Cleanup")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean cleanupChunkLogger = true;
	
	@Config.Comment("Threshold to clean up the Ruins Chunk Logger, in chunks")
	@Config.Name("Chunk Logger Threshold")
	@Config.RangeInt(min=0)
	@RLConfig.ImprovementsOnly("20000")
	@RLConfig.RLCraftTwoEightTwo("20000")
	@RLConfig.RLCraftTwoNine("20000")
	public int chunkThreshold = 20000;
	
	@Config.Comment("Removes the RUINSTRIGGER tag for custom ruins structures as it is very resource intensive and no custom ruins seem to use it.")
	@Config.Name("Remove RUINSTRIGGER Functionality")
	@Config.RequiresMcRestart
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean removeRUINSTRIGGERFunctionality = true;
}
