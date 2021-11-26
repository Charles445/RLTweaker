package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;

import net.minecraftforge.common.config.Config;

public class ConfigMinecraftClient
{
	@Config.Comment("REQUIRES Patch patchOverlayMessage. Whether overlay text should have a dropshadow")
	@Config.Name("Overlay Text Drop Shadow")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean overlayTextDropShadow = false;
	
	@Config.Comment("REQUIRES Patch patchOverlayMessage. Moves overlay text up by this amount (can be negative to move down)")
	@Config.Name("Overlay Text Offset")
	@RLConfig.ImprovementsOnly("0")
	@RLConfig.RLCraftTwoEightTwo("0")
	@RLConfig.RLCraftTwoNine("0")
	public int overlayTextOffset = 0;
}
