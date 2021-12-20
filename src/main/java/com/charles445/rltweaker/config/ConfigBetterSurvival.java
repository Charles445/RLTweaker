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
	
	@Config.Comment("Whether tunneling should fail if the center block break event fails. By default, Better Survival has this false")
	@Config.Name("Tunneling Cancelable")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean tunnelingCancelable = true;
	
	@Config.Comment("Speed multiplier for the Range enchantment. Default is 2")
	@Config.Name("Range Speed Multiplier")
	@RLConfig.ImprovementsOnly("2.0")
	@RLConfig.RLCraftTwoEightTwo("2.0")
	@RLConfig.RLCraftTwoNine("2.0")
	public double rangeSpeedMultiplier = 2.0d;
	
	@Config.Comment("Whether blindness affects mobs at all")
	@Config.Name("Mob Blindness")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean mobBlindness = true;
	
	@Config.Comment("How strong the reduced follow range for mobs is with blindness")
	@Config.Name("Mob Blindness Percentage")
	@RLConfig.ImprovementsOnly("80")
	@RLConfig.RLCraftTwoEightTwo("80")
	@RLConfig.RLCraftTwoNine("80")
	@Config.RangeDouble(min=0.0d, max=100.0d)
	public double mobBlindnessPercentage = 80.0d;
	
	@Config.Comment("Blacklist for mob blindness")
	@Config.Name("Mob Blindness Blacklist")
	@RLConfig.ImprovementsOnly("examplemod:mob")
	@RLConfig.RLCraftTwoEightTwo("examplemod:mob")
	@RLConfig.RLCraftTwoNine("examplemod:mob")
	public String[] mobBlindnessBlacklist = new String[]{"examplemod:mob"};
}
