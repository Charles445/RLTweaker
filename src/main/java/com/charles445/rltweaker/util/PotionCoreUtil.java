package com.charles445.rltweaker.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class PotionCoreUtil
{
	public static void scheduleRemovePotion(EntityLivingBase entity, final Potion potion)
	{
		//Potion Core Compatibility
		if(entity.isPotionActive(potion))
		{
			if(entity.getActivePotionEffect(potion).getDuration() > 1)
			{
				entity.removePotionEffect(potion);
				entity.addPotionEffect(new PotionEffect(potion,1));
			}
		}
	}
}
