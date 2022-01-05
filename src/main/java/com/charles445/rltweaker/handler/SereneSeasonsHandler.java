package com.charles445.rltweaker.handler;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.hook.HookWorld;
import com.charles445.rltweaker.hook.HookWorld.IChunkTickPost;
import com.charles445.rltweaker.reflect.SereneSeasonsReflect;
import com.charles445.rltweaker.reflect.SereneSeasonsReflect.Season;
import com.charles445.rltweaker.reflect.SereneSeasonsReflect.SubSeason;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class SereneSeasonsHandler
{
	SereneSeasonsReflect reflector;
	
	public SereneSeasonsHandler()
	{
		try
		{
			reflector = new SereneSeasonsReflect();
			
			if(ModConfig.server.sereneseasons.replaceRandomUpdateHandler && ModConfig.patches.chunkTicks)
			{
				IEventListener listener = (IEventListener) CompatUtil.findAndRemoveHandlerFromEventBus("sereneseasons.handler.season.RandomUpdateHandler", "onWorldTick");
				if(listener != null)
				{
					RLTweaker.logger.info("Replacing SereneSeasons RandomUpdateHandler world tick");
					HookWorld.sereneSeasonsPost = new SereneChunkTick(listener);
				}
			}
		}
		catch(Exception e)
		{
			RLTweaker.logger.error("Failed to setup SereneSeasonsHandler!", e);
			ErrorUtil.logSilent("Serene Seasons Critical Setup Failure");
			
			//Crash on Critical
			if(e instanceof CriticalException)
				throw new RuntimeException(e);
		}
	}
	
	public class SereneChunkTick implements IChunkTickPost<SubSeason>
	{
		IEventListener handler;
		private boolean selfWorking;
		private int LCG = (new Random()).nextInt();
		private Map<ResourceLocation, Boolean> cachedBiomeSeasonalEffects;
		private Map<ResourceLocation, Boolean> cachedBiomeTropical;
		
		public SereneChunkTick(IEventListener handler)
		{
			this.handler = handler;
			selfWorking = true;
			MinecraftForge.EVENT_BUS.register(this);
			
			try
			{
				cachedBiomeSeasonalEffects = reflector.createBiomeSeasonalEffects();
				cachedBiomeTropical = reflector.createBiomeTropicalSeasons();
			}
			catch (Exception e)
			{
				selfWorking = false;
				RLTweaker.logger.error("Failed to cache biome information!", e);
				ErrorUtil.logSilent("Serene Seasons Chunk Tick Biome Seasonal Effects");
			}
		}
		
		@SubscribeEvent
		public void onWorldTick(TickEvent.WorldTickEvent event)
		{
			if(!selfWorking || !HookWorld.chunkTickPatchEnabled)
				handler.invoke(event);
		}
		
		@Override
		public SubSeason preUpdate(WorldServer world)
		{
			if(selfWorking)
			{
				try
				{
					SubSeason subSeason = reflector.getSubSeason(reflector.getISeasonState(world));
					Season season = subSeason.getSeason();
					
					Object seasonConfig = reflector.getSeasonConfig();
					boolean changeWeatherFrequency = reflector.getConfigChangeWeatherFrequency(seasonConfig);
					
					WorldInfo worldinfo = world.getWorldInfo();
					
					if(season == Season.WINTER)
					{
						if(changeWeatherFrequency)
						{
							if(worldinfo.isThundering())
								worldinfo.setThundering(false);
							
							if(!worldinfo.isRaining() && worldinfo.getRainTime() > 36000)
								worldinfo.setRainTime(world.rand.nextInt(24000) + 12000);
						}
					}
					else
					{
						if(changeWeatherFrequency)
						{
							if(season == Season.SPRING)
							{
								if(!worldinfo.isRaining() && worldinfo.getRainTime() > 96000)
									worldinfo.setRainTime(world.rand.nextInt(84000) + 12000);
									
							}
							else if(season == Season.SUMMER)
							{
								if(!worldinfo.isThundering() && worldinfo.getThunderTime() > 36000)
									worldinfo.setThunderTime(world.rand.nextInt(24000) + 12000);
							}
						}
						
						if(reflector.getConfigGenerateSnowAndIce(seasonConfig) && reflector.isDimensionWhitelisted(world.provider.getDimension()))
							return subSeason;
					}
				}
				catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
				{
					selfWorking = false;
					RLTweaker.logger.error("Failed to run Serene Seasons Chunk Tick! Returning to default behavior.", e);
					ErrorUtil.logSilent("Serene Seasons Chunk Tick Invocation");
				}
			}
			
			return null;
		}
		
		@Override
		public void invoke(Chunk chunk, SubSeason companion)
		{
			//RLTweaker.logger.info("Serene invoke: "+companion.name()+" "+chunk.x+" "+chunk.z);
			
			int randVal = 4;
			switch(companion)
			{
				case EARLY_SPRING:
					randVal = 16;
					break;
				case MID_SPRING:
					randVal = 12;
					break;
				case LATE_SPRING:
					randVal = 8;
					break;
				default:
					break;
			}
			
			if(chunk.getWorld().rand.nextInt(randVal) != 0)
				return;
			
			int blockX = chunk.x << 4;
			int blockZ = chunk.z << 4;
			World world = chunk.getWorld();
			
			//Get LCG and chunkOffset
			int updateLCG = LCG * 3 + 1013904223;
			LCG = updateLCG;
			int chunkOffset = updateLCG >> 2;
			BlockPos pos = chunk.getPrecipitationHeight(
					new BlockPos(blockX + (chunkOffset & 15), 0, blockZ + (chunkOffset >> 8 & 15)));
			
			//Get Biome and seasonal effects
			Biome biome = chunk.getBiome(pos, world.getBiomeProvider());
			if(Objects.equals(cachedBiomeSeasonalEffects.get(biome.getRegistryName()), false))
				return;
			
			boolean firstRun = true;
			
			int posX = pos.getX();
			int posZ = pos.getZ();
			
			for(int posY = pos.getY(); posY >= 0;posY--)
			{
				Block block = chunk.getBlockState(posX, posY, posZ).getBlock();
				
				if(block == Blocks.SNOW_LAYER)
				{
					pos = new BlockPos(posX, posY, posZ);
					if(getUnsafeTemperature(chunk, biome, pos, companion) >= 0.15f)
					{
						//Too much internal logic to use chunk here
						//RLTweaker.logger.info("("+randVal+") "+"SNOW_LAYER: "+pos.toString());
						world.setBlockToAir(pos);
						break;
					}
					
				}
				
				if(!firstRun)
				{
					if(block == Blocks.ICE)
					{
						pos = new BlockPos(posX, posY, posZ);
						if(getUnsafeTemperature(chunk, biome, pos, companion) >= 0.15f)
						{
							//RLTweaker.logger.info("("+randVal+") "+"ICE: "+pos.toString());
							try
							{
								reflector.turnIntoWater(block, world, pos);
							}
							catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
							{
								selfWorking = false;
								RLTweaker.logger.error("Failed to run Serene Seasons Chunk Tick! Ice broke. Returning to default behavior.", e);
								ErrorUtil.logSilent("Serene Seasons Chunk Tick Invocation Ice");
								return;
							}

							break;
						}
					}
				}
				else
				{
					firstRun = false;
				}
			}
		}
		
		private float getUnsafeTemperature(Chunk chunk, Biome biome, BlockPos pos, SubSeason subseason)
		{
			//Seasonal effects are enabled
			//We don't need to clamp temperatures
			//It's not winter
			float baseTemp = biome.getTemperature(pos);
			
			if(biome.getDefaultTemperature() > 0.8f)
				return baseTemp;
			
			if(Objects.equals(cachedBiomeTropical.get(biome.getRegistryName()), true))
				return baseTemp;
			
			switch (subseason)
			{
				case EARLY_AUTUMN:
				case LATE_SPRING:
					return baseTemp - 0.1f;
	
				case MID_AUTUMN:
				case MID_SPRING:
					return baseTemp - 0.2f;
					
				case EARLY_SPRING:
				case LATE_AUTUMN:
					return baseTemp - 0.4f;
					
				default:
					return baseTemp;
			}
		}
	}
}
