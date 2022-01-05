package com.charles445.rltweaker.asm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.tree.ClassNode;

import com.charles445.rltweaker.asm.helper.ASMHelper;
import com.charles445.rltweaker.asm.patch.IPatch;
import com.charles445.rltweaker.asm.patch.Patch;
import com.charles445.rltweaker.asm.patch.PatchAggressiveMotionChecker;
import com.charles445.rltweaker.asm.patch.PatchAnvilDupe;
import com.charles445.rltweaker.asm.patch.PatchBetterCombatCriticalsFix;
import com.charles445.rltweaker.asm.patch.PatchBetterCombatMountFix;
import com.charles445.rltweaker.asm.patch.PatchBroadcastSounds;
import com.charles445.rltweaker.asm.patch.PatchChunkTicks;
import com.charles445.rltweaker.asm.patch.PatchConcurrentParticles;
import com.charles445.rltweaker.asm.patch.PatchDoorPathfinding;
import com.charles445.rltweaker.asm.patch.PatchEnchant;
import com.charles445.rltweaker.asm.patch.PatchEntityBlockDestroy;
import com.charles445.rltweaker.asm.patch.PatchFixOldGorgon;
import com.charles445.rltweaker.asm.patch.PatchFixOldHippocampus;
import com.charles445.rltweaker.asm.patch.PatchHopper;
import com.charles445.rltweaker.asm.patch.PatchItemFrameDupe;
import com.charles445.rltweaker.asm.patch.PatchLessCollisions;
import com.charles445.rltweaker.asm.patch.PatchLycanitesDupe;
import com.charles445.rltweaker.asm.patch.PatchMyrmexQueenHiveSpam;
import com.charles445.rltweaker.asm.patch.PatchOverlayMessage;
import com.charles445.rltweaker.asm.patch.PatchPathfindingChunkCache;
import com.charles445.rltweaker.asm.patch.PatchPushReaction;
import com.charles445.rltweaker.asm.patch.PatchRealBench;
import com.charles445.rltweaker.asm.patch.PatchReducedSearchSize;
import com.charles445.rltweaker.asm.patch.PatchWaystoneScroll;
import com.charles445.rltweaker.asm.patch.compat.PatchBrokenTransformers;
import com.charles445.rltweaker.asm.patch.compat.PatchCatServer;
import com.charles445.rltweaker.asm.patch.compat.PatchCraftBukkit;
import com.charles445.rltweaker.asm.patch.compat.PatchLootManagement;
import com.charles445.rltweaker.asm.util.ASMInfo;
import com.charles445.rltweaker.asm.util.ASMLogger;
import com.charles445.rltweaker.asm.util.ServerType;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class RLTweakerASM implements IClassTransformer
{
	private boolean run = true;
	
	private boolean firstrun = false;
	
	//private boolean debug = true;
	
	protected static Map<String, List<IPatch>> transformMap = new HashMap<>();
	
	public RLTweakerASM()
	{
		super();
		
		ASMInfo.cacheServerType(this.getClass().getClassLoader());
		
		ASMLogger.info("Server Type: "+ASMInfo.serverType.name());
		
		this.run = ASMConfig.getBoolean("general.patches.ENABLED", true);
		
		if(this.run)
		{
			ASMLogger.info("Patcher is enabled");
		}
		else
		{
			ASMLogger.info("Patcher has been disabled");
			return;
		}
		
		this.createPatches();
	}
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass)
	{
		byte[] result = doTransform(name, transformedName, basicClass);
		return result;
	}
	
	public byte[] doTransform(String name, String transformedName, byte[] basicClass)
	{
		if(true)
		{
			//Test to compute frames on EVERYTHING, although this test skips RLTweaker transformers entirely
			//return ASMHelper.writeClassToBytes(ASMHelper.readClassFromBytes(basicClass), ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		}
		
		if(!this.run)
			return basicClass;
		
		if(!firstrun)
		{
			firstrun = true;
			
			//Gather up loaded transformers in order to make informed decisions later
			if(this.getClass().getClassLoader() instanceof LaunchClassLoader)
			{
				LaunchClassLoader loader = (LaunchClassLoader)this.getClass().getClassLoader();
				for(IClassTransformer transformer : loader.getTransformers())
				{
					if(transformer != null)
					{
						Patch.loadedTransformers.add(transformer.getClass().getName());
					}
				}
			}
			else
			{
				ASMLogger.warn("WARNING: RLTweaker transformer firstrun is not loaded by LaunchClassLoader! Some mod and server compatibility will fail!");
			}
		}
		
		//if(this.debug)
		//	PatchDebug.transformAll(basicClass);
		
		//Check for patches
		if(transformMap.containsKey(transformedName))
		{
			ASMLogger.info("Patch exists for "+transformedName);
			int flags = 0;
			int oldFlags = 0;
			
			boolean ranAnyPatch = false;
			
			ClassNode clazzNode = ASMHelper.readClassFromBytes(basicClass);
			
			//TODO backup old classnode state and flags for safer exception handling?
			for(IPatch patch : transformMap.get(transformedName))
			{
				oldFlags = flags;
				flags = flags | patch.getFlags();
				try
				{
					patch.patch(clazzNode);
					if(patch.isCancelled())
					{
						flags = oldFlags;
					}
					else
					{
						ranAnyPatch = true;
					}
				}
				catch(Exception e)
				{
					ASMLogger.warn("Failed at patch "+patch.getPatchManager().getName());
					ASMLogger.warn("Failed to write "+transformedName);
					ASMLogger.error("Failed Patch Trace: ", e);
					return basicClass;
				}
			}
			
			//TODO verbose
			if(ranAnyPatch)
			{
				ASMLogger.info("Writing class "+transformedName+" with flags "+flagsAsString(flags));
				return ASMHelper.writeClassToBytes(clazzNode, flags);
				//return ASMHelper.writeClassToBytes(clazzNode, ClassWriter.COMPUTE_FRAMES|ClassWriter.COMPUTE_MAXS);
			}
			else
			{
				ASMLogger.info("All patches for class "+transformedName+" were cancelled, skipping...");
				return basicClass;
			}
			
		}
		
		
		return basicClass;
	}
	
	public static String flagsAsString(int flags)
	{
		switch(flags)
		{
			case 0: return "None";
			case 1: return "MAXS";
			case 2: return "FRAMES";
			case 3: return "MAXS | FRAMES";
			default: return "(unknown "+flags+")";
		}
	}
	
	public static void addPatch(IPatch patch)
	{
		String target = patch.getTargetClazz();
		
		if(!transformMap.containsKey(target))
			transformMap.put(target, new ArrayList<IPatch>());
		
		List<IPatch> patches = transformMap.get(target);
		patches.add(patch);
	}
	
	private void createPatches()
	{
		//Create all the patches
		
		//particleThreading
		if(ASMConfig.getBoolean("general.patches.particleThreading", true))
		{
			new PatchConcurrentParticles();
		}
		
		//lessCollisions
		if(ASMConfig.getBoolean("general.patches.lessCollisions", true))
		{
			new PatchLessCollisions();
		}
		
		//betterCombatMountFix
		if(ASMConfig.getBoolean("general.patches.betterCombatMountFix", true))
		{
			new PatchBetterCombatMountFix();
		}
		
		//realBenchDupeBugFix
		if(ASMConfig.getBoolean("general.patches.realBenchDupeBugFix", true))
		{
			new PatchRealBench();
		}
		
		//iafFixMyrmexQueenHiveSpam
		if(ASMConfig.getBoolean("general.patches.iafFixMyrmexQueenHiveSpam", true))
		{
			new PatchMyrmexQueenHiveSpam();
		}
		
		//lycanitesPetDupeFix
		if(ASMConfig.getBoolean("general.patches.lycanitesPetDupeFix", false))
		{
			new PatchLycanitesDupe();
		}
		
		//doorPathfindingFix
		if(ASMConfig.getBoolean("general.patches.doorPathfindingFix", true))
		{
			new PatchDoorPathfinding();
		}
		
		//reducedSearchSize
		if(ASMConfig.getBoolean("general.patches.reducedSearchSize", false))
		{
			new PatchReducedSearchSize();
		}
		
		//patchBroadcastSounds
		if(ASMConfig.getBoolean("general.patches.patchBroadcastSounds", false))
		{
			new PatchBroadcastSounds();
		}
		
		//patchEnchantments
		if(ASMConfig.getBoolean("general.patches.patchEnchantments", false))
		{
			new PatchEnchant();
		}
		
		//aggressiveMotionChecker
		if(ASMConfig.getBoolean("general.patches.aggressiveMotionChecker", true))
		{
			new PatchAggressiveMotionChecker();
		}

		//patchEntityBlockDestroy
		if(ASMConfig.getBoolean("general.patches.patchEntityBlockDestroy", false))
		{
			new PatchEntityBlockDestroy();
		}
		
		//patchItemFrameDupe
		if(ASMConfig.getBoolean("general.patches.patchItemFrameDupe", true))
		{
			new PatchItemFrameDupe();
		}
		
		//patchPushReaction
		if(ASMConfig.getBoolean("general.patches.patchPushReaction", false))
		{
			new PatchPushReaction();
		}
		
		//patchOverlayMessage
		if(ASMConfig.getBoolean("general.patches.patchOverlayMessage", false))
		{
			new PatchOverlayMessage();
		}
		
		//patchAnvilDupe
		if(ASMConfig.getBoolean("general.patches.patchAnvilDupe", true))
		{
			new PatchAnvilDupe();
		}
		
		//patchHopper
		if(ASMConfig.getBoolean("general.patches.patchHopper", false))
		{
			new PatchHopper();
		}

		//betterCombatCriticalsFix
		if(ASMConfig.getBoolean("general.patches.betterCombatCriticalsFix", true))
		{
			new PatchBetterCombatCriticalsFix();
		}
		
		//fixOldHippocampus
		if(ASMConfig.getBoolean("general.patches.fixOldHippocampus", false))
		{
			new PatchFixOldHippocampus();
		}
		
		//fixOldGorgon
		if(ASMConfig.getBoolean("general.patches.fixOldGorgon", false))
		{
			new PatchFixOldGorgon();
		}
		
		//fixWaystoneScrolls
		if(ASMConfig.getBoolean("general.patches.fixWaystoneScrolls", true))
		{
			new PatchWaystoneScroll();
		}
		
		//pathfindingChunkCacheFix
		if(ASMConfig.getBoolean("general.patches.pathfindingChunkCacheFix", true))
		{
			new PatchPathfindingChunkCache();
		}
		
		//serverCompatibility
		if(ASMConfig.getBoolean("general.patches.serverCompatibility", true))
		{
			boolean hasSponge = ASMInfo.hasSponge;
			boolean catServer = ASMInfo.serverType == ServerType.CATSERVER;
			boolean mohist = ASMInfo.serverType == ServerType.MOHIST;
			
			if(hasSponge || catServer || mohist)
			{
				new PatchLootManagement();
			}
			
			//Craftbukkit
			if(catServer || mohist)
			{
				new PatchBrokenTransformers();
				new PatchCraftBukkit();
			}
			
			//CatServer
			if(catServer)
			{
				new PatchCatServer();
			}
		}
		
		//chunkTicks
		if(ASMConfig.getBoolean("general.patches.chunkTicks", true))
		{
			new PatchChunkTicks();
		}
		
		//new PatchForgeNetwork();
	}

}
