package com.charles445.rltweaker.config;

import net.minecraftforge.common.config.Config;

public class ConfigSME
{
	@Config.Comment("Arc Slash fixes")
	@Config.Name("Manage Arc Slash")
	@Config.RequiresMcRestart
	public boolean manageArcSlash = true;
	
	@Config.Comment("Empowered Defence fixes")
	@Config.Name("Manage Empowered Defence")
	@Config.RequiresMcRestart
	public boolean manageEmpoweredDefence = true;
	
	@Config.Comment("Evasion fixes")
	@Config.Name("Manage Evasion")
	@Config.RequiresMcRestart
	public boolean manageEvasion = true;
	
	@Config.Comment("Freezing fixes")
	@Config.Name("Manage Freezing")
	@Config.RequiresMcRestart
	public boolean manageFreezing = true;
	
	@Config.Comment("Parry fixes")
	@Config.Name("Manage Parry")
	@Config.RequiresMcRestart
	public boolean manageParry = true;
	
	@Config.Comment("Unreasonable fixes")
	@Config.Name("Manage Unreasonable")
	@Config.RequiresMcRestart
	public boolean manageUnreasonable = true;
	
	@Config.Comment("Upgraded Potentials fixes")
	@Config.Name("Manage Upgraded Potentials")
	public boolean manageUpgradedPotentials = true;
}
