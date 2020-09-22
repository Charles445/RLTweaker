package com.charles445.rltweaker.config.init;

import java.util.HashMap;
import java.util.Map;

import com.charles445.rltweaker.util.ModNames;

import net.minecraftforge.fml.common.Loader;

public class JsonConfigLessCollisions
{
	public static Map<String, Double> getDefaults()
	{
		//Np combat allies or offensive tools
		//No mountables, except for pigs.
		//No projectiles of any kind
		//Caution with entities that may become the owner of explosions
		
		
		
		Map<String,Double> map = new HashMap<String,Double>();
		
		//Default value is 2.0d
		double dfv = 2.0d;
		
		if(Loader.isModLoaded(ModNames.DEFILEDLANDS))
		{
			map.put("lykrast.defiledlands.common.entity.boss.EntityDestroyer", dfv);
			map.put("lykrast.defiledlands.common.entity.boss.EntityMourner", dfv);
			map.put("lykrast.defiledlands.common.entity.monster.EntityHost", dfv);
			map.put("lykrast.defiledlands.common.entity.monster.EntityScuttler", dfv);
			map.put("lykrast.defiledlands.common.entity.monster.EntityShambler", dfv);
			map.put("lykrast.defiledlands.common.entity.monster.EntityShamblerTwisted", dfv);
			map.put("lykrast.defiledlands.common.entity.monster.EntitySlimeDefiled", dfv);
			map.put("lykrast.defiledlands.common.entity.passive.EntityBookWyrm", dfv);
		}
		
		if(Loader.isModLoaded(ModNames.FAMILIARFAUNA))
		{
			map.put("familiarfauna.entities.EntityButterfly", dfv);
			map.put("familiarfauna.entities.EntityDeer", dfv);
			map.put("familiarfauna.entities.EntityDragonfly", dfv);
			map.put("familiarfauna.entities.EntityPixie", dfv);
			map.put("familiarfauna.entities.EntitySnail", dfv);
			map.put("familiarfauna.entities.EntityTurkey", dfv);
		}
		
		if(Loader.isModLoaded(ModNames.ICEANDFIRE))
		{
			//NOTE, these entity names are tuned for 1.7.1
			//I don't think they got refactored at any point though
			
			map.put("com.github.alexthe666.iceandfire.entity.EntityCyclops", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntityCyclopsEye", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntityDeathWormEgg", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntityDragonEgg", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntityDragonSkull", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntityGorgon", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntityHippocampus", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntityHippogryphEgg", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntityMultipartPart", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntityMyrmexEgg", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntityMyrmexQueen", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntityMyrmexRoyal", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntityMyrmexSentinel", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntityMyrmexSoldier", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntityMyrmexWorker", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntityPixie", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntitySeaSerpent", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntitySeaSerpentBubbles", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntitySiren", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntitySnowVillager", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntityStymphalianBird", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntityTroll", dfv);
		}
		
		if(Loader.isModLoaded(ModNames.LYCANITESMOBS))
		{
			map.put("com.lycanitesmobs.core.entity.EntityItemCustom", dfv);
		}
		
		if(Loader.isModLoaded(ModNames.TRUMPETSKELETON))
		{
			map.put("com.jamieswhiteshirt.trumpetskeleton.common.entity.EntityTrumpetSkeleton", dfv);
		}
		
		if(Loader.isModLoaded(ModNames.TUMBLEWEED))
		{
			map.put("net.konwboy.tumbleweed.common.EntityTumbleweed", dfv);
		}
		
		//Minecraft
		
		map.put("net.minecraft.entity.item.EntityArmorStand", dfv);
		map.put("net.minecraft.entity.item.EntityItem", dfv);
		map.put("net.minecraft.entity.item.EntityItemFrame", dfv);
		map.put("net.minecraft.entity.item.EntityPainting", dfv);
		map.put("net.minecraft.entity.item.EntityXPOrb", dfv);
		map.put("net.minecraft.entity.monster.EntityBlaze", dfv);
		map.put("net.minecraft.entity.monster.EntityCaveSpider", dfv);
		map.put("net.minecraft.entity.monster.EntityCreeper", dfv);
		map.put("net.minecraft.entity.monster.EntityElderGuardian", dfv);
		map.put("net.minecraft.entity.monster.EntityEnderman", dfv);
		map.put("net.minecraft.entity.monster.EntityEndermite", dfv);
		map.put("net.minecraft.entity.monster.EntityEvoker", dfv);
		map.put("net.minecraft.entity.monster.EntityGhast", dfv);
		map.put("net.minecraft.entity.monster.EntityGuardian", dfv);
		map.put("net.minecraft.entity.monster.EntityHusk", dfv);
		map.put("net.minecraft.entity.monster.EntityIllusionIllager", dfv);
		map.put("net.minecraft.entity.monster.EntityMagmaCube", dfv);
		map.put("net.minecraft.entity.monster.EntityPigZombie", dfv);
		map.put("net.minecraft.entity.monster.EntityPolarBear", dfv);
		map.put("net.minecraft.entity.monster.EntityShulker", dfv);
		map.put("net.minecraft.entity.monster.EntitySilverfish", dfv);
		map.put("net.minecraft.entity.monster.EntitySkeleton", dfv);
		map.put("net.minecraft.entity.monster.EntitySlime", dfv);
		map.put("net.minecraft.entity.monster.EntitySpider", dfv);
		map.put("net.minecraft.entity.monster.EntityStray", dfv);
		map.put("net.minecraft.entity.monster.EntityVex", dfv);
		map.put("net.minecraft.entity.monster.EntityVindicator", dfv);
		map.put("net.minecraft.entity.monster.EntityWitch", dfv);
		map.put("net.minecraft.entity.monster.EntityWitherSkeleton", dfv);
		map.put("net.minecraft.entity.monster.EntityZombie", dfv);
		map.put("net.minecraft.entity.monster.EntityZombieVillager", dfv);

		map.put("net.minecraft.entity.passive.EntityBat", dfv);
		map.put("net.minecraft.entity.passive.EntityChicken", dfv);
		map.put("net.minecraft.entity.passive.EntityCow", dfv);
		map.put("net.minecraft.entity.passive.EntityMooshroom", dfv);
		map.put("net.minecraft.entity.passive.EntityOcelot", dfv);
		map.put("net.minecraft.entity.passive.EntityParrot", dfv);
		map.put("net.minecraft.entity.passive.EntityPig", dfv); //Take a pig ride through asmodeus
		map.put("net.minecraft.entity.passive.EntityRabbit", dfv);
		map.put("net.minecraft.entity.passive.EntitySheep", dfv);
		map.put("net.minecraft.entity.passive.EntitySquid", dfv);
		map.put("net.minecraft.entity.passive.EntityVillager", dfv);
		
		return map;
	}
}
