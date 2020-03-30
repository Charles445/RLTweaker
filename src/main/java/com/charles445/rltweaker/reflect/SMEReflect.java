package com.charles445.rltweaker.reflect;

import java.lang.reflect.Method;

import com.charles445.rltweaker.util.ReflectUtil;

public class SMEReflect
{

	public final Class c_Enchantmentadvancedmending;
	public final Method m_Enchantmentadvancedmending_handler;
	
	public final Class c_EnchantmentSwiper;
	public final Method m_EnchantmentSwiper_handler;

	public final Class c_EnchantmentEmpoweredDefence;
	public final Method m_EnchantmentEmpoweredDefence_handler;
	
	public final Class c_EnchantmentCurseofPossession;
	public final Method m_EnchantmentCurseofPossession_onExist;
	public final Method m_EnchantmentCurseofPossession_toss;
	
	public final Class c_EnchantmentEvasion;
	public final Method m_EnchantmentEvasion_handler;
	
	public final Class c_EnchantmentFreezing;
	public final Method m_EnchantmentFreezing_handler;
	
	public final Class c_EnchantmentParry;
	public final Method m_EnchantmentParry_handler;
	
	public final Class c_EnchantmentFrenzy;
	public final Method m_EnchantmentFrenzy_handler;
	
	public final Class c_EnchantmentUpgradedPotentials;
	public final Method m_EnchantmentUpgradedPotentials_handler;
	
	public SMEReflect() throws Exception
	{
		c_Enchantmentadvancedmending = Class.forName("com.Shultrea.Rin.Ench0_4_0.Enchantmentadvancedmending");
		m_Enchantmentadvancedmending_handler = ReflectUtil.findMethod(c_Enchantmentadvancedmending, "onXP");
		
		c_EnchantmentSwiper = Class.forName("com.Shultrea.Rin.Ench0_4_0.EnchantmentSwiper");
		m_EnchantmentSwiper_handler = ReflectUtil.findMethod(c_EnchantmentSwiper, "HandleEnchant");
		
		c_EnchantmentCurseofPossession = Class.forName("com.Shultrea.Rin.Ench0_4_0.EnchantmentCurseofPossession");
		m_EnchantmentCurseofPossession_onExist = ReflectUtil.findMethod(c_EnchantmentCurseofPossession,"onExist");
		m_EnchantmentCurseofPossession_toss = ReflectUtil.findMethod(c_EnchantmentCurseofPossession,"toss");
		
		c_EnchantmentEmpoweredDefence = Class.forName("com.Shultrea.Rin.Ench0_3_0.EnchantmentEmpoweredDefence");
		m_EnchantmentEmpoweredDefence_handler = ReflectUtil.findMethod(c_EnchantmentEmpoweredDefence, "EmpoweredDefenceEvent");
		
		c_EnchantmentEvasion = Class.forName("com.Shultrea.Rin.Ench0_4_5.EnchantmentEvasion");
		m_EnchantmentEvasion_handler = ReflectUtil.findMethod(c_EnchantmentEvasion, "HandleEnchant");
		
		c_EnchantmentFreezing = Class.forName("com.Shultrea.Rin.Ench0_4_0.EnchantmentFreezing");
		m_EnchantmentFreezing_handler = ReflectUtil.findMethod(c_EnchantmentFreezing, "onEntityDamaged");
		
		c_EnchantmentParry = Class.forName("com.Shultrea.Rin.Ench0_2_0.EnchantmentParry");
		m_EnchantmentParry_handler = ReflectUtil.findMethod(c_EnchantmentParry, "HandleEnchant");
		
		c_EnchantmentFrenzy = Class.forName("com.Shultrea.Rin.Ench0_4_5.EnchantmentFrenzy");
		m_EnchantmentFrenzy_handler = ReflectUtil.findMethod(c_EnchantmentFrenzy, "onAttack");
		
		c_EnchantmentUpgradedPotentials = Class.forName("com.Shultrea.Rin.Ench0_4_0.EnchantmentUpgradedPotentials");
		m_EnchantmentUpgradedPotentials_handler = ReflectUtil.findMethod(c_EnchantmentUpgradedPotentials, "onAnvilAttach");
	}
}
