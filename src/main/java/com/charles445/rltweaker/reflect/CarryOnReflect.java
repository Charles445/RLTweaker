package com.charles445.rltweaker.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CarryOnReflect
{
	public final Class c_RegistrationHandler;
	public final Field f_RegistrationHandler_itemTile;
	
	public final Class c_ItemTile;
	public final Method m_ItemTile_hasTileData;
	public final Method m_ItemTile_getBlock;
	
	public CarryOnReflect() throws Exception
	{
		c_RegistrationHandler = Class.forName("tschipp.carryon.common.handler.RegistrationHandler");
		f_RegistrationHandler_itemTile = ReflectUtil.findField(c_RegistrationHandler, "itemTile");
		
		c_ItemTile = Class.forName("tschipp.carryon.common.item.ItemTile");
		m_ItemTile_hasTileData = ReflectUtil.findMethod(c_ItemTile, "hasTileData");
		m_ItemTile_getBlock = ReflectUtil.findMethod(c_ItemTile, "getBlock", ItemStack.class);
	}
	
	public boolean isItemTile(Item item) throws IllegalArgumentException, IllegalAccessException
	{
		return item == (Item)f_RegistrationHandler_itemTile.get(null);
	}
	
	public boolean hasTileData(ItemStack stack) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (boolean) m_ItemTile_hasTileData.invoke(null, stack);
	}
	
	public Block getItemTileBlock(ItemStack itemTileStack) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (Block) m_ItemTile_getBlock.invoke(null, itemTileStack);
	}
}
