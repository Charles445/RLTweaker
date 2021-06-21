package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.Patch;
import com.charles445.rltweaker.asm.PatchResult;
import com.charles445.rltweaker.asm.Patcher;
import com.charles445.rltweaker.asm.RLTweakerASM;
import com.charles445.rltweaker.asm.util.TransformUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import static com.charles445.rltweaker.asm.helper.PatchHelper.*;

@Patcher(name = "Concurrent Particles")
public class PatchConcurrentParticles
{
	@Patch(target = "net.minecraft.client.particle.ParticleManager", desc = "does particle hook thingy (idk, I did not write this)")
	public static PatchResult particleHookThingy(RLTweakerASM tweaker, ClassNode c_ParticleManager)
	{
		MethodNode m_init = findMethod(c_ParticleManager, "<init>");
		
		AbstractInsnNode anchor = TransformUtil.findNextFieldWithOpcodeAndName(first(m_init), Opcodes.PUTFIELD, "queue", "field_187241_h");
		
		if(anchor == null)
			throw new RuntimeException("Couldn't find queue or field_187241_h");
		
		MethodInsnNode hookCaller = TransformUtil.findPreviousCallWithOpcodeAndName(anchor, Opcodes.INVOKESTATIC, "newArrayDeque");
		
		if(hookCaller == null)
			throw new RuntimeException("Couldn't find newArrayDeque");
		
		hookCaller.owner = "com/charles445/rltweaker/hook/HookMinecraft";
		hookCaller.name = "newConcurrentLinkedDeque";
		hookCaller.desc = "()Ljava/util/concurrent/ConcurrentLinkedDeque;";
		
		return PatchResult.MAXS;
	}
}
