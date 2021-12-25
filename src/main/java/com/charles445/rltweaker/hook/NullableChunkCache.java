package com.charles445.rltweaker.hook;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;

public class NullableChunkCache extends ChunkCache
{
	//This can't actually stop the constructor from loading all chunks
	//So the ChunkCache constructor has a hook that does an instanceof for NullableChunkCache
	
	public NullableChunkCache(World worldIn, BlockPos posFromIn, BlockPos posToIn, int subIn)
	{
		super(worldIn, posFromIn, posToIn, subIn);
	}
}
