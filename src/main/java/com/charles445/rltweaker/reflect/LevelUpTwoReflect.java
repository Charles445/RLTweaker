package com.charles445.rltweaker.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Nullable;

import com.charles445.rltweaker.util.ModNames;
import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

public class LevelUpTwoReflect
{
	public final Class c_IPlayerSkill;
	public final Method m_IPlayerSkill_isActive;
	
	public final Class c_SkillRegistry;
	public final Method  m_SkillRegistry_getSkillLevel;
	
	//Lycanites Mobs
	private boolean isLycanitesAvailable;
	@Nullable
	private Class c_LM_BaseCreatureEntity;
	@Nullable
	private Class c_LM_IGroupBoss;
	
	public LevelUpTwoReflect() throws Exception
	{
		isLycanitesAvailable = false;
		
		c_IPlayerSkill = Class.forName("levelup2.api.IPlayerSkill");
		m_IPlayerSkill_isActive = ReflectUtil.findMethod(c_IPlayerSkill, "isActive");
		
		c_SkillRegistry = Class.forName("levelup2.skills.SkillRegistry");
		m_SkillRegistry_getSkillLevel = ReflectUtil.findMethod(c_SkillRegistry, "getSkillLevel");
		
		//Lycanites Compatibility Setup
		if(Loader.isModLoaded(ModNames.LYCANITESMOBS))
		{
			try
			{
				c_LM_BaseCreatureEntity = Class.forName("com.lycanitesmobs.core.entity.BaseCreatureEntity");
				c_LM_IGroupBoss = Class.forName("com.lycanitesmobs.api.IGroupBoss");
				isLycanitesAvailable = true;
			}
			catch(Exception e)
			{
				//Silent failure
				isLycanitesAvailable = false;
			}
		}
	}
	
	public boolean skillIsActive(Object skill) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (boolean) m_IPlayerSkill_isActive.invoke(skill);
	}
	
	public int getSkillLevel(EntityPlayer player, String skillName) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (int) m_SkillRegistry_getSkillLevel.invoke(null, player, skillName);
	}
	
	//Lycanites Compatibility
	
	public boolean isLycanitesAvailable()
	{
		return isLycanitesAvailable;
	}
	
	public boolean canLycanitesStealth(EntityLiving living)
	{
		if(!this.isLycanitesAvailable())
			return false;
		
		if(c_LM_BaseCreatureEntity == null || c_LM_IGroupBoss == null)
			return false;
		
		//TODO after IGroupBoss check, check CreatureManager.getInstance().getCreatureGroup("boss") not null and hasEntity
		
		//BaseCreatureEntity, but is not a boss
		return c_LM_BaseCreatureEntity.isInstance(living) && !c_LM_IGroupBoss.isInstance(living);
	}
}
