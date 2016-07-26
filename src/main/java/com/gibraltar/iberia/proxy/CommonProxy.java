package com.gibraltar.iberia.proxy;

import java.io.File;
import java.util.ArrayList;
import java.util.function.Consumer;

import com.gibraltar.iberia.challenge.Challenge;
import com.gibraltar.iberia.challenge.HardStoneChallenge;
import com.gibraltar.iberia.challenge.SleepToHealChallenge;
import com.gibraltar.iberia.feature.ReducedDebugInfoFeature;
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
    private ArrayList challenges;

    public void preInit(FMLPreInitializationEvent event)
    {
        configFile = event.getSuggestedConfigurationFile();
        System.out.println(configFile);
		config = new Configuration(configFile);
		config.load();

        challenges = new ArrayList();
        challenges.add(new HardStoneChallenge());
        challenges.add(new SleepToHealChallenge());
        // challenges.add(new ArmorSlowsCraftingChallenge());
        // challenges.add(new ReducedDebugInfoChallenge());

        forEachChallenge(challenge -> challenge.loadConfig(config));

        if (config.hasChanged()) {
            config.save();
        }

        forEachChallenge(challenge -> {
            if (challenge.enabled) {
                challenge.preInit(event);
            }
        });


        Property prop = config.get("features", "Reduced Debug Info", true);
        boolean enabled = prop.getBoolean(true);
        if (enabled) {
            ReducedDebugInfoFeature.init();
        }

        // prop = config.get("features", "Sleep to Heal", true);
        // enabled = prop.getBoolean(true);
        // if (enabled) {
        //     SleepToHealFeature.init();
        // }

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
        forEachChallenge(challenge -> {
            if (challenge.enabled) {
                challenge.init(event);
            }
        });
    }

    private void forEachChallenge(Consumer<Challenge> action) {
	    challenges.forEach(action);
    }
}
