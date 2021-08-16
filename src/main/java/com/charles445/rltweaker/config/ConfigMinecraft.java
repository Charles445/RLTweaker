package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;

import net.minecraftforge.common.config.Config;

public class ConfigMinecraft
{
	@Config.Comment("Debug mode, sends messages to all players and otherwise spams, do not enable unless you are privately testing!")
	@Config.Name("Debug Mode")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("false")
	public boolean debug = false;
	
	@Config.Comment("Aggressively checks for invalid living entity movement and attempts to fix it")
	@Config.Name("Motion Checker")
	@RLConfig.ImprovementsOnly("true")	//Very helpful in other packs, honestly, I see plenty of modded mobs getting NaN rotation
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean motionChecker = true;
	
	@Config.Comment("Speed cap for the motion checker, living entities are not allowed to move faster than this")
	@Config.Name("Motion Checker Speed Cap")
	@Config.RangeDouble(min=1.0D)
	@RLConfig.ImprovementsOnly("48.0")
	@RLConfig.RLCraftTwoEightTwo("48.0")
	@RLConfig.RLCraftTwoNine("48.0")
	public double motionCheckerSpeedCap = 48.0D;
	
	@Config.Comment("Synchronizes dismounts with players more aggressively")
	@Config.Name("Player Dismount Sync")
	@RLConfig.ImprovementsOnly("true")	//TODO finish implementing the delayed mount registration to avoid rare compatibility issues in other packs
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean playerDismountSync = true;
	
	@Config.Comment("Synchronizes arrows with players more aggressively")
	@Config.Name("Player Arrow Sync")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean playerArrowSync = true;
	
	@Config.Comment("Removes some blacksmith chest loot to match TAN")
	@Config.Name("Blacksmith Chest Tweak")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("false") //And I did all this work and everything!
	public boolean blacksmithChestTweak = false;
	
	@Config.Comment("In Minecraft 1.2.5, players who get knocked back have their camera tilted in the direction of the attack.")
	@Config.Name("Damage Tilt Effect")
	@RLConfig.ImprovementsOnly("false")	//If people want it they can turn it on, no need to force it in front of them
	@RLConfig.RLCraftTwoEightTwo("true") //Except people who update their tweakers for 2.8.2 servers then heck yeah
	@RLConfig.RLCraftTwoNine("true")
	public boolean damageTilt = true;
	
	@Config.Comment("Requires lessCollisions patch, change that config value instead, don't change this unless you are benchmarking")
	@Config.Name("Less Collisions")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean lessCollisions = true;
	
	@Config.Comment("Replace thrown witch potions with configured potions")
	@Config.Name("Witch Potion Replacements")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean witchPotionReplacements = false;
	
	@Config.Comment("Replace thrown witch harming potions with configured potions")
	@Config.Name("Witch Potion Replacements - Harming")
	@RLConfig.ImprovementsOnly("minecraft:harming")
	@RLConfig.RLCraftTwoEightTwo("minecraft:harming")
	@RLConfig.RLCraftTwoNine("minecraft:harming")
	public String[] witchHarmingReplacements = {"minecraft:harming"};
	
	@Config.Comment("Replace thrown witch slowness potions with configured potions")
	@Config.Name("Witch Potion Replacements - Slowness")
	@RLConfig.ImprovementsOnly("minecraft:slowness")
	@RLConfig.RLCraftTwoEightTwo("minecraft:slowness")
	@RLConfig.RLCraftTwoNine("minecraft:slowness")
	public String[] witchSlownessReplacements = {"minecraft:slowness"};
	
	@Config.Comment("Replace thrown witch poison potions with configured potions")
	@Config.Name("Witch Potion Replacements - Poison")
	@RLConfig.ImprovementsOnly("minecraft:poison")
	@RLConfig.RLCraftTwoEightTwo("minecraft:poison")
	@RLConfig.RLCraftTwoNine("minecraft:poison")
	public String[] witchPoisonReplacements = {"minecraft:poison"};
	
	@Config.Comment("Replace thrown witch weakness potions with configured potions")
	@Config.Name("Witch Potion Replacements - Weakness")
	@RLConfig.ImprovementsOnly("minecraft:weakness")
	@RLConfig.RLCraftTwoEightTwo("minecraft:weakness")
	@RLConfig.RLCraftTwoNine("minecraft:weakness")
	public String[] witchWeaknessReplacements = {"minecraft:weakness"};
}
