package com.charles445.rltweaker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.charles445.rltweaker.command.CommandErrorReport;
import com.charles445.rltweaker.handler.RoguelikeHandler;
import com.charles445.rltweaker.handler.SMEHandler;
import com.charles445.rltweaker.handler.WaystonesHandler;
import com.charles445.rltweaker.util.ModNames;

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
	acceptedMinecraftVersions = "[1.12]"
	//acceptableRemoteVersions = "*"
	//updateJSON = "https://raw.githubusercontent.com/Charles445/SimpleDifficulty/master/modupdatechecker.json"
	
)
public class RLTweaker
{
	//TODO Evaluate that this can be server side
	
    public static final String MODID = "rltweaker";
    public static final String NAME = "RLTweaker";
    public static final String VERSION = "0.1.0";
    
    @Mod.Instance(RLTweaker.MODID)
	public static RLTweaker instance;
	
	public static Logger logger = LogManager.getLogger("RLTweaker");
	
	@Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
		if(Loader.isModLoaded(ModNames.ROGUELIKEDUNGEONS))
		{
			new RoguelikeHandler();
		}
		
    	if(Loader.isModLoaded(ModNames.WAYSTONES))
    	{
    		new WaystonesHandler();
    	}
    }
	
	@Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
		
    }
	
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	
    }
    
    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event)
    {
    	if(Loader.isModLoaded(ModNames.SOMANYENCHANTMENTS))
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