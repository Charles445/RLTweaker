package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;

import net.minecraftforge.common.config.Config;

public class PatchConfig
{
	//Do NOT provide a Config.Name!
	//DO NOT!
	
	//Fields are to be appended with
	//general.patches.
	
	//general.patches.ENABLED
	@Config.RequiresMcRestart
	@Config.Comment("Master switch for the coremod")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean ENABLED = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Makes the particle queue threaded. Fixes concurrency issue with logical server creating physical client particles.")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean particleThreading = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Makes some entities stop checking for large entity collisions. Not needed without a max entity radius changing mod.")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean lessCollisions = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Allows for mounted combat with BetterCombat")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("false") //Tragic, really. It's client side so we don't want anyone getting confused.
	@RLConfig.RLCraftTwoNine("true")
	public boolean betterCombatMountFix = false;
	
	@Config.RequiresMcRestart
	@Config.Comment("Fixes RealBench dupe bug")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean realBenchDupeBugFix = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Fixes Myrmex Queen hive spam")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean iafFixMyrmexQueenHiveSpam = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Fixes Lycanites Pet Dupe in older LycanitesMobs versions than 2.0.8.0, may cause crashes in newer versions.")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("false")
	public boolean lycanitesPetDupeFix = false;
	
	@Config.RequiresMcRestart
	@Config.Comment("Fixes mobs having trouble pathing through open doors")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean doorPathfindingFix = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Reduces search size for finding some entities like players and items. Not needed without a max entity radius changing mod. Helps with Quark Monster Box lag.")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean reducedSearchSize = false;
	
	@Config.RequiresMcRestart
	@Config.Comment("Enables config option to tweak broadcasted sounds.")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean patchBroadcastSounds = false;
	
	@Config.RequiresMcRestart
	@Config.Comment("Enables config option to blacklist enchantments.")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean patchEnchantments = false;
	
	@Config.RequiresMcRestart
	@Config.Comment("Makes the motion checker even more aggressive.")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean aggressiveMotionChecker = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Enables Entity Block Destroy Blacklist")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean patchEntityBlockDestroy = false;
	
	@Config.RequiresMcRestart
	@Config.Comment("Patches a dupe with modded item frames, specifically Quark")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean patchItemFrameDupe = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Enables Entity Push Prevention")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean patchPushReaction = false;
	
	@Config.RequiresMcRestart
	@Config.Comment("Fixes some mod related anvil dupes")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true)")
	public boolean patchAnvilDupe = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Allows for client side overlay text configuration")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean patchOverlayMessage = false;
	
	@Config.RequiresMcRestart
	@Config.Comment("Allows for some hopper tweaks")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean patchHopper = true;
	
	/*
	@Config.RequiresMcRestart
	@Config.Comment("Fixes Myrmex Queen trades")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean iafFixMyrmexQueenTrades = true;
	*/
	/*
	@Config.RequiresMcRestart
	@Config.Comment("Fixes stealth mechanics in older versions of Level Up")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean levelUpOldStealthFix = true;
	*/
}
