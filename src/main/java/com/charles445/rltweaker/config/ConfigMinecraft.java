package com.charles445.rltweaker.config;

import net.minecraftforge.common.config.Config;

public class ConfigMinecraft
{
	@Config.Comment("Debug mode, sends messages to all players and otherwise spams, do not enable unless you are privately testing!")
	@Config.Name("Debug Mode")
	public boolean debug = false;
	
	@Config.Comment("Aggressively checks for invalid living entity movement and attempts to fix it")
	@Config.Name("Motion Checker")
	public boolean motionChecker = true;
	
	@Config.Comment("Speed cap for the motion checker, living entities are not allowed to move faster than this")
	@Config.Name("Motion Checker Speed Cap")
	@Config.RangeDouble(min=1.0D)
	public double motionCheckerSpeedCap = 48.0D;
	
	@Config.Comment("Synchronizes arrows with players more aggressively")
	@Config.Name("Player Arrow Sync")
	public boolean playerArrowSync = true;
}
