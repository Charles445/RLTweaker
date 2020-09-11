package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.charles445.rltweaker.asm.util.TransformUtil;

public class PatchLessCollisions extends PatchManager
{
	public PatchLessCollisions()
	{
		super("Less Collisions");
		
		add(new Patch(this, "net.minecraft.world.World", 0)
		{
			@Override
			public void patch(ClassNode c_World)
			{
				MethodNode m_getCollisionBoxes = findMethodWithDesc(c_World, 
						"(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;)Ljava/util/List;",
						"func_184144_a", "getCollisionBoxes");
				
				if(m_getCollisionBoxes == null)
					throw new RuntimeException("Couldn't find getCollisionBoxes or func_184144_a with matching desc");
				
				MethodInsnNode getAABBCall = TransformUtil.findNextCallWithOpcodeAndName(first(m_getCollisionBoxes), Opcodes.INVOKEVIRTUAL, "func_72839_b", "getEntitiesWithinAABBExcludingEntity");
				
				if(getAABBCall == null)
				{
					throw new RuntimeException("Couldn't find getEntitiesWithinAABBExcludingEntity or func_72839_b");
					
				}
				//Stack here should have everything needed for the static call, which is convenient.
				getAABBCall.setOpcode(Opcodes.INVOKESTATIC);
				getAABBCall.owner = "com/charles445/rltweaker/hook/HookWorld";
				getAABBCall.name = "getEntitiesWithinAABBExcludingEntity";

				getAABBCall.desc = "(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;)Ljava/util/List;";
			}
		});
	}
}
