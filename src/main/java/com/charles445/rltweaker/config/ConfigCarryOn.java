package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;

import net.minecraftforge.common.config.Config;

public class ConfigCarryOn
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean enabled = true;
	
	@Config.Comment("Prevent creating glitched Quark chests with Carry On")
	@Config.Name("Quark Chest Fix")
	@Config.RequiresMcRestart
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean quarkChestFix = true;
	
	@Config.Comment("More safety checks for dropping tile entities in the world")
	@Config.Name("Tile Entity Drop Safety Check")
	@Config.RequiresMcRestart
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean tileEntityDropSafetyCheck = true;
}
