package com.charles445.rltweaker.config;

import com.charles445.rltweaker.RLTweaker;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Config(modid = RLTweaker.MODID)
public class ModConfig
{
	//Keeping these lower case and uniform to avoid unexpected parsing issues
	//Fun fact, categories get run through toLowerCase(Locale.ENGLISH)
	//But properties do not. Okay...
	
	@Config.Comment("Client options")
	@Config.Name("Client")
	public static ClientConfig client = new ClientConfig();
	
	@Config.Comment("Patch options for the coremod")
	@Config.Name("patches")
	public static PatchConfig patches = new PatchConfig();
	
	@Config.Comment("Server options")
	@Config.Name("Server")
	public static ServerConfig server = new ServerConfig();
	
	public static class ServerConfig
	{
		@Config.Comment("Minecraft tweaks, or anything that isn't mod specific")
		@Config.Name("Minecraft")
		public ConfigMinecraft minecraft = new ConfigMinecraft();
		
		@Config.Comment("Aquaculture tweaks")
		@Config.Name("Aquaculture")
		public ConfigAquaculture aquaculture = new ConfigAquaculture();
		
		@Config.Comment("Battle Towers tweaks")
		@Config.Name("Battle Towers")
		public ConfigBattleTowers battletowers = new ConfigBattleTowers();
		
		@Config.Comment("Lost Cities tweaks")
		@Config.Name("Lost Cities")
		public ConfigLostCities lostcities = new ConfigLostCities();
		
		@Config.Comment("Recurrent Complex tweaks")
		@Config.Name("Recurrent Complex")
		public ConfigRecurrent recurrentcomplex = new ConfigRecurrent();
		
		@Config.Comment("Reskillable tweaks")
		@Config.Name("Reskillable")
		public ConfigReskillable reskillable = new ConfigReskillable();
		
		@Config.Comment("Roguelike Dungeons tweaks")
		@Config.Name("Roguelike Dungeons")
		public ConfigRoguelike roguelike = new ConfigRoguelike();
		
		@Config.Comment("Ruins tweaks")
		@Config.Name("Ruins")
		public ConfigRuins ruins = new ConfigRuins();
		
		@Config.Comment("So Many Enchantments tweaks")
		@Config.Name("So Many Enchantments")
		public ConfigSME somanyenchantments = new ConfigSME();
		
		@Config.Comment("Tough As Nails tweaks")
		@Config.Name("Tough As Nails")
		public ConfigTAN toughasnails = new ConfigTAN();
		
		@Config.Comment("Waystones tweaks")
		@Config.Name("Waystones")
		public ConfigWaystones waystones = new ConfigWaystones();
	}
	
	public static class ClientConfig
	{
		@Config.Comment("Classy Hats tweaks")
		@Config.Name("Classy Hats")
		public ConfigClassyHatsClient classyhats = new ConfigClassyHatsClient();
		
		@Config.Comment("Potion Core tweaks")
		@Config.Name("Potion Core")
		public ConfigPotionCoreClient potioncore = new ConfigPotionCoreClient();
	}
	
	@Mod.EventBusSubscriber(modid = RLTweaker.MODID)
	private static class EventHandler
	{
		@SubscribeEvent
		@SideOnly(Side.CLIENT)
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
		{
			if(event.getModID().equals(RLTweaker.MODID))
			{
				ConfigManager.sync(RLTweaker.MODID, Config.Type.INSTANCE);
			}
		}
	}
}
