package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.Patch;
import com.charles445.rltweaker.asm.PatchResult;
import com.charles445.rltweaker.asm.Patcher;
import com.charles445.rltweaker.asm.RLTweakerASM;
import com.charles445.rltweaker.asm.util.TransformUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import static com.charles445.rltweaker.asm.helper.PatchHelper.*;

@Patcher(name = "BetterCombat Mount Fix")
public class PatchBetterCombatMountFix
{
	@Patch(target = "bettercombat.mod.client.handler.EventHandlersClient", desc = "Hooks into getMouseOverExtended")
	public static PatchResult hookMouseOverExtended(RLTweakerASM tweaker, ClassNode c_EventHandlersClient) {
		MethodNode m_getMouseOverExtended = findMethod(c_EventHandlersClient, "getMouseOverExtended");
		
		//Get to the canBeCollidedWith check
		MethodInsnNode hookCaller = TransformUtil.findNextCallWithOpcodeAndName(first(m_getMouseOverExtended), Opcodes.INVOKEVIRTUAL, "canBeCollidedWith", "func_70067_L");
		
		if(hookCaller == null)
			throw new RuntimeException("Couldn't find canBeCollidedWith or func_70067_L");
		
		//Add another parameter load before replacing the hook, rvEntity
		LocalVariableNode lvn_rvEntity = TransformUtil.findLocalVariableWithName(m_getMouseOverExtended, "rvEntity");
		if(lvn_rvEntity == null)
			throw new RuntimeException("Couldn't find local variable rvEntity");
		m_getMouseOverExtended.instructions.insertBefore(hookCaller, new VarInsnNode(Opcodes.ALOAD, lvn_rvEntity.index));
		
		//The hookCaller function now has a stack size of two: entity and rvEntity
		
		hookCaller.setOpcode(Opcodes.INVOKESTATIC);
		hookCaller.owner = "com/charles445/rltweaker/hook/HookBetterCombat";
		hookCaller.name = "strictCollisionCheck";
		hookCaller.desc = "(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity;)Z";
		
		return PatchResult.MAXS_FRAMES;
	}
}
