package com.charles445.rltweaker.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.entity.player.EntityPlayer;

public class LevelUpTwoReflect
{
	public final Class c_IPlayerSkill;
	public final Method m_IPlayerSkill_isActive;
	
	public final Class c_SkillRegistry;
	public final Method  m_SkillRegistry_getSkillLevel;
	
	public LevelUpTwoReflect() throws Exception
	{
		c_IPlayerSkill = Class.forName("levelup2.api.IPlayerSkill");
		m_IPlayerSkill_isActive = ReflectUtil.findMethod(c_IPlayerSkill, "isActive");
		
		c_SkillRegistry = Class.forName("levelup2.skills.SkillRegistry");
		m_SkillRegistry_getSkillLevel = ReflectUtil.findMethod(c_SkillRegistry, "getSkillLevel");
	}
	
	public boolean skillIsActive(Object skill) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (boolean) m_IPlayerSkill_isActive.invoke(skill);
	}
	
	public int getSkillLevel(EntityPlayer player, String skillName) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (int) m_SkillRegistry_getSkillLevel.invoke(null, player, skillName);
	}
}
