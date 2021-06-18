package com.charles445.rltweaker.asm;

import com.charles445.rltweaker.asm.helper.ASMHelper;
import com.charles445.rltweaker.asm.patch.PatchBetterCombatMountFix;
import com.charles445.rltweaker.asm.patch.PatchConcurrentParticles;
import com.charles445.rltweaker.asm.patch.PatchLessCollisions;
import com.charles445.rltweaker.asm.patch.PatchRealBench;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.tree.ClassNode;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.BiFunction;

public class RLTweakerASM implements IClassTransformer
{
	private Map<String, List<PatchMeta>> transformMap = new HashMap<>();
	
	// TODO: Use MODID constant (It worked for ISeeDragons, but might not be safe if RLTweaker#<clinit> loads the wrong classes.
	private static final ASMConfig config = new ASMConfig("rltweaker");
	
	public RLTweakerASM()
	{
		this.createPatches();
	}
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass)
	{
		//Check for patches
		if(transformMap.containsKey(transformedName))
		{
			System.out.println("Patch exists for "+transformedName);
			PatchResult result = PatchResult.NO_MUTATION;
			
			ClassNode clazzNode = ASMHelper.readClassFromBytes(basicClass);
			
			for(PatchMeta manager : transformMap.get(transformedName))
			{
				try
				{
					System.out.println("Applying patch [" + manager.name + "] " + manager.desc);
					result = result.add(manager.patch.apply(this, clazzNode));
				}
				catch(Exception e)
				{
					// TODO: If there was an error, clazzNode may have been mutated into an undefined state.
					System.out.println("Failed at patch "+manager.name);
					System.out.println("Failed to write "+transformedName);
					e.printStackTrace();
					return basicClass;
				}
			}
			
			//TODO verbose
			if(result.isMutated())
			{
				System.out.println("Writing class "+transformedName+" with flags "+result);
				return ASMHelper.writeClassToBytes(clazzNode, result.getFlags());
			}
			else
			{
				System.out.println("All patches for class "+transformedName+" were cancelled, skipping...");
				return basicClass;
			}
		}
		
		return basicClass;
	}

	public void addPatcher(Class<?> clazz)
	{
		@Nullable
		Patcher patcher = clazz.getAnnotation(Patcher.class);
		if (patcher == null) {
			throw new IllegalArgumentException(clazz.getName() + " does not have an @Patcher annotation");
		}
		
		for (Method m : clazz.getDeclaredMethods()) {
			@Nullable
			Patch patch = m.getAnnotation(Patch.class);
			if (patch != null) {
				if (!Modifier.isPublic(m.getModifiers()) || !Modifier.isStatic(m.getModifiers()) ||
						!Arrays.equals(m.getParameterTypes(), new Class[]{RLTweakerASM.class, ClassNode.class}) ||
						!m.getReturnType().equals(PatchResult.class)) {
					throw new IllegalArgumentException(clazz.getName() + "#" + m.getName() + " is not declared correctly to be a @Patch");
				}
				
				addPatch(patch.target(),
						patcher.name().equals("") ? clazz.getSimpleName() : patcher.name(),
						patch.desc(),
						(tweaker, clazzNode) -> {
							try {
								return (PatchResult) m.invoke(null, tweaker, clazzNode);
							} catch (ReflectiveOperationException e) {
								// We sanitized the method already
								throw new RuntimeException("This shouldn't have happened (blame xcube)", e);
							}
						});
			}
		}
	}
	
	public void addPatch(String target, String name, String desc, BiFunction<RLTweakerASM,ClassNode, PatchResult> patch) {
		this.transformMap.computeIfAbsent(target, t -> new ArrayList<>())
				.add(new PatchMeta(name, desc, patch));
	}
	
	public void addPatch(String target, BiFunction<RLTweakerASM,ClassNode, PatchResult> patch) {
		this.addPatch(target, "<anonymous>", "", patch);
	}
	
	private void createPatches()
	{
		//Create all the patches
		if(!config.getBoolean("general.patches.ENABLED", true))
		{
			System.out.println("Patcher has been disabled");
			return;
		}
		System.out.println("Patcher is enabled");
		
		//particleThreading
		if(config.getBoolean("general.patches.particleThreading", true))
		{
			addPatcher(PatchConcurrentParticles.class);
		}
		
		//lessCollisions
		if(config.getBoolean("general.patches.lessCollisions", true))
		{
			addPatcher(PatchLessCollisions.class);
		}
		
		//betterCombatMountFix
		if(config.getBoolean("general.patches.betterCombatMountFix", true))
		{
			addPatcher(PatchBetterCombatMountFix.class);
		}
		
		//realBenchDupeBugFix
		if(config.getBoolean("general.patches.realBenchDupeBugFix", true))
		{
			addPatcher(PatchRealBench.class);
		}
		
		//addPatcher(PatchForgeNetwork.class);
	}
	
	private static final class PatchMeta {
		
		final String name;
		final String desc;
		final BiFunction<RLTweakerASM,ClassNode, PatchResult> patch;
		
		private PatchMeta(String name, String desc, BiFunction<RLTweakerASM,ClassNode, PatchResult> patch) {
			this.name = name;
			this.desc = desc;
			this.patch = patch;
		}
	}
}
