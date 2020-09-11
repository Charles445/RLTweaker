package com.charles445.rltweaker;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.charles445.rltweaker.capability.ITweakerCapability;
import com.charles445.rltweaker.capability.TweakerCapability;
import com.charles445.rltweaker.capability.TweakerStorage;
import com.charles445.rltweaker.command.CommandAdvisor;
import com.charles445.rltweaker.command.CommandErrorReport;
import com.charles445.rltweaker.config.JsonConfig;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.handler.BattleTowersHandler;
import com.charles445.rltweaker.handler.MinecraftHandler;
import com.charles445.rltweaker.handler.MotionCheckHandler;
import com.charles445.rltweaker.handler.RecurrentHandler;
import com.charles445.rltweaker.handler.ReskillableHandler;
import com.charles445.rltweaker.handler.RoguelikeHandler;
import com.charles445.rltweaker.handler.RuinsHandler;
import com.charles445.rltweaker.handler.SMEHandler;
import com.charles445.rltweaker.handler.TANHandler;
import com.charles445.rltweaker.handler.WaystonesHandler;
import com.charles445.rltweaker.network.NetworkHandler;
import com.charles445.rltweaker.network.PacketHandler;
import com.charles445.rltweaker.proxy.CommonProxy;
import com.charles445.rltweaker.util.ModNames;
import com.charles445.rltweaker.util.VersionDelimiter;

import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;

@Mod
(
	modid = RLTweaker.MODID, 
	name = RLTweaker.NAME, 
	version = RLTweaker.VERSION,
	acceptedMinecraftVersions = "[1.12]",
	acceptableRemoteVersions = "[0.3.0,)" //THIS IS NO LONGER USED
	//updateJSON = "https://raw.githubusercontent.com/Charles445/SimpleDifficulty/master/modupdatechecker.json"
	
)
public class RLTweaker
{
	//TODO Evaluate that this can be server side
	
	public static final String MODID = "rltweaker";
	public static final String NAME = "RLTweaker";
	public static final String VERSION = "0.4.0";
	public static final VersionDelimiter VERSION_DELIMITER = new VersionDelimiter(VERSION);
	public static final VersionDelimiter MINIMUM_VERSION = new VersionDelimiter("0.3.0");
	
	@Mod.Instance(RLTweaker.MODID)
	public static RLTweaker instance;
	
	@SidedProxy(clientSide = "com.charles445.rltweaker.proxy.ClientProxy",
			serverSide = "com.charles445.rltweaker.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	public static Logger logger = LogManager.getLogger("RLTweaker");
	
	public static File jsonDirectory;
	
	public static Map<String, Object> handlers = new HashMap<>();
	
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		jsonDirectory = new File(event.getModConfigurationDirectory(), RLTweaker.MODID);
		
		PacketHandler.init();
		
		CapabilityManager.INSTANCE.register(ITweakerCapability.class, new TweakerStorage(), TweakerCapability::new);
		
		handlers.put(ModNames.MINECRAFT, new MinecraftHandler());
		
		if(Loader.isModLoaded(ModNames.RESKILLABLE) && ModConfig.server.reskillable.enabled)
		{
			handlers.put(ModNames.RESKILLABLE, new ReskillableHandler());
		}
		
		if(Loader.isModLoaded(ModNames.ROGUELIKEDUNGEONS) && ModConfig.server.roguelike.enabled)
		{
			handlers.put(ModNames.ROGUELIKEDUNGEONS, new RoguelikeHandler());
		}
		
		if(Loader.isModLoaded(ModNames.RUINS) && ModConfig.server.ruins.enabled)
		{
			handlers.put(ModNames.RUINS, new RuinsHandler());
		}
		
		if(Loader.isModLoaded(ModNames.WAYSTONES) && ModConfig.server.waystones.enabled)
		{
			handlers.put(ModNames.WAYSTONES, new WaystonesHandler());
		}
		
		proxy.preInit();
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		if(Loader.isModLoaded(ModNames.RECURRENTCOMPLEX) && ModConfig.server.recurrentcomplex.enabled)
		{
			handlers.put(ModNames.RECURRENTCOMPLEX, new RecurrentHandler());
		}
		
		if(Loader.isModLoaded(ModNames.TOUGHASNAILS) && ModConfig.server.toughasnails.enabled)
		{
			handlers.put(ModNames.TOUGHASNAILS, new TANHandler());
		}
		
		proxy.init();
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		JsonConfig.init();
		
		proxy.postInit();
	}
	
	@Mod.EventHandler
	public void loadComplete(FMLLoadCompleteEvent event)
	{
		//Motion Check Handler runs after everything else, that way the priority listed will always be after
		new MotionCheckHandler();
		
		if(Loader.isModLoaded(ModNames.SOMANYENCHANTMENTS) && ModConfig.server.somanyenchantments.enabled)
		{
			handlers.put(ModNames.SOMANYENCHANTMENTS, new SMEHandler());
		}
		
		if(Loader.isModLoaded(ModNames.BATTLETOWERS) && ModConfig.server.battletowers.enabled)
		{
			handlers.put(ModNames.BATTLETOWERS, new BattleTowersHandler());
		}
		
		proxy.loadComplete();
	}
	
	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandAdvisor());
		event.registerServerCommand(new CommandErrorReport());
	}
	
	@NetworkCheckHandler
	public boolean checkVersion(Map<String, String> values, Side side)
	{
		String version = values.get(MODID);
		
		if(side == Side.SERVER)
		{
			NetworkHandler.serverHasVersioning = false;
			
			//System.out.println("checkVersion SERVER");
			if(StringUtils.isEmpty(version))
			{
				NetworkHandler.serverVersion = new VersionDelimiter("0.0.0");
			}
			else
			{
				VersionDelimiter servervd = new VersionDelimiter(version);
				NetworkHandler.serverVersion = servervd;
				if(servervd.isNewerVersionThan(0, 4))
				{
					NetworkHandler.serverHasVersioning = true;
				}
			}
			
			RLTweaker.logger.trace("Server Version: "+NetworkHandler.serverVersion);
			RLTweaker.logger.trace("Local Version: "+VERSION_DELIMITER);
			RLTweaker.logger.trace("Server Has Versioning: "+NetworkHandler.serverHasVersioning);
			
			return true;
		}
		else
		{
			//System.out.println("checkVersion CLIENT");
			if(StringUtils.isEmpty(version))
				return false;
			
			VersionDelimiter clientvd = new VersionDelimiter(version);
			
			RLTweaker.logger.trace("Client Version: "+clientvd);
			RLTweaker.logger.trace("Local Version: "+VERSION_DELIMITER);
			
			boolean result = clientvd.isNewerVersionThan(MINIMUM_VERSION);
			RLTweaker.logger.trace("Result: "+result);
			
			return result;
		}
	}
	
}