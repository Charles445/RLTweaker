package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;

import net.minecraftforge.common.config.Config;

public class PatchConfig
{
	//Do NOT provide a Config.Name!
	//DO NOT!
	
	//Fields are to be appended with
	//general.patches.
	
	//general.patches.ENABLED
	@Config.RequiresMcRestart
	@Config.Comment("Master switch for the coremod")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean ENABLED = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Makes the particle queue threaded. Fixes concurrency issue with logical server creating physical client particles.")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean particleThreading = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Makes some entities stop checking for large entity collisions")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean lessCollisions = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Allows for mounted combat with BetterCombat")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("false") //Tragic, really. It's client side so we don't want anyone getting confused.
	@RLConfig.RLCraftTwoNine("true")
	public boolean betterCombatMountFix = false;
	
	@Config.RequiresMcRestart
	@Config.Comment("Fixes RealBench dupe bug")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean realBenchDupeBugFix = true;
}
