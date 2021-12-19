package com.charles445.rltweaker.handler;

import javax.annotation.Nullable;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.FBPClientReflect;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FBPHandlerClient
{
	FBPClientReflect reflector;
	
	@Nullable
	FBPGhostListener listener = null;
	
	public FBPHandlerClient()
	{
		try
		{
			reflector = new FBPClientReflect();
			
			if(ModConfig.client.fbp.fixPlacementGhostBlocks)
			{
				listener = new FBPGhostListener();
			}
		}
		catch (Exception e)
		{
			RLTweaker.logger.error("Failed to setup FBPHandlerClient!", e);
			ErrorUtil.logSilent("FBP Client Critical Setup Failure");
			
			//Crash on Critical
			if(e instanceof CriticalException)
				throw new RuntimeException(e);
		}
	}
	
	public class FBPGhostListener implements IWorldEventListener
	{
		public FBPGhostListener()
		{
			MinecraftForge.EVENT_BUS.register(this);
		}
		
		@SubscribeEvent(priority = EventPriority.LOW)
		public void onWorldLoadEvent(final WorldEvent.Load event)
		{
			event.getWorld().addEventListener(this);
		}
		
		@Override
		public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags)
		{
			if(newState.getBlock() == reflector.o_FBPBlock && oldState.getBlock() == Blocks.AIR)
			{
				try
				{
					//BlockNode
					Object blockNode = reflector.getBlockNode(pos);
					if(blockNode != null)
					{
						reflector.setBlockNodeToAir(blockNode);
					}
				}
				catch(IllegalArgumentException | IllegalAccessException e)
				{
					ErrorUtil.logSilent("FBP BlockNodeToAir Invocation");
				}
			}
		}
		
		@Override
		public void notifyLightSet(BlockPos pos) {}
		
		@Override
		public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {}
		
		@Override
		public void playSoundToAllNearExcept(EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch) {}
		
		@Override
		public void playRecord(SoundEvent soundIn, BlockPos pos) {}
		
		@Override
		public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {}
		
		@Override
		public void spawnParticle(int id, boolean ignoreRange, boolean p_190570_3_, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... parameters) {}
		
		@Override
		public void onEntityAdded(Entity entityIn) {}
		
		@Override
		public void onEntityRemoved(Entity entityIn) {}
		
		@Override
		public void broadcastSound(int soundID, BlockPos pos, int data) {}
		
		@Override
		public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) {}
		
		@Override
		public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {}
	}
}
