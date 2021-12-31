package com.charles445.rltweaker.asm.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ClassDisplayer
{
	public static ClassDisplayer instance = new ClassDisplayer();
	
	private Map<Integer,String> opcodeToStringCache = new HashMap<Integer, String>();
	
	private void println(String s)
	{
		ASMLogger.info(s);
	}
	
	public ClassDisplayer()
	{
		try
		{
			for(Field f : OpcodesHidden.class.getDeclaredFields())
			{
				//logger.info(f.get(null));
				Object o = f.get(null);
				if(o instanceof Integer)
				{
					if(!opcodeToStringCache.containsKey((Integer)o))
					{
						opcodeToStringCache.put((Integer)o, f.getName());
					}
				}
			}
		}
		catch(Exception e)
		{
			println("Couldn't generate opcodeToString list in ClassDisplayer");
		}
	}
	
	public void printAllMethods(ClassNode clazzNode)
	{
		for(MethodNode m : clazzNode.methods)
		{
			println(m.name+" : "+m.desc);
			printMethod(m);
			println("");
		}
	}
	
	public void printMethod(MethodNode methodNode)
	{
		AbstractInsnNode anchor = methodNode.instructions.getFirst();
		while(anchor!=null)
		{
			println(nodeToString(anchor));
			anchor = anchor.getNext();
		}
	}
	
	public void printMethodLocalVariables(MethodNode methodNode)
	{
		if(methodNode.localVariables!=null)
		{
			for(LocalVariableNode lvn : methodNode.localVariables)
			{
				StringBuilder sb = new StringBuilder();
				sb.append(lvn.index);
				sb.append(" : ");
				sb.append(lvn.start.getLabel().toString());
				sb.append(" -> ");
				sb.append(lvn.end.getLabel().toString());
				sb.append(" : ");
				sb.append("\t");
				sb.append(lvn.name);
				sb.append("(");
				sb.append(lvn.desc);
				sb.append(")");
				println(sb.toString());
			}
		}
	}
	
	protected String nodeToString(final AbstractInsnNode node)
	{
		switch(node.getType())
		{
			case AbstractInsnNode.INSN: return getInsn(node);
			case AbstractInsnNode.INT_INSN: return getIntInsn(node);
			case AbstractInsnNode.VAR_INSN: return getVarInsn(node);
			case AbstractInsnNode.TYPE_INSN: return getTypeInsn(node);
			case AbstractInsnNode.FIELD_INSN: return getFieldInsn(node);
			case AbstractInsnNode.METHOD_INSN: return getMethodInsn(node);
			case AbstractInsnNode.INVOKE_DYNAMIC_INSN: return getInvokeDynamicInsn(node);
			case AbstractInsnNode.JUMP_INSN: return getJumpInsn(node);
			case AbstractInsnNode.LABEL: return getLabel(node);
			case AbstractInsnNode.LDC_INSN: return getLdcInsn(node);
			case AbstractInsnNode.IINC_INSN: return getIincInsn(node);
			case AbstractInsnNode.TABLESWITCH_INSN: return getTableswitchInsn(node);
			case AbstractInsnNode.LOOKUPSWITCH_INSN: return getLookupswitchInsn(node);
			case AbstractInsnNode.MULTIANEWARRAY_INSN: return getMultianewarrayInsn(node);
			case AbstractInsnNode.FRAME: return getFrame(node);
			case AbstractInsnNode.LINE: return getLine(node);
		}
		
		
		return "";
	}
	
	protected String getInsn(final AbstractInsnNode node)
	{
		return opcodeToString(((InsnNode)node).getOpcode());
	}

	protected String getLine(final AbstractInsnNode node)
	{
		LineNumberNode line = (LineNumberNode)node;
		return "Line "+line.line;
	}

	protected String getFrame(final AbstractInsnNode node)
	{
		return "f";
	}

	protected String getMultianewarrayInsn(final AbstractInsnNode node)
	{
		return "getMultianewarrayInsn";
	}

	protected String getLookupswitchInsn(final AbstractInsnNode node)
	{
		return "getLookupswitchInsn";
	}

	protected String getTableswitchInsn(final AbstractInsnNode node)
	{
		return "getTableswitchInsn";
	}

	protected String getIincInsn(final AbstractInsnNode node)
	{
		IincInsnNode n = (IincInsnNode)node;
		return opcodeToString(n.getOpcode())+" ["+n.var+"] += "+n.incr;
	}

	protected String getLdcInsn(final AbstractInsnNode node)
	{
		LdcInsnNode n = (LdcInsnNode)node;
		return opcodeToString(n.getOpcode())+" "+n.cst;
	}

	protected String getLabel(final AbstractInsnNode node)
	{
		return "Label "+node.hashCode();
	}

	protected String getJumpInsn(final AbstractInsnNode node)
	{
		JumpInsnNode n = (JumpInsnNode)node;
		return opcodeToString(n.getOpcode())+" "+n.label.hashCode();
		
	}

	protected String getInvokeDynamicInsn(final AbstractInsnNode node)
	{
		InvokeDynamicInsnNode n = (InvokeDynamicInsnNode)node;
		StringBuilder sb = new StringBuilder(); 
		sb.append(opcodeToString(n.getOpcode()));
		sb.append(" ");
		sb.append(n.name);
		sb.append(" ");
		sb.append(n.desc);
		sb.append(" ");
		sb.append(n.bsm == null?"null":n.bsm.getOwner() + " " + n.bsm.getName() + " " + n.bsm.getDesc());
		sb.append(" - - - ");
		if(n.bsmArgs == null)
		{
			sb.append("null");
		}
		else
		{
			for(Object o : n.bsmArgs)
			{
				sb.append(o == null ? "null" : o.toString());
				sb.append(" ");
			}
		}
		return sb.toString();
	}

	protected String getMethodInsn(final AbstractInsnNode node)
	{
		MethodInsnNode n = (MethodInsnNode)node;
		StringBuilder sb = new StringBuilder();
		sb.append(opcodeToString(n.getOpcode()));
		sb.append(" ");
		sb.append(n.owner);
		sb.append(" ");
		sb.append(n.name);
		sb.append(" ");
		sb.append(n.desc);
		sb.append(" ");
		sb.append(n.itf);
		return sb.toString();
	}

	protected String getFieldInsn(final AbstractInsnNode node)
	{
		FieldInsnNode n = (FieldInsnNode)node;
		StringBuilder sb = new StringBuilder();
		sb.append(opcodeToString(n.getOpcode()));
		sb.append(" ");
		sb.append(n.owner);
		sb.append(" ");
		sb.append(n.name);
		sb.append(" ");
		sb.append(n.desc);
		sb.append(" ");
		return sb.toString();
	}

	protected String getTypeInsn(final AbstractInsnNode node)
	{
		TypeInsnNode n = (TypeInsnNode)node;
		return opcodeToString(n.getOpcode())+" "+n.desc;
	}

	protected String getVarInsn(final AbstractInsnNode node)
	{
		VarInsnNode n = (VarInsnNode)node;
		return opcodeToString(n.getOpcode())+" "+n.var;
	}

	protected String getIntInsn(final AbstractInsnNode node)
	{
		IntInsnNode n = (IntInsnNode)node;
		return opcodeToString(n.getOpcode())+" "+n.operand;
	}
	
	protected String opcodeToString(int i)
	{
		String result = opcodeToStringCache.get(i);
		return result==null?"INVALID "+i:result;
	}
	
	protected interface OpcodesHidden
	{
		//ALL Insn Opcodes including ones normally hidden by the Opcodes.class since ASM handles them automatically
		//This list was taken from Objectweb ASM
		
		int NOP = 0;
	    int ACONST_NULL = 1; 
	    int ICONST_M1 = 2; 
	    int ICONST_0 = 3; 
	    int ICONST_1 = 4; 
	    int ICONST_2 = 5; 
	    int ICONST_3 = 6; 
	    int ICONST_4 = 7; 
	    int ICONST_5 = 8; 
	    int LCONST_0 = 9; 
	    int LCONST_1 = 10; 
	    int FCONST_0 = 11; 
	    int FCONST_1 = 12; 
	    int FCONST_2 = 13; 
	    int DCONST_0 = 14; 
	    int DCONST_1 = 15; 
	    int BIPUSH = 16; 
	    int SIPUSH = 17; 
	    int LDC = 18; 
	    int LDC_W = 19; 
	    int LDC2_W = 20; 
	    int ILOAD = 21;
	    int LLOAD = 22; 
	    int FLOAD = 23; 
	    int DLOAD = 24; 
	    int ALOAD = 25; 
	    int ILOAD_0 = 26; 
	    int ILOAD_1 = 27; 
	    int ILOAD_2 = 28; 
	    int ILOAD_3 = 29; 
	    int LLOAD_0 = 30; 
	    int LLOAD_1 = 31; 
	    int LLOAD_2 = 32; 
	    int LLOAD_3 = 33; 
	    int FLOAD_0 = 34; 
	    int FLOAD_1 = 35; 
	    int FLOAD_2 = 36; 
	    int FLOAD_3 = 37; 
	    int DLOAD_0 = 38; 
	    int DLOAD_1 = 39; 
	    int DLOAD_2 = 40; 
	    int DLOAD_3 = 41; 
	    int ALOAD_0 = 42; 
	    int ALOAD_1 = 43; 
	    int ALOAD_2 = 44; 
	    int ALOAD_3 = 45; 
	    int IALOAD = 46;
	    int LALOAD = 47; 
	    int FALOAD = 48; 
	    int DALOAD = 49; 
	    int AALOAD = 50; 
	    int BALOAD = 51; 
	    int CALOAD = 52; 
	    int SALOAD = 53; 
	    int ISTORE = 54;
	    int LSTORE = 55; 
	    int FSTORE = 56; 
	    int DSTORE = 57; 
	    int ASTORE = 58; 
	    int ISTORE_0 = 59; 
	    int ISTORE_1 = 60; 
	    int ISTORE_2 = 61; 
	    int ISTORE_3 = 62; 
	    int LSTORE_0 = 63; 
	    int LSTORE_1 = 64; 
	    int LSTORE_2 = 65; 
	    int LSTORE_3 = 66; 
	    int FSTORE_0 = 67; 
	    int FSTORE_1 = 68; 
	    int FSTORE_2 = 69; 
	    int FSTORE_3 = 70; 
	    int DSTORE_0 = 71; 
	    int DSTORE_1 = 72; 
	    int DSTORE_2 = 73; 
	    int DSTORE_3 = 74; 
	    int ASTORE_0 = 75; 
	    int ASTORE_1 = 76; 
	    int ASTORE_2 = 77; 
	    int ASTORE_3 = 78; 
	    int IASTORE = 79;
	    int LASTORE = 80; 
	    int FASTORE = 81; 
	    int DASTORE = 82; 
	    int AASTORE = 83; 
	    int BASTORE = 84; 
	    int CASTORE = 85; 
	    int SASTORE = 86; 
	    int POP = 87; 
	    int POP2 = 88; 
	    int DUP = 89; 
	    int DUP_X1 = 90; 
	    int DUP_X2 = 91; 
	    int DUP2 = 92; 
	    int DUP2_X1 = 93; 
	    int DUP2_X2 = 94; 
	    int SWAP = 95; 
	    int IADD = 96; 
	    int LADD = 97; 
	    int FADD = 98; 
	    int DADD = 99; 
	    int ISUB = 100; 
	    int LSUB = 101; 
	    int FSUB = 102; 
	    int DSUB = 103; 
	    int IMUL = 104; 
	    int LMUL = 105; 
	    int FMUL = 106; 
	    int DMUL = 107; 
	    int IDIV = 108; 
	    int LDIV = 109; 
	    int FDIV = 110; 
	    int DDIV = 111; 
	    int IREM = 112; 
	    int LREM = 113; 
	    int FREM = 114; 
	    int DREM = 115; 
	    int INEG = 116; 
	    int LNEG = 117; 
	    int FNEG = 118; 
	    int DNEG = 119; 
	    int ISHL = 120; 
	    int LSHL = 121; 
	    int ISHR = 122; 
	    int LSHR = 123; 
	    int IUSHR = 124; 
	    int LUSHR = 125; 
	    int IAND = 126; 
	    int LAND = 127; 
	    int IOR = 128; 
	    int LOR = 129; 
	    int IXOR = 130; 
	    int LXOR = 131; 
	    int IINC = 132; 
	    int I2L = 133;
	    int I2F = 134; 
	    int I2D = 135; 
	    int L2I = 136; 
	    int L2F = 137; 
	    int L2D = 138; 
	    int F2I = 139; 
	    int F2L = 140; 
	    int F2D = 141; 
	    int D2I = 142; 
	    int D2L = 143; 
	    int D2F = 144; 
	    int I2B = 145; 
	    int I2C = 146; 
	    int I2S = 147; 
	    int LCMP = 148; 
	    int FCMPL = 149; 
	    int FCMPG = 150; 
	    int DCMPL = 151; 
	    int DCMPG = 152; 
	    int IFEQ = 153; 
	    int IFNE = 154; 
	    int IFLT = 155; 
	    int IFGE = 156; 
	    int IFGT = 157; 
	    int IFLE = 158; 
	    int IF_ICMPEQ = 159; 
	    int IF_ICMPNE = 160; 
	    int IF_ICMPLT = 161; 
	    int IF_ICMPGE = 162; 
	    int IF_ICMPGT = 163; 
	    int IF_ICMPLE = 164; 
	    int IF_ACMPEQ = 165; 
	    int IF_ACMPNE = 166; 
	    int GOTO = 167; 
	    int JSR = 168; 
	    int RET = 169;
	    int TABLESWITCH = 170;
	    int LOOKUPSWITCH = 171; 
	    int IRETURN = 172;
	    int LRETURN = 173; 
	    int FRETURN = 174; 
	    int DRETURN = 175; 
	    int ARETURN = 176; 
	    int RETURN = 177; 
	    int GETSTATIC = 178;
	    int PUTSTATIC = 179; 
	    int GETFIELD = 180; 
	    int PUTFIELD = 181; 
	    int INVOKEVIRTUAL = 182;
	    int INVOKESPECIAL = 183; 
	    int INVOKESTATIC = 184; 
	    int INVOKEINTERFACE = 185; 
	    int INVOKEDYNAMIC = 186; 
	    int NEW = 187; 
	    int NEWARRAY = 188; 
	    int ANEWARRAY = 189; 
	    int ARRAYLENGTH = 190;
	    int ATHROW = 191; 
	    int CHECKCAST = 192; 
	    int INSTANCEOF = 193; 
	    int MONITORENTER = 194;
	    int MONITOREXIT = 195; 
	    int WIDE = 196; 
	    int MULTIANEWARRAY = 197; 
	    int IFNULL = 198; 
	    int IFNONNULL = 199; 
	    int GOTO_W = 200; 
	    int JSR_W = 201; 
	}

}
