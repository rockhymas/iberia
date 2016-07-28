package com.gibraltar.iberia.proxy;

import java.io.File;
import java.util.ArrayList;
import java.util.function.Consumer;

import com.gibraltar.iberia.challenge.Challenge;
import com.gibraltar.iberia.challenge.HardStoneChallenge;
import com.gibraltar.iberia.challenge.ReducedDebugInfoChallenge;
import com.gibraltar.iberia.challenge.SleepToHealChallenge;
import com.gibraltar.iberia.challenge.ArmorSlowsCraftingChallenge;

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
        challenges.add(new ArmorSlowsCraftingChallenge());
        challenges.add(new ReducedDebugInfoChallenge());

        forEachChallenge(challenge -> challenge.loadConfig(config));

        if (config.hasChanged()) {
            config.save();
        }

        forEachChallenge(challenge -> {
            if (challenge.enabled) {
                challenge.preInit(event);
            }
        });

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
