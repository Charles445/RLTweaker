package com.charles445.rltweaker.config;

import net.minecraftforge.common.config.Config;

public class ConfigPotionCoreClient
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.Comment("Fix for Magic Shielding HUD Color affecting the hunger bar")
	@Config.Name("MagicShieldingHUDFix")
	@Config.RequiresMcRestart
	public boolean magicShieldingHUDFix = true;
}
