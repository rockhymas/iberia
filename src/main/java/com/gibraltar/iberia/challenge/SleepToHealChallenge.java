/**
 * This class was created by Rock Hymas. It's distributed as
 * part of the Iberia Mod. Get the Source Code in github:
 * https://github.com/rockhymas/iberia
 *
 * Iberia is Open Source and distributed under the
 * CC-BY-NC-SA 4.0 License: https://creativecommons.org/licenses/by-nc-sa/4.0/
 */
package com.gibraltar.iberia.challenge;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.GameRules;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SleepToHealChallenge extends Challenge {
    private float healAmountHard;
    private float healAmountNormal;
    private float healAmountEasy;

    @Override
    public boolean hasSubscriptions() {
        return true;
    }

    @Override
	public void loadConfig(Configuration config) {
		super.loadConfig(config);

		Property prop = config.get(name, "HealAmountHard", 2.0D);
        healAmountHard = (float)prop.getDouble(2.0D);
		prop = config.get(name, "HealAmountNormal", 4.0D);
        healAmountNormal = (float)prop.getDouble(4.0D);
		prop = config.get(name, "HealAmountEasy", 6.0D);
        healAmountEasy = (float)prop.getDouble(6.0D);
    }

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
        GameRules rules = event.getWorld().getGameRules();
        rules.setOrCreateGameRule("naturalRegeneration", "false");
    }

    @SubscribeEvent
    public void onPlayerWakeUp(PlayerWakeUpEvent event) {
        // For some reason, should set spawn is true and update world is false when you get a full night's sleep.
        // No other combination of event parameters can indicate that.
        if (event.shouldSetSpawn() && !event.updateWorld()) {
            EntityPlayer player = event.getEntityPlayer();
            if (player.getHealth() < player.getMaxHealth() && !player.getFoodStats().needFood()) {
                switch (player.worldObj.getDifficulty()) {
                    case HARD:
                        player.heal(healAmountHard);
                        break;
                    case NORMAL:
                        player.heal(healAmountNormal);
                        break;
                    case EASY:
                        player.heal(healAmountEasy);
                        break;
                }
            }
        }
    }
}