package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;

import net.minecraftforge.common.config.Config;

public class ConfigBetterSurvival
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean enabled = true;
	
	@Config.Comment("Enables blacklist for blocks with the tunneling enchantment")
	@Config.Name("Tunneling Blacklist Enabled")
	@Config.RequiresMcRestart
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean tunnelingBlacklistEnabled = false;
	
	@Config.Comment("Whether the tunneling blacklist is a whitelist. Acting as a whitelist may cause buggy behavior. Use with caution.")
	@Config.Name("Tunneling Blacklist Is Whitelist")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("false")
	public boolean tunnelingBlacklistIsWhitelist = false;
	
	@Config.Comment("Blocks that are blacklisted from interacting with the tunneling enchantment")
	@Config.Name("Tunneling Blacklist")
	@RLConfig.ImprovementsOnly("minecraft:chest")
	@RLConfig.RLCraftTwoEightTwo("minecraft:chest")
	@RLConfig.RLCraftTwoNine("minecraft:chest")
	public String[] tunnelingBlacklist = {"minecraft:chest"};
}
