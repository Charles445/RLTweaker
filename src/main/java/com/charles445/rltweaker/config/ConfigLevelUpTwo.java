package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;

import net.minecraftforge.common.config.Config;

public class ConfigLevelUpTwo
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean enabled = true;
	
	@Config.Comment("Whether pets can use skills")
	@Config.Name("Pets Use Skills")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("false")
	public boolean petsUseSkills = true;
	
	@Config.Comment("Overhauls the stealth mechanic")
	@Config.Name("Stealth Overhaul")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean stealthOverhaul = false;
	
	@Config.Comment("Should stealth overhaul apply to Lycanites Mobs. Only does anything if Stealth Overhaul is enabled")
	@Config.Name("Stealth Overhaul Lycanites")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean stealthOverhaulLycanites = true;
	
	@Config.Comment("Base distance mobs can see you at while sneaking for stealth calculations. Only applies if stealth level is over zero.")
	@Config.Name("Stealth Overhaul Base Distance")
	@RLConfig.ImprovementsOnly("16.0")
	@RLConfig.RLCraftTwoEightTwo("16.0")
	@RLConfig.RLCraftTwoNine("16.0")
	public double stealthOverhaulBaseDistance = 16.0d;
	
	@Config.Comment("How much closer in blocks mobs need to be to see a sneaking player per stealth level.")
	@Config.Name("Stealth Overhaul Distance Per Level")
	@RLConfig.ImprovementsOnly("0.8")
	@RLConfig.RLCraftTwoEightTwo("0.8")
	@RLConfig.RLCraftTwoNine("0.8")
	public double stealthOverhaulDistancePerLevel = 0.8d;
}
