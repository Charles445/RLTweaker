package com.charles445.rltweaker.hook;

import java.util.concurrent.ConcurrentLinkedDeque;

import com.charles445.rltweaker.config.ModConfig;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class HookMinecraft
{
	public static <E> ConcurrentLinkedDeque<E> newConcurrentLinkedDeque()
	{
		return new ConcurrentLinkedDeque<E>();
	}
	
	public static PathNodeType verifyPathNodeType(PathNodeType type, IBlockAccess access, int x, int y, int z)
	{
		boolean isBaseWood = type == PathNodeType.DOOR_WOOD_CLOSED;
		boolean isBaseIron = type == PathNodeType.DOOR_IRON_CLOSED;
		
		if(isBaseWood || isBaseIron)
		{
			BlockPos basePos = new BlockPos(x, y, z);
			IBlockState baseState = access.getBlockState(basePos);
			Block baseBlock = baseState.getBlock();
			
			//Sanity instanceof check and check if top half
			if(baseBlock instanceof BlockDoor && baseState.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER)
			{
				Material refMaterial = isBaseWood ? Material.WOOD : Material.IRON;
				BlockPos downPos = basePos.down();
				IBlockState downState = access.getBlockState(downPos);
				Block downBlock = downState.getBlock();
				Material downMaterial = downState.getMaterial();
				
				//Check bottom half
				if(downBlock instanceof BlockDoor && downMaterial == refMaterial && ((Boolean)downState.getValue(BlockDoor.OPEN)).booleanValue())
				{
					return PathNodeType.DOOR_OPEN;
				}
			}
		}
		
		return type;
	}
	
	public static void playLimitedBroadcastSound(PlayerList playerList, SPacketEffect packet, BlockPos pos)
	{
		double maxDist = ModConfig.server.minecraft.broadcastedSoundsDistanceLimit;
		maxDist *= maxDist;
		for (EntityPlayerMP player : playerList.getPlayers())
        {
			if(player.getDistanceSq(pos) < maxDist)
				player.connection.sendPacket(packet);
        }
	}
}
