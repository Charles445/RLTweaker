package com.charles445.rltweaker.config;

import net.minecraftforge.common.config.Config;

public class ConfigIceAndFire
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.Comment("Prevent statues from picking up items (and other mob griefing events)")
	@Config.Name("Statue Mob Griefing Fix")
	public boolean statueMobGriefingFix = true;
	
	@Config.Comment("Prevent statues from saving some data they should not")
	@Config.Name("Statue Data Fixes")
	public boolean statueDataFixes = true;
}
