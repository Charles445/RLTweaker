package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.Patch;
import com.charles445.rltweaker.asm.PatchResult;
import com.charles445.rltweaker.asm.Patcher;
import com.charles445.rltweaker.asm.RLTweakerASM;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import com.charles445.rltweaker.asm.util.TransformUtil;

import static com.charles445.rltweaker.asm.helper.PatchHelper.*;

@Patcher(name = "Real Bench")
public class PatchRealBench
{
	@Patch(target = "pw.prok.realbench.WorkbenchTile")
	public static PatchResult patchWorkbenchTile(RLTweakerASM tweaker, ClassNode c_WorkbenchTile) {
		FieldNode f_mResult = new FieldNode(Opcodes.ASM5, Opcodes.ACC_PROTECTED, "mResult", "Lnet/minecraft/util/NonNullList;", "Lnet/minecraft/item/ItemStack;", null);
		
		MethodNode m_init = findMethodWithDesc(c_WorkbenchTile, "()V", "<init>");
		MethodNode m_writeSlots = findMethod(c_WorkbenchTile, "writeSlots");
		MethodNode m_readSlots = findMethod(c_WorkbenchTile, "readSlots");
		
		//Add mResult field
		c_WorkbenchTile.fields.add(f_mResult);
		
		if(true) //Add mResult instantiation
		{
			InsnList insert = new InsnList();
			insert.add(new VarInsnNode(Opcodes.ALOAD, 0)); //this
			insert.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookRealBench", "getResultSlotInit", "()Lnet/minecraft/util/NonNullList;", false));
			insert.add(new FieldInsnNode(Opcodes.PUTFIELD, "pw/prok/realbench/WorkbenchTile", "mResult", "Lnet/minecraft/util/NonNullList;"));
			TransformUtil.insertBeforeFirst(m_init, insert);
		}
		
		if(true) //writeSlots
		{
			InsnList insert = new InsnList();
			insert.add(new VarInsnNode(Opcodes.ALOAD, 1)); //nbt
			insert.add(new VarInsnNode(Opcodes.ALOAD, 0)); //nbt, this
			insert.add(new FieldInsnNode(Opcodes.GETFIELD, "pw/prok/realbench/WorkbenchTile", "mResult", "Lnet/minecraft/util/NonNullList;")); //nbt, mResult
			insert.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookRealBench", "writeSlots", "(Lnet/minecraft/nbt/NBTTagCompound;Lnet/minecraft/util/NonNullList;)V", false));
			TransformUtil.insertBeforeFirst(m_writeSlots, insert);
		}
		
		if(true) //readSlots
		{
			InsnList insert = new InsnList();
			insert.add(new VarInsnNode(Opcodes.ALOAD, 1)); //nbt
			insert.add(new VarInsnNode(Opcodes.ALOAD, 0)); //nbt, this
			insert.add(new FieldInsnNode(Opcodes.GETFIELD, "pw/prok/realbench/WorkbenchTile", "mResult", "Lnet/minecraft/util/NonNullList;")); //nbt, mResult
			insert.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookRealBench", "readSlots", "(Lnet/minecraft/nbt/NBTTagCompound;Lnet/minecraft/util/NonNullList;)V", false));
			TransformUtil.insertBeforeFirst(m_readSlots, insert);
		}
		return PatchResult.MAXS_FRAMES;
	}
	
	@Patch(target = "net.minecraft.inventory.ContainerWorkbench")
	public static PatchResult patchWorkbench(RLTweakerASM tweaker, ClassNode c_ContainerWorkbench) {
		MethodNode m_init = findMethodWithDesc(c_ContainerWorkbench, "(Lnet/minecraft/entity/player/InventoryPlayer;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", "<init>");
		
		//First we need to figure out if RealBench is even installed
		boolean isRealBenchInstalled = false;
		AbstractInsnNode anchor = first(m_init);
		while(anchor != null)
		{
			if(anchor.getType() == AbstractInsnNode.METHOD_INSN)
			{
				if(((MethodInsnNode)anchor).owner.equals("pw/prok/realbench/asm/ASMHooks"))
				{
					isRealBenchInstalled = true;
				}
			}
			anchor = anchor.getNext();
		}
		
		if(!isRealBenchInstalled)
		{
			//Cancel Patch
			return PatchResult.NO_MUTATION;
		}
		//RealBench is installed, replace InventoryCraftResult constructor with our own
		
		anchor = first(m_init);
		while(anchor != null)
		{
			if(anchor.getOpcode() == Opcodes.NEW)
			{
				TypeInsnNode newAnchor = (TypeInsnNode)anchor;
				if(newAnchor.desc.equals("net/minecraft/inventory/InventoryCraftResult"))
				{
					newAnchor.desc = "com/charles445/rltweaker/hook/HookRealBench$Result";
				}
			}
			else if(anchor.getOpcode() == Opcodes.INVOKESPECIAL)
			{
				MethodInsnNode initAnchor = (MethodInsnNode)anchor;
				if(initAnchor.name.equals("<init>") && initAnchor.owner.equals("net/minecraft/inventory/InventoryCraftResult"))
				{
					initAnchor.owner = "com/charles445/rltweaker/hook/HookRealBench$Result";
					initAnchor.desc = "(Lnet/minecraft/inventory/Container;)V";
					//Also push the container to the stack as it needs it
					InsnList qq = new InsnList();
					qq.add(new VarInsnNode(Opcodes.ALOAD, 0));
					m_init.instructions.insertBefore(initAnchor, qq);
				}
			}
			
			anchor = anchor.getNext();
		}
		
		//announce("CLASS DISPLAYER: ContainerWorkbench");
		//(new ClassDisplayer()).printMethod(m_init);
		return PatchResult.MAXS_FRAMES;
	}
}
