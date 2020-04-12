package com.charles445.rltweaker.config;

import net.minecraftforge.common.config.Config;

public class ConfigRuins
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.Comment("Clean up the Ruins Chunk Logger automatically")
	@Config.Name("Chunk Logger Cleanup")
	public boolean cleanupChunkLogger = true;
	
	@Config.Comment("Threshold to clean up the Ruins Chunk Logger, in chunks")
	@Config.Name("Chunk Logger Threshold")
	@Config.RangeInt(min=0)
	public int chunkThreshold = 20000;
}
