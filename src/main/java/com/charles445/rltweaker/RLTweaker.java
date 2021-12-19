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
import com.charles445.rltweaker.command.CommandDebug;
import com.charles445.rltweaker.command.CommandErrorReport;
import com.charles445.rltweaker.command.CommandRLTweakerConfig;
import com.charles445.rltweaker.config.JsonConfig;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.debug.DebugUtil;
import com.charles445.rltweaker.handler.AquacultureHandler;
import com.charles445.rltweaker.handler.BattleTowersHandler;
import com.charles445.rltweaker.handler.BaublesHandler;
import com.charles445.rltweaker.handler.BetterSurvivalHandler;
import com.charles445.rltweaker.handler.CarryOnHandler;
import com.charles445.rltweaker.handler.CharmHandler;
import com.charles445.rltweaker.handler.ClassyHatsHandler;
import com.charles445.rltweaker.handler.GrapplemodHandler;
import com.charles445.rltweaker.handler.IceAndFireHandler;
import com.charles445.rltweaker.handler.InfernalMobsHandler;
import com.charles445.rltweaker.handler.LevelUpTwoHandler;
import com.charles445.rltweaker.handler.LostCitiesHandler;
import com.charles445.rltweaker.handler.LycanitesHandler;
import com.charles445.rltweaker.handler.MinecraftHandler;
import com.charles445.rltweaker.handler.MotionCheckHandler;
import com.charles445.rltweaker.handler.QuarkHandler;
import com.charles445.rltweaker.handler.RecurrentHandler;
import com.charles445.rltweaker.handler.ReskillableHandler;
import com.charles445.rltweaker.handler.RoguelikeHandler;
import com.charles445.rltweaker.handler.RuinsHandler;
import com.charles445.rltweaker.handler.RusticHandler;
import com.charles445.rltweaker.handler.SMEHandler;
import com.charles445.rltweaker.handler.SRParasitesHandler;
import com.charles445.rltweaker.handler.SpawnerControlHandler;
import com.charles445.rltweaker.handler.TANHandler;
import com.charles445.rltweaker.handler.WaystonesHandler;
import com.charles445.rltweaker.network.NetworkHandler;
import com.charles445.rltweaker.network.PacketHandler;
import com.charles445.rltweaker.proxy.CommonProxy;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ModNames;
import com.charles445.rltweaker.util.VersionDelimiter;

import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
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
	acceptedMinecraftVersions = "[1.12, 1.13)",
	acceptableRemoteVersions = "[0.3.0,)", //THIS IS NO LONGER USED
	dependencies = "required-after:forge@[14.23.5.2859,);"
	//updateJSON = "https://raw.githubusercontent.com/Charles445/SimpleDifficulty/master/modupdatechecker.json"
	
)
public class RLTweaker
{
	public static final String MODID = "rltweaker";
	public static final String NAME = "RLTweaker";
	public static final String VERSION = "0.4.6";
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
	public static Map<String, Object> clientHandlers = new HashMap<>();
	
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		FMLCommonHandler.instance().registerCrashCallable(new ErrorUtil.CrashCallable());
		
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
		if(Loader.isModLoaded(ModNames.AQUACULTURE) && ModConfig.server.aquaculture.enabled)
		{
			handlers.put(ModNames.AQUACULTURE, new AquacultureHandler());
		}
		
		if(Loader.isModLoaded(ModNames.GRAPPLEMOD) && ModConfig.server.grapplemod.enabled)
		{
			handlers.put(ModNames.GRAPPLEMOD, new GrapplemodHandler());
		}
		
		if(Loader.isModLoaded(ModNames.ICEANDFIRE) && ModConfig.server.iceandfire.enabled)
		{
			handlers.put(ModNames.ICEANDFIRE, new IceAndFireHandler());
		}
		
		if(Loader.isModLoaded(ModNames.RECURRENTCOMPLEX) && ModConfig.server.recurrentcomplex.enabled)
		{
			handlers.put(ModNames.RECURRENTCOMPLEX, new RecurrentHandler());
		}
		
		if(Loader.isModLoaded(ModNames.TOUGHASNAILS) && ModConfig.server.toughasnails.enabled)
		{
			handlers.put(ModNames.TOUGHASNAILS, new TANHandler());
		}
		
		if(Loader.isModLoaded(ModNames.SPAWNERCONTROL) && ModConfig.server.spawnercontrol.enabled)
		{
			handlers.put(ModNames.SPAWNERCONTROL, new SpawnerControlHandler());
		}
		
		proxy.init();
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		if(Loader.isModLoaded(ModNames.LOSTCITIES) && ModConfig.server.lostcities.enabled)
		{
			handlers.put(ModNames.LOSTCITIES, new LostCitiesHandler());
		}
		
		if(Loader.isModLoaded(ModNames.CHARM) && ModConfig.server.charm.enabled)
		{
			handlers.put(ModNames.CHARM, new CharmHandler());
		}
		
		if(Loader.isModLoaded(ModNames.CARRYON) && ModConfig.server.carryon.enabled)
		{
			handlers.put(ModNames.CARRYON, new CarryOnHandler());
		}
		
		if(Loader.isModLoaded(ModNames.BETTERSURVIVAL) && ModConfig.server.bettersurvival.enabled)
		{
			handlers.put(ModNames.BETTERSURVIVAL, new BetterSurvivalHandler());
		}
		
		if(Loader.isModLoaded(ModNames.BAUBLES) && ModConfig.server.baubles.enabled)
		{
			handlers.put(ModNames.BAUBLES, new BaublesHandler());
		}
		
		JsonConfig.init();
		
		proxy.postInit();
	}
	
	@Mod.EventHandler
	public void loadComplete(FMLLoadCompleteEvent event)
	{
		if(Loader.isModLoaded(ModNames.SRPARASITES) && ModConfig.server.srparasites.enabled)
		{
			handlers.put(ModNames.SRPARASITES, new SRParasitesHandler());
		}
		
		if(Loader.isModLoaded(ModNames.SOMANYENCHANTMENTS) && ModConfig.server.somanyenchantments.enabled)
		{
			handlers.put(ModNames.SOMANYENCHANTMENTS, new SMEHandler());
		}
		
		if(Loader.isModLoaded(ModNames.BATTLETOWERS) && ModConfig.server.battletowers.enabled)
		{
			handlers.put(ModNames.BATTLETOWERS, new BattleTowersHandler());
		}
		
		if(Loader.isModLoaded(ModNames.LEVELUPTWO) && ModConfig.server.leveluptwo.enabled)
		{
			handlers.put(ModNames.LEVELUPTWO, new LevelUpTwoHandler());
		}
		
		if(Loader.isModLoaded(ModNames.QUARK) && ModConfig.server.quark.enabled)
		{
			handlers.put(ModNames.QUARK, new QuarkHandler());
		}
		
		if(Loader.isModLoaded(ModNames.RUSTIC) && ModConfig.server.rustic.enabled)
		{
			handlers.put(ModNames.RUSTIC, new RusticHandler());
		}
		
		if(Loader.isModLoaded(ModNames.LYCANITESMOBS) && ModConfig.server.lycanitesmobs.enabled)
		{
			handlers.put(ModNames.LYCANITESMOBS, new LycanitesHandler());
		}
		
		if(Loader.isModLoaded(ModNames.CLASSYHATS) && ModConfig.server.classyhats.enabled)
		{
			handlers.put(ModNames.CLASSYHATS, new ClassyHatsHandler());
		}
		
		if(Loader.isModLoaded(ModNames.INFERNALMOBS) && ModConfig.server.infernalmobs.enabled)
		{
			handlers.put(ModNames.INFERNALMOBS, new InfernalMobsHandler());
		}
		
		/*
		if(Loader.isModLoaded(ModNames.VARIEDCOMMODITIES) && ModConfig.server.variedcommodities.enabled)
		{
			handlers.put(ModNames.VARIEDCOMMODITIES, new VariedCommoditiesHandler());
		}
		*/
		
		proxy.loadComplete();
		
		//Motion Check Handler runs after everything else, that way the priority listed will always be after
		handlers.put("MotionCheckHandler", new MotionCheckHandler());
		
		//Run any loadComplete debug routines
		DebugUtil.loadCompleteDebugRoutine();
	}
	
	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandAdvisor());
		event.registerServerCommand(new CommandDebug());
		event.registerServerCommand(new CommandErrorReport());
		event.registerServerCommand(new CommandRLTweakerConfig());
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
				if(servervd.isSameOrNewerVersion(0, 4))
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
			
			boolean result = clientvd.isSameOrNewerVersion(MINIMUM_VERSION);
			RLTweaker.logger.trace("Result: "+result);
			
			return result;
		}
	}
	
}