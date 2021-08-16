package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;

import net.minecraftforge.common.config.Config;

public class ConfigRecurrent
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean enabled = true;
	
	@Config.Comment("Allows for some recurrent complex event configuration")
	@Config.Name("Manage Recurrent Complex Events")
	@Config.RequiresMcRestart
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean manageRCForgeEventHandler = true;
	
	@Config.Comment("Generates structures one chunk at a time. Vanilla Recurrent Complex is true")
	@Config.Name("Generate Structures Partially")
	@Config.RequiresMcRestart
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("false")
	public boolean generatePartially = false;
	
	@Config.Comment("Cleans the reccomplex-structuredata.dat regularly. Vanilla Recurrent Complex is false")
	@Config.Name("Clean Structure Data")
	@Config.RequiresMcRestart
	@RLConfig.ImprovementsOnly("true")	//Lets be real this is why people are getting this mod
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean cleanStructureData = true;
	
	@Config.Comment("How many structures need to generate before the dat is cleaned")
	@Config.Name("Clean Structure Threshold")
	@Config.RangeInt(min=1)
	@RLConfig.ImprovementsOnly("100")
	@RLConfig.RLCraftTwoEightTwo("100")
	@RLConfig.RLCraftTwoNine("100")
	public int cleanStructureThreshold = 100;
}

