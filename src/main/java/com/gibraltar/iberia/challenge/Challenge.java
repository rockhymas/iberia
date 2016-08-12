/**
 * This class was created by Rock Hymas. It's distributed as
 * part of the Iberia Mod. Get the Source Code in github:
 * https://github.com/rockhymas/iberia
 *
 * Iberia is Open Source and distributed under the
 * CC-BY-NC-SA 4.0 License: https://creativecommons.org/licenses/by-nc-sa/4.0/
 */
package com.gibraltar.iberia.challenge;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.MinecraftForge;
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
		if (hasSubscriptions()) {
			MinecraftForge.EVENT_BUS.register(this);
		}
	}

	public void init(FMLInitializationEvent event) {
	}

	public void postInit(FMLPostInitializationEvent event) {
	}

	protected boolean hasSubscriptions() {
		return false;
	}

	public void loadConfig(Configuration config) {
        Property prop = config.get("_challenges", name, true);
        enabled = prop.getBoolean(true);
	}
}