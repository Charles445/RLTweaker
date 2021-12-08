package com.charles445.rltweaker.handler;

import java.lang.reflect.Field;

import javax.annotation.Nullable;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.handler.MinecraftHandler.IContainerValidator;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ModNames;
import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.block.Block;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class RusticHandler
{
	public RusticHandler()
	{
		if(ModConfig.server.rustic.woodHarvestToolFix)
		{
			trySetAxe(new ResourceLocation(ModNames.RUSTIC, "log"));
			trySetAxe(new ResourceLocation(ModNames.RUSTIC, "planks"));
		}
		
		//TODO Config for the validator
		RusticContainerValidator validator = new RusticContainerValidator();
		MinecraftHandler.containerValidators.put("rustic.common.tileentity.BrewingBarrel", validator);
		MinecraftHandler.containerValidators.put("rustic.common.tileentity.ContainerCondenser", validator);
		MinecraftHandler.containerValidators.put("rustic.common.tileentity.ContainerCondenserAdvanced", validator);
	}
	
	private void trySetAxe(ResourceLocation rs)
	{
		if(Block.REGISTRY.containsKey(rs))
		{
			Block block = Block.REGISTRY.getObject(rs);
			if(block.getRegistryName().equals(rs))
			{
				RLTweaker.logger.info("Setting block "+rs.toString()+" harvest type to axe");
				block.setHarvestLevel("axe", 0);
			}
		}
	}
	
	public class RusticContainerValidator implements IContainerValidator
	{
		@Nullable
		private Class c_BrewingBarrel;
		@Nullable
		private Field f_BrewingBarrel_te;
		
		@Nullable
		private Class c_ContainerCondenser;
		@Nullable
		private Field f_ContainerCondenser_te;
		
		@Nullable
		private Class c_ContainerCondenserAdvanced;
		@Nullable
		private Field f_ContainerCondenserAdvanced_te;
		
		public RusticContainerValidator()
		{
			try
			{
				c_BrewingBarrel = Class.forName("rustic.common.tileentity.ContainerBrewingBarrel");
				c_ContainerCondenser = Class.forName("rustic.common.tileentity.ContainerCondenser");
				c_ContainerCondenserAdvanced = Class.forName("rustic.common.tileentity.ContainerCondenserAdvanced");
				
				f_BrewingBarrel_te = ReflectUtil.findField(c_BrewingBarrel, "te");
				f_ContainerCondenser_te = ReflectUtil.findField(c_ContainerCondenser, "te");
				f_ContainerCondenserAdvanced_te = ReflectUtil.findField(c_ContainerCondenserAdvanced, "te");
			}
			catch (Exception e)
			{
				//Error only if this feature is desired
				
				if(ModConfig.server.rustic.validateContainers)
					ErrorUtil.logSilent("Rustic Container Validation Reflection");
			}
		}
		
		
		@Override
		public boolean isValid(Container container)
		{
			if(!ModConfig.server.rustic.validateContainers)
				return true;
			
			if(c_BrewingBarrel != null && c_BrewingBarrel.isInstance(container))
				return handleBrewingBarrel(container);
			
			if(c_ContainerCondenser != null && c_ContainerCondenser.isInstance(container))
				return handleContainerCondenser(container);
			
			if(c_ContainerCondenserAdvanced != null && c_ContainerCondenserAdvanced.isInstance(container))
				return handleContainerCondenserAdvanced(container);
			
			return true;
		}
		
		private boolean handleBrewingBarrel(Container container)
		{
			if(f_BrewingBarrel_te == null)
				return true;
			
			try
			{
				TileEntity te = (TileEntity) f_BrewingBarrel_te.get(container);
				if(te != null)
					return !te.isInvalid();
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				ErrorUtil.logSilent("Rustic Container Validation Brewing Barrel Invocation");
			}
			
			return true;
		}
		
		private boolean handleContainerCondenser(Container container)
		{
			if(f_ContainerCondenser_te == null)
				return true;
			
			try
			{
				TileEntity te = (TileEntity) f_ContainerCondenser_te.get(container);
				if(te != null)
					return !te.isInvalid();
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				ErrorUtil.logSilent("Rustic Container Validation Condenser Invocation");
			}
			
			return true;
		}
		
		private boolean handleContainerCondenserAdvanced(Container container)
		{
			if(f_ContainerCondenserAdvanced_te == null)
				return true;
			
			try
			{
				TileEntity te = (TileEntity) f_ContainerCondenserAdvanced_te.get(container);
				if(te != null)
					return !te.isInvalid();
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				ErrorUtil.logSilent("Rustic Container Validation Condenser Advanced Invocation");
			}
			
			return true;
		}
	}
}
