package com.charles445.rltweaker.config.json;

public class JsonDoubleBlockState
{
	public static final JsonDoubleBlockState AIR = new JsonDoubleBlockState(JsonBlockState.AIR, JsonBlockState.AIR);
	
	public JsonBlockState input;
	
	public JsonBlockState output;
	
	public JsonDoubleBlockState(JsonBlockState input, JsonBlockState output)
	{
		this.input = input;
		this.output = output;
	}
}
