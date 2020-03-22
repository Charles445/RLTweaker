package com.charles445.rltweaker.config;

import net.minecraftforge.common.config.Config;

public class ConfigRoguelike
{
	@Config.Comment("Prevent the Mining Fatigue crash")
	@Config.Name("Prevent Fatigue Crash")
	public boolean preventFatigueCrash = true;
}
