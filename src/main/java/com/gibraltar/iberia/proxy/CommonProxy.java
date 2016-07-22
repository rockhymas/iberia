package com.gibraltar.iberia.proxy;

import java.io.File;

import com.gibraltar.iberia.feature.HardStoneFeature;
import com.gibraltar.iberia.feature.ReducedDebugInfoFeature;
import com.gibraltar.iberia.feature.SleepToHealFeature;
import com.gibraltar.iberia.feature.SlowGuiAccessFeature;
import com.gibraltar.iberia.feature.QuickArmorSwapFeature;
import com.gibraltar.iberia.feature.ArmorStandElytraRenderFeature;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class CommonProxy {
	public static Configuration config;
	public static File configFile;

    public void preInit(FMLPreInitializationEvent event)
    {
        configFile = event.getSuggestedConfigurationFile();
        System.out.println(configFile);
		config = new Configuration(configFile);
		config.load();

        Property prop = config.get("features", "Hard Stone", true);
        boolean enabled = prop.getBoolean(true);
        if (enabled) {
    	    HardStoneFeature.init();
        }

        prop = config.get("features", "Reduced Debug Info", true);
        enabled = prop.getBoolean(true);
        if (enabled) {
            ReducedDebugInfoFeature.init();
        }

        prop = config.get("features", "Sleep to Heal", true);
        enabled = prop.getBoolean(true);
        if (enabled) {
            SleepToHealFeature.init();
        }

        prop = config.get("features", "Slow Gui Access", true);
        enabled = prop.getBoolean(true);
        if (enabled) {
            SlowGuiAccessFeature.init();
        }

        prop = config.get("features", "Quick Armor Swap", true);
        enabled = prop.getBoolean(true);
        if (enabled) {
            QuickArmorSwapFeature.init();
        }

        prop = config.get("features", "Elytra On Armor Stands", true);
        enabled = prop.getBoolean(true);
        if (enabled) {
            ArmorStandElytraRenderFeature.init();
        }

        config.save();
    }

	public void init(FMLInitializationEvent event)
    {
    }
}
