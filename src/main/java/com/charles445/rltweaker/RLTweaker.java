package com.charles445.rltweaker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.charles445.rltweaker.capability.ITweakerCapability;
import com.charles445.rltweaker.capability.TweakerCapability;
import com.charles445.rltweaker.capability.TweakerStorage;
import com.charles445.rltweaker.command.CommandErrorReport;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.handler.MinecraftHandler;
import com.charles445.rltweaker.handler.MotionCheckHandler;
import com.charles445.rltweaker.handler.RecurrentHandler;
import com.charles445.rltweaker.handler.RoguelikeHandler;
import com.charles445.rltweaker.handler.RuinsHandler;
import com.charles445.rltweaker.handler.SMEHandler;
import com.charles445.rltweaker.handler.TANHandler;
import com.charles445.rltweaker.handler.WaystonesHandler;
import com.charles445.rltweaker.network.PacketHandler;
import com.charles445.rltweaker.util.ModNames;

import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod
(
	modid = RLTweaker.MODID, 
	name = RLTweaker.NAME, 
	version = RLTweaker.VERSION,
	acceptedMinecraftVersions = "[1.12]",
	acceptableRemoteVersions = "[0.2.0,)" //Last update - Arrow Sync, Teleport Thirst, Thirst Packets
	//updateJSON = "https://raw.githubusercontent.com/Charles445/SimpleDifficulty/master/modupdatechecker.json"
	
)
public class RLTweaker
{
	//TODO Evaluate that this can be server side
	
    public static final String MODID = "rltweaker";
    public static final String NAME = "RLTweaker";
    public static final String VERSION = "0.2.1";
    
    @Mod.Instance(RLTweaker.MODID)
	public static RLTweaker instance;
	
	public static Logger logger = LogManager.getLogger("RLTweaker");
	
	@Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
		PacketHandler.init();
		
		CapabilityManager.INSTANCE.register(ITweakerCapability.class, new TweakerStorage(), TweakerCapability::new);
		
    	new MinecraftHandler();
		
		if(Loader.isModLoaded(ModNames.ROGUELIKEDUNGEONS) && ModConfig.server.roguelike.enabled)
		{
			new RoguelikeHandler();
		}
		
		if(Loader.isModLoaded(ModNames.RUINS) && ModConfig.server.ruins.enabled)
		{
			new RuinsHandler();
		}
		
    	if(Loader.isModLoaded(ModNames.WAYSTONES) && ModConfig.server.waystones.enabled)
    	{
    		new WaystonesHandler();
    	}
    }
	
	@Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
		if(Loader.isModLoaded(ModNames.RECURRENTCOMPLEX) && ModConfig.server.recurrentcomplex.enabled)
		{
			new RecurrentHandler();
		}
		
		if(Loader.isModLoaded(ModNames.TOUGHASNAILS) && ModConfig.server.toughasnails.enabled)
		{
			new TANHandler();
		}
    }
	
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	
    }
    
    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event)
    {
    	//Motion Check Handler runs after everything else, that way the priority listed will always be after
		new MotionCheckHandler();
    	
    	if(Loader.isModLoaded(ModNames.SOMANYENCHANTMENTS) && ModConfig.server.somanyenchantments.enabled)
    	{
    		new SMEHandler();
    	}
    }
    
    @Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
    	event.registerServerCommand(new CommandErrorReport());
	}
    
}