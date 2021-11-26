package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;

import net.minecraftforge.common.config.Config;

public class ConfigQuark
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean enabled = true;
	
	@Config.Comment("Instead of using an anvil, ancient tomes must be right clicked in the offhand to be combined with an item in the mainhand")
	@Config.Name("Ancient Tomes Alternate Behavior")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean ancientTomesAlternateBehavior = false;
}
