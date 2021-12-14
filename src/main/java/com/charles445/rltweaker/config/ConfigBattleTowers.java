package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;

import net.minecraftforge.common.config.Config;

public class ConfigBattleTowers
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean enabled = true;
	
	@Config.Comment("Make tower explosions belong to nobody, avoids Infernal Mobs issues")
	@Config.Name("Change Tower Explosion Owner")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean towerExplosionNoCredit = false;
	
	@Config.Comment("Golems who are attacked will get angry more consistently, and from farther away")
	@Config.Name("Golem High Aggression")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean golemHighAggression = false;
	
	@Config.Comment("Make golems stop building up speed when dormant")
	@Config.Name("Golem Dormant Speed Fix")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean golemDormantSpeedFix = false;
	
	@Config.Comment("Prevent golems from drowning")
	@Config.Name("Golem Drowning Fix")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean golemDrowningFix = false;
	
	@Config.Comment("Prevent golems from suffocating in walls")
	@Config.Name("Golem Suffocating Fix")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean golemSuffocatingFix = false;
	
	@Config.Comment("Prevent falling block damage for golems")
	@Config.Name("Golem Falling Block Fix")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean golemFallingBlockFix = false;
	
	@Config.Comment("Prevent anvil damage for golems")
	@Config.Name("Golem Anvil Fix")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean golemAnvilFix = false;
	
	@Config.Comment("Prevent golems taking damage from lycanites fluids")
	@Config.Name("Golem Lycanites Fluid Fix")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean golemLycanitesFluidFix = false;
	
	@Config.Comment("Max Golem XZ movement to make using tools like fishing rods harder. Set to a negative number to disable")
	@Config.Name("Golem Speed Cap")
	@RLConfig.ImprovementsOnly("-1.0")
	@RLConfig.RLCraftTwoEightTwo("-1.0")
	@RLConfig.RLCraftTwoNine("0.125d")
	@Config.RangeDouble(min=-1.0d)
	public double golemSpeedCap = -1.0d;
	
	@Config.Comment("Max Golem Y movement (upwards) to make launching him harder. Set to a negative number to disable")
	@Config.Name("Golem Speed Cap Upwards")
	@RLConfig.ImprovementsOnly("-1.0")
	@RLConfig.RLCraftTwoEightTwo("-1.0")
	@RLConfig.RLCraftTwoNine("-1.0")
	@Config.RangeDouble(min=-1.0d)
	public double golemSpeedCapUpwards = -1.0d;
	
	@Config.Comment("Prevent golems from riding anything, like boats or minecarts")
	@Config.Name("Golem Auto Dismount")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean golemAutoDismount = false;
	
	@Config.Comment("Replace the golem's projectile with a LycanitesMobs Projectile (Requires LycanitesMobs)")
	@Config.Name("Golem Lycanites Projectile")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean golemLycanitesProjectile = false;
	
	@Config.Comment("The name of the projectile to use")
	@Config.Name("Golem Lycanites Projectile Name")
	@RLConfig.ImprovementsOnly("demonicblast")
	@RLConfig.RLCraftTwoEightTwo("demonicblast")
	@RLConfig.RLCraftTwoNine("demonicblast")
	public String golemLycanitesProjectileName = "demonicblast";
	
	@Config.Comment("Scale modifier for the lycanites projectile")
	@Config.Name("Golem Lycanites Projectile Scale Modifier")
	@RLConfig.ImprovementsOnly("1.0")
	@RLConfig.RLCraftTwoEightTwo("1.0")
	@RLConfig.RLCraftTwoNine("1.0")
	public double golemLycanitesProjectileScaleModifier = 1.0d;
	
	@Config.Comment("Speed modifier for the lycanites projectile")
	@Config.Name("Golem Lycanites Projectile Speed Modifier")
	@RLConfig.ImprovementsOnly("9.0")
	@RLConfig.RLCraftTwoEightTwo("9.0")
	@RLConfig.RLCraftTwoNine("9.0")
	public double golemLycanitesProjectileSpeedModifier = 9.0d;
	
	@Config.Comment("Use dimension blacklist")
	@Config.Name("Dimension Blacklist Enabled")
	@Config.RequiresMcRestart
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean dimensionBlacklistEnabled = false;
	
	@Config.Comment("Dimension blacklist of dimension IDs")
	@Config.Name("Dimension Blacklist")
	@Config.RequiresMcRestart
	@RLConfig.ImprovementsOnly("111")
	@RLConfig.RLCraftTwoEightTwo("111")
	@RLConfig.RLCraftTwoNine("111")
	public int[] dimensionBlacklistIds = {111};
	
	@Config.Comment("Whether the dimension blacklist is a whitelist")
	@Config.Name("Dimension Blacklist Is Whitelist")
	@Config.RequiresMcRestart
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("false")
	public boolean dimensionBlacklistIsWhitelist = false;
	
	@Config.Comment("Make towers generate consistently instead of as soon as possible, avoiding the need for the positions file. Be warned, this disables tower commands.")
	@Config.Name("Consistent Tower Generation")
	@Config.RequiresMcRestart
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("false")
	public boolean consistentTowerGeneration = false;
	
	
}
