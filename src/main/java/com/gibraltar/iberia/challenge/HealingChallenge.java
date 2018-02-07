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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.gibraltar.iberia.challenge.SleepChallenge;
import com.gibraltar.iberia.Iberia;

public class HealingChallenge extends Challenge {
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

		Property prop = config.get(name, "HealAmountHard", 3.0D);
		healAmountHard = (float)prop.getDouble(3.0D);
		prop = config.get(name, "HealAmountNormal", 6.0D);
		healAmountNormal = (float)prop.getDouble(6.0D);
		prop = config.get(name, "HealAmountEasy", 9.0D);
		healAmountEasy = (float)prop.getDouble(9.0D);
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		GameRules rules = event.getWorld().getGameRules();
		rules.setOrCreateGameRule("naturalRegeneration", "false");
	}

	@SubscribeEvent
	public void onPlayerWakeUp(PlayerWakeUpEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		if (player.world.isRemote) {
			return;
		}

		if (player.isPlayerFullyAsleep() && (Iberia.proxy.isChallengeEnabled(SleepChallenge.class) || player.world.isDaytime()) 
				&& (player.getHealth() < player.getMaxHealth() && !player.getFoodStats().needFood())) {

			switch (player.world.getDifficulty()) {
			case HARD:
				player.heal(healAmountHard);
				break;
			case NORMAL:
				player.heal(healAmountNormal);
				break;
			case EASY:
				player.heal(healAmountEasy);
				break;
			case PEACEFUL:
				break;
			default:
				break;
			}
		}
	}
}