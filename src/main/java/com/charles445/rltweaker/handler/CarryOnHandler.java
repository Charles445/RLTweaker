package com.charles445.rltweaker.handler;

import java.lang.reflect.InvocationTargetException;

import javax.annotation.Nullable;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.QuarkReflect;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ModNames;
import com.charles445.rltweaker.util.StackTraceUtil;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CarryOnHandler
{
	private boolean quarkLoaded = false;
	
	@Nullable
	QuarkReflect reflectorQuark;
	
	public CarryOnHandler()
	{
		try
		{
			//Quark
			this.quarkLoaded = Loader.isModLoaded(ModNames.QUARK);
			try
			{
				if(this.quarkLoaded)
					reflectorQuark = new QuarkReflect();
					
			}
			catch(Exception e)
			{
				RLTweaker.logger.error("Failed to setup Quark for Carryon");
				ErrorUtil.logSilent("CarryOn Quark Reflector Initialization");
			}
		
			MinecraftForge.EVENT_BUS.register(this);
		}
		catch(Exception e)
		{
			RLTweaker.logger.error("Failed to setup CarryOnHandler!", e);
			ErrorUtil.logSilent("CarryOn Critical Setup Failure");
			
			//Crash on Critical
			if(e instanceof CriticalException)
				throw new RuntimeException(e);
		}
	}
	
	@SubscribeEvent
	@SuppressWarnings("deprecation")
	public void onPlaceEvent(BlockEvent.PlaceEvent event)
	{
		//Run on both client and server for synchronization purposes, probably unnecessary
		
		if(StackTraceUtil.stackTraceHasClassOfMethod("tschipp.carryon.common.item.ItemTile", "func_180614_a", "onItemUse"))
		{
			//Event is a call from CarryOn
			BlockSnapshot snapshot = event.getBlockSnapshot();
			ResourceLocation registry = snapshot.getRegistryName();
			World world = event.getWorld();
			EntityPlayer player = event.getPlayer();
			
			if(quarkLoaded && reflectorQuark != null && registry.getResourceDomain().equals(ModNames.QUARK))
			{
				handlePlacedQuarkBlock(event, snapshot, registry, world, player);
			}
		}
	}
	
	public void handlePlacedQuarkBlock(BlockEvent.PlaceEvent event, BlockSnapshot snapshot, ResourceLocation registry, World world, EntityPlayer player)
	{
		Block block = snapshot.getReplacedBlock().getBlock();
		BlockPos pos = snapshot.getPos();
		
		if(ModConfig.server.carryon.quarkChestFix && reflectorQuark.isBlockCustomChest(block))
		{
			try
			{
				//tile entity nbt doesn't get set until later, so obtaining type must be done via carryon
				ItemStack stack = event.getItemInHand();
				
				if(stack.hasTagCompound())
				{
					NBTTagCompound compound = stack.getTagCompound();
					
					if(compound.hasKey("tileData"))
					{
						NBTTagCompound data = compound.getCompoundTag("tileData");
						
						Object type = reflectorQuark.getChestTypeFromString(data.getString("type"));
						
						if(type != null)
						{
							int chestCount = 0;
							
							chestCount += getChestAdjacentCount(block, type, world, pos.north());
							chestCount += getChestAdjacentCount(block, type, world, pos.south());
							chestCount += getChestAdjacentCount(block, type, world, pos.east());
							chestCount += getChestAdjacentCount(block, type, world, pos.west());
							
							//Cancel if there are too many matching chests
							//Because quark chests don't fix themselves, this means any chestq	
							if(chestCount >= 1)
							{
								event.setCanceled(true);
							}
						}
					}
				}
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				e.printStackTrace();
				ErrorUtil.logSilent("CarryOn Quark BlockCustomChest");
			}
		}
	}
	
	private int getChestAdjacentCount(Block block, Object type, World world, BlockPos adjacent) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		if(checkChestMatch(block, type, world, adjacent))
			return reflectorQuark.isDoubleChest(block, world, adjacent, type) ? 2 : 1;
		
		return 0;
	}
	
	private boolean checkChestMatch(Block block, Object type, World world, BlockPos pos) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (world.getBlockState(pos).getBlock() == block && reflectorQuark.getCustomChestType(block, world, pos) == type);
	}
}
