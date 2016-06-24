package com.gibraltar.iberia.feature;

import net.minecraftforge.common.MinecraftForge;

public class ReducedDebugInfoFeature {	
	public static void init() {
		MinecraftForge.EVENT_BUS.register(new ReducedDebugInfoWatcher());
	}
}
