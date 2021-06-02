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
	
	@Config.Comment("Synchronizes dismounts with players more aggressively")
	@Config.Name("Player Dismount Sync")
	public boolean playerDismountSync = true;
	
	@Config.Comment("Synchronizes arrows with players more aggressively")
	@Config.Name("Player Arrow Sync")
	public boolean playerArrowSync = true;
	
	@Config.Comment("Removes some blacksmith chest loot to match TAN")
	@Config.Name("Blacksmith Chest Tweak")
	public boolean blacksmithChestTweak = true;
	
	@Config.Comment("In Minecraft 1.2.5, players who get knocked back have their camera tilted in the direction of the attack.")
	@Config.Name("Damage Tilt Effect")
	public boolean damageTilt = true;
	
	@Config.Comment("Requires lessCollisions patch, change that config value instead, don't change this unless you are benchmarking")
	@Config.Name("Less Collisions")
	public boolean lessCollisions = true;
	
	@Config.Comment("Replace thrown witch potions with configured potions")
	@Config.Name("Witch Potion Replacements")
	public boolean witchPotionReplacements = false;
	
	@Config.Comment("Replace thrown witch harming potions with configured potions")
	@Config.Name("Witch Potion Replacements - Harming")
	public String[] witchHarmingReplacements = {"minecraft:harming"};
	
	@Config.Comment("Replace thrown witch slowness potions with configured potions")
	@Config.Name("Witch Potion Replacements - Slowness")
	public String[] witchSlownessReplacements = {"minecraft:slowness"};
	
	@Config.Comment("Replace thrown witch poison potions with configured potions")
	@Config.Name("Witch Potion Replacements - Poison")
	public String[] witchPoisonReplacements = {"minecraft:poison"};
	
	@Config.Comment("Replace thrown witch weakness potions with configured potions")
	@Config.Name("Witch Potion Replacements - Weakness")
	public String[] witchWeaknessReplacements = {"minecraft:weakness"};
}
