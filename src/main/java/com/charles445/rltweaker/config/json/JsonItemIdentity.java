package com.charles445.rltweaker.config.json;

import javax.annotation.Nullable;

import com.charles445.rltweaker.RLTweaker;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class JsonItemIdentity
{
	public String registryName;
	
	public int metadata;
	
	@Nullable
	public String nbt;
	
	@Nullable
	//private skips serialization
	private NBTTagCompound nbtCompound;
	
	public JsonItemIdentity(String registryName, int metadata)
	{
		this(registryName, metadata, null);
	}
	
	public JsonItemIdentity(String registryName, int metadata, String nbt)
	{
		this.metadata = metadata;
		this.nbt = nbt;
		
		tryPopulateCompound();
	}
	
	public void tryPopulateCompound()
	{
		if(this.nbtCompound==null)
		{
			if(this.nbt==null)
			{
				this.nbtCompound = null;
			}
			else
			{
				//TODO inform that this failed
				
				try
				{
					this.nbtCompound = JsonToNBT.getTagFromJson(nbt);
					
					if(this.nbtCompound==null)
						throw new Exception();
				}
				catch (Exception e)
				{
					RLTweaker.logger.error("Configuration failed with NBT string: "+nbt);
					
					//Remove the NBT from the item
					//TODO this could cause a problem and cause error spam
					
					this.nbtCompound = null;
					this.nbt = null;
				}
			
			}
		}
	}
	
	public boolean matches(ItemStack stack)
	{
		//TODO this gets twice
		if(stack.hasTagCompound())
		{
			return matches(stack.getItem().getRegistryName().toString(), stack.getMetadata(), stack.getTagCompound());
		}
		else
		{
			return matches(stack.getItem().getRegistryName().toString(), stack.getMetadata(), null);
		}
	}
	
	public boolean matches(String registryName, int stackMetadata)
	{
		return matches(registryName, stackMetadata, null);
	}

	public boolean matches(JsonItemIdentity sentIdentity)
	{
		return matches(sentIdentity.registryName, sentIdentity.metadata, sentIdentity.nbtCompound);
	}
	
	public boolean matches(String registryName, int stackMetadata, @Nullable NBTTagCompound stackCompound)
	{
		//Check registry name
		if(!this.registryName.equals(registryName))
			return false;
		
		//Check metadaata
		if(metadata == -1 || metadata == 32767 || metadata == stackMetadata)
		{
			if(nbt == null || nbt.isEmpty())
			{
				return true;
			}
			else
			{
				//Populate the internal compound if it's null
				tryPopulateCompound();
				
				//Return the result of the nested compound checker
				return checkNestedCompound(this.nbtCompound, stackCompound);
			}
		}
		else
		{
			return false;
		}
	}
	
	private boolean checkNestedCompound(NBTTagCompound selfCompound, NBTTagCompound stackCompound)
	{
		//SimpleDifficulty.logger.debug("checkNestedCompound selfCompound: "+selfCompound);
		//SimpleDifficulty.logger.debug("checkNestedCompound stackCompound: "+stackCompound);
		//TODO performance tests, ideally this won't fire very often (item use and only if the item has an applicable entry in the json
		//STILL, this sort of thing can sneak up on you with performance issues and is worth testing out
		
		//Check if the stack has the required compound
		if(stackCompound==null)
			return false;
		
		//Compare all tags from the identity compound
		for(String key : selfCompound.getKeySet())
		{
			NBTBase a = selfCompound.getTag(key);
			NBTBase b = stackCompound.getTag(key);
			
			//continue on match, return false otherwise
			
			if(a!=null)
			{
				//nbtCompound tag exists and is not null
				
				if(a instanceof NBTTagCompound && b instanceof NBTTagCompound)
				{
					//New tag compound that needs checking for a mismatch
					if(checkNestedCompound((NBTTagCompound)a, (NBTTagCompound)b))
					{
						continue;
					}
					else
					{
						return false;
					}
				}
				else if(a.equals(b))
				{
					continue;
				}
				else
				{
					return false;
				}
			}
			else
			{
				//nbtCompound tag exists but is null (somehow)
				
				if(b==null)
				{
					//Matching null
					continue;
				}
				else
				{
					//stackCompound's tag of the same thing is not null
					return false;
				}
			}
		}
		
		//If this was reached, it's a match
		return true;
	}
}
