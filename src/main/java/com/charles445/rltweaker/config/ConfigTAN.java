package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;

import net.minecraftforge.common.config.Config;

public class ConfigTAN
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean enabled = true;
	
	@Config.Comment("Fixes thirst drain on teleportation")
	@Config.Name("Teleport Thirst Fix")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean fixTeleportThirst = true;
	
	@Config.Comment("Exhaustion change in a tick for the teleport fix to kick in.")
	@Config.Name("Teleport Thirst Threshold")
	@Config.RangeDouble(min=1.0D, max=40.0D)
	@RLConfig.ImprovementsOnly("8.0")
	@RLConfig.RLCraftTwoEightTwo("8.0")
	@RLConfig.RLCraftTwoNine("8.0")
	public double teleportThirstThreshold = 8.0D;
	
	@Config.Comment("Regularly send players extra Thirst packets")
	@Config.Name("Send Extra Thirst Packets")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean sendExtraThirstPackets = true;
	
	@Config.Comment("How often extra Thirst packets get sent out, in ticks")
	@Config.Name("Thirst Packet Frequency")
	@Config.RangeInt(min=1)
	@RLConfig.ImprovementsOnly("20")
	@RLConfig.RLCraftTwoEightTwo("20")
	@RLConfig.RLCraftTwoNine("20")
	public int extraThirstPacketFrequency = 20;
	
}
