package com.charles445.rltweaker.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Nullable;

import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PotionCoreClientReflect
{
	public final IAttribute MAGIC_SHIELDING;
	
	public final ResourceLocation ARMOR_MAGIC_SHIELD;
	
	public final Class c_PotionCoreAttributes;
	public final Field f_PotionCoreAttributes_MAGIC_SHIELDING;
	
	public final Class c_PotionCoreEventHandlerClient;
	public final Method m_drawEnchantmentGlint;
	public final Field f_PotionCoreEventHandlerClient_ARMOR_MAGIC_SHIELD;
	
	public PotionCoreClientReflect() throws Exception
	{
		c_PotionCoreAttributes = Class.forName("com.tmtravlr.potioncore.PotionCoreAttributes");
		f_PotionCoreAttributes_MAGIC_SHIELDING = ReflectUtil.findField(c_PotionCoreAttributes, "MAGIC_SHIELDING");
		MAGIC_SHIELDING = (IAttribute) f_PotionCoreAttributes_MAGIC_SHIELDING.get(null);
		
		c_PotionCoreEventHandlerClient = Class.forName("com.tmtravlr.potioncore.PotionCoreEventHandlerClient");
		m_drawEnchantmentGlint = ReflectUtil.findMethod(c_PotionCoreEventHandlerClient, "drawEnchantmentGlint");
		f_PotionCoreEventHandlerClient_ARMOR_MAGIC_SHIELD = ReflectUtil.findField(c_PotionCoreEventHandlerClient, "ARMOR_MAGIC_SHIELD");
		
		ARMOR_MAGIC_SHIELD = (ResourceLocation) f_PotionCoreEventHandlerClient_ARMOR_MAGIC_SHIELD.get(null);
	}
	
	public void drawEnchantmentGlint(double x, double y, double z, double width, double height) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		m_drawEnchantmentGlint.invoke(null, x,y,z,width,height);
	}
}
