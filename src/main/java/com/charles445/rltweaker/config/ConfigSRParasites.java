package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;

import net.minecraftforge.common.config.Config;

public class ConfigSRParasites
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean enabled = true;
	
	@Config.Comment("Whether parasites should prevent players from sleeping")
	@Config.Name("Parasites Sleep Prevention")
	@Config.RequiresMcRestart
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("false")
	public boolean parasitesSleepPrevention = true;
	
	@Config.Comment("Forcefully remove parasites from certain dimensions")
	@Config.Name("Parasites Dimension Blacklist Enabled")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean parasitesDimensionBlacklistEnabled = false;
	
	@Config.Comment("Dimension blacklist for parasites")
	@Config.Name("Parasites Dimension Blacklist")
	@RLConfig.ImprovementsOnly("")
	@RLConfig.RLCraftTwoEightTwo("")
	@RLConfig.RLCraftTwoNine("0|1|-1")
	public int[] parasitesDimensionBlacklist = new int[0];
	
	@Config.Comment("Attempt to fix biomass crash")
	@Config.Name("Parasites Biomass Crash Fix")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean parasitesBiomassCrashFix = true;
}
