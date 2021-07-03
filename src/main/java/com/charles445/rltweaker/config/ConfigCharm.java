package com.charles445.rltweaker.config;

import net.minecraftforge.common.config.Config;

public class ConfigCharm
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.Comment("Fix enchantments appearing on incompatible items")
	@Config.Name("Fix Incorrect Item Enchantments")
	@Config.RequiresMcRestart
	public boolean fixIncorrectItemEnchantments = true;
}
