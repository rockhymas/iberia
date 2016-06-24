package com.gibraltar.iberia.feature;

import net.minecraftforge.common.MinecraftForge;

public class SleepToHealFeature {	
	public static void init() {
		MinecraftForge.EVENT_BUS.register(new SleepToHealWatcher());
	}
}