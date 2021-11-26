package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;

import net.minecraftforge.common.config.Config;

public class ConfigPotionCoreClient
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean enabled = true;
	
	@Config.Comment("Fix for Magic Shielding HUD Color affecting the hunger bar")
	@Config.Name("Magic Shielding HUD Fix")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean magicShieldingHUDFix = true;
	
	@Config.Comment("Whether to render Potion Core's armor icons on the hud")
	@Config.Name("Render Armor Icons")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean renderArmorIcons = true;
	
	@Config.Comment("Whether to render Potion Core's resistance red armor outlines on the hud")
	@Config.Name("Render Armor Resistance")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean renderArmorResistance = true;
	
	@Config.Comment("Compatibility for Overloaded Armor Bar, rendering magic shielding properly")
	@Config.Name("Overloaded Armor Bar Compatibility")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean overloadedArmorBarCompatibility = true;
	
}
