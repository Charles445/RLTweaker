package com.charles445.rltweaker.asm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.tree.ClassNode;

import com.charles445.rltweaker.asm.helper.ASMHelper;
import com.charles445.rltweaker.asm.patch.IPatch;
import com.charles445.rltweaker.asm.patch.PatchConcurrentParticles;
import com.charles445.rltweaker.asm.patch.PatchLessCollisions;

import net.minecraft.launchwrapper.IClassTransformer;

public class RLTweakerASM implements IClassTransformer
{
	private boolean run = true;
	
	protected static Map<String, List<IPatch>> transformMap = new HashMap<>();
	
	public RLTweakerASM()
	{
		super();
		
		this.run = ASMConfig.getBoolean("general.patches.ENABLED", true);
		
		if(this.run)
		{
			System.out.println("Patcher is enabled");
		}
		else
		{
			System.out.println("Patcher has been disabled");
			return;
		}
		
		this.createPatches();
	}
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass)
	{
		if(!this.run)
			return basicClass;
		
		//Check for patches
		if(transformMap.containsKey(transformedName))
		{
			int flags = 0;
			
			ClassNode clazzNode = ASMHelper.readClassFromBytes(basicClass);
			
			for(IPatch patch : transformMap.get(transformedName))
			{
				flags = flags | patch.getFlags();
				try
				{
					patch.patch(clazzNode);
				}
				catch(Exception e)
				{
					System.out.println("Failed at patch "+patch.getPatchManager().getName());
					System.out.println("Failed to write "+transformedName);
					e.printStackTrace();
					return basicClass;
				}
			}
			
			return ASMHelper.writeClassToBytes(clazzNode, flags);
			
		}
		
		
		return basicClass;
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
	}

}
