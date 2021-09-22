package com.charles445.rltweaker.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.annotation.Nullable;

import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class QuarkReflect
{
	public final Class c_BlockCustomChest;
	public final Method m_BlockCustomChest_getCustomType;
	public final Method m_BlockCustomChest_isDoubleChest;
	
	public final Class c_VariedChests$ChestType;
	public final Field f_VariedChests$ChestType_NAME_TO_TYPE;
	
	public QuarkReflect() throws Exception
	{
		c_BlockCustomChest = Class.forName("vazkii.quark.decoration.block.BlockCustomChest");
		m_BlockCustomChest_getCustomType = ReflectUtil.findMethod(c_BlockCustomChest, "getCustomType", IBlockAccess.class, BlockPos.class);
		m_BlockCustomChest_isDoubleChest = ReflectUtil.findMethod(c_BlockCustomChest, "isDoubleChest");
		
		c_VariedChests$ChestType = Class.forName("vazkii.quark.decoration.feature.VariedChests$ChestType");
		f_VariedChests$ChestType_NAME_TO_TYPE = ReflectUtil.findField(c_VariedChests$ChestType, "NAME_TO_TYPE");
	}
	
	/** Returns VariedChests.ChestType **/
	public Object getCustomChestType(Block blockCustomChest, IBlockAccess access, BlockPos pos) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return m_BlockCustomChest_getCustomType.invoke(blockCustomChest, access, pos);
	}
	
	@Nullable
	/** Returns VariedChests.ChestType or null **/
	public Object getChestTypeFromString(String typeTag) throws IllegalArgumentException, IllegalAccessException
	{
		Map nameMap = (Map) f_VariedChests$ChestType_NAME_TO_TYPE.get(null);
		if(nameMap == null)
			return null;
		return nameMap.get(typeTag);
	}
	
	public boolean isDoubleChest(Block blockCustomChest, World world, BlockPos pos, Object chestType) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (boolean) m_BlockCustomChest_isDoubleChest.invoke(blockCustomChest, world, pos, chestType);
	}
	
	public boolean isBlockCustomChest(Block block)
	{
		return c_BlockCustomChest.isInstance(block);
	}
}
