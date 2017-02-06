/**
 * This class was created by Rock Hymas. It's distributed as
 * part of the Iberia Mod. Get the Source Code in github:
 * https://github.com/rockhymas/iberia
 *
 * Iberia is Open Source and distributed under the
 * CC-BY-NC-SA 4.0 License: https://creativecommons.org/licenses/by-nc-sa/4.0/
 */
package com.gibraltar.iberia.proxy;

import java.io.File;
import java.util.ArrayList;
import java.util.function.Consumer;

import com.gibraltar.iberia.challenge.Challenge;
import com.gibraltar.iberia.challenge.ArmorChallenge;
import com.gibraltar.iberia.challenge.HealingChallenge;
import com.gibraltar.iberia.challenge.NavigationChallenge;
import com.gibraltar.iberia.challenge.SleepChallenge;
import com.gibraltar.iberia.challenge.SpawnChallenge;
import com.gibraltar.iberia.challenge.StoneChallenge;
import com.gibraltar.iberia.network.MessageRegistry;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class CommonProxy {
	public static Configuration config;
	public static File configFile;
    protected ArrayList challenges;

    public void preInit(FMLPreInitializationEvent event)
    {
        configFile = event.getSuggestedConfigurationFile();
        System.out.println(configFile);
		config = new Configuration(configFile);
		config.load();

        challenges = new ArrayList();
        challenges.add(new StoneChallenge());
        challenges.add(new SleepChallenge());
        challenges.add(new ArmorChallenge());
        challenges.add(new NavigationChallenge());
        challenges.add(new SpawnChallenge());
        challenges.add(new HealingChallenge());

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

        MessageRegistry.init();
    }

	public void init(FMLInitializationEvent event)
    {
        forEachChallenge(challenge -> {
            if (challenge.enabled) {
                challenge.init(event);
            }
        });
    }

    protected void forEachChallenge(Consumer<Challenge> action) {
	    challenges.forEach(action);
    }

    public boolean isChallengeEnabled(Class clazz) {
        boolean enabled = false;
        for (int i = 0; i < challenges.size(); i++) {
            Challenge challenge = (Challenge)challenges.get(i);
            if (clazz.isInstance(challenge)) {
                enabled = challenge.enabled;
            }
        }

        return enabled;
    }
}
