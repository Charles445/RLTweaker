package com.charles445.rltweaker.asm;

import org.objectweb.asm.ClassWriter;

public class PatchResult {
	
	public static PatchResult NO_MUTATION = new PatchResult(-1);
	public static PatchResult NO_FLAGS = new PatchResult(0);
	public static PatchResult MAXS = new PatchResult(ClassWriter.COMPUTE_MAXS);
	public static PatchResult MAXS_FRAMES = new PatchResult(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
	public static PatchResult FRAMES = new PatchResult(ClassWriter.COMPUTE_FRAMES);
	
	/**
	 * {@link org.objectweb.asm.ClassWriter} flags
	 * no-mutation if negative
	 */
	private final int flags;
	
	public PatchResult(int flags) {
		this.flags = flags;
	}
	
	/**
	 * @return the {@link org.objectweb.asm.ClassWriter} flags that should be used
	 */
	public int getFlags() {
		return Math.max(this.flags, 0);
	}
	
	/**
	 * @return True if the {@link org.objectweb.asm.tree.ClassNode} has been mutated
	 */
	public boolean isMutated() {
		return this.flags >= 0;
	}
	
	public PatchResult add(PatchResult other) {
		// if there is no mutation use -1, else bitwise-or the flags
		return new PatchResult(this.isMutated() || other.isMutated() ?
				this.getFlags() | other.getFlags() : -1);
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof PatchResult &&
				Math.max(-1, ((PatchResult) other).flags) == Math.max(-1, this.flags);
	}
	
	@Override
	public int hashCode() {
		return Integer.hashCode(Math.max(-1, this.flags));
	}
	
	@Override
	public String toString() {
		switch (this.getFlags()) {
			case 0:
				return "None";
			case 1:
				return "MAXS";
			case 2:
				return "FRAMES";
			case 3:
				return "MAXS | FRAMES";
			default:
				return "(unknown " + this.getFlags() + ")";
		}
	}
}
