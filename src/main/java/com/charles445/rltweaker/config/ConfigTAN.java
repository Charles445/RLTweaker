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
	
	@Config.Comment("Prevents Tough As Nails from creating an extra attack entity event. Does nothing if ISeeDragons is in the pack.")
	@Config.Name("Fix Extra Attack Bug")
	@Config.RequiresMcRestart
	@RLConfig.ImprovementsOnly("false") //Packs are balanced around this glitch by default.
	@RLConfig.RLCraftTwoEightTwo("false") //Handled by ISeeDragons
	@RLConfig.RLCraftTwoNine("false") //Handled by ISeeDragons
	public boolean fixExtraAttackBug = false;
	
	@Config.Comment("Fixes TAN bug where holding spacebar after dismounting glitches your jump and drains your thirst")
	@Config.Name("Fix Dismount Thirst Drain Bug")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("false") //Handled by ISeeDragons
	@RLConfig.RLCraftTwoNine("false")
	public boolean fixDismountThirstDrainBug = true;
	
	
}
