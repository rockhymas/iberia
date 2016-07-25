package com.gibraltar.iberia.challenge;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Challenge {
	public boolean enabled;
    public String name;

    public Challenge() {
        name = getClass().getSimpleName().replaceAll("Challenge", "").toLowerCase();
    }

	public void preInit(FMLPreInitializationEvent event) {
	}

	public void init(FMLInitializationEvent event) {
	}

	public void postInit(FMLPostInitializationEvent event) {
	}

	public void loadConfig(Configuration config) {
        Property prop = config.get("challenges", name, true);
        enabled = prop.getBoolean(true);
	}
}